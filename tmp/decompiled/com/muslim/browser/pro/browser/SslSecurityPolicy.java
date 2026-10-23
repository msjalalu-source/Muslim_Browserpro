/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  android.net.http.SslCertificate
 *  android.net.http.SslError
 *  android.os.Build$VERSION
 *  android.util.Log
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.io.CloseableKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Charsets
 *  kotlin.text.StringsKt
 */
package com.muslim.browser.pro.browser;

import android.net.Uri;
import android.net.http.SslCertificate;
import android.net.http.SslError;
import android.os.Build;
import android.util.Log;
import java.io.ByteArrayInputStream;
import java.io.Closeable;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import javax.security.auth.x500.X500Principal;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.io.CloseableKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Charsets;
import kotlin.text.StringsKt;

@Metadata(d1={"\u0000j\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010#\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0012\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0004\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u00017B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u0005J\u000e\u0010\u0010\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0005J\u000e\u0010\u0012\u001a\u00020\u00112\u0006\u0010\u000f\u001a\u00020\u0005J\u0006\u0010\u0013\u001a\u00020\u0011J\u000e\u0010\u0014\u001a\u00020\u00052\u0006\u0010\u000f\u001a\u00020\u0005J\u001e\u0010\u0015\u001a\u0004\u0018\u00010\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u0005JN\u0010\u001a\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\n\b\u0002\u0010\u0019\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u001b\u001a\u0004\u0018\u00010\b2\u0018\b\u0002\u0010\u001c\u001a\u0012\u0012\u0004\u0012\u00020\u0005\u0012\u0006\u0012\u0004\u0018\u00010\b\u0018\u00010\u001d2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\"\u0010 \u001a\u00020\u000e2\u0006\u0010!\u001a\u00020\b2\u0006\u0010\"\u001a\u00020\b2\n\b\u0002\u0010\u001e\u001a\u0004\u0018\u00010\u001fJ\n\u0010#\u001a\u0004\u0018\u00010\u001fH\u0002J\u000e\u0010$\u001a\u00020\u000e2\u0006\u0010%\u001a\u00020\bJ\u0016\u0010&\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010%\u001a\u00020\bJ\u0016\u0010'\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\u00052\u0006\u0010(\u001a\u00020\u0005J\u0010\u0010)\u001a\u0004\u0018\u00010\u00052\u0006\u0010%\u001a\u00020\bJ\u0010\u0010*\u001a\u0004\u0018\u00010\u00052\u0006\u0010%\u001a\u00020\bJ\u0018\u0010+\u001a\u00020,2\u0006\u0010-\u001a\u00020.2\u0006\u0010/\u001a\u00020.H\u0002J\u0010\u00100\u001a\u0004\u0018\u00010\b2\u0006\u00101\u001a\u00020\u0005J\u0012\u00102\u001a\u0004\u0018\u00010\b2\b\u00103\u001a\u0004\u0018\u000104J\u0010\u00105\u001a\u0004\u0018\u00010\u00052\u0006\u00106\u001a\u00020\u0005R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\b0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R2\u0010\t\u001a&\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00050\u0005 \u000b*\u0012\u0012\f\u0012\n \u000b*\u0004\u0018\u00010\u00050\u0005\u0018\u00010\f0\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u00068"}, d2={"Lcom/muslim/browser/pro/browser/SslSecurityPolicy;", "", "<init>", "()V", "TAG", "", "intermediateCache", "Ljava/util/concurrent/ConcurrentHashMap;", "Ljava/security/cert/X509Certificate;", "sessionApprovedHosts", "", "kotlin.jvm.PlatformType", "", "isSessionApproved", "", "host", "approveHostForSession", "", "revokeHostApproval", "clearSessionApprovals", "normalizeHost", "preliminaryCheck", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision;", "error", "Landroid/net/http/SslError;", "currentHost", "validateIncompleteChain", "overrideLeafCert", "intermediateFetcher", "Lkotlin/Function1;", "trustManagerOverride", "Ljavax/net/ssl/X509TrustManager;", "validateChainAgainstSystemTrust", "leafCert", "intermediateCert", "getSystemTrustManager", "isSelfSigned", "cert", "verifyHostname", "matchDomain", "pattern", "extractCn", "extractAiaCaIssuersUrl", "indexOfSubarray", "", "array", "", "sub", "fetchIntermediateCertificate", "urlStr", "getX509Certificate", "sslCert", "Landroid/net/http/SslCertificate;", "extractHostFromUrl", "url", "Decision", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class SslSecurityPolicy {
    public static final int $stable;
    public static final SslSecurityPolicy INSTANCE;
    private static final String TAG = "SslSecurityPolicy";
    private static final ConcurrentHashMap<String, X509Certificate> intermediateCache;
    private static final Set<String> sessionApprovedHosts;

    static {
        INSTANCE = new SslSecurityPolicy();
        intermediateCache = new ConcurrentHashMap();
        sessionApprovedHosts = Collections.synchronizedSet(new LinkedHashSet());
        $stable = 8;
    }

    private SslSecurityPolicy() {
    }

    private final X509TrustManager getSystemTrustManager() {
        X509TrustManager object = null;
        TrustManagerFactory object2 = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        object2.init((KeyStore)null);
        TrustManager[] exception = object2.getTrustManagers();
        Intrinsics.checkNotNullExpressionValue((Object)exception, (String)"getTrustManagers(...)");
        Object[] objectArray = exception;
        Object object32 = new ArrayList();
        Collection collection = (Collection)object32;
        for (Object object32 : objectArray) {
            if (!(object32 instanceof X509TrustManager)) continue;
            collection.add(object32);
        }
        try {
            X509TrustManager x509TrustManager;
            List list = (List)collection;
            object = x509TrustManager = (X509TrustManager)CollectionsKt.firstOrNull((List)list);
        }
        catch (Exception exception2) {
            Log.e((String)TAG, (String)"Error initializing system TrustManagerFactory", (Throwable)exception2);
        }
        return object;
    }

    private final int indexOfSubarray(byte[] byArray, byte[] byArray2) {
        int n = byArray2.length == 0 ? 1 : 0;
        if (n == 0 && byArray.length >= byArray2.length) {
            n = 0;
            int n2 = byArray.length - byArray2.length;
            if (n2 <= 0) {
                while (true) {
                    boolean bl;
                    boolean bl2 = true;
                    int n3 = 0;
                    int n4 = byArray2.length;
                    while (true) {
                        bl = bl2;
                        if (n3 >= n4) break;
                        if (byArray[n + n3] != byArray2[n3]) {
                            bl = false;
                            break;
                        }
                        ++n3;
                    }
                    if (bl) {
                        return n;
                    }
                    if (n == n2) break;
                    ++n;
                }
            }
            return -1;
        }
        return -1;
    }

    public static /* synthetic */ Decision preliminaryCheck$default(SslSecurityPolicy sslSecurityPolicy, SslError sslError, String string, int n, Object object) {
        if ((n & 2) != 0) {
            string = null;
        }
        return sslSecurityPolicy.preliminaryCheck(sslError, string);
    }

    public static /* synthetic */ boolean validateChainAgainstSystemTrust$default(SslSecurityPolicy sslSecurityPolicy, X509Certificate x509Certificate, X509Certificate x509Certificate2, X509TrustManager x509TrustManager, int n, Object object) {
        if ((n & 4) != 0) {
            x509TrustManager = null;
        }
        return sslSecurityPolicy.validateChainAgainstSystemTrust(x509Certificate, x509Certificate2, x509TrustManager);
    }

    public static /* synthetic */ Decision validateIncompleteChain$default(SslSecurityPolicy sslSecurityPolicy, SslError sslError, String string, X509Certificate x509Certificate, Function1 function1, X509TrustManager x509TrustManager, int n, Object object) {
        if ((n & 2) != 0) {
            string = null;
        }
        if ((n & 4) != 0) {
            x509Certificate = null;
        }
        if ((n & 8) != 0) {
            function1 = null;
        }
        if ((n & 0x10) != 0) {
            x509TrustManager = null;
        }
        return sslSecurityPolicy.validateIncompleteChain(sslError, string, x509Certificate, (Function1<? super String, ? extends X509Certificate>)function1, x509TrustManager);
    }

    public final void approveHostForSession(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        string = this.normalizeHost(string);
        sessionApprovedHosts.add(string);
    }

    public final void clearSessionApprovals() {
        sessionApprovedHosts.clear();
    }

    public final String extractAiaCaIssuersUrl(X509Certificate object) {
        block15: {
            int n;
            int n2;
            Object object2;
            block14: {
                block13: {
                    byte[] byArray;
                    Intrinsics.checkNotNullParameter((Object)object, (String)"cert");
                    object2 = object.getExtensionValue("1.3.6.1.5.5.7.1.1");
                    object = null;
                    if (object2 == null) {
                        return null;
                    }
                    byte[] byArray2 = byArray = new byte[10];
                    byArray[0] = 6;
                    byArray2[1] = 8;
                    byArray2[2] = 43;
                    byArray2[3] = 6;
                    byArray2[4] = 1;
                    byArray2[5] = 5;
                    byArray2[6] = 5;
                    byArray2[7] = 7;
                    byArray2[8] = 48;
                    byArray2[9] = 2;
                    n2 = this.indexOfSubarray((byte[])object2, byArray);
                    if (n2 == -1) {
                        return null;
                    }
                    if ((n2 = byArray.length + n2) >= ((byte[])object2).length) {
                        return null;
                    }
                    n = n2;
                    if (object2[n2] != -122) {
                        boolean bl;
                        boolean bl2 = false;
                        int n3 = n2;
                        int n4 = Math.min(n2 + 4, ((byte[])object2).length);
                        while (true) {
                            n = n2;
                            bl = bl2;
                            if (n3 >= n4) break;
                            if (object2[n3] == -122) {
                                n = n3;
                                bl = true;
                                break;
                            }
                            ++n3;
                        }
                        if (!bl) {
                            return null;
                        }
                    }
                    if (++n >= ((byte[])object2).length) {
                        return null;
                    }
                    n2 = object2[n] & 0xFF;
                    if ((n2 & 0x80) != 0) break block13;
                    ++n;
                    break block14;
                }
                if ((n2 & 0x7F) != 1 || ++n >= ((byte[])object2).length) break block15;
                n2 = object2[n] & 0xFF;
                ++n;
            }
            if (n + n2 <= ((byte[])object2).length && n2 > 0) {
                if (StringsKt.startsWith((String)(object2 = (Object)new String((byte[])object2, n, n2, Charsets.US_ASCII)), (String)"http://", (boolean)true) || StringsKt.startsWith((String)object2, (String)"https://", (boolean)true)) {
                    object = object2;
                }
                return object;
            }
            return null;
        }
        return null;
    }

    public final String extractCn(X509Certificate object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"cert");
        object = ((X509Certificate)object).getSubjectX500Principal();
        if (object != null && (object = ((X500Principal)object).getName()) != null) {
            Iterator iterator = StringsKt.split$default((CharSequence)((CharSequence)object), (String[])new String[]{","}, (boolean)false, (int)0, (int)6, null).iterator();
            while (iterator.hasNext()) {
                object = ((Object)StringsKt.trim((CharSequence)((String)iterator.next()))).toString();
                if (!StringsKt.startsWith((String)object, (String)"CN=", (boolean)true)) continue;
                object = ((String)object).substring(3);
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"substring(...)");
                return ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
            }
            return null;
        }
        return null;
    }

    public final String extractHostFromUrl(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        boolean bl = StringsKt.isBlank((CharSequence)string);
        Object var3_4 = null;
        if (bl) {
            return null;
        }
        try {
            string = Uri.parse((String)string).getHost();
        }
        catch (Exception exception) {
            string = var3_4;
        }
        return string;
    }

    public final X509Certificate fetchIntermediateCertificate(String string) {
        Closeable closeable;
        Object object;
        block6: {
            Intrinsics.checkNotNullParameter((Object)string, (String)"urlStr");
            object = intermediateCache.get(string);
            if (object != null) {
                return object;
            }
            object = new URL(string).openConnection();
            Intrinsics.checkNotNull((Object)object, (String)"null cannot be cast to non-null type java.net.HttpURLConnection");
            object = (HttpURLConnection)object;
            ((URLConnection)object).setConnectTimeout(5000);
            ((URLConnection)object).setReadTimeout(5000);
            ((HttpURLConnection)object).setInstanceFollowRedirects(true);
            ((HttpURLConnection)object).setRequestMethod("GET");
            ((URLConnection)object).setRequestProperty("User-Agent", "FocusShield/1.0 (Android)");
            closeable = ((URLConnection)object).getInputStream();
            try {
                object = (InputStream)closeable;
                object = CertificateFactory.getInstance("X.509").generateCertificate((InputStream)object);
                object = object instanceof X509Certificate ? (X509Certificate)object : null;
                if (object == null) break block6;
            }
            catch (Throwable throwable) {
                try {
                    throw throwable;
                }
                catch (Throwable throwable2) {
                    CloseableKt.closeFinally((Closeable)closeable, (Throwable)throwable);
                    throw throwable2;
                }
            }
            ((Map)intermediateCache).put(string, object);
        }
        CloseableKt.closeFinally((Closeable)closeable, null);
        return object;
    }

    public final X509Certificate getX509Certificate(SslCertificate object) {
        Object var4_4;
        block13: {
            Object object2;
            block12: {
                block11: {
                    Object var3_3 = null;
                    var4_4 = null;
                    if (object == null) {
                        return null;
                    }
                    if (Build.VERSION.SDK_INT >= 29) {
                        try {
                            object2 = object.getX509Certificate();
                            if (object2 != null) {
                                return object2;
                            }
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    object = SslCertificate.saveState((SslCertificate)object);
                    if (object == null) break block11;
                    try {
                        object = object.getByteArray("x509-certificate");
                        break block12;
                    }
                    catch (Exception exception) {
                        object = var3_3;
                    }
                }
                object = null;
            }
            if (object != null) {
                boolean bl = ((SslCertificate)object).length == 0;
                if (bl) break block13;
                CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                object2 = new ByteArrayInputStream((byte[])object);
                object = certificateFactory.generateCertificate((InputStream)object2);
                object = object instanceof X509Certificate ? (X509Certificate)object : var4_4;
            }
        }
        object = var4_4;
        return object;
    }

    public final boolean isSelfSigned(X509Certificate x509Certificate) {
        Intrinsics.checkNotNullParameter((Object)x509Certificate, (String)"cert");
        X500Principal x500Principal = x509Certificate.getSubjectX500Principal();
        X500Principal x500Principal2 = x509Certificate.getIssuerX500Principal();
        boolean bl = true;
        if (x500Principal != null && Intrinsics.areEqual((Object)x500Principal, (Object)x500Principal2)) {
            return true;
        }
        try {
            x509Certificate.verify(x509Certificate.getPublicKey());
        }
        catch (Exception exception) {
            bl = false;
        }
        return bl;
    }

    public final boolean isSessionApproved(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        string = this.normalizeHost(string);
        return sessionApprovedHosts.contains(string);
    }

    public final boolean matchDomain(String string, String string2) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"pattern");
        Locale locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        string = string.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        locale = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
        string2 = string2.toLowerCase(locale);
        Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"toLowerCase(...)");
        if (Intrinsics.areEqual((Object)string, (Object)string2)) {
            return true;
        }
        if (StringsKt.startsWith$default((String)string2, (String)"*.", (boolean)false, (int)2, null)) {
            string2 = StringsKt.removePrefix((String)string2, (CharSequence)"*.");
            if (StringsKt.endsWith$default((String)string, (String)("." + string2), (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)string, (Object)string2)) {
                return true ^ StringsKt.contains$default((CharSequence)StringsKt.removeSuffix((String)string, (CharSequence)("." + string2)), (CharSequence)".", (boolean)false, (int)2, null);
            }
        }
        return false;
    }

    public final String normalizeHost(String object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"host");
        String string = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
        object = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"ROOT");
        object = string.toLowerCase((Locale)object);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toLowerCase(...)");
        return StringsKt.removePrefix((String)object, (CharSequence)"www.");
    }

    public final Decision preliminaryCheck(SslError sslError, String string) {
        if (sslError == null) {
            return new Decision.Reject("Null SSL error");
        }
        CharSequence charSequence = sslError.getUrl();
        String string2 = "";
        String string3 = charSequence;
        if (charSequence == null) {
            string3 = "";
        }
        string3 = this.extractHostFromUrl(string3);
        charSequence = string;
        int n = charSequence != null && !StringsKt.isBlank((CharSequence)charSequence) ? 0 : 1;
        if (n != 0) {
            string = string3;
        }
        string3 = string2;
        if (string != null && (string3 = INSTANCE.normalizeHost(string)) == null) {
            string3 = string2;
        }
        if (StringsKt.isBlank((CharSequence)string3)) {
            return new Decision.Reject("Missing host in SSL error");
        }
        if (sslError.hasError(2)) {
            return new Decision.Reject("Hostname mismatch (SSL_IDMISMATCH)");
        }
        if (sslError.hasError(4)) {
            return new Decision.Reject("Certificate date invalid (SSL_DATE_INVALID)");
        }
        if (sslError.hasError(1)) {
            return new Decision.Reject("Certificate expired (SSL_EXPIRED)");
        }
        if (sslError.hasError(0)) {
            return new Decision.Reject("Certificate not yet valid (SSL_NOTYETVALID)");
        }
        if (this.isSessionApproved(string3)) {
            return Decision.ProceedSessionApproved.INSTANCE;
        }
        if (!sslError.hasError(3)) {
            n = sslError.getPrimaryError();
            return new Decision.Reject("SSL error does not contain SSL_UNTRUSTED (primaryError: " + n + ")");
        }
        return null;
    }

    public final void revokeHostApproval(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        string = this.normalizeHost(string);
        sessionApprovedHosts.remove(string);
    }

    public final boolean validateChainAgainstSystemTrust(X509Certificate x509Certificate, X509Certificate x509Certificate2, X509TrustManager x509TrustManager) {
        Intrinsics.checkNotNullParameter((Object)x509Certificate, (String)"leafCert");
        Intrinsics.checkNotNullParameter((Object)x509Certificate2, (String)"intermediateCert");
        if (x509TrustManager == null) {
            X509TrustManager x509TrustManager2 = this.getSystemTrustManager();
            x509TrustManager = x509TrustManager2;
            if (x509TrustManager2 == null) {
                return false;
            }
        }
        for (String string : CollectionsKt.listOf((Object[])new String[]{"RSA", "ECDSA", "UNKNOWN", "GENERIC"})) {
            try {
                x509TrustManager.checkServerTrusted(new X509Certificate[]{x509Certificate, x509Certificate2}, string);
                return true;
            }
            catch (Exception exception) {
            }
        }
        return false;
    }

    /*
     * Unable to fully structure code
     */
    public final Decision validateIncompleteChain(SslError var1_1, String var2_9, X509Certificate var3_10, Function1<? super String, ? extends X509Certificate> var4_11, X509TrustManager var5_12) {
        block36: {
            block35: {
                block29: {
                    block33: {
                        block32: {
                            block28: {
                                block31: {
                                    block30: {
                                        var8_13 = this.preliminaryCheck((SslError)var1_1, (String)var2_9);
                                        if (var8_13 != null) {
                                            return var8_13;
                                        }
                                        var10_14 = "";
                                        if (var1_1 == null) break block30;
                                        var9_15 = var1_1.getUrl();
                                        var8_13 = var9_15;
                                        if (var9_15 != null) break block31;
                                    }
                                    var8_13 = "";
                                }
                                var9_15 = this.extractHostFromUrl((String)var8_13);
                                var11_16 = (CharSequence)var2_9;
                                var7_17 = false;
                                var6_18 = var11_16 == null || StringsKt.isBlank((CharSequence)var11_16);
                                if (var6_18) {
                                    var2_9 = var9_15;
                                }
                                var9_15 = var10_14;
                                if (var2_9 != null && (var9_15 = SslSecurityPolicy.INSTANCE.normalizeHost((String)var2_9)) == null) {
                                    var9_15 = var10_14;
                                }
                                var2_9 = null;
                                if (var3_10 == null) {
                                    var1_1 = var1_1 != null ? var1_1.getCertificate() : null;
                                    var3_10 = this.getX509Certificate((SslCertificate)var1_1);
                                }
                                if (var3_10 == null) {
                                    return new Decision.Reject("Unable to extract X.509 leaf certificate");
                                }
                                try {
                                    var3_10.checkValidity();
                                    if (!this.isSelfSigned(var3_10)) break block28;
                                }
                                catch (Exception var1_7) {
                                    var1_8 = var1_7.getMessage();
                                    return new Decision.Reject("Leaf certificate validity check failed: " + var1_8);
                                }
                                return new Decision.Reject("Self-signed certificate is strictly rejected");
                            }
                            if (!this.verifyHostname(var9_15, var3_10)) {
                                return new Decision.Reject("Hostname '" + var9_15 + "' does not match certificate subject alternative names");
                            }
                            var10_14 = this.extractAiaCaIssuersUrl(var3_10);
                            var1_1 = (CharSequence)var10_14;
                            if (var1_1 == null) break block32;
                            var6_18 = var7_17;
                            if (!StringsKt.isBlank((CharSequence)var1_1)) break block33;
                        }
                        var6_18 = true;
                    }
                    if (var6_18) {
                        return new Decision.Reject("No Authority Information Access (AIA) caIssuers URI found in certificate");
                    }
                    if (var4_11 == null) ** GOTO lbl51
                    try {
                        block34: {
                            var1_1 = (X509Certificate)var4_11.invoke(var10_14);
                            break block34;
lbl51:
                            // 1 sources

                            var1_1 = this.fetchIntermediateCertificate((String)var10_14);
                        }
                        var4_11 = var1_1;
                    }
                    catch (Exception var1_2) {
                        Log.e((String)"SslSecurityPolicy", (String)("Failed to retrieve intermediate certificate from " + (String)var10_14), (Throwable)var1_2);
                        var4_11 = var2_9;
                    }
                    if (var4_11 == null) {
                        return new Decision.Reject("Failed to obtain intermediate certificate from AIA URI (" + (String)var10_14 + ")");
                    }
                    try {
                        var4_11.checkValidity();
                    }
                    catch (Exception var1_5) {
                        var1_6 = var1_5.getMessage();
                        return new Decision.Reject("Intermediate certificate validity check failed: " + var1_6);
                    }
                    try {
                        var3_10.verify(var4_11.getPublicKey());
                        if (this.validateChainAgainstSystemTrust(var3_10, (X509Certificate)var4_11, (X509TrustManager)var5_12)) break block29;
                    }
                    catch (Exception var1_3) {
                        var1_4 = var1_3.getMessage();
                        return new Decision.Reject("Leaf certificate signature verification failed against candidate intermediate: " + var1_4);
                    }
                    return new Decision.Reject("Reconstructed certificate chain could not be validated against system root CAs");
                }
                ((Map)SslSecurityPolicy.intermediateCache).put(var10_14, var4_11);
                var11_16 = new StringBuilder();
                var1_1 = var3_10.getSubjectX500Principal();
                var5_12 = "Unknown";
                if (var1_1 == null) break block35;
                var2_9 = var1_1 = var1_1.getName();
                if (var1_1 != null) break block36;
            }
            var2_9 = "Unknown";
        }
        var10_14 = var4_11.getSubjectX500Principal();
        var1_1 = var5_12;
        if (var10_14 != null && (var1_1 = var10_14.getName()) == null) {
            var1_1 = var5_12;
        }
        var12_19 = var11_16.append("Issued To: ");
        var5_12 = var10_14 = SslSecurityPolicy.INSTANCE.extractCn(var3_10);
        if (var10_14 == null) {
            var5_12 = var2_9;
        }
        var12_19.append((String)var5_12).append("\n");
        var5_12 = var11_16.append("Verified Intermediate: ");
        var2_9 = var4_11 = SslSecurityPolicy.INSTANCE.extractCn((X509Certificate)var4_11);
        if (var4_11 == null) {
            var2_9 = var1_1;
        }
        var5_12.append((String)var2_9).append("\n");
        var11_16.append("Expires: ").append(var3_10.getNotAfter());
        var1_1 = var11_16.toString();
        return new Decision.PromptUser(var9_15, (String)var8_13, (String)var1_1);
    }

    /*
     * Loose catch block
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final boolean verifyHostname(String object, X509Certificate object2) {
        boolean bl;
        String string;
        String string2;
        block23: {
            block20: {
                boolean bl2;
                Object object3;
                Iterator iterator;
                Intrinsics.checkNotNullParameter((Object)object, (String)"host");
                Intrinsics.checkNotNullParameter((Object)object2, (String)"cert");
                string2 = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
                Object object4 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"ROOT");
                object4 = string2.toLowerCase((Locale)object4);
                Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"toLowerCase(...)");
                string2 = StringsKt.removePrefix((String)object4, (CharSequence)"www.");
                object = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
                object4 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"ROOT");
                string = ((String)object).toLowerCase((Locale)object4);
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                boolean bl3 = true;
                bl = true;
                boolean bl4 = bl3;
                object = ((X509Certificate)object2).getSubjectAlternativeNames();
                if (object == null) break block20;
                bl4 = bl3;
                if (object.isEmpty()) break block20;
                bl4 = bl3;
                try {
                    iterator = object.iterator();
                }
                catch (Exception exception) {
                    bl = bl4;
                }
                while (true) {
                    block22: {
                        block21: {
                            bl4 = bl;
                            if (!iterator.hasNext()) return false;
                            bl4 = bl;
                            object3 = (List)iterator.next();
                            bl4 = bl;
                            if (object3.size() < 2) continue;
                            bl4 = bl;
                            object = object3.get(0);
                            bl4 = bl;
                            bl2 = object instanceof Number;
                            object4 = null;
                            if (!bl2) break block21;
                            bl4 = bl;
                            object = (Number)object;
                            break block22;
                        }
                        object = null;
                    }
                    if (object != null) {
                        bl4 = bl;
                        object = ((Number)object).intValue();
                    } else {
                        object = null;
                    }
                    bl4 = bl;
                    object3 = object3.get(bl ? 1 : 0);
                    if (object3 instanceof String) {
                        object4 = (String)object3;
                    }
                    if (object4 == null) continue;
                    break;
                }
                catch (Exception exception) {
                    break block23;
                }
                {
                    object3 = Locale.ROOT;
                    Intrinsics.checkNotNullExpressionValue((Object)object3, (String)"ROOT");
                    object4 = ((String)object4).toLowerCase((Locale)object3);
                    Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"toLowerCase(...)");
                    if (object4 == null || object == null) continue;
                    if ((Integer)object != 2) continue;
                    if (this.matchDomain(string, (String)object4)) return bl;
                    bl2 = this.matchDomain(string2, (String)object4);
                    if (bl2) return bl;
                    continue;
                }
            }
            bl = true;
        }
        object2 = this.extractCn((X509Certificate)object2);
        if (object2 == null) return false;
        object = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"ROOT");
        object = ((String)object2).toLowerCase((Locale)object);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toLowerCase(...)");
        if (object == null) return false;
        if (this.matchDomain(string, (String)object)) return bl;
        if (!this.matchDomain(string2, (String)object)) return false;
        return bl;
    }

    @Metadata(d1={"\u0000\u001a\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\b7\u0018\u00002\u00020\u0001:\u0003\u0004\u0005\u0006B\t\b\u0004\u00a2\u0006\u0004\b\u0002\u0010\u0003\u0082\u0001\u0003\u0007\b\t\u00a8\u0006\n"}, d2={"Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision;", "", "<init>", "()V", "Reject", "ProceedSessionApproved", "PromptUser", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$ProceedSessionApproved;", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$PromptUser;", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$Reject;", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static abstract class Decision {
        public static final int $stable = 0;

        private Decision() {
        }

        public /* synthetic */ Decision(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        @Metadata(d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003\u00a8\u0006\u0004"}, d2={"Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$ProceedSessionApproved;", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision;", "<init>", "()V", "app"}, k=1, mv={2, 2, 0}, xi=48)
        public static final class ProceedSessionApproved
        extends Decision {
            public static final int $stable = 0;
            public static final ProceedSessionApproved INSTANCE = new ProceedSessionApproved();

            private ProceedSessionApproved() {
                super(null);
            }
        }

        @Metadata(d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u00d6\u0003J\t\u0010\u0014\u001a\u00020\u0015H\u00d6\u0001J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00a8\u0006\u0017"}, d2={"Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$PromptUser;", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision;", "host", "", "url", "certDetails", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getHost", "()Ljava/lang/String;", "getUrl", "getCertDetails", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
        public static final class PromptUser
        extends Decision {
            public static final int $stable = 0;
            private final String certDetails;
            private final String host;
            private final String url;

            public PromptUser(String string, String string2, String string3) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"host");
                Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
                Intrinsics.checkNotNullParameter((Object)string3, (String)"certDetails");
                super(null);
                this.host = string;
                this.url = string2;
                this.certDetails = string3;
            }

            public static /* synthetic */ PromptUser copy$default(PromptUser promptUser, String string, String string2, String string3, int n, Object object) {
                if ((n & 1) != 0) {
                    string = promptUser.host;
                }
                if ((n & 2) != 0) {
                    string2 = promptUser.url;
                }
                if ((n & 4) != 0) {
                    string3 = promptUser.certDetails;
                }
                return promptUser.copy(string, string2, string3);
            }

            public final String component1() {
                return this.host;
            }

            public final String component2() {
                return this.url;
            }

            public final String component3() {
                return this.certDetails;
            }

            public final PromptUser copy(String string, String string2, String string3) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"host");
                Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
                Intrinsics.checkNotNullParameter((Object)string3, (String)"certDetails");
                return new PromptUser(string, string2, string3);
            }

            public boolean equals(Object object) {
                if (this == object) {
                    return true;
                }
                if (!(object instanceof PromptUser)) {
                    return false;
                }
                object = (PromptUser)object;
                if (!Intrinsics.areEqual((Object)this.host, (Object)((PromptUser)object).host)) {
                    return false;
                }
                if (!Intrinsics.areEqual((Object)this.url, (Object)((PromptUser)object).url)) {
                    return false;
                }
                return Intrinsics.areEqual((Object)this.certDetails, (Object)((PromptUser)object).certDetails);
            }

            public final String getCertDetails() {
                return this.certDetails;
            }

            public final String getHost() {
                return this.host;
            }

            public final String getUrl() {
                return this.url;
            }

            public int hashCode() {
                return (this.host.hashCode() * 31 + this.url.hashCode()) * 31 + this.certDetails.hashCode();
            }

            public String toString() {
                String string = this.host;
                String string2 = this.url;
                String string3 = this.certDetails;
                return "PromptUser(host=" + string + ", url=" + string2 + ", certDetails=" + string3 + ")";
            }
        }

        @Metadata(d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\t\u0010\b\u001a\u00020\u0003H\u00c6\u0003J\u0013\u0010\t\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\n\u001a\u00020\u000b2\b\u0010\f\u001a\u0004\u0018\u00010\rH\u00d6\u0003J\t\u0010\u000e\u001a\u00020\u000fH\u00d6\u0001J\t\u0010\u0010\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007\u00a8\u0006\u0011"}, d2={"Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision$Reject;", "Lcom/muslim/browser/pro/browser/SslSecurityPolicy$Decision;", "reason", "", "<init>", "(Ljava/lang/String;)V", "getReason", "()Ljava/lang/String;", "component1", "copy", "equals", "", "other", "", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
        public static final class Reject
        extends Decision {
            public static final int $stable = 0;
            private final String reason;

            public Reject(String string) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"reason");
                super(null);
                this.reason = string;
            }

            public static /* synthetic */ Reject copy$default(Reject reject, String string, int n, Object object) {
                if ((n & 1) != 0) {
                    string = reject.reason;
                }
                return reject.copy(string);
            }

            public final String component1() {
                return this.reason;
            }

            public final Reject copy(String string) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"reason");
                return new Reject(string);
            }

            public boolean equals(Object object) {
                if (this == object) {
                    return true;
                }
                if (!(object instanceof Reject)) {
                    return false;
                }
                object = (Reject)object;
                return Intrinsics.areEqual((Object)this.reason, (Object)((Reject)object).reason);
            }

            public final String getReason() {
                return this.reason;
            }

            public int hashCode() {
                return this.reason.hashCode();
            }

            public String toString() {
                String string = this.reason;
                return "Reject(reason=" + string + ")";
            }
        }
    }
}

