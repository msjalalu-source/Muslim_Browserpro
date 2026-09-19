import com.example.browser.ProtectionEngine;
import com.example.browser.ProtectionEngine.FilterResult;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class ProtectionKeywordsTest {
    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static void assertTrue(String msg, boolean condition) {
        testsRun++;
        if (condition) {
            testsPassed++;
        } else {
            testsFailed++;
            System.err.println("FAIL: " + msg);
        }
    }

    private static void assertFalse(String msg, boolean condition) {
        assertTrue(msg, !condition);
    }

    private static boolean isBlocked(String input) {
        return isBlocked(input, Collections.emptySet());
    }

    private static boolean isBlocked(String input, Set<String> custom) {
        FilterResult result = ProtectionEngine.INSTANCE.checkUrlOrQuery(input, custom);
        return result instanceof FilterResult.Blocked;
    }

    public static void main(String[] args) {
        System.out.println("=== RUNNING PROTECTION ENGINE BUILT-IN KEYWORDS TEST SUITE ===");

        // 1. "x" Whole-word tests (BLOCK cases)
        assertTrue("'x' must be blocked", isBlocked("x"));
        assertTrue("'X' must be blocked", isBlocked("X"));
        assertTrue("'x video' must be blocked", isBlocked("x video"));
        assertTrue("'video x' must be blocked", isBlocked("video x"));
        assertTrue("'porn x' must be blocked", isBlocked("porn x"));
        assertTrue("'https://duckduckgo.com/?q=x' must be blocked", isBlocked("https://duckduckgo.com/?q=x"));
        assertTrue("'https://duckduckgo.com/?q=video%20x' must be blocked", isBlocked("https://duckduckgo.com/?q=video%20x"));
        assertTrue("'https://duckduckgo.com/?q=x%20video' must be blocked", isBlocked("https://duckduckgo.com/?q=x%20video"));

        // 2. "x" Non-whole-word / Substring tests (ALLOW cases - false positive prevention)
        assertFalse("'example' must NOT be blocked", isBlocked("example"));
        assertFalse("'text' must NOT be blocked", isBlocked("text"));
        assertFalse("'extra' must NOT be blocked", isBlocked("extra"));
        assertFalse("'exchange' must NOT be blocked", isBlocked("exchange"));
        assertFalse("'galaxy' must NOT be blocked", isBlocked("galaxy"));
        assertFalse("'oxygen' must NOT be blocked", isBlocked("oxygen"));
        assertFalse("'https://example.com' must NOT be blocked", isBlocked("https://example.com"));
        assertFalse("'https://developer.android.com' must NOT be blocked", isBlocked("https://developer.android.com"));

        // 3. "Browser" and variants Whole-word tests (BLOCK cases)
        assertTrue("'browser' must be blocked", isBlocked("browser"));
        assertTrue("'Browser' must be blocked", isBlocked("Browser"));
        assertTrue("'BROWSER' must be blocked", isBlocked("BROWSER"));
        assertTrue("'my browser' must be blocked", isBlocked("my browser"));
        assertTrue("'web browser' must be blocked", isBlocked("web browser"));
        assertTrue("'browsers' must be blocked", isBlocked("browsers"));
        assertTrue("'Browsers' must be blocked", isBlocked("Browsers"));
        assertTrue("'browsering' must be blocked", isBlocked("browsering"));
        assertTrue("'Browsering' must be blocked", isBlocked("Browsering"));
        assertTrue("'browsered' must be blocked", isBlocked("browsered"));
        assertTrue("'Browsered' must be blocked", isBlocked("Browsered"));
        assertTrue("'https://duckduckgo.com/?q=my%20browser' must be blocked", isBlocked("https://duckduckgo.com/?q=my%20browser"));

        // 4. "Browser" and variants Substring tests (ALLOW cases - false positive prevention)
        assertFalse("'browserexample' must NOT be blocked", isBlocked("browserexample"));
        assertFalse("'mybrowser' must NOT be blocked", isBlocked("mybrowser"));
        assertFalse("'mybrowsers' must NOT be blocked", isBlocked("mybrowsers"));
        assertFalse("'browseringtest' must NOT be blocked", isBlocked("browseringtest"));
        assertFalse("'browseredtest' must NOT be blocked", isBlocked("browseredtest"));
        assertFalse("'https://example.com/mybrowser' must NOT be blocked", isBlocked("https://example.com/mybrowser"));

        // 5. Built-in standard blocked keywords tests (BLOCK cases)
        assertTrue("'Aashiq Banaya' must be blocked", isBlocked("Aashiq Banaya"));
        assertTrue("'aashiq banaya' must be blocked", isBlocked("aashiq banaya"));
        assertTrue("'AASHIQ BANAYA video' must be blocked", isBlocked("AASHIQ BANAYA video"));
        assertTrue("'hot' must be blocked", isBlocked("hot"));
        assertTrue("'HOT' must be blocked", isBlocked("HOT"));
        assertTrue("'adult' must be blocked", isBlocked("adult"));
        assertTrue("'ADULT' must be blocked", isBlocked("ADULT"));
        assertTrue("'porn' must be blocked", isBlocked("porn"));
        assertTrue("'PORN' must be blocked", isBlocked("PORN"));
        assertTrue("'sex' must be blocked", isBlocked("sex"));
        assertTrue("'SEX' must be blocked", isBlocked("SEX"));
        assertTrue("'xxx' must be blocked", isBlocked("xxx"));
        assertTrue("'XXX' must be blocked", isBlocked("XXX"));
        assertTrue("'18+' must be blocked", isBlocked("18+"));
        assertTrue("'18+ content' must be blocked", isBlocked("18+ content"));
        assertTrue("'intimate' must be blocked", isBlocked("intimate"));
        assertTrue("'INTIMATE' must be blocked", isBlocked("INTIMATE"));
        assertTrue("'kiss' must be blocked", isBlocked("kiss"));
        assertTrue("'KISS' must be blocked", isBlocked("KISS"));

        // 6. Adult Domains tests (Adult protection integrity)
        assertTrue("'pornhub.com' must be blocked", isBlocked("https://www.pornhub.com"));
        assertTrue("'xvideos.com' must be blocked", isBlocked("https://xvideos.com/video123"));
        assertTrue("'redtube.com' must be blocked", isBlocked("https://redtube.com/watch"));
        assertTrue("'rule34.xxx' must be blocked", isBlocked("https://rule34.xxx"));

        // 7. Legitimate domains integrity
        assertFalse("'wikipedia.org' must NOT be blocked", isBlocked("https://en.wikipedia.org/wiki/Main_Page"));
        assertFalse("'github.com' must NOT be blocked", isBlocked("https://github.com/torvalds/linux"));
        assertFalse("'kotlinlang.org' must NOT be blocked", isBlocked("https://kotlinlang.org"));
        assertFalse("'news.google.com' must NOT be blocked", isBlocked("https://news.google.com"));

        // 8. Custom keywords coexistence & deduplication
        Set<String> customKeywords = new HashSet<>();
        customKeywords.add("socialfeed");
        customKeywords.add("browser"); // Duplicate of built-in keyword

        assertTrue("Custom keyword 'socialfeed' must be blocked", isBlocked("how to open socialfeed today", customKeywords));
        assertTrue("Built-in keyword 'browser' still blocked without duplicate evaluation", isBlocked("my browser", customKeywords));
        assertFalse("Unrelated term allowed with custom keywords present", isBlocked("learn kotlin coroutines", customKeywords));

        // 9. Ad domain filtering integrity
        assertTrue("Ad domain doubleclick must be blocked", ProtectionEngine.INSTANCE.isAdRequest("https://ad.doubleclick.net/ddm/trackclk/"));
        assertTrue("Ad domain googlesyndication must be blocked", ProtectionEngine.INSTANCE.isAdRequest("https://pagead2.googlesyndication.com/pagead/js/adsbygoogle.js"));
        assertFalse("Wikipedia must not be flagged as ad", ProtectionEngine.INSTANCE.isAdRequest("https://en.wikipedia.org"));

        // 10. Download filtering integrity
        assertTrue("Video mp4 blocked", ProtectionEngine.INSTANCE.checkDownloadType("https://example.com/clip.mp4", "video/mp4", null) == ProtectionEngine.DownloadStatus.BLOCKED_VIDEO);
        assertTrue("Audio mp3 blocked", ProtectionEngine.INSTANCE.checkDownloadType("https://example.com/song.mp3", "audio/mpeg", null) == ProtectionEngine.DownloadStatus.BLOCKED_AUDIO);
        assertTrue("APK blocked", ProtectionEngine.INSTANCE.checkDownloadType("https://example.com/app.apk", "application/vnd.android.package-archive", null) == ProtectionEngine.DownloadStatus.BLOCKED_APK);
        assertTrue("Image jpg allowed", ProtectionEngine.INSTANCE.checkDownloadType("https://example.com/photo.jpg", "image/jpeg", null) == ProtectionEngine.DownloadStatus.ALLOWED_IMAGE);
        assertTrue("PDF allowed", ProtectionEngine.INSTANCE.checkDownloadType("https://example.com/doc.pdf", "application/pdf", null) == ProtectionEngine.DownloadStatus.ALLOWED_PDF);

        System.out.println("--------------------------------------------------");
        System.out.println("TOTAL TESTS RUN: " + testsRun);
        System.out.println("PASSED: " + testsPassed);
        System.out.println("FAILED: " + testsFailed);
        if (testsFailed > 0) {
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED WITH 100% SUCCESS!");
        }
    }
}
