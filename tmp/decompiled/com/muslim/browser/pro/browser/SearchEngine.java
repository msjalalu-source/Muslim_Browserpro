/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.muslim.browser.pro.R$drawable
 *  kotlin.Metadata
 *  kotlin.enums.EnumEntries
 *  kotlin.enums.EnumEntriesKt
 */
package com.muslim.browser.pro.browser;

import com.muslim.browser.pro.R;
import kotlin.Metadata;
import kotlin.enums.EnumEntries;
import kotlin.enums.EnumEntriesKt;

@Metadata(d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0010\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\t\b\u0086\u0081\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00000\u0001B\u0019\b\u0002\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0004\b\u0006\u0010\u0007R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0004\u001a\u00020\u00058G\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bj\u0002\b\fj\u0002\b\r\u00a8\u0006\u000e"}, d2={"Lcom/muslim/browser/pro/browser/SearchEngine;", "", "engineName", "", "iconRes", "", "<init>", "(Ljava/lang/String;ILjava/lang/String;I)V", "getEngineName", "()Ljava/lang/String;", "getIconRes", "()I", "DUCKDUCKGO", "GOOGLE", "app"}, k=1, mv={2, 2, 0}, xi=48)
public enum SearchEngine {
    DUCKDUCKGO("DuckDuckGo", R.drawable.ic_search_duckduckgo),
    GOOGLE("Google", R.drawable.ic_search_google);

    private static final EnumEntries $ENTRIES = EnumEntriesKt.enumEntries((Enum[])$VALUES);
    private final String engineName;
    private final int iconRes;

    private SearchEngine(String string2, int n2) {
        this.engineName = string2;
        this.iconRes = n2;
    }

    public static EnumEntries<SearchEngine> getEntries() {
        return $ENTRIES;
    }

    public final String getEngineName() {
        return this.engineName;
    }

    public final int getIconRes() {
        return this.iconRes;
    }
}

