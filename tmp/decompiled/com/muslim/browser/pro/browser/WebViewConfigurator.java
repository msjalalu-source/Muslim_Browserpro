/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.net.Uri
 *  android.os.Build$VERSION
 *  android.webkit.CookieManager
 *  android.webkit.WebSettings
 *  android.webkit.WebView
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.browser.DesktopCore
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 */
package com.muslim.browser.pro.browser;

import android.net.Uri;
import android.os.Build;
import android.webkit.CookieManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DesktopCore;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0005\n\u0002\u0010\u000b\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\r\n\u0002\u0018\u0002\n\u0002\b\n\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u00012B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0016\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000bJ\u0010\u0010\u0013\u001a\u00020\u000b2\b\u0010\u0014\u001a\u0004\u0018\u00010\u0005J\u0018\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u00182\u0006\u0010\u0012\u001a\u00020\u000bJ\u001e\u0010\u0019\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u000b2\u0006\u0010\u0012\u001a\u00020\u000bJ\u0016\u0010%\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010&\u001a\u00020\u000bJ\u0016\u0010'\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010(\u001a\u00020)J*\u0010*\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u0006\u0010(\u001a\u00020)2\b\b\u0002\u0010+\u001a\u00020\u000bJ4\u0010,\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\b\u0010\u0014\u001a\u0004\u0018\u00010\u00052\u0006\u0010\u0011\u001a\u00020\u000b2\b\b\u0002\u0010+\u001a\u00020\u000b2\b\b\u0002\u0010\u0012\u001a\u00020\u000bJ \u0010-\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010\u0011\u001a\u00020\u000b2\b\b\u0002\u0010\u0012\u001a\u00020\u000bJ6\u0010.\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010/\u001a\u00020\u000b2\b\b\u0002\u0010\u0011\u001a\u00020\u000b2\b\b\u0002\u0010\u0012\u001a\u00020\u000b2\n\b\u0002\u00100\u001a\u0004\u0018\u00010)J\u0016\u00101\u001a\u00020\u00162\u0006\u0010\u0017\u001a\u00020\u00182\u0006\u0010/\u001a\u00020\u000bR\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0007\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u000bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\n\u0010\f\"\u0004\b\r\u0010\u000eR\u000e\u0010\u001a\u001a\u00020\u001bX\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u001c\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u001d\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\tR\u0011\u0010\u001f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010\tR\u0011\u0010!\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010\tR\u0011\u0010#\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b$\u0010\t\u00a8\u00063"}, d2={"Lcom/muslim/browser/pro/browser/WebViewConfigurator;", "", "<init>", "()V", "DESKTOP_USER_AGENT", "", "WINDOWS_10_TOUCH_USER_AGENT", "WINDOWS_10_TOUCH_INJECTION_SCRIPT", "getWINDOWS_10_TOUCH_INJECTION_SCRIPT", "()Ljava/lang/String;", "isDarkThemeActive", "", "()Z", "setDarkThemeActive", "(Z)V", "getActiveIdentityMode", "Lcom/muslim/browser/pro/browser/WebViewConfigurator$BrowserIdentityMode;", "isDesktopEnabled", "isWindows10TouchEnabled", "isAuthenticationUrl", "url", "injectWindows10TouchProfileIfEnabled", "", "webView", "Landroid/webkit/WebView;", "applyIdentityMode", "DESKTOP_VIEWPORT_TARGET_WIDTH", "", "DESKTOP_VIEWPORT_CONTENT", "DESKTOP_VIEWPORT_GUARD_SCRIPT", "getDESKTOP_VIEWPORT_GUARD_SCRIPT", "DESKTOP_VIEWPORT_CLEANUP_SCRIPT", "getDESKTOP_VIEWPORT_CLEANUP_SCRIPT", "DESKTOP_MODE_4_GUARD_SCRIPT", "getDESKTOP_MODE_4_GUARD_SCRIPT", "CLEANUP_ALL_DESKTOP_SCRIPTS", "getCLEANUP_ALL_DESKTOP_SCRIPTS", "applyDesktopViewport", "isDesktopOrTouchEnabled", "applyArchitectureViewport", "architecture", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "syncDesktopArchitecture", "updateUserAgent", "syncDesktopMode", "applyDesktopMode", "configureBaseSettings", "isDarkTheme", "desktopArchitecture", "applyWebViewTheme", "BrowserIdentityMode", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class WebViewConfigurator {
    public static final int $stable;
    private static final String CLEANUP_ALL_DESKTOP_SCRIPTS;
    private static final String DESKTOP_MODE_4_GUARD_SCRIPT;
    public static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static final String DESKTOP_VIEWPORT_CLEANUP_SCRIPT;
    public static final String DESKTOP_VIEWPORT_CONTENT = "width=1280";
    private static final String DESKTOP_VIEWPORT_GUARD_SCRIPT;
    public static final int DESKTOP_VIEWPORT_TARGET_WIDTH = 1280;
    public static final WebViewConfigurator INSTANCE;
    private static final String WINDOWS_10_TOUCH_INJECTION_SCRIPT;
    public static final String WINDOWS_10_TOUCH_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static volatile boolean isDarkThemeActive;

    static {
        INSTANCE = new WebViewConfigurator();
        WINDOWS_10_TOUCH_INJECTION_SCRIPT = "(function() {\n    // 1. Navigator hardware & platform signals (Windows 10 Desktop/Tablet)\n    try {\n        var navProto = Object.getPrototypeOf(navigator) || (window.Navigator && window.Navigator.prototype);\n        var navProps = {\n            platform: { get: function() { return 'Win32'; }, configurable: true },\n            vendor: { get: function() { return 'Google Inc.'; }, configurable: true },\n            userAgent: { get: function() { return 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'; }, configurable: true },\n            appVersion: { get: function() { return '5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36'; }, configurable: true },\n            maxTouchPoints: { get: function() { return 10; }, configurable: true },\n            hardwareConcurrency: { get: function() { return 8; }, configurable: true },\n            deviceMemory: { get: function() { return 8; }, configurable: true }\n        };\n        for (var key in navProps) {\n            try { Object.defineProperty(navigator, key, navProps[key]); } catch(e) {}\n            if (navProto) {\n                try { Object.defineProperty(navProto, key, navProps[key]); } catch(e) {}\n            }\n            if (window.Navigator && window.Navigator.prototype) {\n                try { Object.defineProperty(window.Navigator.prototype, key, navProps[key]); } catch(e) {}\n            }\n        }\n    } catch(e) {}\n\n    // 2. User-Agent Client Hints (navigator.userAgentData: platform 'Windows', mobile false)\n    try {\n        var uaData = {\n            brands: [\n                { brand: 'Google Chrome', version: '131' },\n                { brand: 'Chromium', version: '131' },\n                { brand: 'Not_A Brand', version: '24' }\n            ],\n            mobile: false,\n            platform: 'Windows',\n            getHighEntropyValues: function(hints) {\n                return Promise.resolve({\n                    architecture: 'x86',\n                    bitness: '64',\n                    brands: [\n                        { brand: 'Google Chrome', version: '131' },\n                        { brand: 'Chromium', version: '131' },\n                        { brand: 'Not_A Brand', version: '24' }\n                    ],\n                    fullVersionList: [\n                        { brand: 'Google Chrome', version: '131.0.6778.205' },\n                        { brand: 'Chromium', version: '131.0.6778.205' },\n                        { brand: 'Not_A Brand', version: '24.0.0.0' }\n                    ],\n                    mobile: false,\n                    model: '',\n                    platform: 'Windows',\n                    platformVersion: '10.0.0'\n                });\n            },\n            toJSON: function() {\n                return { brands: this.brands, mobile: this.mobile, platform: this.platform };\n            }\n        };\n        var uadDescriptor = { get: function() { return uaData; }, configurable: true };\n        try { Object.defineProperty(navigator, 'userAgentData', uadDescriptor); } catch(e) {}\n        if (navProto) {\n            try { Object.defineProperty(navProto, 'userAgentData', uadDescriptor); } catch(e) {}\n        }\n        if (window.Navigator && window.Navigator.prototype) {\n            try { Object.defineProperty(window.Navigator.prototype, 'userAgentData', uadDescriptor); } catch(e) {}\n        }\n    } catch(e) {}\n\n    // 3. Screen Dimensions (Desktop-class Windows 10: 1920x1080)\n    try {\n        var screenProto = Object.getPrototypeOf(window.screen) || (window.Screen && window.Screen.prototype);\n        var screenProps = {\n            width: { get: function() { return 1920; }, configurable: true },\n            height: { get: function() { return 1080; }, configurable: true },\n            availWidth: { get: function() { return 1920; }, configurable: true },\n            availHeight: { get: function() { return 1040; }, configurable: true },\n            colorDepth: { get: function() { return 24; }, configurable: true },\n            pixelDepth: { get: function() { return 24; }, configurable: true }\n        };\n        for (var sKey in screenProps) {\n            try { Object.defineProperty(window.screen, sKey, screenProps[sKey]); } catch(e) {}\n            if (screenProto) {\n                try { Object.defineProperty(screenProto, sKey, screenProps[sKey]); } catch(e) {}\n            }\n            if (window.Screen && window.Screen.prototype) {\n                try { Object.defineProperty(window.Screen.prototype, sKey, screenProps[sKey]); } catch(e) {}\n            }\n        }\n    } catch(e) {}\n\n    // 4. Window Viewport & Outer Dimensions (Desktop-consistent: inner/outer width & height, document client dimensions)\n    try {\n        var desktopInnerW = 1280;\n        var desktopInnerH = 720;\n        var desktopOuterW = 1280;\n        var desktopOuterH = 800; // 720 + 80px browser chrome\n\n        var winProto = Object.getPrototypeOf(window) || (window.Window && window.Window.prototype);\n        var winProps = {\n            innerWidth: { get: function() { return desktopInnerW; }, configurable: true },\n            innerHeight: { get: function() { return desktopInnerH; }, configurable: true },\n            outerWidth: { get: function() { return desktopOuterW; }, configurable: true },\n            outerHeight: { get: function() { return desktopOuterH; }, configurable: true }\n        };\n        for (var wKey in winProps) {\n            try { Object.defineProperty(window, wKey, winProps[wKey]); } catch(e) {}\n            if (winProto) {\n                try { Object.defineProperty(winProto, wKey, winProps[wKey]); } catch(e) {}\n            }\n            if (window.Window && window.Window.prototype) {\n                try { Object.defineProperty(window.Window.prototype, wKey, winProps[wKey]); } catch(e) {}\n            }\n        }\n\n        var elemProto = window.Element && window.Element.prototype;\n        if (elemProto) {\n            var origClientHeightDesc = Object.getOwnPropertyDescriptor(elemProto, 'clientHeight');\n            var origClientHeightGet = origClientHeightDesc ? origClientHeightDesc.get : null;\n            Object.defineProperty(elemProto, 'clientHeight', {\n                get: function() {\n                    if (this === document.documentElement || (document.compatMode === 'BackCompat' && this === document.body)) {\n                        return desktopInnerH;\n                    }\n                    return origClientHeightGet ? origClientHeightGet.apply(this, arguments) : 0;\n                },\n                configurable: true\n            });\n\n            var origClientWidthDesc = Object.getOwnPropertyDescriptor(elemProto, 'clientWidth');\n            var origClientWidthGet = origClientWidthDesc ? origClientWidthDesc.get : null;\n            Object.defineProperty(elemProto, 'clientWidth', {\n                get: function() {\n                    if (this === document.documentElement || (document.compatMode === 'BackCompat' && this === document.body)) {\n                        return desktopInnerW;\n                    }\n                    return origClientWidthGet ? origClientWidthGet.apply(this, arguments) : 0;\n                },\n                configurable: true\n            });\n        }\n\n        if (document.documentElement) {\n            try {\n                Object.defineProperty(document.documentElement, 'clientWidth', {\n                    get: function() { return desktopInnerW; },\n                    configurable: true\n                });\n                Object.defineProperty(document.documentElement, 'clientHeight', {\n                    get: function() { return desktopInnerH; },\n                    configurable: true\n                });\n            } catch(e) {}\n        }\n    } catch(e) {}\n\n    // 5. WebGL GPU / Unmasked Renderer Signals (Intel Direct3D11)\n    try {\n        var UNMASKED_VENDOR_WEBGL = 0x9245;\n        var UNMASKED_RENDERER_WEBGL = 0x9246;\n        var targetVendor = 'Google Inc. (Intel)';\n        var targetRenderer = 'ANGLE (Intel, Intel(R) UHD Graphics 620 Direct3D11 vs_5_0 ps_5_0, D3D11)';\n\n        function patchContext(proto) {\n            if (!proto || !proto.getParameter) return;\n            var origGet = proto.getParameter;\n            proto.getParameter = function(param) {\n                if (param === UNMASKED_VENDOR_WEBGL) return targetVendor;\n                if (param === UNMASKED_RENDERER_WEBGL) return targetRenderer;\n                return origGet.apply(this, arguments);\n            };\n        }\n        if (window.WebGLRenderingContext) patchContext(WebGLRenderingContext.prototype);\n        if (window.WebGL2RenderingContext) patchContext(WebGL2RenderingContext.prototype);\n    } catch(e) {}\n\n    // 6. CSS Media Queries for Windows 10 Touch (fine pointer + coarse touch + hover)\n    try {\n        if (window.matchMedia) {\n            var origMm = window.matchMedia;\n            window.matchMedia = function(q) {\n                var res = origMm.apply(this, arguments);\n                if (q.indexOf('(hover: hover)') !== -1 || q.indexOf('(any-hover: hover)') !== -1 ||\n                    q.indexOf('(pointer: fine)') !== -1 || q.indexOf('(any-pointer: fine)') !== -1 ||\n                    q.indexOf('(any-pointer: coarse)') !== -1) {\n                    return {\n                        matches: true,\n                        media: q,\n                        onchange: null,\n                        addListener: function() {},\n                        removeListener: function() {},\n                        addEventListener: function() {},\n                        removeEventListener: function() {},\n                        dispatchEvent: function() { return false; }\n                    };\n                }\n                return res;\n            };\n        }\n    } catch(e) {}\n})();";
        isDarkThemeActive = true;
        DESKTOP_VIEWPORT_GUARD_SCRIPT = "(function() {\n    var TARGET_CONTENT = 'width=1280';\n    var GUARD_KEY = '__mb_desktop_guard__';\n\n    function applyViewport() {\n        try {\n            var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;\n            if (!head) return;\n            var meta = document.querySelector('meta[name=\"viewport\"]');\n            if (!meta) {\n                meta = document.createElement('meta');\n                meta.name = 'viewport';\n                meta.setAttribute('data-mb-created', 'true');\n                head.appendChild(meta);\n            }\n            if (meta.getAttribute('content') !== TARGET_CONTENT) {\n                if (meta.getAttribute('data-mb-orig') === null && !meta.hasAttribute('data-mb-created')) {\n                    meta.setAttribute('data-mb-orig', meta.getAttribute('content') || '');\n                }\n                meta.setAttribute('content', TARGET_CONTENT);\n            }\n        } catch(e) {}\n    }\n\n    function patchDesktopClientHints() {\n        try {\n            if (navigator.userAgentData) {\n                var origUaData = navigator.userAgentData;\n                var fakeUaData = {\n                    brands: origUaData.brands || [\n                        { brand: 'Google Chrome', version: '131' },\n                        { brand: 'Chromium', version: '131' },\n                        { brand: 'Not_A Brand', version: '24' }\n                    ],\n                    mobile: false,\n                    platform: 'Linux',\n                    getHighEntropyValues: function(hints) {\n                        return origUaData.getHighEntropyValues ?\n                            origUaData.getHighEntropyValues(hints).then(function(vals) {\n                                vals.mobile = false;\n                                vals.platform = 'Linux';\n                                return vals;\n                            }) :\n                            Promise.resolve({ mobile: false, platform: 'Linux' });\n                    },\n                    toJSON: function() {\n                        return { brands: this.brands, mobile: false, platform: 'Linux' };\n                    }\n                };\n                Object.defineProperty(navigator, 'userAgentData', {\n                    get: function() { return fakeUaData; },\n                    configurable: true\n                });\n            }\n        } catch(e) {}\n        try {\n            Object.defineProperty(navigator, 'platform', {\n                get: function() { return 'Linux x86_64'; },\n                configurable: true\n            });\n        } catch(e) {}\n    }\n\n    if (window[GUARD_KEY]) {\n        window[GUARD_KEY].ensureViewport();\n        return;\n    }\n\n    applyViewport();\n    patchDesktopClientHints();\n\n    var headObserver = null;\n    var docObserver = null;\n    try {\n        headObserver = new MutationObserver(function(mutations) {\n            for (var i = 0; i < mutations.length; i++) {\n                var m = mutations[i];\n                if (m.type === 'childList') {\n                    applyViewport();\n                    break;\n                } else if (m.type === 'attributes' && m.attributeName === 'content') {\n                    if (m.target && m.target.name === 'viewport' && m.target.getAttribute('content') !== TARGET_CONTENT) {\n                        applyViewport();\n                        break;\n                    }\n                }\n            }\n        });\n        if (document.head) {\n            headObserver.observe(document.head, {\n                childList: true,\n                subtree: true,\n                attributes: true,\n                attributeFilter: ['content']\n            });\n        }\n\n        docObserver = new MutationObserver(function(mutations) {\n            for (var i = 0; i < mutations.length; i++) {\n                if (mutations[i].type === 'childList') {\n                    applyViewport();\n                    if (document.head && headObserver) {\n                        try {\n                            headObserver.disconnect();\n                            headObserver.observe(document.head, {\n                                childList: true,\n                                subtree: true,\n                                attributes: true,\n                                attributeFilter: ['content']\n                            });\n                        } catch(e) {}\n                    }\n                    break;\n                }\n            }\n        });\n        docObserver.observe(document.documentElement, {\n            childList: true,\n            subtree: false\n        });\n    } catch(e) {}\n\n    var navEvents = ['turbo:load', 'turbo:render', 'pjax:end', 'pageshow', 'popstate'];\n    function onNav() {\n        applyViewport();\n    }\n    navEvents.forEach(function(evt) {\n        window.addEventListener(evt, onNav, { passive: true });\n    });\n\n    var origPush = history.pushState;\n    if (origPush) {\n        history.pushState = function() {\n            var res = origPush.apply(this, arguments);\n            applyViewport();\n            return res;\n        };\n    }\n    var origReplace = history.replaceState;\n    if (origReplace) {\n        history.replaceState = function() {\n            var res = origReplace.apply(this, arguments);\n            applyViewport();\n            return res;\n        };\n    }\n\n    window[GUARD_KEY] = {\n        ensureViewport: applyViewport,\n        cleanup: function() {\n            try {\n                if (headObserver) headObserver.disconnect();\n                if (docObserver) docObserver.disconnect();\n                navEvents.forEach(function(evt) {\n                    window.removeEventListener(evt, onNav);\n                });\n                if (origPush) history.pushState = origPush;\n                if (origReplace) history.replaceState = origReplace;\n\n                var meta = document.querySelector('meta[name=\"viewport\"]');\n                if (meta) {\n                    if (meta.hasAttribute('data-mb-created')) {\n                        meta.remove();\n                    } else if (meta.hasAttribute('data-mb-orig')) {\n                        var orig = meta.getAttribute('data-mb-orig');\n                        if (orig) {\n                            meta.setAttribute('content', orig);\n                        } else {\n                            meta.removeAttribute('content');\n                        }\n                        meta.removeAttribute('data-mb-orig');\n                    }\n                }\n            } catch(e) {}\n            delete window[GUARD_KEY];\n        }\n    };\n})();";
        DESKTOP_VIEWPORT_CLEANUP_SCRIPT = "(function() {\n    var GUARD_KEY = '__mb_desktop_guard__';\n    if (window[GUARD_KEY] && typeof window[GUARD_KEY].cleanup === 'function') {\n        window[GUARD_KEY].cleanup();\n    } else {\n        try {\n            var meta = document.querySelector('meta[name=\"viewport\"]');\n            if (meta) {\n                if (meta.hasAttribute('data-mb-created')) {\n                    meta.remove();\n                } else if (meta.hasAttribute('data-mb-orig')) {\n                    var orig = meta.getAttribute('data-mb-orig');\n                    if (orig) {\n                        meta.setAttribute('content', orig);\n                    } else {\n                        meta.removeAttribute('content');\n                    }\n                    meta.removeAttribute('data-mb-orig');\n                }\n            }\n        } catch(e) {}\n    }\n})();";
        DESKTOP_MODE_4_GUARD_SCRIPT = "(function() {\n    var TARGET_CONTENT = 'width=1280';\n    var GUARD_KEY = '__mb_desktop_mode4__';\n\n    var metaObserver = null;\n    var headObserver = null;\n\n    function attachMetaObserver(metaEl) {\n        if (!metaEl || !window.MutationObserver) return;\n        if (metaObserver) {\n            try { metaObserver.disconnect(); } catch(e) {}\n        }\n        metaObserver = new MutationObserver(function(mutations) {\n            for (var i = 0; i < mutations.length; i++) {\n                if (mutations[i].attributeName === 'content') {\n                    if (metaEl.getAttribute('content') !== TARGET_CONTENT) {\n                        applyViewport();\n                    }\n                    break;\n                }\n            }\n        });\n        try {\n            metaObserver.observe(metaEl, {\n                attributes: true,\n                attributeFilter: ['content']\n            });\n        } catch(e) {}\n    }\n\n    function applyViewport() {\n        try {\n            var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;\n            if (!head) return;\n            var meta = document.querySelector('meta[name=\"viewport\"]');\n            if (!meta) {\n                meta = document.createElement('meta');\n                meta.name = 'viewport';\n                meta.setAttribute('data-mb4-created', 'true');\n                head.appendChild(meta);\n            }\n            if (meta.getAttribute('content') !== TARGET_CONTENT) {\n                if (meta.getAttribute('data-mb4-orig') === null && !meta.hasAttribute('data-mb4-created')) {\n                    meta.setAttribute('data-mb4-orig', meta.getAttribute('content') || '');\n                }\n                meta.setAttribute('content', TARGET_CONTENT);\n            }\n            attachMetaObserver(meta);\n        } catch(e) {}\n    }\n\n    function patchDesktopClientHints() {\n        try {\n            if (navigator.userAgentData) {\n                var origUaData = navigator.userAgentData;\n                var fakeUaData = {\n                    brands: origUaData.brands || [\n                        { brand: 'Google Chrome', version: '131' },\n                        { brand: 'Chromium', version: '131' },\n                        { brand: 'Not_A Brand', version: '24' }\n                    ],\n                    mobile: false,\n                    platform: 'Linux',\n                    getHighEntropyValues: function(hints) {\n                        return origUaData.getHighEntropyValues ?\n                            origUaData.getHighEntropyValues(hints).then(function(vals) {\n                                vals.mobile = false;\n                                vals.platform = 'Linux';\n                                return vals;\n                            }) :\n                            Promise.resolve({ mobile: false, platform: 'Linux' });\n                    },\n                    toJSON: function() {\n                        return { brands: this.brands, mobile: false, platform: 'Linux' };\n                    }\n                };\n                Object.defineProperty(navigator, 'userAgentData', {\n                    get: function() { return fakeUaData; },\n                    configurable: true\n                });\n            }\n        } catch(e) {}\n        try {\n            Object.defineProperty(navigator, 'platform', {\n                get: function() { return 'Linux x86_64'; },\n                configurable: true\n            });\n        } catch(e) {}\n    }\n\n    if (window[GUARD_KEY]) {\n        window[GUARD_KEY].ensureViewport();\n        return;\n    }\n\n    applyViewport();\n    patchDesktopClientHints();\n\n    try {\n        var head = document.head || document.getElementsByTagName('head')[0] || document.documentElement;\n        if (head && window.MutationObserver) {\n            headObserver = new MutationObserver(function(mutations) {\n                for (var i = 0; i < mutations.length; i++) {\n                    var m = mutations[i];\n                    if (m.type === 'childList') {\n                        var changed = false;\n                        for (var j = 0; j < m.addedNodes.length; j++) {\n                            var node = m.addedNodes[j];\n                            if (node.nodeType === 1 && node.nodeName === 'META' && (node.name === 'viewport' || node.getAttribute('name') === 'viewport')) {\n                                changed = true;\n                                break;\n                            }\n                        }\n                        if (!changed) {\n                            for (var k = 0; k < m.removedNodes.length; k++) {\n                                var rNode = m.removedNodes[k];\n                                if (rNode.nodeType === 1 && rNode.nodeName === 'META' && (rNode.name === 'viewport' || rNode.getAttribute('name') === 'viewport')) {\n                                    changed = true;\n                                    break;\n                                }\n                            }\n                        }\n                        if (changed) {\n                            applyViewport();\n                            break;\n                        }\n                    }\n                }\n            });\n            headObserver.observe(head, {\n                childList: true,\n                subtree: false\n            });\n        }\n    } catch(e) {}\n\n    var navEvents = ['turbo:load', 'turbo:render', 'pjax:end', 'pageshow', 'popstate'];\n    function onNav() { applyViewport(); }\n    navEvents.forEach(function(evt) {\n        window.addEventListener(evt, onNav, { passive: true });\n    });\n\n    window[GUARD_KEY] = {\n        ensureViewport: applyViewport,\n        cleanup: function() {\n            try {\n                if (headObserver) headObserver.disconnect();\n                if (metaObserver) metaObserver.disconnect();\n                navEvents.forEach(function(evt) {\n                    window.removeEventListener(evt, onNav);\n                });\n                var meta = document.querySelector('meta[name=\"viewport\"]');\n                if (meta) {\n                    if (meta.hasAttribute('data-mb4-created')) {\n                        meta.remove();\n                    } else if (meta.hasAttribute('data-mb4-orig')) {\n                        var orig = meta.getAttribute('data-mb4-orig');\n                        if (orig) meta.setAttribute('content', orig);\n                        else meta.removeAttribute('content');\n                        meta.removeAttribute('data-mb4-orig');\n                    }\n                }\n            } catch(e) {}\n            delete window[GUARD_KEY];\n        }\n    };\n})();";
        CLEANUP_ALL_DESKTOP_SCRIPTS = "(function() {\n    var keys = ['__mb_desktop_guard__', '__mb_desktop_mode4__'];\n    for (var i = 0; i < keys.length; i++) {\n        var k = keys[i];\n        if (window[k] && typeof window[k].cleanup === 'function') {\n            try { window[k].cleanup(); } catch(e) {}\n        }\n    }\n    try {\n        var meta = document.querySelector('meta[name=\"viewport\"]');\n        if (meta) {\n            if (meta.hasAttribute('data-mb-created') || meta.hasAttribute('data-mb4-created')) {\n                meta.remove();\n            } else if (meta.hasAttribute('data-mb-orig') || meta.hasAttribute('data-mb4-orig')) {\n                var orig = meta.getAttribute('data-mb-orig') || meta.getAttribute('data-mb4-orig');\n                if (orig) meta.setAttribute('content', orig);\n                else meta.removeAttribute('content');\n                meta.removeAttribute('data-mb-orig');\n                meta.removeAttribute('data-mb4-orig');\n            }\n        }\n    } catch(e) {}\n})();";
        $stable = 8;
    }

    private WebViewConfigurator() {
    }

    public static /* synthetic */ void applyDesktopMode$default(WebViewConfigurator webViewConfigurator, WebView webView2, boolean bl, boolean bl2, int n, Object object) {
        if ((n & 4) != 0) {
            bl2 = false;
        }
        webViewConfigurator.applyDesktopMode(webView2, bl, bl2);
    }

    public static /* synthetic */ void configureBaseSettings$default(WebViewConfigurator webViewConfigurator, WebView webView2, boolean bl, boolean bl2, boolean bl3, DesktopArchitecture desktopArchitecture, int n, Object object) {
        if ((n & 4) != 0) {
            bl2 = false;
        }
        if ((n & 8) != 0) {
            bl3 = false;
        }
        if ((n & 0x10) != 0) {
            desktopArchitecture = null;
        }
        webViewConfigurator.configureBaseSettings(webView2, bl, bl2, bl3, desktopArchitecture);
    }

    public static /* synthetic */ void syncDesktopArchitecture$default(WebViewConfigurator webViewConfigurator, WebView webView2, String string, DesktopArchitecture desktopArchitecture, boolean bl, int n, Object object) {
        if ((n & 8) != 0) {
            bl = true;
        }
        webViewConfigurator.syncDesktopArchitecture(webView2, string, desktopArchitecture, bl);
    }

    public static /* synthetic */ void syncDesktopMode$default(WebViewConfigurator webViewConfigurator, WebView webView2, String string, boolean bl, boolean bl2, boolean bl3, int n, Object object) {
        if ((n & 8) != 0) {
            bl2 = true;
        }
        if ((n & 0x10) != 0) {
            bl3 = false;
        }
        webViewConfigurator.syncDesktopMode(webView2, string, bl, bl2, bl3);
    }

    public final void applyArchitectureViewport(WebView webView2, DesktopArchitecture desktopArchitecture) {
        block7: {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"architecture");
            try {
                switch (WhenMappings.$EnumSwitchMapping$1[desktopArchitecture.ordinal()]) {
                    default: {
                        break;
                    }
                    case 4: {
                        DesktopCore.INSTANCE.cleanupCommonDesktopState(webView2);
                        break block7;
                    }
                    case 3: {
                        webView2.evaluateJavascript(DESKTOP_MODE_4_GUARD_SCRIPT, null);
                        break block7;
                    }
                    case 1: 
                    case 2: {
                        DesktopCore.INSTANCE.applyCommonDesktopViewport(webView2);
                        break block7;
                    }
                }
                webView2 = new NoWhenBranchMatchedException();
                throw webView2;
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
    }

    public final void applyDesktopMode(WebView webView2, boolean bl, boolean bl2) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        this.applyIdentityMode(webView2, bl, bl2);
    }

    /*
     * Unable to fully structure code
     */
    public final void applyDesktopViewport(WebView var1_1, boolean var2_3) {
        Intrinsics.checkNotNullParameter((Object)var1_1, (String)"webView");
        if (!var2_3) ** GOTO lbl6
        try {
            block2: {
                var3_4 = WebViewConfigurator.DESKTOP_VIEWPORT_GUARD_SCRIPT;
                break block2;
lbl6:
                // 1 sources

                var3_4 = WebViewConfigurator.CLEANUP_ALL_DESKTOP_SCRIPTS;
            }
            var1_1.evaluateJavascript(var3_4, null);
        }
        catch (Throwable var1_2) {
            // empty catch block
        }
    }

    public final void applyIdentityMode(WebView webView2, boolean bl, boolean bl2) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        BrowserIdentityMode browserIdentityMode = this.getActiveIdentityMode(bl, bl2);
        String string = webView2.getUrl();
        DesktopCore.INSTANCE.applyCommonDesktopWebViewSettings(webView2);
        int n = WhenMappings.$EnumSwitchMapping$0[browserIdentityMode.ordinal()];
        String string2 = null;
        switch (n) {
            default: {
                throw new NoWhenBranchMatchedException();
            }
            case 3: {
                break;
            }
            case 2: {
                if (this.isAuthenticationUrl(string)) break;
                string2 = DESKTOP_USER_AGENT;
                break;
            }
            case 1: {
                if (this.isAuthenticationUrl(string)) break;
                string2 = WINDOWS_10_TOUCH_USER_AGENT;
            }
        }
        if (!Intrinsics.areEqual((Object)webView2.getSettings().getUserAgentString(), string2)) {
            webView2.getSettings().setUserAgentString(string2);
        }
        switch (WhenMappings.$EnumSwitchMapping$0[browserIdentityMode.ordinal()]) {
            default: {
                throw new NoWhenBranchMatchedException();
            }
            case 3: {
                DesktopCore.INSTANCE.cleanupCommonDesktopState(webView2);
                break;
            }
            case 2: {
                DesktopCore.INSTANCE.applyCommonDesktopViewport(webView2);
                break;
            }
            case 1: {
                this.injectWindows10TouchProfileIfEnabled(webView2, true);
                DesktopCore.INSTANCE.applyCommonDesktopViewport(webView2);
            }
        }
    }

    public final void applyWebViewTheme(WebView webView2, boolean bl) {
        int n;
        block5: {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            isDarkThemeActive = bl;
            if (Build.VERSION.SDK_INT >= 33) {
                webView2.getSettings().setAlgorithmicDarkeningAllowed(bl);
            }
            if (Build.VERSION.SDK_INT < 29) break block5;
            WebSettings webSettings = webView2.getSettings();
            n = bl ? 2 : 0;
            webSettings.setForceDark(n);
        }
        n = bl ? -16777216 : -1;
        try {
            webView2.setBackgroundColor(n);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    public final void configureBaseSettings(WebView webView2, boolean bl, boolean bl2, boolean bl3, DesktopArchitecture desktopArchitecture) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        cookieManager.setAcceptThirdPartyCookies(webView2, true);
        cookieManager = webView2.getSettings();
        cookieManager.setJavaScriptEnabled(true);
        cookieManager.setDomStorageEnabled(true);
        cookieManager.setDatabaseEnabled(true);
        cookieManager.setCacheMode(-1);
        cookieManager.setSupportMultipleWindows(true);
        cookieManager.setLoadWithOverviewMode(true);
        cookieManager.setUseWideViewPort(true);
        cookieManager.setBuiltInZoomControls(true);
        cookieManager.setDisplayZoomControls(false);
        cookieManager.setTextZoom(100);
        cookieManager.setMixedContentMode(2);
        cookieManager.setOffscreenPreRaster(false);
        cookieManager.setMediaPlaybackRequiresUserGesture(true);
        cookieManager.setSaveFormData(false);
        if (desktopArchitecture == null) {
            desktopArchitecture = bl3 ? DesktopArchitecture.WINDOWS_10_TOUCH : (bl2 ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE);
        }
        this.syncDesktopArchitecture(webView2, webView2.getUrl(), desktopArchitecture, true);
        this.applyWebViewTheme(webView2, bl);
    }

    public final BrowserIdentityMode getActiveIdentityMode(boolean bl, boolean bl2) {
        BrowserIdentityMode browserIdentityMode = bl2 ? BrowserIdentityMode.WINDOWS_10_TOUCH : (bl ? BrowserIdentityMode.DESKTOP_LINUX : BrowserIdentityMode.MOBILE);
        return browserIdentityMode;
    }

    public final String getCLEANUP_ALL_DESKTOP_SCRIPTS() {
        return CLEANUP_ALL_DESKTOP_SCRIPTS;
    }

    public final String getDESKTOP_MODE_4_GUARD_SCRIPT() {
        return DESKTOP_MODE_4_GUARD_SCRIPT;
    }

    public final String getDESKTOP_VIEWPORT_CLEANUP_SCRIPT() {
        return DESKTOP_VIEWPORT_CLEANUP_SCRIPT;
    }

    public final String getDESKTOP_VIEWPORT_GUARD_SCRIPT() {
        return DESKTOP_VIEWPORT_GUARD_SCRIPT;
    }

    public final String getWINDOWS_10_TOUCH_INJECTION_SCRIPT() {
        return WINDOWS_10_TOUCH_INJECTION_SCRIPT;
    }

    public final void injectWindows10TouchProfileIfEnabled(WebView webView2, boolean bl) {
        if (webView2 != null && bl) {
            try {
                webView2.evaluateJavascript(WINDOWS_10_TOUCH_INJECTION_SCRIPT, null);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
            return;
        }
    }

    public final boolean isAuthenticationUrl(String object) {
        block7: {
            block8: {
                block9: {
                    block11: {
                        block10: {
                            Object object2 = (CharSequence)object;
                            boolean bl = object2 == null || StringsKt.isBlank((CharSequence)object2);
                            if (bl) {
                                return false;
                            }
                            try {
                                object = Uri.parse((String)object);
                            }
                            catch (Exception exception) {
                                return false;
                            }
                            String string = object.getHost();
                            if (string == null) break block7;
                            object2 = Locale.ROOT;
                            Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"ROOT");
                            object2 = string.toLowerCase((Locale)object2);
                            Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"toLowerCase(...)");
                            if (object2 == null) break block7;
                            if (Intrinsics.areEqual((Object)object2, (Object)"github.com") || StringsKt.endsWith$default((String)object2, (String)".github.com", (boolean)false, (int)2, null)) break block8;
                            if (Intrinsics.areEqual((Object)object2, (Object)"accounts.google.com") || StringsKt.endsWith$default((String)object2, (String)".accounts.google.com", (boolean)false, (int)2, null) || Intrinsics.areEqual((Object)object2, (Object)"appleid.apple.com") || Intrinsics.areEqual((Object)object2, (Object)"login.microsoftonline.com") || Intrinsics.areEqual((Object)object2, (Object)"auth.account.sony.com") || StringsKt.startsWith$default((String)object2, (String)"auth.", (boolean)false, (int)2, null) || StringsKt.startsWith$default((String)object2, (String)"id.", (boolean)false, (int)2, null) || StringsKt.startsWith$default((String)object2, (String)"login.", (boolean)false, (int)2, null)) break block9;
                            object2 = object.getPath();
                            if (object2 == null) break block10;
                            object = Locale.ROOT;
                            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"ROOT");
                            object2 = ((String)object2).toLowerCase((Locale)object);
                            Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"toLowerCase(...)");
                            object = object2;
                            if (object2 != null) break block11;
                        }
                        object = "";
                    }
                    return StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)"/oauth2/", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)"/oauth/", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)"/openid-connect/", (boolean)false, (int)2, null) || StringsKt.endsWith$default((String)object, (String)"/authorize", (boolean)false, (int)2, null);
                    {
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }

    public final boolean isDarkThemeActive() {
        return isDarkThemeActive;
    }

    public final void setDarkThemeActive(boolean bl) {
        isDarkThemeActive = bl;
    }

    public final void syncDesktopArchitecture(WebView webView2, String string, DesktopArchitecture desktopArchitecture, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"architecture");
        DesktopCore.INSTANCE.synchronizeDesktopWebView(webView2, string, desktopArchitecture, bl);
    }

    public final void syncDesktopMode(WebView webView2, String string, boolean bl, boolean bl2, boolean bl3) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        DesktopArchitecture desktopArchitecture = bl3 ? DesktopArchitecture.WINDOWS_10_TOUCH : (bl ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE);
        this.syncDesktopArchitecture(webView2, string, desktopArchitecture, bl2);
    }

    @Metadata(d1={"\u0000\f\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0002\b\u0006\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003j\u0002\b\u0004j\u0002\b\u0005j\u0002\b\u0006\u00a8\u0006\u0007"}, d2={"Lcom/muslim/browser/pro/browser/WebViewConfigurator$BrowserIdentityMode;", "", "<init>", "(Ljava/lang/String;I)V", "MOBILE", "DESKTOP_LINUX", "WINDOWS_10_TOUCH", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static enum BrowserIdentityMode {
        MOBILE,
        DESKTOP_LINUX,
        WINDOWS_10_TOUCH;

        private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);

        public static EnumEntries<BrowserIdentityMode> getEntries() {
            return $ENTRIES;
        }
    }

    @Metadata(k=3, mv={2, 2, 0}, xi=48)
    public static final class WhenMappings {
        public static final int[] $EnumSwitchMapping$0;
        public static final int[] $EnumSwitchMapping$1;

        static {
            int[] nArray = new int[BrowserIdentityMode.values().length];
            try {
                nArray[BrowserIdentityMode.WINDOWS_10_TOUCH.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowserIdentityMode.DESKTOP_LINUX.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[BrowserIdentityMode.MOBILE.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
            nArray = new int[DesktopArchitecture.values().length];
            try {
                nArray[DesktopArchitecture.STANDARD.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DesktopArchitecture.WINDOWS_10_TOUCH.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DesktopArchitecture.DESKTOP_MODE_4.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DesktopArchitecture.NONE.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$1 = nArray;
        }
    }
}

