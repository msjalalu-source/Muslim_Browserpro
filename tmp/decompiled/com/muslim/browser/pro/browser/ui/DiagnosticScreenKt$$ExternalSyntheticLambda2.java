/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.webkit.WebView
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import android.content.Context;
import android.webkit.WebView;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DiagnosticScreenKt;", method="DiagnosticScreen$lambda$22", proto="(Lkotlin/jvm/functions/Function0;Lcom/muslim/browser/pro/ui/theme/AppColors;Landroid/content/Context;Landroid/webkit/WebView;ZZLandroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DiagnosticScreenKt$$ExternalSyntheticLambda2
implements Function2 {
    public final Function0 f$0;
    public final AppColors f$1;
    public final Context f$2;
    public final WebView f$3;
    public final boolean f$4;
    public final boolean f$5;
    public final MutableState f$6;
    public final MutableState f$7;

    public /* synthetic */ DiagnosticScreenKt$$ExternalSyntheticLambda2(Function0 function0, AppColors appColors, Context context, WebView webView2, boolean bl, boolean bl2, MutableState mutableState, MutableState mutableState2) {
        this.f$0 = function0;
        this.f$1 = appColors;
        this.f$2 = context;
        this.f$3 = webView2;
        this.f$4 = bl;
        this.f$5 = bl2;
        this.f$6 = mutableState;
        this.f$7 = mutableState2;
    }

    public final Object invoke(Object object, Object object2) {
        return DiagnosticScreenKt.DiagnosticScreen$lambda$22(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, (Composer)object, (Integer)object2);
    }
}

