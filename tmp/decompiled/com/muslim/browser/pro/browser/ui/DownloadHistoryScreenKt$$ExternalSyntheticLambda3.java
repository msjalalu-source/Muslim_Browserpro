/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DownloadHistoryScreenKt;", method="DownloadHistoryScreen$lambda$10", proto="(Lcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DownloadHistoryScreenKt$$ExternalSyntheticLambda3
implements Function2 {
    public final AppColors f$0;
    public final MutableState f$1;

    public /* synthetic */ DownloadHistoryScreenKt$$ExternalSyntheticLambda3(AppColors appColors, MutableState mutableState) {
        this.f$0 = appColors;
        this.f$1 = mutableState;
    }

    public final Object invoke(Object object, Object object2) {
        return DownloadHistoryScreenKt.DownloadHistoryScreen$lambda$10(this.f$0, this.f$1, (Composer)object, (Integer)object2);
    }
}

