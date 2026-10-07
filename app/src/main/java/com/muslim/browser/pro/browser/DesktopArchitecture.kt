package com.muslim.browser.pro.browser

enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    STANDARD("Desktop Mode"),
    DESKTOP_MODE_4("Desktop Mode 4"),
    DESKTOP_MODE_11("Desktop Mode 11"),
    DESKTOP_MODE_12("Desktop Mode 12"),
    WINDOWS_10_TOUCH("Windows 10 Desktop"),
    WINDOWS_7("Windows 7");

    fun isAnyDesktop(): Boolean = this != NONE
    fun isStandard(): Boolean = this == STANDARD
    fun isMode4(): Boolean = this == DESKTOP_MODE_4
    fun isMode11(): Boolean = this == DESKTOP_MODE_11
    fun isMode12(): Boolean = this == DESKTOP_MODE_12
    fun isWindows10Touch(): Boolean = this == WINDOWS_10_TOUCH
    fun isWindows7(): Boolean = this == WINDOWS_7
}
