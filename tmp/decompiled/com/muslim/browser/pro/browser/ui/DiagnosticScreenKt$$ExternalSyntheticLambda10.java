/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.ValueCallback
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser.ui;

import android.webkit.ValueCallback;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DiagnosticScreenKt;", method="collectLiveWebViewDiagnostics$lambda$0", proto="(Lkotlin/jvm/functions/Function1;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DiagnosticScreenKt$$ExternalSyntheticLambda10
implements ValueCallback {
    public final Function1 f$0;
    public final String f$1;
    public final String f$2;
    public final String f$3;

    public /* synthetic */ DiagnosticScreenKt$$ExternalSyntheticLambda10(Function1 function1, String string, String string2, String string3) {
        this.f$0 = function1;
        this.f$1 = string;
        this.f$2 = string2;
        this.f$3 = string3;
    }

    public final void onReceiveValue(Object object) {
        DiagnosticScreenKt.collectLiveWebViewDiagnostics$lambda$0(this.f$0, this.f$1, this.f$2, this.f$3, (String)object);
    }
}

