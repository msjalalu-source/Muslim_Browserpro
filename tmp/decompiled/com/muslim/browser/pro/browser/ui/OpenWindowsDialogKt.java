/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.foundation.BackgroundKt
 *  androidx.compose.foundation.BorderStroke
 *  androidx.compose.foundation.BorderStrokeKt
 *  androidx.compose.foundation.ClickableKt
 *  androidx.compose.foundation.ScrollKt
 *  androidx.compose.foundation.ScrollState
 *  androidx.compose.foundation.interaction.InteractionSourceKt
 *  androidx.compose.foundation.interaction.MutableInteractionSource
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Horizontal
 *  androidx.compose.foundation.layout.Arrangement$HorizontalOrVertical
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
 *  androidx.compose.foundation.shape.RoundedCornerShape
 *  androidx.compose.foundation.shape.RoundedCornerShapeKt
 *  androidx.compose.material.icons.Icons
 *  androidx.compose.material.icons.Icons$Filled
 *  androidx.compose.material.icons.filled.CloseKt
 *  androidx.compose.material.icons.filled.HomeKt
 *  androidx.compose.material.icons.filled.LanguageKt
 *  androidx.compose.material3.IconButtonKt
 *  androidx.compose.material3.IconKt
 *  androidx.compose.material3.SurfaceKt
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Applier
 *  androidx.compose.runtime.ComposablesKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.Updater
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.ui.Alignment
 *  androidx.compose.ui.Alignment$Horizontal
 *  androidx.compose.ui.Alignment$Vertical
 *  androidx.compose.ui.ComposedModifierKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.graphics.Shape
 *  androidx.compose.ui.graphics.vector.ImageVector
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.platform.TestTagKt
 *  androidx.compose.ui.text.font.FontWeight
 *  androidx.compose.ui.text.style.TextOverflow
 *  androidx.compose.ui.unit.Dp
 *  androidx.compose.ui.unit.TextUnitKt
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  com.muslim.browser.pro.ui.theme.ThemeKt
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser.ui;

import androidx.compose.foundation.BackgroundKt;
import androidx.compose.foundation.BorderStroke;
import androidx.compose.foundation.BorderStrokeKt;
import androidx.compose.foundation.ClickableKt;
import androidx.compose.foundation.ScrollKt;
import androidx.compose.foundation.ScrollState;
import androidx.compose.foundation.interaction.InteractionSourceKt;
import androidx.compose.foundation.interaction.MutableInteractionSource;
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
import androidx.compose.foundation.shape.RoundedCornerShape;
import androidx.compose.foundation.shape.RoundedCornerShapeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.CloseKt;
import androidx.compose.material.icons.filled.HomeKt;
import androidx.compose.material.icons.filled.LanguageKt;
import androidx.compose.material3.IconButtonKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.SurfaceKt;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.graphics.Shape;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.TestTagKt;
import androidx.compose.ui.text.font.FontWeight;
import androidx.compose.ui.text.style.TextOverflow;
import androidx.compose.ui.unit.Dp;
import androidx.compose.ui.unit.TextUnitKt;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda2;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda3;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda4;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda5;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda6;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda7;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda8;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt$$ExternalSyntheticLambda9;
import com.muslim.browser.pro.ui.theme.AppColors;
import com.muslim.browser.pro.ui.theme.ThemeKt;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u00008\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\u001ae\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0012\u0010\u0004\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u0012\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\u00052\u000e\b\u0002\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\n\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u0007\u00a2\u0006\u0002\u0010\r\u001aA\u0010\u000e\u001a\u00020\u00012\u0006\u0010\u000f\u001a\u00020\u00102\u0006\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00122\f\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00010\t2\f\u0010\u0015\u001a\b\u0012\u0004\u0012\u00020\u00010\tH\u0003\u00a2\u0006\u0002\u0010\u0016\u00a8\u0006\u0017"}, d2={"OpenWindowsDialog", "", "uiState", "Lcom/muslim/browser/pro/browser/BrowserUiState;", "onSelectTab", "Lkotlin/Function1;", "", "onCloseTab", "onNewTab", "Lkotlin/Function0;", "onDismiss", "modifier", "Landroidx/compose/ui/Modifier;", "(Lcom/muslim/browser/pro/browser/BrowserUiState;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/ui/Modifier;Landroidx/compose/runtime/Composer;II)V", "WindowTabItem", "tab", "Lcom/muslim/browser/pro/browser/BrowserTab;", "isActive", "", "canClose", "onClick", "onClose", "(Lcom/muslim/browser/pro/browser/BrowserTab;ZZLkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Landroidx/compose/runtime/Composer;I)V", "app"}, k=2, mv={2, 2, 0}, xi=48)
public final class OpenWindowsDialogKt {
    public static final void OpenWindowsDialog(BrowserUiState browserUiState, Function1<? super String, Unit> function1, Function1<? super String, Unit> function12, Function0<Unit> object, Function0<Unit> function0, Modifier modifier, Composer object2, int n, int n2) {
        int n3;
        Intrinsics.checkNotNullParameter((Object)browserUiState, (String)"uiState");
        Intrinsics.checkNotNullParameter(function1, (String)"onSelectTab");
        Intrinsics.checkNotNullParameter(function12, (String)"onCloseTab");
        Intrinsics.checkNotNullParameter(function0, (String)"onDismiss");
        Composer composer = object2.startRestartGroup(-250265845);
        ComposerKt.sourceInformation((Composer)composer, (String)"C(OpenWindowsDialog)P(5,4,1,3,2)52@2223L2,56@2324L7,64@2601L39,66@2690L15,59@2425L2029:OpenWindowsDialog.kt#kaa2o3");
        int n4 = n3 = n;
        if ((n & 6) == 0) {
            n4 = composer.changedInstance((Object)browserUiState) ? 4 : 2;
            n4 = n3 | n4;
        }
        int n5 = n4;
        if ((n & 0x30) == 0) {
            n3 = composer.changedInstance(function1) ? 32 : 16;
            n5 = n4 | n3;
        }
        n3 = n5;
        if ((n & 0x180) == 0) {
            n4 = composer.changedInstance(function12) ? 256 : 128;
            n3 = n5 | n4;
        }
        n4 = n3;
        if ((n & 0x6000) == 0) {
            n4 = composer.changedInstance(function0) ? 16384 : 8192;
            n4 = n3 | n4;
        }
        if ((n5 = n2 & 0x20) != 0) {
            n4 |= 0x30000;
        } else if ((0x30000 & n) == 0) {
            n3 = composer.changed((Object)modifier) ? 131072 : 65536;
            n4 |= n3;
        }
        if ((0x12093 & n4) == 73874 && composer.getSkipping()) {
            composer.skipToGroupEnd();
            object2 = object;
            object = modifier;
        } else {
            if ((n2 & 8) != 0) {
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)12696653, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
                object = composer.rememberedValue();
                if (object == Composer.Companion.getEmpty()) {
                    object = new OpenWindowsDialogKt$$ExternalSyntheticLambda3();
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            }
            if (n5 != 0) {
                modifier = (Modifier)Modifier.Companion;
            }
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-250265845, (int)n4, (int)-1, (String)"com.muslim.browser.pro.browser.ui.OpenWindowsDialog (OpenWindowsDialog.kt:55)");
            }
            object2 = (CompositionLocal)ThemeKt.getLocalAppColors();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object2 = composer.consume((CompositionLocal)object2);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            AppColors appColors = (AppColors)object2;
            Modifier modifier2 = BackgroundKt.background-bw27NRU$default((Modifier)SizeKt.fillMaxSize$default((Modifier)modifier, (float)0.0f, (int)1, null), (long)Color.copy-wmQWz5c$default((long)Color.Companion.getBlack-0d7_KjU(), (float)0.4f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null), null, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)12708786, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
            object2 = composer.rememberedValue();
            if (object2 == Composer.Companion.getEmpty()) {
                object2 = InteractionSourceKt.MutableInteractionSource();
                composer.updateRememberedValue(object2);
            }
            MutableInteractionSource mutableInteractionSource = (MutableInteractionSource)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)12711610, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
            n4 = (0xE000 & n4) == 16384 ? 1 : 0;
            object2 = composer.rememberedValue();
            if (n4 != 0 || object2 == Composer.Companion.getEmpty()) {
                object2 = new OpenWindowsDialogKt$$ExternalSyntheticLambda4(function0);
                composer.updateRememberedValue(object2);
            }
            object2 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object2 = TestTagKt.testTag((Modifier)ClickableKt.clickable-O2vRcR0$default((Modifier)modifier2, (MutableInteractionSource)mutableInteractionSource, null, (boolean)false, null, null, (Function0)object2, (int)28, null), (String)"open_windows_dialog");
            modifier2 = Alignment.Companion.getBottomCenter();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)733328855, (String)"CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            modifier2 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)modifier2, (boolean)false);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n4 = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            mutableInteractionSource = composer.getCurrentCompositionLocalMap();
            object2 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object2);
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
            Updater.set-impl((Composer)function02, (Object)modifier2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)mutableInteractionSource, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            modifier2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n4)) {
                function02.updateRememberedValue((Object)n4);
                function02.apply((Object)n4, (Function2)modifier2);
            }
            Updater.set-impl((Composer)function02, (Object)object2, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2146769399, (String)"C73@3429L9:Box.kt#2w3rfo");
            object2 = (BoxScope)BoxScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)365897285, (String)"C77@3173L39,79@3270L43,85@3546L902,71@2919L1529:OpenWindowsDialog.kt#kaa2o3");
            object2 = Modifier.Companion;
            float f = Dp.constructor-impl((float)68);
            float f2 = Dp.constructor-impl((float)16);
            float f3 = Dp.constructor-impl((float)16);
            modifier2 = SizeKt.fillMaxWidth((Modifier)SizeKt.widthIn-VpY3zN4$default((Modifier)PaddingKt.padding-qDBjuR0$default((Modifier)object2, (float)f2, (float)0.0f, (float)f3, (float)f, (int)2, null), (float)0.0f, (float)Dp.constructor-impl((float)360), (int)1, null), (float)0.92f);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-126737556, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
            object2 = composer.rememberedValue();
            if (object2 == Composer.Companion.getEmpty()) {
                object2 = InteractionSourceKt.MutableInteractionSource();
                composer.updateRememberedValue(object2);
            }
            mutableInteractionSource = (MutableInteractionSource)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-126734448, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
            object2 = composer.rememberedValue();
            if (object2 == Composer.Companion.getEmpty()) {
                object2 = new OpenWindowsDialogKt$$ExternalSyntheticLambda5();
                composer.updateRememberedValue(object2);
            }
            object2 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object2 = TestTagKt.testTag((Modifier)ClickableKt.clickable-O2vRcR0$default((Modifier)modifier2, (MutableInteractionSource)mutableInteractionSource, null, (boolean)false, null, null, (Function0)object2, (int)28, null), (String)"open_windows_panel");
            modifier2 = RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)14));
            long l = appColors.getSurface-0d7_KjU();
            appColors = BorderStrokeKt.BorderStroke-cXLIe8U((float)Dp.constructor-impl((float)1.0f), (long)appColors.getBorder-0d7_KjU());
            f2 = Dp.constructor-impl((float)6);
            modifier2 = (Shape)modifier2;
            SurfaceKt.Surface-T9BRK9s((Modifier)object2, (Shape)modifier2, (long)l, (long)0L, (float)0.0f, (float)f2, (BorderStroke)appColors, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-1120070582, (boolean)true, (Object)new OpenWindowsDialogKt$$ExternalSyntheticLambda6(browserUiState, function1, function12), (Composer)composer, (int)54)), (Composer)composer, (int)0xC30000, (int)24);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            composer.endNode();
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
            object2 = object;
            object = modifier;
        }
        modifier = composer.endRestartGroup();
        if (modifier != null) {
            modifier.updateScope((Function2)new OpenWindowsDialogKt$$ExternalSyntheticLambda7(browserUiState, (Function1)function1, (Function1)function12, (Function0)object2, (Function0)function0, (Modifier)object, n, n2));
        }
    }

    static final Unit OpenWindowsDialog$lambda$1$lambda$0() {
        return Unit.INSTANCE;
    }

    static final Unit OpenWindowsDialog$lambda$15$lambda$14(BrowserUiState browserUiState, Function1 function1, Function1 function12, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C92@3829L21,87@3634L804:OpenWindowsDialog.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1120070582, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.OpenWindowsDialog.<anonymous>.<anonymous> (OpenWindowsDialog.kt:87)");
            }
            Object object = TestTagKt.testTag((Modifier)ScrollKt.verticalScroll$default((Modifier)SizeKt.heightIn-VpY3zN4$default((Modifier)PaddingKt.padding-3ABfNKs((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)8)), (float)0.0f, (float)Dp.constructor-impl((float)340), (int)1, null), (ScrollState)ScrollKt.rememberScrollState((int)0, (Composer)composer, (int)0, (int)1), (boolean)false, null, (boolean)false, (int)14, null), (String)"open_windows_list");
            Object object2 = (Arrangement.Vertical)Arrangement.INSTANCE.spacedBy-0680j_4(Dp.constructor-impl((float)6));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            object2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)object2, (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(48 >> 3 & 0xE | 48 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            Object object3 = composer.getCurrentCompositionLocalMap();
            object = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object);
            Object object4 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode((Function0)object4);
            } else {
                composer.useNode();
            }
            object4 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)object4, (Object)object2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)object4, (Object)object3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (object4.getInserting() || !Intrinsics.areEqual((Object)object4.rememberedValue(), (Object)n)) {
                object4.updateRememberedValue((Object)n);
                object4.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)object4, (Object)object, (Function2)ComposeUiNode.Companion.getSetModifier());
            boolean bl = false;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object2 = ColumnScopeInstance.INSTANCE;
            n = 48 >> 6 & 0x70 | 6;
            object2 = (ColumnScope)object2;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)637805915, (String)"C:OpenWindowsDialog.kt#kaa2o3");
            composer.startReplaceGroup(-256519877);
            ComposerKt.sourceInformation((Composer)composer, (String)"*102@4303L23,103@4362L22,98@4116L290");
            object2 = browserUiState.getTabs();
            boolean bl2 = false;
            object = object2.iterator();
            while (object.hasNext()) {
                object4 = (BrowserTab)object.next();
                boolean bl3 = Intrinsics.areEqual((Object)((BrowserTab)object4).getId(), (Object)browserUiState.getCurrentTabId());
                boolean bl4 = browserUiState.getTabs().size() > 1;
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1625135395, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
                boolean bl5 = composer.changed((Object)function1);
                boolean bl6 = composer.changedInstance(object4);
                object3 = composer.rememberedValue();
                if (bl5 | bl6 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new OpenWindowsDialogKt$$ExternalSyntheticLambda0(function1, (BrowserTab)object4);
                    composer.updateRememberedValue(object3);
                }
                Function0 function0 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1625137282, (String)"CC(remember):OpenWindowsDialog.kt#9igjgp");
                bl6 = composer.changed((Object)function12);
                bl5 = composer.changedInstance(object4);
                object3 = composer.rememberedValue();
                if (bl6 | bl5 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new OpenWindowsDialogKt$$ExternalSyntheticLambda1(function12, (BrowserTab)object4);
                    composer.updateRememberedValue(object3);
                }
                object3 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                OpenWindowsDialogKt.WindowTabItem((BrowserTab)object4, bl3, bl4, (Function0<Unit>)function0, (Function0<Unit>)object3, composer, 0);
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

    static final Unit OpenWindowsDialog$lambda$15$lambda$14$lambda$13$lambda$12$lambda$11$lambda$10(Function1 function1, BrowserTab browserTab) {
        function1.invoke((Object)browserTab.getId());
        return Unit.INSTANCE;
    }

    static final Unit OpenWindowsDialog$lambda$15$lambda$14$lambda$13$lambda$12$lambda$9$lambda$8(Function1 function1, BrowserTab browserTab) {
        function1.invoke((Object)browserTab.getId());
        return Unit.INSTANCE;
    }

    static final Unit OpenWindowsDialog$lambda$15$lambda$7$lambda$6() {
        return Unit.INSTANCE;
    }

    static final Unit OpenWindowsDialog$lambda$16(BrowserUiState browserUiState, Function1 function1, Function1 function12, Function0 function0, Function0 function02, Modifier modifier, int n, int n2, Composer composer, int n3) {
        OpenWindowsDialogKt.OpenWindowsDialog(browserUiState, (Function1<? super String, Unit>)function1, (Function1<? super String, Unit>)function12, (Function0<Unit>)function0, (Function0<Unit>)function02, modifier, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n | 1)), n2);
        return Unit.INSTANCE;
    }

    static final Unit OpenWindowsDialog$lambda$4$lambda$3(Function0 function0) {
        function0.invoke();
        return Unit.INSTANCE;
    }

    private static final void WindowTabItem(BrowserTab browserTab, boolean bl, boolean bl2, Function0<Unit> function0, Function0<Unit> function02, Composer composer, int n) {
        int n2;
        composer = composer.startRestartGroup(-542446985);
        ComposerKt.sourceInformation((Composer)composer, (String)"C(WindowTabItem)P(4,1)119@4649L7,131@5079L2570,120@4661L2988:OpenWindowsDialog.kt#kaa2o3");
        int n3 = n;
        if ((n & 6) == 0) {
            n2 = composer.changedInstance((Object)browserTab) ? 4 : 2;
            n3 |= n2;
        }
        n2 = n3;
        if ((n & 0x30) == 0) {
            n2 = composer.changed(bl) ? 32 : 16;
            n2 = n3 | n2;
        }
        if ((n & 0x180) == 0) {
            n3 = composer.changed(bl2) ? 256 : 128;
            n3 = n2 | n3;
        } else {
            n3 = n2;
        }
        if ((n & 0xC00) == 0) {
            n2 = composer.changedInstance(function0) ? 2048 : 1024;
            n3 |= n2;
        }
        if ((n & 0x6000) == 0) {
            n2 = composer.changedInstance(function02) ? 16384 : 8192;
            n3 |= n2;
        }
        if ((n3 & 0x2493) == 9362 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-542446985, (int)n3, (int)-1, (String)"com.muslim.browser.pro.browser.ui.WindowTabItem (OpenWindowsDialog.kt:118)");
            }
            Object object = (CompositionLocal)ThemeKt.getLocalAppColors();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object = composer.consume(object);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object = (AppColors)object;
            RoundedCornerShape roundedCornerShape = RoundedCornerShapeKt.RoundedCornerShape-0680j_4((float)Dp.constructor-impl((float)10));
            long l = bl ? object.getSurfaceVariant-0d7_KjU() : object.getSurface-0d7_KjU();
            float f = Dp.constructor-impl((float)1.0f);
            long l2 = bl ? object.getAccent-0d7_KjU() : Color.copy-wmQWz5c$default((long)object.getBorder-0d7_KjU(), (float)0.5f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
            BorderStroke borderStroke = BorderStrokeKt.BorderStroke-cXLIe8U((float)f, (long)l2);
            Modifier modifier = SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            String string = browserTab.getId();
            modifier = TestTagKt.testTag((Modifier)modifier, (String)("window_tab_item_" + string));
            roundedCornerShape = (Shape)roundedCornerShape;
            SurfaceKt.Surface-o_FOJdg(function0, (Modifier)modifier, (boolean)false, (Shape)roundedCornerShape, (long)l, (long)0L, (float)0.0f, (float)0.0f, (BorderStroke)borderStroke, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-802376510, (boolean)true, (Object)new OpenWindowsDialogKt$$ExternalSyntheticLambda8(bl2, function02, browserTab, bl, (AppColors)object), (Composer)composer, (int)54)), (Composer)composer, (int)(n3 >> 9 & 0xE), (int)6, (int)740);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        composer = composer.endRestartGroup();
        if (composer != null) {
            composer.updateScope((Function2)new OpenWindowsDialogKt$$ExternalSyntheticLambda9(browserTab, bl, bl2, function0, function02, n));
        }
    }

    static final Unit WindowTabItem$lambda$23(boolean bl, Function0 function0, BrowserTab object, boolean bl2, AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C132@5089L2554:OpenWindowsDialog.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-802376510, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.WindowTabItem.<anonymous> (OpenWindowsDialog.kt:132)");
            }
            Object object2 = PaddingKt.padding-VpY3zN4((Modifier)SizeKt.fillMaxWidth$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (float)Dp.constructor-impl((float)14), (float)Dp.constructor-impl((float)10));
            Object object3 = Alignment.Companion.getCenterVertically();
            Arrangement.HorizontalOrVertical horizontalOrVertical = Arrangement.INSTANCE.getSpaceBetween();
            horizontalOrVertical = (Arrangement.Horizontal)horizontalOrVertical;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            object3 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)horizontalOrVertical, (Alignment.Vertical)object3, (Composer)composer, (int)(438 >> 3 & 0xE | 438 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            horizontalOrVertical = composer.getCurrentCompositionLocalMap();
            object2 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object2);
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
            Updater.set-impl((Composer)function02, (Object)object3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)horizontalOrVertical, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object3 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n)) {
                function02.updateRememberedValue((Object)n);
                function02.apply((Object)n, (Function2)object3);
            }
            Updater.set-impl((Composer)function02, (Object)object2, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            object2 = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1297405123, (String)"C139@5367L1579,173@6960L39:OpenWindowsDialog.kt#kaa2o3");
            object3 = Alignment.Companion.getCenterVertically();
            object2 = RowScope.weight$default((RowScope)object2, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)693286680, (String)"CC(Row)P(2,1,3)99@5018L58,100@5081L130:Row.kt#2w3rfo");
            object3 = RowKt.rowMeasurePolicy((Arrangement.Horizontal)Arrangement.INSTANCE.getStart(), (Alignment.Vertical)object3, (Composer)composer, (int)(384 >> 3 & 0xE | 384 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            horizontalOrVertical = composer.getCurrentCompositionLocalMap();
            object2 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object2);
            function02 = ComposeUiNode.Companion.getConstructor();
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
            Updater.set-impl((Composer)function02, (Object)object3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)horizontalOrVertical, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object3 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n)) {
                function02.updateRememberedValue((Object)n);
                function02.apply((Object)n, (Function2)object3);
            }
            Updater.set-impl((Composer)function02, (Object)object2, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-407840262, (String)"C101@5126L9:Row.kt#2w3rfo");
            horizontalOrVertical = (RowScope)RowScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-737548931, (String)"C144@5560L344,151@5922L40,154@6056L876:OpenWindowsDialog.kt#kaa2o3");
            object2 = ((BrowserTab)object).isHomePage() ? HomeKt.getHome((Icons.Filled)Icons.INSTANCE.getDefault()) : LanguageKt.getLanguage((Icons.Filled)Icons.INSTANCE.getDefault());
            object3 = ((BrowserTab)object).isHomePage() ? "Home Page" : "Web Page";
            long l = bl2 ? appColors.getAccent-0d7_KjU() : appColors.getIconTint-0d7_KjU();
            function02 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)20));
            IconKt.Icon-ww6aTOc((ImageVector)object2, (String)object3, (Modifier)function02, (long)l, (Composer)composer, (int)384, (int)0);
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)10)), (Composer)composer, (int)6);
            horizontalOrVertical = RowScope.weight$default((RowScope)horizontalOrVertical, (Modifier)((Modifier)Modifier.Companion), (float)1.0f, (boolean)false, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            object3 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            object2 = composer.getCurrentCompositionLocalMap();
            horizontalOrVertical = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)horizontalOrVertical);
            function02 = ComposeUiNode.Companion.getConstructor();
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
            Updater.set-impl((Composer)function02, (Object)object3, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function02, (Object)object2, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function02.getInserting() || !Intrinsics.areEqual((Object)function02.rememberedValue(), (Object)n)) {
                function02.updateRememberedValue((Object)n);
                function02.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function02, (Object)horizontalOrVertical, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object2 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2137205434, (String)"C155@6117L425,163@6563L351:OpenWindowsDialog.kt#kaa2o3");
            if (((BrowserTab)object).isHomePage()) {
                object2 = "Browser Home Page";
            } else {
                object2 = ((BrowserTab)object).getPageTitle();
                n = object2.length() == 0 ? 1 : 0;
                if (n != 0) {
                    object2 = "Web Page";
                }
                object2 = (String)object2;
            }
            l = appColors.getTextPrimary-0d7_KjU();
            long l2 = TextUnitKt.getSp((int)14);
            object3 = FontWeight.Companion;
            object3 = bl2 ? object3.getSemiBold() : object3.getNormal();
            n = TextOverflow.Companion.getEllipsis-gIe3tQ8();
            TextKt.Text--4IGK_g((String)object2, null, (long)l, (long)l2, null, (FontWeight)object3, null, (long)0L, null, null, (long)0L, (int)n, (boolean)false, (int)1, (int)0, null, null, (Composer)composer, (int)3072, (int)3120, (int)120786);
            if (((BrowserTab)object).isHomePage()) {
                object2 = "Home";
            } else {
                object2 = ((BrowserTab)object).getUrl();
                n = object2.length() == 0 ? 1 : 0;
                if (n != 0) {
                    object2 = "about:blank";
                }
                object2 = (String)object2;
            }
            l = bl2 ? appColors.getAccent-0d7_KjU() : appColors.getTextSecondary-0d7_KjU();
            l2 = TextUnitKt.getSp((double)11.5);
            n = TextOverflow.Companion.getEllipsis-gIe3tQ8();
            TextKt.Text--4IGK_g((String)object2, null, (long)l, (long)l2, null, null, null, (long)0L, null, null, (long)0L, (int)n, (boolean)false, (int)1, (int)0, null, null, (Composer)composer, (int)3072, (int)3120, (int)120818);
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
            SpacerKt.Spacer((Modifier)SizeKt.width-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)8)), (Composer)composer, (int)6);
            if (bl) {
                composer.startReplaceGroup(1299066691);
                ComposerKt.sourceInformation((Composer)composer, (String)"182@7323L296,177@7114L505");
                object2 = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)32));
                object = ((BrowserTab)object).getId();
                IconButtonKt.IconButton((Function0)function0, (Modifier)TestTagKt.testTag((Modifier)object2, (String)("close_tab_button_" + (String)object)), (boolean)false, null, null, (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-1848123874, (boolean)true, (Object)new OpenWindowsDialogKt$$ExternalSyntheticLambda2(appColors), (Composer)composer, (int)54)), (Composer)composer, (int)196608, (int)28);
            } else {
                composer.startReplaceGroup(1292009820);
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

    static final Unit WindowTabItem$lambda$23$lambda$22$lambda$21(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C183@7345L256:OpenWindowsDialog.kt#kaa2o3");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1848123874, (int)n, (int)-1, (String)"com.muslim.browser.pro.browser.ui.WindowTabItem.<anonymous>.<anonymous>.<anonymous> (OpenWindowsDialog.kt:183)");
            }
            ImageVector imageVector = CloseKt.getClose((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = appColors.getTextSecondary-0d7_KjU();
            appColors = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)16));
            IconKt.Icon-ww6aTOc((ImageVector)imageVector, (String)"Close Window", (Modifier)appColors, (long)l, (Composer)composer, (int)432, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit WindowTabItem$lambda$24(BrowserTab browserTab, boolean bl, boolean bl2, Function0 function0, Function0 function02, int n, Composer composer, int n2) {
        OpenWindowsDialogKt.WindowTabItem(browserTab, bl, bl2, (Function0<Unit>)function0, (Function0<Unit>)function02, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n | 1)));
        return Unit.INSTANCE;
    }
}

