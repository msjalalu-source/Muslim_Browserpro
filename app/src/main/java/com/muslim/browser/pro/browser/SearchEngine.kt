package com.muslim.browser.pro.browser

import androidx.annotation.DrawableRes
import com.muslim.browser.pro.R

/**
 * Exactly two supported search engines in Muslim Browser Pro:
 * 1. DuckDuckGo (Default for fresh installations)
 * 2. Google
 */
enum class SearchEngine(
    val engineName: String,
    @get:DrawableRes val iconRes: Int
) {
    DUCKDUCKGO("DuckDuckGo", R.drawable.ic_search_duckduckgo),
    GOOGLE("Google", R.drawable.ic_search_google)
}
