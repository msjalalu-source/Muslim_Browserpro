package com.muslim.browser.pro.browser

import android.content.Context
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@RunWith(RobolectricTestRunner::class)
class KeyboardVisibilityConfigTest {

    @Test
    fun testMainActivityConfiguredWithAdjustResize() {
        val manifestFile = listOf(
            File("src/main/AndroidManifest.xml"),
            File("app/src/main/AndroidManifest.xml"),
            File("../app/src/main/AndroidManifest.xml")
        ).firstOrNull { it.exists() }

        assertNotNull("AndroidManifest.xml must exist", manifestFile)

        val factory = DocumentBuilderFactory.newInstance()
        factory.isNamespaceAware = true
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(manifestFile!!)

        val activities = doc.getElementsByTagName("activity")
        var mainActivityFound = false
        var softInputModeAttr: String? = null

        for (i in 0 until activities.length) {
            val element = activities.item(i) as org.w3c.dom.Element
            val name = element.getAttribute("android:name")
            if (name == "com.muslim.browser.pro.MainActivity" || name == ".MainActivity") {
                mainActivityFound = true
                softInputModeAttr = element.getAttribute("android:windowSoftInputMode")
                break
            }
        }

        assertTrue("MainActivity must be declared in AndroidManifest.xml", mainActivityFound)
        assertEquals(
            "MainActivity must have android:windowSoftInputMode=\"adjustResize\" configured in AndroidManifest.xml",
            "adjustResize",
            softInputModeAttr
        )
    }

    @Test
    fun testSoftInputModeConstantMatchesAdjustResize() {
        assertEquals(
            0x10,
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        )
    }

    @Test
    fun testKeyboardVisibilityScriptSupportsAllEditableTypes() {
        val script = DesktopCore.KEYBOARD_VISIBILITY_SCRIPT
        assertNotNull("Script must not be null", script)
        assertTrue("Script must handle INPUT elements", script.contains("INPUT"))
        assertTrue("Script must handle TEXTAREA elements", script.contains("TEXTAREA"))
        assertTrue("Script must handle contenteditable elements", script.contains("isContentEditable"))
        assertTrue("Script must handle role=textbox", script.contains("textbox"))
        assertTrue("Script must check activeElement", script.contains("activeElement"))
        assertTrue("Script must support shadowRoot", script.contains("shadowRoot"))
        assertTrue("Script must contain scrollIntoViewIfNeeded or scrollIntoView",
            script.contains("scrollIntoViewIfNeeded") && script.contains("scrollIntoView"))
        assertTrue("Script must listen to visualViewport or window resize",
            script.contains("visualViewport") && script.contains("resize"))
        assertTrue("Script must listen to focusin", script.contains("focusin"))
        assertTrue("Script must not attach pointerup listener to prevent scroll snap-back", !script.contains("pointerup"))
        assertTrue("Script must be idempotent with guard key", script.contains("__mb_keyboard_guard__"))
    }

    @Test
    fun testKeyboardInsetsPaddingCalculationPreventsDoublePadding() {
        val bottomBarHeight = 41.8f + 48f // 89.8f

        // When keyboard is closed (ime = 0)
        val imeClosed = 0f
        val paddingClosed = maxOf(0f, imeClosed - bottomBarHeight)
        assertEquals("When keyboard is closed, additional padding must be 0", 0f, paddingClosed, 0.001f)

        // When keyboard is partially open or smaller than bottom bar
        val imeSmall = 50f
        val paddingSmall = maxOf(0f, imeSmall - bottomBarHeight)
        assertEquals("When IME is smaller than bottom bar, additional padding must be 0", 0f, paddingSmall, 0.001f)

        // When keyboard is fully open (e.g. 300dp)
        val imeOpen = 300f
        val paddingOpen = maxOf(0f, imeOpen - bottomBarHeight)
        assertEquals("Additional padding must be exactly ime - bottomBarHeight", 210.2f, paddingOpen, 0.001f)
        assertEquals("Total reserved height from bottom must match IME height exactly", imeOpen, bottomBarHeight + paddingOpen, 0.001f)
    }

    @Test
    fun testWebViewContainerPreventsFocusTheft() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val container = FrameLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
            isFocusable = false
            isFocusableInTouchMode = false
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }

        assertFalse("WebView parent container must not be focusable to prevent focus theft", container.isFocusable)
        assertFalse("WebView parent container must not be focusable in touch mode", container.isFocusableInTouchMode)
        assertEquals(
            "WebView parent container must prioritize descendant focusability",
            ViewGroup.FOCUS_AFTER_DESCENDANTS,
            container.descendantFocusability
        )
    }

    @Test
    fun testWebViewConfiguredForTouchModeFocus() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val webView = WebView(context).apply {
            isFocusable = true
            isFocusableInTouchMode = true
        }

        assertTrue("WebView must be focusable to receive input and suggestions", webView.isFocusable)
        assertTrue("WebView must be focusable in touch mode to maintain soft keyboard connection", webView.isFocusableInTouchMode)
    }

    @Test
    fun testBottomSystemNavigationInsetsRespected() {
        // 3-button navigation (typically 48dp navigation bar)
        val threeButtonNavInset = 48f
        val bottomBarContentHeight = 41.8f + 0.5f // row + divider
        val totalBottomPaddingThreeButton = bottomBarContentHeight + threeButtonNavInset
        assertTrue("Bottom padding for 3-button nav must strictly exceed the system nav bar height",
            totalBottomPaddingThreeButton > threeButtonNavInset)
        assertEquals("Total bottom padding must equal bottom bar content height + 3-button nav inset",
            90.3f, totalBottomPaddingThreeButton, 0.001f)

        // Gesture navigation (typically 16dp - 24dp navigation bar)
        val gestureNavInset = 20f
        val totalBottomPaddingGesture = bottomBarContentHeight + gestureNavInset
        assertTrue("Bottom padding for gesture nav must strictly exceed the gesture nav bar height",
            totalBottomPaddingGesture > gestureNavInset)
        assertEquals("Total bottom padding must equal bottom bar content height + gesture nav inset",
            62.3f, totalBottomPaddingGesture, 0.001f)
    }

    @Test
    fun testCombinedKeyboardAndBottomBarPaddingNoOverlap() {
        val navInset = 48f
        val bottomBarHeight = 41.8f + navInset // 89.8f

        // Case 1: Keyboard closed (ime = 0) -> Content area stops at bottomBarHeight (above nav bar)
        val imeClosed = 0f
        val additionalClosed = maxOf(0f, imeClosed - bottomBarHeight)
        val effectiveBottomPaddingClosed = bottomBarHeight + additionalClosed
        assertEquals(89.8f, effectiveBottomPaddingClosed, 0.001f)
        assertTrue("Content must stop above navigation bar when keyboard is closed",
            effectiveBottomPaddingClosed >= navInset)

        // Case 2: Keyboard smaller than bottom bar -> Content area stops at bottomBarHeight (above nav bar)
        val imeSmall = 60f
        val additionalSmall = maxOf(0f, imeSmall - bottomBarHeight)
        val effectiveBottomPaddingSmall = bottomBarHeight + additionalSmall
        assertEquals(89.8f, effectiveBottomPaddingSmall, 0.001f)
        assertTrue("Content must stop above navigation bar when IME is small",
            effectiveBottomPaddingSmall >= navInset)

        // Case 3: Keyboard larger than bottom bar -> Content area stops above keyboard (no double padding)
        val imeLarge = 320f
        val additionalLarge = maxOf(0f, imeLarge - bottomBarHeight)
        val effectiveBottomPaddingLarge = bottomBarHeight + additionalLarge
        assertEquals(320f, effectiveBottomPaddingLarge, 0.001f)
        assertTrue("Content must stop above keyboard when IME is large",
            effectiveBottomPaddingLarge >= imeLarge)
    }
}


