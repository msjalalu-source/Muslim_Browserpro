/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.os.Bundle
 *  android.util.Log
 *  android.view.View
 *  android.view.ViewGroup
 *  android.view.ViewParent
 *  android.webkit.WebView
 *  com.muslim.browser.pro.browser.DesktopArchitecture
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.functions.Function0
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 */
package com.muslim.browser.pro.browser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import com.muslim.browser.pro.browser.DesktopArchitecture;
import com.muslim.browser.pro.browser.TabWebViewManager$$ExternalSyntheticLambda0;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(d1={"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0002\u0010\u000e\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\b\n\u0002\u0010%\n\u0000\n\u0002\u0010$\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0016\n\u0002\u0010\"\n\u0002\b\u0004\b\u0007\u0018\u0000 A2\u00020\u0001:\u0001AB\u00f4\u0001\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012!\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007\u00128\b\u0002\u0010\r\u001a2\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000e\u0012\u001c\b\u0002\u0010\u0012\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000e\u0012\u001c\b\u0002\u0010\u0014\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000e\u0012\u0010\b\u0002\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\u0016\u0012\u001c\b\u0002\u0010\u0017\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000e\u0012\u0010\b\u0002\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0018\u0018\u00010\u0016\u00a2\u0006\u0004\b\u001a\u0010\u001bJ\u000e\u0010$\u001a\u00020\u00132\u0006\u0010\u000b\u001a\u00020\bJ\u0010\u0010%\u001a\u0004\u0018\u00010\f2\u0006\u0010\u000b\u001a\u00020\bJD\u0010&\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00130'2\u0006\u0010\u000b\u001a\u00020\b2\n\b\u0002\u0010(\u001a\u0004\u0018\u00010\b2\n\b\u0002\u0010\u0010\u001a\u0004\u0018\u00010\u000f2\u0010\b\u0002\u0010)\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0016J\u000e\u0010*\u001a\u00020\u00112\u0006\u0010+\u001a\u00020\u0013J\u000e\u0010,\u001a\u00020\u00112\u0006\u0010-\u001a\u00020\u0013J\u000e\u0010.\u001a\u00020\u00112\u0006\u0010/\u001a\u00020\u0018J\u000e\u0010$\u001a\u00020\u00132\u0006\u00100\u001a\u00020\fJ\u001a\u00101\u001a\u00020\u00112\u0012\u00102\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00110\u0007J\u000e\u00103\u001a\u00020\u00112\u0006\u00104\u001a\u00020\bJ\u000e\u00105\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\bJ\u0012\u00106\u001a\u00020\u00112\n\b\u0002\u00107\u001a\u0004\u0018\u00010\bJ\u000e\u00108\u001a\u00020\u00112\u0006\u0010\u000b\u001a\u00020\bJ\u0006\u00109\u001a\u00020\u0011J\u0006\u0010:\u001a\u00020\u0011R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001dR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR)\u0010\u0006\u001a\u001d\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0004\u0012\u00020\f0\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R>\u0010\r\u001a2\u0012\u0013\u0012\u00110\b\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u000b\u0012\u0013\u0012\u00110\u000f\u00a2\u0006\f\b\t\u0012\b\b\n\u0012\u0004\b\b(\u0010\u0012\u0004\u0012\u00020\u00110\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0012\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0014\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0013\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0015\u001a\n\u0012\u0004\u0012\u00020\u0013\u0018\u00010\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\"\u0010\u0017\u001a\u0016\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u0018\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0016\u0010\u0019\u001a\n\u0012\u0004\u0012\u00020\u0018\u0018\u00010\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000RN\u0010 \u001aB\u0012\f\u0012\n \"*\u0004\u0018\u00010\b0\b\u0012\f\u0012\n \"*\u0004\u0018\u00010\f0\f \"* \u0012\f\u0012\n \"*\u0004\u0018\u00010\b0\b\u0012\f\u0012\n \"*\u0004\u0018\u00010\f0\f\u0018\u00010#0!X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010;\u001a\u00020\u00058F\u00a2\u0006\u0006\u001a\u0004\b<\u0010\u001fR\u0017\u0010=\u001a\b\u0012\u0004\u0012\u00020\b0>8F\u00a2\u0006\u0006\u001a\u0004\b?\u0010@\u00a8\u0006B"}, d2={"Lcom/muslim/browser/pro/browser/TabWebViewManager;", "", "context", "Landroid/content/Context;", "maxLiveWebViews", "", "webViewFactory", "Lkotlin/Function1;", "", "Lkotlin/ParameterName;", "name", "tabId", "Landroid/webkit/WebView;", "onSaveTabBundle", "Lkotlin/Function2;", "Landroid/os/Bundle;", "bundle", "", "onSyncTheme", "", "onSyncDesktopMode", "isDesktopModeProvider", "Lkotlin/Function0;", "onSyncArchitecture", "Lcom/muslim/browser/pro/browser/DesktopArchitecture;", "architectureProvider", "<init>", "(Landroid/content/Context;ILkotlin/jvm/functions/Function1;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function0;Lkotlin/jvm/functions/Function2;Lkotlin/jvm/functions/Function0;)V", "getContext", "()Landroid/content/Context;", "getMaxLiveWebViews", "()I", "liveWebViews", "", "kotlin.jvm.PlatformType", "", "hasLiveWebView", "getLiveWebView", "getOrCreateWebView", "Lkotlin/Pair;", "url", "onRestored", "syncAllLiveWebViewsTheme", "isDarkTheme", "syncAllLiveWebViews", "isDesktopMode", "syncAllLiveWebViewsArchitecture", "architecture", "webView", "forEachLiveWebView", "action", "ensureCapacity", "exemptTabId", "destroyWebView", "pauseAll", "exceptTabId", "resumeTab", "clearAllData", "destroyAll", "liveWebViewCount", "getLiveWebViewCount", "liveTabIds", "", "getLiveTabIds", "()Ljava/util/Set;", "Companion", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class TabWebViewManager {
    public static final int $stable;
    public static final Companion Companion;
    public static final int MAX_LIVE_WEBVIEWS = 4;
    private final Function0<DesktopArchitecture> architectureProvider;
    private final Context context;
    private final Function0<Boolean> isDesktopModeProvider;
    private final Map<String, WebView> liveWebViews;
    private final int maxLiveWebViews;
    private final Function2<String, Bundle, Unit> onSaveTabBundle;
    private final Function2<WebView, DesktopArchitecture, Unit> onSyncArchitecture;
    private final Function2<WebView, Boolean, Unit> onSyncDesktopMode;
    private final Function2<WebView, Boolean, Unit> onSyncTheme;
    private final Function1<String, WebView> webViewFactory;

    static {
        Companion = new Companion(null);
        $stable = 8;
    }

    public TabWebViewManager(Context context, int n, Function1<? super String, ? extends WebView> function1, Function2<? super String, ? super Bundle, Unit> function2, Function2<? super WebView, ? super Boolean, Unit> function22, Function2<? super WebView, ? super Boolean, Unit> function23, Function0<Boolean> function0, Function2<? super WebView, ? super DesktopArchitecture, Unit> function24, Function0<? extends DesktopArchitecture> function02) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        Intrinsics.checkNotNullParameter(function1, (String)"webViewFactory");
        Intrinsics.checkNotNullParameter(function2, (String)"onSaveTabBundle");
        this.context = context;
        this.maxLiveWebViews = n;
        this.webViewFactory = function1;
        this.onSaveTabBundle = function2;
        this.onSyncTheme = function22;
        this.onSyncDesktopMode = function23;
        this.isDesktopModeProvider = function0;
        this.onSyncArchitecture = function24;
        this.architectureProvider = function02;
        this.liveWebViews = Collections.synchronizedMap(new LinkedHashMap(this.maxLiveWebViews, 0.75f, true));
    }

    public /* synthetic */ TabWebViewManager(Context context, int n, Function1 function1, Function2 function2, Function2 function22, Function2 function23, Function0 function0, Function2 function24, Function0 function02, int n2, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n2 & 2) != 0) {
            n = 4;
        }
        if ((n2 & 8) != 0) {
            function2 = new TabWebViewManager$$ExternalSyntheticLambda0();
        }
        if ((n2 & 0x10) != 0) {
            function22 = null;
        }
        if ((n2 & 0x20) != 0) {
            function23 = null;
        }
        if ((n2 & 0x40) != 0) {
            function0 = null;
        }
        if ((n2 & 0x80) != 0) {
            function24 = null;
        }
        if ((n2 & 0x100) != 0) {
            function02 = null;
        }
        this(context, n, (Function1<? super String, ? extends WebView>)function1, (Function2<? super String, ? super Bundle, Unit>)function2, (Function2<? super WebView, ? super Boolean, Unit>)function22, (Function2<? super WebView, ? super Boolean, Unit>)function23, (Function0<Boolean>)function0, (Function2<? super WebView, ? super DesktopArchitecture, Unit>)function24, (Function0<? extends DesktopArchitecture>)function02);
    }

    static final Unit _init_$lambda$0(String string, Bundle bundle) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<unused var>");
        Intrinsics.checkNotNullParameter((Object)bundle, (String)"<unused var>");
        return Unit.INSTANCE;
    }

    public static /* synthetic */ Pair getOrCreateWebView$default(TabWebViewManager tabWebViewManager, String string, String string2, Bundle bundle, Function0 function0, int n, Object object) {
        if ((n & 2) != 0) {
            string2 = null;
        }
        if ((n & 4) != 0) {
            bundle = null;
        }
        if ((n & 8) != 0) {
            function0 = null;
        }
        return tabWebViewManager.getOrCreateWebView(string, string2, bundle, (Function0<Unit>)function0);
    }

    public static /* synthetic */ void pauseAll$default(TabWebViewManager tabWebViewManager, String string, int n, Object object) {
        if ((n & 1) != 0) {
            string = null;
        }
        tabWebViewManager.pauseAll(string);
    }

    public final void clearAllData() {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            object = object.entrySet().iterator();
            while (object.hasNext()) {
                WebView webView2 = (WebView)((Map.Entry)object.next()).getValue();
                try {
                    webView2.clearHistory();
                    webView2.clearCache(true);
                    webView2.clearFormData();
                    webView2.clearSslPreferences();
                }
                catch (Exception exception) {}
            }
            object = Unit.INSTANCE;
            return;
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void destroyAll() {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            Iterator<Map.Entry<String, WebView>> iterator = object.entrySet().iterator();
            while (true) {
                if (!iterator.hasNext()) {
                    this.liveWebViews.clear();
                    object = Unit.INSTANCE;
                    return;
                }
                WebView webView2 = iterator.next().getValue();
                try {
                    object = webView2.getParent();
                    object = object instanceof ViewGroup ? (ViewGroup)object : null;
                    if (object != null) {
                        object.removeView((View)webView2);
                    }
                    webView2.stopLoading();
                    webView2.pauseTimers();
                    webView2.onPause();
                    webView2.removeAllViews();
                    webView2.destroy();
                }
                catch (Exception exception) {
                }
            }
        }
    }

    /*
     * WARNING - void declaration
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void destroyWebView(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        WebView webView2 = this.liveWebViews.remove(string);
        if (webView2 == null) {
            return;
        }
        try {
            void var2_6;
            ViewParent viewParent = webView2.getParent();
            if (viewParent instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup)viewParent;
            } else {
                Object var2_5 = null;
            }
            if (var2_6 != null) {
                var2_6.removeView((View)webView2);
            }
            webView2.stopLoading();
            webView2.pauseTimers();
            webView2.destroy();
            return;
        }
        catch (Exception exception) {
            Log.e((String)"TabWebViewManager", (String)("Error destroying WebView for " + string), (Throwable)exception);
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void ensureCapacity(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"exemptTabId");
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            while (this.liveWebViews.size() >= this.maxLiveWebViews) {
                Object object;
                Iterator<Map.Entry<String, WebView>> iterator = this.liveWebViews.entrySet().iterator();
                Object object2 = null;
                do {
                    object = object2;
                } while (iterator.hasNext() && Intrinsics.areEqual((Object)(object = iterator.next()).getKey(), (Object)string));
                if (object == null) break;
                object2 = object.getKey();
                Object object3 = object.getValue();
                object = new Bundle();
                try {
                    object3.saveState((Bundle)object);
                    this.onSaveTabBundle.invoke(object2, object);
                }
                catch (Exception exception) {
                    object = new StringBuilder();
                    Log.e((String)"TabWebViewManager", (String)((StringBuilder)object).append("Error saving state on eviction for ").append((String)object2).toString(), (Throwable)exception);
                }
                try {
                    object = object3.getParent();
                    object = object instanceof ViewGroup ? (ViewGroup)object : null;
                    if (object != null) {
                        object.removeView((View)object3);
                    }
                    object3.stopLoading();
                    object3.pauseTimers();
                    object3.destroy();
                }
                catch (Exception exception) {
                    object3 = new StringBuilder();
                    Log.e((String)"TabWebViewManager", (String)((StringBuilder)object3).append("Error destroying evicted WebView for ").append((String)object2).toString(), (Throwable)exception);
                }
                iterator.remove();
            }
            string = Unit.INSTANCE;
            return;
        }
    }

    public final void forEachLiveWebView(Function1<? super WebView, Unit> unit) {
        Intrinsics.checkNotNullParameter(unit, (String)"action");
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            object = object.entrySet().iterator();
            while (object.hasNext()) {
                WebView webView2 = (WebView)((Map.Entry)object.next()).getValue();
                try {
                    Intrinsics.checkNotNull((Object)webView2);
                    unit.invoke((Object)webView2);
                }
                catch (Exception exception) {}
            }
            unit = Unit.INSTANCE;
            return;
        }
    }

    public final Context getContext() {
        return this.context;
    }

    public final Set<String> getLiveTabIds() {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Set set = CollectionsKt.toSet((Iterable)this.liveWebViews.keySet());
            return set;
        }
    }

    public final WebView getLiveWebView(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        return this.liveWebViews.get(string);
    }

    public final int getLiveWebViewCount() {
        return this.liveWebViews.size();
    }

    public final int getMaxLiveWebViews() {
        return this.maxLiveWebViews;
    }

    public final Pair<WebView, Boolean> getOrCreateWebView(String function0, String object, Bundle object2, Function0<Unit> function02) {
        boolean bl;
        Intrinsics.checkNotNullParameter((Object)function0, (String)"tabId");
        Function0<DesktopArchitecture> function03 = this.liveWebViews.get(function0);
        boolean bl2 = false;
        Function0<Boolean> function04 = null;
        WebView webView2 = null;
        if (function03 != null) {
            object = this.architectureProvider;
            function0 = webView2;
            if (object != null) {
                function0 = (DesktopArchitecture)object.invoke();
            }
            if (function0 != null) {
                object = this.onSyncArchitecture;
                if (object != null) {
                    object.invoke(function03, function0);
                }
            } else {
                function0 = this.isDesktopModeProvider;
                if (function0 != null) {
                    boolean bl3 = (Boolean)function0.invoke();
                    function0 = this.onSyncDesktopMode;
                    if (function0 != null) {
                        function0.invoke(function03, (Object)bl3);
                    }
                }
            }
            return new Pair(function03, (Object)false);
        }
        this.ensureCapacity((String)function0);
        webView2 = (WebView)this.webViewFactory.invoke((Object)function0);
        function03 = this.architectureProvider;
        if (function03 != null) {
            function04 = (Function0<Boolean>)function03.invoke();
        }
        if (function04 != null) {
            function03 = this.onSyncArchitecture;
            if (function03 != null) {
                function03.invoke((Object)webView2, function04);
            }
        } else {
            function04 = this.isDesktopModeProvider;
            if (function04 != null) {
                bl = (Boolean)function04.invoke();
                function04 = this.onSyncDesktopMode;
                if (function04 != null) {
                    function04.invoke((Object)webView2, (Object)bl);
                }
            }
        }
        boolean bl4 = false;
        if (object2 != null) {
            webView2.restoreState(object2);
            bl = bl4 = true;
            if (function02 != null) {
                function02.invoke();
                bl = bl4;
            }
        } else {
            object2 = (CharSequence)object;
            if (object2 == null || StringsKt.isBlank((CharSequence)object2)) {
                bl2 = true;
            }
            bl = bl4;
            if (!bl2) {
                webView2.loadUrl(object);
                bl = bl4;
            }
        }
        object = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
        object.put(function0, webView2);
        return new Pair((Object)webView2, (Object)bl);
    }

    public final boolean hasLiveWebView(WebView webView2) {
        Intrinsics.checkNotNullParameter((Object)webView2, (String)"webView");
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            boolean bl = this.liveWebViews.containsValue(webView2);
            return bl;
        }
    }

    public final boolean hasLiveWebView(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        return this.liveWebViews.containsKey(string);
    }

    public final void pauseAll(String string) {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            WebView entry;
            Map<String, WebView> map3 = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(map3, (String)"liveWebViews");
            for (Map.Entry entry2 : map3.entrySet()) {
                String string2 = (String)entry2.getKey();
                entry = (WebView)entry2.getValue();
                boolean bl = Intrinsics.areEqual((Object)string2, (Object)string);
                if (bl) continue;
            }
            {
                try {
                    entry.onPause();
                    entry.pauseTimers();
                    continue;
                }
                catch (Exception exception) {
                    continue;
                }
            }
            string = Unit.INSTANCE;
            return;
        }
    }

    public final void resumeTab(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"tabId");
        string = this.liveWebViews.get(string);
        if (string == null) {
            return;
        }
        try {
            string.onResume();
            string.resumeTimers();
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void syncAllLiveWebViews(boolean bl) {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            object = object.entrySet().iterator();
            while (true) {
                if (!object.hasNext()) {
                    object = Unit.INSTANCE;
                    return;
                }
                WebView webView2 = (WebView)((Map.Entry)object.next()).getValue();
                try {
                    Function2<WebView, Boolean, Unit> function2 = this.onSyncDesktopMode;
                    if (function2 == null) continue;
                    Intrinsics.checkNotNull((Object)webView2);
                    function2.invoke((Object)webView2, (Object)bl);
                    webView2 = Unit.INSTANCE;
                }
                catch (Exception exception) {
                    webView2 = Unit.INSTANCE;
                    continue;
                }
                break;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void syncAllLiveWebViewsArchitecture(DesktopArchitecture desktopArchitecture) {
        Intrinsics.checkNotNullParameter((Object)desktopArchitecture, (String)"architecture");
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            object = object.entrySet().iterator();
            while (true) {
                if (!object.hasNext()) {
                    desktopArchitecture = Unit.INSTANCE;
                    return;
                }
                WebView webView2 = (WebView)((Map.Entry)object.next()).getValue();
                try {
                    Object object2;
                    if (this.onSyncArchitecture != null) {
                        object2 = this.onSyncArchitecture;
                        Intrinsics.checkNotNull((Object)webView2);
                        object2.invoke((Object)webView2, (Object)desktopArchitecture);
                        webView2 = Unit.INSTANCE;
                        continue;
                    }
                    object2 = this.onSyncDesktopMode;
                    if (object2 == null) continue;
                    Intrinsics.checkNotNull((Object)webView2);
                    object2.invoke((Object)webView2, (Object)desktopArchitecture.isAnyDesktop());
                    webView2 = Unit.INSTANCE;
                }
                catch (Exception exception) {
                    webView2 = Unit.INSTANCE;
                    continue;
                }
                break;
            }
        }
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public final void syncAllLiveWebViewsTheme(boolean bl) {
        Map<String, WebView> map2 = this.liveWebViews;
        Intrinsics.checkNotNullExpressionValue(map2, (String)"liveWebViews");
        synchronized (map2) {
            Object object = this.liveWebViews;
            Intrinsics.checkNotNullExpressionValue(object, (String)"liveWebViews");
            object = object.entrySet().iterator();
            while (true) {
                if (!object.hasNext()) {
                    object = Unit.INSTANCE;
                    return;
                }
                WebView webView2 = (WebView)((Map.Entry)object.next()).getValue();
                try {
                    Function2<WebView, Boolean, Unit> function2 = this.onSyncTheme;
                    if (function2 == null) continue;
                    Intrinsics.checkNotNull((Object)webView2);
                    function2.invoke((Object)webView2, (Object)bl);
                    webView2 = Unit.INSTANCE;
                }
                catch (Exception exception) {
                    webView2 = Unit.INSTANCE;
                    continue;
                }
                break;
            }
        }
    }

    @Metadata(d1={"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003R\u000e\u0010\u0004\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0006"}, d2={"Lcom/muslim/browser/pro/browser/TabWebViewManager$Companion;", "", "<init>", "()V", "MAX_LIVE_WEBVIEWS", "", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static final class Companion {
        private Companion() {
        }

        public /* synthetic */ Companion(DefaultConstructorMarker defaultConstructorMarker) {
            this();
        }
    }
}

