/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  androidx.compose.runtime.State
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import androidx.compose.runtime.State;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$19$lambda$18", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Lkotlin/jvm/functions/Function1;Landroid/webkit/WebView;Landroidx/compose/runtime/State;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda42
implements Function0 {
    public final BrowserViewModel f$0;
    public final Function1 f$1;
    public final WebView f$2;
    public final State f$3;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda42(BrowserViewModel browserViewModel, Function1 function1, WebView webView2, State state) {
        this.f$0 = browserViewModel;
        this.f$1 = function1;
        this.f$2 = webView2;
        this.f$3 = state;
    }

    public final Object invoke() {
        return MainActivityKt.BrowserApp$lambda$19$lambda$18(this.f$0, this.f$1, this.f$2, this.f$3);
    }
}

