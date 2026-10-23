/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import com.muslim.browser.pro.browser.TranslationEngine;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$79$lambda$78", proto="(Lcom/muslim/browser/pro/browser/BrowserViewModel;Lcom/muslim/browser/pro/browser/TranslationEngine;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda9
implements Function1 {
    public final BrowserViewModel f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda9(BrowserViewModel browserViewModel) {
        this.f$0 = browserViewModel;
    }

    public final Object invoke(Object object) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$79$lambda$78(this.f$0, (TranslationEngine)((Object)object));
    }
}

