package com.muslim.browser.pro.browser;

public final class WebViewConfigurator$WhenMappings {
    public static final int[] $EnumSwitchMapping$0;
    public static final int[] $EnumSwitchMapping$1;

    static {
        // Mapping for BrowserIdentityMode
        int[] m0;
        try {
            Class<?> identityModeClass = Class.forName("com.muslim.browser.pro.browser.WebViewConfigurator$BrowserIdentityMode");
            Object[] identityValues = (Object[]) identityModeClass.getMethod("values").invoke(null);
            int[] mapping0 = new int[identityValues.length];
            for (int i = 0; i < identityValues.length; i++) {
                String name = ((Enum<?>) identityValues[i]).name();
                switch (name) {
                    case "WINDOWS_10_TOUCH":
                        mapping0[i] = 1;
                        break;
                    case "DESKTOP_LINUX":
                        mapping0[i] = 2;
                        break;
                    case "MOBILE":
                        mapping0[i] = 3;
                        break;
                    default:
                        mapping0[i] = 3;
                        break;
                }
            }
            m0 = mapping0;
        } catch (Throwable t) {
            m0 = new int[4];
        }
        $EnumSwitchMapping$0 = m0;

        // Mapping for DesktopArchitecture
        DesktopArchitecture[] values = DesktopArchitecture.values();
        int[] mapping1 = new int[values.length];
        for (int i = 0; i < values.length; i++) {
            String name = values[i].name();
            switch (name) {
                case "STANDARD":
                    mapping1[i] = 1;
                    break;
                case "WINDOWS_10_TOUCH":
                    mapping1[i] = 2;
                    break;
                case "DESKTOP_MODE_4":
                    mapping1[i] = 3;
                    break;
                case "NONE":
                    mapping1[i] = 4;
                    break;
                case "DESKTOP_MODE_11":
                case "DESKTOP_MODE_12":
                    mapping1[i] = 1;
                    break;
                default:
                    mapping1[i] = 1;
                    break;
            }
        }
        $EnumSwitchMapping$1 = mapping1;
    }
}
