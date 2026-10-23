/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import com.muslim.browser.pro.browser.TabWebViewManager;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$114", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Lcom/muslim/browser/pro/browser/TabWebViewManager;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;IIILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda36
implements Function2 {
    public final BrowserViewModel f$0;
    public final TabWebViewManager f$1;
    public final Function1 f$10;
    public final int f$11;
    public final int f$12;
    public final int f$13;
    public final Function0 f$2;
    public final Function0 f$3;
    public final Function1 f$4;
    public final Function1 f$5;
    public final Function1 f$6;
    public final Function1 f$7;
    public final Function1 f$8;
    public final Function1 f$9;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda36(BrowserViewModel browserViewModel, TabWebViewManager tabWebViewManager, Function0 function0, Function0 function02, Function1 function1, Function1 function12, Function1 function13, Function1 function14, Function1 function15, Function1 function16, Function1 function17, int n, int n2, int n3) {
        this.f$0 = browserViewModel;
        this.f$1 = tabWebViewManager;
        this.f$2 = function0;
        this.f$3 = function02;
        this.f$4 = function1;
        this.f$5 = function12;
        this.f$6 = function13;
        this.f$7 = function14;
        this.f$8 = function15;
        this.f$9 = function16;
        this.f$10 = function17;
        this.f$11 = n;
        this.f$12 = n2;
        this.f$13 = n3;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivityKt.BrowserApp$lambda$114(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, (Composer)object, (Integer)object2);
    }
}

