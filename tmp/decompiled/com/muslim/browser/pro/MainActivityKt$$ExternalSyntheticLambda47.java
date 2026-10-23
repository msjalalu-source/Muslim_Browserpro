/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.WebView
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 */
package com.muslim.browser.pro;

import android.webkit.WebView;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72", proto="(Landroid/webkit/WebView;Ljava/lang/String;Lkotlin/jvm/functions/Function1;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda47
implements Function2 {
    public final WebView f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda47(WebView webView2) {
        this.f$0 = webView2;
    }

    public final Object invoke(Object object, Object object2) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72(this.f$0, (String)object, (Function1)object2);
    }
}

