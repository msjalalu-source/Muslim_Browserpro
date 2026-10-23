/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.ValueCallback
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import android.webkit.ValueCallback;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72$lambda$71", proto="(Lkotlin/jvm/functions/Function1;Ljava/lang/String;)V")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda50
implements ValueCallback {
    public final Function1 f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda50(Function1 function1) {
        this.f$0 = function1;
    }

    public final void onReceiveValue(Object object) {
        MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72$lambda$71(this.f$0, (String)object);
    }
}

