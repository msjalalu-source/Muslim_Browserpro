# ProGuard rules for Muslim Browser Pro

# Preserve JavascriptInterface methods for WebViews
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
-keep class com.muslim.browser.pro.browser.BlobDownloadBridge { *; }

# Preserve JSON parsing classes and models
-keep class org.json.** { *; }

# Preserve Enums (e.g. AppTheme, DesktopArchitecture, SearchEngine, etc.)
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Jetpack Compose and Kotlin coroutines/reflection rules
-dontwarn androidx.compose.**
-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod
