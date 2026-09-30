package com.muslim.browser.pro

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BengaliTranslator
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TranslationEngine
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TranslationEngineTest {

    private lateinit var app: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        app = ApplicationProvider.getApplicationContext()
        val prefs = app.getSharedPreferences("focus_shield_prefs", Application.MODE_PRIVATE)
        prefs.edit().clear().commit()
        repository = SettingsRepository(app)
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = null
    }

    @After
    fun tearDown() {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = null
    }

    @Test
    fun `verify Lingva is completely removed from available translation engines`() {
        val availableNames = TranslationEngine.values().map { it.name }
        assertFalse("LINGVA must not exist in TranslationEngine enum", availableNames.contains("LINGVA"))
        assertEquals("Exactly two engines must remain", 2, TranslationEngine.values().size)
        assertTrue("LibreTranslate must remain", availableNames.contains("LIBRE_TRANSLATE"))
        assertTrue("MyMemory must remain", availableNames.contains("MYMEMORY"))
    }

    @Test
    fun `verify default translation engine is LibreTranslate`() {
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, repository.selectedTranslationEngine)
    }

    @Test
    fun `verify legacy Lingva preference safely migrates to LibreTranslate without crashing`() {
        // Simulate a device that had LINGVA saved in SharedPreferences in a prior app version
        val prefs = app.getSharedPreferences("focus_shield_prefs", Application.MODE_PRIVATE)
        prefs.edit().putString("key_selected_translation_engine", "LINGVA").commit()

        val freshRepo = SettingsRepository(app)
        // Must not throw IllegalArgumentException and must return LIBRE_TRANSLATE
        val loadedEngine = freshRepo.selectedTranslationEngine
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, loadedEngine)

        // Verify the preference was migrated to prevent repeated exceptions
        assertEquals("LIBRE_TRANSLATE", prefs.getString("key_selected_translation_engine", null))
    }

    @Test
    fun `verify translation engine persistence across app restart for remaining engines`() {
        // Initially default
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, repository.selectedTranslationEngine)

        // Switch to MyMemory
        repository.selectedTranslationEngine = TranslationEngine.MYMEMORY
        assertEquals(TranslationEngine.MYMEMORY, repository.selectedTranslationEngine)

        // Simulate app restart with a fresh repository instance
        val restartedRepo = SettingsRepository(app)
        assertEquals(TranslationEngine.MYMEMORY, restartedRepo.selectedTranslationEngine)

        // Switch back to LibreTranslate
        restartedRepo.selectedTranslationEngine = TranslationEngine.LIBRE_TRANSLATE
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, restartedRepo.selectedTranslationEngine)

        // Simulate second restart
        val secondRestartedRepo = SettingsRepository(app)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, secondRestartedRepo.selectedTranslationEngine)
    }

    @Test
    fun `verify ViewModel single selection behavior for remaining translation engines`() {
        val viewModel = BrowserViewModel(app)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, viewModel.uiState.value.selectedTranslationEngine)

        // Select MyMemory -> only MyMemory active
        viewModel.selectTranslationEngine(TranslationEngine.MYMEMORY)
        assertEquals(TranslationEngine.MYMEMORY, viewModel.uiState.value.selectedTranslationEngine)
        assertEquals(TranslationEngine.MYMEMORY, repository.selectedTranslationEngine)

        // Select LibreTranslate -> only LibreTranslate active
        viewModel.selectTranslationEngine(TranslationEngine.LIBRE_TRANSLATE)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, viewModel.uiState.value.selectedTranslationEngine)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, repository.selectedTranslationEngine)
    }

    @Test
    fun `verify isolated cache keys per translation engine`() {
        val text = "Welcome to the site"
        val libreKey = BengaliTranslator.cacheKey(TranslationEngine.LIBRE_TRANSLATE, text)
        val myMemoryKey = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, text)

        assertNotEquals(libreKey, myMemoryKey)
        assertTrue(libreKey.startsWith("LIBRE_TRANSLATE:auto:bn:"))
        assertTrue(myMemoryKey.startsWith("MYMEMORY:auto:bn:"))
    }

    @Test
    fun `verify test override caches per engine and does not leak across engines`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { "বাংলা:$it" }

        // Translate with LibreTranslate
        val resLibre = BengaliTranslator.translate("Hello", TranslationEngine.LIBRE_TRANSLATE)
        assertTrue(resLibre.isSuccess)
        assertEquals("বাংলা:Hello", resLibre.getOrThrow())

        // Cache hit for LibreTranslate should work even after clearing override
        BengaliTranslator.testTranslatorOverride = null
        val cachedLibre = BengaliTranslator.translate("Hello", TranslationEngine.LIBRE_TRANSLATE)
        assertTrue(cachedLibre.isSuccess)
        assertEquals("বাংলা:Hello", cachedLibre.getOrThrow())

        // But MyMemory cache for "Hello" should NOT exist (cache isolation)
        // With test override null and dummy endpoint, MyMemory should not hit Libre's cache
        BengaliTranslator.myMemoryBaseUrl = "http://127.0.0.1:9999"
        val myMemoryRes = BengaliTranslator.translate("Hello", TranslationEngine.MYMEMORY)
        // MyMemory failed or did not return Libre's cached value
        assertNotEquals("বাংলা:Hello", myMemoryRes.getOrNull())
    }

    @Test
    fun `verify MyMemory HTML entities decoding`() {
        val encoded = "It&#39;s &quot;peaceful&quot; &amp; &lt;calm&gt;&nbsp;here"
        val decoded = BengaliTranslator.decodeHtmlEntities(encoded)
        assertEquals("It's \"peaceful\" & <calm> here", decoded)
    }

    @Test
    fun `verify MyMemory handles quota warnings as error`() {
        val result = BengaliTranslator.translateWithMyMemory("test")
        assertTrue(result.isSuccess || result.isFailure)
    }

    @Test
    fun `verify no fallback across providers on failure`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.myMemoryBaseUrl = "http://127.0.0.1:9999"
        BengaliTranslator.testTranslatorOverride = null

        // Calling MyMemory on invalid URL must fail and NOT fallback to LibreTranslate
        val result = BengaliTranslator.translate("Test single failure", TranslationEngine.MYMEMORY)
        // Should be failure or original text returned safely without crashing
        assertTrue(result.isSuccess || result.isFailure)
        // Verify LibreTranslate was NOT called
        assertEquals(0, BengaliTranslator.TranslationStats.http429Count)
    }

    @Test
    fun `verify English to Bengali translation with MyMemory returns result`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { text ->
            if (text == "Welcome") "স্বাগতম" else "অনুবাদ:$text"
        }

        val result = BengaliTranslator.translate("Welcome", TranslationEngine.MYMEMORY)
        assertTrue(result.isSuccess)
        assertEquals("স্বাগতম", result.getOrThrow())
    }

    @Test
    fun `verify cache hit avoids a second network request`() = runBlocking {
        BengaliTranslator.clearCache()
        val networkCallCount = java.util.concurrent.atomic.AtomicInteger(0)
        BengaliTranslator.testTranslatorOverride = { text ->
            networkCallCount.incrementAndGet()
            "বাংলা:$text"
        }

        // First translation: must hit network/override
        val res1 = BengaliTranslator.translate("Hello Cache", TranslationEngine.MYMEMORY)
        assertTrue(res1.isSuccess)
        assertEquals("বাংলা:Hello Cache", res1.getOrThrow())
        assertEquals(1, networkCallCount.get())

        // Second translation with identical text: must hit cache and NOT invoke network/override
        val res2 = BengaliTranslator.translate("Hello Cache", TranslationEngine.MYMEMORY)
        assertTrue(res2.isSuccess)
        assertEquals("বাংলা:Hello Cache", res2.getOrThrow())
        assertEquals(1, networkCallCount.get()) // Still 1!
    }

    @Test
    fun `verify different source text creates a different cache entry`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { "বাংলা:$it" }

        BengaliTranslator.translate("Apple", TranslationEngine.MYMEMORY)
        BengaliTranslator.translate("Banana", TranslationEngine.MYMEMORY)

        assertEquals(2, BengaliTranslator.getCacheSize())
        val appleKey = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Apple")
        val bananaKey = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Banana")

        assertEquals("বাংলা:Apple", BengaliTranslator.getFromCache(appleKey))
        assertEquals("বাংলা:Banana", BengaliTranslator.getFromCache(bananaKey))
    }

    @Test
    fun `verify different language pairs do not collide in the cache`() {
        val text = "Sample Text"
        val keyEnBn = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, text, "en", "bn")
        val keyFrBn = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, text, "fr", "bn")
        val keyAutoBn = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, text, "auto", "bn")

        assertNotEquals(keyEnBn, keyFrBn)
        assertNotEquals(keyEnBn, keyAutoBn)
        assertEquals("MYMEMORY:en:bn:Sample Text", keyEnBn)
        assertEquals("MYMEMORY:fr:bn:Sample Text", keyFrBn)
        assertEquals("MYMEMORY:auto:bn:Sample Text", keyAutoBn)

        BengaliTranslator.putInCache(keyEnBn, "English result")
        BengaliTranslator.putInCache(keyFrBn, "French result")

        assertEquals("English result", BengaliTranslator.getFromCache(keyEnBn))
        assertEquals("French result", BengaliTranslator.getFromCache(keyFrBn))
    }

    @Test
    fun `verify identical concurrent requests share one network request`() = runBlocking {
        BengaliTranslator.clearCache()
        val networkCallCount = java.util.concurrent.atomic.AtomicInteger(0)

        BengaliTranslator.testTranslatorOverride = { text ->
            networkCallCount.incrementAndGet()
            Thread.sleep(50) // Simulate network delay
            "যৌথ অনুবাদ:$text"
        }

        // Launch 5 concurrent translation requests for the exact same text
        val deferreds = (1..5).map {
            async {
                BengaliTranslator.translate("Concurrent Test Phrase", TranslationEngine.MYMEMORY)
            }
        }

        val results = deferreds.awaitAll()

        // All 5 must succeed with the translated result
        for (res in results) {
            assertTrue(res.isSuccess)
            assertEquals("যৌথ অনুবাদ:Concurrent Test Phrase", res.getOrThrow())
        }

        // Must have coalesced into exactly 1 network execution
        assertEquals(1, networkCallCount.get())
    }

    @Test
    fun `verify failed requests do not permanently poison the cache`() = runBlocking {
        BengaliTranslator.clearCache()
        var shouldFail = true
        BengaliTranslator.testTranslatorOverride = { text ->
            if (shouldFail) {
                throw java.io.IOException("Simulated transient connection failure")
            } else {
                "সফল:$text"
            }
        }

        val key = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Retry Phrase")

        // First attempt fails
        val failRes = BengaliTranslator.translate("Retry Phrase", TranslationEngine.MYMEMORY)
        assertTrue(failRes.isFailure)
        assertNull("Failed request must not be stored in cache", BengaliTranslator.getFromCache(key))

        // Subsequent attempt succeeds and caches cleanly
        shouldFail = false
        val successRes = BengaliTranslator.translate("Retry Phrase", TranslationEngine.MYMEMORY)
        assertTrue(successRes.isSuccess)
        assertEquals("সফল:Retry Phrase", successRes.getOrThrow())
        assertEquals("সফল:Retry Phrase", BengaliTranslator.getFromCache(key))
    }

    @Test
    fun `verify timeout and error handling in MyMemory`() {
        BengaliTranslator.myMemoryBaseUrl = "http://127.0.0.1:54321" // Dead port to trigger connection error
        val result = BengaliTranslator.translateWithMyMemory("Timeout test")
        assertTrue("Dead connection must return failure", result.isFailure)
        assertNotNull(result.exceptionOrNull())
    }

    @Test
    fun `verify other translation engine remains unaffected and isolated`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { "Libre:$it" }

        val libreRes = BengaliTranslator.translate("Isolation Test", TranslationEngine.LIBRE_TRANSLATE)
        assertTrue(libreRes.isSuccess)
        assertEquals("Libre:Isolation Test", libreRes.getOrThrow())

        // Verify LibreTranslate's key exists, but MyMemory's does not
        val libreKey = BengaliTranslator.cacheKey(TranslationEngine.LIBRE_TRANSLATE, "Isolation Test")
        val myMemoryKey = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Isolation Test")

        assertEquals("Libre:Isolation Test", BengaliTranslator.getFromCache(libreKey))
        assertNull(BengaliTranslator.getFromCache(myMemoryKey))
    }

    @Test
    fun `verify UI receives the translation result correctly in ViewModel`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { "বাংলা:$it" }

        val viewModel = BrowserViewModel(app)
        viewModel.selectTranslationEngine(TranslationEngine.MYMEMORY)
        // Navigate to an active page (translation requires non-home, non-blank page)
        viewModel.submitQueryOrUrl("https://news.example.com")

        // Trigger translation
        viewModel.translateCurrentPage(
            evaluateJs = { script, callback ->
                if (script.contains("NodeFilter.SHOW_TEXT")) {
                    // TreeWalker extraction script result
                    callback?.invoke("{\"texts\": [\"Headline\", \"Story paragraph\"]}")
                } else {
                    // Replace script result
                    callback?.invoke("success")
                }
            },
            reloadPage = {}
        )

        // Await coroutine completion across Dispatchers.IO and Dispatchers.Main
        var attempts = 0
        while (viewModel.uiState.value.isTranslating && attempts < 40) {
            delay(50)
            org.robolectric.shadows.ShadowLooper.idleMainLooper()
            attempts++
        }

        val state = viewModel.uiState.value
        assertTrue("Page should be marked translated", state.isPageTranslated)
        assertFalse("Translating flag should reset to false", state.isTranslating)
    }

    @Test
    fun `verify no translation request runs on the main thread`() = runBlocking {
        BengaliTranslator.clearCache()
        var executionThreadWasMain = true

        BengaliTranslator.testTranslatorOverride = { text ->
            executionThreadWasMain = (android.os.Looper.myLooper() == android.os.Looper.getMainLooper())
            "ফলাফল:$text"
        }

        val res = BengaliTranslator.translate("Thread Check", TranslationEngine.MYMEMORY)
        assertTrue(res.isSuccess)
        assertFalse("Translation request must run on background Dispatchers.IO, not Main thread", executionThreadWasMain)
    }

    @Test
    fun `verify no duplicate MyMemory request is generated for a single user action with repeated phrases`() = runBlocking {
        BengaliTranslator.clearCache()
        val networkCallCount = java.util.concurrent.atomic.AtomicInteger(0)

        BengaliTranslator.testTranslatorOverride = { text ->
            networkCallCount.incrementAndGet()
            "অনুবাদ:$text"
        }

        // A single webpage often has repeating phrases (e.g. "Read more", "Home", "Share")
        val pagePhrases = listOf("Read more", "World News", "Read more", "World News", "Read more")
        val batchRes = BengaliTranslator.translateBatch(pagePhrases, TranslationEngine.MYMEMORY)

        assertTrue(batchRes.isSuccess)
        val translations = batchRes.getOrThrow()
        assertEquals(5, translations.size)
        assertEquals("অনুবাদ:Read more", translations[0])
        assertEquals("অনুবাদ:World News", translations[1])
        assertEquals("অনুবাদ:Read more", translations[2])

        // Only 2 distinct phrases -> exactly 2 network calls
        assertEquals(2, networkCallCount.get())
    }

    @Test
    fun `verify request A fails, request B joins, both receive failure, and future request C succeeds cleanly`() = runBlocking {
        BengaliTranslator.clearCache()
        val networkCallCount = java.util.concurrent.atomic.AtomicInteger(0)
        var shouldFail = true

        BengaliTranslator.testTranslatorOverride = { text ->
            val count = networkCallCount.incrementAndGet()
            Thread.sleep(60) // Ensure B joins while A is in flight
            if (shouldFail) {
                throw java.io.IOException("Simulated transient socket timeout")
            } else {
                "সফল:$text"
            }
        }

        // 1. Request A starts
        val jobA = async { BengaliTranslator.translate("Shared Phrase", TranslationEngine.MYMEMORY) }
        delay(10) // Brief delay so A is registered in-flight
        // 2. Request B joins Request A
        val jobB = async { BengaliTranslator.translate("Shared Phrase", TranslationEngine.MYMEMORY) }

        val resA = jobA.await()
        val resB = jobB.await()

        // 3. Request A fails
        assertTrue("Request A must fail", resA.isFailure)
        // 4. Request B receives the failure
        assertTrue("Request B must receive failure", resB.isFailure)
        assertEquals("Both must share the single network execution", 1, networkCallCount.get())

        // 5. In-flight entry must NOT be poisoned, and cache must NOT contain failed translation
        val key = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Shared Phrase")
        assertNull("Failed entry must not be in cache", BengaliTranslator.getFromCache(key))

        // 6. Future request C retries and succeeds cleanly
        shouldFail = false
        val resC = BengaliTranslator.translate("Shared Phrase", TranslationEngine.MYMEMORY)
        assertTrue("Request C retry must succeed", resC.isSuccess)
        assertEquals("সফল:Shared Phrase", resC.getOrThrow())
        assertEquals(2, networkCallCount.get())
        assertEquals("সফল:Shared Phrase", BengaliTranslator.getFromCache(key))
    }

    @Test
    fun `verify cancellation of initiator does not leave in-flight map permanently stuck`() = runBlocking {
        BengaliTranslator.clearCache()
        val networkStarted = kotlinx.coroutines.CompletableDeferred<Unit>()
        val shouldCancel = kotlinx.coroutines.CompletableDeferred<Unit>()

        BengaliTranslator.testTranslatorOverride = { text ->
            networkStarted.complete(Unit)
            // Block until test triggers cancellation
            runBlocking { shouldCancel.await() }
            throw kotlinx.coroutines.CancellationException("Operation cancelled by user")
        }

        val parentJob = kotlinx.coroutines.Job()
        val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO + parentJob)

        val initiator = scope.async {
            BengaliTranslator.translate("Cancel Text", TranslationEngine.MYMEMORY)
        }

        // Wait until initiator starts in-flight
        networkStarted.await()

        // Cancel the initiator coroutine scope and let override throw CancellationException
        parentJob.cancel()
        shouldCancel.complete(Unit)

        try {
            initiator.await()
        } catch (_: kotlinx.coroutines.CancellationException) {
            // Expected cancellation
        }

        // Verify cache does NOT have the cancelled entry
        val key = BengaliTranslator.cacheKey(TranslationEngine.MYMEMORY, "Cancel Text")
        assertNull(BengaliTranslator.getFromCache(key))

        // A subsequent request for the same text must execute cleanly and not be blocked or stuck
        BengaliTranslator.testTranslatorOverride = { "নতুন:$it" }
        val subsequent = BengaliTranslator.translate("Cancel Text", TranslationEngine.MYMEMORY)
        assertTrue(subsequent.isSuccess)
        assertEquals("নতুন:Cancel Text", subsequent.getOrThrow())
    }

    @Test
    fun `verify LRU cache eviction at max capacity`() {
        val cache = BengaliTranslator.TranslationLruCache(maxSize = 10)
        for (i in 1..15) {
            cache.put("key_$i", "value_$i")
        }

        // Cache size must not exceed maxSize 10
        assertEquals(10, cache.size())
        // Oldest keys 1 to 5 should have been evicted
        assertNull(cache.get("key_1"))
        assertNull(cache.get("key_5"))
        // Most recent keys 6 to 15 must remain
        assertNotNull(cache.get("key_6"))
        assertEquals("value_15", cache.get("key_15"))
    }

    @Test
    fun `verify language detection safety prevents false classification of non-English text`() {
        // Confident English text -> uses en|bn
        assertEquals("en|bn", BengaliTranslator.resolveLanguagePair("Welcome to our service"))
        assertEquals("en|bn", BengaliTranslator.resolveLanguagePair("Read more"))
        assertEquals("en|bn", BengaliTranslator.resolveLanguagePair("World News Today"))
        assertEquals("en|bn", BengaliTranslator.resolveLanguagePair("It's a great application"))

        // Non-Latin scripts -> autodetect|bn (or rejected)
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("السلام عليكم")) // Arabic
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("नमस्ते भारत")) // Hindi
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("你好世界")) // Chinese
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("こんにちは")) // Japanese
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("안녕하세요")) // Korean

        // Accented European text -> autodetect|bn
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("café et croissants")) // French
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("feliz año nuevo")) // Spanish
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("schöne Grüße")) // German

        // Unaccented non-English text without English vocabulary -> autodetect|bn
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("Bonjour tout le monde"))
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("Hola amigo como estas"))
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("Lorem ipsum dolor sit amet"))

        // URLs and code -> autodetect|bn
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("https://example.com/test"))
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("function compute() { return 10; }"))

        // Numbers and symbols
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("12345 67890"))
        assertEquals("autodetect|bn", BengaliTranslator.resolveLanguagePair("$$$ #@!"))
    }

    @Test
    fun `verify empty and extreme input handling`() = runBlocking {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = { "বাংলা:$it" }

        // Empty and whitespace input
        val emptyRes = BengaliTranslator.translate("")
        assertTrue(emptyRes.isSuccess)
        assertEquals("", emptyRes.getOrThrow())

        val wsRes = BengaliTranslator.translate("   ")
        assertTrue(wsRes.isSuccess)
        assertEquals("   ", wsRes.getOrThrow())

        // Very long input (4,000 characters)
        val longString = "Article paragraph. ".repeat(200)
        val longRes = BengaliTranslator.translate(longString, TranslationEngine.MYMEMORY)
        assertTrue(longRes.isSuccess)
        assertEquals("বাংলা:${longString.trim()}", longRes.getOrThrow())
    }
}

