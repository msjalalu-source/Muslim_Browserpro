/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$51$lambda$50", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Landroid/webkit/WebView;Ljava/lang/String;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda30
implements Function1 {
    public final BrowserViewModel f$0;
    public final WebView f$1;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda30(BrowserViewModel browserViewModel, WebView webView2) {
        this.f$0 = browserViewModel;
        this.f$1 = webView2;
    }

    public final Object invoke(Object object) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$51$lambda$50(this.f$0, this.f$1, (String)object);
    }
}

