/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.ui.Modifier
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.ui.Modifier;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BlockedInfo;
import com.muslim.browser.pro.browser.ui.BlockedScreenKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BlockedScreenKt;", method="BlockedScreen$lambda$6", proto="(Lcom/muslim/browser/pro/browser/BlockedInfo;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;ZLandroidx/compose/ui/Modifier;IILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BlockedScreenKt$$ExternalSyntheticLambda3
implements Function2 {
    public final BlockedInfo f$0;
    public final Function0 f$1;
    public final Function0 f$2;
    public final boolean f$3;
    public final Modifier f$4;
    public final int f$5;
    public final int f$6;

    public /* synthetic */ BlockedScreenKt$$ExternalSyntheticLambda3(BlockedInfo blockedInfo, Function0 function0, Function0 function02, boolean bl, Modifier modifier, int n, int n2) {
        this.f$0 = blockedInfo;
        this.f$1 = function0;
        this.f$2 = function02;
        this.f$3 = bl;
        this.f$4 = modifier;
        this.f$5 = n;
        this.f$6 = n2;
    }

    public final Object invoke(Object object, Object object2) {
        return BlockedScreenKt.BlockedScreen$lambda$6(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, (Composer)object, (Integer)object2);
    }
}

