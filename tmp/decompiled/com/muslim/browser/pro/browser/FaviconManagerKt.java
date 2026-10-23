/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Bitmap
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.EffectsKt
 *  androidx.compose.runtime.MutableState
 *  androidx.compose.runtime.SnapshotStateKt
 *  androidx.compose.runtime.State
 *  androidx.compose.ui.platform.AndroidCompositionLocals_androidKt
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.CoroutineScope
 */
package com.muslim.browser.pro.browser;

import android.content.Context;
import android.graphics.Bitmap;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import com.muslim.browser.pro.browser.FaviconManager;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;

@Metadata(d1={"\u0000\u0010\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\u001a\u0017\u0010\u0000\u001a\u0004\u0018\u00010\u00012\u0006\u0010\u0002\u001a\u00020\u0003H\u0007\u00a2\u0006\u0002\u0010\u0004\u00a8\u0006\u0005\u00b2\u0006\f\u0010\u0006\u001a\u0004\u0018\u00010\u0001X\u008a\u008e\u0002"}, d2={"rememberFavicon", "Landroid/graphics/Bitmap;", "url", "", "(Ljava/lang/String;Landroidx/compose/runtime/Composer;I)Landroid/graphics/Bitmap;", "app", "bitmap"}, k=2, mv={2, 2, 0}, xi=48)
public final class FaviconManagerKt {
    public static final /* synthetic */ Bitmap access$rememberFavicon$lambda$2(MutableState mutableState) {
        return FaviconManagerKt.rememberFavicon$lambda$2((MutableState<Bitmap>)mutableState);
    }

    public static final /* synthetic */ void access$rememberFavicon$lambda$3(MutableState mutableState, Bitmap bitmap) {
        FaviconManagerKt.rememberFavicon$lambda$3((MutableState<Bitmap>)mutableState, bitmap);
    }

    public static final Bitmap rememberFavicon(String object, Composer composer, int n) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"url");
        ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-437207841, (String)"C(rememberFavicon)174@6373L7,175@6417L51,176@6487L85,180@6601L219,180@6578L242:FaviconManager.kt#30fmqj");
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventStart((int)-437207841, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.rememberFavicon (FaviconManager.kt:173)");
        }
        Object object2 = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalContext();
        ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
        object2 = composer.consume(object2);
        ComposerKt.sourceInformationMarkerEnd((Composer)composer);
        Context context = ((Context)object2).getApplicationContext();
        ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)750064050, (String)"CC(remember):FaviconManager.kt#9igjgp");
        n = (n & 0xE ^ 6) > 4 && composer.changed(object) || (n & 6) == 4 ? 1 : 0;
        object2 = composer.rememberedValue();
        if (n == 0 && object2 != Composer.Companion.getEmpty()) {
            object = object2;
        } else {
            object = FaviconManager.INSTANCE.extractDomain((String)object);
            composer.updateRememberedValue(object);
        }
        object2 = object;
        ComposerKt.sourceInformationMarkerEnd((Composer)composer);
        ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)750066324, (String)"CC(remember):FaviconManager.kt#9igjgp");
        boolean bl = composer.changed(object2);
        object = composer.rememberedValue();
        if (bl || object == Composer.Companion.getEmpty()) {
            object = SnapshotStateKt.mutableStateOf$default((Object)FaviconManager.INSTANCE.getFromMemory((String)object2), null, (int)2, null);
            composer.updateRememberedValue(object);
        }
        MutableState mutableState = (MutableState)object;
        ComposerKt.sourceInformationMarkerEnd((Composer)composer);
        ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)750070106, (String)"CC(remember):FaviconManager.kt#9igjgp");
        boolean bl2 = composer.changed((Object)mutableState);
        bl = composer.changed(object2);
        boolean bl3 = composer.changedInstance((Object)context);
        object = composer.rememberedValue();
        if (bl2 | bl | bl3 || object == Composer.Companion.getEmpty()) {
            object = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>((String)object2, context, (MutableState<Bitmap>)mutableState, null){
                final MutableState<Bitmap> $bitmap$delegate;
                final Context $context;
                final String $domain;
                int label;
                {
                    this.$domain = string;
                    this.$context = context;
                    this.$bitmap$delegate = mutableState;
                    super(2, continuation);
                }

                public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                    return (Continuation)new /* invalid duplicate definition of identical inner class */;
                }

                public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                    return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                }

                public final Object invokeSuspend(Object object) {
                    block6: {
                        Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                            default: {
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }
                            case 1: {
                                ResultKt.throwOnFailure((Object)object);
                                break;
                            }
                            case 0: {
                                ResultKt.throwOnFailure((Object)object);
                                if (FaviconManagerKt.access$rememberFavicon$lambda$2(this.$bitmap$delegate) == null && !StringsKt.isBlank((CharSequence)this.$domain)) {
                                    object = FaviconManager.INSTANCE;
                                    Object object3 = this.$context;
                                    Intrinsics.checkNotNull((Object)object3);
                                    String string = this.$domain;
                                    Continuation continuation = (Continuation)this;
                                    this.label = 1;
                                    object3 = ((FaviconManager)object).loadFavicon((Context)object3, string, (Continuation<? super Bitmap>)continuation);
                                    object = object3;
                                    if (object3 != object2) break;
                                    return object2;
                                }
                                break block6;
                            }
                        }
                        object = (Bitmap)object;
                        if (object != null) {
                            FaviconManagerKt.access$rememberFavicon$lambda$3(this.$bitmap$delegate, (Bitmap)object);
                        }
                    }
                    return Unit.INSTANCE;
                }
            };
            composer.updateRememberedValue(object);
        }
        object = (Function2)object;
        ComposerKt.sourceInformationMarkerEnd((Composer)composer);
        EffectsKt.LaunchedEffect((Object)object2, (Function2)object, (Composer)composer, (int)0);
        object = FaviconManagerKt.rememberFavicon$lambda$2((MutableState<Bitmap>)mutableState);
        if (ComposerKt.isTraceInProgress()) {
            ComposerKt.traceEventEnd();
        }
        ComposerKt.sourceInformationMarkerEnd((Composer)composer);
        return object;
    }

    private static final Bitmap rememberFavicon$lambda$2(MutableState<Bitmap> mutableState) {
        return (Bitmap)((State)mutableState).getValue();
    }

    private static final void rememberFavicon$lambda$3(MutableState<Bitmap> mutableState, Bitmap bitmap) {
        mutableState.setValue((Object)bitmap);
    }
}

