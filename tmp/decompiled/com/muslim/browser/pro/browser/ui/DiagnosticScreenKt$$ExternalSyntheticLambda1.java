/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DiagnosticScreenKt;", method="DiagnosticItemCard$lambda$27", proto="(ILjava/lang/String;Ljava/lang/String;Lcom/muslim/browser/pro/ui/theme/AppColors;ZIILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DiagnosticScreenKt$$ExternalSyntheticLambda1
implements Function2 {
    public final int f$0;
    public final String f$1;
    public final String f$2;
    public final AppColors f$3;
    public final boolean f$4;
    public final int f$5;
    public final int f$6;

    public /* synthetic */ DiagnosticScreenKt$$ExternalSyntheticLambda1(int n, String string, String string2, AppColors appColors, boolean bl, int n2, int n3) {
        this.f$0 = n;
        this.f$1 = string;
        this.f$2 = string2;
        this.f$3 = appColors;
        this.f$4 = bl;
        this.f$5 = n2;
        this.f$6 = n3;
    }

    public final Object invoke(Object object, Object object2) {
        return DiagnosticScreenKt.DiagnosticItemCard$lambda$27(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, (Composer)object, (Integer)object2);
    }
}

