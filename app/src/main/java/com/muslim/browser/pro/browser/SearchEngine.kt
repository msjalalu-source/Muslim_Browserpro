package com.muslim.browser.pro.browser

import com.muslim.browser.pro.R
import java.net.URLEncoder

enum class SearchEngine(val engineName: String, val iconRes: Int) {
    GOOGLE("Google", R.drawable.ic_search_google),
    DUCKDUCKGO("DuckDuckGo", R.drawable.ic_search_duckduckgo);

    fun buildSearchUrl(query: String): String {
        val encoded = URLEncoder.encode(query.trim(), "UTF-8")
        return when (this) {
            GOOGLE -> "https://www.google.com/search?q=$encoded"
            DUCKDUCKGO -> "https://duckduckgo.com/?q=$encoded"
        }
    }
}
