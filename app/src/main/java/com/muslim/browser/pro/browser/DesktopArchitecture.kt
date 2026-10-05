package com.muslim.browser.pro.browser

/**
 * Defines the mutually exclusive desktop identity architectures supported by Muslim Browser Pro:
 *
 * 1. [NONE] - Standard mobile identity and viewport.
 * 2. [STANDARD] - Reference Desktop Mode: Full dual-MutationObserver guard, Turbo/PJAX events,
 *    history monkey-patching, Client Hints override, desktop UA, and native WebSettings.
 * 3. [DESKTOP_MODE_1] - Conservative Simplification: Single head-only MutationObserver,
 *    Turbo/PJAX/navigation event listeners, Client Hints override, desktop UA, and native WebSettings.
 *    (Eliminates document-level root observer and history monkey-patching).
 * 4. [DESKTOP_MODE_2] - Balanced Simplification: Zero MutationObservers, zero history monkey-patching,
 *    zero client hints override script. Pure event-driven viewport enforcement on Turbo/PJAX/navigation
 *    events, desktop UA, and native WebSettings.
 * 5. [DESKTOP_MODE_3] - Maximum Simplification: Zero JavaScript injection. Pure native WebView
 *    configuration (desktop UA, useWideViewPort, loadWithOverviewMode, zoom settings).
 * 6. [WINDOWS_10_TOUCH] - Windows 10 desktop identity with touch profile (10 touch points, D3D11 GPU).
 */
enum class DesktopArchitecture(val displayName: String) {
    NONE("Mobile"),
    STANDARD("Desktop Mode"),
    DESKTOP_MODE_1("Desktop Mode 1"),
    DESKTOP_MODE_2("Desktop Mode 2"),
    DESKTOP_MODE_3("Desktop Mode 3"),
    WINDOWS_10_TOUCH("Windows 10 Desktop"),
    WINDOWS_7("Windows 7");

    val isAnyDesktop: Boolean
        get() = this != NONE

    val isStandard: Boolean
        get() = this == STANDARD

    val isMode1: Boolean
        get() = this == DESKTOP_MODE_1

    val isMode2: Boolean
        get() = this == DESKTOP_MODE_2

    val isMode3: Boolean
        get() = this == DESKTOP_MODE_3

    val isWindows10Touch: Boolean
        get() = this == WINDOWS_10_TOUCH

    val isWindows7: Boolean
        get() = this == WINDOWS_7
}
