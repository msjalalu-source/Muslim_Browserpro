/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import com.muslim.browser.pro.browser.DownloadStatus;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\t\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\b\u001e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001Bi\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\b\b\u0002\u0010\b\u001a\u00020\u0003\u0012\b\b\u0002\u0010\t\u001a\u00020\u0005\u0012\b\b\u0002\u0010\n\u001a\u00020\u000b\u0012\b\b\u0002\u0010\f\u001a\u00020\u0005\u0012\b\b\u0002\u0010\r\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\u0004\b\u000f\u0010\u0010J\t\u0010\u001e\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0005H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0003H\u00c6\u0003J\t\u0010#\u001a\u00020\u0005H\u00c6\u0003J\t\u0010$\u001a\u00020\u000bH\u00c6\u0003J\t\u0010%\u001a\u00020\u0005H\u00c6\u0003J\t\u0010&\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010'\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003Jo\u0010(\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\b\b\u0002\u0010\b\u001a\u00020\u00032\b\b\u0002\u0010\t\u001a\u00020\u00052\b\b\u0002\u0010\n\u001a\u00020\u000b2\b\b\u0002\u0010\f\u001a\u00020\u00052\b\b\u0002\u0010\r\u001a\u00020\u00052\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\u0003H\u00c6\u0001J\u0013\u0010)\u001a\u00020*2\b\u0010+\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010,\u001a\u00020-H\u00d6\u0001J\t\u0010.\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0012R\u0011\u0010\b\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0012R\u0011\u0010\t\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0014R\u0011\u0010\n\u001a\u00020\u000b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\f\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0014R\u0011\u0010\r\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0014R\u0013\u0010\u000e\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012\u00a8\u0006/"}, d2={"Lcom/muslim/browser/pro/browser/DownloadEntry;", "", "id", "", "downloadId", "", "fileName", "url", "mimeType", "timestamp", "status", "Lcom/muslim/browser/pro/browser/DownloadStatus;", "downloadedBytes", "totalBytes", "localUri", "<init>", "(Ljava/lang/String;JLjava/lang/String;Ljava/lang/String;Ljava/lang/String;JLcom/muslim/browser/pro/browser/DownloadStatus;JJLjava/lang/String;)V", "getId", "()Ljava/lang/String;", "getDownloadId", "()J", "getFileName", "getUrl", "getMimeType", "getTimestamp", "getStatus", "()Lcom/muslim/browser/pro/browser/DownloadStatus;", "getDownloadedBytes", "getTotalBytes", "getLocalUri", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "component10", "copy", "equals", "", "other", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class DownloadEntry {
    public static final int $stable = 0;
    private final long downloadId;
    private final long downloadedBytes;
    private final String fileName;
    private final String id;
    private final String localUri;
    private final String mimeType;
    private final DownloadStatus status;
    private final long timestamp;
    private final long totalBytes;
    private final String url;

    public DownloadEntry(String string, long l, String string2, String string3, String string4, long l2, DownloadStatus downloadStatus, long l3, long l4, String string5) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"fileName");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"mimeType");
        Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
        this.id = string;
        this.downloadId = l;
        this.fileName = string2;
        this.url = string3;
        this.mimeType = string4;
        this.timestamp = l2;
        this.status = downloadStatus;
        this.downloadedBytes = l3;
        this.totalBytes = l4;
        this.localUri = string5;
    }

    public /* synthetic */ DownloadEntry(String string, long l, String string2, String string3, String string4, long l2, DownloadStatus downloadStatus, long l3, long l4, String string5, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 1) != 0) {
            string = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        }
        if ((n & 2) != 0) {
            l = -1L;
        }
        if ((n & 0x10) != 0) {
            string4 = "";
        }
        if ((n & 0x20) != 0) {
            l2 = System.currentTimeMillis();
        }
        if ((n & 0x40) != 0) {
            downloadStatus = DownloadStatus.DOWNLOADING;
        }
        if ((n & 0x80) != 0) {
            l3 = 0L;
        }
        if ((n & 0x100) != 0) {
            l4 = -1L;
        }
        if ((n & 0x200) != 0) {
            string5 = null;
        }
        this(string, l, string2, string3, string4, l2, downloadStatus, l3, l4, string5);
    }

    public static /* synthetic */ DownloadEntry copy$default(DownloadEntry downloadEntry, String string, long l, String string2, String string3, String string4, long l2, DownloadStatus downloadStatus, long l3, long l4, String string5, int n, Object object) {
        block9: {
            if ((n & 1) != 0) {
                string = downloadEntry.id;
            }
            if ((n & 2) != 0) {
                l = downloadEntry.downloadId;
            }
            if ((n & 4) != 0) {
                string2 = downloadEntry.fileName;
            }
            if ((n & 8) != 0) {
                string3 = downloadEntry.url;
            }
            if ((n & 0x10) != 0) {
                string4 = downloadEntry.mimeType;
            }
            if ((n & 0x20) != 0) {
                l2 = downloadEntry.timestamp;
            }
            if ((n & 0x40) != 0) {
                downloadStatus = downloadEntry.status;
            }
            if ((n & 0x80) != 0) {
                l3 = downloadEntry.downloadedBytes;
            }
            if ((n & 0x100) != 0) {
                l4 = downloadEntry.totalBytes;
            }
            if ((n & 0x200) == 0) break block9;
            string5 = downloadEntry.localUri;
        }
        return downloadEntry.copy(string, l, string2, string3, string4, l2, downloadStatus, l3, l4, string5);
    }

    public final String component1() {
        return this.id;
    }

    public final String component10() {
        return this.localUri;
    }

    public final long component2() {
        return this.downloadId;
    }

    public final String component3() {
        return this.fileName;
    }

    public final String component4() {
        return this.url;
    }

    public final String component5() {
        return this.mimeType;
    }

    public final long component6() {
        return this.timestamp;
    }

    public final DownloadStatus component7() {
        return this.status;
    }

    public final long component8() {
        return this.downloadedBytes;
    }

    public final long component9() {
        return this.totalBytes;
    }

    public final DownloadEntry copy(String string, long l, String string2, String string3, String string4, long l2, DownloadStatus downloadStatus, long l3, long l4, String string5) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"fileName");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"mimeType");
        Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
        return new DownloadEntry(string, l, string2, string3, string4, l2, downloadStatus, l3, l4, string5);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DownloadEntry)) {
            return false;
        }
        object = (DownloadEntry)object;
        if (!Intrinsics.areEqual((Object)this.id, (Object)((DownloadEntry)object).id)) {
            return false;
        }
        if (this.downloadId != ((DownloadEntry)object).downloadId) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.fileName, (Object)((DownloadEntry)object).fileName)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)((DownloadEntry)object).url)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.mimeType, (Object)((DownloadEntry)object).mimeType)) {
            return false;
        }
        if (this.timestamp != ((DownloadEntry)object).timestamp) {
            return false;
        }
        if (this.status != ((DownloadEntry)object).status) {
            return false;
        }
        if (this.downloadedBytes != ((DownloadEntry)object).downloadedBytes) {
            return false;
        }
        if (this.totalBytes != ((DownloadEntry)object).totalBytes) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.localUri, (Object)((DownloadEntry)object).localUri);
    }

    public final long getDownloadId() {
        return this.downloadId;
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public final String getFileName() {
        return this.fileName;
    }

    public final String getId() {
        return this.id;
    }

    public final String getLocalUri() {
        return this.localUri;
    }

    public final String getMimeType() {
        return this.mimeType;
    }

    public final DownloadStatus getStatus() {
        return this.status;
    }

    public final long getTimestamp() {
        return this.timestamp;
    }

    public final long getTotalBytes() {
        return this.totalBytes;
    }

    public final String getUrl() {
        return this.url;
    }

    public int hashCode() {
        int n = this.id.hashCode();
        int n2 = Long.hashCode(this.downloadId);
        int n3 = this.fileName.hashCode();
        int n4 = this.url.hashCode();
        int n5 = this.mimeType.hashCode();
        int n6 = Long.hashCode(this.timestamp);
        int n7 = this.status.hashCode();
        int n8 = Long.hashCode(this.downloadedBytes);
        int n9 = Long.hashCode(this.totalBytes);
        int n10 = this.localUri == null ? 0 : this.localUri.hashCode();
        return ((((((((n * 31 + n2) * 31 + n3) * 31 + n4) * 31 + n5) * 31 + n6) * 31 + n7) * 31 + n8) * 31 + n9) * 31 + n10;
    }

    public String toString() {
        String string = this.id;
        long l = this.downloadId;
        String string2 = this.fileName;
        String string3 = this.url;
        String string4 = this.mimeType;
        long l2 = this.timestamp;
        DownloadStatus downloadStatus = this.status;
        long l3 = this.downloadedBytes;
        long l4 = this.totalBytes;
        String string5 = this.localUri;
        return "DownloadEntry(id=" + string + ", downloadId=" + l + ", fileName=" + string2 + ", url=" + string3 + ", mimeType=" + string4 + ", timestamp=" + l2 + ", status=" + (Object)((Object)downloadStatus) + ", downloadedBytes=" + l3 + ", totalBytes=" + l4 + ", localUri=" + string5 + ")";
    }
}

