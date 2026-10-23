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
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.HomePageKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/HomePageKt;", method="HomePage$lambda$76$lambda$60", proto="(Lkotlin/jvm/functions/Function2;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Lcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class HomePageKt$$ExternalSyntheticLambda21
implements Function2 {
    public final Function2 f$0;
    public final MutableState f$1;
    public final MutableState f$2;
    public final MutableState f$3;
    public final AppColors f$4;

    public /* synthetic */ HomePageKt$$ExternalSyntheticLambda21(Function2 function2, MutableState mutableState, MutableState mutableState2, MutableState mutableState3, AppColors appColors) {
        this.f$0 = function2;
        this.f$1 = mutableState;
        this.f$2 = mutableState2;
        this.f$3 = mutableState3;
        this.f$4 = appColors;
    }

    public final Object invoke(Object object, Object object2) {
        return HomePageKt.HomePage$lambda$76$lambda$60(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, (Composer)object, (Integer)object2);
    }
}

