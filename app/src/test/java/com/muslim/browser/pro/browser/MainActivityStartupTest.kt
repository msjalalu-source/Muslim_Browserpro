package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.muslim.browser.pro.MainActivity
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MainActivityStartupTest {

    @Test
    fun testStartupWithDefaultPreferences() {
        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun testStartupWithLegacyStandardPreference() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("key_desktop_architecture", "STANDARD").commit()

        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }

    @Test
    fun testStartupWithNullArchitecturePreference() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val prefs = context.getSharedPreferences("focus_shield_prefs", Context.MODE_PRIVATE)
        prefs.edit().remove("key_desktop_architecture").putBoolean("key_desktop_mode", true).commit()

        val controller = Robolectric.buildActivity(MainActivity::class.java).setup()
        val activity = controller.get()
        assertNotNull(activity)
    }
}
