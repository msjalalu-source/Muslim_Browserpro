/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BrowserWebViewKt;", method="BrowserWebView$lambda$32$lambda$23", proto="(Lcom/muslim/browser/pro/browser/BrowserUiState;Lcom/muslim/browser/pro/ui/theme/AppColors;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserWebViewKt$$ExternalSyntheticLambda0
implements Function2 {
    public final BrowserUiState f$0;
    public final AppColors f$1;
    public final Function0 f$2;
    public final Function1 f$3;
    public final MutableState f$4;
    public final MutableState f$5;

    public /* synthetic */ BrowserWebViewKt$$ExternalSyntheticLambda0(BrowserUiState browserUiState, AppColors appColors, Function0 function0, Function1 function1, MutableState mutableState, MutableState mutableState2) {
        this.f$0 = browserUiState;
        this.f$1 = appColors;
        this.f$2 = function0;
        this.f$3 = function1;
        this.f$4 = mutableState;
        this.f$5 = mutableState2;
    }

    public final Object invoke(Object object, Object object2) {
        return BrowserWebViewKt.BrowserWebView$lambda$32$lambda$23(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, (Composer)object, (Integer)object2);
    }
}

