package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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

    @Test
    fun testPortalErrorIsSanitizedWithoutLeakingUrl() {
        val json = """{"error": "Visit https://portal.libretranslate.com to get an API key"}"""
        val error = LibreTranslateClient.parseLibreTranslateError(json)
        assertEquals("API key required", error)
        assertTrue(!error.contains("portal.libretranslate.com"))
    }

    @Test
    fun testBuildJsonRequestBodySingleTextWithoutApiKey() {
        val json = LibreTranslateClient.buildJsonRequestBody(
            texts = listOf("Hello world"),
            sourceLang = "auto",
            targetLang = "bn",
            apiKey = ""
        )
        assertEquals("Hello world", json.getString("q"))
        assertEquals("auto", json.getString("source"))
        assertEquals("bn", json.getString("target"))
        assertEquals("text", json.getString("format"))
        assertFalse(json.has("api_key"))
    }

    @Test
    fun testBuildJsonRequestBodyBatchTextsWithApiKey() {
        val texts = listOf("Title", "Description", "Footer")
        val json = LibreTranslateClient.buildJsonRequestBody(
            texts = texts,
            sourceLang = "auto",
            targetLang = "bn",
            apiKey = "my_secret_key_999"
        )
        val qArray = json.getJSONArray("q")
        assertEquals(3, qArray.length())
        assertEquals("Title", qArray.getString(0))
        assertEquals("Description", qArray.getString(1))
        assertEquals("Footer", qArray.getString(2))
        assertEquals("auto", json.getString("source"))
        assertEquals("bn", json.getString("target"))
        assertEquals("text", json.getString("format"))
        assertEquals("my_secret_key_999", json.getString("api_key"))
    }

    @Test
    fun testParseResponseMalformedOrEmptyReturnsEmptyList() {
        val emptyList1 = LibreTranslateClient.parseLibreTranslateResponse("", 1)
        assertTrue(emptyList1.isEmpty())

        val emptyList2 = LibreTranslateClient.parseLibreTranslateResponse("<html>502 Bad Gateway</html>", 1)
        assertTrue(emptyList2.isEmpty())

        val emptyList3 = LibreTranslateClient.parseLibreTranslateResponse("{}", 1)
        assertTrue(emptyList3.isEmpty())

        val emptyList4 = LibreTranslateClient.parseLibreTranslateResponse("""{"error": "Cannot detect language"}""", 1)
        assertTrue(emptyList4.isEmpty())
    }

    @Test
    fun testParseErrorSanitizationVariousFormats() {
        assertEquals("", LibreTranslateClient.parseLibreTranslateError(""))
        assertEquals("", LibreTranslateClient.parseLibreTranslateError("   "))

        val invalidKeyJson = """{"error": "Invalid API key"}"""
        assertEquals("API key required", LibreTranslateClient.parseLibreTranslateError(invalidKeyJson))

        val plainTextError = "Server temporary unavailable"
        assertEquals("Server temporary unavailable", LibreTranslateClient.parseLibreTranslateError(plainTextError))
    }

    @Test
    fun testBrowserViewModelTranslationStateTransitions() {
        val app = ApplicationProvider.getApplicationContext<android.app.Application>()
        val vm = BrowserViewModel(app)

        assertFalse(vm.uiState.value.isTranslating)
        assertFalse(vm.uiState.value.isPageTranslated)

        // When evaluating empty content, isTranslating immediately resets to false
        vm.translateCurrentPage(evalScript = { _, callback ->
            callback("[]")
        })

        assertFalse(vm.uiState.value.isTranslating)
        assertFalse(vm.uiState.value.isPageTranslated)
    }
}
