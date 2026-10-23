package com.muslim.browser.pro.browser

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
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
    fun testTranslationEngineEnum() {
        val engines = TranslationEngine.values()
        assertEquals(1, engines.size)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, engines[0])
        assertEquals("LibreTranslate", engines[0].displayName)
    }

    @Test
    fun testBengaliTranslatorConstants() {
        assertEquals("bn", BengaliTranslator.TARGET_LANGUAGE)
        assertEquals("https://translate.disroot.org/translate", BengaliTranslator.LIBRE_TRANSLATE_URL)
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
        assertEquals("LIBRE_TRANSLATE:en:bn:Hello world", key)
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
        val texts = (1..30).map { "Item $it" }
        val batches = BengaliTranslator.chunkIntoBatches(texts)
        assertTrue(batches.isNotEmpty())
        assertEquals(2, batches.size) // 25 items max per batch
        assertEquals(25, batches[0].size)
        assertEquals(5, batches[1].size)
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
}
