/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import com.muslim.browser.pro.browser.BlockedInfo;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.HistoryEntry;
import com.muslim.browser.pro.browser.SearchEngine;
import com.muslim.browser.pro.browser.SslWarningState;
import com.muslim.browser.pro.browser.TranslationEngine;
import com.muslim.browser.pro.ui.theme.AppTheme;
import java.util.List;
import java.util.Set;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u0000n\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0005\n\u0002\u0010\b\n\u0002\b\b\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\bI\b\u0087\b\u0018\u00002\u00020\u0001B\u00e1\u0002\u0012\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\u0006\u0012\b\b\u0002\u0010\n\u001a\u00020\u0006\u0012\b\b\u0002\u0010\u000b\u001a\u00020\u0006\u0012\b\b\u0002\u0010\f\u001a\u00020\b\u0012\b\b\u0002\u0010\r\u001a\u00020\u000e\u0012\b\b\u0002\u0010\u000f\u001a\u00020\b\u0012\b\b\u0002\u0010\u0010\u001a\u00020\b\u0012\b\b\u0002\u0010\u0011\u001a\u00020\b\u0012\b\b\u0002\u0010\u0012\u001a\u00020\b\u0012\b\b\u0002\u0010\u0013\u001a\u00020\b\u0012\b\b\u0002\u0010\u0014\u001a\u00020\b\u0012\b\b\u0002\u0010\u0015\u001a\u00020\b\u0012\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0003\u0012\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0003\u0012\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u0012\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u0006\u0012\u000e\b\u0002\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0003\u0012\u000e\b\u0002\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00060 \u0012\b\b\u0002\u0010!\u001a\u00020\b\u0012\b\b\u0002\u0010\"\u001a\u00020\b\u0012\b\b\u0002\u0010#\u001a\u00020$\u0012\b\b\u0002\u0010%\u001a\u00020\b\u0012\b\b\u0002\u0010&\u001a\u00020\b\u0012\b\b\u0002\u0010'\u001a\u00020(\u0012\b\b\u0002\u0010)\u001a\u00020*\u0012\b\b\u0002\u0010+\u001a\u00020,\u0012\n\b\u0002\u0010-\u001a\u0004\u0018\u00010.\u0012\b\b\u0002\u0010/\u001a\u00020\b\u00a2\u0006\u0004\b0\u00101J\u000f\u0010S\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003H\u00c6\u0003J\t\u0010T\u001a\u00020\u0006H\u00c6\u0003J\t\u0010U\u001a\u00020\bH\u00c6\u0003J\t\u0010V\u001a\u00020\u0006H\u00c6\u0003J\t\u0010W\u001a\u00020\u0006H\u00c6\u0003J\t\u0010X\u001a\u00020\u0006H\u00c6\u0003J\t\u0010Y\u001a\u00020\bH\u00c6\u0003J\t\u0010Z\u001a\u00020\u000eH\u00c6\u0003J\t\u0010[\u001a\u00020\bH\u00c6\u0003J\t\u0010\\\u001a\u00020\bH\u00c6\u0003J\t\u0010]\u001a\u00020\bH\u00c6\u0003J\t\u0010^\u001a\u00020\bH\u00c6\u0003J\t\u0010_\u001a\u00020\bH\u00c6\u0003J\t\u0010`\u001a\u00020\bH\u00c6\u0003J\t\u0010a\u001a\u00020\bH\u00c6\u0003J\u000f\u0010b\u001a\b\u0012\u0004\u0012\u00020\u00170\u0003H\u00c6\u0003J\u000f\u0010c\u001a\b\u0012\u0004\u0012\u00020\u00190\u0003H\u00c6\u0003J\u000b\u0010d\u001a\u0004\u0018\u00010\u001bH\u00c6\u0003J\u000b\u0010e\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J\u000f\u0010f\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0003H\u00c6\u0003J\u000f\u0010g\u001a\b\u0012\u0004\u0012\u00020\u00060 H\u00c6\u0003J\t\u0010h\u001a\u00020\bH\u00c6\u0003J\t\u0010i\u001a\u00020\bH\u00c6\u0003J\t\u0010j\u001a\u00020$H\u00c6\u0003J\t\u0010k\u001a\u00020\bH\u00c6\u0003J\t\u0010l\u001a\u00020\bH\u00c6\u0003J\t\u0010m\u001a\u00020(H\u00c6\u0003J\t\u0010n\u001a\u00020*H\u00c6\u0003J\t\u0010o\u001a\u00020,H\u00c6\u0003J\u000b\u0010p\u001a\u0004\u0018\u00010.H\u00c6\u0003J\t\u0010q\u001a\u00020\bH\u00c6\u0003J\u00e3\u0002\u0010r\u001a\u00020\u00002\u000e\b\u0002\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\b2\b\b\u0002\u0010\r\u001a\u00020\u000e2\b\b\u0002\u0010\u000f\u001a\u00020\b2\b\b\u0002\u0010\u0010\u001a\u00020\b2\b\b\u0002\u0010\u0011\u001a\u00020\b2\b\b\u0002\u0010\u0012\u001a\u00020\b2\b\b\u0002\u0010\u0013\u001a\u00020\b2\b\b\u0002\u0010\u0014\u001a\u00020\b2\b\b\u0002\u0010\u0015\u001a\u00020\b2\u000e\b\u0002\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u00032\u000e\b\u0002\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u00032\n\b\u0002\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\n\b\u0002\u0010\u001c\u001a\u0004\u0018\u00010\u00062\u000e\b\u0002\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u00032\u000e\b\u0002\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00060 2\b\b\u0002\u0010!\u001a\u00020\b2\b\b\u0002\u0010\"\u001a\u00020\b2\b\b\u0002\u0010#\u001a\u00020$2\b\b\u0002\u0010%\u001a\u00020\b2\b\b\u0002\u0010&\u001a\u00020\b2\b\b\u0002\u0010'\u001a\u00020(2\b\b\u0002\u0010)\u001a\u00020*2\b\b\u0002\u0010+\u001a\u00020,2\n\b\u0002\u0010-\u001a\u0004\u0018\u00010.2\b\b\u0002\u0010/\u001a\u00020\bH\u00c6\u0001J\u0013\u0010s\u001a\u00020\b2\b\u0010t\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010u\u001a\u00020\u000eH\u00d6\u0001J\t\u0010v\u001a\u00020\u0006H\u00d6\u0001R\u0017\u0010\u0002\u001a\b\u0012\u0004\u0012\u00020\u00040\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b2\u00103R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b4\u00105R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u00106R\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b7\u00105R\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b8\u00105R\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b9\u00105R\u0011\u0010\f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u00106R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b:\u0010;R\u0011\u0010\u000f\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u00106R\u0011\u0010\u0010\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b<\u00106R\u0011\u0010\u0011\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b=\u00106R\u0011\u0010\u0012\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u00106R\u0011\u0010\u0013\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u00106R\u0011\u0010\u0014\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u00106R\u0011\u0010\u0015\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u00106R\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00170\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b>\u00103R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b?\u00103R\u0013\u0010\u001a\u001a\u0004\u0018\u00010\u001b\u00a2\u0006\b\n\u0000\u001a\u0004\b@\u0010AR\u0013\u0010\u001c\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\bB\u00105R\u0017\u0010\u001d\u001a\b\u0012\u0004\u0012\u00020\u001e0\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\bC\u00103R\u0017\u0010\u001f\u001a\b\u0012\u0004\u0012\u00020\u00060 \u00a2\u0006\b\n\u0000\u001a\u0004\bD\u0010ER\u0011\u0010!\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b!\u00106R\u0011\u0010\"\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u00106R\u0011\u0010#\u001a\u00020$\u00a2\u0006\b\n\u0000\u001a\u0004\bF\u0010GR\u0011\u0010%\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b%\u00106R\u0011\u0010&\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b&\u00106R\u0011\u0010'\u001a\u00020(\u00a2\u0006\b\n\u0000\u001a\u0004\bH\u0010IR\u0011\u0010)\u001a\u00020*\u00a2\u0006\b\n\u0000\u001a\u0004\bJ\u0010KR\u0011\u0010+\u001a\u00020,\u00a2\u0006\b\n\u0000\u001a\u0004\bL\u0010MR\u0013\u0010-\u001a\u0004\u0018\u00010.\u00a2\u0006\b\n\u0000\u001a\u0004\bN\u0010OR\u0011\u0010/\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b/\u00106R\u0011\u0010P\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\bP\u00106R\u0011\u0010Q\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\bQ\u00106R\u0011\u0010R\u001a\u00020\b8F\u00a2\u0006\u0006\u001a\u0004\bR\u00106\u00a8\u0006w"}, d2={"Lcom/muslim/browser/pro/browser/BrowserUiState;", "", "tabs", "", "Lcom/muslim/browser/pro/browser/BrowserTab;", "currentTabId", "", "isHomePage", "", "currentUrl", "searchInput", "pageTitle", "isLoading", "loadingProgress", "", "isPageContentVisible", "canGoBack", "canGoForward", "isMenuOpen", "isTabsDialogOpen", "isHistoryOpen", "isDownloadsOpen", "browsingHistory", "Lcom/muslim/browser/pro/browser/HistoryEntry;", "downloadHistory", "Lcom/muslim/browser/pro/browser/DownloadEntry;", "blockedInfo", "Lcom/muslim/browser/pro/browser/BlockedInfo;", "toastMessage", "favoriteSites", "Lcom/muslim/browser/pro/browser/FavoriteSite;", "customKeywords", "", "isPopupBlockingEnabled", "isAdBlockingEnabled", "desktopArchitecture", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "isPageTranslated", "isTranslating", "appTheme", "Lcom/muslim/browser/pro/ui/theme/AppTheme;", "selectedTranslationEngine", "Lcom/muslim/browser/pro/browser/TranslationEngine;", "selectedSearchEngine", "Lcom/muslim/browser/pro/browser/SearchEngine;", "sslWarningState", "Lcom/muslim/browser/pro/browser/SslWarningState;", "isDiagnosticOpen", "<init>", "(Ljava/util/List;Ljava/lang/String;ZLjava/lang/String;Ljava/lang/String;Ljava/lang/String;ZIZZZZZZZLjava/util/List;Ljava/util/List;Lcom/muslim/browser/pro/browser/BlockedInfo;Ljava/lang/String;Ljava/util/List;Ljava/util/Set;ZZLcom/muslim/browser/pro/browser/DesktopArchitecture;ZZLcom/muslim/browser/pro/ui/theme/AppTheme;Lcom/muslim/browser/pro/browser/TranslationEngine;Lcom/muslim/browser/pro/browser/SearchEngine;Lcom/muslim/browser/pro/browser/SslWarningState;Z)V", "getTabs", "()Ljava/util/List;", "getCurrentTabId", "()Ljava/lang/String;", "()Z", "getCurrentUrl", "getSearchInput", "getPageTitle", "getLoadingProgress", "()I", "getCanGoBack", "getCanGoForward", "getBrowsingHistory", "getDownloadHistory", "getBlockedInfo", "()Lcom/muslim/browser/pro/browser/BlockedInfo;", "getToastMessage", "getFavoriteSites", "getCustomKeywords", "()Ljava/util/Set;", "getDesktopArchitecture", "()Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "getAppTheme", "()Lcom/muslim/browser/pro/ui/theme/AppTheme;", "getSelectedTranslationEngine", "()Lcom/muslim/browser/pro/browser/TranslationEngine;", "getSelectedSearchEngine", "()Lcom/muslim/browser/pro/browser/SearchEngine;", "getSslWarningState", "()Lcom/muslim/browser/pro/browser/SslWarningState;", "isDesktopModeEnabled", "isDesktopMode4Enabled", "isWindows10TouchEnabled", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "component14", "component15", "component16", "component17", "component18", "component19", "component20", "component21", "component22", "component23", "component24", "component25", "component26", "component27", "component28", "component29", "component30", "component31", "copy", "equals", "other", "hashCode", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class BrowserUiState {
    public static final int $stable = 8;
    private final AppTheme appTheme;
    private final BlockedInfo blockedInfo;
    private final List<HistoryEntry> browsingHistory;
    private final boolean canGoBack;
    private final boolean canGoForward;
    private final String currentTabId;
    private final String currentUrl;
    private final Set<String> customKeywords;
    private final DesktopArchitecture desktopArchitecture;
    private final List<DownloadEntry> downloadHistory;
    private final List<FavoriteSite> favoriteSites;
    private final boolean isAdBlockingEnabled;
    private final boolean isDiagnosticOpen;
    private final boolean isDownloadsOpen;
    private final boolean isHistoryOpen;
    private final boolean isHomePage;
    private final boolean isLoading;
    private final boolean isMenuOpen;
    private final boolean isPageContentVisible;
    private final boolean isPageTranslated;
    private final boolean isPopupBlockingEnabled;
    private final boolean isTabsDialogOpen;
    private final boolean isTranslating;
    private final int loadingProgress;
    private final String pageTitle;
    private final String searchInput;
    private final SearchEngine selectedSearchEngine;
    private final TranslationEngine selectedTranslationEngine;
    private final SslWarningState sslWarningState;
    private final List<BrowserTab> tabs;
    private final String toastMessage;

    public BrowserUiState() {
        this(null, null, false, null, null, null, false, 0, false, false, false, false, false, false, false, null, null, null, null, null, null, false, false, null, false, false, null, null, null, null, false, Integer.MAX_VALUE, null);
    }

    public BrowserUiState(List<BrowserTab> list, String string, boolean bl, String string2, String string3, String string4, boolean bl2, int n, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, List<HistoryEntry> list2, List<DownloadEntry> list3, BlockedInfo blockedInfo, String string5, List<FavoriteSite> list4, Set<String> set, boolean bl10, boolean bl11, DesktopArchitecture desktopArchitecture, boolean bl12, boolean bl13, AppTheme appTheme, TranslationEngine translationEngine, SearchEngine searchEngine, SslWarningState sslWarningState, boolean bl14) {
        Intrinsics.checkNotNullParameter(list, (String)"tabs");
        Intrinsics.checkNotNullParameter((Object)string, (String)"currentTabId");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"currentUrl");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"searchInput");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"pageTitle");
        Intrinsics.checkNotNullParameter(list2, (String)"browsingHistory");
        Intrinsics.checkNotNullParameter(list3, (String)"downloadHistory");
        Intrinsics.checkNotNullParameter(list4, (String)"favoriteSites");
        Intrinsics.checkNotNullParameter(set, (String)"customKeywords");
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"desktopArchitecture");
        Intrinsics.checkNotNullParameter((Object)appTheme, (String)"appTheme");
        Intrinsics.checkNotNullParameter((Object)((Object)translationEngine), (String)"selectedTranslationEngine");
        Intrinsics.checkNotNullParameter((Object)((Object)searchEngine), (String)"selectedSearchEngine");
        this.tabs = list;
        this.currentTabId = string;
        this.isHomePage = bl;
        this.currentUrl = string2;
        this.searchInput = string3;
        this.pageTitle = string4;
        this.isLoading = bl2;
        this.loadingProgress = n;
        this.isPageContentVisible = bl3;
        this.canGoBack = bl4;
        this.canGoForward = bl5;
        this.isMenuOpen = bl6;
        this.isTabsDialogOpen = bl7;
        this.isHistoryOpen = bl8;
        this.isDownloadsOpen = bl9;
        this.browsingHistory = list2;
        this.downloadHistory = list3;
        this.blockedInfo = blockedInfo;
        this.toastMessage = string5;
        this.favoriteSites = list4;
        this.customKeywords = set;
        this.isPopupBlockingEnabled = bl10;
        this.isAdBlockingEnabled = bl11;
        this.desktopArchitecture = desktopArchitecture;
        this.isPageTranslated = bl12;
        this.isTranslating = bl13;
        this.appTheme = appTheme;
        this.selectedTranslationEngine = translationEngine;
        this.selectedSearchEngine = searchEngine;
        this.sslWarningState = sslWarningState;
        this.isDiagnosticOpen = bl14;
    }

    public /* synthetic */ BrowserUiState(List list, String string, boolean bl, String string2, String string3, String string4, boolean bl2, int n, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, List list2, List list3, BlockedInfo blockedInfo, String string5, List list4, Set set, boolean bl10, boolean bl11, DesktopArchitecture desktopArchitecture, boolean bl12, boolean bl13, AppTheme appTheme, TranslationEngine translationEngine, SearchEngine searchEngine, SslWarningState sslWarningState, boolean bl14, int n2, DefaultConstructorMarker object) {
        if ((n2 & 1) != 0) {
            list = CollectionsKt.listOf((Object)new BrowserTab("default_tab", null, null, null, false, false, false, null, false, false, 0, false, null, 8190, null));
        }
        if ((n2 & 2) != 0) {
            string = "default_tab";
        }
        if ((n2 & 4) != 0) {
            bl = true;
        }
        object = "";
        if ((n2 & 8) != 0) {
            string2 = "";
        }
        if ((n2 & 0x10) != 0) {
            string3 = object;
        }
        if ((n2 & 0x20) != 0) {
            string4 = "Home";
        }
        if ((n2 & 0x40) != 0) {
            bl2 = false;
        }
        if ((n2 & 0x80) != 0) {
            n = 0;
        }
        if ((n2 & 0x100) != 0) {
            bl3 = false;
        }
        if ((n2 & 0x200) != 0) {
            bl4 = false;
        }
        if ((n2 & 0x400) != 0) {
            bl5 = false;
        }
        if ((n2 & 0x800) != 0) {
            bl6 = false;
        }
        if ((n2 & 0x1000) != 0) {
            bl7 = false;
        }
        if ((n2 & 0x2000) != 0) {
            bl8 = false;
        }
        if ((n2 & 0x4000) != 0) {
            bl9 = false;
        }
        if ((n2 & 0x8000) != 0) {
            list2 = CollectionsKt.emptyList();
        }
        if ((n2 & 0x10000) != 0) {
            list3 = CollectionsKt.emptyList();
        }
        object = null;
        if ((n2 & 0x20000) != 0) {
            blockedInfo = null;
        }
        if ((n2 & 0x40000) != 0) {
            string5 = null;
        }
        if ((n2 & 0x80000) != 0) {
            list4 = CollectionsKt.emptyList();
        }
        if ((n2 & 0x100000) != 0) {
            set = SetsKt.emptySet();
        }
        if ((n2 & 0x200000) != 0) {
            bl10 = true;
        }
        if ((n2 & 0x400000) != 0) {
            bl11 = true;
        }
        if ((n2 & 0x800000) != 0) {
            desktopArchitecture = DesktopArchitecture.NONE;
        }
        if ((n2 & 0x1000000) != 0) {
            bl12 = false;
        }
        if ((n2 & 0x2000000) != 0) {
            bl13 = false;
        }
        if ((n2 & 0x4000000) != 0) {
            appTheme = AppTheme.BLACK_WHITE;
        }
        if ((n2 & 0x8000000) != 0) {
            translationEngine = TranslationEngine.LIBRE_TRANSLATE;
        }
        if ((n2 & 0x10000000) != 0) {
            searchEngine = SearchEngine.DUCKDUCKGO;
        }
        if ((n2 & 0x20000000) != 0) {
            sslWarningState = object;
        }
        if ((n2 & 0x40000000) != 0) {
            bl14 = false;
        }
        this(list, string, bl, string2, string3, string4, bl2, n, bl3, bl4, bl5, bl6, bl7, bl8, bl9, list2, list3, blockedInfo, string5, list4, set, bl10, bl11, desktopArchitecture, bl12, bl13, appTheme, translationEngine, searchEngine, sslWarningState, bl14);
    }

    public static /* synthetic */ BrowserUiState copy$default(BrowserUiState browserUiState, List list, String string, boolean bl, String string2, String string3, String string4, boolean bl2, int n, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, List list2, List list3, BlockedInfo blockedInfo, String string5, List list4, Set set, boolean bl10, boolean bl11, DesktopArchitecture desktopArchitecture, boolean bl12, boolean bl13, AppTheme appTheme, TranslationEngine translationEngine, SearchEngine searchEngine, SslWarningState sslWarningState, boolean bl14, int n2, Object object) {
        block30: {
            if ((n2 & 1) != 0) {
                list = browserUiState.tabs;
            }
            if ((n2 & 2) != 0) {
                string = browserUiState.currentTabId;
            }
            if ((n2 & 4) != 0) {
                bl = browserUiState.isHomePage;
            }
            if ((n2 & 8) != 0) {
                string2 = browserUiState.currentUrl;
            }
            if ((n2 & 0x10) != 0) {
                string3 = browserUiState.searchInput;
            }
            if ((n2 & 0x20) != 0) {
                string4 = browserUiState.pageTitle;
            }
            if ((n2 & 0x40) != 0) {
                bl2 = browserUiState.isLoading;
            }
            if ((n2 & 0x80) != 0) {
                n = browserUiState.loadingProgress;
            }
            if ((n2 & 0x100) != 0) {
                bl3 = browserUiState.isPageContentVisible;
            }
            if ((n2 & 0x200) != 0) {
                bl4 = browserUiState.canGoBack;
            }
            if ((n2 & 0x400) != 0) {
                bl5 = browserUiState.canGoForward;
            }
            if ((n2 & 0x800) != 0) {
                bl6 = browserUiState.isMenuOpen;
            }
            if ((n2 & 0x1000) != 0) {
                bl7 = browserUiState.isTabsDialogOpen;
            }
            if ((n2 & 0x2000) != 0) {
                bl8 = browserUiState.isHistoryOpen;
            }
            if ((n2 & 0x4000) != 0) {
                bl9 = browserUiState.isDownloadsOpen;
            }
            if ((n2 & 0x8000) != 0) {
                list2 = browserUiState.browsingHistory;
            }
            if ((n2 & 0x10000) != 0) {
                list3 = browserUiState.downloadHistory;
            }
            if ((n2 & 0x20000) != 0) {
                blockedInfo = browserUiState.blockedInfo;
            }
            if ((n2 & 0x40000) != 0) {
                string5 = browserUiState.toastMessage;
            }
            if ((n2 & 0x80000) != 0) {
                list4 = browserUiState.favoriteSites;
            }
            if ((n2 & 0x100000) != 0) {
                set = browserUiState.customKeywords;
            }
            if ((n2 & 0x200000) != 0) {
                bl10 = browserUiState.isPopupBlockingEnabled;
            }
            if ((n2 & 0x400000) != 0) {
                bl11 = browserUiState.isAdBlockingEnabled;
            }
            if ((n2 & 0x800000) != 0) {
                desktopArchitecture = browserUiState.desktopArchitecture;
            }
            if ((n2 & 0x1000000) != 0) {
                bl12 = browserUiState.isPageTranslated;
            }
            if ((n2 & 0x2000000) != 0) {
                bl13 = browserUiState.isTranslating;
            }
            if ((n2 & 0x4000000) != 0) {
                appTheme = browserUiState.appTheme;
            }
            if ((n2 & 0x8000000) != 0) {
                translationEngine = browserUiState.selectedTranslationEngine;
            }
            if ((n2 & 0x10000000) != 0) {
                searchEngine = browserUiState.selectedSearchEngine;
            }
            if ((n2 & 0x20000000) != 0) {
                sslWarningState = browserUiState.sslWarningState;
            }
            if ((n2 & 0x40000000) == 0) break block30;
            bl14 = browserUiState.isDiagnosticOpen;
        }
        return browserUiState.copy(list, string, bl, string2, string3, string4, bl2, n, bl3, bl4, bl5, bl6, bl7, bl8, bl9, list2, list3, blockedInfo, string5, list4, set, bl10, bl11, desktopArchitecture, bl12, bl13, appTheme, translationEngine, searchEngine, sslWarningState, bl14);
    }

    public final List<BrowserTab> component1() {
        return this.tabs;
    }

    public final boolean component10() {
        return this.canGoBack;
    }

    public final boolean component11() {
        return this.canGoForward;
    }

    public final boolean component12() {
        return this.isMenuOpen;
    }

    public final boolean component13() {
        return this.isTabsDialogOpen;
    }

    public final boolean component14() {
        return this.isHistoryOpen;
    }

    public final boolean component15() {
        return this.isDownloadsOpen;
    }

    public final List<HistoryEntry> component16() {
        return this.browsingHistory;
    }

    public final List<DownloadEntry> component17() {
        return this.downloadHistory;
    }

    public final BlockedInfo component18() {
        return this.blockedInfo;
    }

    public final String component19() {
        return this.toastMessage;
    }

    public final String component2() {
        return this.currentTabId;
    }

    public final List<FavoriteSite> component20() {
        return this.favoriteSites;
    }

    public final Set<String> component21() {
        return this.customKeywords;
    }

    public final boolean component22() {
        return this.isPopupBlockingEnabled;
    }

    public final boolean component23() {
        return this.isAdBlockingEnabled;
    }

    public final DesktopArchitecture component24() {
        return this.desktopArchitecture;
    }

    public final boolean component25() {
        return this.isPageTranslated;
    }

    public final boolean component26() {
        return this.isTranslating;
    }

    public final AppTheme component27() {
        return this.appTheme;
    }

    public final TranslationEngine component28() {
        return this.selectedTranslationEngine;
    }

    public final SearchEngine component29() {
        return this.selectedSearchEngine;
    }

    public final boolean component3() {
        return this.isHomePage;
    }

    public final SslWarningState component30() {
        return this.sslWarningState;
    }

    public final boolean component31() {
        return this.isDiagnosticOpen;
    }

    public final String component4() {
        return this.currentUrl;
    }

    public final String component5() {
        return this.searchInput;
    }

    public final String component6() {
        return this.pageTitle;
    }

    public final boolean component7() {
        return this.isLoading;
    }

    public final int component8() {
        return this.loadingProgress;
    }

    public final boolean component9() {
        return this.isPageContentVisible;
    }

    public final BrowserUiState copy(List<BrowserTab> list, String string, boolean bl, String string2, String string3, String string4, boolean bl2, int n, boolean bl3, boolean bl4, boolean bl5, boolean bl6, boolean bl7, boolean bl8, boolean bl9, List<HistoryEntry> list2, List<DownloadEntry> list3, BlockedInfo blockedInfo, String string5, List<FavoriteSite> list4, Set<String> set, boolean bl10, boolean bl11, DesktopArchitecture desktopArchitecture, boolean bl12, boolean bl13, AppTheme appTheme, TranslationEngine translationEngine, SearchEngine searchEngine, SslWarningState sslWarningState, boolean bl14) {
        Intrinsics.checkNotNullParameter(list, (String)"tabs");
        Intrinsics.checkNotNullParameter((Object)string, (String)"currentTabId");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"currentUrl");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"searchInput");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"pageTitle");
        Intrinsics.checkNotNullParameter(list2, (String)"browsingHistory");
        Intrinsics.checkNotNullParameter(list3, (String)"downloadHistory");
        Intrinsics.checkNotNullParameter(list4, (String)"favoriteSites");
        Intrinsics.checkNotNullParameter(set, (String)"customKeywords");
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"desktopArchitecture");
        Intrinsics.checkNotNullParameter((Object)appTheme, (String)"appTheme");
        Intrinsics.checkNotNullParameter((Object)((Object)translationEngine), (String)"selectedTranslationEngine");
        Intrinsics.checkNotNullParameter((Object)((Object)searchEngine), (String)"selectedSearchEngine");
        return new BrowserUiState(list, string, bl, string2, string3, string4, bl2, n, bl3, bl4, bl5, bl6, bl7, bl8, bl9, list2, list3, blockedInfo, string5, list4, set, bl10, bl11, desktopArchitecture, bl12, bl13, appTheme, translationEngine, searchEngine, sslWarningState, bl14);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BrowserUiState)) {
            return false;
        }
        object = (BrowserUiState)object;
        if (!Intrinsics.areEqual(this.tabs, ((BrowserUiState)object).tabs)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.currentTabId, (Object)((BrowserUiState)object).currentTabId)) {
            return false;
        }
        if (this.isHomePage != ((BrowserUiState)object).isHomePage) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.currentUrl, (Object)((BrowserUiState)object).currentUrl)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.searchInput, (Object)((BrowserUiState)object).searchInput)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.pageTitle, (Object)((BrowserUiState)object).pageTitle)) {
            return false;
        }
        if (this.isLoading != ((BrowserUiState)object).isLoading) {
            return false;
        }
        if (this.loadingProgress != ((BrowserUiState)object).loadingProgress) {
            return false;
        }
        if (this.isPageContentVisible != ((BrowserUiState)object).isPageContentVisible) {
            return false;
        }
        if (this.canGoBack != ((BrowserUiState)object).canGoBack) {
            return false;
        }
        if (this.canGoForward != ((BrowserUiState)object).canGoForward) {
            return false;
        }
        if (this.isMenuOpen != ((BrowserUiState)object).isMenuOpen) {
            return false;
        }
        if (this.isTabsDialogOpen != ((BrowserUiState)object).isTabsDialogOpen) {
            return false;
        }
        if (this.isHistoryOpen != ((BrowserUiState)object).isHistoryOpen) {
            return false;
        }
        if (this.isDownloadsOpen != ((BrowserUiState)object).isDownloadsOpen) {
            return false;
        }
        if (!Intrinsics.areEqual(this.browsingHistory, ((BrowserUiState)object).browsingHistory)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.downloadHistory, ((BrowserUiState)object).downloadHistory)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.blockedInfo, (Object)((BrowserUiState)object).blockedInfo)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.toastMessage, (Object)((BrowserUiState)object).toastMessage)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.favoriteSites, ((BrowserUiState)object).favoriteSites)) {
            return false;
        }
        if (!Intrinsics.areEqual(this.customKeywords, ((BrowserUiState)object).customKeywords)) {
            return false;
        }
        if (this.isPopupBlockingEnabled != ((BrowserUiState)object).isPopupBlockingEnabled) {
            return false;
        }
        if (this.isAdBlockingEnabled != ((BrowserUiState)object).isAdBlockingEnabled) {
            return false;
        }
        if (this.desktopArchitecture != ((BrowserUiState)object).desktopArchitecture) {
            return false;
        }
        if (this.isPageTranslated != ((BrowserUiState)object).isPageTranslated) {
            return false;
        }
        if (this.isTranslating != ((BrowserUiState)object).isTranslating) {
            return false;
        }
        if (this.appTheme != ((BrowserUiState)object).appTheme) {
            return false;
        }
        if (this.selectedTranslationEngine != ((BrowserUiState)object).selectedTranslationEngine) {
            return false;
        }
        if (this.selectedSearchEngine != ((BrowserUiState)object).selectedSearchEngine) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.sslWarningState, (Object)((BrowserUiState)object).sslWarningState)) {
            return false;
        }
        return this.isDiagnosticOpen == ((BrowserUiState)object).isDiagnosticOpen;
    }

    public final AppTheme getAppTheme() {
        return this.appTheme;
    }

    public final BlockedInfo getBlockedInfo() {
        return this.blockedInfo;
    }

    public final List<HistoryEntry> getBrowsingHistory() {
        return this.browsingHistory;
    }

    public final boolean getCanGoBack() {
        return this.canGoBack;
    }

    public final boolean getCanGoForward() {
        return this.canGoForward;
    }

    public final String getCurrentTabId() {
        return this.currentTabId;
    }

    public final String getCurrentUrl() {
        return this.currentUrl;
    }

    public final Set<String> getCustomKeywords() {
        return this.customKeywords;
    }

    public final DesktopArchitecture getDesktopArchitecture() {
        return this.desktopArchitecture;
    }

    public final List<DownloadEntry> getDownloadHistory() {
        return this.downloadHistory;
    }

    public final List<FavoriteSite> getFavoriteSites() {
        return this.favoriteSites;
    }

    public final int getLoadingProgress() {
        return this.loadingProgress;
    }

    public final String getPageTitle() {
        return this.pageTitle;
    }

    public final String getSearchInput() {
        return this.searchInput;
    }

    public final SearchEngine getSelectedSearchEngine() {
        return this.selectedSearchEngine;
    }

    public final TranslationEngine getSelectedTranslationEngine() {
        return this.selectedTranslationEngine;
    }

    public final SslWarningState getSslWarningState() {
        return this.sslWarningState;
    }

    public final List<BrowserTab> getTabs() {
        return this.tabs;
    }

    public final String getToastMessage() {
        return this.toastMessage;
    }

    public int hashCode() {
        int n = ((Object)this.tabs).hashCode();
        int n2 = this.currentTabId.hashCode();
        int n3 = Boolean.hashCode(this.isHomePage);
        int n4 = this.currentUrl.hashCode();
        int n5 = this.searchInput.hashCode();
        int n6 = this.pageTitle.hashCode();
        int n7 = Boolean.hashCode(this.isLoading);
        int n8 = Integer.hashCode(this.loadingProgress);
        int n9 = Boolean.hashCode(this.isPageContentVisible);
        int n10 = Boolean.hashCode(this.canGoBack);
        int n11 = Boolean.hashCode(this.canGoForward);
        int n12 = Boolean.hashCode(this.isMenuOpen);
        int n13 = Boolean.hashCode(this.isTabsDialogOpen);
        int n14 = Boolean.hashCode(this.isHistoryOpen);
        int n15 = Boolean.hashCode(this.isDownloadsOpen);
        int n16 = ((Object)this.browsingHistory).hashCode();
        int n17 = ((Object)this.downloadHistory).hashCode();
        BlockedInfo blockedInfo = this.blockedInfo;
        int n18 = 0;
        int n19 = blockedInfo == null ? 0 : this.blockedInfo.hashCode();
        int n20 = this.toastMessage == null ? 0 : this.toastMessage.hashCode();
        int n21 = ((Object)this.favoriteSites).hashCode();
        int n22 = ((Object)this.customKeywords).hashCode();
        int n23 = Boolean.hashCode(this.isPopupBlockingEnabled);
        int n24 = Boolean.hashCode(this.isAdBlockingEnabled);
        int n25 = this.desktopArchitecture.hashCode();
        int n26 = Boolean.hashCode(this.isPageTranslated);
        int n27 = Boolean.hashCode(this.isTranslating);
        int n28 = this.appTheme.hashCode();
        int n29 = this.selectedTranslationEngine.hashCode();
        int n30 = this.selectedSearchEngine.hashCode();
        if (this.sslWarningState != null) {
            n18 = this.sslWarningState.hashCode();
        }
        return (((((((((((((((((((((((((((((n * 31 + n2) * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n7) * 31 + n8) * 31 + n9) * 31 + n10) * 31 + n11) * 31 + n12) * 31 + n13) * 31 + n14) * 31 + n15) * 31 + n16) * 31 + n17) * 31 + n19) * 31 + n20) * 31 + n21) * 31 + n22) * 31 + n23) * 31 + n24) * 31 + n25) * 31 + n26) * 31 + n27) * 31 + n28) * 31 + n29) * 31 + n30) * 31 + n18) * 31 + Boolean.hashCode(this.isDiagnosticOpen);
    }

    public final boolean isAdBlockingEnabled() {
        return this.isAdBlockingEnabled;
    }

    public final boolean isDesktopMode4Enabled() {
        boolean bl = this.desktopArchitecture == DesktopArchitecture.DESKTOP_MODE_4;
        return bl;
    }

    public final boolean isDesktopModeEnabled() {
        boolean bl = this.desktopArchitecture == DesktopArchitecture.STANDARD;
        return bl;
    }

    public final boolean isDiagnosticOpen() {
        return this.isDiagnosticOpen;
    }

    public final boolean isDownloadsOpen() {
        return this.isDownloadsOpen;
    }

    public final boolean isHistoryOpen() {
        return this.isHistoryOpen;
    }

    public final boolean isHomePage() {
        return this.isHomePage;
    }

    public final boolean isLoading() {
        return this.isLoading;
    }

    public final boolean isMenuOpen() {
        return this.isMenuOpen;
    }

    public final boolean isPageContentVisible() {
        return this.isPageContentVisible;
    }

    public final boolean isPageTranslated() {
        return this.isPageTranslated;
    }

    public final boolean isPopupBlockingEnabled() {
        return this.isPopupBlockingEnabled;
    }

    public final boolean isTabsDialogOpen() {
        return this.isTabsDialogOpen;
    }

    public final boolean isTranslating() {
        return this.isTranslating;
    }

    public final boolean isWindows10TouchEnabled() {
        boolean bl = this.desktopArchitecture == DesktopArchitecture.WINDOWS_10_TOUCH;
        return bl;
    }

    public String toString() {
        List<BrowserTab> list = this.tabs;
        String string = this.currentTabId;
        boolean bl = this.isHomePage;
        String string2 = this.currentUrl;
        String string3 = this.searchInput;
        String string4 = this.pageTitle;
        boolean bl2 = this.isLoading;
        int n = this.loadingProgress;
        boolean bl3 = this.isPageContentVisible;
        boolean bl4 = this.canGoBack;
        boolean bl5 = this.canGoForward;
        boolean bl6 = this.isMenuOpen;
        boolean bl7 = this.isTabsDialogOpen;
        boolean bl8 = this.isHistoryOpen;
        boolean bl9 = this.isDownloadsOpen;
        List<HistoryEntry> list2 = this.browsingHistory;
        List<DownloadEntry> list3 = this.downloadHistory;
        BlockedInfo blockedInfo = this.blockedInfo;
        String string5 = this.toastMessage;
        List<FavoriteSite> list4 = this.favoriteSites;
        Set<String> set = this.customKeywords;
        boolean bl10 = this.isPopupBlockingEnabled;
        boolean bl11 = this.isAdBlockingEnabled;
        DesktopArchitecture desktopArchitecture = this.desktopArchitecture;
        boolean bl12 = this.isPageTranslated;
        boolean bl13 = this.isTranslating;
        AppTheme appTheme = this.appTheme;
        TranslationEngine translationEngine = this.selectedTranslationEngine;
        SearchEngine searchEngine = this.selectedSearchEngine;
        SslWarningState sslWarningState = this.sslWarningState;
        boolean bl14 = this.isDiagnosticOpen;
        return "BrowserUiState(tabs=" + list + ", currentTabId=" + string + ", isHomePage=" + bl + ", currentUrl=" + string2 + ", searchInput=" + string3 + ", pageTitle=" + string4 + ", isLoading=" + bl2 + ", loadingProgress=" + n + ", isPageContentVisible=" + bl3 + ", canGoBack=" + bl4 + ", canGoForward=" + bl5 + ", isMenuOpen=" + bl6 + ", isTabsDialogOpen=" + bl7 + ", isHistoryOpen=" + bl8 + ", isDownloadsOpen=" + bl9 + ", browsingHistory=" + list2 + ", downloadHistory=" + list3 + ", blockedInfo=" + blockedInfo + ", toastMessage=" + string5 + ", favoriteSites=" + list4 + ", customKeywords=" + set + ", isPopupBlockingEnabled=" + bl10 + ", isAdBlockingEnabled=" + bl11 + ", desktopArchitecture=" + desktopArchitecture + ", isPageTranslated=" + bl12 + ", isTranslating=" + bl13 + ", appTheme=" + appTheme + ", selectedTranslationEngine=" + (Object)((Object)translationEngine) + ", selectedSearchEngine=" + (Object)((Object)searchEngine) + ", sslWarningState=" + sslWarningState + ", isDiagnosticOpen=" + bl14 + ")";
    }
}

