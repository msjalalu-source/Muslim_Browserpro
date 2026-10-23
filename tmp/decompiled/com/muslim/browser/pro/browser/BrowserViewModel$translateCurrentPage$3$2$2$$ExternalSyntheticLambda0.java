/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser;

import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserViewModel;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/BrowserViewModel$translateCurrentPage$3$2$2;", method="invokeSuspend$lambda$2", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Ljava/lang/String;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserViewModel$translateCurrentPage$3$2$2$$ExternalSyntheticLambda0
implements Function1 {
    public final BrowserViewModel f$0;

    public /* synthetic */ BrowserViewModel$translateCurrentPage$3$2$2$$ExternalSyntheticLambda0(BrowserViewModel browserViewModel) {
        this.f$0 = browserViewModel;
    }

    public final Object invoke(Object object) {
        return BrowserViewModel.translateCurrentPage.3.2.invokeSuspend$lambda$2(this.f$0, (String)object);
    }
}

