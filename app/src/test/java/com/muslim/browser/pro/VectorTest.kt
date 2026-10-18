
package com.muslim.browser.pro

import androidx.test.core.app.ApplicationProvider
import android.content.Context
import androidx.core.content.ContextCompat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class VectorTest {
    @Test
    fun testDuckDuckGoDrawable() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val drawable = ContextCompat.getDrawable(context, R.drawable.ic_search_duckduckgo)
        println("Drawable: $drawable")
    }
}
