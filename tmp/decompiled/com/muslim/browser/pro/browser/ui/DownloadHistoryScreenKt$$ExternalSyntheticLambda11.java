/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DownloadHistoryScreenKt;", method="DownloadItemRow$lambda$48", proto="(ZZLcom/muslim/browser/pro/browser/DownloadEntry;Lcom/muslim/browser/pro/ui/theme/AppColors;ZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DownloadHistoryScreenKt$$ExternalSyntheticLambda11
implements Function2 {
    public final boolean f$0;
    public final boolean f$1;
    public final DownloadEntry f$2;
    public final AppColors f$3;
    public final boolean f$4;
    public final Function0 f$5;
    public final Function0 f$6;

    public /* synthetic */ DownloadHistoryScreenKt$$ExternalSyntheticLambda11(boolean bl, boolean bl2, DownloadEntry downloadEntry, AppColors appColors, boolean bl3, Function0 function0, Function0 function02) {
        this.f$0 = bl;
        this.f$1 = bl2;
        this.f$2 = downloadEntry;
        this.f$3 = appColors;
        this.f$4 = bl3;
        this.f$5 = function0;
        this.f$6 = function02;
    }

    public final Object invoke(Object object, Object object2) {
        return DownloadHistoryScreenKt.DownloadItemRow$lambda$48(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, (Composer)object, (Integer)object2);
    }
}

