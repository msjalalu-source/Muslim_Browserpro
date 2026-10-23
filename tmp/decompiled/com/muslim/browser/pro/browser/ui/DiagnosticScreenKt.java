/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.ClipData
 *  android.content.ClipboardManager
 *  android.content.Context
 *  android.webkit.ValueCallback
 *  android.webkit.WebView
 *  android.widget.Toast
 *  androidx.compose.foundation.BorderStroke
 *  androidx.compose.foundation.BorderStrokeKt
 *  androidx.compose.foundation.ScrollKt
 *  androidx.compose.foundation.ScrollState
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Horizontal
 *  androidx.compose.foundation.layout.Arrangement$Vertical
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
 *  androidx.compose.material.icons.Icons
 *  androidx.compose.material.icons.Icons$Filled
 *  androidx.compose.material.icons.filled.BugReportKt
 *  androidx.compose.material.icons.filled.CloseKt
 *  androidx.compose.material.icons.filled.RefreshKt
 *  androidx.compose.material3.ButtonColors
 *  androidx.compose.material3.ButtonDefaults
 *  androidx.compose.material3.ButtonKt
 *  androidx.compose.material3.IconButtonKt
 *  androidx.compose.material3.IconKt
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
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.ColorKt
 *  androidx.compose.ui.graphics.Shape
 *  androidx.compose.ui.graphics.vector.ImageVector
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.platform.AndroidCompositionLocals_androidKt
 *  androidx.compose.ui.platform.TestTagKt
 *  androidx.compose.ui.text.font.FontFamily
 *  androidx.compose.ui.text.font.FontWeight
 *  androidx.compose.ui.unit.Dp
 *  androidx.compose.ui.unit.TextUnitKt
 *  com.muslim.browser.pro.browser.ui.ComposableSingletons$DiagnosticScreenKt
 *  com.muslim.browser.pro.ui.theme.AppColors
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
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.CoroutineScope
 *  org.json.JSONObject
 *  org.json.JSONTokener
 */
package com.muslim.browser.pro.browser.ui;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.layout.Arrangement;
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
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.BugReportKt;
import androidx.compose.material.icons.filled.CloseKt;
import androidx.compose.material.icons.filled.RefreshKt;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
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
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.ColorKt;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.text.font.FontFamily;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import com.muslim.browser.pro.browser.WebViewConfigurator;
import com.muslim.browser.pro.browser.ui.ComposableSingletons;
import com.muslim.browser.pro.browser.ui.DiagnosticData;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda10;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda2;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda3;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda4;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda5;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda6;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda7;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda8;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt$$ExternalSyntheticLambda9;
import com.muslim.browser.pro.ui.theme.AppColors;
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
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;
import org.json.JSONObject;
import org.json.JSONTokener;

@Metadata(d1={"\u0000F\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\u001a6\u0010\u0000\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00010\b\u001aA\u0010\n\u001a\u00020\u00012\b\u0010\u0002\u001a\u0004\u0018\u00010\u00032\u0006\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\f\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u0007\u00a2\u0006\u0002\u0010\u000f\u001a7\u0010\u0010\u001a\u00020\u00012\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\u00142\u0006\u0010\u0016\u001a\u00020\u00172\b\b\u0002\u0010\u0018\u001a\u00020\u0005H\u0003\u00a2\u0006\u0002\u0010\u0019\u00a8\u0006\u001a\u00b2\u0006\n\u0010\u001b\u001a\u00020\tX\u008a\u008e\u0002\u00b2\u0006\n\u0010\u001c\u001a\u00020\u0005X\u008a\u008e\u0002"}, d2={"collectLiveWebViewDiagnostics", "", "webView", "Landroid/webkit/WebView;", "isDesktopModeEnabled", "", "isWindows10TouchEnabled", "onResult", "Lkotlin/Function1;", "Lcom/muslim/browser/pro/browser/ui/DiagnosticData;", "DiagnosticScreen", "onDismiss", "Lkotlin/Function0;", "modifier", "Landroidx/compose/ui/Modifier;", "(Landroid/webkit/WebView;ZZLkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "DiagnosticItemCard", "index", "", "label", "", "value", "colors", "Lcom/muslim/browser/pro/ui/theme/AppColors;", "highlight", "(ILjava/lang/String;Ljava/lang/String;Lcom/muslim/browser/pro/ui/theme/AppColors;ZLandroidx/compose/runtime/Composer;II)V", "app", "diagnosticData", "isRefreshing"}, k=2, mv={2, 2, 0}, xi=48)
public final class DiagnosticScreenKt {
    private static final void DiagnosticItemCard(int n, String string, String string2, AppColors appColors, boolean bl, Composer composer, int n2, int n3) {
        block12: {
            boolean bl2;
            int n4;
            int n5;
            Composer composer2 = composer.startRestartGroup(1111472136);
            ComposerKt.sourceInformation((Composer)composer2, (String)"C(DiagnosticItemCard)P(2,3,4)646@28090L685,636@27705L1070:DiagnosticScreen.kt#kaa2o3");
            int n6 = n2;
            if ((n2 & 6) == 0) {
                n5 = composer2.changed(n) ? 4 : 2;
                n6 |= n5;
            }
            if ((n2 & 0x30) == 0) {
                n5 = composer2.changed((Object)string) ? 32 : 16;
                n6 |= n5;
            }
            if ((n2 & 0x180) == 0) {
                n5 = composer2.changed((Object)string2) ? 256 : 128;
                n6 |= n5;
            }
            if ((n2 & 0xC00) == 0) {
                n5 = composer2.changed((Object)appColors) ? 2048 : 1024;
                n6 |= n5;
            }
            if ((n4 = n3 & 0x10) != 0) {
                n6 |= 0x6000;
            } else if ((n2 & 0x6000) == 0) {
                n5 = composer2.changed(bl) ? 16384 : 8192;
                n6 |= n5;
            }
            if ((n6 & 0x2493) == 9362 && composer2.getSkipping()) {
                composer2.skipToGroupEnd();
                bl2 = bl;
            } else {
                if (n4 != 0) {
                    bl = false;
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)1111472136, (int)n6, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticItemCard (DiagnosticScreen.kt:635)");
                }
                Modifier modifier = PaddingKt.padding-VpY3zN4$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)0.0f, (float)Dp.constructor-impl((float)3), (int)1, null);
                Shape shape = (Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)6));
                long l = bl ? appColors.getSurfaceVariant-0d7_KjU() : appColors.getSurface-0d7_KjU();
                float f = Dp.constructor-impl((float)((float)0.5));
                long l2 = bl ? Color.copy-wmQWz5c$default((long)appColors.getAccent-0d7_KjU(), (float)0.5f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)appColors.getBorder-0d7_KjU(), (float)0.5f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                composer = BorderStrokeKt.BorderStroke-cXLIe8U((float)f, (long)l2);
                DiagnosticScreenKt$$ExternalSyntheticLambda0 diagnosticScreenKt$$ExternalSyntheticLambda0 = new DiagnosticScreenKt$$ExternalSyntheticLambda0(n, string, bl, appColors, string2);
                SurfaceKt.Surface-T9BRK9s((Modifier)modifier, (Shape)shape, (long)l, (long)0L, (float)0.0f, (float)0.0f, (BorderStroke)composer, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-1707600947, (boolean)true, (Object)diagnosticScreenKt$$ExternalSyntheticLambda0, (Composer)composer2, (int)54)), (Composer)composer2, (int)0xC00006, (int)56);
                bl2 = bl;
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventEnd();
                    bl2 = bl;
                }
            }
            composer = composer2.endRestartGroup();
            if (composer == null) break block12;
            composer.updateScope((Function2)new DiagnosticScreenKt$$ExternalSyntheticLambda1(n, string, string2, appColors, bl2, n2, n3));
        }
    }

    static final Unit DiagnosticItemCard$lambda$26(int n, String string, boolean bl, AppColors appColors, String charSequence, Composer composer, int n2) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C647@28100L669:DiagnosticScreen.kt#kaa2o3");
        if ((n2 & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1707600947, (int)n2, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticItemCard.<anonymous> (DiagnosticScreen.kt:647)");
            }
            Modifier modifier = PaddingKt.padding-VpY3zN4((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)10), (float)Dp.constructor-impl((float)6));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            Object object = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(6 >> 3 & 0xE | 6 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n2 = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
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
            Updater.set-impl((Composer)function0, (Object)object, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n2)) {
                function0.updateRememberedValue((Object)n2);
                function0.apply((Object)n2, (Function2)object);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-737330716, (String)"C648@28187L223,654@28423L40,655@28476L283:DiagnosticScreen.kt#kaa2o3");
            object = n + ". " + string;
            long l = TextUnitKt.getSp((int)11);
            string = FontWeight.Companion.getSemiBold();
            long l2 = bl ? appColors.getAccent-0d7_KjU() : appColors.getTextSecondary-0d7_KjU();
            TextKt.Text--4IGK_g((String)object, null, (long)l2, (long)l, null, (FontWeight)string, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)199680, (int)0, (int)131026);
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)2)), (Composer)composer, (int)6);
            string = charSequence = (CharSequence)charSequence;
            if (StringsKt.isBlank((CharSequence)charSequence)) {
                string = "(empty)";
            }
            charSequence = string;
            long l3 = TextUnitKt.getSp((int)12);
            object = FontFamily.Companion.getMonospace();
            string = FontWeight.Companion.getMedium();
            l2 = appColors.getTextPrimary-0d7_KjU();
            l = TextUnitKt.getSp((int)16);
            appColors = (FontFamily)object;
            TextKt.Text--4IGK_g((String)charSequence, null, (long)l2, (long)l3, null, (FontWeight)string, (FontFamily)appColors, (long)0L, null, null, (long)l, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)199680, (int)6, (int)129938);
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

    static final Unit DiagnosticItemCard$lambda$27(int n, String string, String string2, AppColors appColors, boolean bl, int n2, int n3, Composer composer, int n4) {
        DiagnosticScreenKt.DiagnosticItemCard(n, string, string2, appColors, bl, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n2 | 1)), n3);
        return Unit.INSTANCE;
    }

    public static final void DiagnosticScreen(WebView webView2, boolean bl, boolean bl2, Function0<Unit> function0, Modifier modifier, Composer object, int n, int n2) {
        int n3;
        int n4;
        int n5;
        Intrinsics.checkNotNullParameter(function0, (String)"onDismiss");
        Composer composer = object.startRestartGroup(-1496931595);
        ComposerKt.sourceInformation((Composer)composer, (String)"C(DiagnosticScreen)P(4!2,3)305@14572L7,306@14612L7,308@14647L45,309@14717L34,319@15018L29,319@14997L50,328@15208L12301,323@15053L12456:DiagnosticScreen.kt#kaa2o3");
        int n6 = n5 = n;
        if ((n & 6) == 0) {
            n6 = composer.changedInstance((Object)webView2) ? 4 : 2;
            n6 = n5 | n6;
        }
        if ((n & 0x30) == 0) {
            n5 = composer.changed(bl) ? 32 : 16;
            n6 |= n5;
        }
        if ((n4 = n2 & 4) != 0) {
            n6 |= 0x180;
        } else if ((n & 0x180) == 0) {
            n5 = composer.changed(bl2) ? 256 : 128;
            n6 |= n5;
        }
        n5 = n6;
        if ((n & 0xC00) == 0) {
            n5 = composer.changedInstance(function0) ? 2048 : 1024;
            n5 = n6 | n5;
        }
        if ((n3 = n2 & 0x10) != 0) {
            n6 = n5 | 0x6000;
        } else if ((n & 0x6000) == 0) {
            n6 = composer.changed((Object)modifier) ? 16384 : 8192;
            n6 = n5 | n6;
        } else {
            n6 = n5;
        }
        if ((n6 & 0x2493) == 9362 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (n4 != 0) {
                bl2 = false;
            }
            if (n3 != 0) {
                modifier = (Modifier)Modifier.Companion;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1496931595, (int)n6, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticScreen (DiagnosticScreen.kt:304)");
            }
            object = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalContext();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object = composer.consume((CompositionLocal)object);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            Context context = (Context)object;
            object = (CompositionLocal)ThemeKt.getLocalAppColors();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object = composer.consume((CompositionLocal)object);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            AppColors appColors = (AppColors)object;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)262791298, (String)"CC(remember):DiagnosticScreen.kt#9igjgp");
            object = composer.rememberedValue();
            if (object == Composer.Companion.getEmpty()) {
                object = SnapshotStateKt.mutableStateOf$default((Object)new DiagnosticData(null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, 0x1FFFFFF, null), null, (int)2, null);
                composer.updateRememberedValue(object);
            }
            MutableState mutableState = (MutableState)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)262793527, (String)"CC(remember):DiagnosticScreen.kt#9igjgp");
            object = composer.rememberedValue();
            Object object2 = Composer.Companion.getEmpty();
            n4 = 0;
            if (object == object2) {
                object = SnapshotStateKt.mutableStateOf$default((Object)false, null, (int)2, null);
                composer.updateRememberedValue(object);
            }
            object2 = (MutableState)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            Unit unit = Unit.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)262803154, (String)"CC(remember):DiagnosticScreen.kt#9igjgp");
            int n7 = composer.changedInstance((Object)webView2);
            n5 = (n6 & 0x70) == 32 ? 1 : 0;
            if ((n6 & 0x380) == 256) {
                n4 = 1;
            }
            object = composer.rememberedValue();
            if ((n7 | n5 | n4) != 0 || object == Composer.Companion.getEmpty()) {
                object = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(webView2, bl, bl2, (MutableState<Boolean>)object2, (MutableState<DiagnosticData>)mutableState, null){
                    final MutableState<DiagnosticData> $diagnosticData$delegate;
                    final boolean $isDesktopModeEnabled;
                    final MutableState<Boolean> $isRefreshing$delegate;
                    final boolean $isWindows10TouchEnabled;
                    final WebView $webView;
                    int label;
                    {
                        this.$webView = webView2;
                        this.$isDesktopModeEnabled = bl;
                        this.$isWindows10TouchEnabled = bl2;
                        this.$isRefreshing$delegate = mutableState;
                        this.$diagnosticData$delegate = mutableState2;
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
                        DiagnosticScreenKt.access$DiagnosticScreen$refreshData(this.$webView, this.$isDesktopModeEnabled, this.$isWindows10TouchEnabled, this.$isRefreshing$delegate, this.$diagnosticData$delegate);
                        return Unit.INSTANCE;
                    }
                };
                composer.updateRememberedValue(object);
            }
            object = (Function2)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            EffectsKt.LaunchedEffect((Object)unit, (Function2)object, (Composer)composer, (int)6);
            SurfaceKt.Surface-T9BRK9s((Modifier)TestTagKt.testTag((Modifier)SizeKt.fillMaxSize$default((Modifier)modifier, (float)0.0f, (int)1, null), (String)"diagnostic_screen_overlay"), null, (long)appColors.getBackground-0d7_KjU(), (long)0L, (float)0.0f, (float)0.0f, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)586799696, (boolean)true, (Object)new DiagnosticScreenKt$$ExternalSyntheticLambda2(function0, appColors, context, webView2, bl, bl2, mutableState, (MutableState)object2), (Composer)composer, (int)54)), (Composer)composer, (int)0xC00000, (int)122);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        object = composer.endRestartGroup();
        if (object != null) {
            object.updateScope((Function2)new DiagnosticScreenKt$$ExternalSyntheticLambda3(webView2, bl, bl2, function0, modifier, n, n2));
        }
    }

    private static final DiagnosticData DiagnosticScreen$lambda$2(MutableState<DiagnosticData> mutableState) {
        return (DiagnosticData)((State)mutableState).getValue();
    }

    static final Unit DiagnosticScreen$lambda$22(Function0 function0, AppColors appColors, Context object, WebView webView2, boolean bl, boolean bl2, MutableState mutableState, MutableState mutableState2, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C329@15218L12285:DiagnosticScreen.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)586799696, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticScreen.<anonymous> (DiagnosticScreen.kt:329)");
            }
            Modifier modifier = PaddingKt.padding-3ABfNKs((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)16));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            Object object2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(6 >> 3 & 0xE | 6 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            CompositionLocalMap compositionLocalMap = composer.getCurrentCompositionLocalMap();
            modifier = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)modifier);
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
            Updater.set-impl((Composer)function02, (Object)object2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n)) {
                function02.updateRememberedValue((Object)n);
                function02.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function02, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            modifier = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)727031522, (String)"C335@15390L2252,383@17656L41,386@17771L2383,437@20168L41,444@20413L21,440@20265L6612,617@27227L146,610@26927L566:DiagnosticScreen.kt#kaa2o3");
            compositionLocalMap = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            object2 = Alignment.Companion.getCenterVertically();
            function02 = Arrangement.INSTANCE.getSpaceBetween();
            function02 = (Arrangement.Horizontal)function02;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            function02 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)function02, (Alignment.Vertical)object2, (Composer)composer, (int)(438 >> 3 & 0xE | 438 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            object2 = composer.getCurrentCompositionLocalMap();
            compositionLocalMap = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)compositionLocalMap);
            Function0 function03 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function03);
            } else {
                composer.useNode();
            }
            function03 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function03, (Object)function02, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function03, (Object)object2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function03.getInserting() || !Intrinsics.areEqual((Object)function03.rememberedValue(), (Object)n)) {
                function03.updateRememberedValue((Object)n);
                function03.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function03, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            object2 = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)251385449, (String)"C340@15608L1608,374@17380L248,371@17234L394:DiagnosticScreen.kt#kaa2o3");
            object2 = Alignment.Companion.getCenterVertically();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            function02 = (Modifier)Modifier.Companion;
            object2 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)Arrangement.INSTANCE.getStart(), (Alignment.Vertical)object2, (Composer)composer, (int)(384 >> 3 & 0xE | 384 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            compositionLocalMap = composer.getCurrentCompositionLocalMap();
            function02 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)function02);
            function03 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function03);
            } else {
                composer.useNode();
            }
            function03 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function03, (Object)object2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function03, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function03.getInserting() || !Intrinsics.areEqual((Object)function03.rememberedValue(), (Object)n)) {
                function03.updateRememberedValue((Object)n);
                function03.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function03, (Object)function02, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            object2 = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)407303493, (String)"C341@15682L257,347@15960L39,348@16020L1178:DiagnosticScreen.kt#kaa2o3");
            object2 = BugReportKt.getBugReport((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = appColors.getAccent-0d7_KjU();
            compositionLocalMap = (Modifier)Modifier.Companion;
            compositionLocalMap = SizeKt.size-3ABfNKs((Modifier)compositionLocalMap, (float)Dp.constructor-impl((float)24));
            IconKt.Icon-ww6aTOc((ImageVector)object2, (String)"Diagnostic Debug", (Modifier)compositionLocalMap, (long)l, (Composer)composer, (int)432, (int)0);
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)8)), (Composer)composer, (int)6);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            object2 = (Modifier)Modifier.Companion;
            compositionLocalMap = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            function02 = composer.getCurrentCompositionLocalMap();
            object2 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object2);
            function03 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function03);
            } else {
                composer.useNode();
            }
            function03 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function03, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function03, (Object)function02, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            compositionLocalMap = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function03.getInserting() || !Intrinsics.areEqual((Object)function03.rememberedValue(), (Object)n)) {
                function03.updateRememberedValue((Object)n);
                function03.apply((Object)n, (Function2)compositionLocalMap);
            }
            Updater.set-impl((Composer)function03, (Object)object2, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object2 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1464891868, (String)"C353@16406L480,349@16053L833,362@16911L265:DiagnosticScreen.kt#kaa2o3");
            object2 = (Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)4));
            l = appColors.isMonochrome() ? Color.copy-wmQWz5c$default((long)appColors.getBorder-0d7_KjU(), (float)0.3f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null) : Color.copy-wmQWz5c$default((long)ColorKt.Color((long)4293874512L), (float)0.2f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
            float f = Dp.constructor-impl((float)1.0f);
            long l2 = appColors.isMonochrome() ? appColors.getBorder-0d7_KjU() : ColorKt.Color((long)4293874512L);
            SurfaceKt.Surface-T9BRK9s(null, (Shape)object2, (long)l, (long)0L, (float)0.0f, (float)0.0f, (BorderStroke)BorderStrokeKt.BorderStroke-cXLIe8U((float)f, (long)l2), (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)2139621793, (boolean)true, (Object)new DiagnosticScreenKt$$ExternalSyntheticLambda5(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)0xC00000, (int)57);
            TextKt.Text--4IGK_g((String)"Browser Signals & Viewport Inspector", null, (long)appColors.getTextPrimary-0d7_KjU(), (long)TextUnitKt.getSp((int)14), null, (FontWeight)FontWeight.Companion.getBold(), null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)199686, (int)0, (int)131026);
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
            IconButtonKt.IconButton((Function0)function0, (Modifier)TestTagKt.testTag((Modifier)((Modifier)Modifier.Companion), (String)"close_diagnostic_button"), (boolean)false, null, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-2091149575, (boolean)true, (Object)new DiagnosticScreenKt$$ExternalSyntheticLambda6(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)196656, (int)28);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)10)), (Composer)composer, (int)6);
            function02 = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            object2 = (Arrangement.Horizontal)Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl((float)8));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            object2 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)object2, (Alignment.Vertical)Alignment.Companion.getTop(), (Composer)composer, (int)(54 >> 3 & 0xE | 54 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            compositionLocalMap = composer.getCurrentCompositionLocalMap();
            function02 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)function02);
            function03 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function03);
            } else {
                composer.useNode();
            }
            function03 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function03, (Object)object2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function03, (Object)compositionLocalMap, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function03.getInserting() || !Intrinsics.areEqual((Object)function03.rememberedValue(), (Object)n)) {
                function03.updateRememberedValue((Object)n);
                function03.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function03, (Object)function02, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            compositionLocalMap = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1012436159, (String)"C391@17965L420,402@18660L158,390@17927L1305,417@19296L17,422@19555L101,426@19739L401,416@19250L890:DiagnosticScreen.kt#kaa2o3");
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1279584643, (String)"CC(remember):DiagnosticScreen.kt#9igjgp");
            boolean bl3 = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (!bl3 && object2 != Composer.Companion.getEmpty()) {
                object = object2;
            } else {
                object = new DiagnosticScreenKt$$ExternalSyntheticLambda7((Context)object, mutableState);
                composer.updateRememberedValue(object);
            }
            object = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ButtonKt.Button((Function0)object, (Modifier)TestTagKt.testTag((Modifier)SizeKt.height-3ABfNKs((Modifier)RowScope.weight$default((RowScope)compositionLocalMap, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)Dp.constructor-impl((float)36)), (String)"copy_diagnostic_button"), (boolean)false, (Shape)((Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)6))), (ButtonColors)ButtonDefaults.INSTANCE.buttonColors-ro_MJ88(appColors.getButtonBackground-0d7_KjU(), appColors.getButtonText-0d7_KjU(), 0L, 0L, composer, ButtonDefaults.$stable << 12, 12), null, null, null, null, (Function3)ComposableSingletons.DiagnosticScreenKt.INSTANCE.getLambda$-1566976337$app(), (Composer)composer, (int)0x30000000, (int)484);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1279626832, (String)"CC(remember):DiagnosticScreen.kt#9igjgp");
            boolean bl4 = composer.changedInstance((Object)webView2);
            boolean bl5 = composer.changed(bl);
            bl3 = composer.changed(bl2);
            object = composer.rememberedValue();
            if (bl4 | bl5 | bl3 || object == Composer.Companion.getEmpty()) {
                object = new DiagnosticScreenKt$$ExternalSyntheticLambda8(webView2, bl, bl2, mutableState2, mutableState);
                composer.updateRememberedValue(object);
            }
            object = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ButtonKt.OutlinedButton((Function0)object, (Modifier)TestTagKt.testTag((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)36)), (String)"refresh_diagnostic_button"), (boolean)false, (Shape)((Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)6))), (ButtonColors)ButtonDefaults.INSTANCE.outlinedButtonColors-ro_MJ88(0L, appColors.getTextPrimary-0d7_KjU(), 0L, 0L, composer, ButtonDefaults.$stable << 12, 13), null, (BorderStroke)BorderStrokeKt.BorderStroke-cXLIe8U((float)Dp.constructor-impl((float)1.0f), (long)appColors.getBorder-0d7_KjU()), null, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)1210981613, (boolean)true, (Object)new DiagnosticScreenKt$$ExternalSyntheticLambda9(mutableState2), (Composer)composer, (int)54)), (Composer)composer, (int)0x30000030, (int)420);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)12)), (Composer)composer, (int)6);
            mutableState2 = ScrollKt.verticalScroll$default((Modifier)SizeKt.fillMaxWidth$default((Modifier)ColumnScope.weight$default((ColumnScope)modifier, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null), (float)0.0f, (int)1, null), (ScrollState)ScrollKt.rememberScrollState((int)0, (Composer)composer, (int)0, (int)1), (boolean)false, null, (boolean)false, (int)14, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            webView2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            object = composer.getCurrentCompositionLocalMap();
            mutableState2 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)mutableState2);
            object2 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode((Function0)object2);
            } else {
                composer.useNode();
            }
            object2 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)object2, (Object)webView2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)object2, (Object)object, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (object2.getInserting() || !Intrinsics.areEqual((Object)object2.rememberedValue(), (Object)n)) {
                object2.updateRememberedValue((Object)n);
                object2.apply((Object)n, (Function2)object);
            }
            Updater.set-impl((Composer)object2, (Object)mutableState2, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1421712068, (String)"C446@20468L273,453@20758L214,459@20989L216,465@21222L214,471@21453L216,477@21686L274,484@21977L238,490@22232L204,496@22453L206,502@22676L221,508@22914L257,515@23188L231,521@23436L229,527@23682L288,534@23987L218,540@24222L262,547@24501L255,554@24773L221,560@25011L213,566@25241L239,572@25497L225,578@25739L258,585@26014L262,592@26293L229,598@26539L265,606@26822L41:DiagnosticScreen.kt#kaa2o3");
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWebViewUserAgentString();
            DiagnosticScreenKt.DiagnosticItemCard(1, "webView.settings.userAgentString", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWindowInnerWidth();
            DiagnosticScreenKt.DiagnosticItemCard(2, "window.innerWidth", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWindowInnerHeight();
            DiagnosticScreenKt.DiagnosticItemCard(3, "window.innerHeight", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWindowOuterWidth();
            DiagnosticScreenKt.DiagnosticItemCard(4, "window.outerWidth", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWindowOuterHeight();
            DiagnosticScreenKt.DiagnosticItemCard(5, "window.outerHeight", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getDocumentClientWidth();
            DiagnosticScreenKt.DiagnosticItemCard(6, "document.documentElement.clientWidth", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getDocumentClientHeight();
            DiagnosticScreenKt.DiagnosticItemCard(7, "document.documentElement.clientHeight", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getScreenWidth();
            DiagnosticScreenKt.DiagnosticItemCard(8, "screen.width", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getScreenHeight();
            DiagnosticScreenKt.DiagnosticItemCard(9, "screen.height", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getDevicePixelRatio();
            DiagnosticScreenKt.DiagnosticItemCard(10, "window.devicePixelRatio", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorUserAgent();
            DiagnosticScreenKt.DiagnosticItemCard(11, "navigator.userAgent", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getUserAgentDataMobile();
            DiagnosticScreenKt.DiagnosticItemCard(12, "navigator.userAgentData.mobile", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorMaxTouchPoints();
            DiagnosticScreenKt.DiagnosticItemCard(13, "navigator.maxTouchPoints", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getViewportMetaContent();
            DiagnosticScreenKt.DiagnosticItemCard(14, "document.querySelector('meta[name=\"viewport\"]')", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getCurrentWebViewUrl();
            DiagnosticScreenKt.DiagnosticItemCard(15, "Current WebView URL", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getDesktopModeState();
            DiagnosticScreenKt.DiagnosticItemCard(16, "Current Desktop Mode State", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorPlatform();
            DiagnosticScreenKt.DiagnosticItemCard(17, "navigator.platform", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorAppVersion();
            DiagnosticScreenKt.DiagnosticItemCard(18, "navigator.appVersion", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorVendor();
            DiagnosticScreenKt.DiagnosticItemCard(19, "navigator.vendor", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorHardwareConcurrency();
            DiagnosticScreenKt.DiagnosticItemCard(20, "navigator.hardwareConcurrency", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getNavigatorDeviceMemory();
            DiagnosticScreenKt.DiagnosticItemCard(21, "navigator.deviceMemory", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWebglVendor();
            DiagnosticScreenKt.DiagnosticItemCard(22, "WebGL UNMASKED_VENDOR_WEBGL", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getWebglRenderer();
            DiagnosticScreenKt.DiagnosticItemCard(23, "WebGL UNMASKED_RENDERER_WEBGL", (String)object, appColors, true, composer, 24630, 0);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getCssPointerHover();
            DiagnosticScreenKt.DiagnosticItemCard(24, "CSS Pointer / Hover Capabilities", (String)object, appColors, false, composer, 54, 16);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).getUaClientHintsPlatform();
            DiagnosticScreenKt.DiagnosticItemCard(25, "UA Client Hints Platform", (String)object, appColors, true, composer, 24630, 0);
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)16)), (Composer)composer, (int)6);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ButtonKt.Button((Function0)function0, (Modifier)TestTagKt.testTag((Modifier)SizeKt.height-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)44)), (String)"diagnostic_return_button"), (boolean)false, (Shape)((Shape)RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)8))), (ButtonColors)ButtonDefaults.INSTANCE.buttonColors-ro_MJ88(appColors.getButtonBackground-0d7_KjU(), appColors.getButtonText-0d7_KjU(), 0L, 0L, composer, ButtonDefaults.$stable << 12, 12), null, null, null, null, (Function3)ComposableSingletons.DiagnosticScreenKt.INSTANCE.getLambda$778689322$app(), (Composer)composer, (int)0x30000030, (int)484);
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

    static final Unit DiagnosticScreen$lambda$22$lambda$21$lambda$13$lambda$11$lambda$10$lambda$9(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C354@16436L424:DiagnosticScreen.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)2139621793, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous>.<anonymous> (DiagnosticScreen.kt:354)");
            }
            long l = appColors.isMonochrome() ? appColors.getTextPrimary-0d7_KjU() : ColorKt.Color((long)4293874512L);
            long l2 = TextUnitKt.getSp((int)10);
            appColors = FontWeight.Companion.getBold();
            Modifier modifier = PaddingKt.padding-VpY3zN4((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)6), (float)Dp.constructor-impl((float)2));
            TextKt.Text--4IGK_g((String)"IDENTITY & VIEWPORT DIAGNOSTIC", (Modifier)modifier, (long)l, (long)l2, null, (FontWeight)appColors, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)199734, (int)0, (int)131024);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit DiagnosticScreen$lambda$22$lambda$21$lambda$13$lambda$12(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C375@17402L208:DiagnosticScreen.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-2091149575, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous> (DiagnosticScreen.kt:375)");
            }
            ImageVector imageVector = CloseKt.getClose((Icons.Filled)Icons.INSTANCE.getDefault());
            IconKt.Icon-ww6aTOc((ImageVector)imageVector, (String)"Close Diagnostic Screen", null, (long)appColors.getTextPrimary-0d7_KjU(), (Composer)composer, (int)48, (int)4);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit DiagnosticScreen$lambda$22$lambda$21$lambda$19$lambda$15$lambda$14(Context context, MutableState mutableState) {
        Object object = context.getSystemService("clipboard");
        object = object instanceof ClipboardManager ? (ClipboardManager)object : null;
        mutableState = ClipData.newPlainText((CharSequence)"Diagnostic Data", (CharSequence)DiagnosticScreenKt.DiagnosticScreen$lambda$2((MutableState<DiagnosticData>)mutableState).formatForClipboard());
        if (object != null) {
            object.setPrimaryClip((ClipData)mutableState);
        }
        Toast.makeText((Context)context, (CharSequence)"Diagnostic data copied to clipboard!", (int)0).show();
        return Unit.INSTANCE;
    }

    static final Unit DiagnosticScreen$lambda$22$lambda$21$lambda$19$lambda$17$lambda$16(WebView webView2, boolean bl, boolean bl2, MutableState mutableState, MutableState mutableState2) {
        DiagnosticScreenKt.DiagnosticScreen$refreshData(webView2, bl, bl2, (MutableState<Boolean>)mutableState, (MutableState<DiagnosticData>)mutableState2);
        return Unit.INSTANCE;
    }

    static final Unit DiagnosticScreen$lambda$22$lambda$21$lambda$19$lambda$18(MutableState object, RowScope rowScope, Composer composer, int n) {
        Intrinsics.checkNotNullParameter((Object)rowScope, (String)"$this$OutlinedButton");
        ComposerKt.sourceInformation((Composer)composer, (String)"C427@19761L211,432@19993L39,433@20053L69:DiagnosticScreen.kt#kaa2o3");
        if ((n & 0x11) == 16 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1210981613, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.DiagnosticScreen.<anonymous>.<anonymous>.<anonymous>.<anonymous> (DiagnosticScreen.kt:427)");
            }
            rowScope = RefreshKt.getRefresh((Icons.Filled)Icons.INSTANCE.getDefault());
            IconKt.Icon-ww6aTOc((ImageVector)rowScope, (String)"Refresh Diagnostic", (Modifier)SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)14)), (long)0L, (Composer)composer, (int)432, (int)8);
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)4)), (Composer)composer, (int)6);
            object = DiagnosticScreenKt.DiagnosticScreen$lambda$5((MutableState<Boolean>)object) ? "Reading..." : "Refresh";
            TextKt.Text--4IGK_g((String)object, null, (long)0L, (long)TextUnitKt.getSp((int)12), null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)3072, (int)0, (int)131062);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit DiagnosticScreen$lambda$23(WebView webView2, boolean bl, boolean bl2, Function0 function0, Modifier modifier, int n, int n2, Composer composer, int n3) {
        DiagnosticScreenKt.DiagnosticScreen(webView2, bl, bl2, (Function0<Unit>)function0, modifier, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n | 1)), n2);
        return Unit.INSTANCE;
    }

    private static final void DiagnosticScreen$lambda$3(MutableState<DiagnosticData> mutableState, DiagnosticData diagnosticData) {
        mutableState.setValue((Object)diagnosticData);
    }

    private static final boolean DiagnosticScreen$lambda$5(MutableState<Boolean> mutableState) {
        return (Boolean)((State)mutableState).getValue();
    }

    private static final void DiagnosticScreen$lambda$6(MutableState<Boolean> mutableState, boolean bl) {
        mutableState.setValue((Object)bl);
    }

    private static final void DiagnosticScreen$refreshData(WebView webView2, boolean bl, boolean bl2, MutableState<Boolean> mutableState, MutableState<DiagnosticData> mutableState2) {
        DiagnosticScreenKt.DiagnosticScreen$lambda$6(mutableState, true);
        DiagnosticScreenKt.collectLiveWebViewDiagnostics(webView2, bl, bl2, new DiagnosticScreenKt$$ExternalSyntheticLambda4(mutableState2, mutableState));
    }

    static final Unit DiagnosticScreen$refreshData$lambda$7(MutableState mutableState, MutableState mutableState2, DiagnosticData diagnosticData) {
        Intrinsics.checkNotNullParameter((Object)diagnosticData, (String)"data");
        DiagnosticScreenKt.DiagnosticScreen$lambda$3((MutableState<DiagnosticData>)mutableState, diagnosticData);
        DiagnosticScreenKt.DiagnosticScreen$lambda$6((MutableState<Boolean>)mutableState2, false);
        return Unit.INSTANCE;
    }

    public static final /* synthetic */ void access$DiagnosticScreen$refreshData(WebView webView2, boolean bl, boolean bl2, MutableState mutableState, MutableState mutableState2) {
        DiagnosticScreenKt.DiagnosticScreen$refreshData(webView2, bl, bl2, (MutableState<Boolean>)mutableState, (MutableState<DiagnosticData>)mutableState2);
    }

    public static final void collectLiveWebViewDiagnostics(WebView webView2, boolean bl, boolean bl2, Function1<? super DiagnosticData, Unit> function1) {
        String string;
        String string2;
        Object object = "";
        Intrinsics.checkNotNullParameter(function1, (String)"onResult");
        String string3 = bl2 ? "WINDOWS 10 TOUCH PROFILE" : (bl ? "DESKTOP MODE" : "MOBILE MODE");
        if (webView2 == null) {
            function1.invoke((Object)new DiagnosticData(null, "Error: WebView is null", null, null, null, null, null, null, null, null, null, null, null, null, null, string3, null, null, null, null, null, null, null, null, null, 33521661, null));
            return;
        }
        if (bl2) {
            WebViewConfigurator.INSTANCE.injectWindows10TouchProfileIfEnabled(webView2, true);
        }
        try {
            string = string2 = webView2.getSettings().getUserAgentString();
            if (string2 == null) {
                string = "";
            }
            string2 = string;
        }
        catch (Exception exception) {
            string2 = "";
        }
        try {
            string = webView2.getUrl();
            if (string == null) {
                string = object;
            }
        }
        catch (Exception exception) {
            string = "";
        }
        try {
            object = new DiagnosticScreenKt$$ExternalSyntheticLambda10(function1, string2, string, string3);
            webView2.evaluateJavascript("(function() {\n    try {\n        var vp = document.querySelector('meta[name=\"viewport\"]');\n        var vpContent = vp ? vp.getAttribute('content') : 'NONE (No meta[name=viewport] tag found)';\n        var uadMobile = 'Not available (navigator.userAgentData undefined)';\n        var uadPlatform = 'Not available';\n        if (window.navigator && window.navigator.userAgentData) {\n            if (typeof window.navigator.userAgentData.mobile !== 'undefined') {\n                uadMobile = String(window.navigator.userAgentData.mobile);\n            }\n            if (typeof window.navigator.userAgentData.platform !== 'undefined') {\n                uadPlatform = String(window.navigator.userAgentData.platform);\n            }\n        }\n        var wglVendor = 'N/A';\n        var wglRenderer = 'N/A';\n        try {\n            var canvas = document.createElement('canvas');\n            var gl = canvas.getContext('webgl') || canvas.getContext('experimental-webgl');\n            if (gl) {\n                var ext = gl.getExtension('WEBGL_debug_renderer_info');\n                if (ext) {\n                    wglVendor = String(gl.getParameter(ext.UNMASKED_VENDOR_WEBGL));\n                    wglRenderer = String(gl.getParameter(ext.UNMASKED_RENDERER_WEBGL));\n                }\n            }\n        } catch(e) {}\n\n        var cssSignals = [];\n        try {\n            if (window.matchMedia) {\n                cssSignals.push('hover:' + window.matchMedia('(hover: hover)').matches);\n                cssSignals.push('pointer:fine=' + window.matchMedia('(pointer: fine)').matches);\n                cssSignals.push('any-pointer:coarse=' + window.matchMedia('(any-pointer: coarse)').matches);\n            }\n        } catch(e) {}\n\n        return JSON.stringify({\n            innerWidth: String(window.innerWidth),\n            innerHeight: String(window.innerHeight),\n            outerWidth: String(window.outerWidth),\n            outerHeight: String(window.outerHeight),\n            clientWidth: String(document.documentElement ? document.documentElement.clientWidth : 'N/A'),\n            clientHeight: String(document.documentElement ? document.documentElement.clientHeight : 'N/A'),\n            screenWidth: String(window.screen ? window.screen.width : 'N/A'),\n            screenHeight: String(window.screen ? window.screen.height : 'N/A'),\n            devicePixelRatio: String(window.devicePixelRatio),\n            navigatorUserAgent: String(window.navigator ? window.navigator.userAgent : 'N/A'),\n            userAgentDataMobile: uadMobile,\n            userAgentDataPlatform: uadPlatform,\n            maxTouchPoints: String(window.navigator ? window.navigator.maxTouchPoints : 'N/A'),\n            viewportMeta: vpContent,\n            platform: String(window.navigator ? window.navigator.platform : 'N/A'),\n            appVersion: String(window.navigator ? window.navigator.appVersion : 'N/A'),\n            vendor: String(window.navigator ? window.navigator.vendor : 'N/A'),\n            hardwareConcurrency: String(window.navigator ? window.navigator.hardwareConcurrency : 'N/A'),\n            deviceMemory: String(window.navigator ? window.navigator.deviceMemory : 'N/A'),\n            webglVendor: wglVendor,\n            webglRenderer: wglRenderer,\n            cssPointerHover: cssSignals.join(', ')\n        });\n    } catch(e) {\n        return JSON.stringify({ error: e.toString() });\n    }\n})()", (ValueCallback)object);
        }
        catch (Exception exception) {
            String string4 = exception.getMessage();
            string4 = "Execution Error: " + string4;
            function1.invoke((Object)new DiagnosticData(string2, string4, null, null, null, null, null, null, null, null, null, null, null, null, string, string3, null, null, null, null, null, null, null, null, null, 33505276, null));
        }
    }

    public static /* synthetic */ void collectLiveWebViewDiagnostics$default(WebView webView2, boolean bl, boolean bl2, Function1 function1, int n, Object object) {
        if ((n & 4) != 0) {
            bl2 = false;
        }
        DiagnosticScreenKt.collectLiveWebViewDiagnostics(webView2, bl, bl2, (Function1<? super DiagnosticData, Unit>)function1);
    }

    static final void collectLiveWebViewDiagnostics$lambda$0(Function1 function1, String string, String string2, String string3, String string4) {
        if (string4 != null && !Intrinsics.areEqual((Object)string4, (Object)"null")) {
            try {
                Object object;
                if (StringsKt.startsWith$default((String)string4, (String)"\"", (boolean)false, (int)2, null) && StringsKt.endsWith$default((String)string4, (String)"\"", (boolean)false, (int)2, null)) {
                    object = new JSONTokener(string4);
                    string4 = object.nextValue().toString();
                }
                Object object2 = new JSONObject(string4);
                String string5 = object2.optString("innerWidth", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string5, (String)"optString(...)");
                String string6 = object2.optString("innerHeight", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string6, (String)"optString(...)");
                String string7 = object2.optString("outerWidth", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string7, (String)"optString(...)");
                String string8 = object2.optString("outerHeight", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string8, (String)"optString(...)");
                String string9 = object2.optString("clientWidth", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string9, (String)"optString(...)");
                object = object2.optString("clientHeight", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)object, (String)"optString(...)");
                String string10 = object2.optString("screenWidth", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string10, (String)"optString(...)");
                string4 = object2.optString("screenHeight", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string4, (String)"optString(...)");
                String string11 = object2.optString("devicePixelRatio", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string11, (String)"optString(...)");
                String string12 = object2.optString("navigatorUserAgent", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string12, (String)"optString(...)");
                String string13 = object2.optString("userAgentDataMobile", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string13, (String)"optString(...)");
                String string14 = object2.optString("maxTouchPoints", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string14, (String)"optString(...)");
                String string15 = object2.optString("viewportMeta", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string15, (String)"optString(...)");
                String string16 = object2.optString("platform", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string16, (String)"optString(...)");
                String string17 = object2.optString("appVersion", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string17, (String)"optString(...)");
                String string18 = object2.optString("vendor", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string18, (String)"optString(...)");
                String string19 = object2.optString("hardwareConcurrency", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string19, (String)"optString(...)");
                String string20 = object2.optString("deviceMemory", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string20, (String)"optString(...)");
                String string21 = object2.optString("webglVendor", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string21, (String)"optString(...)");
                String string22 = object2.optString("webglRenderer", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string22, (String)"optString(...)");
                String string23 = object2.optString("cssPointerHover", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)string23, (String)"optString(...)");
                object2 = object2.optString("userAgentDataPlatform", "N/A");
                Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"optString(...)");
                DiagnosticData diagnosticData = new DiagnosticData(string, string5, string6, string7, string8, string9, (String)object, string10, string4, string11, string12, string13, string14, string15, string2, string3, string16, string17, string18, string19, string20, string21, string22, string23, (String)object2);
                function1.invoke((Object)diagnosticData);
            }
            catch (Exception exception) {
                String string24 = exception.getMessage();
                string24 = "Parse Error: " + string24;
                function1.invoke((Object)new DiagnosticData(string, string24, null, null, null, null, null, null, null, null, null, null, null, null, string2, string3, null, null, null, null, null, null, null, null, null, 33505276, null));
            }
            return;
        }
        function1.invoke((Object)new DiagnosticData(string, "Unavailable (evaluateJavascript returned null)", null, null, null, null, null, null, null, null, null, null, null, null, string2, string3, null, null, null, null, null, null, null, null, null, 33505276, null));
    }
}

