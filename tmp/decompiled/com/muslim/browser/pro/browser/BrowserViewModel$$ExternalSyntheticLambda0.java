/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser;

import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserViewModel;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/BrowserViewModel;", method="translateCurrentPage$lambda$44", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Lkotlin/jvm/functions/Function2;Ljava/lang/String;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserViewModel$$ExternalSyntheticLambda0
implements Function1 {
    public final BrowserViewModel f$0;
    public final Function2 f$1;

    public /* synthetic */ BrowserViewModel$$ExternalSyntheticLambda0(BrowserViewModel browserViewModel, Function2 function2) {
        this.f$0 = browserViewModel;
        this.f$1 = function2;
    }

    public final Object invoke(Object object) {
        return BrowserViewModel.translateCurrentPage$lambda$44(this.f$0, this.f$1, (String)object);
    }
}

