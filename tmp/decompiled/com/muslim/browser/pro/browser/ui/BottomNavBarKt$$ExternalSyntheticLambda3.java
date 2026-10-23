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
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.BottomNavBarKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BottomNavBarKt;", method="BottomNavBar$lambda$8$lambda$7$lambda$5", proto="(ZLcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BottomNavBarKt$$ExternalSyntheticLambda3
implements Function2 {
    public final boolean f$0;
    public final AppColors f$1;

    public /* synthetic */ BottomNavBarKt$$ExternalSyntheticLambda3(boolean bl, AppColors appColors) {
        this.f$0 = bl;
        this.f$1 = appColors;
    }

    public final Object invoke(Object object, Object object2) {
        return BottomNavBarKt.BottomNavBar$lambda$8$lambda$7$lambda$5(this.f$0, this.f$1, (Composer)object, (Integer)object2);
    }
}

