package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LibreTranslateClientTest {

    @Test
    fun testTranslationEngineEnumOnlyContainsLibreTranslate() {
        val engines = TranslationEngine.values()
        assertEquals(1, engines.size)
        assertEquals(TranslationEngine.LIBRE_TRANSLATE, engines[0])
        assertEquals("LibreTranslate", engines[0].displayName)
    }

    @Test
    fun testDefaultServerUrl() {
        assertEquals("https://libretranslate.com", LibreTranslateClient.DEFAULT_SERVER_URL)
        val context = ApplicationProvider.getApplicationContext<Context>()
        val url = LibreTranslateClient.getServerUrl(context)
        assertEquals("https://libretranslate.com", url)
    }

    @Test
    fun testCustomServerUrlAndApiKeyPersistence() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        LibreTranslateClient.setServerUrl(context, "https://translate.terraprint.co")
        assertEquals("https://translate.terraprint.co", LibreTranslateClient.getServerUrl(context))

        LibreTranslateClient.setApiKey(context, "test_api_key_12345")
        assertEquals("test_api_key_12345", LibreTranslateClient.getApiKey(context))

        // Reset to default
        LibreTranslateClient.setServerUrl(context, "")
        assertEquals("https://libretranslate.com", LibreTranslateClient.getServerUrl(context))
    }

    @Test
    fun testParseResponseArrayOfStrings() {
        val json = """{"translatedText": ["হ্যালো", "বিশ্ব"]}"""
        val parsed = LibreTranslateClient.parseLibreTranslateResponse(json, 2)
        assertEquals(2, parsed.size)
        assertEquals("হ্যালো", parsed[0])
        assertEquals("বিশ্ব", parsed[1])
    }

    @Test
    fun testParseResponseSingleString() {
        val json = """{"translatedText": "হ্যালো"}"""
        val parsed = LibreTranslateClient.parseLibreTranslateResponse(json, 1)
        assertEquals(1, parsed.size)
        assertEquals("হ্যালো", parsed[0])
    }

    @Test
    fun testParseResponseArrayOfObjects() {
        val json = """[{"translatedText": "প্রথম"}, {"translatedText": "দ্বিতীয়"}]"""
        val parsed = LibreTranslateClient.parseLibreTranslateResponse(json, 2)
        assertEquals(2, parsed.size)
        assertEquals("প্রথম", parsed[0])
        assertEquals("দ্বিতীয়", parsed[1])
    }

    @Test
    fun testParseErrorObject() {
        val json = """{"error": "API key required"}"""
        val error = LibreTranslateClient.parseLibreTranslateError(json)
        assertEquals("API key required", error)
    }

    @Test
    fun testParseErrorMessageFallback() {
        val json = """{"message": "Rate limit exceeded"}"""
        val error = LibreTranslateClient.parseLibreTranslateError(json)
        assertEquals("Rate limit exceeded", error)
    }
}
