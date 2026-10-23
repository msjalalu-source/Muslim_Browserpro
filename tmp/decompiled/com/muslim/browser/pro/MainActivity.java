/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Activity
 *  android.app.DownloadManager
 *  android.app.DownloadManager$Request
 *  android.content.ActivityNotFoundException
 *  android.content.BroadcastReceiver
 *  android.content.ClipData
 *  android.content.ContentResolver
 *  android.content.Context
 *  android.content.Intent
 *  android.content.IntentFilter
 *  android.graphics.Bitmap
 *  android.graphics.Color
 *  android.net.Uri
 *  android.net.http.SslError
 *  android.os.Bundle
 *  android.os.Environment
 *  android.os.Message
 *  android.util.Log
 *  android.view.View
 *  android.view.ViewGroup$LayoutParams
 *  android.view.Window
 *  android.webkit.CookieManager
 *  android.webkit.DownloadListener
 *  android.webkit.MimeTypeMap
 *  android.webkit.SslErrorHandler
 *  android.webkit.URLUtil
 *  android.webkit.ValueCallback
 *  android.webkit.WebChromeClient
 *  android.webkit.WebChromeClient$FileChooserParams
 *  android.webkit.WebResourceError
 *  android.webkit.WebResourceRequest
 *  android.webkit.WebResourceResponse
 *  android.webkit.WebSettings
 *  android.webkit.WebStorage
 *  android.webkit.WebView
 *  android.webkit.WebView$WebViewTransport
 *  android.webkit.WebViewClient
 *  androidx.activity.ComponentActivity
 *  androidx.activity.EdgeToEdge
 *  androidx.activity.compose.ComponentActivityKt
 *  androidx.activity.result.ActivityResult
 *  androidx.activity.result.ActivityResultLauncher
 *  androidx.activity.result.contract.ActivityResultContract
 *  androidx.activity.result.contract.ActivityResultContracts$StartActivityForResult
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.CompositionLocal
 *  androidx.compose.runtime.EffectsKt
 *  androidx.compose.runtime.State
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.ui.platform.AndroidCompositionLocals_androidKt
 *  androidx.core.content.ContextCompat
 *  androidx.core.view.WindowCompat
 *  androidx.lifecycle.LifecycleOwner
 *  androidx.lifecycle.LifecycleOwnerKt
 *  androidx.lifecycle.ViewModelLazy
 *  androidx.lifecycle.ViewModelProvider$Factory
 *  androidx.lifecycle.ViewModelStore
 *  androidx.lifecycle.compose.FlowExtKt
 *  androidx.lifecycle.viewmodel.CreationExtras
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.browser.DesktopCore
 *  com.muslim.browser.pro.browser.DownloadPolicy
 *  com.muslim.browser.pro.browser.DownloadPolicy$Result$Blocked
 *  com.muslim.browser.pro.browser.NavigationController
 *  com.muslim.browser.pro.browser.NavigationDecision$Allowed
 *  com.muslim.browser.pro.browser.NavigationDecision$Blocked
 *  com.muslim.browser.pro.browser.NavigationDecision$Download
 *  com.muslim.browser.pro.browser.NavigationDecision$Redirect
 *  com.muslim.browser.pro.browser.ProtectionEngine
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  com.muslim.browser.pro.ui.theme.ThemeKt
 *  kotlin.Deprecated
 *  kotlin.Lazy
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Pair
 *  kotlin.ReplaceWith
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.Boxing
 *  kotlin.coroutines.jvm.internal.ContinuationImpl
 *  kotlin.coroutines.jvm.internal.SpillingKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.ArrayIteratorKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$BooleanRef
 *  kotlin.jvm.internal.Reflection
 *  kotlin.reflect.KClass
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Dispatchers
 *  kotlinx.coroutines.Job
 *  kotlinx.coroutines.Job$DefaultImpls
 */
package com.muslim.browser.pro;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.MimeTypeMap;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.activity.ComponentActivity;
import androidx.activity.EdgeToEdge;
import androidx.activity.compose.ComponentActivityKt;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.CompositionLocal;
import androidx.compose.runtime.EffectsKt;
import androidx.compose.runtime.State;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.platform.AndroidCompositionLocals_androidKt;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LifecycleOwnerKt;
import androidx.lifecycle.ViewModelLazy;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.compose.FlowExtKt;
import androidx.lifecycle.viewmodel.CreationExtras;
import com.muslim.browser.pro.MainActivity;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda10;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda11;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda12;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda13;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda14;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda15;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda16;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda17;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda18;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda19;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda2;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda20;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda3;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda4;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda5;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda6;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda7;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda8;
import com.muslim.browser.pro.MainActivity$$ExternalSyntheticLambda9;
import com.muslim.browser.pro.MainActivity$createConfiguredWebView$webView$1$2$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.MainActivityKt;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.BrowserViewModel;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DesktopCore;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.DownloadPolicy;
import com.muslim.browser.pro.browser.DownloadProgressPoller;
import com.muslim.browser.pro.browser.DownloadProgressUpdate;
import com.muslim.browser.pro.browser.DownloadStatus;
import com.muslim.browser.pro.browser.FaviconManager;
import com.muslim.browser.pro.browser.NavigationController;
import com.muslim.browser.pro.browser.NavigationDecision;
import com.muslim.browser.pro.browser.ProtectionEngine;
import com.muslim.browser.pro.browser.SslSecurityPolicy;
import com.muslim.browser.pro.browser.TabWebViewManager;
import com.muslim.browser.pro.browser.WebViewConfigurator;
import com.muslim.browser.pro.ui.theme.AppTheme;
import com.muslim.browser.pro.ui.theme.ThemeKt;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import kotlin.Deprecated;
import kotlin.Lazy;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Pair;
import kotlin.ReplaceWith;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.ArrayIteratorKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.jvm.internal.Reflection;
import kotlin.reflect.KClass;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;

@Metadata(d1={"\u0000\u0089\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\u0010\u0011\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010%\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010!\n\u0002\u0018\u0002\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0018\u0002*\u0001*\b\u0007\u0018\u0000 Q2\u00020\u0001:\u0001QB\u0007\u00a2\u0006\u0004\b\u0002\u0010\u0003J\b\u0010#\u001a\u00020$H\u0002J\u0010\u0010%\u001a\u00020&H\u0080@\u00a2\u0006\u0004\b'\u0010(J\u0015\u0010,\u001a\u00020$2\u0006\u0010-\u001a\u00020.H\u0000\u00a2\u0006\u0002\b/J\u0012\u00100\u001a\u00020$2\b\u00101\u001a\u0004\u0018\u000102H\u0015J\u0015\u00103\u001a\u00020\u000b2\u0006\u00104\u001a\u00020\u0019H\u0000\u00a2\u0006\u0002\b5J\u0010\u00106\u001a\u00020$2\u0006\u00107\u001a\u00020\u0019H\u0002J\u0010\u00108\u001a\u00020$2\u0006\u00107\u001a\u00020\u0019H\u0002J2\u00109\u001a\u00020$2\u0006\u0010:\u001a\u00020\u00192\n\b\u0002\u0010;\u001a\u0004\u0018\u00010\u00192\n\b\u0002\u0010<\u001a\u0004\u0018\u00010\u00192\n\b\u0002\u0010=\u001a\u0004\u0018\u00010\u0019J\u001f\u0010>\u001a\u00020&2\b\u0010?\u001a\u0004\u0018\u00010\u000b2\u0006\u0010:\u001a\u00020\u0019H\u0000\u00a2\u0006\u0002\b@J\b\u0010A\u001a\u00020$H\u0002J\b\u0010B\u001a\u00020$H\u0002J\u0015\u0010C\u001a\u00020$2\u0006\u0010D\u001a\u00020EH\u0000\u00a2\u0006\u0002\bFJ\u0015\u0010G\u001a\u00020$2\u0006\u0010H\u001a\u00020&H\u0000\u00a2\u0006\u0002\bIJ\u0015\u0010J\u001a\u00020$2\u0006\u0010H\u001a\u00020&H\u0000\u00a2\u0006\u0002\bKJ\u0015\u0010L\u001a\u00020$2\u0006\u0010H\u001a\u00020&H\u0000\u00a2\u0006\u0002\bMJ\b\u0010N\u001a\u00020$H\u0014J\b\u0010O\u001a\u00020$H\u0014J\b\u0010P\u001a\u00020$H\u0014R\u001b\u0010\u0004\u001a\u00020\u00058BX\u0082\u0084\u0002\u00a2\u0006\f\n\u0004\b\b\u0010\t\u001a\u0004\b\u0006\u0010\u0007R\u0010\u0010\n\u001a\u0004\u0018\u00010\u000bX\u0082\u000e\u00a2\u0006\u0002\n\u0000R$\u0010\u000e\u001a\u00020\r2\u0006\u0010\f\u001a\u00020\r@@X\u0086.\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000f\u0010\u0010\"\u0004\b\u0011\u0010\u0012R\u001c\u0010\u0013\u001a\u0010\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00160\u0015\u0018\u00010\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000Rf\u0010\u0017\u001aZ\u0012\f\u0012\n \u001a*\u0004\u0018\u00010\u00190\u0019\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u001c \u001a*\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001b0\u001b \u001a*,\u0012\f\u0012\n \u001a*\u0004\u0018\u00010\u00190\u0019\u0012\u0018\u0012\u0016\u0012\u0004\u0012\u00020\u001c \u001a*\n\u0012\u0004\u0012\u00020\u001c\u0018\u00010\u001b0\u001b\u0018\u00010\u001d0\u0018X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020 0\u001fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010!\u001a\u0004\u0018\u00010\"X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0010\u0010)\u001a\u00020*X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010+\u00a8\u0006R\u00b2\u0006\n\u0010S\u001a\u00020TX\u008a\u0084\u0002"}, d2={"Lcom/muslim/browser/pro/MainActivity;", "Landroidx/activity/ComponentActivity;", "<init>", "()V", "viewModel", "Lcom/muslim/browser/pro/browser/BrowserViewModel;", "getViewModel", "()Lcom/muslim/browser/pro/browser/BrowserViewModel;", "viewModel$delegate", "Lkotlin/Lazy;", "webViewInstance", "Landroid/webkit/WebView;", "value", "Lcom/muslim/browser/pro/browser/TabWebViewManager;", "tabWebViewManager", "getTabWebViewManager", "()Lcom/muslim/browser/pro/browser/TabWebViewManager;", "setTabWebViewManager$app", "(Lcom/muslim/browser/pro/browser/TabWebViewManager;)V", "fileChooserCallback", "Landroid/webkit/ValueCallback;", "", "Landroid/net/Uri;", "pendingSslHandlers", "", "", "kotlin.jvm.PlatformType", "", "Landroid/webkit/SslErrorHandler;", "", "fileChooserLauncher", "Landroidx/activity/result/ActivityResultLauncher;", "Landroid/content/Intent;", "progressPollingJob", "Lkotlinx/coroutines/Job;", "startProgressPolling", "", "queryActiveDownloads", "", "queryActiveDownloads$app", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "downloadReceiver", "com/muslim/browser/pro/MainActivity$downloadReceiver$1", "Lcom/muslim/browser/pro/MainActivity$downloadReceiver$1;", "checkDownloadStatus", "downloadId", "", "checkDownloadStatus$app", "onCreate", "savedInstanceState", "Landroid/os/Bundle;", "createConfiguredWebView", "tabId", "createConfiguredWebView$app", "onSslPromptProceed", "host", "onSslPromptCancel", "startDownload", "url", "userAgent", "contentDisposition", "mimetype", "handleUrlNavigation", "view", "handleUrlNavigation$app", "clearAllData", "clearCacheAndCookies", "setDesktopArchitecture", "architecture", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "setDesktopArchitecture$app", "setDesktopMode", "enabled", "setDesktopMode$app", "setDesktopMode4", "setDesktopMode4$app", "setWindows10Touch", "setWindows10Touch$app", "onPause", "onResume", "onDestroy", "Companion", "app", "uiState", "Lcom/muslim/browser/pro/browser/BrowserUiState;"}, k=1, mv={2, 2, 0}, xi=48)
public final class MainActivity
extends ComponentActivity {
    public static final int $stable;
    public static final Companion Companion;
    public static final String DESKTOP_USER_AGENT = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private static final byte[] EMPTY_BLOCKED_BYTES;
    public static final String WINDOWS_10_TOUCH_USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
    private final downloadReceiver.1 downloadReceiver;
    private ValueCallback<Uri[]> fileChooserCallback;
    private final ActivityResultLauncher<Intent> fileChooserLauncher;
    private final Map<String, List<SslErrorHandler>> pendingSslHandlers;
    private Job progressPollingJob;
    public TabWebViewManager tabWebViewManager;
    private final Lazy viewModel$delegate;
    private WebView webViewInstance;

    static {
        Companion = new Companion(null);
        $stable = 8;
        EMPTY_BLOCKED_BYTES = new byte[0];
    }

    public MainActivity() {
        ComponentActivity componentActivity = this;
        Function0 function0 = (Function0)new Function0<ViewModelProvider.Factory>(componentActivity){
            final ComponentActivity $this_viewModels;
            {
                this.$this_viewModels = componentActivity;
                super(0);
            }

            public final ViewModelProvider.Factory invoke() {
                return this.$this_viewModels.getDefaultViewModelProviderFactory();
            }
        };
        KClass kClass = Reflection.getOrCreateKotlinClass(BrowserViewModel.class);
        Function0 function02 = (Function0)new Function0<ViewModelStore>(componentActivity){
            final ComponentActivity $this_viewModels;
            {
                this.$this_viewModels = componentActivity;
                super(0);
            }

            public final ViewModelStore invoke() {
                return this.$this_viewModels.getViewModelStore();
            }
        };
        this.viewModel$delegate = (Lazy)new ViewModelLazy(kClass, function02, function0, (Function0)new Function0<CreationExtras>(null, componentActivity){
            final Function0 $extrasProducer;
            final ComponentActivity $this_viewModels;
            {
                this.$extrasProducer = function0;
                this.$this_viewModels = componentActivity;
                super(0);
            }

            public final CreationExtras invoke() {
                Function0 function0;
                block3: {
                    block2: {
                        function0 = this.$extrasProducer;
                        if (function0 == null) break block2;
                        CreationExtras creationExtras = (CreationExtras)function0.invoke();
                        function0 = creationExtras;
                        if (creationExtras != null) break block3;
                    }
                    function0 = this.$this_viewModels.getDefaultViewModelCreationExtras();
                }
                return function0;
            }
        });
        this.pendingSslHandlers = Collections.synchronizedMap(new LinkedHashMap());
        function02 = (ActivityResultContract)new ActivityResultContracts.StartActivityForResult();
        this.fileChooserLauncher = this.registerForActivityResult((ActivityResultContract)function02, new MainActivity$$ExternalSyntheticLambda3(this));
        this.downloadReceiver = new BroadcastReceiver(this){
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public void onReceive(Context object, Intent intent) {
                long l;
                object = intent != null ? intent.getAction() : null;
                if (Intrinsics.areEqual((Object)object, (Object)"android.intent.action.DOWNLOAD_COMPLETE") && (l = intent.getLongExtra("extra_download_id", -1L)) != -1L) {
                    this.this$0.checkDownloadStatus$app(l);
                }
            }
        };
    }

    public static final /* synthetic */ byte[] access$getEMPTY_BLOCKED_BYTES$cp() {
        return EMPTY_BLOCKED_BYTES;
    }

    public static final /* synthetic */ ValueCallback access$getFileChooserCallback$p(MainActivity mainActivity) {
        return mainActivity.fileChooserCallback;
    }

    public static final /* synthetic */ ActivityResultLauncher access$getFileChooserLauncher$p(MainActivity mainActivity) {
        return mainActivity.fileChooserLauncher;
    }

    public static final /* synthetic */ Map access$getPendingSslHandlers$p(MainActivity mainActivity) {
        return mainActivity.pendingSslHandlers;
    }

    public static final /* synthetic */ Job access$getProgressPollingJob$p(MainActivity mainActivity) {
        return mainActivity.progressPollingJob;
    }

    public static final /* synthetic */ BrowserViewModel access$getViewModel(MainActivity mainActivity) {
        return mainActivity.getViewModel();
    }

    public static final /* synthetic */ WebView access$getWebViewInstance$p(MainActivity mainActivity) {
        return mainActivity.webViewInstance;
    }

    public static final /* synthetic */ void access$setFileChooserCallback$p(MainActivity mainActivity, ValueCallback valueCallback) {
        mainActivity.fileChooserCallback = valueCallback;
    }

    public static final /* synthetic */ void access$setProgressPollingJob$p(MainActivity mainActivity, Job job) {
        mainActivity.progressPollingJob = job;
    }

    private final void clearAllData() {
        try {
            this.getTabWebViewManager().clearAllData();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            this.getViewModel().clearAllHistory();
            this.getViewModel().onHistoryCleared();
            FaviconManager.INSTANCE.clearCache((Context)this);
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            WebStorage.getInstance().deleteAllData();
            DownloadPolicy.INSTANCE.clearTrustedOrigin();
            this.getViewModel().showToast("All browsing data cleared.");
        }
        catch (Exception exception) {
            this.getViewModel().showToast("Browsing data cleared.");
        }
    }

    private final void clearCacheAndCookies() {
        try {
            this.getTabWebViewManager().clearAllData();
        }
        catch (Exception exception) {
            // empty catch block
        }
        try {
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookies(null);
            cookieManager.flush();
            WebStorage.getInstance().deleteAllData();
            this.getViewModel().showToast("Cache and cookies cleared.");
        }
        catch (Exception exception) {
            this.getViewModel().showToast("Cache and cookies cleared.");
        }
    }

    static final void createConfiguredWebView$lambda$36$lambda$35(MainActivity mainActivity, String string, String string2, String string3, String string4, long l) {
        Intrinsics.checkNotNull((Object)string);
        mainActivity.startDownload(string, string2, string3, string4);
    }

    static final void fileChooserLauncher$lambda$0(MainActivity mainActivity, ActivityResult activityResult) {
        Intrinsics.checkNotNullParameter((Object)activityResult, (String)"result");
        ValueCallback<Uri[]> valueCallback = mainActivity.fileChooserCallback;
        mainActivity.fileChooserCallback = null;
        if (valueCallback == null) {
            return;
        }
        valueCallback.onReceiveValue((Object)Companion.parseFileChooserResult(activityResult.getResultCode(), activityResult.getData(), mainActivity.getContentResolver()));
    }

    private final BrowserViewModel getViewModel() {
        return (BrowserViewModel)((Object)this.viewModel$delegate.getValue());
    }

    static final WebView onCreate$lambda$2(MainActivity mainActivity, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        return mainActivity.createConfiguredWebView$app(string);
    }

    static final Unit onCreate$lambda$3(MainActivity mainActivity, String string, Bundle bundle) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)bundle, (String)"bundle");
        mainActivity.getViewModel().saveTabState(string, bundle);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34(MainActivity mainActivity, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C303@12931L29,307@13212L213,307@13179L246,314@13489L7,327@14260L980,327@14212L1028:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            State state;
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1419673879, (int)n, (int)-1, (String)"com.muslim.browser.pro.MainActivity.onCreate.<anonymous> (MainActivity.kt:303)");
            }
            boolean bl = MainActivity.onCreate$lambda$34$lambda$10((State<BrowserUiState>)(state = FlowExtKt.collectAsStateWithLifecycle(mainActivity.getViewModel().getUiState(), null, null, null, (Composer)composer, (int)0, (int)7))).getAppTheme() == AppTheme.BLACK_WHITE;
            boolean bl2 = MainActivity.onCreate$lambda$34$lambda$10((State<BrowserUiState>)state).getAppTheme() == AppTheme.FORCE_DARK_ON;
            AppTheme appTheme = MainActivity.onCreate$lambda$34$lambda$10((State<BrowserUiState>)state).getAppTheme();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1492991700, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl3 = composer.changedInstance((Object)mainActivity);
            boolean bl4 = composer.changed(bl2);
            Object object = composer.rememberedValue();
            if (bl3 | bl4 || object == Composer.Companion.getEmpty()) {
                object = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(mainActivity, bl2, null){
                    final boolean $isForceDark;
                    int label;
                    final MainActivity this$0;
                    {
                        this.this$0 = mainActivity;
                        this.$isForceDark = bl;
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
                        this.this$0.getTabWebViewManager().syncAllLiveWebViewsTheme(this.$isForceDark);
                        object = MainActivity.access$getWebViewInstance$p(this.this$0);
                        if (object != null) {
                            boolean bl = this.$isForceDark;
                            MainActivity.Companion.applyWebViewTheme((WebView)object, bl);
                        }
                        return Unit.INSTANCE;
                    }
                };
                composer.updateRememberedValue(object);
            }
            object = (Function2)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            EffectsKt.LaunchedEffect((Object)appTheme, (Function2)object, (Composer)composer, (int)0);
            object = (CompositionLocal)AndroidCompositionLocals_androidKt.getLocalView();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)2023513938, (String)"CC:CompositionLocal.kt#9igjgp");
            object = composer.consume((CompositionLocal)object);
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            appTheme = (View)object;
            if (!appTheme.isInEditMode()) {
                composer.startReplaceGroup(962230002);
                ComposerKt.sourceInformation((Composer)composer, (String)"316@13587L597,316@13576L608");
                ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1492979316, (String)"CC(remember):MainActivity.kt#9igjgp");
                bl4 = composer.changedInstance((Object)appTheme);
                bl2 = composer.changed(bl);
                object = composer.rememberedValue();
                if (bl4 | bl2 || object == Composer.Companion.getEmpty()) {
                    object = new MainActivity$$ExternalSyntheticLambda0((View)appTheme, bl);
                    composer.updateRememberedValue(object);
                }
                object = (Function0)object;
                ComposerKt.sourceInformationMarkerEnd((Composer)composer);
                EffectsKt.SideEffect((Function0)object, (Composer)composer, (int)0);
            } else {
                composer.startReplaceGroup(948783659);
            }
            composer.endReplaceGroup();
            ThemeKt.MyApplicationTheme((AppTheme)MainActivity.onCreate$lambda$34$lambda$10((State<BrowserUiState>)state).getAppTheme(), (Function2)((Function2)ComposableLambdaKt.rememberComposableLambda((int)-314134582, (boolean)true, (Object)new MainActivity$$ExternalSyntheticLambda1(mainActivity), (Composer)composer, (int)54)), (Composer)composer, (int)48, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    private static final BrowserUiState onCreate$lambda$34$lambda$10(State<BrowserUiState> state) {
        return (BrowserUiState)state.getValue();
    }

    static final Unit onCreate$lambda$34$lambda$13$lambda$12(View view, boolean bl) {
        Object object = view.getContext();
        boolean bl2 = object instanceof Activity;
        Window window = null;
        object = bl2 ? (Activity)object : null;
        if (object != null) {
            window = object.getWindow();
        }
        if (window != null) {
            view = WindowCompat.getInsetsController((Window)window, (View)view);
            Intrinsics.checkNotNullExpressionValue((Object)view, (String)"getInsetsController(...)");
            view.setAppearanceLightStatusBars(bl ^ true);
            view.setAppearanceLightNavigationBars(bl ^ true);
            view = window.getDecorView();
            int n = bl ? -16777216 : Color.parseColor((String)"#F4F6F9");
            view.setBackgroundColor(n);
        }
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33(MainActivity object, Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C331@14429L18,332@14494L26,333@14564L38,334@14647L39,335@14733L41,336@14811L36,337@14883L35,338@14964L117,341@15128L80,328@14278L948:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-314134582, (int)n, (int)-1, (String)"com.muslim.browser.pro.MainActivity.onCreate.<anonymous>.<anonymous> (MainActivity.kt:328)");
            }
            BrowserViewModel browserViewModel = object.getViewModel();
            TabWebViewManager tabWebViewManager = object.getTabWebViewManager();
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235617156, (String)"CC(remember):MainActivity.kt#9igjgp");
            boolean bl = composer.changedInstance(object);
            Object object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda12((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function0 function0 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235615068, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda13((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function0 function02 = (Function0)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235612816, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda14((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function1 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235610159, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda15((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function12 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235607405, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda16((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function13 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235604914, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda17((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function14 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235602611, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda18((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function15 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235599937, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (bl || object2 == Composer.Companion.getEmpty()) {
                object2 = new MainActivity$$ExternalSyntheticLambda19((MainActivity)((Object)object));
                composer.updateRememberedValue(object2);
            }
            Function1 function16 = (Function1)object2;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            ComposerKt.sourceInformationMarkerStart((Composer)composer, (int)-1235594726, (String)"CC(remember):MainActivity.kt#9igjgp");
            bl = composer.changedInstance(object);
            object2 = composer.rememberedValue();
            if (!bl && object2 != Composer.Companion.getEmpty()) {
                object = object2;
            } else {
                object = new MainActivity$$ExternalSyntheticLambda20((MainActivity)((Object)object));
                composer.updateRememberedValue(object);
            }
            object = (Function1)object;
            ComposerKt.sourceInformationMarkerEnd((Composer)composer);
            MainActivityKt.BrowserApp(browserViewModel, tabWebViewManager, (Function0<Unit>)function0, (Function0<Unit>)function02, (Function1<? super Boolean, Unit>)function1, (Function1<? super Boolean, Unit>)function12, (Function1<? super Boolean, Unit>)function13, (Function1<? super String, Unit>)function14, (Function1<? super String, Unit>)function15, (Function1<? super String, Boolean>)function16, (Function1<? super WebView, Unit>)object, composer, 0, 0, 0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$15$lambda$14(MainActivity mainActivity) {
        mainActivity.clearAllData();
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$17$lambda$16(MainActivity mainActivity) {
        mainActivity.clearCacheAndCookies();
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$19$lambda$18(MainActivity mainActivity, boolean bl) {
        mainActivity.setDesktopMode$app(bl);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$21$lambda$20(MainActivity mainActivity, boolean bl) {
        mainActivity.setDesktopMode4$app(bl);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$23$lambda$22(MainActivity mainActivity, boolean bl) {
        mainActivity.setWindows10Touch$app(bl);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$25$lambda$24(MainActivity mainActivity, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        mainActivity.onSslPromptProceed(string);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$27$lambda$26(MainActivity mainActivity, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        mainActivity.onSslPromptCancel(string);
        return Unit.INSTANCE;
    }

    static final boolean onCreate$lambda$34$lambda$33$lambda$30$lambda$29(MainActivity mainActivity, String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        WebView webView2 = mainActivity.webViewInstance;
        boolean bl = webView2 != null ? mainActivity.handleUrlNavigation$app(webView2, string) : false;
        return bl;
    }

    static final Unit onCreate$lambda$34$lambda$33$lambda$32$lambda$31(MainActivity mainActivity, WebView webView2) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"newWv");
        mainActivity.webViewInstance = webView2;
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$4(WebView webView2, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"wv");
        Companion.applyWebViewTheme(webView2, bl);
        return Unit.INSTANCE;
    }

    static final Unit onCreate$lambda$5(MainActivity object, WebView webView2, boolean bl) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"wv");
        boolean bl2 = ((BrowserUiState)((MainActivity)((Object)object)).getViewModel().getUiState().getValue()).isWindows10TouchEnabled();
        object = WebViewConfigurator.INSTANCE;
        String string = webView2.getUrl();
        ((WebViewConfigurator)object).syncDesktopMode(webView2, string, bl, true, bl2);
        return Unit.INSTANCE;
    }

    static final boolean onCreate$lambda$6(MainActivity mainActivity) {
        return ((BrowserUiState)mainActivity.getViewModel().getUiState().getValue()).isDesktopModeEnabled();
    }

    static final Unit onCreate$lambda$7(WebView webView2, DesktopArchitecture desktopArchitecture) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"wv");
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"arch");
        WebViewConfigurator webViewConfigurator = WebViewConfigurator.INSTANCE;
        String string = webView2.getUrl();
        webViewConfigurator.syncDesktopArchitecture(webView2, string, desktopArchitecture, true);
        return Unit.INSTANCE;
    }

    static final DesktopArchitecture onCreate$lambda$8(MainActivity mainActivity) {
        return ((BrowserUiState)mainActivity.getViewModel().getUiState().getValue()).getDesktopArchitecture();
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final void onSslPromptCancel(String webView2) {
        boolean bl;
        this.getViewModel().dismissSslWarning();
        Map<String, List<SslErrorHandler>> map2 = this.pendingSslHandlers;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"pendingSslHandlers");
        synchronized (map2) {
            SslErrorHandler sslErrorHandler = this.pendingSslHandlers.remove(webView2);
            webView2 = sslErrorHandler;
            if (sslErrorHandler == null) {
                webView2 = CollectionsKt.emptyList();
            }
        }
        for (SslErrorHandler sslErrorHandler : (Iterable)webView2) {
            if (sslErrorHandler == null) continue;
            try {
                sslErrorHandler.cancel();
            }
            catch (Exception sslErrorHandler2) {
                Log.e((String)"SSL", (String)"Error executing handler.cancel()", (Throwable)sslErrorHandler2);
            }
        }
        this.getViewModel().onPageCommitVisible();
        webView2 = this.webViewInstance;
        boolean bl2 = bl = false;
        if (webView2 != null) {
            bl2 = bl;
            if (webView2.canGoBack()) {
                bl2 = true;
            }
        }
        if (bl2) {
            webView2 = this.webViewInstance;
            if (webView2 != null) {
                webView2.goBack();
            }
        } else {
            this.getViewModel().goHome();
        }
        this.getViewModel().showToast("Connection cancelled.");
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final void onSslPromptProceed(String webView2) {
        SslSecurityPolicy.INSTANCE.approveHostForSession((String)webView2);
        this.getViewModel().dismissSslWarning();
        Map<String, List<SslErrorHandler>> map2 = this.pendingSslHandlers;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"pendingSslHandlers");
        synchronized (map2) {
            SslErrorHandler sslErrorHandler = this.pendingSslHandlers.remove(webView2);
            webView2 = sslErrorHandler;
            if (sslErrorHandler == null) {
                webView2 = CollectionsKt.emptyList();
            }
        }
        for (SslErrorHandler sslErrorHandler : (Iterable)webView2) {
            if (sslErrorHandler == null) continue;
            try {
                sslErrorHandler.proceed();
            }
            catch (Exception sslErrorHandler2) {
                Log.e((String)"SSL", (String)"Error executing handler.proceed()", (Throwable)sslErrorHandler2);
            }
        }
        webView2 = this.webViewInstance;
        if (webView2 != null) {
            webView2.reload();
        }
    }

    public static /* synthetic */ void startDownload$default(MainActivity mainActivity, String string, String string2, String string3, String string4, int n, Object object) {
        if ((n & 2) != 0) {
            string2 = null;
        }
        if ((n & 4) != 0) {
            string3 = null;
        }
        if ((n & 8) != 0) {
            string4 = null;
        }
        mainActivity.startDownload(string, string2, string3, string4);
    }

    private final void startProgressPolling() {
        boolean bl;
        Job job = this.progressPollingJob;
        boolean bl2 = bl = false;
        if (job != null) {
            bl2 = bl;
            if (job.isActive()) {
                bl2 = true;
            }
        }
        if (bl2) {
            return;
        }
        this.progressPollingJob = BuildersKt.launch$default((CoroutineScope)((CoroutineScope)LifecycleOwnerKt.getLifecycleScope((LifecycleOwner)((LifecycleOwner)this))), null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, null){
            private Object L$0;
            boolean Z$0;
            int label;
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
                super(2, continuation);
            }

            public final Continuation<Unit> create(Object object, Continuation<?> object2) {
                object2 = new /* invalid duplicate definition of identical inner class */;
                object2.L$0 = object;
                return object2;
            }

            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            /*
             * Exception decompiling
             */
            public final Object invokeSuspend(Object var1_1) {
                /*
                 * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
                 * 
                 * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [0[SWITCH], 4[CASE]], but top level block is 5[WHILELOOP]
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
                 *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
                 *     at org.benf.cfr.reader.entities.Method.dump(Method.java:598)
                 *     at org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperAnonymousInner.dumpWithArgs(ClassFileDumperAnonymousInner.java:87)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.ConstructorInvokationAnonymousInner.dumpInner(ConstructorInvokationAnonymousInner.java:82)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:142)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.dumpInner(CastExpression.java:114)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:139)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.CastExpression.dumpInner(CastExpression.java:114)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:142)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:98)
                 *     at org.benf.cfr.reader.state.TypeUsageCollectingDumper.dump(TypeUsageCollectingDumper.java:194)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.StaticFunctionInvokation.dumpInner(StaticFunctionInvokation.java:143)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dumpWithOuterPrecedence(AbstractExpression.java:142)
                 *     at org.benf.cfr.reader.bytecode.analysis.parse.expression.AbstractExpression.dump(AbstractExpression.java:98)
                 *     at org.benf.cfr.reader.state.TypeUsageCollectingDumper.dump(TypeUsageCollectingDumper.java:194)
                 *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.StructuredAssignment.dump(StructuredAssignment.java:69)
                 *     at org.benf.cfr.reader.state.TypeUsageCollectingDumper.dump(TypeUsageCollectingDumper.java:194)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:220)
                 *     at org.benf.cfr.reader.bytecode.analysis.structured.statement.Block.dump(Block.java:564)
                 *     at org.benf.cfr.reader.state.TypeUsageCollectingDumper.dump(TypeUsageCollectingDumper.java:194)
                 *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.dump(Op04StructuredStatement.java:220)
                 *     at org.benf.cfr.reader.entities.attributes.AttributeCode.dump(AttributeCode.java:135)
                 *     at org.benf.cfr.reader.state.TypeUsageCollectingDumper.dump(TypeUsageCollectingDumper.java:194)
                 *     at org.benf.cfr.reader.entities.Method.dump(Method.java:627)
                 *     at org.benf.cfr.reader.entities.classfilehelpers.AbstractClassFileDumper.dumpMethods(AbstractClassFileDumper.java:211)
                 *     at org.benf.cfr.reader.entities.classfilehelpers.ClassFileDumperNormal.dump(ClassFileDumperNormal.java:70)
                 *     at org.benf.cfr.reader.entities.ClassFile.dump(ClassFile.java:1167)
                 *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:952)
                 *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
                 *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
                 *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
                 *     at org.benf.cfr.reader.Main.main(Main.java:54)
                 */
                throw new IllegalStateException("Decompilation failed");
            }
        }), (int)3, null);
    }

    public final void checkDownloadStatus$app(long l) {
        BuildersKt.launch$default((CoroutineScope)((CoroutineScope)LifecycleOwnerKt.getLifecycleScope((LifecycleOwner)((LifecycleOwner)this))), null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this, l, null){
            final long $downloadId;
            Object L$0;
            int label;
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
                this.$downloadId = l;
                super(2, continuation);
            }

            public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                return (Continuation)new /* invalid duplicate definition of identical inner class */;
            }

            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            public final Object invokeSuspend(Object object) {
                block14: {
                    Object object2;
                    Object object3;
                    Object object4;
                    block15: {
                        object4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                        switch (this.label) {
                            default: {
                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                            }
                            case 2: {
                                DownloadManager downloadManager = (DownloadManager)this.L$0;
                                ResultKt.throwOnFailure((Object)object);
                                break block14;
                            }
                            case 1: {
                                object3 = (DownloadManager)this.L$0;
                                ResultKt.throwOnFailure((Object)object);
                                object2 = object;
                                object = object3;
                                break;
                            }
                            case 0: {
                                ResultKt.throwOnFailure((Object)object);
                                object = this.this$0.getSystemService("download");
                                object = object instanceof DownloadManager ? (DownloadManager)object : null;
                                object2 = object;
                                if (object != null) {
                                    object3 = DownloadProgressPoller.INSTANCE;
                                    long l = this.$downloadId;
                                    object2 = (Continuation)this;
                                    this.L$0 = SpillingKt.nullOutSpilledVariable((Object)object);
                                    this.label = 1;
                                    object2 = object3 = ((DownloadProgressPoller)object3).querySingleDownloadProgress((DownloadManager)object, l, (Continuation<? super DownloadProgressUpdate>)object2);
                                    if (object3 != object4) break;
                                    return object4;
                                }
                                break block15;
                            }
                        }
                        object3 = (DownloadProgressUpdate)object2;
                        object2 = object;
                        if (object3 != null) {
                            object2 = ((DownloadProgressUpdate)object3).getStatus();
                            switch (checkDownloadStatus.WhenMappings.$EnumSwitchMapping$0[((Enum)object2).ordinal()]) {
                                default: {
                                    throw new NoWhenBranchMatchedException();
                                }
                                case 4: 
                                case 5: {
                                    MainActivity.access$getViewModel(this.this$0).updateDownloadProgress(((DownloadProgressUpdate)object3).getDownloadId(), ((DownloadProgressUpdate)object3).getStatus(), ((DownloadProgressUpdate)object3).getDownloadedBytes(), ((DownloadProgressUpdate)object3).getTotalBytes(), ((DownloadProgressUpdate)object3).getLocalUri());
                                    object2 = object;
                                    break;
                                }
                                case 1: 
                                case 2: 
                                case 3: {
                                    MainActivity.access$getViewModel(this.this$0).updateDownloadStatus(((DownloadProgressUpdate)object3).getDownloadId(), ((DownloadProgressUpdate)object3).getStatus(), ((DownloadProgressUpdate)object3).getLocalUri(), ((DownloadProgressUpdate)object3).getDownloadedBytes(), ((DownloadProgressUpdate)object3).getTotalBytes());
                                    object2 = object;
                                }
                            }
                        }
                    }
                    object = this.this$0;
                    object3 = (Continuation)this;
                    this.L$0 = SpillingKt.nullOutSpilledVariable((Object)object2);
                    this.label = 2;
                    if ((object = ((MainActivity)((Object)object)).queryActiveDownloads$app((Continuation<? super Boolean>)object3)) == object4) {
                        return object4;
                    }
                }
                if (!((Boolean)object).booleanValue()) {
                    object = MainActivity.access$getProgressPollingJob$p(this.this$0);
                    if (object != null) {
                        Job.DefaultImpls.cancel$default((Job)object, null, (int)1, null);
                    }
                    MainActivity.access$setProgressPollingJob$p(this.this$0, null);
                }
                return Unit.INSTANCE;
            }
        }), (int)3, null);
    }

    public final WebView createConfiguredWebView$app(String object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"tabId");
        boolean bl = ((BrowserUiState)this.getViewModel().getUiState().getValue()).getAppTheme() == AppTheme.FORCE_DARK_ON;
        DesktopArchitecture desktopArchitecture = ((BrowserUiState)this.getViewModel().getUiState().getValue()).getDesktopArchitecture();
        WebView webView2 = new WebView((Context)this);
        int n = System.identityHashCode(webView2);
        Log.d((String)"DESKTOP_DEBUG", (String)("createConfiguredWebView: tabId=" + (String)object + ", instance=" + n + ", architecture=" + desktopArchitecture + ", isForceDark=" + bl));
        webView2.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        object = WebViewConfigurator.INSTANCE;
        WebViewConfigurator.configureBaseSettings$default((WebViewConfigurator)object, webView2, bl, false, false, desktopArchitecture, 12, null);
        webView2.setWebViewClient(new WebViewClient(this){
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            private final boolean processUrlLoading(WebView webView2, String string) {
                Log.d((String)"DIAGNOSTIC", (String)("processUrlLoading: URL=" + string));
                return this.this$0.handleUrlNavigation$app(webView2, string);
            }

            public void onPageCommitVisible(WebView webView2, String string) {
                WebSettings webSettings;
                super.onPageCommitVisible(webView2, string);
                if (webView2 != null && (webSettings = webView2.getSettings()) != null) {
                    webSettings.setCacheMode(-1);
                }
                webSettings = ((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).getDesktopArchitecture();
                if (webView2 != null) {
                    DesktopCore.INSTANCE.handlePageLifecycle(webView2, (DesktopArchitecture)webSettings, string);
                }
                MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
            }

            public void onPageFinished(WebView webView2, String string) {
                super.onPageFinished(webView2, string);
                Object object = ((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).getDesktopArchitecture();
                if (webView2 != null) {
                    DesktopCore.INSTANCE.handlePageLifecycle(webView2, object, string);
                }
                int n = System.identityHashCode(webView2);
                Object var7_5 = null;
                object = webView2 != null && (object = webView2.getSettings()) != null ? object.getUserAgentString() : null;
                Log.d((String)"DESKTOP_DEBUG", (String)("onPageFinished: instance=" + n + ", URL=" + string + ", UA=" + (String)object));
                if (string != null) {
                    BrowserViewModel browserViewModel = MainActivity.access$getViewModel(this.this$0);
                    object = var7_5;
                    if (webView2 != null) {
                        object = webView2.getTitle();
                    }
                    boolean bl = false;
                    boolean bl2 = webView2 != null ? webView2.canGoBack() : false;
                    if (webView2 != null) {
                        bl = webView2.canGoForward();
                    }
                    browserViewModel.onPageFinished(string, (String)object, bl2, bl);
                }
                if (webView2 != null) {
                    MainActivity.Companion.applyWebViewTheme(webView2, MainActivity.Companion.isDarkThemeActive());
                }
            }

            public void onPageStarted(WebView webView2, String string, Bitmap object) {
                super.onPageStarted(webView2, string, object);
                DesktopArchitecture desktopArchitecture = ((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).getDesktopArchitecture();
                Object var5_5 = null;
                if (webView2 != null) {
                    switch (createConfiguredWebView.webView.1.WhenMappings.$EnumSwitchMapping$0[desktopArchitecture.ordinal()]) {
                        default: {
                            throw new NoWhenBranchMatchedException();
                        }
                        case 4: {
                            object = null;
                            break;
                        }
                        case 2: 
                        case 3: {
                            if (WebViewConfigurator.INSTANCE.isAuthenticationUrl(string)) {
                                object = null;
                                break;
                            }
                            object = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
                            break;
                        }
                        case 1: {
                            object = WebViewConfigurator.INSTANCE.isAuthenticationUrl(string) ? null : "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36";
                        }
                    }
                    if (!Intrinsics.areEqual((Object)webView2.getSettings().getUserAgentString(), (Object)object)) {
                        webView2.getSettings().setUserAgentString((String)object);
                    }
                    if (desktopArchitecture == DesktopArchitecture.WINDOWS_10_TOUCH && !WebViewConfigurator.INSTANCE.isAuthenticationUrl(string)) {
                        WebViewConfigurator.INSTANCE.injectWindows10TouchProfileIfEnabled(webView2, true);
                    }
                }
                int n = System.identityHashCode(webView2);
                object = var5_5;
                if (webView2 != null) {
                    webView2 = webView2.getSettings();
                    object = var5_5;
                    if (webView2 != null) {
                        object = webView2.getUserAgentString();
                    }
                }
                Log.d((String)"DESKTOP_DEBUG", (String)("onPageStarted: instance=" + n + ", URL=" + string + ", UA=" + (String)object + ", architecture=" + desktopArchitecture));
                if (string != null) {
                    MainActivity.access$getViewModel(this.this$0).onPageStarted(string);
                }
            }

            public void onReceivedError(WebView object, WebResourceRequest webResourceRequest, WebResourceError object2) {
                boolean bl;
                super.onReceivedError(object, webResourceRequest, object2);
                if (object != null && (object = object.getSettings()) != null) {
                    object.setCacheMode(-1);
                }
                Boolean bl2 = null;
                object = webResourceRequest != null ? webResourceRequest.getUrl() : null;
                Integer n = object2 != null ? Integer.valueOf(object2.getErrorCode()) : null;
                object2 = object2 != null ? object2.getDescription() : null;
                if (webResourceRequest != null) {
                    bl2 = webResourceRequest.isForMainFrame();
                }
                Log.e((String)"DIAGNOSTIC", (String)("onReceivedError: URL=" + object + ", errorCode=" + n + ", description=" + object2 + ", isMainFrame=" + bl2));
                boolean bl3 = bl = false;
                if (webResourceRequest != null) {
                    bl3 = bl;
                    if (webResourceRequest.isForMainFrame()) {
                        bl3 = true;
                    }
                }
                if (bl3) {
                    MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
                }
            }

            public void onReceivedHttpError(WebView object, WebResourceRequest webResourceRequest, WebResourceResponse webResourceResponse) {
                boolean bl;
                super.onReceivedHttpError(object, webResourceRequest, webResourceResponse);
                if (object != null && (object = object.getSettings()) != null) {
                    object.setCacheMode(-1);
                }
                String string = null;
                object = webResourceRequest != null ? webResourceRequest.getUrl() : null;
                Integer n = webResourceResponse != null ? Integer.valueOf(webResourceResponse.getStatusCode()) : null;
                if (webResourceResponse != null) {
                    string = webResourceResponse.getReasonPhrase();
                }
                Log.e((String)"DIAGNOSTIC", (String)("onReceivedHttpError: URL=" + object + ", statusCode=" + n + ", reason=" + string));
                boolean bl2 = bl = false;
                if (webResourceRequest != null) {
                    bl2 = bl;
                    if (webResourceRequest.isForMainFrame()) {
                        bl2 = true;
                    }
                }
                if (bl2) {
                    MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
                }
            }

            /*
             * WARNING - void declaration
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             * Converted monitor instructions to comments
             * Lifted jumps to return sites
             */
            public void onReceivedSslError(WebView object, SslErrorHandler object2, SslError object3) {
                void var2_2;
                block13: {
                    Object object4;
                    Object object5;
                    block15: {
                        block14: {
                            if (var2_2 == null || object3 == null) break block13;
                            object5 = SslSecurityPolicy.INSTANCE.preliminaryCheck((SslError)object3, (String)(object = object != null && (object = object.getUrl()) != null ? SslSecurityPolicy.INSTANCE.extractHostFromUrl((String)object) : null));
                            if (object5 instanceof SslSecurityPolicy.Decision.Reject) {
                                object = ((SslSecurityPolicy.Decision.Reject)object5).getReason();
                                String string = object3.getUrl();
                                Log.e((String)"SSL", (String)("Preliminary reject: " + (String)object + " for URL=" + string));
                                var2_2.cancel();
                                object = MainActivity.access$getViewModel(this.this$0);
                                object2 = ((SslSecurityPolicy.Decision.Reject)object5).getReason();
                                ((BrowserViewModel)((Object)object)).showToast("Connection blocked: Certificate untrusted (" + (String)object2 + ")");
                                MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
                                return;
                            }
                            if (object5 instanceof SslSecurityPolicy.Decision.ProceedSessionApproved) {
                                object = object3.getUrl();
                                Log.d((String)"SSL", (String)("Proceeding with session-approved host for URL=" + (String)object));
                                var2_2.proceed();
                                return;
                            }
                            if (object == null) {
                                object4 = SslSecurityPolicy.INSTANCE;
                                object = object5 = object3.getUrl();
                                if (object5 == null) {
                                    object = "";
                                }
                                object = ((SslSecurityPolicy)object4).extractHostFromUrl((String)object);
                            }
                            if (object == null) break block14;
                            object = object5 = SslSecurityPolicy.INSTANCE.normalizeHost((String)object);
                            if (object5 != null) break block15;
                        }
                        object = "";
                    }
                    if (StringsKt.isBlank((CharSequence)((CharSequence)object))) {
                        var2_2.cancel();
                        MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
                        return;
                    }
                    object5 = MainActivity.access$getPendingSslHandlers$p(this.this$0);
                    Intrinsics.checkNotNullExpressionValue((Object)object5, (String)"access$getPendingSslHandlers$p(...)");
                    object4 = this.this$0;
                    // MONITORENTER : object5
                    List list = (List)MainActivity.access$getPendingSslHandlers$p((MainActivity)((Object)object4)).get(object);
                    boolean bl = false;
                    if (list != null) {
                        list.add(var2_2);
                    } else {
                        object4 = MainActivity.access$getPendingSslHandlers$p((MainActivity)((Object)object4));
                        Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"access$getPendingSslHandlers$p(...)");
                        object4.put(object, CollectionsKt.mutableListOf((Object[])new SslErrorHandler[]{var2_2}));
                        bl = true;
                    }
                    // MONITOREXIT : object5
                    if (!bl) {
                        return;
                    }
                    BuildersKt.launch$default((CoroutineScope)((CoroutineScope)LifecycleOwnerKt.getLifecycleScope((LifecycleOwner)((LifecycleOwner)this.this$0))), null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.this$0, (String)object, object3, null){
                        final SslError $error;
                        final String $host;
                        int label;
                        final MainActivity this$0;
                        {
                            this.this$0 = mainActivity;
                            this.$host = string;
                            this.$error = sslError;
                            super(2, continuation);
                        }

                        public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                        }

                        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                            return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                        }

                        /*
                         * Enabled aggressive block sorting
                         * Enabled unnecessary exception pruning
                         * Enabled aggressive exception aggregation
                         */
                        public final Object invokeSuspend(Object object) {
                            Object object2;
                            Object object3;
                            Object object4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
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
                                    object3 = (CoroutineContext)Dispatchers.getIO();
                                    object2 = (Function2)new Function2<CoroutineScope, Continuation<? super SslSecurityPolicy.Decision>, Object>(this.$error, this.$host, null){
                                        final SslError $error;
                                        final String $host;
                                        int label;
                                        {
                                            this.$error = sslError;
                                            this.$host = string;
                                            super(2, continuation);
                                        }

                                        public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                                            return (Continuation)new /* invalid duplicate definition of identical inner class */;
                                        }

                                        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super SslSecurityPolicy.Decision> continuation) {
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
                                            return SslSecurityPolicy.validateIncompleteChain$default(SslSecurityPolicy.INSTANCE, this.$error, this.$host, null, null, null, 28, null);
                                        }
                                    };
                                    object = (Continuation)this;
                                    this.label = 1;
                                    object3 = BuildersKt.withContext((CoroutineContext)object3, (Function2)object2, (Continuation)object);
                                    object = object3;
                                    if (object3 != object4) break;
                                    return object4;
                                }
                            }
                            object4 = (SslSecurityPolicy.Decision)object;
                            if (object4 instanceof SslSecurityPolicy.Decision.PromptUser) {
                                object = ((SslSecurityPolicy.Decision.PromptUser)object4).getHost();
                                Log.w((String)"SSL", (String)("AIA validated for host: " + (String)object + ". Showing user warning."));
                                MainActivity.access$getViewModel(this.this$0).showSslWarning(((SslSecurityPolicy.Decision.PromptUser)object4).getHost(), ((SslSecurityPolicy.Decision.PromptUser)object4).getUrl(), ((SslSecurityPolicy.Decision.PromptUser)object4).getCertDetails());
                                return Unit.INSTANCE;
                            }
                            if (!(object4 instanceof SslSecurityPolicy.Decision.Reject)) {
                                if (!(object4 instanceof SslSecurityPolicy.Decision.ProceedSessionApproved)) throw new NoWhenBranchMatchedException();
                                object4 = MainActivity.access$getPendingSslHandlers$p(this.this$0);
                                Intrinsics.checkNotNullExpressionValue((Object)object4, (String)"access$getPendingSslHandlers$p(...)");
                                object3 = this.this$0;
                                object = this.$host;
                                synchronized (object4) {
                                    object3 = (List)MainActivity.access$getPendingSslHandlers$p((MainActivity)((Object)object3)).remove(object);
                                    object = object3;
                                    if (object3 == null) {
                                        object = CollectionsKt.emptyList();
                                    }
                                }
                            } else {
                                object3 = ((SslSecurityPolicy.Decision.Reject)object4).getReason();
                                object = this.$host;
                                Log.e((String)"SSL", (String)("AIA validation rejected: " + (String)object3 + " for host: " + (String)object));
                                object2 = MainActivity.access$getPendingSslHandlers$p(this.this$0);
                                Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"access$getPendingSslHandlers$p(...)");
                                object = this.this$0;
                                object3 = this.$host;
                                synchronized (object2) {
                                    object3 = (List)MainActivity.access$getPendingSslHandlers$p((MainActivity)((Object)object)).remove(object3);
                                    object = object3;
                                    if (object3 == null) {
                                        object = CollectionsKt.emptyList();
                                    }
                                }
                                object = ((Iterable)object).iterator();
                                while (true) {
                                    if (!object.hasNext()) {
                                        object = MainActivity.access$getViewModel(this.this$0);
                                        object3 = ((SslSecurityPolicy.Decision.Reject)object4).getReason();
                                        ((BrowserViewModel)((Object)object)).showToast("Connection blocked: Certificate untrusted (" + (String)object3 + ")");
                                        MainActivity.access$getViewModel(this.this$0).onPageCommitVisible();
                                        return Unit.INSTANCE;
                                    }
                                    object3 = (SslErrorHandler)object.next();
                                    if (object3 == null) continue;
                                    try {
                                        object3.cancel();
                                    }
                                    catch (Exception exception) {}
                                }
                            }
                            object = ((Iterable)object).iterator();
                            while (object.hasNext()) {
                                object3 = (SslErrorHandler)object.next();
                                if (object3 == null) continue;
                                try {
                                    object3.proceed();
                                }
                                catch (Exception object2222) {}
                            }
                            return Unit.INSTANCE;
                        }
                    }), (int)3, null);
                    return;
                }
                if (var2_2 == null) return;
                var2_2.cancel();
            }

            public WebResourceResponse shouldInterceptRequest(WebView object, WebResourceRequest object2) {
                block2: {
                    block3: {
                        Object object3;
                        block5: {
                            block4: {
                                if (object2 == null || (object3 = object2.getUrl()) == null) break block2;
                                if (!((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).isAdBlockingEnabled() || !ProtectionEngine.INSTANCE.isAdRequest(object3)) break block3;
                                object3 = object3.toString();
                                Intrinsics.checkNotNullExpressionValue((Object)object3, (String)"toString(...)");
                                object = object2.getRequestHeaders();
                                if (object == null) break block4;
                                object2 = (String)object.get("Accept");
                                object = object2;
                                if (object2 != null) break block5;
                            }
                            object = "subresource";
                        }
                        Log.e((String)"DIAGNOSTIC", (String)"INTERCEPT_BLOCK=shouldInterceptRequest");
                        Log.e((String)"DIAGNOSTIC", (String)("REQUEST_URL=" + (String)object3));
                        Log.e((String)"DIAGNOSTIC", (String)("RESOURCE_TYPE=" + (String)object));
                        Log.e((String)"DIAGNOSTIC", (String)"BLOCK_REASON=Ad Request Blocked");
                        return new WebResourceResponse("text/plain", "UTF-8", (InputStream)new ByteArrayInputStream(MainActivity.access$getEMPTY_BLOCKED_BYTES$cp()));
                    }
                    return super.shouldInterceptRequest(object, object2);
                }
                return null;
            }

            public boolean shouldOverrideUrlLoading(WebView webView2, WebResourceRequest object) {
                if (object != null && (object = object.getUrl()) != null && (object = object.toString()) != null) {
                    return this.processUrlLoading(webView2, (String)object);
                }
                return false;
            }

            @Deprecated(message="Deprecated in Java", replaceWith=@ReplaceWith(expression="shouldOverrideUrlLoading(view, request)", imports={}))
            public boolean shouldOverrideUrlLoading(WebView webView2, String string) {
                if (string == null) {
                    return false;
                }
                return this.processUrlLoading(webView2, string);
            }
        });
        webView2.setWebChromeClient(new WebChromeClient(this){
            final MainActivity this$0;
            {
                this.this$0 = mainActivity;
            }

            public static final /* synthetic */ void access$onCreateWindow$forwardToParent(Ref.BooleanRef booleanRef, WebView webView2, WebView webView3, String string) {
                createConfiguredWebView.webView.1.2.onCreateWindow$forwardToParent(booleanRef, webView2, webView3, string);
            }

            private static final void onCreateWindow$forwardToParent(Ref.BooleanRef booleanRef, WebView webView2, WebView webView3, String string) {
                if (!(booleanRef.element || StringsKt.isBlank((CharSequence)string) || Intrinsics.areEqual((Object)string, (Object)"about:blank"))) {
                    booleanRef.element = true;
                    if (webView2 != null) {
                        webView2.post((Runnable)new MainActivity$createConfiguredWebView$webView$1$2$$ExternalSyntheticLambda0(webView3, webView2, string));
                    }
                    return;
                }
            }

            static final void onCreateWindow$forwardToParent$lambda$1(WebView webView2, WebView webView3, String string) {
                try {
                    webView2.stopLoading();
                    webView2.destroy();
                }
                catch (Exception exception) {
                    // empty catch block
                }
                webView3.loadUrl(string);
            }

            public boolean onCreateWindow(WebView object, boolean bl, boolean bl2, Message message) {
                if (((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).isPopupBlockingEnabled() && !bl2) {
                    Log.d((String)"POPUP", (String)"Blocked unsolicited popup window (no user gesture)");
                    return false;
                }
                if (message != null) {
                    WebView webView2 = new WebView((Context)this.this$0);
                    Object object2 = webView2.getSettings();
                    object2.setJavaScriptEnabled(true);
                    object2.setDomStorageEnabled(true);
                    object2.setSupportMultipleWindows(false);
                    CookieManager.getInstance().setAcceptThirdPartyCookies(webView2, true);
                    object2 = WebViewConfigurator.INSTANCE;
                    ((WebViewConfigurator)object2).applyIdentityMode(webView2, ((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).isDesktopModeEnabled(), ((BrowserUiState)MainActivity.access$getViewModel(this.this$0).getUiState().getValue()).isWindows10TouchEnabled());
                    webView2.setWebViewClient(new WebViewClient(new Ref.BooleanRef(), (WebView)object, webView2){
                        final Ref.BooleanRef $isHandled;
                        final WebView $tempWebView;
                        final WebView $view;
                        {
                            this.$isHandled = booleanRef;
                            this.$view = webView2;
                            this.$tempWebView = webView3;
                        }

                        public void onPageStarted(WebView object, String string, Bitmap bitmap) {
                            super.onPageStarted(object, string, bitmap);
                            object = string;
                            boolean bl = object == null || StringsKt.isBlank((CharSequence)object);
                            if (!bl && !Intrinsics.areEqual((Object)string, (Object)"about:blank")) {
                                createConfiguredWebView.webView.1.2.access$onCreateWindow$forwardToParent(this.$isHandled, this.$view, this.$tempWebView, string);
                            }
                        }

                        public boolean shouldOverrideUrlLoading(WebView object, WebResourceRequest webResourceRequest) {
                            if (webResourceRequest != null && (object = webResourceRequest.getUrl()) != null && (object = object.toString()) != null) {
                                createConfiguredWebView.webView.1.2.access$onCreateWindow$forwardToParent(this.$isHandled, this.$view, this.$tempWebView, (String)object);
                                return true;
                            }
                            return false;
                        }

                        @Deprecated(message="Deprecated in Java")
                        public boolean shouldOverrideUrlLoading(WebView webView2, String string) {
                            if (string == null) {
                                return false;
                            }
                            createConfiguredWebView.webView.1.2.access$onCreateWindow$forwardToParent(this.$isHandled, this.$view, this.$tempWebView, string);
                            return true;
                        }
                    });
                    object = message.obj;
                    object = object instanceof WebView.WebViewTransport ? (WebView.WebViewTransport)object : null;
                    if (object != null) {
                        object.setWebView(webView2);
                    }
                    message.sendToTarget();
                    return true;
                }
                return false;
            }

            public void onProgressChanged(WebView webView2, int n) {
                super.onProgressChanged(webView2, n);
                MainActivity.access$getViewModel(this.this$0).onProgressChanged(n);
            }

            public boolean onShowFileChooser(WebView object, ValueCallback<Uri[]> intent, WebChromeClient.FileChooserParams fileChooserParams) {
                object = MainActivity.access$getFileChooserCallback$p(this.this$0);
                if (object != null) {
                    object.onReceiveValue(null);
                }
                MainActivity.access$setFileChooserCallback$p(this.this$0, intent);
                boolean bl = true;
                boolean bl2 = true;
                boolean bl3 = fileChooserParams != null && fileChooserParams.getMode() == 1;
                object = fileChooserParams != null ? fileChooserParams.getAcceptTypes() : null;
                intent = MainActivity.Companion.createFileChooserIntent((String[])object, bl3);
                try {
                    MainActivity.access$getFileChooserLauncher$p(this.this$0).launch((Object)intent);
                    bl3 = bl;
                }
                catch (Exception exception) {
                    ValueCallback valueCallback = MainActivity.access$getFileChooserCallback$p(this.this$0);
                    if (valueCallback != null) {
                        valueCallback.onReceiveValue(null);
                    }
                    MainActivity.access$setFileChooserCallback$p(this.this$0, null);
                    bl3 = false;
                }
                catch (ActivityNotFoundException activityNotFoundException) {
                    try {
                        object = MainActivity.Companion.createGetContentIntent((String[])object, bl3);
                        MainActivity.access$getFileChooserLauncher$p(this.this$0).launch(object);
                        bl3 = bl2;
                    }
                    catch (Exception exception) {
                        ValueCallback valueCallback = MainActivity.access$getFileChooserCallback$p(this.this$0);
                        if (valueCallback != null) {
                            valueCallback.onReceiveValue(null);
                        }
                        MainActivity.access$setFileChooserCallback$p(this.this$0, null);
                        bl3 = false;
                    }
                }
                return bl3;
            }
        });
        webView2.setDownloadListener((DownloadListener)new MainActivity$$ExternalSyntheticLambda2(this));
        return webView2;
    }

    public final TabWebViewManager getTabWebViewManager() {
        TabWebViewManager tabWebViewManager = this.tabWebViewManager;
        if (tabWebViewManager != null) {
            return tabWebViewManager;
        }
        Intrinsics.throwUninitializedPropertyAccessException((String)"tabWebViewManager");
        return null;
    }

    public final boolean handleUrlNavigation$app(WebView object, String string) {
        block11: {
            boolean bl;
            block8: {
                NavigationController navigationController;
                block10: {
                    block9: {
                        block7: {
                            Intrinsics.checkNotNullParameter((Object)string, (String)"url");
                            Log.d((String)"DIAGNOSTIC", (String)("handleUrlNavigation: URL=" + string));
                            navigationController = NavigationController.INSTANCE;
                            navigationController = navigationController.evaluate(string, ((BrowserUiState)this.getViewModel().getUiState().getValue()).getCustomKeywords(), (Collection)this.getViewModel().getNormalizedKeywords(), ((BrowserUiState)this.getViewModel().getUiState().getValue()).getSelectedSearchEngine());
                            boolean bl2 = navigationController instanceof NavigationDecision.Blocked;
                            bl = true;
                            if (!bl2) break block7;
                            if (Intrinsics.areEqual((Object)((NavigationDecision.Blocked)navigationController).getReason(), (Object)"Download Blocked")) {
                                this.getViewModel().showToast(((NavigationDecision.Blocked)navigationController).getDetail());
                                this.getViewModel().onPageCommitVisible();
                            } else {
                                object = this.getViewModel();
                                ((BrowserViewModel)((Object)object)).setBlockedUrl(string, ((NavigationDecision.Blocked)navigationController).getReason(), ((NavigationDecision.Blocked)navigationController).getDetail());
                            }
                            break block8;
                        }
                        if (!(navigationController instanceof NavigationDecision.Redirect)) break block9;
                        if (object != null) {
                            object.loadUrl(((NavigationDecision.Redirect)navigationController).getUrl());
                        }
                        break block8;
                    }
                    if (!(navigationController instanceof NavigationDecision.Download)) break block10;
                    string = ((NavigationDecision.Download)navigationController).getUrl();
                    object = object != null && (object = object.getSettings()) != null ? object.getUserAgentString() : null;
                    this.startDownload(string, (String)object, null, ((NavigationDecision.Download)navigationController).getMimeType());
                    break block8;
                }
                if (!(navigationController instanceof NavigationDecision.Allowed)) break block11;
                bl = false;
            }
            return bl;
        }
        throw new NoWhenBranchMatchedException();
    }

    protected void onCreate(Bundle object4) {
        boolean bl;
        Object object2;
        Object object3;
        block3: {
            super.onCreate(object4);
            EdgeToEdge.enable$default((ComponentActivity)this, null, null, (int)3, null);
            object3 = new IntentFilter("android.intent.action.DOWNLOAD_COMPLETE");
            object2 = (Context)this;
            object4 = this.downloadReceiver;
            ContextCompat.registerReceiver((Context)object2, (BroadcastReceiver)object4, (IntentFilter)object3, (int)4);
            this.startProgressPolling();
            object4 = (Context)this;
            this.setTabWebViewManager$app(new TabWebViewManager((Context)object4, 4, new MainActivity$$ExternalSyntheticLambda4(this), new MainActivity$$ExternalSyntheticLambda5(this), new MainActivity$$ExternalSyntheticLambda6(), new MainActivity$$ExternalSyntheticLambda7(this), new MainActivity$$ExternalSyntheticLambda8(this), new MainActivity$$ExternalSyntheticLambda9(), new MainActivity$$ExternalSyntheticLambda10(this)));
            for (Object object4 : (Iterable)((BrowserUiState)this.getViewModel().getUiState().getValue()).getTabs()) {
                if (!Intrinsics.areEqual((Object)((BrowserTab)object4).getId(), (Object)((BrowserUiState)this.getViewModel().getUiState().getValue()).getCurrentTabId())) continue;
                break block3;
            }
            object4 = null;
        }
        object2 = (BrowserTab)object4;
        object3 = this.getTabWebViewManager();
        String string = ((BrowserUiState)this.getViewModel().getUiState().getValue()).getCurrentTabId();
        boolean bl2 = bl = false;
        if (object2 != null) {
            bl2 = bl;
            if (!((BrowserTab)object2).isHomePage()) {
                bl2 = true;
            }
        }
        object4 = bl2 ? ((BrowserTab)object2).getUrl() : null;
        object2 = object2 != null ? ((BrowserTab)object2).getBundle() : null;
        this.webViewInstance = (WebView)TabWebViewManager.getOrCreateWebView$default((TabWebViewManager)object3, string, (String)object4, (Bundle)object2, null, 8, null).component1();
        ComponentActivityKt.setContent$default((ComponentActivity)this, null, (Function2)((Function2)ComposableLambdaKt.composableLambdaInstance((int)1419673879, (boolean)true, (Object)new MainActivity$$ExternalSyntheticLambda11(this))), (int)1, null);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    protected void onDestroy() {
        SslSecurityPolicy.INSTANCE.clearSessionApprovals();
        ValueCallback<Uri[]> valueCallback = this.pendingSslHandlers;
        Intrinsics.checkNotNullExpressionValue(valueCallback, (String)"pendingSslHandlers");
        synchronized (valueCallback) {
            Unit unit = ((Iterable)this.pendingSslHandlers.values()).iterator();
            block8: while (true) {
                if (!unit.hasNext()) {
                    this.pendingSslHandlers.clear();
                    unit = Unit.INSTANCE;
                    // MONITOREXIT @DISABLED, blocks:[4, 8, 12] lbl10 : MonitorExitStatement: MONITOREXIT : valueCallback
                    valueCallback = this.progressPollingJob;
                    if (valueCallback != null) {
                        Job.DefaultImpls.cancel$default((Job)valueCallback, null, (int)1, null);
                    }
                    this.progressPollingJob = null;
                    try {
                        this.unregisterReceiver(this.downloadReceiver);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    valueCallback = this.fileChooserCallback;
                    if (valueCallback != null) {
                        valueCallback.onReceiveValue(null);
                    }
                    this.fileChooserCallback = null;
                    try {
                        this.getTabWebViewManager().destroyAll();
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    this.webViewInstance = null;
                    super.onDestroy();
                    return;
                }
                Object object = (List)unit.next();
                Intrinsics.checkNotNull((Object)object);
                object = ((Iterable)object).iterator();
                while (true) {
                    if (!object.hasNext()) continue block8;
                    SslErrorHandler sslErrorHandler = (SslErrorHandler)object.next();
                    try {
                        sslErrorHandler.cancel();
                    }
                    catch (Exception sslErrorHandler2) {
                    }
                }
                break;
            }
        }
    }

    protected void onPause() {
        super.onPause();
        try {
            TabWebViewManager.pauseAll$default(this.getTabWebViewManager(), null, 1, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected void onResume() {
        super.onResume();
        try {
            this.getTabWebViewManager().resumeTab(((BrowserUiState)this.getViewModel().getUiState().getValue()).getCurrentTabId());
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * Unable to fully structure code
     */
    public final Object queryActiveDownloads$app(Continuation<? super Boolean> var1_1) {
        if (!(var1_1 instanceof queryActiveDownloads.1)) ** GOTO lbl-1000
        var4_2 = var1_1;
        if ((var4_2.label & -2147483648) != 0) {
            var4_2.label += -2147483648;
            var1_1 = var4_2;
        } else lbl-1000:
        // 2 sources

        {
            var1_1 = new ContinuationImpl(this, (Continuation<? super queryActiveDownloads.1>)var1_1){
                Object L$0;
                Object L$1;
                Object L$2;
                int label;
                Object result;
                final MainActivity this$0;
                {
                    this.this$0 = mainActivity;
                    super(continuation);
                }

                public final Object invokeSuspend(Object object) {
                    this.result = object;
                    this.label |= Integer.MIN_VALUE;
                    return this.this$0.queryActiveDownloads$app((Continuation<? super Boolean>)((Continuation)this));
                }
            };
        }
        var4_2 = var1_1.result;
        var5_3 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (var1_1.label) {
            default: {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            case 1: {
                var5_3 = (List)var1_1.L$2;
                var5_3 = (List)var1_1.L$1;
                var1_1 = (DownloadManager)var1_1.L$0;
                ResultKt.throwOnFailure((Object)var4_2);
                var1_1 = var4_2;
                break;
            }
            case 0: {
                ResultKt.throwOnFailure((Object)var4_2);
                var4_2 = this.getSystemService("download");
                var4_2 = var4_2 instanceof DownloadManager != false ? (DownloadManager)var4_2 : null;
                var2_4 = false;
                if (var4_2 == null) {
                    return Boxing.boxBoolean((boolean)false);
                }
                var6_5 = ((BrowserUiState)this.getViewModel().getUiState().getValue()).getDownloadHistory();
                var8_6 = var6_5;
                var7_7 = new ArrayList<E>();
                var10_8 = var8_6.iterator();
                while (var10_8.hasNext()) {
                    var8_6 = var10_8.next();
                    var9_10 = (DownloadEntry)var8_6;
                    var3_9 = var9_10.getDownloadId() > 0L && (var9_10.getStatus() == DownloadStatus.DOWNLOADING || var9_10.getStatus() == DownloadStatus.PAUSED) ? true : var2_4;
                    if (!var3_9) continue;
                    var7_7.add(var8_6);
                }
                if ((var7_7 = (List)var7_7).isEmpty()) {
                    return Boxing.boxBoolean((boolean)var2_4);
                }
                var8_6 = DownloadProgressPoller.INSTANCE;
                var1_1.L$0 = SpillingKt.nullOutSpilledVariable((Object)var4_2);
                var1_1.L$1 = SpillingKt.nullOutSpilledVariable(var6_5);
                var1_1.L$2 = SpillingKt.nullOutSpilledVariable((Object)var7_7);
                var1_1.label = 1;
                if ((var1_1 = var8_6.queryActiveDownloadsProgress((DownloadManager)var4_2, (List<DownloadEntry>)var7_7, (Continuation<? super Pair<? extends List<DownloadProgressUpdate>, Boolean>>)var1_1)) != var5_3) break;
                return var5_3;
            }
        }
        var4_2 = (Pair)var1_1;
        var1_1 = (List)var4_2.component1();
        var2_4 = (Boolean)var4_2.component2();
        var4_2 = var1_1.iterator();
        block9: while (var4_2.hasNext()) {
            var1_1 = (DownloadProgressUpdate)var4_2.next();
            var5_3 = var1_1.getStatus();
            switch (WhenMappings.$EnumSwitchMapping$0[var5_3.ordinal()]) {
                default: {
                    throw new NoWhenBranchMatchedException();
                }
                case 3: 
                case 4: 
                case 5: {
                    this.getViewModel().updateDownloadStatus(var1_1.getDownloadId(), var1_1.getStatus(), var1_1.getLocalUri(), var1_1.getDownloadedBytes(), var1_1.getTotalBytes());
                    continue block9;
                }
                case 1: 
                case 2: 
            }
            this.getViewModel().updateDownloadProgress(var1_1.getDownloadId(), var1_1.getStatus(), var1_1.getDownloadedBytes(), var1_1.getTotalBytes(), var1_1.getLocalUri());
        }
        return Boxing.boxBoolean((boolean)var2_4);
    }

    public final void setDesktopArchitecture$app(DesktopArchitecture object) {
        int n;
        String string;
        Object object2;
        WebView webView2;
        block9: {
            String string2;
            int n2;
            block8: {
                Intrinsics.checkNotNullParameter((Object)object, (String)"architecture");
                Log.d((String)"DESKTOP_DEBUG", (String)("setDesktopArchitecture toggle: architecture=" + object));
                this.getViewModel().selectDesktopArchitecture((DesktopArchitecture)object);
                this.getTabWebViewManager().syncAllLiveWebViewsArchitecture((DesktopArchitecture)object);
                webView2 = this.webViewInstance;
                if (webView2 == null) {
                    return;
                }
                boolean bl = this.getTabWebViewManager().hasLiveWebView(webView2);
                n2 = 1;
                if (!bl) {
                    object2 = WebViewConfigurator.INSTANCE;
                    string = webView2.getUrl();
                    ((WebViewConfigurator)object2).syncDesktopArchitecture(webView2, string, (DesktopArchitecture)object, true);
                }
                object2 = webView2.getUrl();
                string = null;
                if (object2 == null) break block8;
                n = !StringsKt.isBlank((CharSequence)((CharSequence)object2)) && !Intrinsics.areEqual((Object)object2, (Object)"about:blank") ? 1 : 0;
                if (n == 0) {
                    object2 = null;
                }
                if (object2 != null) break block9;
            }
            n = !StringsKt.isBlank((CharSequence)(string2 = ((BrowserUiState)this.getViewModel().getUiState().getValue()).getCurrentUrl())) && !Intrinsics.areEqual((Object)string2, (Object)"about:blank") ? n2 : 0;
            object2 = string;
            if (n != 0) {
                object2 = string2;
            }
        }
        n = System.identityHashCode(webView2);
        string = webView2.getSettings().getUserAgentString();
        Log.d((String)"DESKTOP_DEBUG", (String)("setDesktopArchitecture applied: instance=" + n + ", currentUrl=" + (String)object2 + ", UA=" + string + ", architecture=" + object));
        if (object2 != null && !((BrowserUiState)this.getViewModel().getUiState().getValue()).isHomePage()) {
            n = System.identityHashCode(webView2);
            object = webView2.getSettings().getUserAgentString();
            Log.d((String)"DESKTOP_DEBUG", (String)("setDesktopArchitecture triggering reload: instance=" + n + ", UA_before_reload=" + (String)object));
            webView2.reload();
            return;
        }
    }

    public final void setDesktopMode$app(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE;
        this.setDesktopArchitecture$app(desktopArchitecture);
    }

    public final void setDesktopMode4$app(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.DESKTOP_MODE_4 : DesktopArchitecture.NONE;
        this.setDesktopArchitecture$app(desktopArchitecture);
    }

    public final void setTabWebViewManager$app(TabWebViewManager tabWebViewManager) {
        Intrinsics.checkNotNullParameter((Object)tabWebViewManager, (String)"<set-?>");
        this.tabWebViewManager = tabWebViewManager;
    }

    public final void setWindows10Touch$app(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.WINDOWS_10_TOUCH : DesktopArchitecture.NONE;
        this.setDesktopArchitecture$app(desktopArchitecture);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void startDownload(String var1_1, String var2_2, String var3_8, String var4_9) {
        block42: {
            block38: {
                block41: {
                    block40: {
                        block39: {
                            block37: {
                                Intrinsics.checkNotNullParameter((Object)var1_1, (String)"url");
                                var9_10 /* !! */  = DownloadPolicy.INSTANCE.evaluate((String)var1_1, (String)var4_9, (String)var3_8);
                                if (var9_10 /* !! */  instanceof DownloadPolicy.Result.Blocked) {
                                    this.getViewModel().showToast(((DownloadPolicy.Result.Blocked)var9_10 /* !! */ ).getReason());
                                    this.getViewModel().onPageCommitVisible();
                                    return;
                                }
                                try {
                                    var9_10 /* !! */  = Uri.parse((String)StringsKt.trim((CharSequence)((CharSequence)var1_1)).toString());
                                }
                                catch (Exception var9_11) {
                                    var9_10 /* !! */  = null;
                                }
                                if (var9_10 /* !! */  != null && (var10_12 = var9_10 /* !! */ .getScheme()) != null) {
                                    var11_13 = Locale.ROOT;
                                    Intrinsics.checkNotNullExpressionValue((Object)var11_13, (String)"ROOT");
                                    var10_12 = var10_12.toLowerCase((Locale)var11_13);
                                    Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"toLowerCase(...)");
                                } else {
                                    var10_12 = null;
                                }
                                if (!Intrinsics.areEqual((Object)var10_12, (Object)"http") && !Intrinsics.areEqual((Object)var10_12, (Object)"https")) {
                                    Log.e((String)"DownloadManager", (String)("Unsupported URI scheme for download: " + (String)var1_1));
                                    this.getViewModel().showToast("Cannot download: unsupported link scheme.");
                                    this.getViewModel().onPageCommitVisible();
                                    return;
                                }
                                var10_12 = var11_13 = DownloadPolicy.INSTANCE.extractFileName((String)var1_1, (String)var3_8);
                                if (StringsKt.isBlank((CharSequence)((CharSequence)var11_13))) {
                                    var10_12 = URLUtil.guessFileName((String)var1_1, (String)var3_8, (String)var4_9);
                                    Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"guessFileName(...)");
                                }
                                var3_8 = (CharSequence)var10_12;
                                var3_8 = var10_12 = StringsKt.trim((CharSequence)new Regex("[/\\\\:*?\"<>|]").replace((CharSequence)var3_8, "_")).toString();
                                if (StringsKt.isBlank((CharSequence)((CharSequence)var10_12))) {
                                    var6_14 = System.currentTimeMillis();
                                    var3_8 = "download_" + var6_14;
                                }
                                if ((var5_15 = var4_9 != null && StringsKt.equals((String)var4_9, (String)"audio/mpeg", (boolean)true) == true) || (var5_15 = var4_9 != null && StringsKt.equals((String)var4_9, (String)"audio/mp3", (boolean)true) == true)) ** GOTO lbl-1000
                                var11_13 = StringsKt.substringBefore$default((String)var1_1, (char)'?', null, (int)2, null);
                                var10_12 = Locale.ROOT;
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"ROOT");
                                var10_12 = var11_13.toLowerCase((Locale)var10_12);
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"toLowerCase(...)");
                                if (StringsKt.endsWith$default((String)var10_12, (String)".mp3", (boolean)false, (int)2, null)) ** GOTO lbl-1000
                                var10_12 = Locale.ROOT;
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"ROOT");
                                var10_12 = var3_8.toLowerCase((Locale)var10_12);
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"toLowerCase(...)");
                                if (!StringsKt.endsWith$default((String)var10_12, (String)".mp3", (boolean)false, (int)2, null)) {
                                    var5_15 = false;
                                } else lbl-1000:
                                // 3 sources

                                {
                                    var5_15 = true;
                                }
                                if (!var5_15) ** GOTO lbl-1000
                                var10_12 = Locale.ROOT;
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"ROOT");
                                var10_12 = var3_8.toLowerCase((Locale)var10_12);
                                Intrinsics.checkNotNullExpressionValue((Object)var10_12, (String)"toLowerCase(...)");
                                if (!StringsKt.endsWith$default((String)var10_12, (String)".mp3", (boolean)false, (int)2, null)) {
                                    if (StringsKt.contains$default((CharSequence)((CharSequence)var3_8), (char)'.', (boolean)false, (int)2, null)) {
                                        var3_8 = StringsKt.substringBeforeLast$default((String)var3_8, (char)'.', null, (int)2, null);
                                        var3_8 = (String)var3_8 + ".mp3";
                                    } else {
                                        var3_8 = var3_8 + ".mp3";
                                    }
                                    var10_12 = var3_8;
                                } else lbl-1000:
                                // 2 sources

                                {
                                    var10_12 = var3_8;
                                }
                                if (var5_15) {
                                    var3_8 = "audio/mpeg";
                                } else {
                                    var3_8 = var4_9;
                                    var5_15 = var3_8 == null || StringsKt.isBlank((CharSequence)var3_8);
                                    if (!var5_15 && !Intrinsics.areEqual((Object)var4_9, (Object)"*/*")) {
                                        var3_8 = var4_9;
                                    } else {
                                        var4_9 = StringsKt.substringAfterLast((String)var10_12, (char)'.', (String)"");
                                        var3_8 = Locale.ROOT;
                                        Intrinsics.checkNotNullExpressionValue((Object)var3_8, (String)"ROOT");
                                        var3_8 = var4_9.toLowerCase((Locale)var3_8);
                                        Intrinsics.checkNotNullExpressionValue((Object)var3_8, (String)"toLowerCase(...)");
                                        var3_8 = MimeTypeMap.getSingleton().getMimeTypeFromExtension((String)var3_8);
                                        if (var3_8 == null) {
                                            var3_8 = "*/*";
                                        }
                                    }
                                }
                                var4_9 = (CharSequence)var2_2;
                                var5_15 = var4_9 == null || StringsKt.isBlank((CharSequence)var4_9);
                                if (var5_15) {
                                    var2_2 = this.webViewInstance;
                                    var2_2 = var2_2 != null && (var2_2 = var2_2.getSettings()) != null ? var2_2.getUserAgentString() : null;
                                }
                                try {
                                    var4_9 = new DownloadManager.Request((Uri)var9_10 /* !! */ );
                                    var4_9.setMimeType((String)var3_8);
                                    var4_9.setAllowedOverMetered(true);
                                    var4_9.setAllowedOverRoaming(true);
                                    var9_10 /* !! */  = (CharSequence)var2_2;
                                    if (var9_10 /* !! */  == null) break block37;
                                }
                                catch (Exception var2_6) {
                                    // empty catch block
                                    break block38;
                                }
                                try {
                                    if (StringsKt.isBlank((CharSequence)var9_10 /* !! */ )) break block37;
                                    var5_15 = false;
                                    break block39;
                                }
                                catch (Exception var2_3) {
                                    break block38;
                                }
                            }
                            var5_15 = true;
                        }
                        if (!var5_15) {
                            var4_9.addRequestHeader("User-Agent", (String)var2_2);
                        }
                        try {
                            var2_2 = CookieManager.getInstance().getCookie((String)var1_1);
                        }
                        catch (Exception var2_4) {
                            var2_2 = null;
                        }
                        {
                            var9_10 /* !! */  = (CharSequence)var2_2;
                        }
                        {
                            var5_15 = var9_10 /* !! */  == null || StringsKt.isBlank((CharSequence)var9_10 /* !! */ );
                            if (var5_15) ** GOTO lbl-1000
                            var4_9.addRequestHeader("Cookie", (String)var2_2);
                        }
lbl-1000:
                        // 2 sources

                        {
                            if ((var9_10 /* !! */  = (CharSequence)(var2_2 = (var2_2 = this.webViewInstance) != null ? var2_2.getUrl() : null)) == null || (var8_16 = StringsKt.isBlank((CharSequence)var9_10 /* !! */ ))) break block40;
                            var5_15 = false;
                            break block41;
                        }
                    }
                    var5_15 = true;
                }
                if (var5_15) ** GOTO lbl131
                try {
                    if (!StringsKt.startsWith$default((String)var2_2, (String)"data:", (boolean)false, (int)2, null) && !StringsKt.startsWith$default((String)var2_2, (String)"about:", (boolean)false, (int)2, null)) {
                        var4_9.addRequestHeader("Referer", (String)var2_2);
                    }
lbl131:
                    // 4 sources

                    var4_9.setTitle((CharSequence)var10_12);
                    var4_9.setDescription((CharSequence)"Downloading with Muslim Browser Pro...");
                    var4_9.setNotificationVisibility(1);
                    var4_9.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, (String)var10_12);
                    var2_2 = this.getSystemService("download");
                    var2_2 = var2_2 instanceof DownloadManager != false ? (DownloadManager)var2_2 : null;
                    var6_14 = var2_2 != null ? var2_2.enqueue((DownloadManager.Request)var4_9) : -1L;
                    break block42;
                }
                catch (Exception var2_5) {}
            }
            Log.e((String)"DownloadManager", (String)("Failed to enqueue download for " + (String)var1_1), (Throwable)((Throwable)var2_7));
            var6_14 = -1L;
        }
        if (var6_14 <= 0L) {
            var1_1 = new DownloadEntry(null, -1L, (String)var10_12, (String)var1_1, (String)var3_8, System.currentTimeMillis(), DownloadStatus.FAILED, 0L, 0L, null, 897, null);
            this.getViewModel().recordDownload((DownloadEntry)var1_1);
            this.getViewModel().showToast("Download failed to start.");
            this.getViewModel().onPageCommitVisible();
            return;
        }
        var1_1 = new DownloadEntry(null, var6_14, (String)var10_12, (String)var1_1, (String)var3_8, System.currentTimeMillis(), DownloadStatus.DOWNLOADING, 0L, 0L, null, 897, null);
        this.getViewModel().recordDownload((DownloadEntry)var1_1);
        this.getViewModel().showToast("Download started.");
        this.getViewModel().onPageCommitVisible();
        this.startProgressPolling();
    }

    @Metadata(d1={"\u0000V\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0002\b\u0007\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010\u0011\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0010\u0010\u000f\u001a\u00020\n2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0007J\u0016\u0010\u0011\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\nJ\u0016\u0010\u0016\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0017\u001a\u00020\nJ\u001e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u0015\u001a\u00020\n2\u0006\u0010\u0017\u001a\u00020\nJ*\u0010\u0019\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\n2\b\b\u0002\u0010\u0015\u001a\u00020\n2\b\b\u0002\u0010\u0017\u001a\u00020\nJ\u0016\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u0013\u001a\u00020\u00142\u0006\u0010\u001a\u001a\u00020\nJ!\u0010\u001c\u001a\b\u0012\u0004\u0012\u00020\u00070\u001d2\u000e\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u001d\u00a2\u0006\u0002\u0010\u001fJ#\u0010 \u001a\u00020!2\u000e\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u001d2\u0006\u0010\"\u001a\u00020\n\u00a2\u0006\u0002\u0010#J#\u0010$\u001a\u00020!2\u000e\u0010\u001e\u001a\n\u0012\u0004\u0012\u00020\u0007\u0018\u00010\u001d2\u0006\u0010\"\u001a\u00020\n\u00a2\u0006\u0002\u0010#J1\u0010%\u001a\n\u0012\u0004\u0012\u00020&\u0018\u00010\u001d2\u0006\u0010'\u001a\u00020(2\b\u0010)\u001a\u0004\u0018\u00010!2\n\b\u0002\u0010*\u001a\u0004\u0018\u00010+\u00a2\u0006\u0002\u0010,R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0086T\u00a2\u0006\u0002\n\u0000R$\u0010\u000b\u001a\u00020\n2\u0006\u0010\t\u001a\u00020\n8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006-"}, d2={"Lcom/muslim/browser/pro/MainActivity$Companion;", "", "<init>", "()V", "EMPTY_BLOCKED_BYTES", "", "DESKTOP_USER_AGENT", "", "WINDOWS_10_TOUCH_USER_AGENT", "value", "", "isDarkThemeActive", "()Z", "setDarkThemeActive", "(Z)V", "isAuthenticationUrl", "url", "applyDesktopMode", "", "webView", "Landroid/webkit/WebView;", "isDesktopEnabled", "applyWindows10Touch", "isWindows10TouchEnabled", "applyIdentityMode", "configureBaseSettings", "isDarkTheme", "applyWebViewTheme", "normalizeMimeTypes", "", "acceptTypes", "([Ljava/lang/String;)[Ljava/lang/String;", "createFileChooserIntent", "Landroid/content/Intent;", "isMultiple", "([Ljava/lang/String;Z)Landroid/content/Intent;", "createGetContentIntent", "parseFileChooserResult", "Landroid/net/Uri;", "resultCode", "", "data", "resolver", "Landroid/content/ContentResolver;", "(ILandroid/content/Intent;Landroid/content/ContentResolver;)[Landroid/net/Uri;", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public static /* synthetic */ void configureBaseSettings$default(Companion companion, WebView webView2, boolean bl, boolean bl2, boolean bl3, int n, Object object) {
            if ((n & 4) != 0) {
                bl2 = false;
            }
            if ((n & 8) != 0) {
                bl3 = false;
            }
            companion.configureBaseSettings(webView2, bl, bl2, bl3);
        }

        public static /* synthetic */ Uri[] parseFileChooserResult$default(Companion companion, int n, Intent intent, ContentResolver contentResolver, int n2, Object object) {
            if ((n2 & 4) != 0) {
                contentResolver = null;
            }
            return companion.parseFileChooserResult(n, intent, contentResolver);
        }

        public final void applyDesktopMode(WebView webView2, boolean bl) {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            WebViewConfigurator.applyDesktopMode$default(WebViewConfigurator.INSTANCE, webView2, bl, false, 4, null);
        }

        public final void applyIdentityMode(WebView webView2, boolean bl, boolean bl2) {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            WebViewConfigurator.INSTANCE.applyIdentityMode(webView2, bl, bl2);
        }

        public final void applyWebViewTheme(WebView webView2, boolean bl) {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            WebViewConfigurator.INSTANCE.applyWebViewTheme(webView2, bl);
        }

        public final void applyWindows10Touch(WebView webView2, boolean bl) {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            WebViewConfigurator.INSTANCE.applyIdentityMode(webView2, false, bl);
        }

        public final void configureBaseSettings(WebView webView2, boolean bl, boolean bl2, boolean bl3) {
            Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
            WebViewConfigurator.configureBaseSettings$default(WebViewConfigurator.INSTANCE, webView2, bl, bl2, bl3, null, 16, null);
        }

        public final Intent createFileChooserIntent(String[] intent, boolean bl) {
            String[] stringArray = this.normalizeMimeTypes((String[])intent);
            intent = new Intent("android.intent.action.OPEN_DOCUMENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.addFlags(1);
            if (bl) {
                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            if (stringArray.length == 1) {
                intent.setType(stringArray[0]);
            } else {
                intent.setType("*/*");
                intent.putExtra("android.intent.extra.MIME_TYPES", stringArray);
            }
            return intent;
        }

        public final Intent createGetContentIntent(String[] stringArray, boolean bl) {
            stringArray = this.normalizeMimeTypes(stringArray);
            Intent intent = new Intent("android.intent.action.GET_CONTENT");
            intent.addCategory("android.intent.category.OPENABLE");
            intent.addFlags(1);
            if (bl) {
                intent.putExtra("android.intent.extra.ALLOW_MULTIPLE", true);
            }
            if (stringArray.length == 1) {
                intent.setType(stringArray[0]);
            } else {
                intent.setType("*/*");
                intent.putExtra("android.intent.extra.MIME_TYPES", stringArray);
            }
            return intent;
        }

        public final boolean isAuthenticationUrl(String string) {
            return WebViewConfigurator.INSTANCE.isAuthenticationUrl(string);
        }

        public final boolean isDarkThemeActive() {
            return WebViewConfigurator.INSTANCE.isDarkThemeActive();
        }

        public final String[] normalizeMimeTypes(String[] object) {
            boolean bl;
            if (object != null && !(bl = ((String[])object).length == 0)) {
                List list = new ArrayList();
                object = ArrayIteratorKt.iterator((Object[])object);
                while (object.hasNext()) {
                    Object object2 = (String)object.next();
                    if (StringsKt.isBlank((CharSequence)((CharSequence)object2))) continue;
                    object2 = StringsKt.split$default((CharSequence)((CharSequence)object2), (String[])new String[]{","}, (boolean)false, (int)0, (int)6, null).iterator();
                    while (object2.hasNext()) {
                        String string = ((Object)StringsKt.trim((CharSequence)((String)object2.next()))).toString();
                        bl = ((CharSequence)string).length() == 0;
                        if (bl) continue;
                        if (StringsKt.startsWith$default((String)string, (String)".", (boolean)false, (int)2, null)) {
                            string = string.substring(1);
                            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"substring(...)");
                            Locale locale = Locale.ROOT;
                            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
                            string = string.toLowerCase(locale);
                            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
                            string = MimeTypeMap.getSingleton().getMimeTypeFromExtension(string);
                            if (string != null) {
                                list.add(string);
                                continue;
                            }
                            list.add("*/*");
                            continue;
                        }
                        if (!StringsKt.contains$default((CharSequence)string, (CharSequence)"/", (boolean)false, (int)2, null)) continue;
                        list.add(string);
                    }
                }
                if (list.isEmpty()) {
                    object = new String[]{"*/*"};
                } else {
                    object = CollectionsKt.distinct((Iterable)list);
                    object = object.toArray(new String[0]);
                }
                return object;
            }
            return new String[]{"*/*"};
        }

        public final Uri[] parseFileChooserResult(int n, Intent uriArray, ContentResolver contentResolver) {
            Uri uri = null;
            if (n == -1 && uriArray != null) {
                ClipData clipData = uriArray.getClipData();
                Uri uri2 = uriArray.getData();
                if (clipData != null && clipData.getItemCount() > 0) {
                    int n2 = clipData.getItemCount();
                    uriArray = new Uri[n2];
                    for (n = 0; n < n2; ++n) {
                        uri = clipData.getItemAt(n).getUri();
                        if (contentResolver != null) {
                            try {
                                contentResolver.takePersistableUriPermission(uri, 1);
                            }
                            catch (Exception exception) {
                                // empty catch block
                            }
                        }
                        Intrinsics.checkNotNull((Object)uri);
                        uriArray[n] = uri;
                    }
                } else if (uri2 != null) {
                    if (contentResolver != null) {
                        try {
                            contentResolver.takePersistableUriPermission(uri2, 1);
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                    uriArray = new Uri[]{uri2};
                } else {
                    uriArray = uri;
                }
                return uriArray;
            }
            return null;
        }

        public final void setDarkThemeActive(boolean bl) {
            WebViewConfigurator.INSTANCE.setDarkThemeActive(bl);
        }
    }

    @Metadata(k=3, mv={2, 2, 0}, xi=48)
    public static final class WhenMappings {
        public static final int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[DownloadStatus.values().length];
            try {
                nArray[DownloadStatus.DOWNLOADING.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DownloadStatus.PAUSED.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DownloadStatus.COMPLETED.ordinal()] = 3;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DownloadStatus.FAILED.ordinal()] = 4;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[DownloadStatus.CANCELLED.ordinal()] = 5;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

