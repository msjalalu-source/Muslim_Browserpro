/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.foundation.text.KeyboardActionScope
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.foundation.text.KeyboardActionScope;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.HomePageKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/HomePageKt;", method="HomePage$lambda$76$lambda$54$lambda$42$lambda$41$lambda$32$lambda$31", proto="(Lcom/muslim/browser/pro/browser/BrowserUiState;Lkotlin/jvm/functions/Function1;Landroidx/compose/foundation/text/KeyboardActionScope;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class HomePageKt$$ExternalSyntheticLambda33
implements Function1 {
    public final BrowserUiState f$0;
    public final Function1 f$1;

    public /* synthetic */ HomePageKt$$ExternalSyntheticLambda33(BrowserUiState browserUiState, Function1 function1) {
        this.f$0 = browserUiState;
        this.f$1 = function1;
    }

    public final Object invoke(Object object) {
        return HomePageKt.HomePage$lambda$76$lambda$54$lambda$42$lambda$41$lambda$32$lambda$31(this.f$0, this.f$1, (KeyboardActionScope)object);
    }
}

