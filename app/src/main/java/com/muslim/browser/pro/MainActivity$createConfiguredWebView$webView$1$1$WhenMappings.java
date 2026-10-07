package com.muslim.browser.pro;

import com.muslim.browser.pro.browser.DesktopArchitecture;

public final class MainActivity$createConfiguredWebView$webView$1$1$WhenMappings {
    public static final int[] $EnumSwitchMapping$0;

    static {
        DesktopArchitecture[] values = DesktopArchitecture.values();
        int[] mapping = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            String name = values[i].name();
            switch (name) {
                case "WINDOWS_10_TOUCH":
                    mapping[i] = 1;
                    break;
                case "STANDARD":
                    mapping[i] = 2;
                    break;
                case "DESKTOP_MODE_4":
                case "WINDOWS_7":
                    mapping[i] = 3;
                    break;
                case "DESKTOP_MODE_11":
                case "DESKTOP_MODE_12":
                    mapping[i] = 2;
                    break;
                case "NONE":
                    mapping[i] = 4;
                    break;
                default:
                    mapping[i] = 2;
                    break;
            }
        }
        $EnumSwitchMapping$0 = mapping;
    }
}
