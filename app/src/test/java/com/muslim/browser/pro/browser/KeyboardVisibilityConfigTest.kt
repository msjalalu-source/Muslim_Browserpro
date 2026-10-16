package com.muslim.browser.pro.browser

import android.view.WindowManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

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
}
