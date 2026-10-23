/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.SslWarningState;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$111$lambda$104", proto="(Lkotlin/jvm/functions/Function1;Lcom/muslim/browser/pro/browser/SslWarningState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda23
implements Function2 {
    public final Function1 f$0;
    public final SslWarningState f$1;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda23(Function1 function1, SslWarningState sslWarningState) {
        this.f$0 = function1;
        this.f$1 = sslWarningState;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$111$lambda$104(this.f$0, this.f$1, (Composer)object, (Integer)object2);
    }
}

