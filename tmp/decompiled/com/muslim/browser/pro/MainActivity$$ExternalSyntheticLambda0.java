/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.view.View
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 */
package com.muslim.browser.pro;

import android.view.View;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;
import kotlin.jvm.functions.Function0;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity;", method="onCreate$lambda$34$lambda$13$lambda$12", proto="(Landroid/view/View;Z)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$$ExternalSyntheticLambda0
implements Function0 {
    public final View f$0;
    public final boolean f$1;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda0(View view, boolean bl) {
        this.f$0 = view;
        this.f$1 = bl;
    }

    public final Object invoke() {
        return MainActivity.onCreate$lambda$34$lambda$13$lambda$12(this.f$0, this.f$1);
    }
}

