/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreenKt;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DownloadHistoryScreenKt;", method="StatusProgressLine$lambda$57", proto="(Lcom/muslim/browser/pro/browser/DownloadEntry;ILandroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DownloadHistoryScreenKt$$ExternalSyntheticLambda0
implements Function2 {
    public final DownloadEntry f$0;
    public final int f$1;

    public /* synthetic */ DownloadHistoryScreenKt$$ExternalSyntheticLambda0(DownloadEntry downloadEntry, int n) {
        this.f$0 = downloadEntry;
        this.f$1 = n;
    }

    public final Object invoke(Object object, Object object2) {
        return DownloadHistoryScreenKt.StatusProgressLine$lambda$57(this.f$0, this.f$1, (Composer)object, (Integer)object2);
    }
}

