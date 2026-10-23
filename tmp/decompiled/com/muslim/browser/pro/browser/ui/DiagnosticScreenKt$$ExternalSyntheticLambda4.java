/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DiagnosticData;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DiagnosticScreenKt;", method="DiagnosticScreen$refreshData$lambda$7", proto="(Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Lcom/muslim/browser/pro/browser/ui/DiagnosticData;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DiagnosticScreenKt$$ExternalSyntheticLambda4
implements Function1 {
    public final MutableState f$0;
    public final MutableState f$1;

    public /* synthetic */ DiagnosticScreenKt$$ExternalSyntheticLambda4(MutableState mutableState, MutableState mutableState2) {
        this.f$0 = mutableState;
        this.f$1 = mutableState2;
    }

    public final Object invoke(Object object) {
        return DiagnosticScreenKt.DiagnosticScreen$refreshData$lambda$7(this.f$0, this.f$1, (DiagnosticData)object);
    }
}

