/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro;

import android.content.Context;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.DownloadEntry;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivityKt;", method="BrowserApp$lambda$113$lambda$112$lambda$93$lambda$92", proto="(Landroid/content/Context;Lcom/muslim/browser/pro/browser/DownloadEntry;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivityKt$$ExternalSyntheticLambda17
implements Function1 {
    public final Context f$0;

    public /* synthetic */ MainActivityKt$$ExternalSyntheticLambda17(Context context) {
        this.f$0 = context;
    }

    public final Object invoke(Object object) {
        return MainActivityKt.BrowserApp$lambda$113$lambda$112$lambda$93$lambda$92(this.f$0, (DownloadEntry)object);
    }
}

