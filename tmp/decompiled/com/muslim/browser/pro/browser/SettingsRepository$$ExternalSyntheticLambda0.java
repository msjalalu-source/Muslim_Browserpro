/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser;

import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.HistoryEntry;
import com.muslim.browser.pro.browser.SettingsRepository;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/SettingsRepository;", method="deleteHistoryEntry$lambda$4", proto="(Ljava/lang/String;Lcom/muslim/browser/pro/browser/HistoryEntry;)Z")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class SettingsRepository$$ExternalSyntheticLambda0
implements Function1 {
    public final String f$0;

    public /* synthetic */ SettingsRepository$$ExternalSyntheticLambda0(String string) {
        this.f$0 = string;
    }

    public final Object invoke(Object object) {
        return SettingsRepository.deleteHistoryEntry$lambda$4(this.f$0, (HistoryEntry)object);
    }
}

