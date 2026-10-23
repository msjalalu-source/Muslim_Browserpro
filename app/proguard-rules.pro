# ProGuard rules for Muslim Browser Pro

# Preserve JavascriptInterface methods for WebViews
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Preserve JSON parsing classes and models
-keep class org.json.** { *; }

# Keep data models serialized/deserialized or used across state
-keep class com.muslim.browser.pro.browser.** { *; }
-keep class com.muslim.browser.pro.ui.theme.** { *; }
-keep class com.muslim.browser.pro.data.** { *; }

# Preserve Enums (e.g. AppTheme, DesktopArchitecture, SearchEngine, etc.)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Jetpack Compose and Kotlin coroutines/reflection rules
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
