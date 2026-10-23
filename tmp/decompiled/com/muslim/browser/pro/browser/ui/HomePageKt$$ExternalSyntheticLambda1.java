/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.ui.HomePageKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/HomePageKt;", method="FavoriteSiteItem$lambda$82", proto="(Lcom/muslim/browser/pro/browser/FavoriteSite;Lkotlin/jvm/functions/Function0;ILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class HomePageKt$$ExternalSyntheticLambda1
implements Function2 {
    public final FavoriteSite f$0;
    public final Function0 f$1;
    public final int f$2;

    public /* synthetic */ HomePageKt$$ExternalSyntheticLambda1(FavoriteSite favoriteSite, Function0 function0, int n) {
        this.f$0 = favoriteSite;
        this.f$1 = function0;
        this.f$2 = n;
    }

    public final Object invoke(Object object, Object object2) {
        return HomePageKt.FavoriteSiteItem$lambda$82(this.f$0, this.f$1, this.f$2, (Composer)object, (Integer)object2);
    }
}

