/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.content.SharedPreferences
 *  android.content.SharedPreferences$Editor
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  com.muslim.browser.pro.ui.theme.AppTheme
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 *  org.json.JSONArray
 *  org.json.JSONObject
 */
package com.muslim.browser.pro.browser;

import android.content.Context;
import android.content.SharedPreferences;
import com.muslim.browser.pro.browser.BrowserTab;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.DownloadEntry;
import com.muslim.browser.pro.browser.DownloadStatus;
import com.muslim.browser.pro.browser.FavoriteSite;
import com.muslim.browser.pro.browser.HistoryEntry;
import com.muslim.browser.pro.browser.SearchEngine;
import com.muslim.browser.pro.browser.SettingsRepository$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.browser.SettingsRepository$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.browser.TranslationEngine;
import com.muslim.browser.pro.ui.theme.AppTheme;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;
import org.json.JSONArray;
import org.json.JSONObject;

@Metadata(d1={"\u0000\u00a0\u0001\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\"\n\u0000\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\n\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u0000 r2\u00020\u0001:\u0001rB\u000f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\b\u0010\u001c\u001a\u00020\u001dH\u0002J\b\u0010\u001e\u001a\u00020\u001dH\u0002J\b\u0010\u001f\u001a\u00020\u001dH\u0002J\b\u0010 \u001a\u00020\u001dH\u0002J\b\u0010!\u001a\u00020\u001dH\u0002J\f\u0010\"\u001a\b\u0012\u0004\u0012\u00020\n0\u0017J\f\u0010#\u001a\b\u0012\u0004\u0012\u00020\n0\u0019J\u000e\u0010$\u001a\u00020%2\u0006\u0010&\u001a\u00020\nJ\u000e\u0010'\u001a\u00020%2\u0006\u0010&\u001a\u00020\nJ\u000e\u0010(\u001a\u00020%2\u0006\u0010&\u001a\u00020\nJ\u0006\u0010)\u001a\u00020\u001dJ\f\u0010*\u001a\b\u0012\u0004\u0012\u00020\u00110\u0019J\u0014\u0010+\u001a\u00020\u001d2\f\u0010,\u001a\b\u0012\u0004\u0012\u00020\u00110\u0019J\u0016\u0010-\u001a\u00020\u00112\u0006\u0010.\u001a\u00020\n2\u0006\u0010/\u001a\u00020\nJ\f\u0010U\u001a\b\u0012\u0004\u0012\u00020\u00130\u0019J\u0018\u0010V\u001a\u0004\u0018\u00010\u00132\u0006\u0010W\u001a\u00020\n2\u0006\u0010/\u001a\u00020\nJ\u000e\u0010X\u001a\u00020%2\u0006\u0010Y\u001a\u00020\nJ\u0006\u0010Z\u001a\u00020\u001dJ\b\u0010[\u001a\u00020\u001dH\u0002J\f\u0010\\\u001a\b\u0012\u0004\u0012\u00020\u00150\u0019J\u000e\u0010]\u001a\u00020\u00152\u0006\u0010^\u001a\u00020\u0015J@\u0010_\u001a\u00020%2\u0006\u0010`\u001a\u00020a2\u0006\u0010b\u001a\u00020c2\b\b\u0002\u0010d\u001a\u00020a2\b\b\u0002\u0010e\u001a\u00020a2\n\b\u0002\u0010f\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010g\u001a\u00020%J6\u0010h\u001a\u00020%2\u0006\u0010`\u001a\u00020a2\u0006\u0010b\u001a\u00020c2\n\b\u0002\u0010f\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010d\u001a\u00020a2\b\b\u0002\u0010e\u001a\u00020aJ\u000e\u0010i\u001a\u00020%2\u0006\u0010Y\u001a\u00020\nJ\u0006\u0010j\u001a\u00020\u001dJ\b\u0010k\u001a\u00020\u001dH\u0002J\u001c\u0010l\u001a\u00020\u001d2\f\u0010m\u001a\b\u0012\u0004\u0012\u00020n0\u00192\u0006\u0010o\u001a\u00020\nJ\u001a\u0010p\u001a\u0016\u0012\n\u0012\b\u0012\u0004\u0012\u00020n0\u0019\u0012\u0004\u0012\u00020\n\u0018\u00010qR\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\b\u001a\u0012\u0012\u0004\u0012\u00020\n0\tj\b\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\f\u001a\u0012\u0012\u0004\u0012\u00020\n0\tj\b\u0012\u0004\u0012\u00020\n`\u000bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\r\u001a\u0012\u0012\u0004\u0012\u00020\n0\u000ej\b\u0012\u0004\u0012\u00020\n`\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0010\u001a\u0012\u0012\u0004\u0012\u00020\u00110\u000ej\b\u0012\u0004\u0012\u00020\u0011`\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0012\u001a\u0012\u0012\u0004\u0012\u00020\u00130\u000ej\b\u0012\u0004\u0012\u00020\u0013`\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001e\u0010\u0014\u001a\u0012\u0012\u0004\u0012\u00020\u00150\u000ej\b\u0012\u0004\u0012\u00020\u0015`\u000fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\n0\u0017X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00110\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001a\u001a\b\u0012\u0004\u0012\u00020\u00130\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u0014\u0010\u001b\u001a\b\u0012\u0004\u0012\u00020\u00150\u0019X\u0082\u000e\u00a2\u0006\u0002\n\u0000R$\u00101\u001a\u00020%2\u0006\u00100\u001a\u00020%8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b1\u00102\"\u0004\b3\u00104R$\u00105\u001a\u00020%2\u0006\u00100\u001a\u00020%8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b5\u00102\"\u0004\b6\u00104R$\u00108\u001a\u0002072\u0006\u00100\u001a\u0002078F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b9\u0010:\"\u0004\b;\u0010<R$\u0010=\u001a\u00020%2\u0006\u00100\u001a\u00020%8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b=\u00102\"\u0004\b>\u00104R$\u0010?\u001a\u00020%2\u0006\u00100\u001a\u00020%8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\b?\u00102\"\u0004\b@\u00104R$\u0010A\u001a\u00020%2\u0006\u00100\u001a\u00020%8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\bA\u00102\"\u0004\bB\u00104R$\u0010D\u001a\u00020C2\u0006\u00100\u001a\u00020C8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\bE\u0010F\"\u0004\bG\u0010HR$\u0010J\u001a\u00020I2\u0006\u00100\u001a\u00020I8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\bK\u0010L\"\u0004\bM\u0010NR$\u0010P\u001a\u00020O2\u0006\u00100\u001a\u00020O8F@FX\u0086\u000e\u00a2\u0006\f\u001a\u0004\bQ\u0010R\"\u0004\bS\u0010T\u00a8\u0006s"}, d2={"Lcom/muslim/browser/pro/browser/SettingsRepository;", "", "context", "Landroid/content/Context;", "<init>", "(Landroid/content/Context;)V", "prefs", "Landroid/content/SharedPreferences;", "inMemoryUserKeywords", "Ljava/util/LinkedHashSet;", "", "Lkotlin/collections/LinkedHashSet;", "inMemoryKeywords", "inMemoryNormalizedKeywords", "Ljava/util/ArrayList;", "Lkotlin/collections/ArrayList;", "inMemoryFavorites", "Lcom/muslim/browser/pro/browser/FavoriteSite;", "inMemoryHistory", "Lcom/muslim/browser/pro/browser/HistoryEntry;", "inMemoryDownloads", "Lcom/muslim/browser/pro/browser/DownloadEntry;", "cachedKeywordsSet", "", "cachedFavoritesList", "", "cachedHistoryList", "cachedDownloadList", "rebuildEffectiveKeywords", "", "rebuildNormalizedKeywords", "loadFavoritesFromDisk", "loadHistoryFromDisk", "loadDownloadHistoryFromDisk", "getCustomKeywords", "getNormalizedKeywords", "isDefaultProtectedKeyword", "", "keyword", "addCustomKeyword", "removeCustomKeyword", "clearUserKeywords", "getFavoriteSites", "saveFavoriteSites", "sites", "addFavoriteSite", "name", "url", "value", "isPopupBlockingEnabled", "()Z", "setPopupBlockingEnabled", "(Z)V", "isAdBlockingEnabled", "setAdBlockingEnabled", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "desktopArchitecture", "getDesktopArchitecture", "()Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "setDesktopArchitecture", "(Lcom/muslim/browser/pro/browser/DesktopArchitecture;)V", "isDesktopModeEnabled", "setDesktopModeEnabled", "isWindows10TouchEnabled", "setWindows10TouchEnabled", "isDesktopMode4Enabled", "setDesktopMode4Enabled", "Lcom/muslim/browser/pro/ui/theme/AppTheme;", "appTheme", "getAppTheme", "()Lcom/muslim/browser/pro/ui/theme/AppTheme;", "setAppTheme", "(Lcom/muslim/browser/pro/ui/theme/AppTheme;)V", "Lcom/muslim/browser/pro/browser/TranslationEngine;", "selectedTranslationEngine", "getSelectedTranslationEngine", "()Lcom/muslim/browser/pro/browser/TranslationEngine;", "setSelectedTranslationEngine", "(Lcom/muslim/browser/pro/browser/TranslationEngine;)V", "Lcom/muslim/browser/pro/browser/SearchEngine;", "selectedSearchEngine", "getSelectedSearchEngine", "()Lcom/muslim/browser/pro/browser/SearchEngine;", "setSelectedSearchEngine", "(Lcom/muslim/browser/pro/browser/SearchEngine;)V", "getHistory", "addHistoryEntry", "title", "deleteHistoryEntry", "id", "clearHistory", "saveHistoryToDisk", "getDownloadHistory", "addDownloadEntry", "entry", "updateDownloadProgress", "downloadId", "", "status", "Lcom/muslim/browser/pro/browser/DownloadStatus;", "downloadedBytes", "totalBytes", "localUri", "persistToDisk", "updateDownloadStatus", "deleteDownloadEntry", "clearAllDownloadHistory", "saveDownloadHistoryToDisk", "saveTabs", "tabs", "Lcom/muslim/browser/pro/browser/BrowserTab;", "activeTabId", "getSavedTabs", "Lkotlin/Pair;", "Companion", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class SettingsRepository {
    public static final int $stable;
    public static final Companion Companion;
    private static final List<FavoriteSite> DEFAULT_FAVORITES;
    private static final List<String> DEFAULT_PROTECTED_KEYWORDS;
    private static final String KEY_ACTIVE_TAB_ID = "key_active_tab_id";
    private static final String KEY_AD_BLOCKING = "key_ad_blocking";
    private static final String KEY_APP_THEME = "key_app_theme";
    private static final String KEY_CUSTOM_KEYWORDS = "key_custom_keywords";
    private static final String KEY_DESKTOP_ARCHITECTURE = "key_desktop_architecture";
    private static final String KEY_DESKTOP_MODE = "key_desktop_mode";
    private static final String KEY_DOWNLOAD_HISTORY = "key_download_history";
    private static final String KEY_FAVORITES = "key_favorite_sites";
    private static final String KEY_HISTORY = "key_browsing_history";
    private static final String KEY_POPUP_BLOCKING = "key_popup_blocking";
    private static final String KEY_SAVED_TABS = "key_saved_tabs";
    private static final String KEY_SELECTED_SEARCH_ENGINE = "key_selected_search_engine";
    private static final String KEY_SELECTED_TRANSLATION_ENGINE = "key_selected_translation_engine";
    private static final String KEY_WINDOWS_10_TOUCH = "key_windows_10_touch";
    private static final String PREFS_NAME = "focus_shield_prefs";
    private List<DownloadEntry> cachedDownloadList;
    private List<FavoriteSite> cachedFavoritesList;
    private List<HistoryEntry> cachedHistoryList;
    private Set<String> cachedKeywordsSet;
    private final ArrayList<DownloadEntry> inMemoryDownloads;
    private final ArrayList<FavoriteSite> inMemoryFavorites;
    private final ArrayList<HistoryEntry> inMemoryHistory;
    private final LinkedHashSet<String> inMemoryKeywords;
    private final ArrayList<String> inMemoryNormalizedKeywords;
    private final LinkedHashSet<String> inMemoryUserKeywords;
    private final SharedPreferences prefs;

    static {
        Companion = new Companion(null);
        $stable = 8;
        DEFAULT_PROTECTED_KEYWORDS = CollectionsKt.listOf((Object[])new String[]{"Aashiq Banaya", "hot", "intimate", "adult", "kiss", "online", "video", "anonymous", "anonymity", "18+", "download", "downloaded", "downloading", "downl"});
        FavoriteSite favoriteSite = new FavoriteSite("fav_moldovalive", "MoldovaLive", "https://moldovalive.md", "ML", 4278221163L);
        FavoriteSite favoriteSite2 = new FavoriteSite("fav_moldova1", "Moldova1", "https://moldova1.md/i/en", "M1", 4279592384L);
        FavoriteSite favoriteSite3 = new FavoriteSite("fav_aistudio", "Google AI Studio", "https://aistudio.google.com", "AI", 4279923688L);
        FavoriteSite favoriteSite4 = new FavoriteSite("fav_github", "GitHub", "https://github.com/", "GH", 4280559918L);
        FavoriteSite favoriteSite5 = new FavoriteSite("fav_prothomalo", "Prothom Alo ePaper", "https://epaper.prothomalo.com/Home", "PA", 4292030255L);
        FavoriteSite favoriteSite6 = new FavoriteSite("fav_dailystar", "The Daily Star Bangla", "https://bangla.thedailystar.net", "DS", 4280825235L);
        FavoriteSite favoriteSite7 = new FavoriteSite("fav_ittefaq", "Ittefaq", "https://www.ittefaq.com.bd", "IT", 4293284096L);
        FavoriteSite favoriteSite8 = new FavoriteSite("fav_banginews", "BangiNews", "https://banginews.com", "BN", 4278223759L);
        DEFAULT_FAVORITES = CollectionsKt.listOf((Object[])new FavoriteSite[]{favoriteSite, favoriteSite2, favoriteSite3, favoriteSite4, favoriteSite5, favoriteSite6, favoriteSite7, favoriteSite8});
    }

    public SettingsRepository(Context object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"context");
        object = object.getSharedPreferences(PREFS_NAME, 0);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"getSharedPreferences(...)");
        this.prefs = object;
        this.inMemoryUserKeywords = new LinkedHashSet();
        this.inMemoryKeywords = new LinkedHashSet();
        this.inMemoryNormalizedKeywords = new ArrayList();
        this.inMemoryFavorites = new ArrayList();
        this.inMemoryHistory = new ArrayList();
        this.inMemoryDownloads = new ArrayList();
        this.cachedKeywordsSet = SetsKt.emptySet();
        this.cachedFavoritesList = CollectionsKt.emptyList();
        this.cachedHistoryList = CollectionsKt.emptyList();
        this.cachedDownloadList = CollectionsKt.emptyList();
        Set set = this.prefs.getStringSet(KEY_CUSTOM_KEYWORDS, SetsKt.emptySet());
        object = set;
        if (set == null) {
            object = SetsKt.emptySet();
        }
        this.inMemoryUserKeywords.addAll((Collection)object);
        this.rebuildEffectiveKeywords();
        this.loadFavoritesFromDisk();
        this.loadHistoryFromDisk();
        this.loadDownloadHistoryFromDisk();
    }

    static final boolean deleteDownloadEntry$lambda$6(String string, DownloadEntry downloadEntry) {
        Intrinsics.checkNotNullParameter((Object)downloadEntry, (String)"it");
        return Intrinsics.areEqual((Object)downloadEntry.getId(), (Object)string);
    }

    static final boolean deleteHistoryEntry$lambda$4(String string, HistoryEntry historyEntry) {
        Intrinsics.checkNotNullParameter((Object)historyEntry, (String)"it");
        return Intrinsics.areEqual((Object)historyEntry.getId(), (Object)string);
    }

    /*
     * Loose catch block
     */
    private final void loadDownloadHistoryFromDisk() {
        String string = "optString(...)";
        this.inMemoryDownloads.clear();
        String string2 = this.prefs.getString(KEY_DOWNLOAD_HISTORY, null);
        if (string2 == null) {
            this.cachedDownloadList = CollectionsKt.emptyList();
            return;
        }
        Object object = string2;
        object = string2;
        JSONArray jSONArray = new JSONArray(string2);
        object = string2;
        int n = jSONArray.length();
        for (int i = 0; i < n; ++i) {
            long l;
            long l2;
            long l3;
            String string3;
            String string4;
            String string5;
            long l4;
            String string6;
            ArrayList<DownloadEntry> arrayList;
            Object object2;
            Object object3;
            block24: {
                object = string2;
                object3 = jSONArray.getJSONObject(i);
                object = string2;
                object2 = object3.optString("status", "COMPLETED");
                try {
                    Intrinsics.checkNotNull((Object)object2);
                    object2 = DownloadStatus.valueOf(object2);
                }
                catch (Exception exception) {
                    object = string2;
                    object2 = DownloadStatus.COMPLETED;
                }
                object = string2;
                arrayList = this.inMemoryDownloads;
                object = string2;
                string6 = object3.optString("id", UUID.randomUUID().toString());
                object = string2;
                Intrinsics.checkNotNullExpressionValue((Object)string6, (String)string);
                object = string2;
                l4 = object3.optLong("downloadId", -1L);
                object = string2;
                string5 = object3.optString("fileName", "download");
                object = string2;
                Intrinsics.checkNotNullExpressionValue((Object)string5, (String)string);
                object = string2;
                string4 = object3.optString("url", "");
                object = string2;
                Intrinsics.checkNotNullExpressionValue((Object)string4, (String)string);
                object = string2;
                string3 = object3.optString("mimeType", "");
                object = string2;
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)string);
                try {
                    l3 = object3.optLong("timestamp", System.currentTimeMillis());
                    l2 = object3.optLong("downloadedBytes", 0L);
                    l = object3.optLong("totalBytes", -1L);
                    object = object3.optString("localUri", "");
                    boolean bl = object.length() == 0;
                    if (!bl) break block24;
                    object = null;
                }
                catch (Exception exception) {
                    break;
                }
            }
            object3 = (String)object;
            object = new DownloadEntry(string6, l4, string5, string4, string3, l3, (DownloadStatus)((Object)object2), l2, l, (String)object3);
            arrayList.add((DownloadEntry)object);
            continue;
            {
                catch (Exception exception) {
                    // empty catch block
                    break;
                }
            }
        }
        this.cachedDownloadList = CollectionsKt.toList((Iterable)this.inMemoryDownloads);
    }

    /*
     * Exception decompiling
     */
    private final void loadFavoritesFromDisk() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [16[TRYBLOCK]], but top level block is 24[WHILELOOP]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    private final void loadHistoryFromDisk() {
        this.inMemoryHistory.clear();
        String string = this.prefs.getString(KEY_HISTORY, null);
        if (string == null) {
            this.cachedHistoryList = CollectionsKt.emptyList();
            return;
        }
        JSONArray jSONArray = new JSONArray(string);
        int n = jSONArray.length();
        for (int i = 0; i < n; ++i) {
            try {
                JSONObject jSONObject = jSONArray.getJSONObject(i);
                ArrayList<HistoryEntry> arrayList = this.inMemoryHistory;
                String string2 = jSONObject.optString("id", UUID.randomUUID().toString());
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"optString(...)");
                string = jSONObject.optString("title", "");
                Intrinsics.checkNotNullExpressionValue((Object)string, (String)"optString(...)");
                String string3 = jSONObject.getString("url");
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"getString(...)");
                HistoryEntry historyEntry = new HistoryEntry(string2, string, string3, jSONObject.optLong("timestamp", System.currentTimeMillis()));
                arrayList.add(historyEntry);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
                break;
            }
        }
        this.cachedHistoryList = CollectionsKt.toList((Iterable)this.inMemoryHistory);
    }

    private final void rebuildEffectiveKeywords() {
        this.inMemoryKeywords.clear();
        this.inMemoryKeywords.addAll((Collection<String>)DEFAULT_PROTECTED_KEYWORDS);
        this.inMemoryKeywords.addAll((Collection<String>)this.inMemoryUserKeywords);
        this.cachedKeywordsSet = CollectionsKt.toSet((Iterable)this.inMemoryKeywords);
        this.rebuildNormalizedKeywords();
    }

    private final void rebuildNormalizedKeywords() {
        this.inMemoryNormalizedKeywords.clear();
        Iterator iterator = this.inMemoryKeywords.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        while (iterator.hasNext()) {
            Object object = iterator.next();
            Intrinsics.checkNotNullExpressionValue(object, (String)"next(...)");
            object = ((Object)StringsKt.trim((CharSequence)((String)object))).toString();
            Locale locale = Locale.ROOT;
            Intrinsics.checkNotNullExpressionValue((Object)locale, (String)"ROOT");
            object = ((String)object).toLowerCase(locale);
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toLowerCase(...)");
            boolean bl = ((CharSequence)object).length() > 0;
            if (!bl || this.inMemoryNormalizedKeywords.contains(object)) continue;
            this.inMemoryNormalizedKeywords.add((String)object);
        }
    }

    private final void saveDownloadHistoryToDisk() {
        this.cachedDownloadList = CollectionsKt.toList((Iterable)this.inMemoryDownloads);
        JSONArray jSONArray = new JSONArray();
        Iterator<DownloadEntry> iterator = this.inMemoryDownloads.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        while (iterator.hasNext()) {
            DownloadEntry downloadEntry = iterator.next();
            Intrinsics.checkNotNullExpressionValue((Object)downloadEntry, (String)"next(...)");
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", (Object)downloadEntry.getId());
            jSONObject.put("downloadId", downloadEntry.getDownloadId());
            jSONObject.put("fileName", (Object)downloadEntry.getFileName());
            jSONObject.put("url", (Object)downloadEntry.getUrl());
            jSONObject.put("mimeType", (Object)downloadEntry.getMimeType());
            jSONObject.put("timestamp", downloadEntry.getTimestamp());
            jSONObject.put("status", (Object)downloadEntry.getStatus().name());
            jSONObject.put("downloadedBytes", downloadEntry.getDownloadedBytes());
            jSONObject.put("totalBytes", downloadEntry.getTotalBytes());
            if (downloadEntry.getLocalUri() != null) {
                jSONObject.put("localUri", (Object)downloadEntry.getLocalUri());
            }
            jSONArray.put((Object)jSONObject);
        }
        this.prefs.edit().putString(KEY_DOWNLOAD_HISTORY, jSONArray.toString()).apply();
    }

    private final void saveHistoryToDisk() {
        this.cachedHistoryList = CollectionsKt.toList((Iterable)this.inMemoryHistory);
        JSONArray jSONArray = new JSONArray();
        Iterator<HistoryEntry> iterator = this.inMemoryHistory.iterator();
        Intrinsics.checkNotNullExpressionValue(iterator, (String)"iterator(...)");
        while (iterator.hasNext()) {
            HistoryEntry historyEntry = iterator.next();
            Intrinsics.checkNotNullExpressionValue((Object)historyEntry, (String)"next(...)");
            HistoryEntry historyEntry2 = historyEntry;
            historyEntry = new JSONObject();
            historyEntry.put("id", historyEntry2.getId());
            historyEntry.put("title", historyEntry2.getTitle());
            historyEntry.put("url", historyEntry2.getUrl());
            historyEntry.put("timestamp", historyEntry2.getTimestamp());
            jSONArray.put((Object)historyEntry);
        }
        this.prefs.edit().putString(KEY_HISTORY, jSONArray.toString()).apply();
    }

    public static /* synthetic */ boolean updateDownloadProgress$default(SettingsRepository settingsRepository, long l, DownloadStatus downloadStatus, long l2, long l3, String string, boolean bl, int n, Object object) {
        if ((n & 4) != 0) {
            l2 = -1L;
        }
        if ((n & 8) != 0) {
            l3 = -1L;
        }
        if ((n & 0x10) != 0) {
            string = null;
        }
        if ((n & 0x20) != 0) {
            bl = false;
        }
        return settingsRepository.updateDownloadProgress(l, downloadStatus, l2, l3, string, bl);
    }

    public static /* synthetic */ boolean updateDownloadStatus$default(SettingsRepository settingsRepository, long l, DownloadStatus downloadStatus, String string, long l2, long l3, int n, Object object) {
        block2: {
            if ((n & 4) != 0) {
                string = null;
            }
            if ((n & 8) != 0) {
                l2 = -1L;
            }
            if ((n & 0x10) == 0) break block2;
            l3 = -1L;
        }
        return settingsRepository.updateDownloadStatus(l, downloadStatus, string, l2, l3);
    }

    public final boolean addCustomKeyword(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"keyword");
        string = ((Object)StringsKt.trim((CharSequence)string)).toString();
        boolean bl = ((CharSequence)string).length() == 0;
        if (bl) {
            return false;
        }
        Object object = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"ROOT");
        object = string.toLowerCase((Locale)object);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toLowerCase(...)");
        if (this.inMemoryNormalizedKeywords.contains(object)) {
            return false;
        }
        this.inMemoryUserKeywords.add(string);
        this.inMemoryKeywords.add(string);
        this.inMemoryNormalizedKeywords.add((String)object);
        this.cachedKeywordsSet = CollectionsKt.toSet((Iterable)this.inMemoryKeywords);
        this.prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, (Set)this.inMemoryUserKeywords).apply();
        return true;
    }

    public final DownloadEntry addDownloadEntry(DownloadEntry downloadEntry) {
        Intrinsics.checkNotNullParameter((Object)downloadEntry, (String)"entry");
        this.inMemoryDownloads.add(0, downloadEntry);
        if (this.inMemoryDownloads.size() > 500) {
            this.inMemoryDownloads.remove(this.inMemoryDownloads.size() - 1);
        }
        this.saveDownloadHistoryToDisk();
        return downloadEntry;
    }

    public final FavoriteSite addFavoriteSite(String object, String object2) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"name");
        Intrinsics.checkNotNullParameter((Object)object2, (String)"url");
        String string = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
        object = ((Object)StringsKt.trim((CharSequence)((CharSequence)object2))).toString();
        if (!StringsKt.startsWith((String)object, (String)"http://", (boolean)true) && !StringsKt.startsWith((String)object, (String)"https://", (boolean)true)) {
            object = "https://" + (String)object;
        }
        if (!StringsKt.isBlank((CharSequence)string)) {
            object2 = StringsKt.take((String)string, (int)2).toUpperCase(Locale.ROOT);
            Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"toUpperCase(...)");
        } else {
            object2 = "W";
        }
        Object object3 = CollectionsKt.listOf((Object[])new Long[]{4282549748L, 4278225275L, 4284364209L, 4278234305L, 4292352864L, 4281944491L, 4294675456L, 4282622023L, 0xFF333333L});
        long l = ((Number)object3.get(Math.abs(string.hashCode() % object3.size()))).longValue();
        object3 = UUID.randomUUID().toString();
        Intrinsics.checkNotNullExpressionValue((Object)object3, (String)"toString(...)");
        object2 = new FavoriteSite((String)object3, string, (String)object, (String)object2, l);
        object = CollectionsKt.toMutableList((Collection)this.inMemoryFavorites);
        object.add(object2);
        this.saveFavoriteSites((List<FavoriteSite>)object);
        return object2;
    }

    public final HistoryEntry addHistoryEntry(String object, String string) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"title");
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        string = ((Object)StringsKt.trim((CharSequence)string)).toString();
        if (!(StringsKt.isBlank((CharSequence)string) || Intrinsics.areEqual((Object)string, (Object)"about:blank") || StringsKt.startsWith$default((String)string, (String)"about:", (boolean)false, (int)2, null))) {
            object = !StringsKt.isBlank((CharSequence)((CharSequence)object)) ? ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString() : string;
            HistoryEntry historyEntry = (HistoryEntry)CollectionsKt.firstOrNull((List)this.inMemoryHistory);
            if (historyEntry != null && Intrinsics.areEqual((Object)historyEntry.getUrl(), (Object)string)) {
                if (Intrinsics.areEqual((Object)object, (Object)string)) {
                    object = historyEntry.getTitle();
                }
                object = HistoryEntry.copy$default(historyEntry, null, (String)object, null, System.currentTimeMillis(), 5, null);
                this.inMemoryHistory.set(0, (HistoryEntry)object);
                this.saveHistoryToDisk();
                return object;
            }
            object = new HistoryEntry(null, (String)object, string, System.currentTimeMillis(), 1, null);
            this.inMemoryHistory.add(0, (HistoryEntry)object);
            if (this.inMemoryHistory.size() > 500) {
                this.inMemoryHistory.remove(this.inMemoryHistory.size() - 1);
            }
            this.saveHistoryToDisk();
            return object;
        }
        return null;
    }

    public final void clearAllDownloadHistory() {
        this.inMemoryDownloads.clear();
        this.cachedDownloadList = CollectionsKt.emptyList();
        this.prefs.edit().remove(KEY_DOWNLOAD_HISTORY).apply();
    }

    public final void clearHistory() {
        this.inMemoryHistory.clear();
        this.cachedHistoryList = CollectionsKt.emptyList();
        this.prefs.edit().remove(KEY_HISTORY).apply();
    }

    public final void clearUserKeywords() {
        this.inMemoryUserKeywords.clear();
        this.rebuildEffectiveKeywords();
        this.prefs.edit().remove(KEY_CUSTOM_KEYWORDS).apply();
    }

    public final boolean deleteDownloadEntry(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        boolean bl = CollectionsKt.removeAll((List)this.inMemoryDownloads, (Function1)new SettingsRepository$$ExternalSyntheticLambda1(string));
        if (bl) {
            this.saveDownloadHistoryToDisk();
        }
        return bl;
    }

    public final boolean deleteHistoryEntry(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        boolean bl = CollectionsKt.removeAll((List)this.inMemoryHistory, (Function1)new SettingsRepository$$ExternalSyntheticLambda0(string));
        if (bl) {
            this.saveHistoryToDisk();
        }
        return bl;
    }

    public final AppTheme getAppTheme() {
        String string = this.prefs.getString(KEY_APP_THEME, null);
        if (string == null) {
            return AppTheme.BLACK_WHITE;
        }
        try {
            string = AppTheme.valueOf((String)string);
        }
        catch (Exception exception) {
            this.prefs.edit().putString(KEY_APP_THEME, "BLACK_WHITE").commit();
            string = AppTheme.BLACK_WHITE;
        }
        return string;
    }

    public final Set<String> getCustomKeywords() {
        return this.cachedKeywordsSet;
    }

    public final DesktopArchitecture getDesktopArchitecture() {
        String string = this.prefs.getString(KEY_DESKTOP_ARCHITECTURE, null);
        if (string != null) {
            try {
                string = DesktopArchitecture.valueOf((String)string);
            }
            catch (Exception exception) {
                string = DesktopArchitecture.NONE;
            }
            return string;
        }
        boolean bl = this.prefs.getBoolean(KEY_WINDOWS_10_TOUCH, false);
        boolean bl2 = this.prefs.getBoolean(KEY_DESKTOP_MODE, false);
        string = bl ? DesktopArchitecture.WINDOWS_10_TOUCH : (bl2 ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE);
        this.prefs.edit().putString(KEY_DESKTOP_ARCHITECTURE, string.name()).apply();
        return string;
    }

    public final List<DownloadEntry> getDownloadHistory() {
        return this.cachedDownloadList;
    }

    public final List<FavoriteSite> getFavoriteSites() {
        return this.cachedFavoritesList;
    }

    public final List<HistoryEntry> getHistory() {
        return this.cachedHistoryList;
    }

    public final List<String> getNormalizedKeywords() {
        return this.inMemoryNormalizedKeywords;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final Pair<List<BrowserTab>, String> getSavedTabs() {
        int n;
        List list;
        String string;
        Object object;
        block20: {
            int n2;
            Object var6_3;
            block19: {
                block18: {
                    JSONArray jSONArray;
                    block16: {
                        object = this.prefs;
                        var6_3 = null;
                        if ((object = object.getString(KEY_SAVED_TABS, null)) == null) {
                            return null;
                        }
                        string = this.prefs.getString(KEY_ACTIVE_TAB_ID, null);
                        jSONArray = new JSONArray((String)object);
                        if (jSONArray.length() != 0) break block16;
                        return null;
                    }
                    object = new ArrayList();
                    list = (List)object;
                    n = 0;
                    int n3 = jSONArray.length();
                    while (true) {
                        String string2;
                        String string3;
                        JSONObject jSONObject;
                        boolean bl;
                        block17: {
                            n2 = 1;
                            bl = true;
                            if (n >= n3) break;
                            jSONObject = jSONArray.getJSONObject(n);
                            string3 = jSONObject.getString("id");
                            string2 = jSONObject.optString("url", "");
                            Intrinsics.checkNotNull((Object)string2);
                            n2 = ((CharSequence)string2).length() > 0 ? 1 : 0;
                            object = n2 != 0 ? string2 : "Home";
                            object = jSONObject.optString("pageTitle", (String)object);
                            if (((CharSequence)string2).length() == 0) break block17;
                            bl = false;
                        }
                        bl = jSONObject.optBoolean("isHomePage", bl);
                        String string4 = jSONObject.optString("searchInput", string2);
                        Intrinsics.checkNotNull((Object)string3);
                        Intrinsics.checkNotNull((Object)object);
                        Intrinsics.checkNotNull((Object)string4);
                        BrowserTab browserTab = new BrowserTab(string3, string2, string4, (String)object, bl, false, false, null, false, false, 0, false, null, 8160, null);
                        list.add(browserTab);
                        ++n;
                        continue;
                        break;
                    }
                    if (!list.isEmpty()) break block18;
                    return null;
                }
                object = list;
                if (!(object instanceof Collection) || !((Collection)object).isEmpty()) break block19;
                n = 0;
                break block20;
            }
            try {
                object = object.iterator();
                while (object.hasNext()) {
                    if (!Intrinsics.areEqual((Object)((BrowserTab)object.next()).getId(), (Object)string)) continue;
                    n = n2;
                    break block20;
                }
                n = 0;
            }
            catch (Exception exception) {
                return var6_3;
            }
        }
        if (n != 0) {
            Intrinsics.checkNotNull((Object)string);
            object = string;
            return new Pair((Object)list, object);
        }
        object = ((BrowserTab)CollectionsKt.first((List)list)).getId();
        return new Pair((Object)list, object);
    }

    public final SearchEngine getSelectedSearchEngine() {
        Object object = this.prefs;
        Object object2 = "DUCKDUCKGO";
        if ((object = object.getString(KEY_SELECTED_SEARCH_ENGINE, "DUCKDUCKGO")) != null) {
            object2 = object;
        }
        try {
            object2 = SearchEngine.valueOf(object2);
        }
        catch (Exception exception) {
            object2 = SearchEngine.DUCKDUCKGO;
        }
        return object2;
    }

    public final TranslationEngine getSelectedTranslationEngine() {
        String string;
        Object object = string = this.prefs.getString(KEY_SELECTED_TRANSLATION_ENGINE, "LIBRE_TRANSLATE");
        if (string == null) {
            object = "LIBRE_TRANSLATE";
        }
        try {
            object = TranslationEngine.valueOf(object);
        }
        catch (Exception exception) {
            this.prefs.edit().putString(KEY_SELECTED_TRANSLATION_ENGINE, "LIBRE_TRANSLATE").commit();
            object = TranslationEngine.LIBRE_TRANSLATE;
        }
        return object;
    }

    public final boolean isAdBlockingEnabled() {
        return this.prefs.getBoolean(KEY_AD_BLOCKING, true);
    }

    public final boolean isDefaultProtectedKeyword(String object) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"keyword");
        Iterator iterator = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
        object = Locale.ROOT;
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"ROOT");
        object = ((String)((Object)iterator)).toLowerCase((Locale)object);
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toLowerCase(...)");
        iterator = DEFAULT_PROTECTED_KEYWORDS;
        boolean bl = iterator instanceof Collection;
        boolean bl2 = false;
        if (!bl || !((Collection)((Object)iterator)).isEmpty()) {
            iterator = iterator.iterator();
            while (iterator.hasNext()) {
                String string = ((Object)StringsKt.trim((CharSequence)((String)iterator.next()))).toString();
                Object object2 = Locale.ROOT;
                Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"ROOT");
                object2 = string.toLowerCase((Locale)object2);
                Intrinsics.checkNotNullExpressionValue((Object)object2, (String)"toLowerCase(...)");
                if (!Intrinsics.areEqual((Object)object2, (Object)object)) continue;
                bl2 = true;
                break;
            }
        }
        return bl2;
    }

    public final boolean isDesktopMode4Enabled() {
        boolean bl = this.getDesktopArchitecture() == DesktopArchitecture.DESKTOP_MODE_4;
        return bl;
    }

    public final boolean isDesktopModeEnabled() {
        boolean bl = this.getDesktopArchitecture() == DesktopArchitecture.STANDARD;
        return bl;
    }

    public final boolean isPopupBlockingEnabled() {
        return this.prefs.getBoolean(KEY_POPUP_BLOCKING, true);
    }

    public final boolean isWindows10TouchEnabled() {
        boolean bl = this.getDesktopArchitecture() == DesktopArchitecture.WINDOWS_10_TOUCH;
        return bl;
    }

    public final boolean removeCustomKeyword(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"keyword");
        if (this.isDefaultProtectedKeyword(string)) {
            return false;
        }
        boolean bl = this.inMemoryUserKeywords.remove(string);
        if (bl) {
            this.rebuildEffectiveKeywords();
            this.prefs.edit().putStringSet(KEY_CUSTOM_KEYWORDS, (Set)this.inMemoryUserKeywords).apply();
        }
        return bl;
    }

    public final void saveFavoriteSites(List<FavoriteSite> object) {
        Intrinsics.checkNotNullParameter(object, (String)"sites");
        this.inMemoryFavorites.clear();
        this.inMemoryFavorites.addAll((Collection)object);
        this.cachedFavoritesList = CollectionsKt.toList((Iterable)this.inMemoryFavorites);
        JSONArray jSONArray = new JSONArray();
        object = object.iterator();
        while (object.hasNext()) {
            FavoriteSite favoriteSite = (FavoriteSite)object.next();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", (Object)favoriteSite.getId());
            jSONObject.put("name", (Object)favoriteSite.getName());
            jSONObject.put("url", (Object)favoriteSite.getUrl());
            jSONObject.put("iconLetter", (Object)favoriteSite.getIconLetter());
            jSONObject.put("badgeColor", favoriteSite.getBadgeColor());
            jSONArray.put((Object)jSONObject);
        }
        this.prefs.edit().putString(KEY_FAVORITES, jSONArray.toString()).apply();
    }

    public final void saveTabs(List<BrowserTab> object, String string) {
        Intrinsics.checkNotNullParameter(object, (String)"tabs");
        Intrinsics.checkNotNullParameter((Object)string, (String)"activeTabId");
        JSONArray jSONArray = new JSONArray();
        object = object.iterator();
        while (object.hasNext()) {
            BrowserTab browserTab = (BrowserTab)object.next();
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("id", (Object)browserTab.getId());
            jSONObject.put("url", (Object)browserTab.getUrl());
            jSONObject.put("pageTitle", (Object)browserTab.getPageTitle());
            jSONObject.put("isHomePage", browserTab.isHomePage());
            jSONObject.put("searchInput", (Object)browserTab.getSearchInput());
            jSONArray.put((Object)jSONObject);
        }
        this.prefs.edit().putString(KEY_SAVED_TABS, jSONArray.toString()).putString(KEY_ACTIVE_TAB_ID, string).apply();
    }

    public final void setAdBlockingEnabled(boolean bl) {
        this.prefs.edit().putBoolean(KEY_AD_BLOCKING, bl).apply();
    }

    public final void setAppTheme(AppTheme appTheme) {
        Intrinsics.checkNotNullParameter((Object)appTheme, (String)"value");
        this.prefs.edit().putString(KEY_APP_THEME, appTheme.name()).apply();
    }

    public final void setDesktopArchitecture(DesktopArchitecture desktopArchitecture) {
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"value");
        SharedPreferences.Editor editor = this.prefs.edit().putString(KEY_DESKTOP_ARCHITECTURE, desktopArchitecture.name());
        DesktopArchitecture desktopArchitecture2 = DesktopArchitecture.STANDARD;
        boolean bl = true;
        boolean bl2 = desktopArchitecture == desktopArchitecture2;
        editor = editor.putBoolean(KEY_DESKTOP_MODE, bl2);
        bl2 = desktopArchitecture == DesktopArchitecture.WINDOWS_10_TOUCH ? bl : false;
        editor.putBoolean(KEY_WINDOWS_10_TOUCH, bl2).apply();
    }

    public final void setDesktopMode4Enabled(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.DESKTOP_MODE_4 : DesktopArchitecture.NONE;
        this.setDesktopArchitecture(desktopArchitecture);
    }

    public final void setDesktopModeEnabled(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.STANDARD : DesktopArchitecture.NONE;
        this.setDesktopArchitecture(desktopArchitecture);
    }

    public final void setPopupBlockingEnabled(boolean bl) {
        this.prefs.edit().putBoolean(KEY_POPUP_BLOCKING, bl).apply();
    }

    public final void setSelectedSearchEngine(SearchEngine searchEngine) {
        Intrinsics.checkNotNullParameter((Object)((Object)searchEngine), (String)"value");
        this.prefs.edit().putString(KEY_SELECTED_SEARCH_ENGINE, searchEngine.name()).apply();
    }

    public final void setSelectedTranslationEngine(TranslationEngine translationEngine) {
        Intrinsics.checkNotNullParameter((Object)((Object)translationEngine), (String)"value");
        this.prefs.edit().putString(KEY_SELECTED_TRANSLATION_ENGINE, translationEngine.name()).apply();
    }

    public final void setWindows10TouchEnabled(boolean bl) {
        DesktopArchitecture desktopArchitecture = bl ? DesktopArchitecture.WINDOWS_10_TOUCH : DesktopArchitecture.NONE;
        this.setDesktopArchitecture(desktopArchitecture);
    }

    public final boolean updateDownloadProgress(long l, DownloadStatus downloadStatus, long l2, long l3, String string, boolean bl) {
        boolean bl2;
        block5: {
            Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
            if (l == -1L) {
                return false;
            }
            bl2 = false;
            int n = ((Collection)this.inMemoryDownloads).size();
            for (int i = 0; i < n; ++i) {
                DownloadEntry downloadEntry = this.inMemoryDownloads.get(i);
                Intrinsics.checkNotNullExpressionValue((Object)downloadEntry, (String)"get(...)");
                if (downloadEntry.getDownloadId() != l) continue;
                ArrayList<DownloadEntry> arrayList = this.inMemoryDownloads;
                if (string == null) {
                    string = downloadEntry.getLocalUri();
                }
                l = l2 >= 0L ? l2 : downloadEntry.getDownloadedBytes();
                if (l3 <= 0L) {
                    l3 = downloadEntry.getTotalBytes();
                }
                arrayList.set(i, DownloadEntry.copy$default(downloadEntry, null, 0L, null, null, null, 0L, downloadStatus, l, l3, string, 63, null));
                bl2 = true;
                break;
            }
            if (!bl2) break block5;
            this.cachedDownloadList = CollectionsKt.toList((Iterable)this.inMemoryDownloads);
            if (bl) {
                this.saveDownloadHistoryToDisk();
            }
        }
        return bl2;
    }

    public final boolean updateDownloadStatus(long l, DownloadStatus downloadStatus, String string, long l2, long l3) {
        Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
        return this.updateDownloadProgress(l, downloadStatus, l2, l3, string, true);
    }

    @Metadata(d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u000f\n\u0002\u0010 \n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u0017\u0010\u0014\u001a\b\u0012\u0004\u0012\u00020\u00050\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0017\u0010\u0018\u001a\b\u0012\u0004\u0012\u00020\u00190\u0015\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0017\u00a8\u0006\u001b"}, d2={"Lcom/muslim/browser/pro/browser/SettingsRepository$Companion;", "", "<init>", "()V", "PREFS_NAME", "", "KEY_CUSTOM_KEYWORDS", "KEY_POPUP_BLOCKING", "KEY_AD_BLOCKING", "KEY_DESKTOP_MODE", "KEY_WINDOWS_10_TOUCH", "KEY_DESKTOP_ARCHITECTURE", "KEY_APP_THEME", "KEY_SELECTED_TRANSLATION_ENGINE", "KEY_SELECTED_SEARCH_ENGINE", "KEY_FAVORITES", "KEY_SAVED_TABS", "KEY_ACTIVE_TAB_ID", "KEY_HISTORY", "KEY_DOWNLOAD_HISTORY", "DEFAULT_PROTECTED_KEYWORDS", "", "getDEFAULT_PROTECTED_KEYWORDS", "()Ljava/util/List;", "DEFAULT_FAVORITES", "Lcom/muslim/browser/pro/browser/FavoriteSite;", "getDEFAULT_FAVORITES", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }

        public final List<FavoriteSite> getDEFAULT_FAVORITES() {
            return DEFAULT_FAVORITES;
        }

        public final List<String> getDEFAULT_PROTECTED_KEYWORDS() {
            return DEFAULT_PROTECTED_KEYWORDS;
        }
    }
}

