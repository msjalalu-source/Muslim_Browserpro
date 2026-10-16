package com.muslim.browser.pro.browser

import android.content.ComponentName
import android.content.Context
import android.content.pm.PackageManager
import android.view.WindowManager
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import com.muslim.browser.pro.MainActivity

@RunWith(RobolectricTestRunner::class)
@Config(packageName = "com.muslim.browser.pro")
class KeyboardVisibilityConfigTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext<Context>()
    }

    @Test
    fun testMainActivityConfiguredWithAdjustResize() {
        val componentName = ComponentName(context, MainActivity::class.java)
        val activityInfo = context.packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)

        val adjustMode = activityInfo.softInputMode and WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST
        assertEquals(
            "MainActivity must have SOFT_INPUT_ADJUST_RESIZE configured in AndroidManifest.xml",
            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE,
            adjustMode
        )
    }
}
