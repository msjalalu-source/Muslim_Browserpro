package com.muslim.browser.pro.browser

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class ShareManifestIntegrationTest {

    @Test
    fun testMainActivityHasRequiredShareAndDeepLinkIntentFilters() {
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
        var mainActivityElement: org.w3c.dom.Element? = null

        for (i in 0 until activities.length) {
            val element = activities.item(i) as org.w3c.dom.Element
            val name = element.getAttribute("android:name")
            if (name == "com.muslim.browser.pro.MainActivity" || name == ".MainActivity") {
                mainActivityElement = element
                break
            }
        }

        assertNotNull("MainActivity must be declared in AndroidManifest.xml", mainActivityElement)
        val act = mainActivityElement!!

        // Activity must be exported to receive intents from external apps
        assertEquals("true", act.getAttribute("android:exported"))

        // launchMode should be singleTask so share targets reuse the existing browser task
        assertEquals("singleTask", act.getAttribute("android:launchMode"))

        // Must preserve adjustResize from keyboard visibility requirement
        assertEquals("adjustResize", act.getAttribute("android:windowSoftInputMode"))

        // Inspect child intent filters
        val intentFilters = act.getElementsByTagName("intent-filter")
        assertTrue("MainActivity must have at least 3 intent filters", intentFilters.length >= 3)

        var hasLauncher = false
        var hasSendText = false
        var hasViewHttp = false
        var hasViewHttps = false

        for (i in 0 until intentFilters.length) {
            val filter = intentFilters.item(i) as org.w3c.dom.Element

            val actions = filter.getElementsByTagName("action")
            val categories = filter.getElementsByTagName("category")
            val dataNodes = filter.getElementsByTagName("data")

            val actionNames = (0 until actions.length).map {
                (actions.item(it) as org.w3c.dom.Element).getAttribute("android:name")
            }
            val categoryNames = (0 until categories.length).map {
                (categories.item(it) as org.w3c.dom.Element).getAttribute("android:name")
            }

            if (actionNames.contains("android.intent.action.MAIN") &&
                categoryNames.contains("android.intent.category.LAUNCHER")
            ) {
                hasLauncher = true
            }

            if (actionNames.contains("android.intent.action.SEND") &&
                categoryNames.contains("android.intent.category.DEFAULT")
            ) {
                val mimeTypes = (0 until dataNodes.length).map {
                    (dataNodes.item(it) as org.w3c.dom.Element).getAttribute("android:mimeType")
                }
                if (mimeTypes.contains("text/plain")) {
                    hasSendText = true
                }
            }

            if (actionNames.contains("android.intent.action.VIEW") &&
                categoryNames.contains("android.intent.category.DEFAULT") &&
                categoryNames.contains("android.intent.category.BROWSABLE")
            ) {
                val schemes = (0 until dataNodes.length).map {
                    (dataNodes.item(it) as org.w3c.dom.Element).getAttribute("android:scheme")
                }
                if (schemes.contains("http")) hasViewHttp = true
                if (schemes.contains("https")) hasViewHttps = true
            }
        }

        assertTrue("MainActivity must have MAIN / LAUNCHER intent filter", hasLauncher)
        assertTrue("MainActivity must have ACTION_SEND text/plain intent filter for Sharesheet", hasSendText)
        assertTrue("MainActivity must have ACTION_VIEW http intent filter", hasViewHttp)
        assertTrue("MainActivity must have ACTION_VIEW https intent filter", hasViewHttps)
    }
}
