/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  androidx.compose.runtime.Composer
 *  androidx.compose.ui.Modifier
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import android.webkit.WebView;
import androidx.compose.runtime.Composer;
import androidx.compose.ui.Modifier;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DiagnosticScreenKt;", method="DiagnosticScreen$lambda$23", proto="(Landroid/webkit/WebView;ZZLkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;IILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DiagnosticScreenKt$$ExternalSyntheticLambda3
implements Function2 {
    public final WebView f$0;
    public final boolean f$1;
    public final boolean f$2;
    public final Function0 f$3;
    public final Modifier f$4;
    public final int f$5;
    public final int f$6;

    public /* synthetic */ DiagnosticScreenKt$$ExternalSyntheticLambda3(WebView webView2, boolean bl, boolean bl2, Function0 function0, Modifier modifier, int n, int n2) {
        this.f$0 = webView2;
        this.f$1 = bl;
        this.f$2 = bl2;
        this.f$3 = function0;
        this.f$4 = modifier;
        this.f$5 = n;
        this.f$6 = n2;
    }

    public final Object invoke(Object object, Object object2) {
        return DiagnosticScreenKt.DiagnosticScreen$lambda$23(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, (Composer)object, (Integer)object2);
    }
}

