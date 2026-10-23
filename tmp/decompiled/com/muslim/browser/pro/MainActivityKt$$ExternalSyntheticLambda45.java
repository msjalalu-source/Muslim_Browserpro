/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.material3.SnackbarHostState
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import androidx.compose.material3.SnackbarHostState;
import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$36", proto="(Landroidx/compose/material3/SnackbarHostState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda45
implements Function2 {
    public final SnackbarHostState f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda45(SnackbarHostState snackbarHostState) {
        this.f$0 = snackbarHostState;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivityKt.BrowserApp$lambda$36(this.f$0, (Composer)object, (Integer)object2);
    }
}

