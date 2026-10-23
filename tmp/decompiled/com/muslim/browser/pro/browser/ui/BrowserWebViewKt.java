/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewGroup$LayoutParams
 *  android.webkit.WebView
 *  android.widget.FrameLayout
 *  androidx.compose.foundation.BackgroundKt
 *  androidx.compose.foundation.BorderStroke
 *  androidx.compose.foundation.BorderStrokeKt
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Horizontal
 *  androidx.compose.foundation.layout.Arrangement$Vertical
 *  androidx.compose.foundation.layout.BoxKt
 *  androidx.compose.foundation.layout.BoxScope
 *  androidx.compose.foundation.layout.BoxScopeInstance
 *  androidx.compose.foundation.layout.ColumnKt
 *  androidx.compose.foundation.layout.ColumnScope
 *  androidx.compose.foundation.layout.ColumnScopeInstance
 *  androidx.compose.foundation.layout.PaddingKt
 *  androidx.compose.foundation.layout.RowKt
 *  androidx.compose.foundation.layout.RowScope
 *  androidx.compose.foundation.layout.RowScopeInstance
 *  androidx.compose.foundation.layout.SizeKt
 *  androidx.compose.foundation.layout.SpacerKt
 *  androidx.compose.foundation.shape.RoundedCornerShapeKt
 *  androidx.compose.foundation.text.BasicTextFieldKt
 *  androidx.compose.foundation.text.KeyboardActionScope
 *  androidx.compose.foundation.text.KeyboardActions
 *  androidx.compose.foundation.text.KeyboardOptions
 *  androidx.compose.material.icons.Icons
 *  androidx.compose.material.icons.Icons$Filled
 *  androidx.compose.material.icons.filled.CloseKt
 *  androidx.compose.material.icons.filled.LockKt
 *  androidx.compose.material.icons.filled.RefreshKt
 *  androidx.compose.material3.IconButtonKt
 *  androidx.compose.material3.IconKt
 *  androidx.compose.material3.ProgressIndicatorKt
 *  androidx.compose.material3.SurfaceKt
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Applier
 *  androidx.compose.runtime.ComposablesKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.CompositionLocalMap
 *  androidx.compose.runtime.EffectsKt
 *  androidx.compose.runtime.MutableState
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.SnapshotStateKt
 *  androidx.compose.runtime.State
 *  androidx.compose.runtime.Updater
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.ui.Alignment
 *  androidx.compose.ui.Alignment$Horizontal
 *  androidx.compose.ui.Alignment$Vertical
 *  androidx.compose.ui.ComposedModifierKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.graphics.Brush
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.Shape
 *  androidx.compose.ui.graphics.SolidColor
 *  androidx.compose.ui.graphics.vector.ImageVector
 *  androidx.compose.ui.layout.MeasurePolicy
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.platform.TestTagKt
 *  androidx.compose.ui.text.TextStyle
 *  androidx.compose.ui.text.input.ImeAction
 *  androidx.compose.ui.text.style.TextOverflow
 *  androidx.compose.ui.unit.Dp
 *  androidx.compose.ui.unit.TextUnitKt
 *  androidx.compose.ui.viewinterop.AndroidView_androidKt
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  com.muslim.browser.pro.ui.theme.ThemeKt
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlinx.coroutines.CoroutineScope
 */
package com.muslim.browser.pro.browser.ui;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.FrameLayout;
import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.layout.Arrangement;
import androidx.compose.foundation.layout.BoxKt;
import androidx.compose.foundation.layout.BoxScope;
import androidx.compose.foundation.layout.BoxScopeInstance;
import androidx.compose.foundation.layout.ColumnKt;
import androidx.compose.foundation.layout.ColumnScope;
import androidx.compose.foundation.layout.ColumnScopeInstance;
import androidx.compose.foundation.layout.PaddingKt;
import androidx.compose.foundation.layout.RowKt;
import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.RowScopeInstance;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.foundation.text.BasicTextFieldKt;
import androidx.compose.foundation.text.KeyboardActionScope;
import androidx.compose.foundation.text.KeyboardActions;
import androidx.compose.foundation.text.KeyboardOptions;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.CloseKt;
import androidx.compose.material.icons.filled.LockKt;
import androidx.compose.material.icons.filled.RefreshKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.ProgressIndicatorKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.CompositionLocalMap;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.MutableState;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.SnapshotStateKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Brush;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.SolidColor;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.layout.MeasurePolicy;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.text.input.ImeAction;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import androidx.compose.ui.viewinterop.AndroidView_androidKt;
import com.muslim.browser.pro.MainActivity;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda10;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda11;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda12;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda2;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda3;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda4;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda5;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda6;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda7;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda8;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt$$ExternalSyntheticLambda9;
import com.muslim.browser.pro.ui.theme.AppColors;
import com.muslim.browser.pro.ui.theme.AppTheme;
import com.muslim.browser.pro.ui.theme.ThemeKt;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlinx.coroutines.CoroutineScope;

@Metadata(d1={"\u00002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000b\n\u0000\u001aI\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\u0012\u0010\u0006\u001a\u000e\u0012\u0004\u0012\u00020\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\t\u001a\b\u0012\u0004\u0012\u00020\u00010\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u00a2\u0006\u0002\u0010\r\u00a8\u0006\u000e\u00b2\u0006\n\u0010\u000f\u001a\u00020\u0010X\u008a\u008e\u0002\u00b2\u0006\n\u0010\u0011\u001a\u00020\bX\u008a\u008e\u0002"}, d2={"BrowserWebView", "", "uiState", "Lcom/muslim/browser/pro/browser/BrowserUiState;", "webView", "Landroid/webkit/WebView;", "onUrlSubmit", "Lkotlin/Function1;", "", "onReload", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "(Lcom/muslim/browser/pro/browser/BrowserUiState;Landroid/webkit/WebView;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "app", "isEditingUrl", "", "editUrlText"}, k=2, mv={2, 2, 0}, xi=48)
public final class BrowserWebViewKt {
    public static final void BrowserWebView(BrowserUiState browserUiState, WebView webView2, Function1<? super String, Unit> function1, Function0<Unit> function0, Modifier modifier, Composer object, int n, int n2) {
        int n3;
        int n4;
        Intrinsics.checkNotNullParameter((Object)browserUiState, (String)"uiState");
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        Intrinsics.checkNotNullParameter(function1, (String)"onUrlSubmit");
        Intrinsics.checkNotNullParameter(function0, (String)"onReload");
        Composer composer = object.startRestartGroup(-385217815);
        ComposerKt.sourceInformation((Composer)composer, (String)"C(BrowserWebView)P(3,4,2,1)60@2542L7,61@2574L34,62@2632L31,65@2789L91,65@2754L126,73@2924L8502:BrowserWebView.kt#kaa2o3");
        int n5 = n4 = n;
        if ((n & 6) == 0) {
            n5 = composer.changedInstance((Object)browserUiState) ? 4 : 2;
            n5 = n4 | n5;
        }
        n4 = n5;
        if ((n & 0x30) == 0) {
            n4 = composer.changedInstance((Object)webView2) ? 32 : 16;
            n4 = n5 | n4;
        }
        n5 = n4;
        if ((n & 0x180) == 0) {
            n5 = composer.changedInstance(function1) ? 256 : 128;
            n5 = n4 | n5;
        }
        n4 = n5;
        if ((n & 0xC00) == 0) {
            n4 = composer.changedInstance(function0) ? 2048 : 1024;
            n4 = n5 | n4;
        }
        if ((n3 = n2 & 0x10) != 0) {
            n4 |= 0x6000;
        } else if ((n & 0x6000) == 0) {
            n5 = composer.changed((Object)modifier) ? 16384 : 8192;
            n4 |= n5;
        }
        if ((n4 & 0x2493) == 9362 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (n3 != 0) {
                modifier = (Modifier)Modifier.Companion;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-385217815, (int)n4, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView (BrowserWebView.kt:59)");
            }
            object = (CompositionLocal)ThemeKt.getLocalAppColors();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object = composer.consume((CompositionLocal)object);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            AppColors appColors = (AppColors)object;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-827123637, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            object = composer.rememberedValue();
            if (object == Composer.Companion.getEmpty()) {
                object = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                composer.updateRememberedValue(object);
            }
            MutableState mutableState = (MutableState)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-827121784, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            object = composer.rememberedValue();
            if (object == Composer.Companion.getEmpty()) {
                object = SnapshotStateKt.mutableStateOf$default((Object)"", null, (int)2, null);
                composer.updateRememberedValue(object);
            }
            MutableState mutableState2 = (MutableState)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            String string = browserUiState.getCurrentUrl();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-827116700, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            boolean bl = composer.changedInstance((Object)browserUiState);
            object = composer.rememberedValue();
            if (bl || object == Composer.Companion.getEmpty()) {
                object = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(browserUiState, (MutableState<Boolean>)mutableState, (MutableState<String>)mutableState2, null){
                    final MutableState<String> $editUrlText$delegate;
                    final MutableState<Boolean> $isEditingUrl$delegate;
                    final BrowserUiState $uiState;
                    int label;
                    {
                        this.$uiState = browserUiState;
                        this.$isEditingUrl$delegate = mutableState;
                        this.$editUrlText$delegate = mutableState2;
                        super(2, continuation);
                    }

                    public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                        return (Continuation)new /* invalid duplicate definition of identical inner class */;
                    }

                    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                        return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                    }

                    public final Object invokeSuspend(Object object) {
                        IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                            default: {
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }
                            case 0: 
                        }
                        ResultKt.throwOnFailure((Object)object);
                        if (!BrowserWebViewKt.access$BrowserWebView$lambda$1(this.$isEditingUrl$delegate)) {
                            BrowserWebViewKt.access$BrowserWebView$lambda$5(this.$editUrlText$delegate, this.$uiState.getCurrentUrl());
                        }
                        return Unit.INSTANCE;
                    }
                };
                composer.updateRememberedValue(object);
            }
            object = (Function2)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            EffectsKt.LaunchedEffect((Object)string, (Function2)object, (Composer)composer, (int)0);
            long l = appColors.getBackground-0d7_KjU();
            object = TestTagKt.testTag((Modifier)BackgroundKt.background-bw27NRU$default((Modifier)SizeKt.fillMaxSize$default((Modifier)modifier, (float)0.0f, (int)1, null), (long)appColors.getBackground-0d7_KjU(), null, (int)2, null), (String)"browser_web_view_container");
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            Arrangement.Vertical vertical = Arrangement.INSTANCE.getTop();
            string = Alignment.Companion.getStart();
            string = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)vertical, (Alignment.Horizontal)string, (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n4 = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            vertical = composer.getCurrentCompositionLocalMap();
            object = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object);
            Function0 function02 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function02);
            } else {
                composer.useNode();
            }
            function02 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function02, (Object)string, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)vertical, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            string = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n4)) {
                function02.updateRememberedValue((Object)n4);
                function02.apply((Object)n4, (Function2)string);
            }
            Updater.set-impl((Composer)function02, (Object)object, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-295073467, (String)"C84@3326L5745,80@3153L5918,208@9111L2309:BrowserWebView.kt#kaa2o3");
            object = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            long l2 = appColors.getSurface-0d7_KjU();
            float f = appColors.isMonochrome() ? Dp.constructor-impl((float)0.0f) : Dp.constructor-impl((float)2);
            SurfaceKt.Surface-T9BRK9s((Modifier)object, null, (long)l2, (long)0L, (float)0.0f, (float)f, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)2045763512, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda0(browserUiState, appColors, function0, function1, mutableState, mutableState2), (Composer)composer, (int)54)), (Composer)composer, (int)0xC00006, (int)90);
            mutableState = BackgroundKt.background-bw27NRU$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (long)l, null, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)733328855, (String)"CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            object = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)Alignment.Companion.getTopStart(), (boolean)false);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n4 = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            mutableState2 = composer.getCurrentCompositionLocalMap();
            mutableState = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)mutableState);
            string = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode((Function0)string);
            } else {
                composer.useNode();
            }
            string = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)string, (Object)object, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)string, (Object)mutableState2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (string.getInserting() || !Intrinsics.areEqual((Object)string.rememberedValue(), (Object)n4)) {
                string.updateRememberedValue((Object)n4);
                string.apply((Object)n4, (Function2)object);
            }
            Updater.set-impl((Composer)string, (Object)mutableState, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2146769399, (String)"C73@3429L9:Box.kt#2w3rfo");
            object = (BoxScope)BoxScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1292028451, (String)"C214@9280L373,222@9680L935,213@9241L1439:BrowserWebView.kt#kaa2o3");
            mutableState = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-872962878, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            object = composer.rememberedValue();
            if (object == Composer.Companion.getEmpty()) {
                object = new BrowserWebViewKt$$ExternalSyntheticLambda1();
                composer.updateRememberedValue(object);
            }
            mutableState2 = (Function1)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-872949516, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            bl = composer.changedInstance((Object)browserUiState);
            boolean bl2 = composer.changedInstance((Object)webView2);
            object = composer.rememberedValue();
            if (bl | bl2 || object == Composer.Companion.getEmpty()) {
                object = new BrowserWebViewKt$$ExternalSyntheticLambda2(browserUiState, webView2);
                composer.updateRememberedValue(object);
            }
            object = (Function1)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            AndroidView_androidKt.AndroidView((Function1)mutableState2, (Modifier)mutableState, (Function1)object, (Composer)composer, (int)54, (int)0);
            if (!browserUiState.isPageContentVisible() && browserUiState.isLoading()) {
                composer.startReplaceGroup(-1290518721);
                ComposerKt.sourceInformation((Composer)composer, (String)"247@10949L447,244@10830L566");
                object = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
                SurfaceKt.Surface-T9BRK9s((Modifier)object, null, (long)l, (long)0L, (float)0.0f, (float)0.0f, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)1018211447, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda3(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)0xC00006, (int)122);
            } else {
                composer.startReplaceGroup(-1301263755);
            }
            composer.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        object = composer.endRestartGroup();
        if (object != null) {
            object.updateScope((Function2)new BrowserWebViewKt$$ExternalSyntheticLambda4(browserUiState, webView2, function1, function0, modifier, n, n2));
        }
    }

    private static final boolean BrowserWebView$lambda$1(MutableState<Boolean> mutableState) {
        return (Boolean)((State)mutableState).getValue();
    }

    private static final void BrowserWebView$lambda$2(MutableState<Boolean> mutableState, boolean bl) {
        mutableState.setValue((Object)bl);
    }

    static final Unit BrowserWebView$lambda$32$lambda$23(BrowserUiState object, AppColors appColors, Function0 object2, Function1 function1, MutableState mutableState, MutableState mutableState2, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C85@3340L5721:BrowserWebView.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)2045763512, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous> (BrowserWebView.kt:85)");
            }
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            Modifier modifier = (Modifier)Modifier.Companion;
            MeasurePolicy measurePolicy = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            CompositionLocalMap compositionLocalMap = composer.getCurrentCompositionLocalMap();
            modifier = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)modifier);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function0);
            } else {
                composer.useNode();
            }
            function0 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function0, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            measurePolicy = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)measurePolicy);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            measurePolicy = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-70560029, (String)"C86@3365L5097:BrowserWebView.kt#kaa2o3");
            compositionLocalMap = PaddingKt.padding-VpY3zN4((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)((float)41.8))), (float)Dp.constructor-impl((float)10), (float)Dp.constructor-impl((float)3));
            measurePolicy = Alignment.Companion.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            modifier = RowKt.rowMeasurePolicy((Arrangement.Horizontal)Arrangement.INSTANCE.getStart(), (Alignment.Vertical)measurePolicy, (Composer)composer, (int)(390 >> 3 & 0xE | 390 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            measurePolicy = composer.getCurrentCompositionLocalMap();
            compositionLocalMap = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)compositionLocalMap);
            function0 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function0);
            } else {
                composer.useNode();
            }
            function0 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            measurePolicy = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)measurePolicy);
            }
            Updater.set-impl((Composer)function0, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            compositionLocalMap = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2078489174, (String)"C93@3669L253,100@3944L39,109@4347L3467,102@4005L3809,174@7836L39,181@8123L321,176@7897L547:BrowserWebView.kt#kaa2o3");
            measurePolicy = LockKt.getLock((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = appColors.getAccent-0d7_KjU();
            modifier = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)13));
            IconKt.Icon-ww6aTOc((ImageVector)measurePolicy, (String)"Secure Connection", (Modifier)modifier, (long)l, (Composer)composer, (int)432, (int)0);
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)6)), (Composer)composer, (int)6);
            SurfaceKt.Surface-T9BRK9s((Modifier)SizeKt.height-3ABfNKs((Modifier)RowScope.weight$default((RowScope)compositionLocalMap, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)Dp.constructor-impl((float)32)), (Shape)((Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)16))), (long)appColors.getSurfaceVariant-0d7_KjU(), (long)0L, (float)0.0f, (float)0.0f, (BorderStroke)BorderStrokeKt.BorderStroke-cXLIe8U((float)Dp.constructor-impl((float)((float)0.5)), (long)appColors.getBorder-0d7_KjU()), (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-1610139027, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda5((BrowserUiState)object, appColors, function1, mutableState, mutableState2), (Composer)composer, (int)54)), (Composer)composer, (int)0xC00000, (int)56);
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)6)), (Composer)composer, (int)6);
            IconButtonKt.IconButton((Function0)object2, (Modifier)TestTagKt.testTag((Modifier)SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)32)), (String)"browser_refresh_button"), (boolean)false, null, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)36562735, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda6(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)196656, (int)28);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (((BrowserUiState)object).isLoading()) {
                composer.startReplaceGroup(-65574145);
                ComposerKt.sourceInformation((Composer)composer, (String)"194@8638L34,193@8578L365");
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2080323024, (String)"CC(remember):BrowserWebView.kt#9igjgp");
                boolean bl = composer.changedInstance(object);
                object2 = composer.rememberedValue();
                if (!bl && object2 != Composer.Companion.getEmpty()) {
                    object = object2;
                } else {
                    object = new BrowserWebViewKt$$ExternalSyntheticLambda7((BrowserUiState)object);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ProgressIndicatorKt.LinearProgressIndicator-GJbTh5U((Function0)object, (Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)2)), (long)appColors.getAccent-0d7_KjU(), (long)Color.copy-wmQWz5c$default((long)appColors.getAccent-0d7_KjU(), (float)0.2f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), (int)0, (float)0.0f, null, (Composer)composer, (int)48, (int)112);
                composer.endReplaceGroup();
            } else {
                composer.startReplaceGroup(-65176508);
                ComposerKt.sourceInformation((Composer)composer, (String)"202@8989L40");
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)2)), (Composer)composer, (int)6);
                composer.endReplaceGroup();
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17(BrowserUiState object, AppColors appColors, Function1 object2, MutableState mutableState, MutableState mutableState2, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C110@4373L3419:BrowserWebView.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1610139027, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (BrowserWebView.kt:110)");
            }
            Object object3 = Alignment.Companion.getCenterVertically();
            Modifier modifier = PaddingKt.padding-VpY3zN4$default((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)10), (float)0.0f, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            Object object4 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)Arrangement.INSTANCE.getStart(), (Alignment.Vertical)object3, (Composer)composer, (int)(390 >> 3 & 0xE | 390 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            object3 = composer.getCurrentCompositionLocalMap();
            modifier = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)modifier);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function0);
            } else {
                composer.useNode();
            }
            function0 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function0, (Object)object4, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)object3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object3 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)object3);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            object4 = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)285217150, (String)"C134@5730L165,118@4825L144,139@5979L1034,116@4666L2377:BrowserWebView.kt#kaa2o3");
            object3 = BrowserWebViewKt.BrowserWebView$lambda$1((MutableState<Boolean>)mutableState) ? BrowserWebViewKt.BrowserWebView$lambda$4((MutableState<String>)mutableState2) : ((BrowserUiState)object).getCurrentUrl();
            Modifier modifier2 = TestTagKt.testTag((Modifier)RowScope.weight$default((RowScope)object4, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (String)"browser_top_url_bar");
            long l = appColors.getTextPrimary-0d7_KjU();
            long l2 = TextUnitKt.getSp((int)12);
            modifier = new TextStyle(l, l2, null, null, null, null, null, 0L, null, null, null, 0L, null, null, null, 0, 0, TextUnitKt.getSp((int)16), null, null, null, 0, 0, null, 0xFDFFFC, null);
            function0 = new SolidColor(appColors.getAccent-0d7_KjU(), null);
            KeyboardOptions keyboardOptions = new KeyboardOptions(0, null, 0, ImeAction.Companion.getGo-eUduSuo(), null, null, null, 119, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-129315666, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            boolean bl = composer.changed(object2);
            object4 = composer.rememberedValue();
            if (!bl && object4 != Composer.Companion.getEmpty()) {
                object2 = object4;
            } else {
                object2 = new BrowserWebViewKt$$ExternalSyntheticLambda8((Function1)object2, mutableState, mutableState2);
                composer.updateRememberedValue(object2);
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object4 = new KeyboardActions(null, object2, null, null, null, null, 61, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-129344647, (String)"CC(remember):BrowserWebView.kt#9igjgp");
            object2 = composer.rememberedValue();
            if (object2 == Composer.Companion.getEmpty()) {
                object2 = new BrowserWebViewKt$$ExternalSyntheticLambda9(mutableState, mutableState2);
                composer.updateRememberedValue(object2);
            }
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            BasicTextFieldKt.BasicTextField((String)object3, (Function1)object2, (Modifier)modifier2, (boolean)false, (boolean)false, (TextStyle)modifier, (KeyboardOptions)keyboardOptions, (KeyboardActions)object4, (boolean)true, (int)0, (int)0, null, null, null, (Brush)((Brush)function0), (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)98336300, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda10((BrowserUiState)object, appColors, mutableState, mutableState2), (Composer)composer, (int)54)), (Composer)composer, (int)102236208, (int)196608, (int)15896);
            if (BrowserWebViewKt.BrowserWebView$lambda$1((MutableState<Boolean>)mutableState) && (n = ((CharSequence)BrowserWebViewKt.BrowserWebView$lambda$4((MutableState<String>)mutableState2)).length() > 0 ? 1 : 0) != 0) {
                composer.startReplaceGroup(287574482);
                ComposerKt.sourceInformation((Composer)composer, (String)"160@7211L20,162@7335L401,159@7153L583");
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-129268419, (String)"CC(remember):BrowserWebView.kt#9igjgp");
                object = composer.rememberedValue();
                if (object == Composer.Companion.getEmpty()) {
                    object = new BrowserWebViewKt$$ExternalSyntheticLambda11(mutableState2);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                IconButtonKt.IconButton((Function0)object, (Modifier)SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)24)), (boolean)false, null, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-396895087, (boolean)true, (Object)new BrowserWebViewKt$$ExternalSyntheticLambda12(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)196662, (int)28);
            } else {
                composer.startReplaceGroup(280491385);
            }
            composer.endReplaceGroup();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$10$lambda$9(MutableState mutableState, MutableState mutableState2, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"it");
        BrowserWebViewKt.BrowserWebView$lambda$2((MutableState<Boolean>)mutableState, true);
        BrowserWebViewKt.BrowserWebView$lambda$5((MutableState<String>)mutableState2, string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$12(BrowserUiState browserUiState, AppColors appColors, MutableState mutableState, MutableState mutableState2, Function2 function2, Composer composer, int n) {
        int n2;
        Intrinsics.checkNotNullParameter((Object)function2, (String)"innerTextField");
        ComposerKt.sourceInformation((Composer)composer, (String)"C140@6035L944:BrowserWebView.kt#kaa2o3");
        int n3 = n2 = n;
        if ((n & 6) == 0) {
            n = composer.changedInstance((Object)function2) ? 4 : 2;
            n3 = n2 | n;
        }
        if ((n3 & 0x13) == 18 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)98336300, (int)n3, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (BrowserWebView.kt:140)");
            }
            Alignment alignment = Alignment.Companion.getCenterStart();
            Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)733328855, (String)"CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            MeasurePolicy measurePolicy = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)false);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            alignment = composer.getCurrentCompositionLocalMap();
            modifier = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)modifier);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function0);
            } else {
                composer.useNode();
            }
            function0 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function0, (Object)measurePolicy, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)alignment, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            alignment = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)alignment);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2146769399, (String)"C73@3429L9:Box.kt#2w3rfo");
            alignment = (BoxScope)BoxScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1330231685, (String)"C153@6925L16:BrowserWebView.kt#kaa2o3");
            if (BrowserWebViewKt.BrowserWebView$lambda$1((MutableState<Boolean>)mutableState) && (n = ((CharSequence)BrowserWebViewKt.BrowserWebView$lambda$4((MutableState<String>)mutableState2)).length() == 0 ? 1 : 0) != 0 || !BrowserWebViewKt.BrowserWebView$lambda$1((MutableState<Boolean>)mutableState) && (n = ((CharSequence)browserUiState.getCurrentUrl()).length() == 0 ? 1 : 0) != 0) {
                composer.startReplaceGroup(1330322173);
                ComposerKt.sourceInformation((Composer)composer, (String)"145@6419L423");
                long l = appColors.getTextSecondary-0d7_KjU();
                long l2 = TextUnitKt.getSp((int)12);
                n = TextOverflow.Companion.getEllipsis-gIe3tQ8();
                TextKt.Text--4IGK_g((String)"Search or enter address...", null, (long)l, (long)l2, null, null, null, (long)0L, null, null, (long)0L, (int)n, (boolean)false, (int)1, (int)0, null, null, (Composer)composer, (int)3078, (int)3120, (int)120818);
            } else {
                composer.startReplaceGroup(1323983324);
            }
            composer.endReplaceGroup();
            function2.invoke((Object)composer, (Object)(n3 & 0xE));
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$14$lambda$13(MutableState mutableState) {
        BrowserWebViewKt.BrowserWebView$lambda$5((MutableState<String>)mutableState, "");
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$15(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C163@7373L329:BrowserWebView.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-396895087, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (BrowserWebView.kt:163)");
            }
            ImageVector imageVector = CloseKt.getClose((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = appColors.getTextSecondary-0d7_KjU();
            appColors = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)12));
            IconKt.Icon-ww6aTOc((ImageVector)imageVector, (String)"Clear", (Modifier)appColors, (long)l, (Composer)composer, (int)432, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$17$lambda$16$lambda$8$lambda$7(Function1 function1, MutableState mutableState, MutableState mutableState2, KeyboardActionScope keyboardActionScope) {
        Intrinsics.checkNotNullParameter((Object)keyboardActionScope, (String)"$this$KeyboardActions");
        BrowserWebViewKt.BrowserWebView$lambda$2((MutableState<Boolean>)mutableState, false);
        function1.invoke((Object)BrowserWebViewKt.BrowserWebView$lambda$4((MutableState<String>)mutableState2));
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$19$lambda$18(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C182@8149L273:BrowserWebView.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)36562735, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (BrowserWebView.kt:182)");
            }
            ImageVector imageVector = RefreshKt.getRefresh((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = appColors.getIconTint-0d7_KjU();
            appColors = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)15));
            IconKt.Icon-ww6aTOc((ImageVector)imageVector, (String)"Refresh page", (Modifier)appColors, (long)l, (Composer)composer, (int)432, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final float BrowserWebView$lambda$32$lambda$23$lambda$22$lambda$21$lambda$20(BrowserUiState browserUiState) {
        return (float)browserUiState.getLoadingProgress() / 100.0f;
    }

    static final FrameLayout BrowserWebView$lambda$32$lambda$31$lambda$26$lambda$25(Context context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        context = new FrameLayout(context);
        context.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        return context;
    }

    static final Unit BrowserWebView$lambda$32$lambda$31$lambda$28$lambda$27(BrowserUiState browserUiState, WebView webView2, FrameLayout frameLayout) {
        Intrinsics.checkNotNullParameter((Object)frameLayout, (String)"container");
        boolean bl = browserUiState.getAppTheme() == AppTheme.FORCE_DARK_ON;
        if (MainActivity.Companion.isDarkThemeActive() != bl) {
            MainActivity.Companion.applyWebViewTheme(webView2, bl);
        }
        if (frameLayout.getChildCount() != 1 || frameLayout.getChildAt(0) != webView2) {
            browserUiState = webView2.getParent();
            browserUiState = browserUiState instanceof ViewGroup ? (ViewGroup)browserUiState : null;
            if (browserUiState != null) {
                browserUiState.removeView((View)webView2);
            }
            frameLayout.removeAllViews();
            browserUiState = (View)webView2;
            frameLayout.addView((View)browserUiState, new ViewGroup.LayoutParams(-1, -1));
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$32$lambda$31$lambda$30(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C248@10971L407:BrowserWebView.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1018211447, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.BrowserWebView.<anonymous>.<anonymous>.<anonymous> (BrowserWebView.kt:248)");
            }
            Alignment alignment = Alignment.Companion.getCenter();
            Modifier modifier = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)733328855, (String)"CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            alignment = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)alignment, (boolean)false);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            CompositionLocalMap compositionLocalMap = composer.getCurrentCompositionLocalMap();
            modifier = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)modifier);
            Function0 function0 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function0);
            } else {
                composer.useNode();
            }
            function0 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function0, (Object)alignment, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            alignment = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)alignment);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2146769399, (String)"C73@3429L9:Box.kt#2w3rfo");
            alignment = (BoxScope)BoxScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1158944015, (String)"C252@11143L213:BrowserWebView.kt#kaa2o3");
            alignment = (Modifier)Modifier.Companion;
            ProgressIndicatorKt.CircularProgressIndicator-LxG7B9w((Modifier)SizeKt.size-3ABfNKs((Modifier)alignment, (float)Dp.constructor-impl((float)32)), (long)appColors.getAccent-0d7_KjU(), (float)Dp.constructor-impl((float)((float)2.5)), (long)0L, (int)0, (Composer)composer, (int)390, (int)24);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserWebView$lambda$33(BrowserUiState browserUiState, WebView webView2, Function1 function1, Function0 function0, Modifier modifier, int n, int n2, Composer composer, int n3) {
        BrowserWebViewKt.BrowserWebView(browserUiState, webView2, (Function1<? super String, Unit>)function1, (Function0<Unit>)function0, modifier, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n | 1)), n2);
        return Unit.INSTANCE;
    }

    private static final String BrowserWebView$lambda$4(MutableState<String> mutableState) {
        return (String)((State)mutableState).getValue();
    }

    private static final void BrowserWebView$lambda$5(MutableState<String> mutableState, String string) {
        mutableState.setValue((Object)string);
    }

    public static final /* synthetic */ boolean access$BrowserWebView$lambda$1(MutableState mutableState) {
        return BrowserWebViewKt.BrowserWebView$lambda$1((MutableState<Boolean>)mutableState);
    }

    public static final /* synthetic */ void access$BrowserWebView$lambda$5(MutableState mutableState, String string) {
        BrowserWebViewKt.BrowserWebView$lambda$5((MutableState<String>)mutableState, string);
    }
}

