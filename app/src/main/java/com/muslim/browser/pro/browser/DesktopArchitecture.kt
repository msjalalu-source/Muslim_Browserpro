package com.muslim.browser.pro.browser

enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    // অব্যবহৃত, মুছো না। jar এর জন্য রাখা
    STANDARD("Desktop Mode"),
    // অব্যবহৃত, মুছো না। jar এর জন্য রাখা
    DESKTOP_MODE_4("Desktop Mode 4"),
    WINDOWS_10_TOUCH("Windows 10 Desktop");

    fun isAnyDesktop(): Boolean = this == WINDOWS_10_TOUCH
    fun isWindows10Touch(): Boolean = this == WINDOWS_10_TOUCH
}
