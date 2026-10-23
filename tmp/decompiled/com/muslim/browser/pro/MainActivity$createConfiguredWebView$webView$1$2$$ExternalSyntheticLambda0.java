/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity$createConfiguredWebView$webView$1$2;", method="onCreateWindow$forwardToParent$lambda$1", proto="(Landroid/webkit/WebView;Landroid/webkit/WebView;Ljava/lang/String;)V")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$createConfiguredWebView$webView$1$2$$ExternalSyntheticLambda0
implements Runnable {
    public final WebView f$0;
    public final WebView f$1;
    public final String f$2;

    public /* synthetic */ MainActivity$createConfiguredWebView$webView$1$2$$ExternalSyntheticLambda0(WebView webView2, WebView webView3, String string) {
        this.f$0 = webView2;
        this.f$1 = webView3;
        this.f$2 = string;
    }

    @Override
    public final void run() {
        MainActivity.createConfiguredWebView.webView.1.2.onCreateWindow$forwardToParent$lambda$1(this.f$0, this.f$1, this.f$2);
    }
}

