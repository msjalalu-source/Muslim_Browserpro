package com.muslim.browser.pro.browser

import android.net.http.SslCertificate
import android.net.http.SslError
import android.os.Build
import android.util.Log
import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Generic, conservative SSL policy engine for incomplete certificate chains.
 *
 * Security Principles:
 * 1. ZERO domain whitelisting: operates generically for any legitimate HTTPS host.
 * 2. Hard rejects:
 *    - Hostname / SAN mismatch (SSL_IDMISMATCH)
 *    - Expired certificate (SSL_EXPIRED)
 *    - Not-yet-valid certificate (SSL_NOTYETVALID)
 *    - Self-signed certificate (subject == issuer)
 *    - Any certificate that cannot be cryptographically proven to connect to an Android system-trusted root.
 * 3. Proves missing intermediate CA via Authority Information Access (AIA) caIssuers URI.
 * 4. Downloads candidate intermediate, cryptographically verifies leaf was signed by candidate,
 *    and validates the full chain against Android's default system X509TrustManager.
 * 5. Requires explicit user confirmation via Material 3 dialog even after cryptographic proof.
 * 6. Stores approvals in volatile memory for the active session only (never persisted to disk).
 */
object SslSecurityPolicy {

    private const val TAG = "SslSecurityPolicy"

    /**
     * In-memory cache of validated intermediate certificates keyed by AIA URI.
     */
    private val intermediateCache = ConcurrentHashMap<String, X509Certificate>()

    /**
     * Session-only in-memory storage of user-approved hosts.
     * Cleared when the app process terminates (never persisted to storage).
     */
    private val sessionApprovedHosts = Collections.synchronizedSet(mutableSetOf<String>())

    fun isSessionApproved(host: String): Boolean {
        val cleanHost = normalizeHost(host)
        return sessionApprovedHosts.contains(cleanHost)
    }

    fun approveHostForSession(host: String) {
        val cleanHost = normalizeHost(host)
        sessionApprovedHosts.add(cleanHost)
    }

    fun revokeHostApproval(host: String) {
        val cleanHost = normalizeHost(host)
        sessionApprovedHosts.remove(cleanHost)
    }

    fun clearSessionApprovals() {
        sessionApprovedHosts.clear()
    }

    fun normalizeHost(host: String): String {
        return host.trim().lowercase(Locale.ROOT).removePrefix("www.")
    }

    sealed class Decision {
        /**
         * Reject the SSL connection immediately with handler.cancel().
         */
        data class Reject(val reason: String) : Decision()

        /**
         * The host was already approved by the user in this session.
         */
        object ProceedSessionApproved : Decision()

        /**
         * Cryptographically verified incomplete-chain condition.
         * Requires explicit user confirmation via dialog before proceeding.
         */
        data class PromptUser(val host: String, val url: String, val certDetails: String) : Decision()
    }

    /**
     * Performs preliminary synchronous checks.
     * Returns Decision.Reject, Decision.ProceedSessionApproved, or null if deep AIA validation is needed.
     */
    fun preliminaryCheck(error: SslError?, currentHost: String? = null): Decision? {
        if (error == null) {
            return Decision.Reject("Null SSL error")
        }

        val errorUrl = error.url ?: ""
        val hostFromErrorUrl = extractHostFromUrl(errorUrl)
        val host = (if (!hostFromErrorUrl.isNullOrBlank()) hostFromErrorUrl else currentHost)?.let { normalizeHost(it) } ?: ""

        if (host.isBlank()) {
            return Decision.Reject("Missing host in SSL error")
        }

        // 1. FATAL DEFECT CHECKS - MUST NEVER PROCEED
        if (error.hasError(SslError.SSL_IDMISMATCH)) {
            return Decision.Reject("Hostname mismatch (SSL_IDMISMATCH)")
        }
        if (error.hasError(SslError.SSL_DATE_INVALID)) {
            return Decision.Reject("Certificate date invalid (SSL_DATE_INVALID)")
        }
        if (error.hasError(SslError.SSL_EXPIRED)) {
            return Decision.Reject("Certificate expired (SSL_EXPIRED)")
        }
        if (error.hasError(SslError.SSL_NOTYETVALID)) {
            return Decision.Reject("Certificate not yet valid (SSL_NOTYETVALID)")
        }

        // 2. Session approval check
        if (isSessionApproved(host)) {
            return Decision.ProceedSessionApproved
        }

        // 3. Must contain SSL_UNTRUSTED to be considered for incomplete chain
        if (!error.hasError(SslError.SSL_UNTRUSTED)) {
            return Decision.Reject("SSL error does not contain SSL_UNTRUSTED (primaryError: ${error.primaryError})")
        }

        return null // Needs deep AIA verification
    }

    /**
     * Performs complete cryptographic evaluation of an unchained certificate.
     * Non-blocking and safe to call on Dispatchers.IO.
     */
    fun validateIncompleteChain(
        error: SslError?,
        currentHost: String? = null,
        overrideLeafCert: X509Certificate? = null,
        intermediateFetcher: ((String) -> X509Certificate?)? = null,
        trustManagerOverride: X509TrustManager? = null
    ): Decision {
        val errorUrl = error?.url ?: ""
        val hostFromErrorUrl = extractHostFromUrl(errorUrl)
        val host = (if (!hostFromErrorUrl.isNullOrBlank()) hostFromErrorUrl else currentHost)?.let { normalizeHost(it) } ?: ""

        val prelim = preliminaryCheck(error, host)
        if (prelim != null) return prelim

        // 1. Extract leaf X.509 certificate
        val leafCert = overrideLeafCert ?: getX509Certificate(error?.certificate)
        if (leafCert == null) {
            return Decision.Reject("Unable to extract X.509 leaf certificate")
        }

        // 2. Validate validity dates
        try {
            leafCert.checkValidity()
        } catch (e: Exception) {
            return Decision.Reject("Leaf certificate validity check failed: ${e.message}")
        }

        // 3. Reject self-signed certificates (subject == issuer or self-verified)
        if (isSelfSigned(leafCert)) {
            return Decision.Reject("Self-signed certificate is strictly rejected")
        }

        // 4. Validate hostname against leaf certificate SANs / CN
        if (!verifyHostname(host, leafCert)) {
            return Decision.Reject("Hostname '$host' does not match certificate subject alternative names")
        }

        // 5. Extract AIA caIssuers URL
        val aiaUrl = extractAiaCaIssuersUrl(leafCert)
        if (aiaUrl.isNullOrBlank()) {
            return Decision.Reject("No Authority Information Access (AIA) caIssuers URI found in certificate")
        }

        // 6. Fetch / retrieve candidate intermediate certificate
        val candidateIntermediate = try {
            if (intermediateFetcher != null) {
                intermediateFetcher(aiaUrl)
            } else {
                fetchIntermediateCertificate(aiaUrl)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to retrieve intermediate certificate from $aiaUrl", e)
            null
        }

        if (candidateIntermediate == null) {
            return Decision.Reject("Failed to obtain intermediate certificate from AIA URI ($aiaUrl)")
        }

        // 7. Verify candidate intermediate validity dates
        try {
            candidateIntermediate.checkValidity()
        } catch (e: Exception) {
            return Decision.Reject("Intermediate certificate validity check failed: ${e.message}")
        }

        // 8. Cryptographic verification: Leaf must be signed by candidate intermediate
        try {
            leafCert.verify(candidateIntermediate.publicKey)
        } catch (e: Exception) {
            return Decision.Reject("Leaf certificate signature verification failed against candidate intermediate: ${e.message}")
        }

        // 9. Path validation: Candidate intermediate must anchor to an Android system-trusted root CA
        val isChainTrusted = validateChainAgainstSystemTrust(
            leafCert = leafCert,
            intermediateCert = candidateIntermediate,
            trustManagerOverride = trustManagerOverride
        )

        if (!isChainTrusted) {
            return Decision.Reject("Reconstructed certificate chain could not be validated against system root CAs")
        }

        // 10. Cache verified intermediate certificate
        intermediateCache[aiaUrl] = candidateIntermediate

        // 11. Format certificate details for user dialog
        val certSummary = buildString {
            val issuedTo = leafCert.subjectX500Principal?.name ?: "Unknown"
            val issuedBy = candidateIntermediate.subjectX500Principal?.name ?: "Unknown"
            append("Issued To: ").append(extractCn(leafCert) ?: issuedTo).append("\n")
            append("Verified Intermediate: ").append(extractCn(candidateIntermediate) ?: issuedBy).append("\n")
            append("Expires: ").append(leafCert.notAfter)
        }

        return Decision.PromptUser(
            host = host,
            url = errorUrl,
            certDetails = certSummary
        )
    }

    /**
     * Validates that arrayOf(leafCert, intermediateCert) terminates in a trusted system root anchor.
     */
    fun validateChainAgainstSystemTrust(
        leafCert: X509Certificate,
        intermediateCert: X509Certificate,
        trustManagerOverride: X509TrustManager? = null
    ): Boolean {
        val tm = trustManagerOverride ?: getSystemTrustManager() ?: return false
        val chain = arrayOf(leafCert, intermediateCert)
        val authTypes = listOf("RSA", "ECDSA", "UNKNOWN", "GENERIC")

        for (authType in authTypes) {
            try {
                tm.checkServerTrusted(chain, authType)
                return true
            } catch (_: Exception) {
                // Try next authType
            }
        }
        return false
    }

    private fun getSystemTrustManager(): X509TrustManager? {
        return try {
            val tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
            tmf.init(null as KeyStore?)
            tmf.trustManagers.filterIsInstance<X509TrustManager>().firstOrNull()
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing system TrustManagerFactory", e)
            null
        }
    }

    /**
     * Returns true if the certificate is self-signed (subject == issuer or self-verifiable).
     */
    fun isSelfSigned(cert: X509Certificate): Boolean {
        val subject = cert.subjectX500Principal
        val issuer = cert.issuerX500Principal
        if (subject != null && subject == issuer) {
            return true
        }
        return try {
            cert.verify(cert.publicKey)
            true
        } catch (_: Exception) {
            false
        }
    }

    /**
     * Verifies that the requested hostname matches the certificate's SAN or CN as per RFC 6125.
     */
    fun verifyHostname(host: String, cert: X509Certificate): Boolean {
        val cleanHost = host.trim().lowercase(Locale.ROOT).removePrefix("www.")
        val fullHost = host.trim().lowercase(Locale.ROOT)

        // 1. Check Subject Alternative Names (SAN) (RFC 6125)
        try {
            val sans = cert.subjectAlternativeNames
            if (sans != null && sans.isNotEmpty()) {
                for (san in sans) {
                    if (san.size >= 2) {
                        val type = (san[0] as? Number)?.toInt()
                        val value = (san[1] as? String)?.lowercase(Locale.ROOT) ?: continue
                        if (type == 2) { // 2 = dNSName
                            if (matchDomain(fullHost, value) || matchDomain(cleanHost, value)) {
                                return true
                            }
                        }
                    }
                }
                return false // SANs present, so CN is ignored per RFC 6125
            }
        } catch (_: Exception) {}

        // 2. Fallback to Common Name (CN) only if no SANs are present
        val cn = extractCn(cert)?.lowercase(Locale.ROOT) ?: return false
        return matchDomain(fullHost, cn) || matchDomain(cleanHost, cn)
    }

    fun matchDomain(host: String, pattern: String): Boolean {
        val h = host.lowercase(Locale.ROOT)
        val p = pattern.lowercase(Locale.ROOT)
        if (h == p) return true
        if (p.startsWith("*.")) {
            val suffix = p.removePrefix("*.")
            if (h.endsWith(".$suffix") || h == suffix) {
                val prefix = h.removeSuffix(".$suffix")
                return !prefix.contains(".") // Single-level wildcard
            }
        }
        return false
    }

    fun extractCn(cert: X509Certificate): String? {
        val name = cert.subjectX500Principal?.name ?: return null
        val parts = name.split(",")
        for (part in parts) {
            val trimmed = part.trim()
            if (trimmed.startsWith("CN=", ignoreCase = true)) {
                return trimmed.substring(3).trim()
            }
        }
        return null
    }

    /**
     * Extracts the caIssuers URL from the Authority Information Access (AIA) extension (OID: 1.3.6.1.5.5.7.1.1).
     */
    fun extractAiaCaIssuersUrl(cert: X509Certificate): String? {
        val extVal = cert.getExtensionValue("1.3.6.1.5.5.7.1.1") ?: return null
        // OID 1.3.6.1.5.5.7.48.2 (id-ad-caIssuers) in DER format
        val caIssuersOid = byteArrayOf(0x06, 0x08, 0x2b, 0x06, 0x01, 0x05, 0x05, 0x07, 0x30, 0x02)
        val idx = indexOfSubarray(extVal, caIssuersOid)
        if (idx == -1) return null

        var pos = idx + caIssuersOid.size
        if (pos >= extVal.size) return null

        // Look for GeneralName URI tag (0x86)
        if (extVal[pos] != 0x86.toByte()) {
            var foundTag = false
            for (i in pos until minOf(pos + 4, extVal.size)) {
                if (extVal[i] == 0x86.toByte()) {
                    pos = i
                    foundTag = true
                    break
                }
            }
            if (!foundTag) return null
        }

        pos++ // Move past 0x86
        if (pos >= extVal.size) return null

        // Read ASN.1 length
        val firstLenByte = extVal[pos].toInt() and 0xFF
        val length: Int
        if ((firstLenByte and 0x80) == 0) {
            length = firstLenByte
            pos++
        } else {
            val numLenBytes = firstLenByte and 0x7F
            pos++
            if (numLenBytes == 1 && pos < extVal.size) {
                length = extVal[pos].toInt() and 0xFF
                pos++
            } else {
                return null
            }
        }

        if (pos + length > extVal.size || length <= 0) return null

        val url = String(extVal, pos, length, Charsets.US_ASCII)
        return if (url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true)) {
            url
        } else {
            null
        }
    }

    private fun indexOfSubarray(array: ByteArray, sub: ByteArray): Int {
        if (sub.isEmpty() || array.size < sub.size) return -1
        for (i in 0..array.size - sub.size) {
            var match = true
            for (j in sub.indices) {
                if (array[i + j] != sub[j]) {
                    match = false
                    break
                }
            }
            if (match) return i
        }
        return -1
    }

    /**
     * Fetches an intermediate certificate from an AIA URI over HTTP/HTTPS.
     */
    fun fetchIntermediateCertificate(urlStr: String): X509Certificate? {
        val cached = intermediateCache[urlStr]
        if (cached != null) return cached

        val url = URL(urlStr)
        val conn = url.openConnection() as HttpURLConnection
        conn.connectTimeout = 5000
        conn.readTimeout = 5000
        conn.instanceFollowRedirects = true
        conn.requestMethod = "GET"
        conn.setRequestProperty("User-Agent", "FocusShield/1.0 (Android)")

        return conn.inputStream.use { input ->
            val cf = CertificateFactory.getInstance("X.509")
            val cert = cf.generateCertificate(input) as? X509Certificate
            if (cert != null) {
                intermediateCache[urlStr] = cert
            }
            cert
        }
    }

    /**
     * Extracts X.509 certificate from Android SslCertificate.
     */
    fun getX509Certificate(sslCert: SslCertificate?): X509Certificate? {
        if (sslCert == null) return null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            try {
                val cert = sslCert.x509Certificate
                if (cert != null) return cert
            } catch (_: Exception) {}
        }
        return try {
            val bundle = SslCertificate.saveState(sslCert)
            val bytes = bundle?.getByteArray("x509-certificate")
            if (bytes != null && bytes.isNotEmpty()) {
                val cf = CertificateFactory.getInstance("X.509")
                cf.generateCertificate(ByteArrayInputStream(bytes)) as? X509Certificate
            } else {
                null
            }
        } catch (_: Exception) {
            null
        }
    }

    fun extractHostFromUrl(url: String): String? {
        if (url.isBlank()) return null
        return try {
            val uri = android.net.Uri.parse(url)
            uri.host
        } catch (_: Exception) {
            null
        }
    }
}
