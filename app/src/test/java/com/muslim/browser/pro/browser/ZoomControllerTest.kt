package com.muslim.browser.pro.browser

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class ZoomControllerTest {

    private lateinit var context: Context
    private lateinit var app: Application
    private lateinit var repository: SettingsRepository

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
        app = ApplicationProvider.getApplicationContext<Application>()
        repository = SettingsRepository(context)
        repository.setDefaultZoomLevel(ZoomController.DEFAULT_ZOOM)
    }

    @Test
    fun test1_constantsAndBounds() {
        assertEquals(50, ZoomController.MIN_ZOOM)
        assertEquals(300, ZoomController.MAX_ZOOM)
        assertEquals(100, ZoomController.DEFAULT_ZOOM)
        assertEquals(10, ZoomController.STEP_ZOOM)
    }

    @Test
    fun test2_calculateFocalScroll_zoomInFromOrigin() {
        // Zooming in from 100% to 200% centered at cursor (200f, 300f) with initial scroll (0, 0)
        // newScrollX = (0 + 200) * 2 - 200 = 200
        // newScrollY = (0 + 300) * 2 - 300 = 300
        val (newScrollX, newScrollY) = ZoomController.calculateFocalScroll(
            scrollX = 0,
            scrollY = 0,
            focalX = 200f,
            focalY = 300f,
            oldZoomPercent = 100,
            newZoomPercent = 200
        )
        assertEquals(200, newScrollX)
        assertEquals(300, newScrollY)
    }

    @Test
    fun test3_calculateFocalScroll_zoomInWithExistingScroll() {
        // oldScroll (100, 150), cursor at (50f, 50f), zoom 100% -> 150% (ratio 1.5)
        // newScrollX = (100 + 50) * 1.5 - 50 = 225 - 50 = 175
        // newScrollY = (150 + 50) * 1.5 - 50 = 300 - 50 = 250
        val (newScrollX, newScrollY) = ZoomController.calculateFocalScroll(
            scrollX = 100,
            scrollY = 150,
            focalX = 50f,
            focalY = 50f,
            oldZoomPercent = 100,
            newZoomPercent = 150
        )
        assertEquals(175, newScrollX)
        assertEquals(250, newScrollY)
    }

    @Test
    fun test4_calculateFocalScroll_zoomOutCompensatesCorrectly() {
        // Zooming out from 200% to 100% (ratio 0.5) centered at (200f, 300f) with scroll (200, 300)
        // newScrollX = (200 + 200) * 0.5 - 200 = 0
        // newScrollY = (300 + 300) * 0.5 - 300 = 0
        val (newScrollX, newScrollY) = ZoomController.calculateFocalScroll(
            scrollX = 200,
            scrollY = 300,
            focalX = 200f,
            focalY = 300f,
            oldZoomPercent = 200,
            newZoomPercent = 100
        )
        assertEquals(0, newScrollX)
        assertEquals(0, newScrollY)
    }

    @Test
    fun test5_calculateFocalScroll_neverNegative() {
        // Zooming out where formula might yield negative numbers
        val (newScrollX, newScrollY) = ZoomController.calculateFocalScroll(
            scrollX = 10,
            scrollY = 10,
            focalX = 100f,
            focalY = 100f,
            oldZoomPercent = 200,
            newZoomPercent = 100
        )
        assertTrue(newScrollX >= 0)
        assertTrue(newScrollY >= 0)
    }

    @Test
    fun test6_calculateFocalScroll_sameZoomNoChange() {
        val (newScrollX, newScrollY) = ZoomController.calculateFocalScroll(
            scrollX = 120,
            scrollY = 340,
            focalX = 80f,
            focalY = 90f,
            oldZoomPercent = 125,
            newZoomPercent = 125
        )
        assertEquals(120, newScrollX)
        assertEquals(340, newScrollY)
    }

    @Test
    fun test7_buildZoomScript_contentAndSanity() {
        val script = ZoomController.buildZoomScript(150)
        assertTrue(script.contains("document.documentElement.style.zoom"))
        assertTrue(script.contains("targetPercent = 150"))
        assertTrue(script.contains("window.scrollTo"))
        assertTrue(script.contains("__mb_last_mouse_x"))
        assertTrue(script.contains("__mb_last_touch_x"))
        // Must NOT use destructive CSS transform scale
        assertFalse(script.contains("transform: scale"))
        assertFalse(script.contains("transformOrigin"))
    }

    @Test
    fun test8_buildZoomScript_clampInScript() {
        val scriptMin = ZoomController.buildZoomScript(10) // below 50
        assertTrue(scriptMin.contains("targetPercent = 50"))

        val scriptMax = ZoomController.buildZoomScript(500) // above 300
        assertTrue(scriptMax.contains("targetPercent = 300"))
    }

    @Test
    fun test9_trackerScript_monitorsMouseAndTouch() {
        val tracker = ZoomController.TRACKER_SCRIPT
        assertTrue(tracker.contains("mousemove"))
        assertTrue(tracker.contains("touchstart"))
        assertTrue(tracker.contains("touchmove"))
        assertTrue(tracker.contains("__mb_last_mouse_x"))
        assertTrue(tracker.contains("__mb_last_touch_x"))
    }

    @Test
    fun test10_defaultZoomLevelPersistence() {
        assertEquals(100, repository.getDefaultZoomLevel())

        repository.setDefaultZoomLevel(175)
        assertEquals(175, repository.getDefaultZoomLevel())

        // Clamping check on persistence
        repository.setDefaultZoomLevel(20)
        assertEquals(50, repository.getDefaultZoomLevel())

        repository.setDefaultZoomLevel(999)
        assertEquals(300, repository.getDefaultZoomLevel())
    }

    @Test
    fun test11_viewModelZoomInteractions() {
        val vm = BrowserViewModel(app)
        assertEquals(100, vm.uiState.value.currentZoomLevel)
        assertEquals(100, vm.uiState.value.defaultZoomLevel)
        assertFalse(vm.uiState.value.isZoomPanelOpen)

        // Open & close panel
        vm.openZoomPanel()
        assertTrue(vm.uiState.value.isZoomPanelOpen)
        assertFalse(vm.uiState.value.isMenuOpen)

        vm.closeZoomPanel()
        assertFalse(vm.uiState.value.isZoomPanelOpen)

        // Zoom In & Out
        vm.zoomIn(null)
        assertEquals(110, vm.uiState.value.currentZoomLevel)

        vm.zoomIn(null)
        assertEquals(120, vm.uiState.value.currentZoomLevel)

        vm.zoomOut(null)
        assertEquals(110, vm.uiState.value.currentZoomLevel)

        // Reset
        vm.resetZoom(null)
        assertEquals(100, vm.uiState.value.currentZoomLevel)

        // Save as default
        vm.setZoomLevel(150, null)
        vm.saveAsDefaultZoom(150)
        assertEquals(150, vm.uiState.value.defaultZoomLevel)
        assertEquals(150, repository.getDefaultZoomLevel())

        // New Tab inherits default zoom level
        val newTab = vm.openNewTab("https://example.com")
        assertEquals(150, vm.uiState.value.currentZoomLevel)
    }

    @Test
    fun test12_desktopWidthIndependence() {
        // Ensure DesktopCore constant and viewport logic are completely unaffected
        assertEquals("width=1280", DesktopCore.VIEWPORT_CONTENT)
        assertEquals(1280, DesktopCore.TARGET_WIDTH)

        // Zoom script operates strictly on CSS documentElement.style.zoom and does not modify meta viewport
        val zoomScript = ZoomController.buildZoomScript(125)
        assertFalse(zoomScript.contains("meta[name='viewport']"))
        assertFalse(zoomScript.contains("meta[name=\"viewport\"]"))
        assertFalse(zoomScript.contains("width=1280"))
    }
}
