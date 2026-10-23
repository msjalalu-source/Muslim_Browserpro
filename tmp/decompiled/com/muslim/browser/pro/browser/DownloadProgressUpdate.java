/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import com.muslim.browser.pro.browser.DownloadStatus;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B1\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0003\u0012\u0006\u0010\u0007\u001a\u00020\u0003\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\u0004\b\n\u0010\u000bJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\tH\u00c6\u0003J=\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\u00032\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\tH\u00c6\u0001J\u0013\u0010\u001a\u001a\u00020\u001b2\b\u0010\u001c\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001d\u001a\u00020\u001eH\u00d6\u0001J\t\u0010\u001f\u001a\u00020\tH\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\rR\u0011\u0010\u0007\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\rR\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006 "}, d2={"Lcom/muslim/browser/pro/browser/DownloadProgressUpdate;", "", "downloadId", "", "status", "Lcom/muslim/browser/pro/browser/DownloadStatus;", "downloadedBytes", "totalBytes", "localUri", "", "<init>", "(JLcom/muslim/browser/pro/browser/DownloadStatus;JJLjava/lang/String;)V", "getDownloadId", "()J", "getStatus", "()Lcom/muslim/browser/pro/browser/DownloadStatus;", "getDownloadedBytes", "getTotalBytes", "getLocalUri", "()Ljava/lang/String;", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class DownloadProgressUpdate {
    public static final int $stable = 0;
    private final long downloadId;
    private final long downloadedBytes;
    private final String localUri;
    private final DownloadStatus status;
    private final long totalBytes;

    public DownloadProgressUpdate(long l, DownloadStatus downloadStatus, long l2, long l3, String string) {
        Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
        this.downloadId = l;
        this.status = downloadStatus;
        this.downloadedBytes = l2;
        this.totalBytes = l3;
        this.localUri = string;
    }

    public static /* synthetic */ DownloadProgressUpdate copy$default(DownloadProgressUpdate downloadProgressUpdate, long l, DownloadStatus downloadStatus, long l2, long l3, String string, int n, Object object) {
        block4: {
            if ((n & 1) != 0) {
                l = downloadProgressUpdate.downloadId;
            }
            if ((n & 2) != 0) {
                downloadStatus = downloadProgressUpdate.status;
            }
            if ((n & 4) != 0) {
                l2 = downloadProgressUpdate.downloadedBytes;
            }
            if ((n & 8) != 0) {
                l3 = downloadProgressUpdate.totalBytes;
            }
            if ((n & 0x10) == 0) break block4;
            string = downloadProgressUpdate.localUri;
        }
        return downloadProgressUpdate.copy(l, downloadStatus, l2, l3, string);
    }

    public final long component1() {
        return this.downloadId;
    }

    public final DownloadStatus component2() {
        return this.status;
    }

    public final long component3() {
        return this.downloadedBytes;
    }

    public final long component4() {
        return this.totalBytes;
    }

    public final String component5() {
        return this.localUri;
    }

    public final DownloadProgressUpdate copy(long l, DownloadStatus downloadStatus, long l2, long l3, String string) {
        Intrinsics.checkNotNullParameter((Object)((Object)downloadStatus), (String)"status");
        return new DownloadProgressUpdate(l, downloadStatus, l2, l3, string);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof DownloadProgressUpdate)) {
            return false;
        }
        object = (DownloadProgressUpdate)object;
        if (this.downloadId != ((DownloadProgressUpdate)object).downloadId) {
            return false;
        }
        if (this.status != ((DownloadProgressUpdate)object).status) {
            return false;
        }
        if (this.downloadedBytes != ((DownloadProgressUpdate)object).downloadedBytes) {
            return false;
        }
        if (this.totalBytes != ((DownloadProgressUpdate)object).totalBytes) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.localUri, (Object)((DownloadProgressUpdate)object).localUri);
    }

    public final long getDownloadId() {
        return this.downloadId;
    }

    public final long getDownloadedBytes() {
        return this.downloadedBytes;
    }

    public final String getLocalUri() {
        return this.localUri;
    }

    public final DownloadStatus getStatus() {
        return this.status;
    }

    public final long getTotalBytes() {
        return this.totalBytes;
    }

    public int hashCode() {
        int n = Long.hashCode(this.downloadId);
        int n2 = this.status.hashCode();
        int n3 = Long.hashCode(this.downloadedBytes);
        int n4 = Long.hashCode(this.totalBytes);
        int n5 = this.localUri == null ? 0 : this.localUri.hashCode();
        return (((n * 31 + n2) * 31 + n3) * 31 + n4) * 31 + n5;
    }

    public String toString() {
        long l = this.downloadId;
        DownloadStatus downloadStatus = this.status;
        long l2 = this.downloadedBytes;
        long l3 = this.totalBytes;
        String string = this.localUri;
        return "DownloadProgressUpdate(downloadId=" + l + ", status=" + (Object)((Object)downloadStatus) + ", downloadedBytes=" + l2 + ", totalBytes=" + l3 + ", localUri=" + string + ")";
    }
}

