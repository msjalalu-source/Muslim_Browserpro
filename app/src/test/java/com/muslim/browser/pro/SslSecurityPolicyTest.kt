package com.muslim.browser.pro

import android.net.http.SslCertificate
import android.net.http.SslError
import com.muslim.browser.pro.browser.SslSecurityPolicy
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.io.InputStream
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import java.util.Date
import javax.net.ssl.X509TrustManager

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class SslSecurityPolicyTest {

    private val certFactory = CertificateFactory.getInstance("X.509")

    private fun loadCert(resourceName: String): X509Certificate {
        val stream: InputStream = javaClass.classLoader?.getResourceAsStream(resourceName)
            ?: throw IllegalStateException("Resource not found: $resourceName")
        return stream.use { certFactory.generateCertificate(it) as X509Certificate }
    }

    @Before
    fun setUp() {
        SslSecurityPolicy.clearSessionApprovals()
    }

    @Test
    fun `valid normal HTTPS certificate does not trigger incomplete chain flow`() {
        val cert = SslCertificate("google.com", "Google Trust Services", Date(System.currentTimeMillis() - 100000), Date(System.currentTimeMillis() + 100000))
        // When there is NO SSL_UNTRUSTED error, preliminaryCheck rejects it as not an untrusted error
        val error = SslError(SslError.SSL_INVALID, cert, "https://google.com")

        val prelim = SslSecurityPolicy.preliminaryCheck(error)
        assertTrue(prelim is SslSecurityPolicy.Decision.Reject)
        val reject = prelim as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("does not contain SSL_UNTRUSTED"))
    }

    @Test
    fun `hostname mismatch is immediately rejected`() {
        val cert = SslCertificate("wrong.com", "Sectigo", Date(System.currentTimeMillis() - 100000), Date(System.currentTimeMillis() + 100000))
        val error = SslError(SslError.SSL_IDMISMATCH, cert, "https://example.com")

        val prelim = SslSecurityPolicy.preliminaryCheck(error)
        assertTrue(prelim is SslSecurityPolicy.Decision.Reject)
        val reject = prelim as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("Hostname mismatch"))
    }

    @Test
    fun `expired certificate is immediately rejected`() {
        val cert = SslCertificate("example.com", "Sectigo", Date(System.currentTimeMillis() - 100000), Date(System.currentTimeMillis() - 1000))
        val error = SslError(SslError.SSL_EXPIRED, cert, "https://example.com")

        val prelim = SslSecurityPolicy.preliminaryCheck(error)
        assertTrue(prelim is SslSecurityPolicy.Decision.Reject)
        val reject = prelim as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("expired"))
    }

    @Test
    fun `not yet valid certificate is immediately rejected`() {
        val cert = SslCertificate("example.com", "Sectigo", Date(System.currentTimeMillis() + 100000), Date(System.currentTimeMillis() + 200000))
        val error = SslError(SslError.SSL_NOTYETVALID, cert, "https://example.com")

        val prelim = SslSecurityPolicy.preliminaryCheck(error)
        assertTrue(prelim is SslSecurityPolicy.Decision.Reject)
        val reject = prelim as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("not yet valid"))
    }

    @Test
    fun `self signed certificate is strictly rejected`() {
        val selfSigned = loadCert("self_signed.der")
        assertTrue("Expected self-signed detection", SslSecurityPolicy.isSelfSigned(selfSigned))

        val cert = SslCertificate(selfSigned)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://selfsigned.com")

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "selfsigned.com",
            overrideLeafCert = selfSigned
        )
        assertTrue(decision is SslSecurityPolicy.Decision.Reject)
        val reject = decision as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("Self-signed certificate is strictly rejected"))
    }

    @Test
    fun `AIA unavailable is rejected`() {
        val noAiaCert = loadCert("no_aia.der")
        val cert = SslCertificate(noAiaCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://noaia.com")

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "noaia.com",
            overrideLeafCert = noAiaCert
        )
        assertTrue(decision is SslSecurityPolicy.Decision.Reject)
        val reject = decision as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("No Authority Information Access"))
    }

    @Test
    fun `invalid or malicious intermediate signature is rejected`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val maliciousIntermediate = loadCert("self_signed.der") // Completely wrong key

        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://ictbdinvestigation.gov.bd")

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "ictbdinvestigation.gov.bd",
            overrideLeafCert = leafCert,
            intermediateFetcher = { maliciousIntermediate }
        )
        assertTrue(decision is SslSecurityPolicy.Decision.Reject)
        val reject = decision as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("signature verification failed"))
    }

    @Test
    fun `untrusted root CA is rejected`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")

        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://ictbdinvestigation.gov.bd")

        // Custom TrustManager that refuses this root
        val rejectingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {
                throw java.security.cert.CertificateException("Untrusted root anchor")
            }
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "ictbdinvestigation.gov.bd",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = rejectingTrustManager
        )
        assertTrue(decision is SslSecurityPolicy.Decision.Reject)
        val reject = decision as SslSecurityPolicy.Decision.Reject
        assertTrue(reject.reason.contains("could not be validated against system root CAs"))
    }

    @Test
    fun `incomplete chain successfully reconstructed through AIA for ictbdinvestigation_gov_bd`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")

        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://ictbdinvestigation.gov.bd")

        // Successful mock trust manager simulating Android system root anchor match
        val acceptingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "ictbdinvestigation.gov.bd",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = acceptingTrustManager
        )
        assertTrue("Expected PromptUser", decision is SslSecurityPolicy.Decision.PromptUser)
        val prompt = decision as SslSecurityPolicy.Decision.PromptUser
        assertEquals("ictbdinvestigation.gov.bd", prompt.host)
        assertTrue(prompt.certDetails.contains("Sectigo"))
    }

    @Test
    fun `incomplete chain works generically for acc_org_bd`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")

        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://acc.org.bd")

        val acceptingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "acc.org.bd",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = acceptingTrustManager
        )
        assertTrue("Expected PromptUser for acc.org.bd", decision is SslSecurityPolicy.Decision.PromptUser)
        val prompt = decision as SslSecurityPolicy.Decision.PromptUser
        assertEquals("acc.org.bd", prompt.host)
    }

    @Test
    fun `incomplete chain works for third unrelated domain on same cert SAN without whitelist`() {
        // dnc.gov.bd is in the SAN list of the certificate
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")

        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://dnc.gov.bd")

        val acceptingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "dnc.gov.bd",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = acceptingTrustManager
        )
        assertTrue("Expected PromptUser for third domain", decision is SslSecurityPolicy.Decision.PromptUser)
        val prompt = decision as SslSecurityPolicy.Decision.PromptUser
        assertEquals("dnc.gov.bd", prompt.host)
    }

    @Test
    fun `hostname verification rejects domain not present in SANs`() {
        val leafCert = loadCert("ictbd_leaf.der")
        // google.com is NOT in the SAN list of chtdb.gov.bd cert
        assertFalse(SslSecurityPolicy.verifyHostname("google.com", leafCert))
        assertFalse(SslSecurityPolicy.verifyHostname("facebook.com", leafCert))
        assertTrue(SslSecurityPolicy.verifyHostname("ictbdinvestigation.gov.bd", leafCert))
        assertTrue(SslSecurityPolicy.verifyHostname("acc.org.bd", leafCert))
    }

    @Test
    fun `session approval is host specific and never leaks to other hosts`() {
        val hostA = "ictbdinvestigation.gov.bd"
        val hostB = "acc.org.bd"

        assertFalse(SslSecurityPolicy.isSessionApproved(hostA))
        assertFalse(SslSecurityPolicy.isSessionApproved(hostB))

        // User chooses Proceed for hostA
        SslSecurityPolicy.approveHostForSession(hostA)
        assertTrue(SslSecurityPolicy.isSessionApproved(hostA))
        assertFalse("Approval must NOT leak to hostB", SslSecurityPolicy.isSessionApproved(hostB))
        assertFalse("Approval must NOT leak to google.com", SslSecurityPolicy.isSessionApproved("google.com"))

        // HostA subsequent check immediately returns ProceedSessionApproved
        val cert = SslCertificate("chtdb.gov.bd", "Sectigo", Date(System.currentTimeMillis() - 100000), Date(System.currentTimeMillis() + 100000))
        val errorA = SslError(SslError.SSL_UNTRUSTED, cert, "https://$hostA")
        val prelimA = SslSecurityPolicy.preliminaryCheck(errorA)
        assertEquals(SslSecurityPolicy.Decision.ProceedSessionApproved, prelimA)

        // HostB still requires verification
        val errorB = SslError(SslError.SSL_UNTRUSTED, cert, "https://$hostB")
        val prelimB = SslSecurityPolicy.preliminaryCheck(errorB)
        assertEquals(null, prelimB) // Null means deep AIA check required

        // Clearing session revokes HostA
        SslSecurityPolicy.clearSessionApprovals()
        assertFalse(SslSecurityPolicy.isSessionApproved(hostA))
    }

    @Test
    fun `user choosing Cancel leaves host unapproved`() {
        val host = "ictbdinvestigation.gov.bd"
        // When user cancels, approveHostForSession is NOT called
        assertFalse(SslSecurityPolicy.isSessionApproved(host))

        val cert = SslCertificate("chtdb.gov.bd", "Sectigo", Date(System.currentTimeMillis() - 100000), Date(System.currentTimeMillis() + 100000))
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://$host")
        // Preliminary check returns null (not approved)
        assertEquals(null, SslSecurityPolicy.preliminaryCheck(error))
    }

    @Test
    fun `navigationFromDuckDuckGoToIncompleteChainHostValidatesTargetHostNotDuckDuckGo`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")
        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://ictbdinvestigation.gov.bd")

        val acceptingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        // Simulating the user clicking from safe.duckduckgo.com search result
        // currentHost in WebView before navigation commit is "safe.duckduckgo.com"
        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "safe.duckduckgo.com",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = acceptingTrustManager
        )

        // Must NOT reject due to "Hostname 'safe.duckduckgo.com' does not match..."
        // Must validate the target host (ictbdinvestigation.gov.bd) and prompt user
        assertTrue("Must succeed with PromptUser for target host", decision is SslSecurityPolicy.Decision.PromptUser)
        val prompt = decision as SslSecurityPolicy.Decision.PromptUser
        assertEquals("Target host must be ictbdinvestigation.gov.bd, not safe.duckduckgo.com", "ictbdinvestigation.gov.bd", prompt.host)
    }

    @Test
    fun `navigationFromGoogleSearchToIncompleteChainHostValidatesTargetHostNotGoogle`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val intermediateCert = loadCert("sectigo_intermediate.der")
        val cert = SslCertificate(leafCert)
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://ictbdinvestigation.gov.bd")

        val acceptingTrustManager = object : X509TrustManager {
            override fun checkClientTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun checkServerTrusted(chain: Array<out X509Certificate>?, authType: String?) {}
            override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
        }

        // Simulating navigation from Google search results page (currentHost = "www.google.com")
        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "www.google.com",
            overrideLeafCert = leafCert,
            intermediateFetcher = { intermediateCert },
            trustManagerOverride = acceptingTrustManager
        )

        assertTrue("Must succeed with PromptUser for target host", decision is SslSecurityPolicy.Decision.PromptUser)
        val prompt = decision as SslSecurityPolicy.Decision.PromptUser
        assertEquals("Target host must be ictbdinvestigation.gov.bd, not www.google.com", "ictbdinvestigation.gov.bd", prompt.host)
    }

    @Test
    fun `genuineHostnameMismatchForTargetHostIsStrictlyRejectedRegardlessOfPreviousPage`() {
        val leafCert = loadCert("ictbd_leaf.der")
        val cert = SslCertificate(leafCert)
        // Request URL points to a domain not present in the certificate SANs
        val error = SslError(SslError.SSL_UNTRUSTED, cert, "https://spoof-site.com")

        val decision = SslSecurityPolicy.validateIncompleteChain(
            error = error,
            currentHost = "safe.duckduckgo.com",
            overrideLeafCert = leafCert
        )

        assertTrue("Target host mismatch must be strictly rejected", decision is SslSecurityPolicy.Decision.Reject)
        val reject = decision as SslSecurityPolicy.Decision.Reject
        assertTrue("Reject reason must indicate target host mismatch: ${reject.reason}", reject.reason.contains("spoof-site.com"))
    }
}
