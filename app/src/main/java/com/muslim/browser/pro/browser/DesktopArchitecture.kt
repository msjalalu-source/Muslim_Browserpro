package com.muslim.browser.pro.browser

enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    STANDARD("Desktop Mode"),
    WINDOWS_10_TOUCH("Windows 10 Desktop");

    fun isAnyDesktop(): Boolean = this != NONE
    fun isStandard(): Boolean = this == STANDARD
    fun isWindows10Touch(): Boolean = this == WINDOWS_10_TOUCH
}
