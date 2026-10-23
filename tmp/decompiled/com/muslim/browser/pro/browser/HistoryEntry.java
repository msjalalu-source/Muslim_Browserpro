/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0010\t\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B+\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0004\b\b\u0010\tJ\t\u0010\u0010\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0007H\u00c6\u0003J1\u0010\u0014\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0015\u001a\u00020\u00162\b\u0010\u0017\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0018\u001a\u00020\u0019H\u00d6\u0001J\t\u0010\u001a\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\u000bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000bR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000f\u00a8\u0006\u001b"}, d2={"Lcom/muslim/browser/pro/browser/HistoryEntry;", "", "id", "", "title", "url", "timestamp", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", "getId", "()Ljava/lang/String;", "getTitle", "getUrl", "getTimestamp", "()J", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class HistoryEntry {
    public static final int $stable = 0;
    private final String id;
    private final long timestamp;
    private final String title;
    private final String url;

    public HistoryEntry(String string, String string2, String string3, long l) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"title");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        this.id = string;
        this.title = string2;
        this.url = string3;
        this.timestamp = l;
    }

    public /* synthetic */ HistoryEntry(String string, String string2, String string3, long l, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            string = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        }
        if ((n & 8) != 0) {
            l = System.currentTimeMillis();
        }
        this(string, string2, string3, l);
    }

    public static /* synthetic */ HistoryEntry copy$default(HistoryEntry historyEntry, String string, String string2, String string3, long l, int n, Object object) {
        if ((n & 1) != 0) {
            string = historyEntry.id;
        }
        if ((n & 2) != 0) {
            string2 = historyEntry.title;
        }
        if ((n & 4) != 0) {
            string3 = historyEntry.url;
        }
        if ((n & 8) != 0) {
            l = historyEntry.timestamp;
        }
        return historyEntry.copy(string, string2, string3, l);
    }

    public final String component1() {
        return this.id;
    }

    public final String component2() {
        return this.title;
    }

    public final String component3() {
        return this.url;
    }

    public final long component4() {
        return this.timestamp;
    }

    public final HistoryEntry copy(String string, String string2, String string3, long l) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"title");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        return new HistoryEntry(string, string2, string3, l);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof HistoryEntry)) {
            return false;
        }
        object = (HistoryEntry)object;
        if (!Intrinsics.areEqual((Object)this.id, (Object)((HistoryEntry)object).id)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.title, (Object)((HistoryEntry)object).title)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)((HistoryEntry)object).url)) {
            return false;
        }
        return this.timestamp == ((HistoryEntry)object).timestamp;
    }

    public final String getId() {
        return this.id;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final String getTitle() {
        return this.title;
    }

    public final String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return ((this.id.hashCode() * 31 + this.title.hashCode()) * 31 + this.url.hashCode()) * 31 + Long.hashCode(this.timestamp);
    }

    public String toString() {
        String string = this.id;
        String string2 = this.title;
        String string3 = this.url;
        long l = this.timestamp;
        return "HistoryEntry(id=" + string + ", title=" + string2 + ", url=" + string3 + ", timestamp=" + l + ")";
    }
}

