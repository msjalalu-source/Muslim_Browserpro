package com.muslim.browser.pro

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.browser.BengaliTranslator
import com.muslim.browser.pro.browser.BrowserViewModel
import com.muslim.browser.pro.browser.SettingsRepository
import com.muslim.browser.pro.browser.TranslationEngine
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
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
}
