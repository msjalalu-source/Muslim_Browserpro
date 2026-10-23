/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BlockedInfo;
import com.muslim.browser.pro.browser.ui.BlockedScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BlockedScreenKt;", method="BlockedScreen$lambda$5$lambda$4", proto="(Lcom/muslim/browser/pro/ui/theme/AppColors;Lcom/muslim/browser/pro/browser/BlockedInfo;Lkotlin/jvm/functions/Function0;ZLkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BlockedScreenKt$$ExternalSyntheticLambda2
implements Function2 {
    public final AppColors f$0;
    public final BlockedInfo f$1;
    public final Function0 f$2;
    public final boolean f$3;
    public final Function0 f$4;

    public /* synthetic */ BlockedScreenKt$$ExternalSyntheticLambda2(AppColors appColors, BlockedInfo blockedInfo, Function0 function0, boolean bl, Function0 function02) {
        this.f$0 = appColors;
        this.f$1 = blockedInfo;
        this.f$2 = function0;
        this.f$3 = bl;
        this.f$4 = function02;
    }

    public final Object invoke(Object object, Object object2) {
        return BlockedScreenKt.BlockedScreen$lambda$5$lambda$4(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, (Composer)object, (Integer)object2);
    }
}

