/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.ui.Modifier
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.ui.Modifier;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/OpenWindowsDialogKt;", method="OpenWindowsDialog$lambda$16", proto="(Lcom/muslim/browser/pro/browser/BrowserUiState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;IILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class OpenWindowsDialogKt$$ExternalSyntheticLambda7
implements Function2 {
    public final BrowserUiState f$0;
    public final Function1 f$1;
    public final Function1 f$2;
    public final Function0 f$3;
    public final Function0 f$4;
    public final Modifier f$5;
    public final int f$6;
    public final int f$7;

    public /* synthetic */ OpenWindowsDialogKt$$ExternalSyntheticLambda7(BrowserUiState browserUiState, Function1 function1, Function1 function12, Function0 function0, Function0 function02, Modifier modifier, int n, int n2) {
        this.f$0 = browserUiState;
        this.f$1 = function1;
        this.f$2 = function12;
        this.f$3 = function0;
        this.f$4 = function02;
        this.f$5 = modifier;
        this.f$6 = n;
        this.f$7 = n2;
    }

    public final Object invoke(Object object, Object object2) {
        return OpenWindowsDialogKt.OpenWindowsDialog$lambda$16(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, (Composer)object, (Integer)object2);
    }
}

