/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BrowserWebViewKt;", method="BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$12", proto="(Lcom/muslim/browser/pro/browser/BrowserUiState;Lcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserWebViewKt$$ExternalSyntheticLambda10
implements Function3 {
    public final BrowserUiState f$0;
    public final AppColors f$1;
    public final MutableState f$2;
    public final MutableState f$3;

    public /* synthetic */ BrowserWebViewKt$$ExternalSyntheticLambda10(BrowserUiState browserUiState, AppColors appColors, MutableState mutableState, MutableState mutableState2) {
        this.f$0 = browserUiState;
        this.f$1 = appColors;
        this.f$2 = mutableState;
        this.f$3 = mutableState2;
    }

    public final Object invoke(Object object, Object object2, Object object3) {
        return BrowserWebViewKt.BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$12(this.f$0, this.f$1, this.f$2, this.f$3, (Function2)object, (Composer)object2, (Integer)object3);
    }
}

