/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.StringsKt
 */
package com.muslim.browser.pro.browser;

import java.util.Locale;
import java.util.UUID;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.StringsKt;

@Metadata(d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0004\n\u0002\u0010\t\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B5\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0006\u001a\u00020\u0003\u0012\b\b\u0002\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0004\b\t\u0010\nJ\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\bH\u00c6\u0003J;\u0010\u0017\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00032\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0018\u001a\u00020\u00192\b\u0010\u001a\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001b\u001a\u00020\u001cH\u00d6\u0001J\t\u0010\u001d\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\fR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011\u00a8\u0006\u001e"}, d2={"Lcom/muslim/browser/pro/browser/FavoriteSite;", "", "id", "", "name", "url", "iconLetter", "badgeColor", "", "<init>", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;J)V", "getId", "()Ljava/lang/String;", "getName", "getUrl", "getIconLetter", "getBadgeColor", "()J", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class FavoriteSite {
    public static final int $stable = 0;
    private final long badgeColor;
    private final String iconLetter;
    private final String id;
    private final String name;
    private final String url;

    public FavoriteSite(String string, String string2, String string3, String string4, long l) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"name");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"iconLetter");
        this.id = string;
        this.name = string2;
        this.url = string3;
        this.iconLetter = string4;
        this.badgeColor = l;
    }

    public /* synthetic */ FavoriteSite(String charSequence, String string, String string2, String string3, long l, int n, DefaultConstructorMarker object) {
        if ((n & 1) != 0) {
            object = UUID.randomUUID().toString();
            Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toString(...)");
        } else {
            object = charSequence;
        }
        if ((n & 8) != 0) {
            charSequence = string;
            if (!StringsKt.isBlank((CharSequence)charSequence)) {
                charSequence = StringsKt.take((String)((Object)StringsKt.trim((CharSequence)charSequence)).toString(), (int)2).toUpperCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)charSequence, (String)"toUpperCase(...)");
            } else {
                charSequence = "W";
            }
        } else {
            charSequence = string3;
        }
        if ((n & 0x10) != 0) {
            l = 4282549748L;
        }
        this((String)object, string, string2, (String)charSequence, l);
    }

    public static /* synthetic */ FavoriteSite copy$default(FavoriteSite favoriteSite, String string, String string2, String string3, String string4, long l, int n, Object object) {
        if ((n & 1) != 0) {
            string = favoriteSite.id;
        }
        if ((n & 2) != 0) {
            string2 = favoriteSite.name;
        }
        if ((n & 4) != 0) {
            string3 = favoriteSite.url;
        }
        if ((n & 8) != 0) {
            string4 = favoriteSite.iconLetter;
        }
        if ((n & 0x10) != 0) {
            l = favoriteSite.badgeColor;
        }
        return favoriteSite.copy(string, string2, string3, string4, l);
    }

    public final String component1() {
        return this.id;
    }

    public final String component2() {
        return this.name;
    }

    public final String component3() {
        return this.url;
    }

    public final String component4() {
        return this.iconLetter;
    }

    public final long component5() {
        return this.badgeColor;
    }

    public final FavoriteSite copy(String string, String string2, String string3, String string4, long l) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"id");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"name");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"url");
        Intrinsics.checkNotNullParameter((Object)string4, (String)"iconLetter");
        return new FavoriteSite(string, string2, string3, string4, l);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (!(object instanceof FavoriteSite)) {
            return false;
        }
        object = (FavoriteSite)object;
        if (!Intrinsics.areEqual((Object)this.id, (Object)((FavoriteSite)object).id)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)((FavoriteSite)object).name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)((FavoriteSite)object).url)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.iconLetter, (Object)((FavoriteSite)object).iconLetter)) {
            return false;
        }
        return this.badgeColor == ((FavoriteSite)object).badgeColor;
    }

    public final long getBadgeColor() {
        return this.badgeColor;
    }

    public final String getIconLetter() {
        return this.iconLetter;
    }

    public final String getId() {
        return this.id;
    }

    public final String getName() {
        return this.name;
    }

    public final String getUrl() {
        return this.url;
    }

    public int hashCode() {
        return (((this.id.hashCode() * 31 + this.name.hashCode()) * 31 + this.url.hashCode()) * 31 + this.iconLetter.hashCode()) * 31 + Long.hashCode(this.badgeColor);
    }

    public String toString() {
        String string = this.id;
        String string2 = this.name;
        String string3 = this.url;
        String string4 = this.iconLetter;
        long l = this.badgeColor;
        return "FavoriteSite(id=" + string + ", name=" + string2 + ", url=" + string3 + ", iconLetter=" + string4 + ", badgeColor=" + l + ")";
    }
}

