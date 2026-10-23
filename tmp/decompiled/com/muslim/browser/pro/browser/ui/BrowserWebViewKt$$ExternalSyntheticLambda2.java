/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  android.widget.FrameLayout
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser.ui;

import android.webkit.WebView;
import android.widget.FrameLayout;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BrowserWebViewKt;", method="BrowserWebView$lambda$32$lambda$31$lambda$28$lambda$27", proto="(Lcom/muslim/browser/pro/browser/BrowserUiState;Landroid/webkit/WebView;Landroid/widget/FrameLayout;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserWebViewKt$$ExternalSyntheticLambda2
implements Function1 {
    public final BrowserUiState f$0;
    public final WebView f$1;

    public /* synthetic */ BrowserWebViewKt$$ExternalSyntheticLambda2(BrowserUiState browserUiState, WebView webView2) {
        this.f$0 = browserUiState;
        this.f$1 = webView2;
    }

    public final Object invoke(Object object) {
        return BrowserWebViewKt.BrowserWebView$lambda$32$lambda$31$lambda$28$lambda$27(this.f$0, this.f$1, (FrameLayout)object);
    }
}

