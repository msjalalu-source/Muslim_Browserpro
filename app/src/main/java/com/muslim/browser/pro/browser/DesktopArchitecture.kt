package com.muslim.browser.pro.browser

/**
 * Defines the mutually exclusive desktop identity architectures supported by Muslim Browser Pro:
 *
 * 1. [NONE] - Standard mobile identity and viewport.
 * 2. [STANDARD] - Reference Desktop Mode: Full dual-MutationObserver guard, Turbo/PJAX events,
 *    history monkey-patching, Client Hints override, desktop UA, and native WebSettings.
 * 3. [DESKTOP_MODE_4] - Targeted Guard: Single targeted viewport observer (observes only the viewport
 *    meta tag and head direct child additions for viewport meta, ignoring unrelated head elements),
 *    Turbo/PJAX/navigation event listeners, Client Hints override, desktop UA, and native WebSettings.
 * 4. [WINDOWS_10_TOUCH] - Windows 10 desktop identity with touch profile (10 touch points, D3D11 GPU).
 */
enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    STANDARD("Desktop Mode"),
    DESKTOP_MODE_4("Desktop Mode 4"),
    WINDOWS_10_TOUCH("Windows 10 Desktop");

    val isAnyDesktop: Boolean
        get() = this != NONE

    val isStandard: Boolean
        get() = this == STANDARD

    val isMode4: Boolean
        get() = this == DESKTOP_MODE_4

    val isWindows10Touch: Boolean
        get() = this == WINDOWS_10_TOUCH
}
