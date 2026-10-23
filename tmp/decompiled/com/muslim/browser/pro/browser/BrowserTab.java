/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.os.Bundle
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import android.os.Bundle;
import com.muslim.browser.pro.browser.BlockedInfo;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b#\b\u0087\b\u0018\u00002\u00020\u0001B\u008d\u0001\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u0012\b\b\u0002\u0010\t\u001a\u00020\b\u0012\b\b\u0002\u0010\n\u001a\u00020\b\u0012\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f\u0012\b\b\u0002\u0010\r\u001a\u00020\b\u0012\b\b\u0002\u0010\u000e\u001a\u00020\b\u0012\b\b\u0002\u0010\u000f\u001a\u00020\u0010\u0012\b\b\u0002\u0010\u0011\u001a\u00020\b\u0012\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\u0004\b\u0014\u0010\u0015J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0003H\u00c6\u0003J\t\u0010&\u001a\u00020\u0003H\u00c6\u0003J\t\u0010'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\bH\u00c6\u0003J\t\u0010)\u001a\u00020\bH\u00c6\u0003J\t\u0010*\u001a\u00020\bH\u00c6\u0003J\u000b\u0010+\u001a\u0004\u0018\u00010\fH\u00c6\u0003J\t\u0010,\u001a\u00020\bH\u00c6\u0003J\t\u0010-\u001a\u00020\bH\u00c6\u0003J\t\u0010.\u001a\u00020\u0010H\u00c6\u0003J\t\u0010/\u001a\u00020\bH\u00c6\u0003J\u000b\u00100\u001a\u0004\u0018\u00010\u0013H\u00c6\u0003J\u008f\u0001\u00101\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\b2\b\b\u0002\u0010\t\u001a\u00020\b2\b\b\u0002\u0010\n\u001a\u00020\b2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\f2\b\b\u0002\u0010\r\u001a\u00020\b2\b\b\u0002\u0010\u000e\u001a\u00020\b2\b\b\u0002\u0010\u000f\u001a\u00020\u00102\b\b\u0002\u0010\u0011\u001a\u00020\b2\n\b\u0002\u0010\u0012\u001a\u0004\u0018\u00010\u0013H\u00c6\u0001J\u0013\u00102\u001a\u00020\b2\b\u00103\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00104\u001a\u00020\u0010H\u00d6\u0001J\t\u00105\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0017R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0017R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\u001bR\u0011\u0010\t\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\n\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001bR\u0013\u0010\u000b\u001a\u0004\u0018\u00010\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u001fR\u0011\u0010\r\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u001bR\u0011\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\b\n\u0000\u001a\u0004\b \u0010!R\u0011\u0010\u0011\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u001bR\u0013\u0010\u0012\u001a\u0004\u0018\u00010\u0013\u00a2\u0006\b\n\u0000\u001a\u0004\b\"\u0010#\u00a8\u00066"}, d2={"Lcom/muslim/browser/pro/browser/BrowserTab;", "", "id", "", "url", "searchInput", "pageTitle", "isHomePage", "", "canGoBack", "canGoForward", "blockedInfo", "Lcom/muslim/browser/pro/browser/BlockedInfo;", "isPageTranslated", "isLoading", "loadingProgress", "", "isPageContentVisible", "bundle", "Landroid/os/Bundle;", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;ZZZLcom/muslim/browser/pro/browser/BlockedInfo;ZZIZLandroid/os/Bundle;)V", "getId", "()Ljava/lang/String;", "getUrl", "getSearchInput", "getPageTitle", "()Z", "getCanGoBack", "getCanGoForward", "getBlockedInfo", "()Lcom/muslim/browser/pro/browser/BlockedInfo;", "getLoadingProgress", "()I", "getBundle", "()Landroid/os/Bundle;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "component11", "component12", "component13", "copy", "equals", "other", "hashCode", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class BrowserTab {
    public static final int $stable = 8;
    private final BlockedInfo blockedInfo;
    private final Bundle bundle;
    private final boolean canGoBack;
    private final boolean canGoForward;
    private final String id;
    private final boolean isHomePage;
    private final boolean isLoading;
    private final boolean isPageContentVisible;
    private final boolean isPageTranslated;
    private final int loadingProgress;
    private final String pageTitle;
    private final String searchInput;
    private final String url;

    public BrowserTab() {
        this(null, null, null, null, false, false, false, null, false, false, 0, false, null, 8191, null);
    }

    public BrowserTab(String string, String string2, String string3, String string4, boolean bl, boolean bl2, boolean bl3, BlockedInfo blockedInfo, boolean bl4, boolean bl5, int n, boolean bl6, Bundle bundle) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"searchInput");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"pageTitle");
        this.id = string;
        this.url = string2;
        this.searchInput = string3;
        this.pageTitle = string4;
        this.isHomePage = bl;
        this.canGoBack = bl2;
        this.canGoForward = bl3;
        this.blockedInfo = blockedInfo;
        this.isPageTranslated = bl4;
        this.isLoading = bl5;
        this.loadingProgress = n;
        this.isPageContentVisible = bl6;
        this.bundle = bundle;
    }

    public /* synthetic */ BrowserTab(String string, String string2, String string3, String string4, boolean bl, boolean bl2, boolean bl3, BlockedInfo blockedInfo, boolean bl4, boolean bl5, int n, boolean bl6, Bundle bundle, int n2, DefaultConstructorMarker object) {
        if ((n2 & 1) != 0) {
            string = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        }
        object = "";
        if ((n2 & 2) != 0) {
            string2 = "";
        }
        if ((n2 & 4) != 0) {
            string3 = object;
        }
        if ((n2 & 8) != 0) {
            string4 = "Home";
        }
        if ((n2 & 0x10) != 0) {
            bl = true;
        }
        boolean bl7 = false;
        if ((n2 & 0x20) != 0) {
            bl2 = false;
        }
        if ((n2 & 0x40) != 0) {
            bl3 = false;
        }
        object = null;
        if ((n2 & 0x80) != 0) {
            blockedInfo = null;
        }
        if ((n2 & 0x100) != 0) {
            bl4 = false;
        }
        if ((n2 & 0x200) != 0) {
            bl5 = false;
        }
        if ((n2 & 0x400) != 0) {
            n = 0;
        }
        if ((n2 & 0x800) != 0) {
            bl6 = bl7;
        }
        if ((n2 & 0x1000) != 0) {
            bundle = object;
        }
        this(string, string2, string3, string4, bl, bl2, bl3, blockedInfo, bl4, bl5, n, bl6, bundle);
    }

    public static /* synthetic */ BrowserTab copy$default(BrowserTab browserTab, String string, String string2, String string3, String string4, boolean bl, boolean bl2, boolean bl3, BlockedInfo blockedInfo, boolean bl4, boolean bl5, int n, boolean bl6, Bundle bundle, int n2, Object object) {
        block12: {
            if ((n2 & 1) != 0) {
                string = browserTab.id;
            }
            if ((n2 & 2) != 0) {
                string2 = browserTab.url;
            }
            if ((n2 & 4) != 0) {
                string3 = browserTab.searchInput;
            }
            if ((n2 & 8) != 0) {
                string4 = browserTab.pageTitle;
            }
            if ((n2 & 0x10) != 0) {
                bl = browserTab.isHomePage;
            }
            if ((n2 & 0x20) != 0) {
                bl2 = browserTab.canGoBack;
            }
            if ((n2 & 0x40) != 0) {
                bl3 = browserTab.canGoForward;
            }
            if ((n2 & 0x80) != 0) {
                blockedInfo = browserTab.blockedInfo;
            }
            if ((n2 & 0x100) != 0) {
                bl4 = browserTab.isPageTranslated;
            }
            if ((n2 & 0x200) != 0) {
                bl5 = browserTab.isLoading;
            }
            if ((n2 & 0x400) != 0) {
                n = browserTab.loadingProgress;
            }
            if ((n2 & 0x800) != 0) {
                bl6 = browserTab.isPageContentVisible;
            }
            if ((n2 & 0x1000) == 0) break block12;
            bundle = browserTab.bundle;
        }
        return browserTab.copy(string, string2, string3, string4, bl, bl2, bl3, blockedInfo, bl4, bl5, n, bl6, bundle);
    }

    public final String component1() {
        return this.id;
    }

    public final boolean component10() {
        return this.isLoading;
    }

    public final int component11() {
        return this.loadingProgress;
    }

    public final boolean component12() {
        return this.isPageContentVisible;
    }

    public final Bundle component13() {
        return this.bundle;
    }

    public final String component2() {
        return this.url;
    }

    public final String component3() {
        return this.searchInput;
    }

    public final String component4() {
        return this.pageTitle;
    }

    public final boolean component5() {
        return this.isHomePage;
    }

    public final boolean component6() {
        return this.canGoBack;
    }

    public final boolean component7() {
        return this.canGoForward;
    }

    public final BlockedInfo component8() {
        return this.blockedInfo;
    }

    public final boolean component9() {
        return this.isPageTranslated;
    }

    public final BrowserTab copy(String string, String string2, String string3, String string4, boolean bl, boolean bl2, boolean bl3, BlockedInfo blockedInfo, boolean bl4, boolean bl5, int n, boolean bl6, Bundle bundle) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"searchInput");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"pageTitle");
        return new BrowserTab(string, string2, string3, string4, bl, bl2, bl3, blockedInfo, bl4, bl5, n, bl6, bundle);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof BrowserTab)) {
            return false;
        }
        object = (BrowserTab)object;
        if (!Intrinsics.areEqual((Object)this.id, (Object)((BrowserTab)object).id)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)((BrowserTab)object).url)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.searchInput, (Object)((BrowserTab)object).searchInput)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.pageTitle, (Object)((BrowserTab)object).pageTitle)) {
            return false;
        }
        if (this.isHomePage != ((BrowserTab)object).isHomePage) {
            return false;
        }
        if (this.canGoBack != ((BrowserTab)object).canGoBack) {
            return false;
        }
        if (this.canGoForward != ((BrowserTab)object).canGoForward) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.blockedInfo, (Object)((BrowserTab)object).blockedInfo)) {
            return false;
        }
        if (this.isPageTranslated != ((BrowserTab)object).isPageTranslated) {
            return false;
        }
        if (this.isLoading != ((BrowserTab)object).isLoading) {
            return false;
        }
        if (this.loadingProgress != ((BrowserTab)object).loadingProgress) {
            return false;
        }
        if (this.isPageContentVisible != ((BrowserTab)object).isPageContentVisible) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.bundle, (Object)((BrowserTab)object).bundle);
    }

    public final BlockedInfo getBlockedInfo() {
        return this.blockedInfo;
    }

    public final Bundle getBundle() {
        return this.bundle;
    }

    public final boolean getCanGoBack() {
        return this.canGoBack;
    }

    public final boolean getCanGoForward() {
        return this.canGoForward;
    }

    public final String getId() {
        return this.id;
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

    public final String getUrl() {
        return this.url;
    }

    public int hashCode() {
        int n = this.id.hashCode();
        int n2 = this.url.hashCode();
        int n3 = this.searchInput.hashCode();
        int n4 = this.pageTitle.hashCode();
        int n5 = Boolean.hashCode(this.isHomePage);
        int n6 = Boolean.hashCode(this.canGoBack);
        int n7 = Boolean.hashCode(this.canGoForward);
        BlockedInfo blockedInfo = this.blockedInfo;
        int n8 = 0;
        int n9 = blockedInfo == null ? 0 : this.blockedInfo.hashCode();
        int n10 = Boolean.hashCode(this.isPageTranslated);
        int n11 = Boolean.hashCode(this.isLoading);
        int n12 = Integer.hashCode(this.loadingProgress);
        int n13 = Boolean.hashCode(this.isPageContentVisible);
        if (this.bundle != null) {
            n8 = this.bundle.hashCode();
        }
        return (((((((((((n * 31 + n2) * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n7) * 31 + n9) * 31 + n10) * 31 + n11) * 31 + n12) * 31 + n13) * 31 + n8;
    }

    public final boolean isHomePage() {
        return this.isHomePage;
    }

    public final boolean isLoading() {
        return this.isLoading;
    }

    public final boolean isPageContentVisible() {
        return this.isPageContentVisible;
    }

    public final boolean isPageTranslated() {
        return this.isPageTranslated;
    }

    public String toString() {
        String string = this.id;
        String string2 = this.url;
        String string3 = this.searchInput;
        String string4 = this.pageTitle;
        boolean bl = this.isHomePage;
        boolean bl2 = this.canGoBack;
        boolean bl3 = this.canGoForward;
        BlockedInfo blockedInfo = this.blockedInfo;
        boolean bl4 = this.isPageTranslated;
        boolean bl5 = this.isLoading;
        int n = this.loadingProgress;
        boolean bl6 = this.isPageContentVisible;
        Bundle bundle = this.bundle;
        return "BrowserTab(id=" + string + ", url=" + string2 + ", searchInput=" + string3 + ", pageTitle=" + string4 + ", isHomePage=" + bl + ", canGoBack=" + bl2 + ", canGoForward=" + bl3 + ", blockedInfo=" + blockedInfo + ", isPageTranslated=" + bl4 + ", isLoading=" + bl5 + ", loadingProgress=" + n + ", isPageContentVisible=" + bl6 + ", bundle=" + bundle + ")";
    }
}

