/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$111$lambda$108", proto="(Lcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda25
implements Function2 {
    public final AppColors f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda25(AppColors appColors) {
        this.f$0 = appColors;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$111$lambda$108(this.f$0, (Composer)object, (Integer)object2);
    }
}

