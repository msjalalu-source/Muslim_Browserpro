/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity;", method="onCreate$lambda$34$lambda$33$lambda$32$lambda$31", proto="(Lcom/muslim/browser/pro/MainActivity;Landroid/webkit/WebView;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$$ExternalSyntheticLambda20
implements Function1 {
    public final MainActivity f$0;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda20(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final Object invoke(Object object) {
        return MainActivity.onCreate$lambda$34$lambda$33$lambda$32$lambda$31(this.f$0, (WebView)object);
    }
}

