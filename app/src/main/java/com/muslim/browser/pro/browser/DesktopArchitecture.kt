package com.muslim.browser.pro.browser

enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    // অব্যবহৃত, মুছো না
    STANDARD("Desktop Mode"),
    // অব্যবহৃত, মুছো না
    DESKTOP_MODE_4("Desktop Mode 4"),
    WINDOWS_10_TOUCH("Windows 10 Desktop");

    fun isAnyDesktop(): Boolean = this == WINDOWS_10_TOUCH || this == DESKTOP_MODE_4
    fun isMode4(): Boolean = this == DESKTOP_MODE_4
    fun isStandard(): Boolean = this == STANDARD
    fun isWindows10Touch(): Boolean = this == WINDOWS_10_TOUCH
}
