/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.webkit.DownloadListener
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 */
package com.muslim.browser.pro;

import android.webkit.DownloadListener;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity;", method="createConfiguredWebView$lambda$36$lambda$35", proto="(Lcom/muslim/browser/pro/MainActivity;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$$ExternalSyntheticLambda2
implements DownloadListener {
    public final MainActivity f$0;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda2(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final void onDownloadStart(String string, String string2, String string3, String string4, long l) {
        MainActivity.createConfiguredWebView$lambda$36$lambda$35(this.f$0, string, string2, string3, string4, l);
    }
}

