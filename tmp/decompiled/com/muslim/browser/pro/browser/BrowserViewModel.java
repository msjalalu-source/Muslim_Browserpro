/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.app.Application
 *  android.content.Context
 *  android.os.Bundle
 *  android.util.Log
 *  androidx.lifecycle.AndroidViewModel
 *  androidx.lifecycle.ViewModel
 *  androidx.lifecycle.ViewModelKt
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.browser.DownloadPolicy
 *  com.muslim.browser.pro.browser.ProtectionEngine
 *  com.muslim.browser.pro.browser.ProtectionEngine$FilterResult$Blocked
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  kotlin.Metadata
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.SpillingKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.ranges.RangesKt
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Dispatchers
 *  kotlinx.coroutines.Job
 *  kotlinx.coroutines.Job$DefaultImpls
 *  kotlinx.coroutines.flow.FlowKt
 *  kotlinx.coroutines.flow.MutableStateFlow
 *  kotlinx.coroutines.flow.StateFlow
 *  kotlinx.coroutines.flow.StateFlowKt
 *  org.json.JSONArray
 *  org.json.JSONObject
 *  org.json.JSONTokener
 */
package com.muslim.browser.pro.browser;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelKt;
import com.muslim.browser.pro.browser.BengaliTranslator;
import com.muslim.browser.pro.browser.BlockedInfo;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.BrowserUiState;
import com.muslim.browser.pro.browser.BrowserViewModel$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.BrowserViewModel$translateCurrentPage$3$2$2$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.DownloadPolicy;
import com.muslim.browser.pro.browser.DownloadStatus;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.HistoryEntry;
import com.muslim.browser.pro.browser.ProtectionEngine;
import com.muslim.browser.pro.browser.SearchEngine;
import com.muslim.browser.pro.browser.SettingsRepository;
import com.muslim.browser.pro.browser.SslWarningState;
import com.muslim.browser.pro.browser.TranslationEngine;
import com.muslim.browser.pro.ui.theme.AppTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.ranges.RangesKt;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.Job;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.flow.MutableStateFlow;
import kotlinx.coroutines.flow.StateFlow;
import kotlinx.coroutines.flow.StateFlowKt;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

@Metadata(d1={"\u0000\u00b0\u0001\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0010\b\u0007\u0018\u00002\u00020\u0001B\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u0011\u001a\u00020\u0012H\u0002J\u000e\u0010\u0018\u001a\u00020\u00122\u0006\u0010\u0019\u001a\u00020\u001aJ\u000e\u0010\u001b\u001a\u00020\u00122\u0006\u0010\u001c\u001a\u00020\u001dJ\f\u0010\u001e\u001a\b\u0012\u0004\u0012\u00020\u001f0\u0014J\u0016\u0010 \u001a\u00020\u00122\u0006\u0010!\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001fJ\u000e\u0010#\u001a\u00020\u00122\u0006\u0010$\u001a\u00020\u001fJ\u0006\u0010%\u001a\u00020\u0012J\u0006\u0010&\u001a\u00020\u0012J\u0006\u0010'\u001a\u00020\u0012J\u0006\u0010(\u001a\u00020\u0012J\u0010\u0010)\u001a\u00020\u00122\b\u0010*\u001a\u0004\u0018\u00010+J\u0018\u0010,\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u001f2\b\u0010*\u001a\u0004\u0018\u00010+J\u0010\u0010.\u001a\u00020\u00122\b\b\u0002\u0010\"\u001a\u00020\u001fJ\u000e\u0010/\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u001fJ\u000e\u00100\u001a\u00020\u00122\u0006\u0010-\u001a\u00020\u001fJ\u0006\u00101\u001a\u00020\u0012J\u000e\u00102\u001a\u0002032\u0006\u00104\u001a\u00020\u001fJ\u0010\u00105\u001a\u0002032\u0006\u00104\u001a\u00020\u001fH\u0002J\u0010\u00106\u001a\u00020\u001f2\u0006\u00104\u001a\u00020\u001fH\u0002J\u0010\u00107\u001a\u00020\u00122\u0006\u00108\u001a\u00020\u001fH\u0002J\u001e\u00109\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u001f2\u0006\u0010:\u001a\u00020\u001f2\u0006\u0010;\u001a\u00020\u001fJ\u000e\u0010<\u001a\u0002032\u0006\u0010\"\u001a\u00020\u001fJ\u000e\u0010=\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u001fJ\u0006\u0010>\u001a\u00020\u0012J(\u0010?\u001a\u00020\u00122\u0006\u0010\"\u001a\u00020\u001f2\b\u0010@\u001a\u0004\u0018\u00010\u001f2\u0006\u0010A\u001a\u0002032\u0006\u0010B\u001a\u000203J\u000e\u0010C\u001a\u00020\u00122\u0006\u0010D\u001a\u00020EJ\u000e\u0010F\u001a\u0002032\u0006\u0010G\u001a\u00020\u001fJ\u000e\u0010H\u001a\u00020\u00122\u0006\u0010\u001c\u001a\u00020IJ\u000e\u0010J\u001a\u00020\u00122\u0006\u0010K\u001a\u000203J\u000e\u0010L\u001a\u00020\u00122\u0006\u0010K\u001a\u000203J\u000e\u0010M\u001a\u00020\u00122\u0006\u0010N\u001a\u00020OJ\u000e\u0010P\u001a\u00020\u00122\u0006\u0010K\u001a\u000203J\u000e\u0010Q\u001a\u00020\u00122\u0006\u0010K\u001a\u000203J\u000e\u0010R\u001a\u00020\u00122\u0006\u0010K\u001a\u000203J\\\u0010S\u001a\u00020\u00122F\u0010T\u001aB\u0012\u0013\u0012\u00110\u001f\u00a2\u0006\f\bV\u0012\b\b!\u0012\u0004\b\b(W\u0012#\u0012!\u0012\u0006\u0012\u0004\u0018\u00010\u001f\u0012\u0004\u0012\u00020\u0012\u0018\u00010X\u00a2\u0006\f\bV\u0012\b\b!\u0012\u0004\b\b(Y\u0012\u0004\u0012\u00020\u00120U2\f\u0010Z\u001a\b\u0012\u0004\u0012\u00020\u00120[J\u0006\u0010\\\u001a\u00020\u0012J\u0006\u0010]\u001a\u00020\u0012J\u0006\u0010^\u001a\u00020\u0012J\u000e\u0010_\u001a\u00020\u00122\u0006\u0010`\u001a\u00020\u001fJ\u0006\u0010a\u001a\u00020\u0012J\u0006\u0010b\u001a\u00020\u0012J\u0006\u0010c\u001a\u00020\u0012J\u000e\u0010d\u001a\u00020\u00122\u0006\u0010e\u001a\u00020fJ2\u0010g\u001a\u00020\u00122\u0006\u0010h\u001a\u00020i2\u0006\u0010j\u001a\u00020k2\u0006\u0010l\u001a\u00020i2\u0006\u0010m\u001a\u00020i2\n\b\u0002\u0010n\u001a\u0004\u0018\u00010\u001fJ6\u0010o\u001a\u00020\u00122\u0006\u0010h\u001a\u00020i2\u0006\u0010j\u001a\u00020k2\n\b\u0002\u0010n\u001a\u0004\u0018\u00010\u001f2\b\b\u0002\u0010l\u001a\u00020i2\b\b\u0002\u0010m\u001a\u00020iJ\u000e\u0010p\u001a\u00020\u00122\u0006\u0010`\u001a\u00020\u001fJ\u0006\u0010q\u001a\u00020\u0012J\u000e\u0010r\u001a\u00020\u00122\u0006\u0010s\u001a\u00020\u001fJ\u0006\u0010t\u001a\u00020\u0012J\u001e\u0010u\u001a\u00020\u00122\u0006\u0010v\u001a\u00020\u001f2\u0006\u0010\"\u001a\u00020\u001f2\u0006\u0010w\u001a\u00020\u001fJ\u0006\u0010x\u001a\u00020\u0012J\u0006\u0010y\u001a\u00020\u0012J\u0006\u0010z\u001a\u00020\u0012R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\b\u001a\u0004\u0018\u00010\tX\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\n\u001a\b\u0012\u0004\u0012\u00020\f0\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0017\u0010\r\u001a\b\u0012\u0004\u0012\u00020\f0\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0017\u0010\u0013\u001a\b\u0012\u0004\u0012\u00020\u00150\u00148F\u00a2\u0006\u0006\u001a\u0004\b\u0016\u0010\u0017\u00a8\u0006{"}, d2={"Lcom/muslim/browser/pro/browser/BrowserViewModel;", "Landroidx/lifecycle/AndroidViewModel;", "application", "Landroid/app/Application;", "<init>", "(Landroid/app/Application;)V", "repository", "Lcom/muslim/browser/pro/browser/SettingsRepository;", "activeTranslationJob", "Lkotlinx/coroutines/Job;", "_uiState", "Lkotlinx/coroutines/flow/MutableStateFlow;", "Lcom/muslim/browser/pro/browser/BrowserUiState;", "uiState", "Lkotlinx/coroutines/flow/StateFlow;", "getUiState", "()Lkotlinx/coroutines/flow/StateFlow;", "persistTabs", "", "favoriteSites", "", "Lcom/muslim/browser/pro/browser/FavoriteSite;", "getFavoriteSites", "()Ljava/util/List;", "setAppTheme", "theme", "Lcom/muslim/browser/pro/ui/theme/AppTheme;", "selectTranslationEngine", "engine", "Lcom/muslim/browser/pro/browser/TranslationEngine;", "getNormalizedKeywords", "", "addFavoriteSite", "name", "url", "onSearchInputChange", "query", "openMenu", "closeMenu", "openTabsDialog", "closeTabsDialog", "saveCurrentTabState", "bundle", "Landroid/os/Bundle;", "saveTabState", "tabId", "openNewTab", "selectTab", "closeTab", "goHome", "submitQueryOrUrl", "", "input", "isWebUrl", "formatDirectUrl", "loadTargetUrl", "targetUrl", "setBlockedUrl", "reason", "detail", "checkAndFilterUrl", "onPageStarted", "onPageCommitVisible", "onPageFinished", "title", "canBack", "canForward", "onProgressChanged", "progress", "", "addCustomKeyword", "keyword", "selectSearchEngine", "Lcom/muslim/browser/pro/browser/SearchEngine;", "togglePopupBlocking", "enabled", "toggleAdBlocking", "selectDesktopArchitecture", "architecture", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "toggleDesktopMode", "toggleDesktopMode4", "toggleWindows10Touch", "translateCurrentPage", "evaluateJs", "Lkotlin/Function2;", "Lkotlin/ParameterName;", "script", "Lkotlin/Function1;", "callback", "reloadPage", "Lkotlin/Function0;", "onHistoryCleared", "openHistory", "closeHistory", "deleteHistoryEntry", "id", "clearAllHistory", "openDownloads", "closeDownloads", "recordDownload", "entry", "Lcom/muslim/browser/pro/browser/DownloadEntry;", "updateDownloadProgress", "downloadId", "", "status", "Lcom/muslim/browser/pro/browser/DownloadStatus;", "downloadedBytes", "totalBytes", "localUri", "updateDownloadStatus", "deleteDownloadEntry", "clearAllDownloadHistory", "showToast", "msg", "clearToast", "showSslWarning", "host", "details", "dismissSslWarning", "openDiagnostic", "closeDiagnostic", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class BrowserViewModel
extends AndroidViewModel {
    public static final int $stable = 8;
    private final MutableStateFlow<BrowserUiState> _uiState;
    private Job activeTranslationJob;
    private final SettingsRepository repository;
    private final StateFlow<BrowserUiState> uiState;

    public BrowserViewModel(Application object) {
        Object object2;
        Object object32;
        block8: {
            block12: {
                block11: {
                    block10: {
                        block9: {
                            Intrinsics.checkNotNullParameter((Object)object, (String)"application");
                            super(object);
                            this.repository = new SettingsRepository((Context)object);
                            object32 = this.repository.getSavedTabs();
                            if (object32 == null) break block9;
                            object2 = (List)object32.getFirst();
                            object = object2;
                            if (object2 != null) break block10;
                        }
                        object = CollectionsKt.listOf((Object)new BrowserTab("default_tab", null, null, null, false, false, false, null, false, false, 0, false, null, 8190, null));
                    }
                    if (object32 == null) break block11;
                    object2 = object32 = (String)object32.getSecond();
                    if (object32 != null) break block12;
                }
                object2 = ((BrowserTab)CollectionsKt.first((List)object)).getId();
            }
            for (Object object32 : (Iterable)object) {
                if (!Intrinsics.areEqual((Object)((BrowserTab)object32).getId(), object2)) continue;
                object2 = object32;
                break block8;
            }
            object2 = null;
        }
        object2 = object32 = (BrowserTab)object2;
        if (object32 == null) {
            object2 = (BrowserTab)CollectionsKt.first((List)object);
        }
        object32 = object2.getId();
        boolean bl = object2.isHomePage();
        String string = object2.getUrl();
        String string2 = object2.getSearchInput();
        String string3 = object2.getPageTitle();
        List<FavoriteSite> list = this.repository.getFavoriteSites();
        Set<String> set = this.repository.getCustomKeywords();
        boolean bl2 = this.repository.isPopupBlockingEnabled();
        boolean bl3 = this.repository.isAdBlockingEnabled();
        DesktopArchitecture desktopArchitecture = this.repository.getDesktopArchitecture();
        List<HistoryEntry> list2 = this.repository.getHistory();
        List<DownloadEntry> list3 = this.repository.getDownloadHistory();
        AppTheme appTheme = this.repository.getAppTheme();
        object2 = this.repository.getSelectedTranslationEngine();
        SearchEngine searchEngine = this.repository.getSelectedSearchEngine();
        this._uiState = StateFlowKt.MutableStateFlow((Object)new BrowserUiState((List)object, (String)object32, bl, string, string2, string3, false, 0, false, false, false, false, false, false, false, list2, list3, null, null, list, set, bl2, bl3, desktopArchitecture, false, false, appTheme, (TranslationEngine)((Object)object2), searchEngine, null, false, 1661370304, null));
        this.uiState = FlowKt.asStateFlow(this._uiState);
    }

    public static final /* synthetic */ MutableStateFlow access$get_uiState$p(BrowserViewModel browserViewModel) {
        return browserViewModel._uiState;
    }

    private final String formatDirectUrl(String string) {
        block0: {
            if (StringsKt.startsWith((String)string, (String)"http://", (boolean)true) || StringsKt.startsWith((String)string, (String)"https://", (boolean)true)) break block0;
            string = "https://" + string;
        }
        return string;
    }

    private final boolean isWebUrl(String string) {
        boolean bl = true;
        if (!StringsKt.startsWith((String)string, (String)"http://", (boolean)true) && !StringsKt.startsWith((String)string, (String)"https://", (boolean)true)) {
            if (!StringsKt.contains$default((CharSequence)string, (CharSequence)".", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)" ", (boolean)false, (int)2, null) || string.length() < 4) {
                bl = false;
            }
            return bl;
        }
        return true;
    }

    private final void loadTargetUrl(String string) {
        boolean bl;
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        boolean bl2 = ((BrowserUiState)this._uiState.getValue()).isHomePage();
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = browserUiState.getTabs();
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (BrowserTab)iterator.next();
                if (Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)browserUiState.getCurrentTabId())) {
                    bl = bl2 ? false : ((BrowserTab)object).isPageContentVisible();
                    object = BrowserTab.copy$default((BrowserTab)object, null, string, string, null, false, false, false, null, false, true, 0, bl, null, 5225, null);
                }
                collection.add(object);
            }
            object = (List)collection;
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, string, string, null, true, 0, bl = bl2 ? false : browserUiState.isPageContentVisible(), false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2097020578, null)));
        this.persistTabs();
    }

    public static /* synthetic */ void openNewTab$default(BrowserViewModel browserViewModel, String string, int n, Object object) {
        if ((n & 1) != 0) {
            string = "";
        }
        browserViewModel.openNewTab(string);
    }

    private final void persistTabs() {
        BrowserUiState browserUiState = (BrowserUiState)this._uiState.getValue();
        this.repository.saveTabs(browserUiState.getTabs(), browserUiState.getCurrentTabId());
    }

    static final Unit translateCurrentPage$lambda$44(BrowserViewModel browserViewModel, Function2 object, String mutableStateFlow) {
        Object object2 = (CharSequence)mutableStateFlow;
        boolean bl = object2 == null || StringsKt.isBlank((CharSequence)object2);
        if (!bl && !Intrinsics.areEqual((Object)mutableStateFlow, (Object)"null")) {
            object2 = browserViewModel.activeTranslationJob;
            if (object2 != null) {
                Job.DefaultImpls.cancel$default((Job)object2, null, (int)1, null);
            }
            browserViewModel.activeTranslationJob = BuildersKt.launch$default((CoroutineScope)ViewModelKt.getViewModelScope((ViewModel)((ViewModel)browserViewModel)), null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(mutableStateFlow, browserViewModel, (Function2<? super String, ? super Function1<? super String, Unit>, Unit>)object, null){
                final Function2<String, Function1<? super String, Unit>, Unit> $evaluateJs;
                final String $jsonResult;
                Object L$0;
                Object L$1;
                Object L$2;
                Object L$3;
                Object L$4;
                Object L$5;
                Object L$6;
                Object L$7;
                int label;
                final BrowserViewModel this$0;
                {
                    this.$jsonResult = string;
                    this.this$0 = browserViewModel;
                    this.$evaluateJs = function2;
                    super(2, continuation);
                }

                public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                    return (Continuation)new /* invalid duplicate definition of identical inner class */;
                }

                public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                    return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                }

                /*
                 * Unable to fully structure code
                 * Could not resolve type clashes
                 */
                public final Object invokeSuspend(Object var1_1) {
                    block32: {
                        block28: {
                            block31: {
                                block30: {
                                    block29: {
                                        var11_4 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                        switch (this.label) {
                                            default: {
                                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                            }
                                            case 4: {
                                                var5_5 = (Exception)this.L$0;
                                                ResultKt.throwOnFailure((Object)var1_1);
                                                break block32;
                                            }
                                            case 3: {
                                                var5_6 = (Throwable)this.L$6;
                                                var5_6 = this.L$5;
                                                var5_6 = (TranslationEngine)this.L$4;
                                                var5_6 = (ArrayList)this.L$3;
                                                var5_6 = (JSONArray)this.L$2;
                                                var5_6 = (JSONObject)this.L$1;
                                                var5_6 = (String)this.L$0;
                                                ResultKt.throwOnFailure((Object)var1_1);
                                            }
                                            case 2: {
                                                var5_6 = (String)this.L$7;
                                                var5_6 = (List)this.L$6;
                                                var5_6 = this.L$5;
                                                var5_6 = (TranslationEngine)this.L$4;
                                                var5_6 = (ArrayList)this.L$3;
                                                var5_6 = (JSONArray)this.L$2;
                                                var5_6 = (JSONObject)this.L$1;
                                                var5_6 = (String)this.L$0;
                                                ResultKt.throwOnFailure((Object)var1_1);
                                            }
                                            case 1: {
                                                var8_8 = (TranslationEngine)this.L$4;
                                                var5_6 = (ArrayList)this.L$3;
                                                var9_9 = (JSONArray)this.L$2;
                                                var7_10 = (JSONObject)this.L$1;
                                                var10_11 = (String)this.L$0;
                                                ResultKt.throwOnFailure((Object)var1_1);
                                                var6_12 = ((Result)var1_1).unbox-impl();
                                                var1_1 = var10_11;
                                                break;
                                            }
                                            case 0: {
                                                ResultKt.throwOnFailure((Object)var1_1);
                                                if (!StringsKt.startsWith$default((String)this.$jsonResult, (String)"\"", (boolean)false, (int)2, null) || !(var4_14 = StringsKt.endsWith$default((String)this.$jsonResult, (String)"\"", (boolean)false, (int)2, null))) ** GOTO lbl-1000
                                                try {
                                                    var1_1 = new JSONTokener(this.$jsonResult);
                                                    var1_1 = var1_1.nextValue();
                                                    Intrinsics.checkNotNull((Object)var1_1, (String)"null cannot be cast to non-null type kotlin.String");
                                                    var1_1 = (String)var1_1;
                                                }
                                                catch (Exception var1_2) {
                                                    var1_1 = this.$jsonResult;
                                                }
                                                ** GOTO lbl58
lbl-1000:
                                                // 1 sources

                                                {
                                                    var1_1 = this.$jsonResult;
lbl58:
                                                    // 3 sources

                                                    if ((var9_9 = (var7_10 = new JSONObject((String)var1_1)).optJSONArray("texts")) == null) break block28;
                                                }
                                                if (var9_9.length() == 0) break block28;
                                                var5_6 = new ArrayList(var9_9.length());
                                                var3_16 = var9_9.length();
                                                for (var2_15 = 0; var2_15 < var3_16; ++var2_15) {
                                                    var5_6.add(var9_9.getString(var2_15));
                                                    continue;
                                                }
                                                var8_8 = ((BrowserUiState)BrowserViewModel.access$get_uiState$p(this.this$0).getValue()).getSelectedTranslationEngine();
                                                var12_17 = BengaliTranslator.INSTANCE;
                                                var6_12 = (List)var5_6;
                                                var10_11 = (Continuation)this;
                                                this.L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1);
                                                this.L$1 = SpillingKt.nullOutSpilledVariable((Object)var7_10);
                                                this.L$2 = SpillingKt.nullOutSpilledVariable((Object)var9_9);
                                                this.L$3 = SpillingKt.nullOutSpilledVariable((Object)var5_6);
                                                this.L$4 = SpillingKt.nullOutSpilledVariable((Object)var8_8);
                                                this.label = 1;
                                                var6_12 = var12_17.translateBatch-0E7RQCE((List<String>)var6_12, var8_8, (Continuation<? super Result<? extends List<String>>>)var10_11);
                                                if (var6_12 != var11_4) break;
                                                return var11_4;
                                            }
                                        }
                                        if (!Result.isSuccess-impl((Object)var6_12)) break block29;
                                        ResultKt.throwOnFailure((Object)var6_12);
                                        var12_17 = (List)var6_12;
                                        var10_11 = BengaliTranslator.INSTANCE.buildReplaceScript((List<String>)var12_17);
                                        var13_18 = (CoroutineContext)Dispatchers.getMain();
                                        var14_20 = new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.$evaluateJs, var10_11, this.this$0, null){
                                            final Function2<String, Function1<? super String, Unit>, Unit> $evaluateJs;
                                            final String $replaceScript;
                                            int label;
                                            final BrowserViewModel this$0;
                                            {
                                                this.$evaluateJs = function2;
                                                this.$replaceScript = string;
                                                this.this$0 = browserViewModel;
                                                super(2, continuation);
                                            }

                                            static final Unit invokeSuspend$lambda$2(BrowserViewModel browserViewModel, String object) {
                                                BrowserUiState browserUiState;
                                                Object object2;
                                                MutableStateFlow mutableStateFlow = BrowserViewModel.access$get_uiState$p(browserViewModel);
                                                do {
                                                    object2 = mutableStateFlow.getValue();
                                                    browserUiState = (BrowserUiState)object2;
                                                    object = browserUiState.getTabs();
                                                    Collection collection = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
                                                    Iterator<T> iterator = object.iterator();
                                                    while (iterator.hasNext()) {
                                                        BrowserTab browserTab = (BrowserTab)iterator.next();
                                                        object = browserTab;
                                                        if (Intrinsics.areEqual((Object)browserTab.getId(), (Object)browserUiState.getCurrentTabId())) {
                                                            object = BrowserTab.copy$default(browserTab, null, null, null, null, false, false, false, null, true, false, 0, false, null, 7935, null);
                                                        }
                                                        collection.add(object);
                                                    }
                                                    object = (List)collection;
                                                } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, true, false, null, null, null, null, false, 2097151998, null)));
                                                browserViewModel.showToast("\u09ac\u09be\u0982\u09b2\u09be\u09df \u0985\u09a8\u09c1\u09ac\u09be\u09a6 \u09b8\u09ae\u09cd\u09aa\u09a8\u09cd\u09a8 \u09b9\u09df\u09c7\u099b\u09c7");
                                                return Unit.INSTANCE;
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
                                                this.$evaluateJs.invoke((Object)this.$replaceScript, (Object)new BrowserViewModel$translateCurrentPage$3$2$2$$ExternalSyntheticLambda0(this.this$0));
                                                return Unit.INSTANCE;
                                            }
                                        };
                                        var15_22 = (Function2)var14_20;
                                        var14_20 = (Continuation)this;
                                        this.L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1);
                                        this.L$1 = SpillingKt.nullOutSpilledVariable((Object)var7_10);
                                        this.L$2 = SpillingKt.nullOutSpilledVariable((Object)var9_9);
                                        this.L$3 = SpillingKt.nullOutSpilledVariable((Object)var5_6);
                                        this.L$4 = SpillingKt.nullOutSpilledVariable((Object)var8_8);
                                        this.L$5 = SpillingKt.nullOutSpilledVariable((Object)var6_12);
                                        this.L$6 = SpillingKt.nullOutSpilledVariable((Object)var12_17);
                                        this.L$7 = SpillingKt.nullOutSpilledVariable((Object)var10_11);
                                        this.label = 2;
                                        if (BuildersKt.withContext((CoroutineContext)var13_18, (Function2)var15_22, (Continuation)var14_20) == var11_4) {
                                            return var11_4;
                                        }
                                    }
                                    var12_17 = Result.exceptionOrNull-impl((Object)var6_12);
                                    if (var12_17 == null) break block30;
                                    var10_11 = var12_17.getMessage();
                                    break block31;
                                }
                                var10_11 = null;
                            }
                            var13_19 /* !! */  = new StringBuilder();
                            Log.e((String)"BrowserViewModel", (String)var13_19 /* !! */ .append("Translation failed: ").append(var10_11).toString(), (Throwable)var12_17);
                            var10_11 = (CoroutineContext)Dispatchers.getMain();
                            var13_19 /* !! */  = new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.this$0, null){
                                int label;
                                final BrowserViewModel this$0;
                                {
                                    this.this$0 = browserViewModel;
                                    super(2, continuation);
                                }

                                public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                                    return (Continuation)new /* invalid duplicate definition of identical inner class */;
                                }

                                public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                                    return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                                }

                                public final Object invokeSuspend(Object object) {
                                    BrowserUiState browserUiState;
                                    Object object2;
                                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                    switch (this.label) {
                                        default: {
                                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                        }
                                        case 0: 
                                    }
                                    ResultKt.throwOnFailure((Object)object);
                                    object = BrowserViewModel.access$get_uiState$p(this.this$0);
                                    while (!object.compareAndSet(object2 = object.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7DFFFFFF, null)))) {
                                    }
                                    this.this$0.showToast("Translation unavailable. Please try again.");
                                    return Unit.INSTANCE;
                                }
                            };
                            var14_21 = (Function2)var13_19 /* !! */ ;
                            var13_19 /* !! */  = (Continuation)this;
                            this.L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1);
                            this.L$1 = SpillingKt.nullOutSpilledVariable((Object)var7_10);
                            this.L$2 = SpillingKt.nullOutSpilledVariable((Object)var9_9);
                            this.L$3 = SpillingKt.nullOutSpilledVariable((Object)var5_6);
                            this.L$4 = SpillingKt.nullOutSpilledVariable((Object)var8_8);
                            this.L$5 = SpillingKt.nullOutSpilledVariable((Object)var6_12);
                            this.L$6 = SpillingKt.nullOutSpilledVariable((Object)var12_17);
                            this.label = 3;
                            if (BuildersKt.withContext((CoroutineContext)var10_11, (Function2)var14_21, (Continuation)var13_19 /* !! */ ) == var11_4) {
                                return var11_4;
                            }
                        }
                        var5_7 = BrowserViewModel.access$get_uiState$p(this.this$0);
                        while (!var5_7.compareAndSet(var6_13 = var5_7.getValue(), var1_1 = BrowserUiState.copy$default((BrowserUiState)var6_13, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7DFFFFFF, null))) {
                        }
                        this.this$0.showToast("No translatable text found");
                        var1_1 = Unit.INSTANCE;
                        return var1_1;
                        {
                            catch (Exception var1_3) {
                                var5_6 = var1_3.getMessage();
                                Log.e((String)"BrowserViewModel", (String)("Translation exception: " + (String)var5_6), (Throwable)var1_3);
                                var7_10 = (CoroutineContext)Dispatchers.getMain();
                                var6_12 = (Function2)new Function2<CoroutineScope, Continuation<? super Unit>, Object>(this.this$0, null){
                                    int label;
                                    final BrowserViewModel this$0;
                                    {
                                        this.this$0 = browserViewModel;
                                        super(2, continuation);
                                    }

                                    public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                                        return (Continuation)new /* invalid duplicate definition of identical inner class */;
                                    }

                                    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Unit> continuation) {
                                        return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                                    }

                                    public final Object invokeSuspend(Object object) {
                                        BrowserUiState browserUiState;
                                        Object object2;
                                        IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                        switch (this.label) {
                                            default: {
                                                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                            }
                                            case 0: 
                                        }
                                        ResultKt.throwOnFailure((Object)object);
                                        object = BrowserViewModel.access$get_uiState$p(this.this$0);
                                        while (!object.compareAndSet(object2 = object.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7DFFFFFF, null)))) {
                                        }
                                        this.this$0.showToast("Translation unavailable. Please try again.");
                                        return Unit.INSTANCE;
                                    }
                                };
                                var5_6 = (Continuation)this;
                                this.L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_3);
                                this.L$1 = null;
                                this.L$2 = null;
                                this.L$3 = null;
                                this.L$4 = null;
                                this.L$5 = null;
                                this.L$6 = null;
                                this.L$7 = null;
                                this.label = 4;
                                if (BuildersKt.withContext((CoroutineContext)var7_10, (Function2)var6_12, (Continuation)var5_6) != var11_4) break block32;
                                return var11_4;
                            }
                        }
                    }
                    return Unit.INSTANCE;
                }
            }), (int)3, null);
            return Unit.INSTANCE;
        }
        mutableStateFlow = browserViewModel._uiState;
        while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7DFFFFFF, null))) {
        }
        browserViewModel.showToast("Cannot extract webpage content");
        return Unit.INSTANCE;
    }

    public static /* synthetic */ void updateDownloadProgress$default(BrowserViewModel browserViewModel, long l, DownloadStatus downloadStatus, long l2, long l3, String string, int n, Object object) {
        if ((n & 0x10) != 0) {
            string = null;
        }
        browserViewModel.updateDownloadProgress(l, downloadStatus, l2, l3, string);
    }

    public static /* synthetic */ void updateDownloadStatus$default(BrowserViewModel browserViewModel, long l, DownloadStatus downloadStatus, String string, long l2, long l3, int n, Object object) {
        if ((n & 4) != 0) {
            string = null;
        }
        if ((n & 8) != 0) {
            l2 = -1L;
        }
        if ((n & 0x10) != 0) {
            l3 = -1L;
        }
        browserViewModel.updateDownloadStatus(l, downloadStatus, string, l2, l3);
    }

    public final boolean addCustomKeyword(String object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"keyword");
        boolean bl = this.repository.addCustomKeyword((String)object);
        if (bl) {
            BrowserUiState browserUiState;
            Object object2;
            MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
            do {
                object = mutableStateFlow.getValue();
                browserUiState = (BrowserUiState)object;
                object2 = this.repository.getCustomKeywords();
            } while (!mutableStateFlow.compareAndSet(object, object2 = BrowserUiState.copy$default(browserUiState, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, "Keyword added and permanently protected.", null, object2, false, false, null, false, false, null, null, null, null, false, 2146172927, null)));
        } else {
            this.showToast("Keyword is empty or already in the protected list.");
        }
        return bl;
    }

    public final void addFavoriteSite(String object, String object2) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"name");
        Intrinsics.checkNotNullParameter((Object)object2, (String)"url");
        object = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
        object2 = ((Object)StringsKt.trim((CharSequence)((CharSequence)object2))).toString();
        int n = ((CharSequence)object).length();
        int n2 = 1;
        n = n == 0 ? 1 : 0;
        if (n == 0 && (n = ((CharSequence)object2).length() == 0 ? n2 : 0) == 0) {
            this.repository.addFavoriteSite((String)object, (String)object2);
            MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
            while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, this.repository.getFavoriteSites(), null, false, false, null, false, false, null, null, null, null, false, 0x7FF7FFFF, null))) {
            }
            this.showToast("Favorite added.");
            return;
        }
        this.showToast("Website name and URL cannot be empty.");
    }

    public final boolean checkAndFilterUrl(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        Log.d((String)"DIAGNOSTIC", (String)("checkAndFilterUrl: URL=" + string));
        ProtectionEngine protectionEngine = ProtectionEngine.INSTANCE;
        protectionEngine = protectionEngine.checkDirectUrl(string, ((BrowserUiState)this._uiState.getValue()).getCustomKeywords(), (Collection)this.repository.getNormalizedKeywords());
        if (protectionEngine instanceof ProtectionEngine.FilterResult.Blocked) {
            this.setBlockedUrl(string, ((ProtectionEngine.FilterResult.Blocked)protectionEngine).getReason(), ((ProtectionEngine.FilterResult.Blocked)protectionEngine).getDetail());
            return true;
        }
        if (DownloadPolicy.INSTANCE.shouldBlockUrlNavigation(string)) {
            this.showToast("This file type is blocked.");
            return true;
        }
        return false;
    }

    public final void clearAllDownloadHistory() {
        BrowserUiState browserUiState;
        Object object;
        this.repository.clearAllDownloadHistory();
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, CollectionsKt.emptyList(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFEFFFF, null)))) {
        }
        this.showToast("Download history cleared.");
    }

    public final void clearAllHistory() {
        BrowserUiState browserUiState;
        Object object;
        this.repository.clearHistory();
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, CollectionsKt.emptyList(), null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFF7FFF, null)))) {
        }
        this.showToast("Browsing history cleared.");
    }

    public final void clearToast() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFBFFFF, null)))) {
        }
    }

    public final void closeDiagnostic() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x3FFFFFFF, null)))) {
        }
    }

    public final void closeDownloads() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFBFFF, null)))) {
        }
    }

    public final void closeHistory() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFDFFF, null)))) {
        }
    }

    public final void closeMenu() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFF7FF, null)))) {
        }
    }

    public final void closeTab(String object) {
        boolean bl;
        boolean bl2;
        boolean bl3;
        boolean bl4;
        String string;
        String string2;
        String string3;
        boolean bl5;
        String string4;
        BrowserUiState browserUiState;
        Object object2;
        Object object3;
        int n;
        Object object4;
        block10: {
            Intrinsics.checkNotNullParameter((Object)object, (String)"tabId");
            object4 = ((BrowserUiState)this._uiState.getValue()).getTabs();
            if (object4.size() <= 1) {
                this.goHome();
                return;
            }
            n = 0;
            object3 = object4.iterator();
            while (object3.hasNext()) {
                if (!Intrinsics.areEqual((Object)object3.next().getId(), (Object)object)) {
                    ++n;
                    continue;
                }
                break block10;
            }
            n = -1;
        }
        if (n == -1) {
            return;
        }
        object4 = (Iterable)object4;
        object3 = new ArrayList();
        Iterator<BrowserTab> iterator = object4.iterator();
        while (iterator.hasNext()) {
            object4 = iterator.next();
            if (Intrinsics.areEqual((Object)((BrowserTab)object4).getId(), (Object)object)) continue;
            object3.add(object4);
        }
        object4 = (List)object3;
        if (Intrinsics.areEqual((Object)((BrowserUiState)this._uiState.getValue()).getCurrentTabId(), (Object)object)) {
            object = (BrowserTab)object4.get(RangesKt.coerceAtLeast((int)(n - 1), (int)0));
        } else {
            block11: {
                object3 = ((Iterable)object4).iterator();
                while (object3.hasNext()) {
                    object = object3.next();
                    if (!Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)((BrowserUiState)this._uiState.getValue()).getCurrentTabId())) continue;
                    break block11;
                }
                object = null;
            }
            object = object3 = (BrowserTab)object;
            if (object3 == null) {
                object = (BrowserTab)CollectionsKt.first((List)object4);
            }
        }
        iterator = this._uiState;
        do {
            object3 = iterator.getValue();
            browserUiState = (BrowserUiState)object3;
            string4 = ((BrowserTab)object).getId();
            bl5 = ((BrowserTab)object).isHomePage();
            string3 = ((BrowserTab)object).getUrl();
            string2 = ((BrowserTab)object).getSearchInput();
            string = ((BrowserTab)object).getPageTitle();
            bl4 = ((BrowserTab)object).isLoading();
            n = ((BrowserTab)object).getLoadingProgress();
            bl3 = ((BrowserTab)object).isPageContentVisible();
            bl2 = ((BrowserTab)object).getCanGoBack();
            bl = ((BrowserTab)object).getCanGoForward();
            object2 = ((BrowserTab)object).getBlockedInfo();
        } while (!iterator.compareAndSet(object3, object2 = BrowserUiState.copy$default(browserUiState, (List)object4, string4, bl5, string3, string2, string, bl4, n, bl3, bl2, bl, false, false, false, false, null, null, (BlockedInfo)object2, null, null, null, false, false, null, ((BrowserTab)object).isPageTranslated(), false, null, null, null, null, false, 2130573312, null)));
        this.persistTabs();
    }

    public final void closeTabsDialog() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFEFFF, null)))) {
        }
    }

    public final void deleteDownloadEntry(String mutableStateFlow) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)mutableStateFlow, (String)"id");
        this.repository.deleteDownloadEntry((String)mutableStateFlow);
        mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, this.repository.getDownloadHistory(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFEFFFF, null)))) {
        }
    }

    public final void deleteHistoryEntry(String object) {
        Object object2;
        Intrinsics.checkNotNullParameter((Object)object, (String)"id");
        this.repository.deleteHistoryEntry((String)object);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, this.repository.getHistory(), null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFF7FFF, null))) {
        }
    }

    public final void dismissSslWarning() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x5FFFFFFF, null)))) {
        }
    }

    public final List<FavoriteSite> getFavoriteSites() {
        List<FavoriteSite> list;
        List<FavoriteSite> list2 = list = (List<FavoriteSite>)((BrowserUiState)this._uiState.getValue()).getFavoriteSites();
        if (list.isEmpty()) {
            list2 = this.repository.getFavoriteSites();
        }
        return list2;
    }

    public final List<String> getNormalizedKeywords() {
        return this.repository.getNormalizedKeywords();
    }

    public final StateFlow<BrowserUiState> getUiState() {
        return this.uiState;
    }

    public final void goHome() {
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = browserUiState.getTabs();
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (BrowserTab)iterator.next();
                if (Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)browserUiState.getCurrentTabId())) {
                    object = BrowserTab.copy$default((BrowserTab)object, null, "", "", "Home", true, false, false, null, false, false, 0, false, null, 1121, null);
                }
                collection.add(object);
            }
            object = (List)collection;
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, true, "", "", "Home", false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2097016450, null)));
        DownloadPolicy.INSTANCE.clearTrustedOrigin();
        this.persistTabs();
    }

    public final void onHistoryCleared() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object;
        } while (!mutableStateFlow.compareAndSet(object, (Object)(browserUiState = BrowserUiState.copy$default(browserUiState, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFF9FF, null))));
    }

    public final void onPageCommitVisible() {
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = browserUiState.getTabs();
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (BrowserTab)iterator.next();
                if (Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)browserUiState.getCurrentTabId())) {
                    object = BrowserTab.copy$default((BrowserTab)object, null, null, null, null, false, false, false, null, false, false, 0, true, null, 6143, null);
                }
                collection.add(object);
            }
            object = (List)collection;
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, null, null, null, false, 0, true, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFFEFE, null)));
    }

    public final void onPageFinished(String string, String object, boolean bl, boolean bl2) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        if (!StringsKt.isBlank((CharSequence)string) && !Intrinsics.areEqual((Object)string, (Object)"about:blank")) {
            Iterable iterable;
            BrowserUiState browserUiState;
            Object object2;
            Object object3 = (MutableStateFlow<BrowserUiState>)object;
            boolean bl3 = object3 == null || StringsKt.isBlank((CharSequence)object3);
            object3 = !bl3 ? object : string;
            boolean bl4 = StringsKt.isBlank((CharSequence)string);
            object = null;
            if (!(bl4 || Intrinsics.areEqual((Object)string, (Object)"about:blank") || StringsKt.startsWith$default((String)string, (String)"about:", (boolean)false, (int)2, null) || ((BrowserUiState)this._uiState.getValue()).getBlockedInfo() != null)) {
                this.repository.addHistoryEntry((String)object3, string);
                object = this.repository.getHistory();
            }
            Object object4 = this._uiState;
            MutableStateFlow<BrowserUiState> mutableStateFlow = object3;
            object3 = object4;
            do {
                object4 = object3.getValue();
                browserUiState = (BrowserUiState)object4;
                iterable = browserUiState.getTabs();
                object2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)iterable, (int)10));
                for (BrowserTab browserTab : iterable) {
                    if (Intrinsics.areEqual((Object)browserTab.getId(), (Object)browserUiState.getCurrentTabId())) {
                        browserTab = BrowserTab.copy$default(browserTab, null, string, string, mutableStateFlow, false, bl, bl2, null, false, false, 0, true, null, 5505, null);
                    }
                    object2.add((HistoryEntry)((Object)browserTab));
                }
                iterable = (List)object2;
                object2 = object == null ? browserUiState.getBrowsingHistory() : object;
            } while (!object3.compareAndSet(object4, object2 = BrowserUiState.copy$default(browserUiState, (List)iterable, null, false, string, string, mutableStateFlow, false, 0, true, bl, bl2, false, false, false, false, object2, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2147448966, null)));
            this.persistTabs();
            return;
        }
    }

    public final void onPageStarted(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        if (!StringsKt.isBlank((CharSequence)string) && !Intrinsics.areEqual((Object)string, (Object)"about:blank")) {
            BrowserUiState browserUiState;
            Object object;
            Object object2;
            MutableStateFlow<BrowserUiState> mutableStateFlow = this.activeTranslationJob;
            if (mutableStateFlow != null) {
                Job.DefaultImpls.cancel$default((Job)mutableStateFlow, null, (int)1, null);
            }
            this.activeTranslationJob = null;
            mutableStateFlow = this._uiState;
            do {
                object2 = mutableStateFlow.getValue();
                browserUiState = (BrowserUiState)object2;
                Iterable iterable = browserUiState.getTabs();
                object = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)iterable, (int)10));
                for (BrowserTab browserTab : iterable) {
                    if (Intrinsics.areEqual((Object)browserTab.getId(), (Object)browserUiState.getCurrentTabId())) {
                        browserTab = BrowserTab.copy$default(browserTab, null, string, string, null, false, false, false, null, false, true, 0, false, null, 7273, null);
                    }
                    object.add(browserTab);
                }
                object = (List)object;
            } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, string, string, null, true, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2097020834, null)));
            return;
        }
    }

    public final void onProgressChanged(int n) {
        Object object;
        Object object2;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            boolean bl;
            boolean bl2;
            int n2 = n;
            object2 = mutableStateFlow.getValue();
            BrowserUiState browserUiState = (BrowserUiState)object2;
            if (browserUiState.getLoadingProgress() == n2 && (bl2 = browserUiState.isLoading()) == (bl = n2 < 100)) {
                object = browserUiState;
                continue;
            }
            Iterable iterable = browserUiState.getTabs();
            object = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)iterable, (int)10));
            for (BrowserTab browserTab : iterable) {
                if (Intrinsics.areEqual((Object)browserTab.getId(), (Object)browserUiState.getCurrentTabId())) {
                    bl = n2 < 100;
                    browserTab = BrowserTab.copy$default(browserTab, null, null, null, null, false, false, false, null, false, bl, n, false, null, 6655, null);
                    n2 = n;
                }
                object.add(browserTab);
            }
            object = (List)object;
            bl = n2 < 100;
            object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, null, null, null, bl, n2, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2147483454, null);
        } while (!mutableStateFlow.compareAndSet(object2, object));
    }

    public final void onSearchInputChange(String string) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)string, (String)"query");
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, string, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFFFEF, null)))) {
        }
    }

    public final void openDiagnostic() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, true, 0x3FFFF7FF, null)))) {
        }
    }

    public final void openDownloads() {
        List<DownloadEntry> list;
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object;
        } while (!mutableStateFlow.compareAndSet(object, (Object)(browserUiState = BrowserUiState.copy$default(browserUiState, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, true, null, list = this.repository.getDownloadHistory(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2147387391, null))));
    }

    public final void openHistory() {
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = this.repository.getHistory();
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, null, null, false, null, null, null, false, 0, false, false, false, false, false, true, false, object, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFF47FF, null)));
    }

    public final void openMenu() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, true, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFE7FF, null)))) {
        }
    }

    public final void openNewTab(String mutableStateFlow) {
        String string;
        String string2;
        String string3;
        List list;
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        Intrinsics.checkNotNullParameter((Object)mutableStateFlow, (String)"url");
        boolean bl = ((CharSequence)mutableStateFlow).length() > 0;
        Object object3 = bl ? mutableStateFlow : "Home";
        boolean bl2 = ((CharSequence)mutableStateFlow).length() == 0;
        object3 = new BrowserTab(null, (String)mutableStateFlow, (String)mutableStateFlow, (String)object3, bl2, false, false, null, false, false, 0, false, null, 8161, null);
        mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            list = CollectionsKt.plus((Collection)browserUiState.getTabs(), (Object)object3);
            object = ((BrowserTab)object3).getId();
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, list, (String)object, bl2 = ((BrowserTab)object3).isHomePage(), string3 = ((BrowserTab)object3).getUrl(), string2 = ((BrowserTab)object3).getSearchInput(), string = ((BrowserTab)object3).getPageTitle(), false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2147344640, null)));
        this.persistTabs();
    }

    public final void openTabsDialog() {
        BrowserUiState browserUiState;
        Object object;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, true, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFE7FF, null)))) {
        }
    }

    public final void recordDownload(DownloadEntry object) {
        Object object2;
        Intrinsics.checkNotNullParameter((Object)object, (String)"entry");
        this.repository.addDownloadEntry((DownloadEntry)object);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, this.repository.getDownloadHistory(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFEFFFF, null))) {
        }
    }

    public final void saveCurrentTabState(Bundle bundle) {
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = browserUiState.getTabs();
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (BrowserTab)iterator.next();
                if (Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)browserUiState.getCurrentTabId())) {
                    object = BrowserTab.copy$default((BrowserTab)object, null, null, null, null, false, false, false, null, false, false, 0, false, bundle, 4095, null);
                }
                collection.add(object);
            }
            object = (List)collection;
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFFFFE, null)));
    }

    public final void saveTabState(String string, Bundle bundle) {
        BrowserUiState browserUiState;
        Object object;
        Object object2;
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            object = browserUiState.getTabs();
            Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)object, (int)10));
            Iterator iterator = object.iterator();
            while (iterator.hasNext()) {
                object = (BrowserTab)iterator.next();
                if (Intrinsics.areEqual((Object)((BrowserTab)object).getId(), (Object)string)) {
                    object = BrowserTab.copy$default((BrowserTab)object, null, null, null, null, false, false, false, null, false, false, 0, false, bundle, 4095, null);
                }
                collection.add(object);
            }
            object = (List)collection;
        } while (!mutableStateFlow.compareAndSet(object2, object = BrowserUiState.copy$default(browserUiState, (List)object, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFFFFFE, null)));
    }

    public final void selectDesktopArchitecture(DesktopArchitecture desktopArchitecture) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"architecture");
        this.repository.setDesktopArchitecture(desktopArchitecture);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, desktopArchitecture, false, false, null, null, null, null, false, 0x7F7FFFFF, null)))) {
        }
    }

    public final void selectSearchEngine(SearchEngine searchEngine) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)((Object)searchEngine), (String)"engine");
        this.repository.setSelectedSearchEngine(searchEngine);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, searchEngine, null, false, 0x6FFFFFFF, null)))) {
        }
    }

    public final void selectTab(String object) {
        boolean bl;
        BlockedInfo blockedInfo;
        boolean bl2;
        boolean bl3;
        boolean bl4;
        int n;
        boolean bl5;
        String string;
        String string2;
        boolean bl6;
        String string3;
        BrowserUiState browserUiState;
        Object object2;
        Object object32;
        block3: {
            Intrinsics.checkNotNullParameter((Object)object, (String)"tabId");
            for (Object object32 : (Iterable)((BrowserUiState)this._uiState.getValue()).getTabs()) {
                if (!Intrinsics.areEqual((Object)((BrowserTab)object32).getId(), (Object)object)) continue;
                object = object32;
                break block3;
            }
            object = null;
        }
        object = (BrowserTab)object;
        if (object == null) {
            return;
        }
        Iterator iterator = this._uiState;
        do {
            object32 = iterator.getValue();
            browserUiState = (BrowserUiState)object32;
            string3 = ((BrowserTab)object).getId();
            bl6 = ((BrowserTab)object).isHomePage();
            object2 = ((BrowserTab)object).getUrl();
        } while (!iterator.compareAndSet(object32, object2 = BrowserUiState.copy$default(browserUiState, null, string3, bl6, (String)object2, string2 = ((BrowserTab)object).getSearchInput(), string = ((BrowserTab)object).getPageTitle(), bl5 = ((BrowserTab)object).isLoading(), n = ((BrowserTab)object).getLoadingProgress(), bl4 = ((BrowserTab)object).isPageContentVisible(), bl3 = ((BrowserTab)object).getCanGoBack(), bl2 = ((BrowserTab)object).getCanGoForward(), false, false, false, false, null, null, blockedInfo = ((BrowserTab)object).getBlockedInfo(), null, null, null, false, false, null, bl = ((BrowserTab)object).isPageTranslated(), false, null, null, null, null, false, 2130569217, null)));
        this.persistTabs();
    }

    public final void selectTranslationEngine(TranslationEngine translationEngine) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)((Object)translationEngine), (String)"engine");
        this.repository.setSelectedTranslationEngine(translationEngine);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, translationEngine, null, null, false, 0x77FFFFFF, null)))) {
        }
    }

    public final void setAppTheme(AppTheme appTheme) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)appTheme, (String)"theme");
        this.repository.setAppTheme(appTheme);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, appTheme, null, null, null, false, 0x7BFFFFFF, null)))) {
        }
    }

    public final void setBlockedUrl(String mutableStateFlow, String object, String object2) {
        BrowserUiState browserUiState;
        Object object3;
        Intrinsics.checkNotNullParameter((Object)mutableStateFlow, (String)"url");
        Intrinsics.checkNotNullParameter((Object)object, (String)"reason");
        Intrinsics.checkNotNullParameter((Object)object2, (String)"detail");
        Log.e((String)"DIAGNOSTIC", (String)"BLOCK_FUNCTION=BrowserViewModel.setBlockedUrl");
        Log.e((String)"DIAGNOSTIC", (String)("BLOCK_REASON=" + (String)object + ": " + (String)object2));
        Log.e((String)"DIAGNOSTIC", (String)("REQUEST_URL=" + (String)mutableStateFlow));
        Log.e((String)"DIAGNOSTIC", (String)"PROTECTION_RESULT=Blocked");
        object = new BlockedInfo((String)object, (String)object2, (String)mutableStateFlow);
        mutableStateFlow = this._uiState;
        do {
            object2 = mutableStateFlow.getValue();
            browserUiState = (BrowserUiState)object2;
            Iterable iterable = browserUiState.getTabs();
            object3 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)iterable, (int)10));
            for (BrowserTab browserTab : iterable) {
                if (Intrinsics.areEqual((Object)browserTab.getId(), (Object)browserUiState.getCurrentTabId())) {
                    browserTab = BrowserTab.copy$default(browserTab, null, null, null, null, false, false, false, (BlockedInfo)object, false, false, 0, true, null, 5487, null);
                }
                object3.add(browserTab);
            }
            object3 = (List)object3;
        } while (!mutableStateFlow.compareAndSet(object2, object3 = BrowserUiState.copy$default(browserUiState, (List)object3, null, false, null, null, null, false, 0, true, false, false, false, false, false, false, null, null, (BlockedInfo)object, null, null, null, false, false, null, false, false, null, null, null, null, false, 2147352250, null)));
    }

    public final void showSslWarning(String string, String string2, String string3) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"details");
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, new SslWarningState(string, string2, string3), false, 0x5FFFFFFF, null)))) {
        }
    }

    public final void showToast(String string) {
        BrowserUiState browserUiState;
        Object object;
        Intrinsics.checkNotNullParameter((Object)string, (String)"msg");
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, string, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFBFFFF, null)))) {
        }
    }

    public final boolean submitQueryOrUrl(String object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"input");
        object = ((Object)StringsKt.trim((CharSequence)object)).toString();
        boolean bl = ((CharSequence)object).length() == 0;
        if (bl) {
            return false;
        }
        Object object2 = this.repository.getNormalizedKeywords();
        Set<String> set = ((BrowserUiState)this._uiState.getValue()).getCustomKeywords();
        Object object3 = ProtectionEngine.INSTANCE.extractSearchEngineQuery(object);
        if (object3 != null) {
            if ((object2 = ProtectionEngine.INSTANCE.isBlockedByCustomKeywords(object3, set, (Collection)object2)) != null) {
                this.setBlockedUrl((String)object, "Custom Keyword Protection", "Search query blocked due to protected keyword: \"" + (String)object2 + "\"");
                return false;
            }
            object = ((BrowserUiState)this._uiState.getValue()).getSelectedSearchEngine();
            this.loadTargetUrl(ProtectionEngine.INSTANCE.buildSafeSearchUrl(object3, (SearchEngine)((Object)object)));
            return true;
        }
        if (!this.isWebUrl((String)object)) {
            object3 = ProtectionEngine.INSTANCE.isBlockedByCustomKeywords(object, set, (Collection)object2);
            if (object3 != null) {
                this.setBlockedUrl((String)object, "Custom Keyword Protection", "Search query blocked due to protected keyword: \"" + object3 + "\"");
                return false;
            }
            object3 = ((BrowserUiState)this._uiState.getValue()).getSelectedSearchEngine();
            this.loadTargetUrl(ProtectionEngine.INSTANCE.buildSafeSearchUrl(object, (SearchEngine)((Object)object3)));
            return true;
        }
        object3 = ProtectionEngine.INSTANCE.checkDirectUrl(object = this.formatDirectUrl((String)object), set, (Collection)object2);
        if (object3 instanceof ProtectionEngine.FilterResult.Blocked) {
            this.setBlockedUrl((String)object, ((ProtectionEngine.FilterResult.Blocked)object3).getReason(), ((ProtectionEngine.FilterResult.Blocked)object3).getDetail());
            return false;
        }
        if (DownloadPolicy.INSTANCE.shouldBlockUrlNavigation(object)) {
            this.showToast("This file type is blocked.");
            return false;
        }
        this.loadTargetUrl((String)object);
        return true;
    }

    public final void toggleAdBlocking(boolean bl) {
        BrowserUiState browserUiState;
        Object object;
        this.repository.setAdBlockingEnabled(bl);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, bl, null, false, false, null, null, null, null, false, 0x7FBFFFFF, null)))) {
        }
    }

    public final void toggleDesktopMode(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE;
        this.selectDesktopArchitecture(desktopArchitecture);
    }

    public final void toggleDesktopMode4(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.DESKTOP_MODE_4 : DesktopArchitecture.NONE;
        this.selectDesktopArchitecture(desktopArchitecture);
    }

    public final void togglePopupBlocking(boolean bl) {
        BrowserUiState browserUiState;
        Object object;
        this.repository.setPopupBlockingEnabled(bl);
        MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), (Object)(browserUiState = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, bl, false, null, false, false, null, null, null, null, false, 0x7FDFFFFF, null)))) {
        }
    }

    public final void toggleWindows10Touch(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.WINDOWS_10_TOUCH : DesktopArchitecture.NONE;
        this.selectDesktopArchitecture(desktopArchitecture);
    }

    public final void translateCurrentPage(Function2<? super String, ? super Function1<? super String, Unit>, Unit> object, Function0<Unit> object2) {
        Intrinsics.checkNotNullParameter(object, (String)"evaluateJs");
        Intrinsics.checkNotNullParameter(object2, (String)"reloadPage");
        this.closeMenu();
        Object object3 = (BrowserUiState)this._uiState.getValue();
        if (!(((BrowserUiState)object3).isHomePage() || StringsKt.isBlank((CharSequence)((BrowserUiState)object3).getCurrentUrl()) || StringsKt.startsWith$default((String)((BrowserUiState)object3).getCurrentUrl(), (String)"about:", (boolean)false, (int)2, null))) {
            if (((BrowserUiState)object3).isTranslating()) {
                this.showToast("Translation in progress...");
                return;
            }
            if (((BrowserUiState)object3).isPageTranslated()) {
                BrowserUiState browserUiState;
                Object object4;
                object2.invoke();
                MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
                object = object3;
                do {
                    object4 = mutableStateFlow.getValue();
                    browserUiState = (BrowserUiState)object4;
                    object2 = browserUiState.getTabs();
                    Collection collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(object2, (int)10));
                    Iterator iterator = object2.iterator();
                    while (iterator.hasNext()) {
                        object2 = object3 = (BrowserTab)iterator.next();
                        if (Intrinsics.areEqual((Object)((BrowserTab)object3).getId(), (Object)browserUiState.getCurrentTabId())) {
                            object2 = BrowserTab.copy$default((BrowserTab)object3, null, null, null, null, false, false, false, null, false, false, 0, false, null, 7935, null);
                        }
                        collection.add(object2);
                    }
                    object2 = (List)collection;
                } while (!mutableStateFlow.compareAndSet(object4, object2 = BrowserUiState.copy$default(browserUiState, (List)object2, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, 2097151998, null)));
                this.showToast("Original page restored");
                return;
            }
            MutableStateFlow<BrowserUiState> mutableStateFlow = this._uiState;
            while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object3 = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, true, null, null, null, null, false, 0x7DFFFFFF, null))) {
            }
            this.showToast("Translating to \u09ac\u09be\u0982\u09b2\u09be...");
            object.invoke((Object)BengaliTranslator.INSTANCE.buildExtractScript(), (Object)new BrowserViewModel$$ExternalSyntheticLambda0(this, (Function2)object));
            return;
        }
        this.showToast("Translate works on active webpages");
    }

    public final void updateDownloadProgress(long l, DownloadStatus object, long l2, long l3, String mutableStateFlow) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"status");
        Object object2 = this.repository;
        ((SettingsRepository)object2).updateDownloadProgress(l, (DownloadStatus)((Object)object), l2, l3, (String)mutableStateFlow, false);
        mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object2 = mutableStateFlow.getValue(), object = BrowserUiState.copy$default((BrowserUiState)object2, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, this.repository.getDownloadHistory(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFEFFFF, null))) {
        }
    }

    public final void updateDownloadStatus(long l, DownloadStatus object, String object2, long l2, long l3) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"status");
        MutableStateFlow<BrowserUiState> mutableStateFlow = this.repository;
        mutableStateFlow.updateDownloadStatus(l, object, (String)object2, l2, l3);
        mutableStateFlow = this._uiState;
        while (!mutableStateFlow.compareAndSet(object = mutableStateFlow.getValue(), object2 = BrowserUiState.copy$default((BrowserUiState)object, null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, this.repository.getDownloadHistory(), null, null, null, null, false, false, null, false, false, null, null, null, null, false, 0x7FFEFFFF, null))) {
        }
    }
}

