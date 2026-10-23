/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.DownloadManager
 *  android.content.ActivityNotFoundException
 *  android.content.Context
 *  android.content.Intent
 *  android.net.Uri
 *  android.os.Bundle
 *  android.os.Environment
 *  android.util.Log
 *  android.webkit.MimeTypeMap
 *  android.webkit.ValueCallback
 *  android.webkit.WebView
 *  android.widget.Toast
 *  androidx.activity.compose.BackHandlerKt
 *  androidx.compose.foundation.BackgroundKt
 *  androidx.compose.foundation.BorderStroke
 *  androidx.compose.foundation.BorderStrokeKt
 *  androidx.compose.foundation.layout.Arrangement
 *  androidx.compose.foundation.layout.Arrangement$Vertical
 *  androidx.compose.foundation.layout.BoxKt
 *  androidx.compose.foundation.layout.BoxScope
 *  androidx.compose.foundation.layout.BoxScopeInstance
 *  androidx.compose.foundation.layout.ColumnKt
 *  androidx.compose.foundation.layout.ColumnScope
 *  androidx.compose.foundation.layout.ColumnScopeInstance
 *  androidx.compose.foundation.layout.PaddingKt
 *  androidx.compose.foundation.layout.PaddingValues
 *  androidx.compose.foundation.layout.SizeKt
 *  androidx.compose.foundation.layout.SpacerKt
 *  androidx.compose.material3.AndroidAlertDialog_androidKt
 *  androidx.compose.material3.ButtonColors
 *  androidx.compose.material3.ButtonDefaults
 *  androidx.compose.material3.ButtonKt
 *  androidx.compose.material3.MaterialTheme
 *  androidx.compose.material3.ScaffoldKt
 *  androidx.compose.material3.SnackbarHostKt
 *  androidx.compose.material3.SnackbarHostState
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Applier
 *  androidx.compose.runtime.ComposablesKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.EffectsKt
 *  androidx.compose.runtime.RecomposeScopeImplKt
 *  androidx.compose.runtime.State
 *  androidx.compose.runtime.Updater
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.ui.Alignment
 *  androidx.compose.ui.Alignment$Horizontal
 *  androidx.compose.ui.ComposedModifierKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.graphics.Color
 *  androidx.compose.ui.node.ComposeUiNode
 *  androidx.compose.ui.platform.AndroidCompositionLocals_androidKt
 *  androidx.compose.ui.text.TextStyle
 *  androidx.compose.ui.unit.Dp
 *  androidx.core.content.FileProvider
 *  androidx.lifecycle.compose.FlowExtKt
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.browser.DownloadPolicy
 *  com.muslim.browser.pro.browser.ui.BrowserMenuSheetKt
 *  com.muslim.browser.pro.ui.theme.AppColors
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  com.muslim.browser.pro.ui.theme.ThemeKt
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.SpillingKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.CoroutineScope
 */
package com.muslim.browser.pro;

import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.Toast;
import androidx.activity.compose.BackHandlerKt;
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
import androidx.compose.foundation.layout.PaddingValues;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.foundation.layout.SpacerKt;
import androidx.compose.material3.AndroidAlertDialog_androidKt;
import androidx.compose.material3.ButtonColors;
import androidx.compose.material3.ButtonDefaults;
import androidx.compose.material3.ButtonKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.ScaffoldKt;
import androidx.compose.material3.SnackbarHostKt;
import androidx.compose.material3.SnackbarHostState;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Applier;
import androidx.compose.runtime.ComposablesKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.RecomposeScopeImplKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.Updater;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Alignment;
import androidx.compose.ui.ComposedModifierKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.Color;
import androidx.compose.ui.node.ComposeUiNode;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.compose.ui.text.TextStyle;
import androidx.compose.ui.unit.Dp;
import androidx.core.content.FileProvider;
import androidx.lifecycle.compose.FlowExtKt;
import com.muslim.browser.pro.ComposableSingletons$MainActivityKt;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda10;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda11;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda12;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda13;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda14;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda15;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda16;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda17;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda18;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda19;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda2;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda20;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda21;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda22;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda23;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda24;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda25;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda26;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda27;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda28;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda29;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda3;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda30;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda31;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda32;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda33;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda34;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda35;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda36;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda37;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda38;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda39;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda4;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda40;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda41;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda42;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda43;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda44;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda45;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda46;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda47;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda48;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda49;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda5;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda50;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda51;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda52;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda53;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda54;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda55;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda56;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda6;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda7;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda8;
import com.muslim.browser.pro.MainActivityKt$$ExternalSyntheticLambda9;
import com.muslim.browser.pro.browser.BlockedInfo;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.BrowserViewModel;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.DownloadPolicy;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.SearchEngine;
import com.muslim.browser.pro.browser.SslWarningState;
import com.muslim.browser.pro.browser.TabWebViewManager;
import com.muslim.browser.pro.browser.TranslationEngine;
import com.muslim.browser.pro.browser.WebViewConfigurator;
import com.muslim.browser.pro.browser.ui.BlockedScreenKt;
import com.muslim.browser.pro.browser.ui.BottomNavBarKt;
import com.muslim.browser.pro.browser.ui.BrowserMenuSheetKt;
import com.muslim.browser.pro.browser.ui.BrowserWebViewKt;
import com.muslim.browser.pro.browser.ui.DiagnosticScreenKt;
import com.muslim.browser.pro.browser.ui.DownloadHistoryScreenKt;
import com.muslim.browser.pro.browser.ui.HistoryScreenKt;
import com.muslim.browser.pro.browser.ui.HomePageKt;
import com.muslim.browser.pro.browser.ui.OpenWindowsDialogKt;
import com.muslim.browser.pro.ui.theme.AppColors;
import com.muslim.browser.pro.ui.theme.AppTheme;
import com.muslim.browser.pro.ui.theme.ThemeKt;
import java.io.File;
import java.util.List;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import kotlinx.coroutines.CoroutineScope;

@Metadata(d1={"\u0000J\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\u001a\u00d1\u0001\u0010\u0000\u001a\u00020\u00012\u0006\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u00052\f\u0010\u0006\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\f\u0010\b\u001a\b\u0012\u0004\u0012\u00020\u00010\u00072\u0012\u0010\t\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\f\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\r\u001a\u000e\u0012\u0004\u0012\u00020\u000b\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\u0010\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u00010\n2\u0014\b\u0002\u0010\u0011\u001a\u000e\u0012\u0004\u0012\u00020\u000f\u0012\u0004\u0012\u00020\u000b0\n2\u0014\b\u0002\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u00010\nH\u0007\u00a2\u0006\u0002\u0010\u0014\u001a\u0018\u0010\u0015\u001a\u00020\u00012\u0006\u0010\u0016\u001a\u00020\u00172\u0006\u0010\u0018\u001a\u00020\u0019H\u0002\u00a8\u0006\u001a\u00b2\u0006\n\u0010\u001b\u001a\u00020\u001cX\u008a\u0084\u0002"}, d2={"BrowserApp", "", "viewModel", "Lcom/muslim/browser/pro/browser/BrowserViewModel;", "tabWebViewManager", "Lcom/muslim/browser/pro/browser/TabWebViewManager;", "onClearAllData", "Lkotlin/Function0;", "onClearCacheAndCookies", "onToggleDesktopMode", "Lkotlin/Function1;", "", "onToggleDesktopMode4", "onToggleWindows10Touch", "onSslProceed", "", "onSslCancel", "onHandleUrlNavigation", "onActiveWebViewChanged", "Landroid/webkit/WebView;", "(Lcom/muslim/browser/pro/browser/BrowserViewModel;Lcom/muslim/browser/pro/browser/TabWebViewManager;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function1;Landroidx/compose/runtime/Composer;III)V", "openDownloadedFile", "context", "Landroid/content/Context;", "entry", "Lcom/muslim/browser/pro/browser/DownloadEntry;", "app", "uiState", "Lcom/muslim/browser/pro/browser/BrowserUiState;"}, k=2, mv={2, 2, 0}, xi=48)
public final class MainActivityKt {
    public static final void BrowserApp(BrowserViewModel browserViewModel, TabWebViewManager tabWebViewManager, Function0<Unit> function0, Function0<Unit> function02, Function1<? super Boolean, Unit> function1, Function1<? super Boolean, Unit> mainActivityKt$$ExternalSyntheticLambda34, Function1<? super Boolean, Unit> mainActivityKt$$ExternalSyntheticLambda37, Function1<? super String, Unit> mainActivityKt$$ExternalSyntheticLambda38, Function1<? super String, Unit> mainActivityKt$$ExternalSyntheticLambda39, Function1<? super String, Boolean> mainActivityKt$$ExternalSyntheticLambda40, Function1<? super WebView, Unit> object, Composer object222, int n, int n2, int n3) {
        int n4;
        Intrinsics.checkNotNullParameter((Object)((Object)browserViewModel), (String)"viewModel");
        Intrinsics.checkNotNullParameter((Object)tabWebViewManager, (String)"tabWebViewManager");
        Intrinsics.checkNotNullParameter(function0, (String)"onClearAllData");
        Intrinsics.checkNotNullParameter(function02, (String)"onClearCacheAndCookies");
        Intrinsics.checkNotNullParameter(function1, (String)"onToggleDesktopMode");
        Composer composer = object222.startRestartGroup(1569764978);
        ComposerKt.sourceInformation((Composer)composer, (String)"C(BrowserApp)P(10,9,1,2,6,7,8,5,4,3)1195@52059L2,1196@52111L2,1197@52152L2,1198@52192L2,1199@52245L9,1200@52304L2,1202@52348L29,1203@52406L32,1204@52470L7,1205@52542L7,1208@52652L254,1216@52942L410,1216@52912L440,1229@53428L175,1229@53391L212,1238@53673L801,1238@53645L829,1260@54520L392,1276@55078L1338,1307@56441L47,1308@56495L10996,1272@54918L12573:MainActivity.kt#ijd4h7");
        int n5 = n;
        int n6 = n2;
        int n7 = n5;
        if ((n & 6) == 0) {
            n7 = composer.changedInstance((Object)browserViewModel) ? 4 : 2;
            n7 = n5 | n7;
        }
        n5 = n7;
        if ((n & 0x30) == 0) {
            n5 = composer.changedInstance((Object)tabWebViewManager) ? 32 : 16;
            n5 = n7 | n5;
        }
        n7 = n5;
        if ((n & 0x180) == 0) {
            n7 = composer.changedInstance(function0) ? 256 : 128;
            n7 = n5 | n7;
        }
        n5 = n7;
        if ((n & 0xC00) == 0) {
            n5 = composer.changedInstance(function02) ? 2048 : 1024;
            n5 = n7 | n5;
        }
        n7 = n5;
        if ((n & 0x6000) == 0) {
            n7 = composer.changedInstance(function1) ? 16384 : 8192;
            n7 = n5 | n7;
        }
        if ((n4 = n3 & 0x20) != 0) {
            n5 = n7 | 0x30000;
        } else if ((0x30000 & n) == 0) {
            n5 = composer.changedInstance(mainActivityKt$$ExternalSyntheticLambda34) ? 131072 : 65536;
            n5 = n7 | n5;
        } else {
            n5 = n7;
        }
        int n8 = n3 & 0x40;
        if (n8 != 0) {
            n5 |= 0x180000;
        } else if ((n & 0x180000) == 0) {
            n7 = composer.changedInstance(mainActivityKt$$ExternalSyntheticLambda37) ? 0x100000 : 524288;
            n5 |= n7;
        }
        int n9 = n3 & 0x80;
        if (n9 != 0) {
            n5 |= 0xC00000;
        } else if ((n & 0xC00000) == 0) {
            n7 = composer.changedInstance(mainActivityKt$$ExternalSyntheticLambda38) ? 0x800000 : 0x400000;
            n5 |= n7;
        }
        int n10 = n3 & 0x100;
        if (n10 != 0) {
            n5 |= 0x6000000;
        } else if ((n & 0x6000000) == 0) {
            n7 = composer.changedInstance(mainActivityKt$$ExternalSyntheticLambda39) ? 0x4000000 : 0x2000000;
            n5 |= n7;
        }
        int n11 = n3 & 0x200;
        if (n11 != 0) {
            n5 |= 0x30000000;
        } else if ((n & 0x30000000) == 0) {
            n7 = composer.changedInstance(mainActivityKt$$ExternalSyntheticLambda40) ? 0x20000000 : 0x10000000;
            n5 |= n7;
        }
        int n12 = n3 & 0x400;
        if (n12 != 0) {
            n7 = n6 | 6;
        } else if ((n2 & 6) == 0) {
            n7 = composer.changedInstance(object) ? 4 : 2;
            n7 = n6 | n7;
        } else {
            n7 = n6;
        }
        if ((n5 & 0x12492493) == 306783378 && (n7 & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
            object222 = object;
            object = mainActivityKt$$ExternalSyntheticLambda38;
            mainActivityKt$$ExternalSyntheticLambda38 = object222;
        } else {
            String string;
            AppColors appColors;
            Context context;
            SnackbarHostState snackbarHostState;
            State state;
            block51: {
                if (n4 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010631444, (String)"CC(remember):MainActivity.kt#9igjgp");
                    mainActivityKt$$ExternalSyntheticLambda34 = composer.rememberedValue();
                    if (mainActivityKt$$ExternalSyntheticLambda34 == Composer.Companion.getEmpty()) {
                        mainActivityKt$$ExternalSyntheticLambda34 = new MainActivityKt$$ExternalSyntheticLambda34();
                        composer.updateRememberedValue(mainActivityKt$$ExternalSyntheticLambda34);
                    }
                    mainActivityKt$$ExternalSyntheticLambda34 = (Function1)mainActivityKt$$ExternalSyntheticLambda34;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (n8 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010633108, (String)"CC(remember):MainActivity.kt#9igjgp");
                    mainActivityKt$$ExternalSyntheticLambda37 = composer.rememberedValue();
                    if (mainActivityKt$$ExternalSyntheticLambda37 == Composer.Companion.getEmpty()) {
                        mainActivityKt$$ExternalSyntheticLambda37 = new MainActivityKt$$ExternalSyntheticLambda37();
                        composer.updateRememberedValue(mainActivityKt$$ExternalSyntheticLambda37);
                    }
                    mainActivityKt$$ExternalSyntheticLambda37 = (Function1)mainActivityKt$$ExternalSyntheticLambda37;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (n9 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010634420, (String)"CC(remember):MainActivity.kt#9igjgp");
                    mainActivityKt$$ExternalSyntheticLambda38 = composer.rememberedValue();
                    if (mainActivityKt$$ExternalSyntheticLambda38 == Composer.Companion.getEmpty()) {
                        mainActivityKt$$ExternalSyntheticLambda38 = new MainActivityKt$$ExternalSyntheticLambda38();
                        composer.updateRememberedValue(mainActivityKt$$ExternalSyntheticLambda38);
                    }
                    mainActivityKt$$ExternalSyntheticLambda38 = (Function1)mainActivityKt$$ExternalSyntheticLambda38;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (n10 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010635700, (String)"CC(remember):MainActivity.kt#9igjgp");
                    mainActivityKt$$ExternalSyntheticLambda39 = composer.rememberedValue();
                    if (mainActivityKt$$ExternalSyntheticLambda39 == Composer.Companion.getEmpty()) {
                        mainActivityKt$$ExternalSyntheticLambda39 = new MainActivityKt$$ExternalSyntheticLambda39();
                        composer.updateRememberedValue(mainActivityKt$$ExternalSyntheticLambda39);
                    }
                    mainActivityKt$$ExternalSyntheticLambda39 = (Function1)mainActivityKt$$ExternalSyntheticLambda39;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (n11 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010637403, (String)"CC(remember):MainActivity.kt#9igjgp");
                    mainActivityKt$$ExternalSyntheticLambda40 = composer.rememberedValue();
                    if (mainActivityKt$$ExternalSyntheticLambda40 == Composer.Companion.getEmpty()) {
                        mainActivityKt$$ExternalSyntheticLambda40 = new MainActivityKt$$ExternalSyntheticLambda40();
                        composer.updateRememberedValue(mainActivityKt$$ExternalSyntheticLambda40);
                    }
                    mainActivityKt$$ExternalSyntheticLambda40 = (Function1)mainActivityKt$$ExternalSyntheticLambda40;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (n12 != 0) {
                    ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010639284, (String)"CC(remember):MainActivity.kt#9igjgp");
                    object = composer.rememberedValue();
                    if (object == Composer.Companion.getEmpty()) {
                        object = new MainActivityKt$$ExternalSyntheticLambda41();
                        composer.updateRememberedValue(object);
                    }
                    object = (Function1)object;
                    ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                }
                if (ComposerKt.isTraceInProgress()) {
                    ComposerKt.traceEventStart((int)1569764978, (int)n5, (int)n7, (String)"com.muslim.browser.pro.BrowserApp (MainActivity.kt:1201)");
                }
                state = FlowExtKt.collectAsStateWithLifecycle(browserViewModel.getUiState(), null, null, null, (Composer)composer, (int)0, (int)7);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010642578, (String)"CC(remember):MainActivity.kt#9igjgp");
                object222 = composer.rememberedValue();
                if (object222 == Composer.Companion.getEmpty()) {
                    object222 = new SnackbarHostState();
                    composer.updateRememberedValue(object222);
                }
                snackbarHostState = (SnackbarHostState)object222;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                object222 = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalContext();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
                object222 = composer.consume((CompositionLocal)object222);
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                context = (Context)object222;
                object222 = (CompositionLocal)ThemeKt.getLocalAppColors();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
                object222 = composer.consume((CompositionLocal)object222);
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                appColors = (AppColors)object222;
                for (Object object222 : (Iterable)MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getTabs()) {
                    if (!Intrinsics.areEqual((Object)((BrowserTab)object222).getId(), (Object)MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getCurrentTabId())) continue;
                    break block51;
                }
                object222 = null;
            }
            Object object3 = (BrowserTab)object222;
            object222 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getCurrentTabId();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010650672, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean n13 = composer.changed(object222);
            object222 = composer.rememberedValue();
            if (n13 || object222 == Composer.Companion.getEmpty()) {
                string = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getCurrentTabId();
                n6 = object3 != null && !((BrowserTab)object3).isHomePage() ? 1 : 0;
                object222 = n6 != 0 ? ((BrowserTab)object3).getUrl() : null;
                object3 = object3 != null ? ((BrowserTab)object3).getBundle() : null;
                object222 = TabWebViewManager.getOrCreateWebView$default(tabWebViewManager, string, (String)object222, (Bundle)object3, null, 8, null);
                composer.updateRememberedValue(object222);
            }
            object222 = (Pair)object222;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object222 = (WebView)object222.component1();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010660108, (String)"CC(remember):MainActivity.kt#9igjgp");
            n7 = (n7 & 0xE) == 4 ? 1 : 0;
            int n14 = composer.changedInstance(object222);
            int n15 = composer.changedInstance((Object)tabWebViewManager);
            int n16 = composer.changed((Object)state);
            object3 = composer.rememberedValue();
            if ((n7 | n14 | n15 | n16) == 0 && object3 != Composer.Companion.getEmpty()) {
                string = object222;
            } else {
                string = new Function2<CoroutineScope, Continuation<? super Unit>, Object>((Function1<? super WebView, Unit>)object, (WebView)object222, tabWebViewManager, (State<BrowserUiState>)state, null){
                    final WebView $activeWebView;
                    final Function1<WebView, Unit> $onActiveWebViewChanged;
                    final TabWebViewManager $tabWebViewManager;
                    final State<BrowserUiState> $uiState$delegate;
                    int label;
                    {
                        this.$onActiveWebViewChanged = function1;
                        this.$activeWebView = webView2;
                        this.$tabWebViewManager = tabWebViewManager;
                        this.$uiState$delegate = state;
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
                        this.$onActiveWebViewChanged.invoke((Object)this.$activeWebView);
                        this.$tabWebViewManager.pauseAll(MainActivityKt.access$BrowserApp$lambda$12(this.$uiState$delegate).getCurrentTabId());
                        this.$tabWebViewManager.resumeTab(MainActivityKt.access$BrowserApp$lambda$12(this.$uiState$delegate).getCurrentTabId());
                        object = WebViewConfigurator.INSTANCE;
                        WebView webView2 = this.$activeWebView;
                        String string = this.$activeWebView.getUrl();
                        DesktopArchitecture desktopArchitecture = MainActivityKt.access$BrowserApp$lambda$12(this.$uiState$delegate).getDesktopArchitecture();
                        ((WebViewConfigurator)object).syncDesktopArchitecture(webView2, string, desktopArchitecture, true);
                        return Unit.INSTANCE;
                    }
                };
                object3 = object222;
                object3 = (Function2)string;
                composer.updateRememberedValue(object3);
            }
            string = object222;
            object222 = (Function2)object3;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            EffectsKt.LaunchedEffect((Object)string, (Function2)object222, (Composer)composer, (int)0);
            object3 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getToastMessage();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010675425, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl = composer.changed((Object)state);
            boolean bl2 = composer.changedInstance((Object)browserViewModel);
            object222 = composer.rememberedValue();
            if (bl | bl2 || object222 == Composer.Companion.getEmpty()) {
                object222 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(snackbarHostState, browserViewModel, (State<BrowserUiState>)state, null){
                    final SnackbarHostState $snackbarHostState;
                    final State<BrowserUiState> $uiState$delegate;
                    final BrowserViewModel $viewModel;
                    Object L$0;
                    int label;
                    {
                        this.$snackbarHostState = snackbarHostState;
                        this.$viewModel = browserViewModel;
                        this.$uiState$delegate = state;
                        super(2, continuation);
                    }

                    public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                        return (Continuation)new /* invalid duplicate definition of identical inner class */;
                    }

                    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                        return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                    }

                    public final Object invokeSuspend(Object object) {
                        block5: {
                            Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                default: {
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                }
                                case 1: {
                                    object2 = (String)this.L$0;
                                    ResultKt.throwOnFailure((Object)object);
                                    object = object2;
                                    break;
                                }
                                case 0: {
                                    ResultKt.throwOnFailure((Object)object);
                                    object = MainActivityKt.access$BrowserApp$lambda$12(this.$uiState$delegate).getToastMessage();
                                    CharSequence charSequence = (CharSequence)object;
                                    boolean bl = charSequence == null || StringsKt.isBlank((CharSequence)charSequence);
                                    if (!bl) {
                                        charSequence = this.$snackbarHostState;
                                        Continuation continuation = (Continuation)this;
                                        this.L$0 = SpillingKt.nullOutSpilledVariable((Object)object);
                                        this.label = 1;
                                        if (SnackbarHostState.showSnackbar$default((SnackbarHostState)charSequence, (String)object, null, (boolean)false, null, (Continuation)continuation, (int)14, null) != object2) break;
                                        return object2;
                                    }
                                    break block5;
                                }
                            }
                            this.$viewModel.clearToast();
                        }
                        return Unit.INSTANCE;
                    }
                };
                composer.updateRememberedValue(object222);
            }
            object222 = (Function2)object222;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            EffectsKt.LaunchedEffect((Object)object3, (Function2)object222, (Composer)composer, (int)0);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010683891, (String)"CC(remember):MainActivity.kt#9igjgp");
            int n17 = composer.changed((Object)state);
            int n18 = composer.changedInstance((Object)browserViewModel);
            n7 = (n5 & 0xE000000) == 0x4000000 ? 1 : 0;
            int n19 = composer.changedInstance((Object)string);
            object222 = composer.rememberedValue();
            if ((n17 | n18 | n7 | n19) != 0 || object222 == Composer.Companion.getEmpty()) {
                object222 = new MainActivityKt$$ExternalSyntheticLambda42(browserViewModel, (Function1)mainActivityKt$$ExternalSyntheticLambda39, (WebView)string, state);
                composer.updateRememberedValue(object222);
            }
            object222 = (Function0)object222;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            n7 = 0;
            BackHandlerKt.BackHandler((boolean)true, (Function0)object222, (Composer)composer, (int)6, (int)0);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1010710586, (String)"CC(remember):MainActivity.kt#9igjgp");
            if ((n5 & 0x70000000) == 0x20000000) {
                n7 = 1;
            }
            int n20 = composer.changedInstance((Object)browserViewModel);
            int n21 = composer.changedInstance((Object)string);
            object222 = composer.rememberedValue();
            if ((n20 | n7 | n21) != 0 || object222 == Composer.Companion.getEmpty()) {
                object222 = new MainActivityKt$$ExternalSyntheticLambda43((Function1)mainActivityKt$$ExternalSyntheticLambda40, browserViewModel, (WebView)string);
                composer.updateRememberedValue(object222);
            }
            Function1 function12 = (Function1)object222;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            object3 = SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null);
            long l = appColors.getBackground-0d7_KjU();
            long l2 = appColors.getTextPrimary-0d7_KjU();
            object222 = (Function2)ComposableLambdaKt.rememberComposableLambda((int)514412301, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda44((WebView)string, browserViewModel, function1, state), (Composer)composer, (int)54);
            snackbarHostState = (Function2)ComposableLambdaKt.rememberComposableLambda((int)-2069904148, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda45(snackbarHostState), (Composer)composer, (int)54);
            ScaffoldKt.Scaffold-TvnljyQ((Modifier)object3, null, (Function2)object222, (Function2)snackbarHostState, null, (int)0, (long)l, (long)l2, null, (Function3)((Function3)ComposableLambdaKt.rememberComposableLambda((int)-1556984573, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda35(appColors, (WebView)string, function12, browserViewModel, tabWebViewManager, function0, function02, function1, (Function1)mainActivityKt$$ExternalSyntheticLambda34, (Function1)mainActivityKt$$ExternalSyntheticLambda37, context, state, (Function1)mainActivityKt$$ExternalSyntheticLambda39, (Function1)mainActivityKt$$ExternalSyntheticLambda38), (Composer)composer, (int)54)), (Composer)composer, (int)805309830, (int)306);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
            object222 = mainActivityKt$$ExternalSyntheticLambda38;
            mainActivityKt$$ExternalSyntheticLambda38 = object;
            object = object222;
        }
        object222 = composer.endRestartGroup();
        if (object222 != null) {
            object222.updateScope((Function2)new MainActivityKt$$ExternalSyntheticLambda36(browserViewModel, tabWebViewManager, function0, function02, function1, (Function1)mainActivityKt$$ExternalSyntheticLambda34, (Function1)mainActivityKt$$ExternalSyntheticLambda37, (Function1)object, (Function1)mainActivityKt$$ExternalSyntheticLambda39, (Function1)mainActivityKt$$ExternalSyntheticLambda40, (Function1)mainActivityKt$$ExternalSyntheticLambda38, n, n2, n3));
        }
    }

    static final Unit BrowserApp$lambda$1$lambda$0(boolean bl) {
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$11$lambda$10(WebView webView2) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"it");
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113(AppColors appColors, WebView object, Function1 object2, BrowserViewModel browserViewModel, TabWebViewManager function0, Function0 function02, Function0 function03, Function1 function1, Function1 function12, Function1 function13, Context context, State state, Function1 function14, Function1 function15, PaddingValues object3, Composer composer, int n) {
        int n2;
        Intrinsics.checkNotNullParameter((Object)object3, (String)"innerPadding");
        ComposerKt.sourceInformation((Composer)composer, (String)"C1309@56521L10964:MainActivity.kt#ijd4h7");
        int n3 = n2 = n;
        if ((n & 6) == 0) {
            n = composer.changed(object3) ? 4 : 2;
            n3 = n2 | n;
        }
        if ((n3 & 0x13) == 18 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            boolean bl;
            Function2 function2;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1556984573, (int)n3, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous> (MainActivity.kt:1309)");
            }
            object3 = BackgroundKt.background-bw27NRU$default((Modifier)PaddingKt.padding((Modifier)SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), (PaddingValues)object3), (long)appColors.getBackground-0d7_KjU(), null, (int)2, null);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)733328855, (String)"CC(Box)P(2,1,3)72@3384L130:Box.kt#2w3rfo");
            Object object4 = BoxKt.maybeCachedBoxMeasurePolicy((Alignment)Alignment.Companion.getTopStart(), (boolean)false);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            Object object5 = composer.getCurrentCompositionLocalMap();
            object3 = ComposedModifierKt.materializeModifier((Composer)composer, (Modifier)object3);
            Function0 function04 = ComposeUiNode.Companion.getConstructor();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-692256719, (String)"CC(ReusableComposeNode)P(1,2)376@14062L9:Composables.kt#9igjgp");
            if (!(composer.getApplier() instanceof Applier)) {
                ComposablesKt.invalidApplier();
            }
            composer.startReusableNode();
            if (composer.getInserting()) {
                composer.createNode(function04);
            } else {
                composer.useNode();
            }
            function04 = Updater.constructor-impl((Composer)composer);
            Updater.set-impl((Composer)function04, (Object)object4, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function04, (Object)object5, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object5 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function04.getInserting() || !Intrinsics.areEqual((Object)function04.rememberedValue(), (Object)n)) {
                function04.updateRememberedValue((Object)n);
                function04.apply((Object)n, (Function2)object5);
            }
            Updater.set-impl((Composer)function04, (Object)object3, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2146769399, (String)"C73@3429L9:Box.kt#2w3rfo");
            object3 = (BoxScope)BoxScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2139519639, (String)"C1321@57078L62,1317@56912L293:MainActivity.kt#ijd4h7");
            object5 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731579559, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl2 = composer.changedInstance(object);
            object3 = composer.rememberedValue();
            if (bl2 || object3 == Composer.Companion.getEmpty()) {
                object3 = new MainActivityKt$$ExternalSyntheticLambda0((WebView)object);
                composer.updateRememberedValue(object3);
            }
            object3 = (Function0)object3;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            BrowserWebViewKt.BrowserWebView((BrowserUiState)object5, object, (Function1<? super String, Unit>)object2, (Function0<Unit>)object3, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 24576, 0);
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHomePage() && MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getBlockedInfo() == null) {
                composer.startReplaceGroup(2139635578);
                ComposerKt.sourceInformation((Composer)composer, (String)"1332@57505L37,1334@57633L53,1335@57731L36,1329@57361L479");
                object4 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state);
                object5 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getFavoriteSites();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731593198, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (bl2 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda11(browserViewModel);
                    composer.updateRememberedValue(object3);
                }
                function04 = (Function1)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731597310, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (bl2 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda22(browserViewModel);
                    composer.updateRememberedValue(object3);
                }
                function2 = (Function2)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731600429, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (bl2 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda27(browserViewModel);
                    composer.updateRememberedValue(object3);
                }
                object3 = (Function1)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                HomePageKt.HomePage((BrowserUiState)object4, (List<FavoriteSite>)object5, (Function1<? super String, Unit>)function04, (Function1<? super String, Unit>)object2, (Function2<? super String, ? super String, Unit>)function2, (Function1<? super SearchEngine, Unit>)object3, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 0x180000, 0);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getBlockedInfo() != null) {
                composer.startReplaceGroup(2140256477);
                ComposerKt.sourceInformation((Composer)composer, (String)"1344@58086L22,1345@58141L314,1342@57983L604");
                object5 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getBlockedInfo();
                Intrinsics.checkNotNull((Object)object5);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731611775, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (bl2 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda28(browserViewModel);
                    composer.updateRememberedValue(object3);
                }
                object4 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731613827, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance(object);
                bl = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (bl2 | bl || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda29((WebView)object, browserViewModel);
                    composer.updateRememberedValue(object3);
                }
                object3 = (Function0)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                BlockedScreenKt.BlockedScreen((BlockedInfo)object5, (Function0<Unit>)object4, (Function0<Unit>)object3, object.canGoBack(), SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 24576, 0);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isTabsDialogOpen()) {
                composer.startReplaceGroup(2141034577);
                ComposerKt.sourceInformation((Composer)composer, (String)"1362@58828L738,1375@59601L146,1379@59780L481,1388@60295L31,1360@58736L1608");
                object5 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731636235, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl = composer.changedInstance((Object)browserViewModel);
                bl2 = composer.changedInstance(object);
                object3 = composer.rememberedValue();
                if (bl | bl2 || object3 == Composer.Companion.getEmpty()) {
                    object3 = new MainActivityKt$$ExternalSyntheticLambda30(browserViewModel, (WebView)object);
                    composer.updateRememberedValue(object3);
                }
                object4 = (Function1)object3;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731660379, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl = composer.changedInstance((Object)function0);
                bl2 = composer.changedInstance((Object)browserViewModel);
                object3 = composer.rememberedValue();
                if (!(bl | bl2) && object3 != Composer.Companion.getEmpty()) {
                    function0 = object3;
                } else {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda31((TabWebViewManager)function0, browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                object3 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731666442, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                bl = composer.changedInstance(object);
                function0 = composer.rememberedValue();
                if (bl2 | bl || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda32(browserViewModel, (WebView)object);
                    composer.updateRememberedValue((Object)function0);
                }
                function04 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731682472, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda33(browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                function0 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                OpenWindowsDialogKt.OpenWindowsDialog((BrowserUiState)object5, (Function1<? super String, Unit>)object4, (Function1<? super String, Unit>)object3, (Function0<Unit>)function04, function0, null, composer, 0, 32);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isMenuOpen()) {
                composer.startReplaceGroup(2142759014);
                ComposerKt.sourceInformation((Composer)composer, (String)"1396@60566L25,1397@60629L27,1398@60696L119,1402@60852L40,1403@60938L53,1404@61034L50,1410@61448L278,1416@61764L41,1417@61855L55,1418@61952L30,1394@60477L1523");
                object3 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731691138, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda1(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                object5 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731693156, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda2(browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                object4 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731695392, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda3(browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                function04 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731700305, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda4(browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                function2 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731703070, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda5(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                Function1 function16 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731706139, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda6(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                Function1 function17 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731719615, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl = composer.changedInstance((Object)browserViewModel);
                bl2 = composer.changedInstance(object);
                function0 = composer.rememberedValue();
                if (bl | bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda7(browserViewModel, (WebView)object);
                    composer.updateRememberedValue(function0);
                }
                Function0 function05 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731729490, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda8(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                Function1 function18 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731732416, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda9(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                Function1 function19 = (Function1)function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731735495, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda10(browserViewModel);
                    composer.updateRememberedValue(function0);
                }
                function0 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                BrowserMenuSheetKt.BrowserMenuSheet((BrowserUiState)object3, (Function0)object5, (Function0)object4, (Function0)function04, (Function1)function2, (Function1)function16, (Function1)function17, (Function0)function02, (Function0)function03, (Function1)function1, (Function1)function12, (Function1)function13, (Function0)function05, (Function1)function18, (Function1)function19, (Function0)function0, null, (Composer)composer, (int)0, (int)0, (int)65536);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDiagnosticOpen()) {
                composer.startReplaceGroup(2144385274);
                ComposerKt.sourceInformation((Composer)composer, (String)"1428@62400L31,1424@62153L351");
                bl = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDesktopModeEnabled();
                boolean bl3 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isWindows10TouchEnabled();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731749832, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                function0 = composer.rememberedValue();
                if (bl2 || function0 == Composer.Companion.getEmpty()) {
                    function0 = new MainActivityKt$$ExternalSyntheticLambda12(browserViewModel);
                    composer.updateRememberedValue((Object)function0);
                }
                function0 = function0;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                DiagnosticScreenKt.DiagnosticScreen(object, bl, bl3, (Function0<Unit>)function0, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 24576, 0);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHistoryOpen()) {
                composer.startReplaceGroup(2144857404);
                ComposerKt.sourceInformation((Composer)composer, (String)"1437@62728L124,1441@62890L42,1442@62967L31,1443@63032L28,1435@62624L509");
                function0 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getBrowsingHistory();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731760421, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl = composer.changedInstance((Object)browserViewModel);
                bl2 = composer.changed(object2);
                object = composer.rememberedValue();
                if (bl | bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda13(browserViewModel, (Function1)object2);
                    composer.updateRememberedValue(object);
                }
                object2 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731765523, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda14(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                function02 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731767976, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda15(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                function03 = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731770053, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda16(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                HistoryScreenKt.HistoryScreen(function0, (Function1<? super String, Unit>)object2, (Function1<? super String, Unit>)function02, (Function0<Unit>)function03, (Function0<Unit>)object, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 196608, 0);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDownloadsOpen()) {
                composer.startReplaceGroup(2145488533);
                ComposerKt.sourceInformation((Composer)composer, (String)"1452@63375L47,1453@63460L43,1454@63538L39,1455@63611L30,1450@63262L452");
                object2 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getDownloadHistory();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731781048, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)context);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda17(context);
                    composer.updateRememberedValue(object);
                }
                function0 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731783764, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda18(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                function02 = (Function1)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731786256, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda19(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                function03 = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)1731788583, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl2 = composer.changedInstance((Object)browserViewModel);
                object = composer.rememberedValue();
                if (bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda20(browserViewModel);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                DownloadHistoryScreenKt.DownloadHistoryScreen((List<DownloadEntry>)object2, function0, (Function1<? super String, Unit>)function02, (Function0<Unit>)function03, (Function0<Unit>)object, SizeKt.fillMaxSize$default((Modifier)((Modifier)Modifier.Companion), (float)0.0f, (int)1, null), composer, 196608, 0);
            } else {
                composer.startReplaceGroup(2082734489);
            }
            composer.endReplaceGroup();
            object2 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getSslWarningState();
            if (object2 == null) {
                composer.startReplaceGroup(2146151498);
                composer.endReplaceGroup();
            } else {
                composer.startReplaceGroup(2146151499);
                ComposerKt.sourceInformation((Composer)composer, (String)"*1463@63899L30,1511@66462L461,1522@66961L482,1475@64489L275,1482@64793L1631,1462@63847L3614");
                long l = appColors.getSurface-0d7_KjU();
                long l2 = appColors.getTextPrimary-0d7_KjU();
                long l3 = appColors.getTextSecondary-0d7_KjU();
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-128682109, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl = composer.changed((Object)function14);
                bl2 = composer.changed(object2);
                object = composer.rememberedValue();
                if (bl | bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivityKt$$ExternalSyntheticLambda21(function14, (SslWarningState)object2);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                browserViewModel = (Function2)ComposableLambdaKt.rememberComposableLambda((int)-1025646307, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda23(function15, (SslWarningState)object2), (Composer)composer, (int)54);
                function03 = (Function2)ComposableLambdaKt.rememberComposableLambda((int)1206398555, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda24(function14, (SslWarningState)object2, appColors), (Composer)composer, (int)54);
                function0 = ComposableSingletons$MainActivityKt.INSTANCE.getLambda$174937338$app();
                function02 = (Function2)ComposableLambdaKt.rememberComposableLambda((int)-856523879, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda25(appColors), (Composer)composer, (int)54);
                appColors = (Function2)ComposableLambdaKt.rememberComposableLambda((int)-1887985096, (boolean)true, (Object)new MainActivityKt$$ExternalSyntheticLambda26((SslWarningState)object2, appColors), (Composer)composer, (int)54);
                AndroidAlertDialog_androidKt.AlertDialog-Oix01E0((Function0)object, (Function2)browserViewModel, null, (Function2)function03, function0, (Function2)function02, (Function2)appColors, null, (long)l, (long)0L, (long)l2, (long)l3, (float)0.0f, null, (Composer)composer, (int)1797168, (int)0, (int)12932);
                appColors = Unit.INSTANCE;
                composer.endReplaceGroup();
                appColors = Unit.INSTANCE;
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

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$101$lambda$100(Function1 function1, SslWarningState sslWarningState) {
        function1.invoke((Object)sslWarningState.getHost());
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$104(Function1 object, SslWarningState sslWarningState, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1513@66534L31,1515@66696L11,1514@66619L184,1512@66488L413:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1025646307, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1512)");
            }
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-2098587908, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl = composer.changed(object);
            boolean bl2 = composer.changed((Object)sslWarningState);
            Object object2 = composer.rememberedValue();
            if (!(bl | bl2) && object2 != Composer.Companion.getEmpty()) {
                object = object2;
            } else {
                object = new MainActivityKt$$ExternalSyntheticLambda49((Function1)object, sslWarningState);
                composer.updateRememberedValue(object);
            }
            object = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ButtonKt.Button((Function0)object, null, (boolean)false, null, (ButtonColors)ButtonDefaults.INSTANCE.buttonColors-ro_MJ88(MaterialTheme.INSTANCE.getColorScheme(composer, MaterialTheme.$stable).getError-0d7_KjU(), Color.Companion.getWhite-0d7_KjU(), 0L, 0L, composer, ButtonDefaults.$stable << 12 | 0x30, 12), null, null, null, null, ComposableSingletons$MainActivityKt.INSTANCE.getLambda$1062284077$app(), (Composer)composer, (int)0x30000000, (int)494);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$104$lambda$103$lambda$102(Function1 function1, SslWarningState sslWarningState) {
        function1.invoke((Object)sslWarningState.getHost());
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$107(Function1 object, SslWarningState sslWarningState, AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1524@67041L30,1525@67125L117,1523@66987L434:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1206398555, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1523)");
            }
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-292155975, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl = composer.changed(object);
            boolean bl2 = composer.changed((Object)sslWarningState);
            Object object2 = composer.rememberedValue();
            if (!(bl | bl2) && object2 != Composer.Companion.getEmpty()) {
                object = object2;
            } else {
                object = new MainActivityKt$$ExternalSyntheticLambda46((Function1)object, sslWarningState);
                composer.updateRememberedValue(object);
            }
            object = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ButtonKt.OutlinedButton((Function0)object, null, (boolean)false, null, (ButtonColors)ButtonDefaults.INSTANCE.outlinedButtonColors-ro_MJ88(0L, appColors.getTextPrimary-0d7_KjU(), 0L, 0L, composer, ButtonDefaults.$stable << 12, 13), null, (BorderStroke)BorderStrokeKt.BorderStroke-cXLIe8U((float)Dp.constructor-impl((float)1.0f), (long)appColors.getBorder-0d7_KjU()), null, null, ComposableSingletons$MainActivityKt.INSTANCE.getLambda$-2016551255$app(), (Composer)composer, (int)0x30000000, (int)430);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$107$lambda$106$lambda$105(Function1 function1, SslWarningState sslWarningState) {
        function1.invoke((Object)sslWarningState.getHost());
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$108(AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1478@64638L10,1476@64515L227:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-856523879, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1476)");
            }
            TextStyle textStyle = MaterialTheme.INSTANCE.getTypography(composer, MaterialTheme.$stable).getTitleMedium();
            long l = appColors.getTextPrimary-0d7_KjU();
            TextKt.Text--4IGK_g((String)"Security Certificate Warning", null, (long)l, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, (TextStyle)textStyle, (Composer)composer, (int)6, (int)0, (int)65530);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$111$lambda$110(SslWarningState object, AppColors appColors, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1483@64819L1583:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-1887985096, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous>.<anonymous>.<anonymous>.<anonymous> (MainActivity.kt:1483)");
            }
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-483455358, (String)"CC(Column)P(2,3,1)86@4330L61,87@4396L133:Column.kt#2w3rfo");
            Modifier modifier = (Modifier)Modifier.Companion;
            Object object2 = ColumnKt.columnMeasurePolicy((Arrangement.Vertical)Arrangement.INSTANCE.getTop(), (Alignment.Horizontal)Alignment.Companion.getStart(), (Composer)composer, (int)(0 >> 3 & 0xE | 0 >> 3 & 0x70));
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1323940314, (String)"CC(Layout)P(!1,2)79@3208L23,82@3359L411:Layout.kt#80mrfh");
            n = ComposablesKt.getCurrentCompositeKeyHash((Composer)composer, (int)0);
            Object object3 = composer.getCurrentCompositionLocalMap();
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
            Updater.set-impl((Composer)function0, (Object)object2, (Function2)ComposeUiNode.Companion.getSetMeasurePolicy());
            Updater.set-impl((Composer)function0, (Object)object3, (Function2)ComposeUiNode.Companion.getSetResolvedCompositionLocals());
            object2 = ComposeUiNode.Companion.getSetCompositeKeyHash();
            if (function0.getInserting() || !Intrinsics.areEqual((Object)function0.rememberedValue(), (Object)n)) {
                function0.updateRememberedValue((Object)n);
                function0.apply((Object)n, (Function2)object2);
            }
            Updater.set-impl((Composer)function0, (Object)modifier, (Function2)ComposeUiNode.Companion.getSetModifier());
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-384784025, (String)"C88@4444L9:Column.kt#2w3rfo");
            object2 = (ColumnScope)ColumnScopeInstance.INSTANCE;
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-943085295, (String)"C1486@65104L10,1484@64856L359,1489@65244L40,1492@65438L10,1490@65313L239,1503@66046L41,1506@66265L10,1504@66116L260:MainActivity.kt#ijd4h7");
            object2 = ((SslWarningState)object).getHost();
            object2 = "The security certificate for \"" + (String)object2 + "\" cannot be fully verified because the server did not provide its intermediate certificate chain.";
            object3 = MaterialTheme.INSTANCE.getTypography(composer, MaterialTheme.$stable).getBodyMedium();
            long l = appColors.getTextPrimary-0d7_KjU();
            TextKt.Text--4IGK_g((String)object2, null, (long)l, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, (TextStyle)object3, (Composer)composer, (int)0, (int)0, (int)65530);
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)8)), (Composer)composer, (int)6);
            object2 = ((SslWarningState)object).getHost();
            object3 = "Host: " + (String)object2;
            object2 = MaterialTheme.INSTANCE.getTypography(composer, MaterialTheme.$stable).getLabelMedium();
            l = appColors.getTextSecondary-0d7_KjU();
            TextKt.Text--4IGK_g((String)object3, null, (long)l, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, (TextStyle)object2, (Composer)composer, (int)0, (int)0, (int)65530);
            if (!StringsKt.isBlank((CharSequence)((SslWarningState)object).getDetails())) {
                composer.startReplaceGroup(-942366065);
                ComposerKt.sourceInformation((Composer)composer, (String)"1496@65650L40,1499@65848L10,1497@65723L264");
                SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)4)), (Composer)composer, (int)6);
                object = ((SslWarningState)object).getDetails();
                object2 = MaterialTheme.INSTANCE.getTypography(composer, MaterialTheme.$stable).getBodySmall();
                l = Color.copy-wmQWz5c$default((long)appColors.getTextSecondary-0d7_KjU(), (float)0.8f, (float)0.0f, (float)0.0f, (float)0.0f, (int)14, null);
                TextKt.Text--4IGK_g((String)object, null, (long)l, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, (TextStyle)object2, (Composer)composer, (int)0, (int)0, (int)65530);
            } else {
                composer.startReplaceGroup(-1007470560);
            }
            composer.endReplaceGroup();
            SpacerKt.Spacer((Modifier)SizeKt.height-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)10)), (Composer)composer, (int)6);
            object = MaterialTheme.INSTANCE.getTypography(composer, MaterialTheme.$stable).getBodyMedium();
            l = appColors.getTextPrimary-0d7_KjU();
            TextKt.Text--4IGK_g((String)"Do you want to proceed to this website anyway?", null, (long)l, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, (TextStyle)object, (Composer)composer, (int)6, (int)0, (int)65530);
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

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$38$lambda$37(WebView webView2) {
        webView2.reload();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$40$lambda$39(BrowserViewModel browserViewModel, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"it");
        browserViewModel.onSearchInputChange(string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$42$lambda$41(BrowserViewModel browserViewModel, String string, String string2) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"name");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        browserViewModel.addFavoriteSite(string, string2);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$44$lambda$43(BrowserViewModel browserViewModel, SearchEngine searchEngine) {
        Intrinsics.checkNotNullParameter((Object)((Object)searchEngine), (String)"it");
        browserViewModel.selectSearchEngine(searchEngine);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$46$lambda$45(BrowserViewModel browserViewModel) {
        browserViewModel.goHome();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$48$lambda$47(WebView object, BrowserViewModel browserViewModel) {
        if (object.canGoBack()) {
            object.goBack();
            String string = object.getUrl();
            object = string;
            if (string == null) {
                object = "";
            }
            browserViewModel.onPageStarted((String)object);
        } else {
            browserViewModel.goHome();
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$51$lambda$50(BrowserViewModel browserViewModel, WebView webView2, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"selectedTabId");
        if (!Intrinsics.areEqual((Object)string, (Object)((BrowserUiState)browserViewModel.getUiState().getValue()).getCurrentTabId())) {
            BrowserTab browserTab2;
            block4: {
                for (BrowserTab browserTab2 : (Iterable)((BrowserUiState)browserViewModel.getUiState().getValue()).getTabs()) {
                    if (!Intrinsics.areEqual((Object)((BrowserTab)browserTab2).getId(), (Object)((BrowserUiState)browserViewModel.getUiState().getValue()).getCurrentTabId())) continue;
                    break block4;
                }
                browserTab2 = null;
            }
            browserTab2 = browserTab2;
            if (browserTab2 != null && !browserTab2.isHomePage()) {
                Bundle bundle = new Bundle();
                webView2.saveState(bundle);
                browserViewModel.saveTabState(browserTab2.getId(), bundle);
            }
            browserViewModel.selectTab(string);
        } else {
            browserViewModel.closeTabsDialog();
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$53$lambda$52(TabWebViewManager tabWebViewManager, BrowserViewModel browserViewModel, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        tabWebViewManager.destroyWebView(string);
        browserViewModel.closeTab(string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$56$lambda$55(BrowserViewModel browserViewModel, WebView webView2) {
        Bundle bundle2;
        block2: {
            for (Bundle bundle2 : (Iterable)((BrowserUiState)browserViewModel.getUiState().getValue()).getTabs()) {
                if (!Intrinsics.areEqual((Object)((BrowserTab)bundle2).getId(), (Object)((BrowserUiState)browserViewModel.getUiState().getValue()).getCurrentTabId())) continue;
                break block2;
            }
            bundle2 = null;
        }
        BrowserTab browserTab = (BrowserTab)bundle2;
        if (browserTab != null && !browserTab.isHomePage()) {
            bundle2 = new Bundle();
            webView2.saveState(bundle2);
            browserViewModel.saveTabState(browserTab.getId(), bundle2);
        }
        BrowserViewModel.openNewTab$default(browserViewModel, null, 1, null);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$58$lambda$57(BrowserViewModel browserViewModel) {
        browserViewModel.closeTabsDialog();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$60$lambda$59(BrowserViewModel browserViewModel) {
        browserViewModel.closeMenu();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$62$lambda$61(BrowserViewModel browserViewModel) {
        browserViewModel.openHistory();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$64$lambda$63(BrowserViewModel browserViewModel) {
        browserViewModel.closeMenu();
        browserViewModel.openDownloads();
        return Unit.INSTANCE;
    }

    static final boolean BrowserApp$lambda$113$lambda$112$lambda$66$lambda$65(BrowserViewModel browserViewModel, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"kw");
        return browserViewModel.addCustomKeyword(string);
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$68$lambda$67(BrowserViewModel browserViewModel, boolean bl) {
        browserViewModel.togglePopupBlocking(bl);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$70$lambda$69(BrowserViewModel browserViewModel, boolean bl) {
        browserViewModel.toggleAdBlocking(bl);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74(BrowserViewModel browserViewModel, WebView webView2) {
        browserViewModel.translateCurrentPage(new MainActivityKt$$ExternalSyntheticLambda47(webView2), new MainActivityKt$$ExternalSyntheticLambda48(webView2));
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72(WebView webView2, String string, Function1 object) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"script");
        object = object != null ? new MainActivityKt$$ExternalSyntheticLambda50((Function1)object) : null;
        webView2.evaluateJavascript(string, (ValueCallback)object);
        return Unit.INSTANCE;
    }

    static final void BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$72$lambda$71(Function1 function1, String string) {
        function1.invoke((Object)string);
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$75$lambda$74$lambda$73(WebView webView2) {
        webView2.reload();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$77$lambda$76(BrowserViewModel browserViewModel, AppTheme appTheme) {
        Intrinsics.checkNotNullParameter((Object)appTheme, (String)"theme");
        browserViewModel.setAppTheme(appTheme);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$79$lambda$78(BrowserViewModel browserViewModel, TranslationEngine translationEngine) {
        Intrinsics.checkNotNullParameter((Object)((Object)translationEngine), (String)"engine");
        browserViewModel.selectTranslationEngine(translationEngine);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$81$lambda$80(BrowserViewModel browserViewModel) {
        browserViewModel.openDiagnostic();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$83$lambda$82(BrowserViewModel browserViewModel) {
        browserViewModel.closeDiagnostic();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$85$lambda$84(BrowserViewModel browserViewModel, Function1 function1, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        browserViewModel.closeHistory();
        function1.invoke((Object)string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$87$lambda$86(BrowserViewModel browserViewModel, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        browserViewModel.deleteHistoryEntry(string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$89$lambda$88(BrowserViewModel browserViewModel) {
        browserViewModel.clearAllHistory();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$91$lambda$90(BrowserViewModel browserViewModel) {
        browserViewModel.closeHistory();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$93$lambda$92(Context context, DownloadEntry downloadEntry) {
        Intrinsics.checkNotNullParameter((Object)downloadEntry, (String)"entry");
        MainActivityKt.openDownloadedFile(context, downloadEntry);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$95$lambda$94(BrowserViewModel browserViewModel, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        browserViewModel.deleteDownloadEntry(string);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$97$lambda$96(BrowserViewModel browserViewModel) {
        browserViewModel.clearAllDownloadHistory();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$113$lambda$112$lambda$99$lambda$98(BrowserViewModel browserViewModel) {
        browserViewModel.closeDownloads();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$114(BrowserViewModel browserViewModel, TabWebViewManager tabWebViewManager, Function0 function0, Function0 function02, Function1 function1, Function1 function12, Function1 function13, Function1 function14, Function1 function15, Function1 function16, Function1 function17, int n, int n2, int n3, Composer composer, int n4) {
        MainActivityKt.BrowserApp(browserViewModel, tabWebViewManager, (Function0<Unit>)function0, (Function0<Unit>)function02, (Function1<? super Boolean, Unit>)function1, (Function1<? super Boolean, Unit>)function12, (Function1<? super Boolean, Unit>)function13, (Function1<? super String, Unit>)function14, (Function1<? super String, Unit>)function15, (Function1<? super String, Boolean>)function16, (Function1<? super WebView, Unit>)function17, composer, RecomposeScopeImplKt.updateChangedFlags((int)(n | 1)), RecomposeScopeImplKt.updateChangedFlags((int)n2), n3);
        return Unit.INSTANCE;
    }

    private static final BrowserUiState BrowserApp$lambda$12(State<BrowserUiState> state) {
        return (BrowserUiState)state.getValue();
    }

    static final Unit BrowserApp$lambda$19$lambda$18(BrowserViewModel object, Function1 function1, WebView webView2, State state) {
        if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDiagnosticOpen()) {
            ((BrowserViewModel)((Object)object)).closeDiagnostic();
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getSslWarningState() != null) {
            object = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getSslWarningState();
            Intrinsics.checkNotNull((Object)object);
            function1.invoke((Object)((SslWarningState)object).getHost());
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDownloadsOpen()) {
            ((BrowserViewModel)((Object)object)).closeDownloads();
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHistoryOpen()) {
            ((BrowserViewModel)((Object)object)).closeHistory();
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isTabsDialogOpen()) {
            ((BrowserViewModel)((Object)object)).closeTabsDialog();
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isMenuOpen()) {
            ((BrowserViewModel)((Object)object)).closeMenu();
        } else if (MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getBlockedInfo() != null) {
            ((BrowserViewModel)((Object)object)).goHome();
        } else if (!MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHomePage()) {
            if (webView2.canGoBack()) {
                webView2.goBack();
            } else {
                ((BrowserViewModel)((Object)object)).goHome();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$21$lambda$20(Function1 function1, BrowserViewModel browserViewModel, WebView webView2, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"input");
        String string2 = ((Object)StringsKt.trim((CharSequence)string)).toString();
        if (!DownloadPolicy.INSTANCE.isAudio(string2) && !DownloadPolicy.INSTANCE.isDownloadableFileUrl(string2)) {
            if (browserViewModel.submitQueryOrUrl(string)) {
                webView2.loadUrl(((BrowserUiState)browserViewModel.getUiState().getValue()).getCurrentUrl());
            }
        } else {
            function1.invoke((Object)string2);
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$3$lambda$2(boolean bl) {
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35(WebView object, BrowserViewModel browserViewModel, Function1 function1, State state, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1281@55348L117,1284@55497L99,1287@55625L449,1296@56105L66,1299@56211L90,1302@56332L60,1277@55092L1314:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)514412301, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous> (MainActivity.kt:1277)");
            }
            boolean bl = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getCanGoBack() && !MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHomePage();
            boolean bl2 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).getCanGoForward() && !MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isHomePage();
            boolean bl3 = MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDesktopModeEnabled();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)428990978, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl4 = composer.changedInstance(object);
            boolean bl5 = composer.changedInstance((Object)browserViewModel);
            Object object2 = composer.rememberedValue();
            if (bl4 | bl5 || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivityKt$$ExternalSyntheticLambda51((WebView)object, browserViewModel);
                composer.updateRememberedValue(object2);
            }
            Function0 function0 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)428995728, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl5 = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl5 || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivityKt$$ExternalSyntheticLambda52((WebView)object);
                composer.updateRememberedValue(object2);
            }
            Function0 function02 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)429000174, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl4 = composer.changedInstance((Object)browserViewModel);
            bl5 = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (!(bl4 | bl5) && object2 != Composer.Companion.getEmpty()) {
                object = object2;
            } else {
                object = new MainActivityKt$$ExternalSyntheticLambda53(browserViewModel, (WebView)object);
                composer.updateRememberedValue(object);
            }
            object2 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)429015151, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl5 = composer.changedInstance((Object)browserViewModel);
            object = composer.rememberedValue();
            if (bl5 || object == Composer.Companion.getEmpty()) {
                object = new MainActivityKt$$ExternalSyntheticLambda54(browserViewModel);
                composer.updateRememberedValue(object);
            }
            Function0 function03 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)429018567, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl4 = composer.changed((Object)function1);
            bl5 = composer.changed((Object)state);
            object = composer.rememberedValue();
            if (bl4 | bl5 || object == Composer.Companion.getEmpty()) {
                object = new MainActivityKt$$ExternalSyntheticLambda55(function1, state);
                composer.updateRememberedValue(object);
            }
            function1 = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)429022409, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl5 = composer.changedInstance((Object)browserViewModel);
            object = composer.rememberedValue();
            if (bl5 || object == Composer.Companion.getEmpty()) {
                object = new MainActivityKt$$ExternalSyntheticLambda56(browserViewModel);
                composer.updateRememberedValue(object);
            }
            object = (Function0)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            BottomNavBarKt.BottomNavBar(bl, bl2, bl3, (Function0<Unit>)function0, (Function0<Unit>)function02, (Function0<Unit>)object2, (Function0<Unit>)function03, (Function0<Unit>)function1, (Function0<Unit>)object, null, composer, 0, 512);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$23$lambda$22(WebView webView2, BrowserViewModel browserViewModel) {
        if (webView2.canGoBack()) {
            webView2.goBack();
        } else {
            browserViewModel.goHome();
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$25$lambda$24(WebView webView2) {
        if (webView2.canGoForward()) {
            webView2.goForward();
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$28$lambda$27(BrowserViewModel browserViewModel, WebView webView2) {
        Bundle bundle2;
        block2: {
            for (Bundle bundle2 : (Iterable)((BrowserUiState)browserViewModel.getUiState().getValue()).getTabs()) {
                if (!Intrinsics.areEqual((Object)((BrowserTab)bundle2).getId(), (Object)((BrowserUiState)browserViewModel.getUiState().getValue()).getCurrentTabId())) continue;
                break block2;
            }
            bundle2 = null;
        }
        BrowserTab browserTab = (BrowserTab)bundle2;
        if (browserTab != null && !browserTab.isHomePage()) {
            bundle2 = new Bundle();
            webView2.saveState(bundle2);
            browserViewModel.saveTabState(browserTab.getId(), bundle2);
        }
        BrowserViewModel.openNewTab$default(browserViewModel, null, 1, null);
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$30$lambda$29(BrowserViewModel browserViewModel) {
        browserViewModel.openTabsDialog();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$32$lambda$31(Function1 function1, State state) {
        function1.invoke((Object)(MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state).isDesktopModeEnabled() ^ true));
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$35$lambda$34$lambda$33(BrowserViewModel browserViewModel) {
        browserViewModel.openMenu();
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$36(SnackbarHostState snackbarHostState, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1307@56443L43:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-2069904148, (int)n, (int)-1, (String)"com.muslim.browser.pro.BrowserApp.<anonymous> (MainActivity.kt:1307)");
            }
            SnackbarHostKt.SnackbarHost((SnackbarHostState)snackbarHostState, null, null, (Composer)composer, (int)6, (int)6);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$5$lambda$4(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"it");
        return Unit.INSTANCE;
    }

    static final Unit BrowserApp$lambda$7$lambda$6(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"it");
        return Unit.INSTANCE;
    }

    static final boolean BrowserApp$lambda$9$lambda$8(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"it");
        return false;
    }

    public static final /* synthetic */ BrowserUiState access$BrowserApp$lambda$12(State state) {
        return MainActivityKt.BrowserApp$lambda$12((State<BrowserUiState>)state);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private static final void openDownloadedFile(Context var0, DownloadEntry var1_1) {
        block35: {
            block34: {
                block33: {
                    block32: {
                        block31: {
                            block24: {
                                block25: {
                                    var3_4 = true;
                                    var7_5 = var0.getSystemService("download");
                                    var8_8 /* !! */  = var7_5 instanceof DownloadManager != false ? (DownloadManager)var7_5 : null;
                                    var9_9 /* !! */  = null;
                                    var5_10 = var1_1.getDownloadId();
                                    var7_5 = var9_9 /* !! */ ;
                                    if (var5_10 <= 0L) break block24;
                                    if (var8_8 /* !! */  == null) break block25;
                                    try {
                                        var7_5 = var8_8 /* !! */ .getUriForDownloadedFile(var1_1.getDownloadId());
                                        break block24;
                                    }
                                    catch (Exception var7_6) {
                                        var5_10 = var1_1.getDownloadId();
                                        var8_8 /* !! */  = new StringBuilder();
                                        Log.w((String)"DownloadManager", (String)var8_8 /* !! */ .append("Could not get URI from DownloadManager for ").append(var5_10).toString(), (Throwable)var7_6);
                                        var7_5 = var9_9 /* !! */ ;
                                        break block24;
                                    }
                                }
                                var7_5 = null;
                            }
                            var8_8 /* !! */  = var7_5;
                            if (var7_5 == null) {
                                block27: {
                                    block26: {
                                        var8_8 /* !! */  = var1_1.getLocalUri();
                                        if (var8_8 /* !! */  == null) break block26;
                                        if (StringsKt.isBlank((CharSequence)var8_8 /* !! */ )) break block26;
                                        var2_11 = false;
                                        break block27;
                                    }
                                    var2_11 = true;
                                }
                                var8_8 /* !! */  = var7_5;
                                if (!var2_11) {
                                    block30: {
                                        block28: {
                                            block29: {
                                                var8_8 /* !! */  = Uri.parse((String)var1_1.getLocalUri());
                                                if (!Intrinsics.areEqual((Object)var8_8 /* !! */ .getScheme(), (Object)"file")) break block28;
                                                var9_9 /* !! */  = var8_8 /* !! */ .getPath();
                                                var7_5 = var9_9 /* !! */ ;
                                                if (var9_9 /* !! */  != null) break block29;
                                                var7_5 = "";
                                            }
                                            var10_12 = new File((String)var7_5);
                                            if (!var10_12.exists()) break block28;
                                            var8_8 /* !! */  = var0.getPackageName();
                                            var7_5 = new StringBuilder();
                                            var7_5 = var7_5.append((String)var8_8 /* !! */ ).append(".fileprovider").toString();
                                            var7_5 = FileProvider.getUriForFile((Context)var0, (String)var7_5, (File)var10_12);
                                            break block30;
                                        }
                                        var7_5 = var8_8 /* !! */ ;
                                    }
                                    var8_8 /* !! */  = var7_5;
                                }
                            }
                            var9_9 /* !! */  = var8_8 /* !! */ ;
                            if (var8_8 /* !! */  == null) {
                                var7_5 = new File(Environment.getExternalStoragePublicDirectory((String)Environment.DIRECTORY_DOWNLOADS), var1_1.getFileName());
                                var9_9 /* !! */  = var8_8 /* !! */ ;
                                if (!var7_5.exists()) break block31;
                                var8_8 /* !! */  = var0.getPackageName();
                                var9_9 /* !! */  = new StringBuilder();
                                var8_8 /* !! */  = var9_9 /* !! */ .append((String)var8_8 /* !! */ ).append(".fileprovider").toString();
                                var9_9 /* !! */  = FileProvider.getUriForFile((Context)var0, (String)var8_8 /* !! */ , (File)var7_5);
                            }
                        }
                        if (var9_9 /* !! */  != null) ** GOTO lbl75
                        Toast.makeText((Context)var0, (CharSequence)"Downloaded file not found.", (int)0).show();
                        return;
lbl75:
                        // 1 sources

                        var4_13 = StringsKt.endsWith((String)var1_1.getFileName(), (String)".mp3", (boolean)true);
                        var7_5 = "audio/mpeg";
                        if (var4_13) break block32;
                        if (StringsKt.equals((String)var1_1.getMimeType(), (String)"audio/mpeg", (boolean)true) || StringsKt.equals((String)var1_1.getMimeType(), (String)"audio/mp3", (boolean)true)) break block32;
                        var2_11 = false;
                        break block33;
                    }
                    var2_11 = true;
                }
                if (var2_11) break block34;
                var4_13 = StringsKt.isBlank((CharSequence)var1_1.getMimeType());
                if (var4_13) ** GOTO lbl93
                if (!Intrinsics.areEqual((Object)var1_1.getMimeType(), (Object)"*/*")) {
                    var7_5 = var1_1.getMimeType();
                    break block34;
                }
lbl93:
                // 3 sources

                var7_5 = StringsKt.substringAfterLast((String)var1_1.getFileName(), (char)'.', (String)"");
                var8_8 /* !! */  = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)var8_8 /* !! */ , (String)"ROOT");
                var7_5 = var7_5.toLowerCase((Locale)var8_8 /* !! */ );
                Intrinsics.checkNotNullExpressionValue((Object)var7_5, (String)"toLowerCase(...)");
                var8_8 /* !! */  = MimeTypeMap.getSingleton().getMimeTypeFromExtension((String)var7_5);
                var7_5 = var8_8 /* !! */ ;
                if (var8_8 /* !! */  != null) break block34;
                var7_5 = "*/*";
            }
            var8_8 /* !! */  = new Intent("android.intent.action.VIEW");
            var8_8 /* !! */ .setDataAndType((Uri)var9_9 /* !! */ , (String)var7_5);
            var8_8 /* !! */ .addFlags(1);
            var8_8 /* !! */ .addFlags(0x10000000);
            var0.startActivity((Intent)var8_8 /* !! */ );
            {
                catch (Exception var1_2) {
                    var1_3 = var1_2.getMessage();
                    Toast.makeText((Context)var0, (CharSequence)("Could not open file: " + var1_3), (int)0).show();
                    break block35;
                }
                catch (ActivityNotFoundException var7_7) {
                    var2_11 = var3_4;
                    if (!StringsKt.endsWith((String)var1_1.getFileName(), (String)".mp3", (boolean)true)) {
                        var2_11 = StringsKt.contains$default((CharSequence)var1_1.getMimeType(), (CharSequence)"audio", (boolean)false, (int)2, null) != false ? var3_4 : false;
                    }
                    if (var2_11) {
                        Toast.makeText((Context)var0, (CharSequence)"No compatible app found to open this MP3.", (int)0).show();
                        break block35;
                    }
                    var1_1 = var1_1.getFileName();
                    Toast.makeText((Context)var0, (CharSequence)("No compatible app found to open " + (String)var1_1), (int)0).show();
                }
            }
        }
    }
}

