/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import kotlin.jvm.functions.Function0;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$35$lambda$23$lambda$22", proto="(Landroid/webkit/WebView;Lcom/muslim/browser/pro/browser/BrowserViewModel;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda51
implements Function0 {
    public final WebView f$0;
    public final BrowserViewModel f$1;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda51(WebView webView2, BrowserViewModel browserViewModel) {
        this.f$0 = webView2;
        this.f$1 = browserViewModel;
    }

    public final Object invoke() {
        return MainActivityKt.BrowserApp$lambda$35$lambda$23$lambda$22(this.f$0, this.f$1);
    }
}

