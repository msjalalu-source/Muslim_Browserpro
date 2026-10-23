/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.foundation.text.KeyboardActionScope
 *  androidx.compose.runtime.MutableState
 *  com.android.tools.r8.annotations.LambdaMethod
 *  com.android.tools.r8.annotations.SynthesizedClassV2
 *  kotlin.jvm.functions.Function1
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.runtime.MutableState;
import com.android.tools.r8.annotations.LambdaMethod;
import com.android.tools.r8.annotations.SynthesizedClassV2;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt;
import kotlin.jvm.functions.Function1;

@LambdaMethod(holder="Lcom/muslim/browser/pro/browser/ui/BrowserWebViewKt;", method="BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$8$lambda$7", proto="(Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/MutableState;Landroidx/compose/runtime/MutableState;Landroidx/compose/foundation/text/KeyboardActionScope;)Lkotlin/Unit;")
@SynthesizedClassV2(apiLevel=-2, kind=19, versionHash="7bbc1d7254613ffaa026de47d975f73968361ef3e5d7b818ce0a83a33039c6b8")
public final class BrowserWebViewKt$$ExternalSyntheticLambda8
implements Function1 {
    public final Function1 f$0;
    public final MutableState f$1;
    public final MutableState f$2;

    public /* synthetic */ BrowserWebViewKt$$ExternalSyntheticLambda8(Function1 function1, MutableState mutableState, MutableState mutableState2) {
        this.f$0 = function1;
        this.f$1 = mutableState;
        this.f$2 = mutableState2;
    }

    public final Object invoke(Object object) {
        return BrowserWebViewKt.BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$8$lambda$7(this.f$0, this.f$1, this.f$2, (KeyboardActionScope)object);
    }
}

