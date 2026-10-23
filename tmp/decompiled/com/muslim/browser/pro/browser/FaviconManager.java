/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.content.Context
 *  android.graphics.Bitmap
 *  android.graphics.BitmapFactory
 *  android.net.Uri
 *  android.util.LruCache
 *  kotlin.Metadata
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.Boxing
 *  kotlin.io.ByteStreamsKt
 *  kotlin.io.CloseableKt
 *  kotlin.io.FilesKt
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.Dispatchers
 */
package com.muslim.browser.pro.browser;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.LruCache;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;
import kotlin.Metadata;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.io.ByteStreamsKt;
import kotlin.io.CloseableKt;
import kotlin.io.FilesKt;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.Dispatchers;

@Metadata(d1={"\u0000F\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u00c7\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u000e\u0010\u0010\u001a\u00020\u00052\u0006\u0010\u0011\u001a\u00020\u0005J\u0010\u0010\u0012\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0013\u001a\u00020\u0005J \u0010\u0014\u001a\u0004\u0018\u00010\r2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0002\u0010\u0017J\u000e\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0015\u001a\u00020\u0016J\u001a\u0010\u001a\u001a\u0004\u0018\u00010\u001b2\u0006\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0013\u001a\u00020\u0005H\u0002R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\u0007X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\t\u001a\u00020\nX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\r0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u001a\u0010\u000e\u001a\u000e\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u000f0\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u001c"}, d2={"Lcom/muslim/browser/pro/browser/FaviconManager;", "", "<init>", "()V", "CACHE_DIR_NAME", "", "CONNECT_TIMEOUT_MS", "", "READ_TIMEOUT_MS", "SAFE_FILENAME_REGEX", "Lkotlin/text/Regex;", "memoryCache", "Landroid/util/LruCache;", "Landroid/graphics/Bitmap;", "failedDomains", "", "extractDomain", "url", "getFromMemory", "domain", "loadFavicon", "context", "Landroid/content/Context;", "(Landroid/content/Context;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "clearCache", "", "getDiskCacheFile", "Ljava/io/File;", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class FaviconManager {
    public static final int $stable;
    private static final String CACHE_DIR_NAME = "favicons";
    private static final int CONNECT_TIMEOUT_MS = 3500;
    public static final FaviconManager INSTANCE;
    private static final int READ_TIMEOUT_MS = 4000;
    private static final Regex SAFE_FILENAME_REGEX;
    private static final LruCache<String, Boolean> failedDomains;
    private static final LruCache<String, Bitmap> memoryCache;

    static {
        INSTANCE = new FaviconManager();
        SAFE_FILENAME_REGEX = new Regex("[^a-zA-Z0-9.-]");
        memoryCache = new LruCache(64);
        failedDomains = new LruCache(64);
        $stable = 8;
    }

    private FaviconManager() {
    }

    public static final /* synthetic */ File access$getDiskCacheFile(FaviconManager faviconManager, Context context, String string) {
        return faviconManager.getDiskCacheFile(context, string);
    }

    public static final /* synthetic */ LruCache access$getFailedDomains$p() {
        return failedDomains;
    }

    public static final /* synthetic */ LruCache access$getMemoryCache$p() {
        return memoryCache;
    }

    private final File getDiskCacheFile(Context object, String string) {
        try {
            File file = new File(object.getCacheDir(), CACHE_DIR_NAME);
            object = string;
            string = SAFE_FILENAME_REGEX.replace((CharSequence)object, "_");
            object = new StringBuilder();
            string = ((StringBuilder)object).append(string).append(".png").toString();
            object = new File(file, string);
        }
        catch (Exception exception) {
            object = null;
        }
        return object;
    }

    public final void clearCache(Context context) {
        Intrinsics.checkNotNullParameter((Object)context, (String)"context");
        memoryCache.evictAll();
        failedDomains.evictAll();
        try {
            File file = new File(context.getCacheDir(), CACHE_DIR_NAME);
            if (file.exists()) {
                FilesKt.deleteRecursively((File)file);
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public final String extractDomain(String string) {
        CharSequence charSequence;
        String string2 = "";
        Intrinsics.checkNotNullParameter((Object)string, (String)"url");
        try {
            string = ((Object)StringsKt.trim((CharSequence)string)).toString();
            if (StringsKt.isBlank((CharSequence)string)) return "";
            if (StringsKt.startsWith$default((String)string, (String)"about:", (boolean)false, (int)2, null)) return "";
            if (!StringsKt.startsWith$default((String)string, (String)"http://", (boolean)false, (int)2, null) && !StringsKt.startsWith$default((String)string, (String)"https://", (boolean)false, (int)2, null)) {
                charSequence = new StringBuilder();
                string = ((StringBuilder)charSequence).append("https://").append(string).toString();
            }
            charSequence = Uri.parse((String)string).getHost();
            string = charSequence;
            if (charSequence == null) {
                string = "";
            }
            charSequence = string;
        }
        catch (Exception exception) {
            return string2;
        }
        if (StringsKt.startsWith$default((String)string, (String)"www.", (boolean)false, (int)2, null)) {
            charSequence = string.substring(4);
            Intrinsics.checkNotNullExpressionValue((Object)charSequence, (String)"substring(...)");
        }
        string = ((String)charSequence).toLowerCase(Locale.ROOT);
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toLowerCase(...)");
        return string;
    }

    public final Bitmap getFromMemory(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"domain");
        if (StringsKt.isBlank((CharSequence)string)) {
            return null;
        }
        return (Bitmap)memoryCache.get((Object)string);
    }

    public final Object loadFavicon(Context context, String string, Continuation<? super Bitmap> continuation) {
        return BuildersKt.withContext((CoroutineContext)((CoroutineContext)Dispatchers.getIO()), (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Bitmap>, Object>(string, context, null){
            final Context $context;
            final String $domain;
            int label;
            {
                this.$domain = string;
                this.$context = context;
                super(2, continuation);
            }

            public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                return (Continuation)new /* invalid duplicate definition of identical inner class */;
            }

            public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Bitmap> continuation) {
                return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
            }

            /*
             * Unable to fully structure code
             * Enabled aggressive block sorting
             * Enabled unnecessary exception pruning
             * Enabled aggressive exception aggregation
             */
            public final Object invokeSuspend(Object var1_1) {
                block28: {
                    IntrinsicsKt.getCOROUTINE_SUSPENDED();
                    switch (this.label) {
                        default: {
                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                        }
                        case 0: 
                    }
                    ResultKt.throwOnFailure((Object)var1_1);
                    if (StringsKt.isBlank((CharSequence)this.$domain)) {
                        return null;
                    }
                    if (Intrinsics.areEqual((Object)FaviconManager.access$getFailedDomains$p().get((Object)this.$domain), (Object)Boxing.boxBoolean((boolean)true))) {
                        return null;
                    }
                    var1_1 = (Bitmap)FaviconManager.access$getMemoryCache$p().get((Object)this.$domain);
                    if (var1_1 != null) {
                        return var1_1;
                    }
                    var3_5 = FaviconManager.access$getDiskCacheFile(FaviconManager.INSTANCE, this.$context, this.$domain);
                    if (var3_5 != null && var3_5.exists() && var3_5.length() > 0L) {
                        try {
                            var1_1 = BitmapFactory.decodeFile((String)var3_5.getAbsolutePath());
                            if (var1_1 != null) {
                                FaviconManager.access$getMemoryCache$p().put((Object)this.$domain, var1_1);
                                return var1_1;
                            }
                        }
                        catch (Exception var1_2) {
                            Boxing.boxBoolean((boolean)var3_5.delete());
                        }
                    }
                    var4_8 = this.$domain;
                    var1_1 = new StringBuilder();
                    var1_1 = var1_1.append("https://www.google.com/s2/favicons?domain=").append((String)var4_8).append("&sz=128").toString();
                    var4_8 = new URL((String)var1_1);
                    var1_1 = var4_8.openConnection();
                    if (var1_1 instanceof HttpURLConnection == false) return null;
                    var1_1 = (HttpURLConnection)var1_1;
                    if (var1_1 == null) {
                        return null;
                    }
                    var1_1.setConnectTimeout(3500);
                    var1_1.setReadTimeout(4000);
                    var1_1.setInstanceFollowRedirects(true);
                    var1_1.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)");
                    if (var1_1.getResponseCode() != 200) ** GOTO lbl87
                    var5_10 = var1_1.getInputStream();
                    var4_8 = (InputStream)var5_10;
                    Intrinsics.checkNotNull((Object)var4_8);
                    var4_8 = ByteStreamsKt.readBytes((InputStream)var4_8);
                    CloseableKt.closeFinally((Closeable)var5_10, null);
                    var1_1.disconnect();
                    var2_12 = ((Object)var4_8).length == 0;
                    if (var2_12 || (var1_1 = BitmapFactory.decodeByteArray((byte[])var4_8, (int)0, (int)((Object)var4_8).length)) == null) break block28;
                    FaviconManager.access$getMemoryCache$p().put((Object)this.$domain, var1_1);
                    if (var3_5 == null) return var1_1;
                    var5_10 = var3_5.getParentFile();
                    if (var5_10 != null) {
                        Boxing.boxBoolean((boolean)var5_10.mkdirs());
                    }
                    var5_10 = new FileOutputStream((File)var3_5);
                    var3_5 = (Closeable)var5_10;
                    var5_10 = (FileOutputStream)var3_5;
                    var5_10.write((byte[])var4_8);
                    var5_10.flush();
                    var4_8 = Unit.INSTANCE;
                    CloseableKt.closeFinally((Closeable)var3_5, null);
                    return var1_1;
                    catch (Throwable var5_11) {
                        try {
                            throw var5_11;
                        }
                        catch (Throwable var4_9) {
                            try {
                                CloseableKt.closeFinally((Closeable)var3_5, (Throwable)var5_11);
                                throw var4_9;
                            }
                            catch (Exception var3_6) {
                                // empty catch block
                            }
                        }
                    }
                    return var1_1;
                    catch (Throwable var1_3) {
                        try {
                            throw var1_3;
                        }
                        catch (Throwable var3_7) {
                            try {
                                CloseableKt.closeFinally((Closeable)var5_10, (Throwable)var1_3);
                                throw var3_7;
lbl87:
                                // 1 sources

                                var1_1.disconnect();
                            }
                            catch (Exception var1_4) {
                                // empty catch block
                            }
                        }
                    }
                }
                FaviconManager.access$getFailedDomains$p().put((Object)this.$domain, (Object)Boxing.boxBoolean((boolean)true));
                return null;
            }
        }), continuation);
    }
}

