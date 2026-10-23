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
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/OpenWindowsDialogKt;", method="WindowTabItem$lambda$23", proto="(ZLkotlin/jvm/functions/Function0;Lcom/muslim/browser/pro/browser/BrowserTab;ZLcom/muslim/browser/pro/ui/theme/AppColors;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class OpenWindowsDialogKt$$ExternalSyntheticLambda8
implements Function2 {
    public final boolean f$0;
    public final Function0 f$1;
    public final BrowserTab f$2;
    public final boolean f$3;
    public final AppColors f$4;

    public /* synthetic */ OpenWindowsDialogKt$$ExternalSyntheticLambda8(boolean bl, Function0 function0, BrowserTab browserTab, boolean bl2, AppColors appColors) {
        this.f$0 = bl;
        this.f$1 = function0;
        this.f$2 = browserTab;
        this.f$3 = bl2;
        this.f$4 = appColors;
    }

    public final Object invoke(Object object, Object object2) {
        return OpenWindowsDialogKt.WindowTabItem$lambda$23(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, (Composer)object, (Integer)object2);
    }
}

