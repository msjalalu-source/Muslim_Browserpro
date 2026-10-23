/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.activity.result.ActivityResult
 *  androidx.activity.result.ActivityResultCallback
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 */
package com.muslim.browser.pro;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.MainActivity;

@LambdaMethod(holder="Lcom/muslim/browser/pro/MainActivity;", method="fileChooserLauncher$lambda$0", proto="(Lcom/muslim/browser/pro/MainActivity;Landroidx/activity/result/ActivityResult;)V")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class MainActivity$$ExternalSyntheticLambda3
implements ActivityResultCallback {
    public final MainActivity f$0;

    public /* synthetic */ MainActivity$$ExternalSyntheticLambda3(MainActivity mainActivity) {
        this.f$0 = mainActivity;
    }

    public final void onActivityResult(Object object) {
        MainActivity.fileChooserLauncher$lambda$0(this.f$0, (ActivityResult)object);
    }
}

