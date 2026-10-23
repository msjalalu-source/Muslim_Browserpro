/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro.browser;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;

@Metadata(d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\r\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0006\u0010\u0007J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000f\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u0010\u001a\u00020\u00112\b\u0010\u0012\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0013\u001a\u00020\u0014H\u00d6\u0001J\t\u0010\u0015\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\tR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\t\u00a8\u0006\u0016"}, d2={"Lcom/muslim/browser/pro/browser/SslWarningState;", "", "host", "", "url", "details", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getHost", "()Ljava/lang/String;", "getUrl", "getDetails", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class SslWarningState {
    public static final int $stable = 0;
    private final String details;
    private final String host;
    private final String url;

    public SslWarningState(String string, String string2, String string3) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"details");
        this.host = string;
        this.url = string2;
        this.details = string3;
    }

    public static /* synthetic */ SslWarningState copy$default(SslWarningState sslWarningState, String string, String string2, String string3, int n, Object object) {
        if ((n & 1) != 0) {
            string = sslWarningState.host;
        }
        if ((n & 2) != 0) {
            string2 = sslWarningState.url;
        }
        if ((n & 4) != 0) {
            string3 = sslWarningState.details;
        }
        return sslWarningState.copy(string, string2, string3);
    }

    public final String component1() {
        return this.host;
    }

    public final String component2() {
        return this.url;
    }

    public final String component3() {
        return this.details;
    }

    public final SslWarningState copy(String string, String string2, String string3) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"host");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"details");
        return new SslWarningState(string, string2, string3);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof SslWarningState)) {
            return false;
        }
        object = (SslWarningState)object;
        if (!Intrinsics.areEqual((Object)this.host, (Object)((SslWarningState)object).host)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)((SslWarningState)object).url)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.details, (Object)((SslWarningState)object).details);
    }

    public final String getDetails() {
        return this.details;
    }

    public final String getHost() {
        return this.host;
    }

    public final String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return (this.host.hashCode() * 31 + this.url.hashCode()) * 31 + this.details.hashCode();
    }

    public String toString() {
        String string = this.host;
        String string2 = this.url;
        String string3 = this.details;
        return "SslWarningState(host=" + string + ", url=" + string2 + ", details=" + string3 + ")";
    }
}

