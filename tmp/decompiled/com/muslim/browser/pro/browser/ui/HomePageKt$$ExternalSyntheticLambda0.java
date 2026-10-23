/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.graphics.Bitmap
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import android.graphics.Bitmap;
import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.ui.HomePageKt;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/HomePageKt;", method="FavoriteSiteItem$lambda$81$lambda$80", proto="(Landroid/graphics/Bitmap;Lcom/muslim/browser/pro/browser/FavoriteSite;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class HomePageKt$$ExternalSyntheticLambda0
implements Function2 {
    public final Bitmap f$0;
    public final FavoriteSite f$1;

    public /* synthetic */ HomePageKt$$ExternalSyntheticLambda0(Bitmap bitmap, FavoriteSite favoriteSite) {
        this.f$0 = bitmap;
        this.f$1 = favoriteSite;
    }

    public final Object invoke(Object object, Object object2) {
        return HomePageKt.FavoriteSiteItem$lambda$81$lambda$80(this.f$0, this.f$1, (Composer)object, (Integer)object2);
    }
}

