package com.muslim.browser.pro.browser

enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    STANDARD("Desktop Mode"),
    DESKTOP_MODE_4("Desktop Mode 4"),
    WINDOWS_10_TOUCH("Windows 10 Desktop");

    fun isAnyDesktop(): Boolean = this != NONE
    fun isStandard(): Boolean = this == STANDARD
    fun isMode4(): Boolean = this == DESKTOP_MODE_4
    fun isWindows10Touch(): Boolean = this == WINDOWS_10_TOUCH
}
