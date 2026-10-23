/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.runtime.Composer;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreenKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import java.util.List;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/DownloadHistoryScreenKt;", method="DownloadHistoryScreen$lambda$34$lambda$33$lambda$18", proto="(Ljava/util/List;Lcom/muslim/browser/pro/ui/theme/AppColors;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class DownloadHistoryScreenKt$$ExternalSyntheticLambda6
implements Function2 {
    public final List f$0;
    public final AppColors f$1;
    public final Function0 f$2;
    public final MutableState f$3;

    public /* synthetic */ DownloadHistoryScreenKt$$ExternalSyntheticLambda6(List list, AppColors appColors, Function0 function0, MutableState mutableState) {
        this.f$0 = list;
        this.f$1 = appColors;
        this.f$2 = function0;
        this.f$3 = mutableState;
    }

    public final Object invoke(Object object, Object object2) {
        return DownloadHistoryScreenKt.DownloadHistoryScreen$lambda$34$lambda$33$lambda$18(this.f$0, this.f$1, this.f$2, this.f$3, (Composer)object, (Integer)object2);
    }
}

