package com.muslim.browser.pro.browser

/**
 * Supported online translation engines for Bengali translation in Muslim Browser Pro.
 * Only one engine is active at any time. No fallback is performed between engines.
 */
enum class TranslationEngine(val displayName: String) {
    LIBRE_TRANSLATE("LibreTranslate"),
    LINGVA("Lingva Translate"),
    MYMEMORY("MyMemory Translate")
}
