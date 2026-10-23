/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.webkit.WebView
 *  androidx.compose.foundation.layout.PaddingValues
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.State
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function3
 */
package com.muslim.browser.pro;

import android.content.Context;
import android.webkit.WebView;
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.State;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserViewModel;
import com.muslim.browser.pro.browser.TabWebViewManager;
import com.muslim.browser.pro.ui.theme.AppColors;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function3;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113", proto="(Lcom/muslim/browser/pro/ui/theme/AppColors;Landroid/webkit/WebView;Lkotlin/jvm/functions/Function1;Lcom/muslim/browser/pro/browser/BrowserViewModel;Lcom/muslim/browser/pro/browser/TabWebViewManager;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroid/content/Context;Landroidx/compose/runtime/State;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/foundation/layout/PaddingValues;Landroidx/compose/runtime/Composer;I)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda35
implements Function3 {
    public final AppColors f$0;
    public final WebView f$1;
    public final Context f$10;
    public final State f$11;
    public final Function1 f$12;
    public final Function1 f$13;
    public final Function1 f$2;
    public final BrowserViewModel f$3;
    public final TabWebViewManager f$4;
    public final Function0 f$5;
    public final Function0 f$6;
    public final Function1 f$7;
    public final Function1 f$8;
    public final Function1 f$9;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda35(AppColors appColors, WebView webView2, Function1 function1, BrowserViewModel browserViewModel, TabWebViewManager tabWebViewManager, Function0 function0, Function0 function02, Function1 function12, Function1 function13, Function1 function14, Context context, State state, Function1 function15, Function1 function16) {
        this.f$0 = appColors;
        this.f$1 = webView2;
        this.f$2 = function1;
        this.f$3 = browserViewModel;
        this.f$4 = tabWebViewManager;
        this.f$5 = function0;
        this.f$6 = function02;
        this.f$7 = function12;
        this.f$8 = function13;
        this.f$9 = function14;
        this.f$10 = context;
        this.f$11 = state;
        this.f$12 = function15;
        this.f$13 = function16;
    }

    public final Object invoke(Object object, Object object2, Object object3) {
        return MainActivityKt.BrowserApp$lambda$113(this.f$0, this.f$1, this.f$2, this.f$3, this.f$4, this.f$5, this.f$6, this.f$7, this.f$8, this.f$9, this.f$10, this.f$11, this.f$12, this.f$13, (PaddingValues)object, (Composer)object2, (Integer)object3);
    }
}

