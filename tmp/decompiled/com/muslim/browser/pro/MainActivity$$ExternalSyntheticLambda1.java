/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity;", method="onCreate$lambda$34$lambda$33", proto="(Lcom/muslim/browser/pro/MainActivity;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$$ExternalSyntheticLambda1
implements Function2 {
    public final MainActivity f$0;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda1(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivity.onCreate$lambda$34$lambda$33(this.f$0, (Composer)object, (Integer)object2);
    }
}

