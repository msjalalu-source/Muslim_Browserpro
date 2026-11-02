package com.muslim.browser.pro.browser

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BengaliTranslatorTest {

    @Before
    fun setUp() {
        BengaliTranslator.clearCache()
        BengaliTranslator.testTranslatorOverride = null
    }

    @Test
    fun testBengaliTranslatorConstants() {
        assertEquals("bn", BengaliTranslator.TARGET_LANGUAGE)
        assertEquals("gemini-3.5-flash-lite", BengaliTranslator.DEFAULT_MODEL)
    }

    @Test
    fun testBengaliTranslatorLruCache() {
        val cache = BengaliTranslator.TranslationLruCache(3)
        assertEquals(0, cache.size())
        cache.put("k1", "v1")
        cache.put("k2", "v2")
        cache.put("k3", "v3")
        assertEquals("v1", cache.get("k1"))
        assertEquals("v2", cache.get("k2"))
        assertEquals("v3", cache.get("k3"))
        assertEquals(3, cache.size())

        // Access k1, then add k4 -> k2 should be evicted because it was least recently used
        cache.get("k1")
        cache.put("k4", "v4")
        assertEquals(3, cache.size())
        assertEquals("v1", cache.get("k1"))
        assertEquals("v4", cache.get("k4"))
    }

    @Test
    fun testBengaliTranslatorCacheKey() {
        val key = BengaliTranslator.cacheKey(
            text = "  Hello world  ",
            sourceLang = "en",
            targetLang = "bn"
        )
        assertEquals("GEMINI:en:bn:Hello world", key)
    }

    @Test
    fun testBengaliTranslatorIsLikelyEnglish() {
        assertTrue(BengaliTranslator.isLikelyEnglish("Hello world, this is a test news article"))
        assertTrue(BengaliTranslator.isLikelyEnglish("Welcome to our home page"))
        assertFalse(BengaliTranslator.isLikelyEnglish("বাংলা ভাষায় অনুবাদ"))
        assertFalse(BengaliTranslator.isLikelyEnglish("12345"))
        assertFalse(BengaliTranslator.isLikelyEnglish("http://example.com"))
    }

    @Test
    fun testBengaliTranslatorShouldSkipText() {
        assertTrue(BengaliTranslator.shouldSkipText("   "))
        assertTrue(BengaliTranslator.shouldSkipText("a"))
        assertTrue(BengaliTranslator.shouldSkipText("12345"))
        assertTrue(BengaliTranslator.shouldSkipText(".,$%-+/"))
        assertTrue(BengaliTranslator.shouldSkipText("https://example.com/test"))
        assertTrue(BengaliTranslator.shouldSkipText("user@example.com"))
        // Over 60% Bengali characters should be skipped (already translated)
        assertTrue(BengaliTranslator.shouldSkipText("আমার সোনার বাংলা আমি তোমায় ভালোবাসি"))
        assertFalse(BengaliTranslator.shouldSkipText("This is an English paragraph that needs translation."))
    }

    @Test
    fun testBengaliTranslatorDecodeHtmlEntities() {
        val input = "Tom &amp; Jerry &quot;said&quot; &#39;hello&#39; &lt;world&gt; &nbsp; peace"
        val expected = "Tom & Jerry \"said\" 'hello' <world>   peace"
        assertEquals(expected, BengaliTranslator.decodeHtmlEntities(input))
    }

    @Test
    fun testBengaliTranslatorChunkIntoBatches() {
        val texts = (1..75).map { "Item $it" }
        val batches = BengaliTranslator.chunkIntoBatches(texts)
        assertTrue(batches.isNotEmpty())
        assertEquals(2, batches.size) // 60 items max per batch
        assertEquals(60, batches[0].size)
        assertEquals(15, batches[1].size)
    }

    @Test
    fun testBengaliTranslatorScripts() {
        val extract = BengaliTranslator.buildExtractScript()
        assertTrue(extract.contains("window.__bn_nodes = []"))
        assertTrue(extract.contains("NodeFilter.SHOW_TEXT"))
        assertTrue(extract.contains("JSON.stringify({texts: texts})"))

        val replace = BengaliTranslator.buildReplaceScript(listOf("অনুবাদ ১", "অনুবাদ ২"))
        assertTrue(replace.contains("window.__bn_nodes"))
        assertTrue(replace.contains("অনুবাদ ১"))
        assertTrue(replace.contains("অনুবাদ ২"))
    }

    @Test
    fun testBengaliTranslatorTranslateBatchWithOverride() = runBlocking {
        BengaliTranslator.testTranslatorOverride = { text ->
            when (text) {
                "Hello" -> "হ্যালো"
                "World" -> "বিশ্ব"
                else -> text
            }
        }

        val result = BengaliTranslator.translateBatch(listOf("Hello", "World"))
        assertTrue(result.isSuccess)
        val translations = result.getOrThrow()
        assertEquals(2, translations.size)
        assertEquals("হ্যালো", translations[0])
        assertEquals("বিশ্ব", translations[1])
    }

    @Test
    fun testBrowserViewModelTranslationStateTransitions() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = BrowserViewModel(app)

        assertFalse(vm.uiState.value.isTranslating)
        assertFalse(vm.uiState.value.isPageTranslated)

        // When evaluating empty content, isTranslating resets to false
        vm.translateCurrentPage(evaluateJs = { _, callback ->
            callback?.invoke("[]")
        })

        assertFalse(vm.uiState.value.isTranslating)
        assertFalse(vm.uiState.value.isPageTranslated)
    }

    @Test
    fun testBengaliTranslatorCacheHitSpeed() = runBlocking {
        // Pre-populate cache
        BengaliTranslator.putInCache(BengaliTranslator.cacheKey("Fast Title"), "দ্রুত শিরোনাম")
        BengaliTranslator.putInCache(BengaliTranslator.cacheKey("Fast Body"), "দ্রুত বডি")

        val startTime = System.currentTimeMillis()
        val result = BengaliTranslator.translateBatch(listOf("Fast Title", "Fast Body"))
        val duration = System.currentTimeMillis() - startTime

        assertTrue(result.isSuccess)
        val translations = result.getOrThrow()
        assertEquals("দ্রুত শিরোনাম", translations[0])
        assertEquals("দ্রুত বডি", translations[1])
        // Cache hit must resolve virtually instantaneously (< 50ms)
        assertTrue("Cache hit duration was ${duration}ms, expected < 50ms", duration < 50)
    }

    @Test
    fun testBengaliTranslatorProgressiveCallback() = runBlocking {
        BengaliTranslator.putInCache(BengaliTranslator.cacheKey("Cached Item"), "ক্যাশ আইটেম")

        BengaliTranslator.testTranslatorOverride = { text ->
            when (text) {
                "Uncached Item" -> "নতুন আইটেম"
                else -> text
            }
        }

        val progressiveUpdates = mutableListOf<List<String>>()
        val result = BengaliTranslator.translateBatch(listOf("Cached Item", "Uncached Item")) { partial ->
            progressiveUpdates.add(partial)
        }

        assertTrue(result.isSuccess)
        // Should have received progressive updates
        assertTrue(progressiveUpdates.isNotEmpty())
        // First update should already contain the cached translation
        assertEquals("ক্যাশ আইটেম", progressiveUpdates[0][0])
        val finalResult = result.getOrThrow()
        assertEquals("ক্যাশ আইটেম", finalResult[0])
        assertEquals("নতুন আইটেম", finalResult[1])
    }

    @Test
    fun testBengaliTranslatorDuplicateInFlightCoalescing() = runBlocking {
        var callCount = 0
        BengaliTranslator.testTranslatorOverride = { text ->
            callCount++
            Thread.sleep(20)
            "অনুবাদ $text"
        }

        // Run concurrent single-text translations for the same string
        val d1 = async(Dispatchers.IO) {
            BengaliTranslator.translateSingleTextCoalesced("Concurrent Word")
        }
        val d2 = async(Dispatchers.IO) {
            BengaliTranslator.translateSingleTextCoalesced("Concurrent Word")
        }

        val r1 = d1.await()
        val r2 = d2.await()

        assertTrue(r1.isSuccess)
        assertTrue(r2.isSuccess)
        assertEquals("অনুবাদ Concurrent Word", r1.getOrThrow())
        assertEquals("অনুবাদ Concurrent Word", r2.getOrThrow())
    }

    @Test
    fun testParseGeminiResponseSuccess() {
        val json = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "[\"হ্যালো\", \"বিশ্ব\"]"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()
        val result = BengaliTranslator.parseGeminiResponse(json, expectedCount = 2)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        assertEquals("হ্যালো", list[0])
        assertEquals("বিশ্ব", list[1])
    }

    @Test
    fun testParseGeminiResponseWithMarkdownFences() {
        val json = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "```json\n[\"ক\", \"খ\"]\n```"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()
        val result = BengaliTranslator.parseGeminiResponse(json, expectedCount = 2)
        assertTrue(result.isSuccess)
        val list = result.getOrThrow()
        assertEquals(2, list.size)
        assertEquals("ক", list[0])
        assertEquals("খ", list[1])
    }

    @Test
    fun testParseGeminiResponseLengthMismatchThrowsSplittable() {
        val json = """
            {
                "candidates": [
                    {
                        "content": {
                            "parts": [
                                {
                                    "text": "[\"শুধুমাত্র একটি\"]"
                                }
                            ]
                        }
                    }
                ]
            }
        """.trimIndent()
        val result = BengaliTranslator.parseGeminiResponse(json, expectedCount = 2)
        assertTrue(result.isFailure)
        val ex = result.exceptionOrNull()
        assertTrue("Error should be splittable when length mismatches", BengaliTranslator.isSplittableError(ex))
    }

    @Test
    fun testTranslationApiExceptionMessages() {
        val keyEx = BengaliTranslator.TranslationApiException("API Key ভুল বা অনুমতি নেই")
        assertEquals("API Key ভুল বা অনুমতি নেই", keyEx.userMessage)
        assertFalse(BengaliTranslator.isSplittableError(keyEx))

        val modelEx = BengaliTranslator.TranslationApiException("মডেলের নাম ভুল")
        assertEquals("মডেলের নাম ভুল", modelEx.userMessage)

        val quotaEx = BengaliTranslator.TranslationApiException("ব্যবহারের সীমা শেষ, পরে চেষ্টা করুন")
        assertEquals("ব্যবহারের সীমা শেষ, পরে চেষ্টা করুন", quotaEx.userMessage)

        val netEx = BengaliTranslator.TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা")
        assertEquals("ইন্টারনেট বা সার্ভার সমস্যা", netEx.userMessage)

        val regionEx = BengaliTranslator.TranslationApiException("আপনার অঞ্চলে এই সেবা সমর্থিত নয়")
        assertEquals("আপনার অঞ্চলে এই সেবা সমর্থিত নয়", regionEx.userMessage)

        val reqEx = BengaliTranslator.TranslationApiException("অনুরোধে সমস্যা, মডেলের নাম পরীক্ষা করুন")
        assertEquals("অনুরোধে সমস্যা, মডেলের নাম পরীক্ষা করুন", reqEx.userMessage)
    }

    @Test
    fun testPickMostSpecificErrorPrefersSpecificOverGeneric() {
        val generic = BengaliTranslator.TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা")
        val quota = BengaliTranslator.TranslationApiException("ব্যবহারের সীমা শেষ, পরে চেষ্টা করুন")
        val chosen = BengaliTranslator.pickMostSpecificError(listOf(generic, quota))
        assertEquals(quota, chosen)

        val keyErr = BengaliTranslator.TranslationApiException("API Key ভুল বা অনুমতি নেই")
        val chosen2 = BengaliTranslator.pickMostSpecificError(listOf(generic, keyErr, generic))
        assertEquals(keyErr, chosen2)
    }

    @Test
    fun testPickMostSpecificErrorFallbackToGenericWhenAllGeneric() {
        val generic1 = BengaliTranslator.TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা")
        val generic2 = BengaliTranslator.TranslationApiException("ইন্টারনেট বা সার্ভার সমস্যা")
        val chosen = BengaliTranslator.pickMostSpecificError(listOf(generic1, generic2))
        assertEquals(generic1, chosen)
    }

    @Test
    fun testPickMostSpecificErrorReturnsNullWhenAllNull() {
        val chosen = BengaliTranslator.pickMostSpecificError(listOf(null, null))
        org.junit.Assert.assertNull(chosen)
        org.junit.Assert.assertNull(BengaliTranslator.pickMostSpecificError(emptyList()))
    }

    @Test
    fun testBuildRequestBodyGenerationConfigDoesNotHaveTemperature() {
        val body = BengaliTranslator.buildRequestBody(listOf("Hello world"), includeThinkingConfig = true)
        val genConfig = body.getJSONObject("generationConfig")
        assertFalse("generationConfig must not contain temperature", genConfig.has("temperature"))
        assertTrue("generationConfig must contain responseMimeType", genConfig.has("responseMimeType"))
        assertEquals("application/json", genConfig.getString("responseMimeType"))
        assertTrue("generationConfig must contain responseSchema", genConfig.has("responseSchema"))
        assertTrue("generationConfig must contain thinkingConfig when flag is true", genConfig.has("thinkingConfig"))
    }

    @Test
    fun testExtractGoogleErrorMessageSanitization() {
        // 1. Normal message with newlines
        val jsonWithNewlines = """{"error": {"code": 400, "message": "Invalid model\nCheck name\r\nPlease retry"}}"""
        val sanitized = BengaliTranslator.extractGoogleErrorMessage(jsonWithNewlines)
        assertEquals("Invalid model Check name Please retry", sanitized)

        // 2. Message containing AIza API Key pattern
        val jsonWithKey = """{"error": {"code": 400, "message": "API key AIzaSyD12345678901234567890abc is invalid"}}"""
        val masked = BengaliTranslator.extractGoogleErrorMessage(jsonWithKey)
        assertNotNull(masked)
        assertFalse(masked!!.contains("AIzaSyD"))
        assertTrue(masked.contains("***"))
        assertEquals("API key *** is invalid", masked)

        // 3. Very long message exceeds 120 chars
        val longMsg = "A".repeat(150)
        val jsonLong = """{"error": {"code": 400, "message": "$longMsg"}}"""
        val truncated = BengaliTranslator.extractGoogleErrorMessage(jsonLong)
        assertNotNull(truncated)
        assertEquals(120, truncated!!.length)

        // 4. Invalid JSON or missing message
        assertNull(BengaliTranslator.extractGoogleErrorMessage("not a json"))
        assertNull(BengaliTranslator.extractGoogleErrorMessage("""{"error": {}}"""))
        assertNull(BengaliTranslator.extractGoogleErrorMessage("""{"error": {"message": "   "}}"""))
    }
}
