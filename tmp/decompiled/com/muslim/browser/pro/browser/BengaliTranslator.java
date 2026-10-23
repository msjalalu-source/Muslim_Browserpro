/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  android.util.Log
 *  kotlin.Metadata
 *  kotlin.NoWhenBranchMatchedException
 *  kotlin.Result
 *  kotlin.ResultKt
 *  kotlin.Unit
 *  kotlin.collections.CollectionsKt
 *  kotlin.collections.SetsKt
 *  kotlin.coroutines.Continuation
 *  kotlin.coroutines.CoroutineContext
 *  kotlin.coroutines.intrinsics.IntrinsicsKt
 *  kotlin.coroutines.jvm.internal.Boxing
 *  kotlin.coroutines.jvm.internal.ContinuationImpl
 *  kotlin.coroutines.jvm.internal.SpillingKt
 *  kotlin.jvm.functions.Function1
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.Ref$IntRef
 *  kotlin.jvm.internal.Ref$ObjectRef
 *  kotlin.text.CharsKt
 *  kotlin.text.Regex
 *  kotlin.text.StringsKt
 *  kotlinx.coroutines.AwaitKt
 *  kotlinx.coroutines.BuildersKt
 *  kotlinx.coroutines.CompletableDeferred
 *  kotlinx.coroutines.CompletableDeferredKt
 *  kotlinx.coroutines.CoroutineScope
 *  kotlinx.coroutines.CoroutineScopeKt
 *  kotlinx.coroutines.Deferred
 *  kotlinx.coroutines.DelayKt
 *  kotlinx.coroutines.Dispatchers
 *  kotlinx.coroutines.sync.Mutex
 *  kotlinx.coroutines.sync.MutexKt
 *  kotlinx.coroutines.sync.Semaphore
 *  kotlinx.coroutines.sync.SemaphoreKt
 *  org.json.JSONArray
 */
package com.muslim.browser.pro.browser;

import android.util.Log;
import com.muslim.browser.pro.browser.BengaliTranslator;
import com.muslim.browser.pro.browser.TranslationEngine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import kotlin.Metadata;
import kotlin.NoWhenBranchMatchedException;
import kotlin.Result;
import kotlin.ResultKt;
import kotlin.Unit;
import kotlin.collections.CollectionsKt;
import kotlin.collections.SetsKt;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.intrinsics.IntrinsicsKt;
import kotlin.coroutines.jvm.internal.Boxing;
import kotlin.coroutines.jvm.internal.ContinuationImpl;
import kotlin.coroutines.jvm.internal.SpillingKt;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.Ref;
import kotlin.text.CharsKt;
import kotlin.text.Regex;
import kotlin.text.StringsKt;
import kotlinx.coroutines.AwaitKt;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CompletableDeferred;
import kotlinx.coroutines.CompletableDeferredKt;
import kotlinx.coroutines.CoroutineScope;
import kotlinx.coroutines.CoroutineScopeKt;
import kotlinx.coroutines.Deferred;
import kotlinx.coroutines.DelayKt;
import kotlinx.coroutines.Dispatchers;
import kotlinx.coroutines.sync.Mutex;
import kotlinx.coroutines.sync.MutexKt;
import kotlinx.coroutines.sync.Semaphore;
import kotlinx.coroutines.sync.SemaphoreKt;
import org.json.JSONArray;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(d1={"\u0000p\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\b\n\u0002\u0010\b\n\u0002\b\u0005\n\u0002\u0010\t\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0010\u0002\n\u0002\b\u0007\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0002\b\t\n\u0002\u0010 \n\u0002\b\u001c\b\u00c7\u0002\u0018\u00002\u00020\u0001:\u0002YZB\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010$\u001a\u00020%J\u0006\u0010&\u001a\u00020\u000eJ\u0016\u0010'\u001a\u00020%2\u0006\u0010(\u001a\u00020\u00052\u0006\u0010)\u001a\u00020\u0005J\u0010\u0010*\u001a\u0004\u0018\u00010\u00052\u0006\u0010(\u001a\u00020\u0005J*\u0010+\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-2\u0006\u0010.\u001a\u00020\u00052\b\b\u0002\u0010/\u001a\u00020\u00052\b\b\u0002\u00100\u001a\u00020\u0005J\u0015\u00104\u001a\u0002052\u0006\u0010.\u001a\u00020\u0005H\u0000\u00a2\u0006\u0002\b6J\u0015\u00107\u001a\u0002052\u0006\u0010.\u001a\u00020\u0005H\u0000\u00a2\u0006\u0002\b8J!\u00109\u001a\u00020\u00052\u0006\u0010.\u001a\u00020\u00052\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\u0005H\u0000\u00a2\u0006\u0002\b:J\u000e\u0010;\u001a\u00020%H\u0082@\u00a2\u0006\u0002\u0010<J\u000e\u0010=\u001a\u0002052\u0006\u0010.\u001a\u00020\u0005J \u0010>\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050?0?2\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00050?J2\u0010A\u001a\b\u0012\u0004\u0012\u00020\u00050\u001d2\u0006\u0010.\u001a\u00020\u00052\b\b\u0002\u0010,\u001a\u00020-2\b\b\u0002\u0010/\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0004\bB\u0010CJ0\u0010D\u001a\b\u0012\u0004\u0012\u00020\u00050\u001d2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-2\b\b\u0002\u0010/\u001a\u00020\u0005H\u0086@\u00a2\u0006\u0004\bE\u0010CJ4\u0010F\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050?0\u001d2\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00050?2\b\b\u0002\u0010,\u001a\u00020-H\u0086@\u00a2\u0006\u0004\bG\u0010HJ0\u0010I\u001a\b\u0012\u0004\u0012\u00020\u00050\u001d2\u0006\u0010.\u001a\u00020\u00052\u0006\u0010,\u001a\u00020-2\b\b\u0002\u0010/\u001a\u00020\u0005H\u0082@\u00a2\u0006\u0004\bJ\u0010CJ\u001e\u0010K\u001a\b\u0012\u0004\u0012\u00020\u00050\u001d2\u0006\u0010.\u001a\u00020\u0005H\u0080@\u00a2\u0006\u0004\bL\u0010MJ*\u0010N\u001a\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050?0\u001d2\f\u0010@\u001a\b\u0012\u0004\u0012\u00020\u00050?H\u0080@\u00a2\u0006\u0004\bO\u0010PJ)\u0010Q\u001a\b\u0012\u0004\u0012\u00020\u00050\u001d2\u0006\u0010.\u001a\u00020\u00052\n\b\u0002\u0010/\u001a\u0004\u0018\u00010\u0005H\u0000\u00a2\u0006\u0004\bR\u0010SJ\u000e\u0010T\u001a\u00020\u00052\u0006\u0010U\u001a\u00020\u0005J\u0006\u0010V\u001a\u00020\u0005J\u0014\u0010W\u001a\u00020\u00052\f\u0010X\u001a\b\u0012\u0004\u0012\u00020\u00050?R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0007\u001a\u00020\u0005X\u0086T\u00a2\u0006\u0002\n\u0000R\u001a\u0010\b\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u000e\u0010\r\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0010\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0012\u001a\u00020\u000eX\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082T\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0015\u001a\u00020\u0016X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0014X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0018\u001a\u00020\u0019X\u0082\u0004\u00a2\u0006\u0002\n\u0000R&\u0010\u001a\u001a\u001a\u0012\u0004\u0012\u00020\u0005\u0012\u0010\u0012\u000e\u0012\n\u0012\b\u0012\u0004\u0012\u00020\u00050\u001d0\u001c0\u001bX\u0082\u0004\u00a2\u0006\u0002\n\u0000R(\u0010\u001e\u001a\u0010\u0012\u0004\u0012\u00020\u0005\u0012\u0004\u0012\u00020\u0005\u0018\u00010\u001fX\u0080\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b \u0010!\"\u0004\b\"\u0010#R\u001e\u00101\u001a\u0012\u0012\u0004\u0012\u00020\u000502j\b\u0012\u0004\u0012\u00020\u0005`3X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006["}, d2={"Lcom/muslim/browser/pro/browser/BengaliTranslator;", "", "<init>", "()V", "TAG", "", "TARGET_LANGUAGE", "LIBRE_TRANSLATE_URL", "myMemoryBaseUrl", "getMyMemoryBaseUrl", "()Ljava/lang/String;", "setMyMemoryBaseUrl", "(Ljava/lang/String;)V", "CONNECT_TIMEOUT_MS", "", "READ_TIMEOUT_MS", "MAX_CONCURRENT_REQUESTS", "MAX_BATCH_CHARS", "MAX_BATCH_ITEMS", "MIN_DISPATCH_INTERVAL_MS", "", "dispatchMutex", "Lkotlinx/coroutines/sync/Mutex;", "lastDispatchTimeMs", "lruCache", "Lcom/muslim/browser/pro/browser/BengaliTranslator$TranslationLruCache;", "inFlightRequests", "Ljava/util/concurrent/ConcurrentHashMap;", "Lkotlinx/coroutines/CompletableDeferred;", "Lkotlin/Result;", "testTranslatorOverride", "Lkotlin/Function1;", "getTestTranslatorOverride$app", "()Lkotlin/jvm/functions/Function1;", "setTestTranslatorOverride$app", "(Lkotlin/jvm/functions/Function1;)V", "clearCache", "", "getCacheSize", "putInCache", "key", "value", "getFromCache", "cacheKey", "engine", "Lcom/muslim/browser/pro/browser/TranslationEngine;", "text", "sourceLang", "targetLang", "ENGLISH_INDICATORS", "Ljava/util/HashSet;", "Lkotlin/collections/HashSet;", "isLikelyEnglish", "", "isLikelyEnglish$app", "isAsciiEnglish", "isAsciiEnglish$app", "resolveLanguagePair", "resolveLanguagePair$app", "awaitDispatchSlot", "(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "shouldSkipText", "chunkIntoBatches", "", "texts", "translate", "translate-BWLJW6A", "(Ljava/lang/String;Lcom/muslim/browser/pro/browser/TranslationEngine;Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateSingleTextCoalesced", "translateSingleTextCoalesced-BWLJW6A", "translateBatch", "translateBatch-0E7RQCE", "(Ljava/util/List;Lcom/muslim/browser/pro/browser/TranslationEngine;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateSingleText", "translateSingleText-BWLJW6A", "translateWithLibreTranslate", "translateWithLibreTranslate-gIAlu-s$app", "(Ljava/lang/String;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateBatchWithLibreTranslate", "translateBatchWithLibreTranslate-gIAlu-s$app", "(Ljava/util/List;Lkotlin/coroutines/Continuation;)Ljava/lang/Object;", "translateWithMyMemory", "translateWithMyMemory-gIAlu-s$app", "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", "decodeHtmlEntities", "input", "buildExtractScript", "buildReplaceScript", "translations", "TranslationLruCache", "TranslationStats", "app"}, k=1, mv={2, 2, 0}, xi=48)
public final class BengaliTranslator {
    public static final int $stable;
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final HashSet<String> ENGLISH_INDICATORS;
    public static final BengaliTranslator INSTANCE;
    public static final String LIBRE_TRANSLATE_URL = "https://translate.disroot.org/translate";
    private static final int MAX_BATCH_CHARS = 2500;
    private static final int MAX_BATCH_ITEMS = 25;
    private static final int MAX_CONCURRENT_REQUESTS = 4;
    private static final long MIN_DISPATCH_INTERVAL_MS = 600L;
    private static final int READ_TIMEOUT_MS = 7000;
    private static final String TAG = "BengaliTranslator";
    public static final String TARGET_LANGUAGE = "bn";
    private static final Mutex dispatchMutex;
    private static final ConcurrentHashMap<String, CompletableDeferred<Result<String>>> inFlightRequests;
    private static long lastDispatchTimeMs;
    private static final TranslationLruCache lruCache;
    private static volatile String myMemoryBaseUrl;
    private static Function1<? super String, String> testTranslatorOverride;

    static {
        INSTANCE = new BengaliTranslator();
        myMemoryBaseUrl = "https://api.mymemory.translated.net";
        dispatchMutex = MutexKt.Mutex$default((boolean)false, (int)1, null);
        lruCache = new TranslationLruCache(500);
        inFlightRequests = new ConcurrentHashMap();
        try {
            System.setProperty("http.keepAlive", "true");
            System.setProperty("http.maxConnections", "10");
        }
        catch (Exception exception) {
            // empty catch block
        }
        ENGLISH_INDICATORS = SetsKt.hashSetOf((Object[])new String[]{"the", "be", "to", "of", "and", "a", "in", "that", "have", "it", "for", "not", "on", "with", "he", "as", "you", "do", "at", "this", "but", "his", "by", "from", "they", "we", "say", "her", "she", "or", "an", "will", "my", "one", "all", "would", "there", "their", "what", "so", "up", "out", "if", "about", "who", "get", "which", "go", "me", "when", "make", "can", "like", "time", "no", "just", "him", "know", "take", "people", "into", "year", "your", "good", "some", "could", "them", "see", "other", "than", "then", "now", "look", "only", "come", "its", "over", "think", "also", "back", "after", "use", "two", "how", "our", "work", "first", "well", "way", "even", "new", "want", "because", "any", "these", "give", "day", "most", "us", "is", "are", "was", "were", "been", "has", "had", "am", "welcome", "news", "read", "more", "share", "home", "search", "settings", "menu", "page", "next", "previous", "view", "click", "here", "today", "world", "privacy", "terms", "policy", "login", "sign", "in", "up", "download", "save", "cancel", "ok", "yes", "close", "open", "help", "about", "contact", "article", "post", "comment"});
        $stable = 8;
    }

    private BengaliTranslator() {
    }

    public static final /* synthetic */ Object access$awaitDispatchSlot(BengaliTranslator bengaliTranslator, Continuation continuation) {
        return bengaliTranslator.awaitDispatchSlot((Continuation<? super Unit>)continuation);
    }

    public static final /* synthetic */ TranslationLruCache access$getLruCache$p() {
        return lruCache;
    }

    public static final /* synthetic */ Object access$translateSingleText-BWLJW6A(BengaliTranslator bengaliTranslator, String string, TranslationEngine translationEngine, String string2, Continuation continuation) {
        return bengaliTranslator.translateSingleText-BWLJW6A(string, translationEngine, string2, (Continuation<? super Result<String>>)continuation);
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private final Object awaitDispatchSlot(Continuation<? super Unit> var1_1) {
        block13: {
            if (!(var1_1 instanceof awaitDispatchSlot.1)) ** GOTO lbl-1000
            var11_3 = var1_1;
            if ((var11_3.label & -2147483648) != 0) {
                var11_3.label += -2147483648;
            } else lbl-1000:
            // 2 sources

            {
                var11_3 = new ContinuationImpl(this, (Continuation<? super awaitDispatchSlot.1>)var1_1){
                    int I$0;
                    int I$1;
                    long J$0;
                    long J$1;
                    long J$2;
                    Object L$0;
                    int label;
                    Object result;
                    final BengaliTranslator this$0;
                    {
                        this.this$0 = bengaliTranslator;
                        super(continuation);
                    }

                    public final Object invokeSuspend(Object object) {
                        this.result = object;
                        this.label |= Integer.MIN_VALUE;
                        return BengaliTranslator.access$awaitDispatchSlot(this.this$0, (Continuation)this);
                    }
                };
            }
            var10_4 = var11_3.result;
            var12_5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
            switch (var11_3.label) {
                default: {
                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                }
                case 2: {
                    var3_6 = var11_3.J$2;
                    var3_6 = var11_3.J$1;
                    var3_6 = var11_3.J$0;
                    var2_8 = var11_3.I$1;
                    var2_8 = var11_3.I$0;
                    var9_10 /* !! */  = var1_1 = (Mutex)var11_3.L$0;
                    try {
                        ResultKt.throwOnFailure((Object)var10_4);
                        ** GOTO lbl62
                    }
                    catch (Throwable var1_2) {
                        break block13;
                    }
                }
                case 1: {
                    var2_9 = var11_3.I$0;
                    var1_1 = (Mutex)var11_3.L$0;
                    ResultKt.throwOnFailure((Object)var10_4);
                    break;
                }
                case 0: {
                    ResultKt.throwOnFailure((Object)var10_4);
                    var1_1 = BengaliTranslator.dispatchMutex;
                    var11_3.L$0 = var1_1;
                    var11_3.I$0 = 0;
                    var11_3.label = 1;
                    if (var1_1.lock(null, (Continuation)var11_3) == var12_5) {
                        return var12_5;
                    }
                    var2_9 = 0;
                }
            }
            var9_10 /* !! */  = var1_1;
            {
                var3_7 = System.currentTimeMillis();
                var9_10 /* !! */  = var1_1;
                var7_11 = var3_7 - BengaliTranslator.lastDispatchTimeMs;
                var10_4 = var1_1;
                if (var7_11 >= 600L) ** GOTO lbl63
                var5_12 = 600L - var7_11;
                var9_10 /* !! */  = var1_1;
            }
            {
                var11_3.L$0 = var1_1;
                var9_10 /* !! */  = var1_1;
                var11_3.I$0 = var2_9;
                var9_10 /* !! */  = var1_1;
                var11_3.I$1 = 0;
                var9_10 /* !! */  = var1_1;
                var11_3.J$0 = var3_7;
                var9_10 /* !! */  = var1_1;
                var11_3.J$1 = var7_11;
                var9_10 /* !! */  = var1_1;
                var11_3.J$2 = var5_12;
                var9_10 /* !! */  = var1_1;
                var11_3.label = 2;
                var9_10 /* !! */  = var1_1;
                if (DelayKt.delay((long)var5_12, (Continuation)var11_3) == var12_5) {
                    return var12_5;
                }
lbl62:
                // 3 sources

                var10_4 = var1_1;
lbl63:
                // 2 sources

                var9_10 /* !! */  = var10_4;
                BengaliTranslator.lastDispatchTimeMs = System.currentTimeMillis();
                var9_10 /* !! */  = var10_4;
                var1_1 = Unit.INSTANCE;
            }
            var10_4.unlock(null);
            return Unit.INSTANCE;
        }
        var9_10 /* !! */ .unlock(null);
        throw var1_2;
    }

    public static /* synthetic */ String cacheKey$default(BengaliTranslator bengaliTranslator, TranslationEngine translationEngine, String string, String string2, String string3, int n, Object object) {
        if ((n & 4) != 0) {
            string2 = "auto";
        }
        if ((n & 8) != 0) {
            string3 = TARGET_LANGUAGE;
        }
        return bengaliTranslator.cacheKey(translationEngine, string, string2, string3);
    }

    public static /* synthetic */ String resolveLanguagePair$app$default(BengaliTranslator bengaliTranslator, String string, String string2, int n, Object object) {
        if ((n & 2) != 0) {
            string2 = null;
        }
        return bengaliTranslator.resolveLanguagePair$app(string, string2);
    }

    public static /* synthetic */ Object translate-BWLJW6A$default(BengaliTranslator bengaliTranslator, String string, TranslationEngine translationEngine, String string2, Continuation continuation, int n, Object object) {
        if ((n & 2) != 0) {
            translationEngine = TranslationEngine.LIBRE_TRANSLATE;
        }
        if ((n & 4) != 0) {
            string2 = "auto";
        }
        return bengaliTranslator.translate-BWLJW6A(string, translationEngine, string2, (Continuation<? super Result<String>>)continuation);
    }

    public static /* synthetic */ Object translateBatch-0E7RQCE$default(BengaliTranslator bengaliTranslator, List list, TranslationEngine translationEngine, Continuation continuation, int n, Object object) {
        if ((n & 2) != 0) {
            translationEngine = TranslationEngine.LIBRE_TRANSLATE;
        }
        return bengaliTranslator.translateBatch-0E7RQCE(list, translationEngine, (Continuation<? super Result<? extends List<String>>>)continuation);
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    private final Object translateSingleText-BWLJW6A(String var1_1, TranslationEngine var2_3, String var3_4, Continuation<? super Result<String>> var4_5) {
        block19: {
            block18: {
                if (!(var4_5 /* !! */  instanceof translateSingleText.1)) ** GOTO lbl-1000
                var10_6 /* !! */  = var4_5 /* !! */ ;
                if ((var10_6 /* !! */ .label & -2147483648) != 0) {
                    var10_6 /* !! */ .label += -2147483648;
                } else lbl-1000:
                // 2 sources

                {
                    var10_6 /* !! */  = new ContinuationImpl(this, var4_5 /* !! */ ){
                        long J$0;
                        Object L$0;
                        Object L$1;
                        Object L$2;
                        Object L$3;
                        int label;
                        Object result;
                        final BengaliTranslator this$0;
                        {
                            this.this$0 = bengaliTranslator;
                            super(continuation);
                        }

                        public final Object invokeSuspend(Object object) {
                            this.result = object;
                            this.label |= Integer.MIN_VALUE;
                            object = BengaliTranslator.access$translateSingleText-BWLJW6A(this.this$0, null, null, null, (Continuation)this);
                            if (object == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                                return object;
                            }
                            return Result.box-impl((Object)object);
                        }
                    };
                }
                var12_7 = var10_6 /* !! */ .result;
                var11_8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                switch (var10_6 /* !! */ .label) {
                    default: {
                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                    }
                    case 2: {
                        var6_9 = var10_6 /* !! */ .J$0;
                        var4_5 /* !! */  = (String)var10_6 /* !! */ .L$3;
                        var3_4 = (String)var10_6 /* !! */ .L$2;
                        var1_1 /* !! */  = (TranslationEngine)var10_6 /* !! */ .L$1;
                        var2_3 = (String)var10_6 /* !! */ .L$0;
                        ResultKt.throwOnFailure((Object)var12_7);
                        var1_1 /* !! */  = ((Result)var12_7).unbox-impl();
                        break block18;
                    }
                    case 1: {
                        var4_5 /* !! */  = (String)var10_6 /* !! */ .L$3;
                        var3_4 = (String)var10_6 /* !! */ .L$2;
                        var2_3 = (TranslationEngine)var10_6 /* !! */ .L$1;
                        var1_1 /* !! */  = (String)var10_6 /* !! */ .L$0;
                        ResultKt.throwOnFailure((Object)var12_7);
                        break;
                    }
                    case 0: {
                        ResultKt.throwOnFailure((Object)var12_7);
                        var4_5 /* !! */  = StringsKt.trim((CharSequence)var1_1 /* !! */ ).toString();
                        var5_10 = ((CharSequence)var4_5 /* !! */ ).length() == 0 ? 1 : 0;
                        if (var5_10 == 0 && !this.shouldSkipText((String)var4_5 /* !! */ )) {
                            var12_7 = BengaliTranslator.testTranslatorOverride;
                            if (var12_7 != null) {
                                try {
                                    var1_1 /* !! */  = Result.Companion;
                                    var1_1 /* !! */  = Result.constructor-impl((Object)var12_7.invoke(var4_5 /* !! */ ));
                                }
                                catch (Exception var1_2) {
                                    var2_3 = Result.Companion;
                                    var1_1 /* !! */  = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)var1_2));
                                }
                                return var1_1 /* !! */ ;
                            }
                            if (var2_3 != TranslationEngine.LIBRE_TRANSLATE) break;
                            var10_6 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1 /* !! */ );
                            var10_6 /* !! */ .L$1 = var2_3;
                            var10_6 /* !! */ .L$2 = var3_4;
                            var10_6 /* !! */ .L$3 = var4_5 /* !! */ ;
                            var10_6 /* !! */ .label = 1;
                            if (this.awaitDispatchSlot((Continuation<? super Unit>)var10_6 /* !! */ ) != var11_8) break;
                            return var11_8;
                        }
                        break block19;
                    }
                }
                var6_9 = System.currentTimeMillis();
                var5_10 = TranslationStats.INSTANCE.getTotalRequests();
                TranslationStats.INSTANCE.setTotalRequests(var5_10 + 1);
                switch (WhenMappings.$EnumSwitchMapping$0[var2_3.ordinal()]) {
                    default: {
                        throw new NoWhenBranchMatchedException();
                    }
                    case 2: {
                        var1_1 /* !! */  = this.translateWithMyMemory-gIAlu-s$app((String)var4_5 /* !! */ , var3_4);
                        break;
                    }
                    case 1: {
                        var10_6 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1 /* !! */ );
                        var10_6 /* !! */ .L$1 = SpillingKt.nullOutSpilledVariable((Object)var2_3);
                        var10_6 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable((Object)var3_4);
                        var10_6 /* !! */ .L$3 = SpillingKt.nullOutSpilledVariable(var4_5 /* !! */ );
                        var10_6 /* !! */ .J$0 = var6_9;
                        var10_6 /* !! */ .label = 2;
                        var10_6 /* !! */  = this.translateWithLibreTranslate-gIAlu-s$app((String)var4_5 /* !! */ , (Continuation<? super Result<String>>)var10_6 /* !! */ );
                        if (var10_6 /* !! */  == var11_8) {
                            return var11_8;
                        }
                        var2_3 = var1_1 /* !! */ ;
                        var1_1 /* !! */  = var10_6 /* !! */ ;
                    }
                }
            }
            var8_11 = System.currentTimeMillis();
            TranslationStats.INSTANCE.getRequestDurations().add(Boxing.boxLong((long)(var8_11 - var6_9)));
            if (Result.isSuccess-impl((Object)var1_1 /* !! */ )) {
                var5_10 = TranslationStats.INSTANCE.getSuccessfulRequests();
                TranslationStats.INSTANCE.setSuccessfulRequests(var5_10 + 1);
            } else {
                var5_10 = TranslationStats.INSTANCE.getFailedRequests();
                TranslationStats.INSTANCE.setFailedRequests(var5_10 + 1);
            }
            return var1_1 /* !! */ ;
        }
        var2_3 = Result.Companion;
        return Result.constructor-impl((Object)var1_1 /* !! */ );
    }

    static /* synthetic */ Object translateSingleText-BWLJW6A$default(BengaliTranslator bengaliTranslator, String string, TranslationEngine translationEngine, String string2, Continuation continuation, int n, Object object) {
        if ((n & 4) != 0) {
            string2 = "auto";
        }
        return bengaliTranslator.translateSingleText-BWLJW6A(string, translationEngine, string2, (Continuation<? super Result<String>>)continuation);
    }

    public static /* synthetic */ Object translateSingleTextCoalesced-BWLJW6A$default(BengaliTranslator bengaliTranslator, String string, TranslationEngine translationEngine, String string2, Continuation continuation, int n, Object object) {
        if ((n & 4) != 0) {
            string2 = "auto";
        }
        return bengaliTranslator.translateSingleTextCoalesced-BWLJW6A(string, translationEngine, string2, (Continuation<? super Result<String>>)continuation);
    }

    public static /* synthetic */ Object translateWithMyMemory-gIAlu-s$app$default(BengaliTranslator bengaliTranslator, String string, String string2, int n, Object object) {
        if ((n & 2) != 0) {
            string2 = null;
        }
        return bengaliTranslator.translateWithMyMemory-gIAlu-s$app(string, string2);
    }

    public final String buildExtractScript() {
        return "(function() {\n    try {\n        var root = document.body || document.documentElement;\n        if (!root) return JSON.stringify({error: \"No body\"});\n        var walker = document.createTreeWalker(\n            root,\n            NodeFilter.SHOW_TEXT,\n            {\n                acceptNode: function(node) {\n                    if (!node || !node.nodeValue) return NodeFilter.FILTER_REJECT;\n                    var val = node.nodeValue.trim();\n                    if (val.length < 2) return NodeFilter.FILTER_REJECT;\n                    var p = node.parentElement;\n                    if (!p) return NodeFilter.FILTER_REJECT;\n                    var t = p.tagName ? p.tagName.toUpperCase() : \"\";\n                    if (t === 'SCRIPT' || t === 'STYLE' || t === 'NOSCRIPT' || \n                        t === 'CODE' || t === 'PRE' || t === 'INPUT' || \n                        t === 'TEXTAREA' || t === 'SELECT' || t === 'SVG' || \n                        t === 'CANVAS' || t === 'AUDIO' || t === 'VIDEO') {\n                        return NodeFilter.FILTER_REJECT;\n                    }\n                    if (p.offsetParent === null && t !== 'BODY' && t !== 'HTML') {\n                        return NodeFilter.FILTER_REJECT;\n                    }\n                    return NodeFilter.FILTER_ACCEPT;\n                }\n            },\n            false\n        );\n        window.__bn_nodes = [];\n        var texts = [];\n        var n;\n        var count = 0;\n        while ((n = walker.nextNode()) && count < 250) {\n            var orig = n.nodeValue;\n            var trimmed = orig.trim();\n            if (trimmed.length > 1) {\n                window.__bn_nodes.push({node: n, orig: orig});\n                texts.push(trimmed);\n                count++;\n            }\n        }\n        return JSON.stringify({texts: texts});\n    } catch(e) {\n        return JSON.stringify({error: e.toString()});\n    }\n})();";
    }

    public final String buildReplaceScript(List<String> object) {
        Intrinsics.checkNotNullParameter(object, (String)"translations");
        object = new JSONArray((Collection)object).toString();
        Intrinsics.checkNotNullExpressionValue((Object)object, (String)"toString(...)");
        return StringsKt.trimIndent((String)("\n            (function(trans) {\n                try {\n                    if (!window.__bn_nodes || !trans) return \"no_nodes\";\n                    for (var i = 0; i < window.__bn_nodes.length && i < trans.length; i++) {\n                        var item = window.__bn_nodes[i];\n                        var t = trans[i];\n                        if (t && item.node && item.node.parentNode) {\n                            var orig = item.orig;\n                            var leading = (orig.match(/^\\s*/) || [\"\"])[0];\n                            var trailing = (orig.match(/\\s*$/) || [\"\"])[0];\n                            item.node.nodeValue = leading + t + trailing;\n                        }\n                    }\n                    return \"success\";\n                } catch(e) {\n                    return \"error: \" + e.toString();\n                }\n            })(" + (String)object + ");\n        "));
    }

    public final String cacheKey(TranslationEngine object, String string, String string2, String string3) {
        Intrinsics.checkNotNullParameter((Object)object, (String)"engine");
        Intrinsics.checkNotNullParameter((Object)string, (String)"text");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"sourceLang");
        Intrinsics.checkNotNullParameter((Object)string3, (String)"targetLang");
        string = ((Object)StringsKt.trim((CharSequence)string)).toString();
        object = object.name();
        return (String)object + ":" + string2 + ":" + string3 + ":" + string;
    }

    public final List<List<String>> chunkIntoBatches(List<String> list) {
        Intrinsics.checkNotNullParameter(list, (String)"texts");
        ArrayList<List<String>> arrayList = new ArrayList<List<String>>();
        List<String> list2 = new ArrayList<String>();
        int n = 0;
        Iterator<String> iterator = list.iterator();
        list = list2;
        while (iterator.hasNext()) {
            int n2;
            int n3;
            String string;
            block5: {
                block6: {
                    string = iterator.next();
                    n3 = string.length();
                    list2 = list;
                    n2 = n;
                    if (((Collection)list).isEmpty()) break block5;
                    if (n + n3 > 2500) break block6;
                    list2 = list;
                    n2 = n;
                    if (((ArrayList)list).size() < 25) break block5;
                }
                arrayList.add(list);
                list2 = new ArrayList();
                n2 = 0;
            }
            ((ArrayList)list2).add(string);
            n = n2 + n3;
            list = list2;
        }
        if (!((Collection)list).isEmpty()) {
            arrayList.add(list);
        }
        return arrayList;
    }

    public final void clearCache() {
        lruCache.clear();
        inFlightRequests.clear();
        TranslationStats.INSTANCE.reset();
    }

    public final String decodeHtmlEntities(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"input");
        if (!StringsKt.contains$default((CharSequence)string, (char)'&', (boolean)false, (int)2, null)) {
            return string;
        }
        return StringsKt.replace$default((String)StringsKt.replace$default((String)StringsKt.replace$default((String)StringsKt.replace$default((String)StringsKt.replace$default((String)StringsKt.replace$default((String)StringsKt.replace$default((String)string, (String)"&quot;", (String)"\"", (boolean)false, (int)4, null), (String)"&#39;", (String)"'", (boolean)false, (int)4, null), (String)"&apos;", (String)"'", (boolean)false, (int)4, null), (String)"&amp;", (String)"&", (boolean)false, (int)4, null), (String)"&lt;", (String)"<", (boolean)false, (int)4, null), (String)"&gt;", (String)">", (boolean)false, (int)4, null), (String)"&nbsp;", (String)" ", (boolean)false, (int)4, null);
    }

    public final int getCacheSize() {
        return lruCache.size();
    }

    public final String getFromCache(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"key");
        return lruCache.get(string);
    }

    public final String getMyMemoryBaseUrl() {
        return myMemoryBaseUrl;
    }

    public final Function1<String, String> getTestTranslatorOverride$app() {
        return testTranslatorOverride;
    }

    public final boolean isAsciiEnglish$app(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"text");
        return this.isLikelyEnglish$app(string);
    }

    public final boolean isLikelyEnglish$app(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"text");
        string = ((Object)StringsKt.trim((CharSequence)string)).toString();
        if (string.length() < 2) {
            return false;
        }
        boolean bl = false;
        int n = string.length();
        int n2 = 0;
        while (true) {
            boolean bl2 = true;
            if (n2 >= n) break;
            char c = string.charAt(n2);
            if (c >= '\u0080') {
                return false;
            }
            boolean bl3 = 'a' <= c && c < '{';
            if (bl3 || (bl3 = 'A' <= c && c < '[' ? bl2 : false)) {
                bl = true;
            }
            ++n2;
        }
        if (!bl) {
            return false;
        }
        if (!(StringsKt.startsWith((String)string, (String)"http://", (boolean)true) || StringsKt.startsWith((String)string, (String)"https://", (boolean)true) || StringsKt.startsWith((String)string, (String)"www.", (boolean)true) || StringsKt.contains$default((CharSequence)string, (CharSequence)"://", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"{", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"}", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"();", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"</", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"var ", (boolean)false, (int)2, null) || StringsKt.contains$default((CharSequence)string, (CharSequence)"function(", (boolean)false, (int)2, null))) {
            HashSet<String> hashSet = string;
            Iterator iterator = new Regex("[^a-zA-Z]+").split((CharSequence)((Object)hashSet), 0);
            hashSet = new ArrayList();
            iterator = iterator.iterator();
            while (iterator.hasNext()) {
                Object object = iterator.next();
                if (StringsKt.isBlank((CharSequence)((String)object))) continue;
                hashSet.add((String)object);
            }
            if ((hashSet = (List)((Object)hashSet)).isEmpty()) {
                return false;
            }
            for (String string2 : hashSet) {
                hashSet = ENGLISH_INDICATORS;
                String string3 = string2.toLowerCase(Locale.ROOT);
                Intrinsics.checkNotNullExpressionValue((Object)string3, (String)"toLowerCase(...)");
                if (!hashSet.contains(string3)) continue;
                return true;
            }
            return StringsKt.contains((CharSequence)string, (CharSequence)"'s", (boolean)true) || StringsKt.contains((CharSequence)string, (CharSequence)"'t", (boolean)true) || StringsKt.contains((CharSequence)string, (CharSequence)"'re", (boolean)true) || StringsKt.contains((CharSequence)string, (CharSequence)"'ve", (boolean)true) || StringsKt.contains((CharSequence)string, (CharSequence)"'ll", (boolean)true);
            {
            }
        }
        return false;
    }

    public final void putInCache(String string, String string2) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"key");
        Intrinsics.checkNotNullParameter((Object)string2, (String)"value");
        lruCache.put(string, string2);
    }

    public final String resolveLanguagePair$app(String string, String string2) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"text");
        CharSequence charSequence = string2;
        boolean bl = charSequence == null || StringsKt.isBlank((CharSequence)charSequence);
        if (!bl && !Intrinsics.areEqual((Object)string2, (Object)"auto")) {
            return string2 + "|bn";
        }
        string = this.isLikelyEnglish$app(string) ? "en|bn" : "autodetect|bn";
        return string;
    }

    public final void setMyMemoryBaseUrl(String string) {
        Intrinsics.checkNotNullParameter((Object)string, (String)"<set-?>");
        myMemoryBaseUrl = string;
    }

    public final void setTestTranslatorOverride$app(Function1<? super String, String> function1) {
        testTranslatorOverride = function1;
    }

    public final boolean shouldSkipText(String object) {
        int n;
        char c;
        int n2;
        CharSequence charSequence;
        block13: {
            block12: {
                Intrinsics.checkNotNullParameter((Object)object, (String)"text");
                object = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
                if (((String)object).length() < 2) {
                    return true;
                }
                charSequence = (CharSequence)object;
                for (n2 = 0; n2 < charSequence.length(); ++n2) {
                    c = charSequence.charAt(n2);
                    n = !(Character.isDigit(c) || CharsKt.isWhitespace((char)c) || StringsKt.contains$default((CharSequence)".,$%-+/:#()[]{}|\\@!?'\"", (char)c, (boolean)false, (int)2, null)) ? 0 : 1;
                    if (n != 0) continue;
                    n2 = 0;
                    break block12;
                }
                n2 = 1;
            }
            if (n2 != 0) {
                return true;
            }
            charSequence = (CharSequence)object;
            for (n2 = 0; n2 < charSequence.length(); ++n2) {
                if (!Character.isLetter(charSequence.charAt(n2))) continue;
                n2 = 0;
                break block13;
            }
            n2 = 1;
        }
        if (n2 != 0) {
            return true;
        }
        if (!(StringsKt.startsWith((String)object, (String)"http://", (boolean)true) || StringsKt.startsWith((String)object, (String)"https://", (boolean)true) || StringsKt.startsWith((String)object, (String)"www.", (boolean)true) || StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)"://", (boolean)false, (int)2, null))) {
            if (StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)"@", (boolean)false, (int)2, null) && StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)".", (boolean)false, (int)2, null) && !StringsKt.contains$default((CharSequence)((CharSequence)object), (CharSequence)" ", (boolean)false, (int)2, null)) {
                return true;
            }
            charSequence = (CharSequence)object;
            object = new StringBuilder();
            n = charSequence.length();
            for (n2 = 0; n2 < n; ++n2) {
                c = charSequence.charAt(n2);
                if (!Character.isLetter(c)) continue;
                object.append(c);
            }
            n2 = ((CharSequence)(object = ((StringBuilder)object).toString())).length() > 0 ? 1 : 0;
            if (n2 != 0) {
                charSequence = (CharSequence)object;
                n = 0;
                for (n2 = 0; n2 < charSequence.length(); ++n2) {
                    int n3 = charSequence.charAt(n2);
                    boolean bl = 2432 <= n3 && n3 < 2560;
                    n3 = n;
                    if (bl) {
                        n3 = n + 1;
                    }
                    n = n3;
                }
                if ((double)n / (double)((String)object).length() > 0.6) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public final Object translate-BWLJW6A(String var1_1, TranslationEngine var2_2, String var3_3, Continuation<? super Result<String>> var4_4) {
        if (!(var4_4 /* !! */  instanceof translate.1)) ** GOTO lbl-1000
        var5_5 /* !! */  = var4_4 /* !! */ ;
        if ((var5_5 /* !! */ .label & -2147483648) != 0) {
            var5_5 /* !! */ .label += -2147483648;
            var4_4 /* !! */  = var5_5 /* !! */ ;
        } else lbl-1000:
        // 2 sources

        {
            var4_4 /* !! */  = new ContinuationImpl(this, var4_4 /* !! */ ){
                Object L$0;
                Object L$1;
                Object L$2;
                int label;
                Object result;
                final BengaliTranslator this$0;
                {
                    this.this$0 = bengaliTranslator;
                    super(continuation);
                }

                public final Object invokeSuspend(Object object) {
                    this.result = object;
                    this.label |= Integer.MIN_VALUE;
                    object = this.this$0.translate-BWLJW6A(null, null, null, (Continuation<? super Result<String>>)((Continuation)this));
                    if (object == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                        return object;
                    }
                    return Result.box-impl((Object)object);
                }
            };
        }
        var5_5 /* !! */  = var4_4 /* !! */ .result;
        var6_6 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (var4_4 /* !! */ .label) {
            default: {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            case 1: {
                var1_1 /* !! */  = (String)var4_4 /* !! */ .L$2;
                var1_1 /* !! */  = (TranslationEngine)var4_4 /* !! */ .L$1;
                var1_1 /* !! */  = (String)var4_4 /* !! */ .L$0;
                ResultKt.throwOnFailure((Object)var5_5 /* !! */ );
                var1_1 /* !! */  = var5_5 /* !! */ ;
                break;
            }
            case 0: {
                ResultKt.throwOnFailure((Object)var5_5 /* !! */ );
                var7_7 = (CoroutineContext)Dispatchers.getIO();
                var5_5 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Result<? extends String>>, Object>(var1_1 /* !! */ , var2_2 /* !! */ , var3_3, null){
                    final TranslationEngine $engine;
                    final String $sourceLang;
                    final String $text;
                    Object L$0;
                    int label;
                    {
                        this.$text = string;
                        this.$engine = translationEngine;
                        this.$sourceLang = string2;
                        super(2, continuation);
                    }

                    public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                        return (Continuation)new /* invalid duplicate definition of identical inner class */;
                    }

                    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Result<String>> continuation) {
                        return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                    }

                    public final Object invokeSuspend(Object object) {
                        block5: {
                            Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                default: {
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                }
                                case 1: {
                                    object2 = (String)this.L$0;
                                    ResultKt.throwOnFailure((Object)object);
                                    object = ((Result)object).unbox-impl();
                                    break;
                                }
                                case 0: {
                                    ResultKt.throwOnFailure((Object)object);
                                    object = ((Object)StringsKt.trim((CharSequence)this.$text)).toString();
                                    boolean bl = ((CharSequence)object).length() == 0;
                                    if (!bl && !BengaliTranslator.INSTANCE.shouldSkipText((String)object)) {
                                        BengaliTranslator bengaliTranslator = BengaliTranslator.INSTANCE;
                                        TranslationEngine translationEngine = this.$engine;
                                        String string = this.$sourceLang;
                                        Continuation continuation = (Continuation)this;
                                        this.L$0 = SpillingKt.nullOutSpilledVariable((Object)object);
                                        this.label = 1;
                                        if ((object = bengaliTranslator.translateSingleTextCoalesced-BWLJW6A((String)object, translationEngine, string, (Continuation<? super Result<String>>)continuation)) != object2) break;
                                        return object2;
                                    }
                                    break block5;
                                }
                            }
                            return Result.box-impl((Object)object);
                        }
                        object = Result.Companion;
                        return Result.box-impl((Object)Result.constructor-impl((Object)this.$text));
                    }
                };
                var4_4 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1 /* !! */ );
                var4_4 /* !! */ .L$1 = SpillingKt.nullOutSpilledVariable((Object)var2_2 /* !! */ );
                var4_4 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable((Object)var3_3);
                var4_4 /* !! */ .label = 1;
                var2_2 /* !! */  = BuildersKt.withContext((CoroutineContext)var7_7, (Function2)var5_5 /* !! */ , var4_4 /* !! */ );
                var1_1 /* !! */  = var2_2 /* !! */ ;
                if (var2_2 /* !! */  != var6_6) break;
                return var6_6;
            }
        }
        return ((Result)var1_1 /* !! */ ).unbox-impl();
    }

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public final Object translateBatch-0E7RQCE(List<String> var1_1, TranslationEngine var2_2, Continuation<? super Result<? extends List<String>>> var3_3) {
        if (!(var3_3 /* !! */  instanceof translateBatch.1)) ** GOTO lbl-1000
        var4_4 /* !! */  = var3_3 /* !! */ ;
        if ((var4_4 /* !! */ .label & -2147483648) != 0) {
            var4_4 /* !! */ .label += -2147483648;
            var3_3 /* !! */  = var4_4 /* !! */ ;
        } else lbl-1000:
        // 2 sources

        {
            var3_3 /* !! */  = new ContinuationImpl(this, var3_3 /* !! */ ){
                Object L$0;
                Object L$1;
                int label;
                Object result;
                final BengaliTranslator this$0;
                {
                    this.this$0 = bengaliTranslator;
                    super(continuation);
                }

                public final Object invokeSuspend(Object object) {
                    this.result = object;
                    this.label |= Integer.MIN_VALUE;
                    object = this.this$0.translateBatch-0E7RQCE(null, null, (Continuation<? super Result<? extends List<String>>>)((Continuation)this));
                    if (object == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                        return object;
                    }
                    return Result.box-impl((Object)object);
                }
            };
        }
        var4_4 /* !! */  = var3_3 /* !! */ .result;
        var5_5 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
        switch (var3_3 /* !! */ .label) {
            default: {
                throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
            }
            case 1: {
                var1_1 /* !! */  = (TranslationEngine)var3_3 /* !! */ .L$1;
                var1_1 /* !! */  = (List)var3_3 /* !! */ .L$0;
                ResultKt.throwOnFailure((Object)var4_4 /* !! */ );
                var1_1 /* !! */  = var4_4 /* !! */ ;
                break;
            }
            case 0: {
                ResultKt.throwOnFailure((Object)var4_4 /* !! */ );
                var6_6 = (CoroutineContext)Dispatchers.getIO();
                var4_4 /* !! */  = (Function2)new Function2<CoroutineScope, Continuation<? super Result<? extends List<? extends String>>>, Object>((List<String>)var1_1 /* !! */ , var2_2 /* !! */ , null){
                    final TranslationEngine $engine;
                    final List<String> $texts;
                    int I$0;
                    Object L$0;
                    Object L$1;
                    Object L$2;
                    Object L$3;
                    Object L$4;
                    Object L$5;
                    Object L$6;
                    Object L$7;
                    Object L$8;
                    int label;
                    {
                        this.$texts = list;
                        this.$engine = translationEngine;
                        super(2, continuation);
                    }

                    private static final List<String> invokeSuspend$mapFinalResults(List<String> object, ConcurrentHashMap<String, String> concurrentHashMap) {
                        object = (Iterable)object;
                        Collection collection = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault(object, (int)10));
                        Iterator<String> iterator = object.iterator();
                        while (iterator.hasNext()) {
                            object = iterator.next();
                            String string = ((Object)StringsKt.trim((CharSequence)((CharSequence)object))).toString();
                            if (!BengaliTranslator.INSTANCE.shouldSkipText(string) && (string = concurrentHashMap.get(string)) != null) {
                                object = string;
                            }
                            collection.add(object);
                        }
                        object = (List)collection;
                        return object;
                    }

                    public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                        return (Continuation)new /* invalid duplicate definition of identical inner class */;
                    }

                    public final Object invoke(CoroutineScope coroutineScope, Continuation<? super Result<? extends List<String>>> continuation) {
                        return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                    }

                    /*
                     * Unable to fully structure code
                     * Enabled aggressive block sorting
                     * Enabled unnecessary exception pruning
                     * Enabled aggressive exception aggregation
                     */
                    public final Object invokeSuspend(Object var1_1) {
                        block34: {
                            var5_24 /* !! */  = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                            switch (this.label) {
                                default: {
                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                }
                                case 3: {
                                    var8_25 = (Ref.ObjectRef)this.L$4;
                                    var7_39 /* !! */  = (Ref.IntRef)this.L$3;
                                    var5_24 /* !! */  = (Semaphore)this.L$2;
                                    var6_40 /* !! */  = (LinkedHashSet)this.L$1;
                                    var5_24 /* !! */  = (ConcurrentHashMap)this.L$0;
                                    ResultKt.throwOnFailure((Object)var1_1);
                                    var1_2 = var8_25;
                                    break;
                                }
                                case 2: {
                                    var2_41 = this.I$0;
                                    var15_43 /* !! */  = (String)this.L$8;
                                    var11_44 /* !! */  = (Iterator)this.L$7;
                                    var10_47 /* !! */  = this.L$6;
                                    var9_48 = (List)this.L$5;
                                    var14_49 = (Iterator)this.L$4;
                                    var13_50 = (Throwable)this.L$3;
                                    var12_51 = (List)this.L$2;
                                    var8_26 = (LinkedHashSet)this.L$1;
                                    var7_39 /* !! */  = (ConcurrentHashMap)this.L$0;
                                    ResultKt.throwOnFailure((Object)var1_1);
                                    var16_52 = ((Result)var1_1).unbox-impl();
                                    var6_40 /* !! */  = this;
                                    ** GOTO lbl247
                                }
                                case 1: {
                                    var2_41 = this.I$0;
                                    var7_39 /* !! */  = (List)this.L$5;
                                    var12_51 = (Iterator)this.L$4;
                                    var14_49 = (Throwable)this.L$3;
                                    var11_44 /* !! */  = (List)this.L$2;
                                    var9_48 = (LinkedHashSet)this.L$1;
                                    var10_47 /* !! */  = (ConcurrentHashMap)this.L$0;
                                    ResultKt.throwOnFailure((Object)var1_1);
                                    var13_50 = ((Result)var1_1).unbox-impl();
                                    var6_40 /* !! */  = this;
                                    var8_27 = var5_24 /* !! */ ;
                                    var5_24 /* !! */  = var1_1;
                                    var1_3 = var8_27;
                                    var8_28 = var9_48;
                                    ** break;
                                }
                                case 0: {
                                    ResultKt.throwOnFailure((Object)var1_1);
                                    if (this.$texts.isEmpty()) {
                                        var1_4 = Result.Companion;
                                        return Result.box-impl((Object)Result.constructor-impl((Object)CollectionsKt.emptyList()));
                                    }
                                    var6_40 /* !! */  = new ConcurrentHashMap<K, V>();
                                    var7_39 /* !! */  = new LinkedHashSet<E>();
                                    var9_48 = this.$texts.iterator();
                                    while (var9_48.hasNext()) {
                                        var8_29 = StringsKt.trim((CharSequence)((String)var9_48.next())).toString();
                                        if (BengaliTranslator.INSTANCE.shouldSkipText(var8_29)) continue;
                                        var10_47 /* !! */  = BengaliTranslator.cacheKey$default(BengaliTranslator.INSTANCE, this.$engine, var8_29, null, null, 12, null);
                                        var10_47 /* !! */  = BengaliTranslator.access$getLruCache$p().get((String)var10_47 /* !! */ );
                                        if (var10_47 /* !! */  != null) {
                                            ((Map)var6_40 /* !! */ ).put(var8_29, var10_47 /* !! */ );
                                            continue;
                                        }
                                        Boxing.boxBoolean((boolean)var7_39 /* !! */ .add(var8_29));
                                    }
                                    if (var7_39 /* !! */ .isEmpty()) {
                                        var1_5 = Result.Companion;
                                        return Result.box-impl((Object)Result.constructor-impl(translateBatch.2.invokeSuspend$mapFinalResults(this.$texts, var6_40 /* !! */ )));
                                    }
                                    var9_48 = BengaliTranslator.INSTANCE.getTestTranslatorOverride$app();
                                    var8_30 = this.$engine;
                                    if (var9_48 != null) {
                                        var1_6 = this.$texts;
                                        try {
                                            var5_24 /* !! */  = var7_39 /* !! */ .iterator();
                                            Intrinsics.checkNotNullExpressionValue((Object)var5_24 /* !! */ , (String)"iterator(...)");
                                            while (true) {
                                                if (!var5_24 /* !! */ .hasNext()) {
                                                    var5_24 /* !! */  = Result.Companion;
                                                    var1_7 = Result.constructor-impl(translateBatch.2.invokeSuspend$mapFinalResults(var1_6, var6_40 /* !! */ ));
                                                    return Result.box-impl((Object)var1_10);
                                                }
                                                var7_39 /* !! */  = var5_24 /* !! */ .next();
                                                Intrinsics.checkNotNullExpressionValue((Object)var7_39 /* !! */ , (String)"next(...)");
                                                var10_47 /* !! */  = (String)var7_39 /* !! */ ;
                                                var11_45 = (String)var9_48.invoke((Object)var10_47 /* !! */ );
                                                var7_39 /* !! */  = BengaliTranslator.cacheKey$default(BengaliTranslator.INSTANCE, var8_30, (String)var10_47 /* !! */ , null, null, 12, null);
                                                BengaliTranslator.access$getLruCache$p().put((String)var7_39 /* !! */ , var11_45);
                                                ((Map)var6_40 /* !! */ ).put(var10_47 /* !! */ , var11_45);
                                            }
                                        }
                                        catch (Exception var1_8) {
                                            var5_24 /* !! */  = Result.Companion;
                                            var1_9 = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)var1_8));
                                        }
                                        return Result.box-impl((Object)var1_10);
                                    }
                                    if (var8_30 != TranslationEngine.LIBRE_TRANSLATE) ** GOTO lbl109
                                    var13_50 = BengaliTranslator.INSTANCE.chunkIntoBatches(CollectionsKt.toList((Iterable)((Iterable)var7_39 /* !! */ )));
                                    var2_41 = 0;
                                    var14_49 = null;
                                    var12_51 = var13_50.iterator();
                                    var8_31 = this;
                                    var11_44 /* !! */  = var7_39 /* !! */ ;
                                    var9_48 = var5_24 /* !! */ ;
                                    var10_47 /* !! */  = var1_1;
                                    var7_39 /* !! */  = var8_31;
                                    var8_32 = var11_44 /* !! */ ;
                                    var11_44 /* !! */  = var13_50;
                                    var5_24 /* !! */  = var14_49;
                                    var1_11 = var9_48;
                                    var9_48 = var10_47 /* !! */ ;
                                    break block34;
lbl109:
                                    // 1 sources

                                    var11_46 = SemaphoreKt.Semaphore$default((int)4, (int)0, (int)2, null);
                                    var8_34 = new Ref.IntRef();
                                    var1_21 = new Ref.ObjectRef();
                                    var9_48 = (Function2)new Function2<CoroutineScope, Continuation<? super List<? extends Object>>, Object>((LinkedHashSet<String>)var7_39 /* !! */ , var11_46, this.$engine, var6_40 /* !! */ , (Ref.ObjectRef<Throwable>)var1_21, var8_34, null){
                                        final TranslationEngine $engine;
                                        final Ref.ObjectRef<Throwable> $lastError;
                                        final LinkedHashSet<String> $neededTexts;
                                        final Semaphore $semaphore;
                                        final Ref.IntRef $successCount;
                                        final ConcurrentHashMap<String, String> $translationMap;
                                        private Object L$0;
                                        Object L$1;
                                        int label;
                                        {
                                            this.$neededTexts = linkedHashSet;
                                            this.$semaphore = semaphore;
                                            this.$engine = translationEngine;
                                            this.$translationMap = concurrentHashMap;
                                            this.$lastError = objectRef;
                                            this.$successCount = intRef;
                                            super(2, continuation);
                                        }

                                        public final Continuation<Unit> create(Object object, Continuation<?> object2) {
                                            object2 = new /* invalid duplicate definition of identical inner class */;
                                            object2.L$0 = object;
                                            return object2;
                                        }

                                        public final Object invoke(CoroutineScope coroutineScope, Continuation<? super List<? extends Object>> continuation) {
                                            return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                                        }

                                        public final Object invokeSuspend(Object object) {
                                            CoroutineScope coroutineScope = (CoroutineScope)this.L$0;
                                            Object object2 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                            switch (this.label) {
                                                default: {
                                                    throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                                }
                                                case 1: {
                                                    List list = (List)this.L$1;
                                                    ResultKt.throwOnFailure((Object)object);
                                                    break;
                                                }
                                                case 0: {
                                                    ResultKt.throwOnFailure((Object)object);
                                                    Object object3 = this.$neededTexts;
                                                    Semaphore semaphore = this.$semaphore;
                                                    TranslationEngine translationEngine = this.$engine;
                                                    ConcurrentHashMap<String, String> concurrentHashMap = this.$translationMap;
                                                    LinkedHashSet<String> linkedHashSet = this.$neededTexts;
                                                    Ref.ObjectRef<Throwable> objectRef = this.$lastError;
                                                    Object object4 = this.$successCount;
                                                    object = new ArrayList<E>(CollectionsKt.collectionSizeOrDefault((Iterable)object3, (int)10));
                                                    object3 = object3.iterator();
                                                    while (object3.hasNext()) {
                                                        Deferred deferred = BuildersKt.async$default((CoroutineScope)coroutineScope, null, null, (Function2)((Function2)new Function2<CoroutineScope, Continuation<? super Object>, Object>(semaphore, (String)object3.next(), translationEngine, concurrentHashMap, linkedHashSet, objectRef, object4, null){
                                                            final TranslationEngine $engine;
                                                            final Ref.ObjectRef<Throwable> $lastError;
                                                            final String $needed;
                                                            final LinkedHashSet<String> $neededTexts;
                                                            final Semaphore $semaphore;
                                                            final Ref.IntRef $successCount;
                                                            final ConcurrentHashMap<String, String> $translationMap;
                                                            int I$0;
                                                            int I$1;
                                                            Object L$0;
                                                            Object L$1;
                                                            Object L$2;
                                                            Object L$3;
                                                            Object L$4;
                                                            Object L$5;
                                                            Object L$6;
                                                            int label;
                                                            {
                                                                this.$semaphore = semaphore;
                                                                this.$needed = string;
                                                                this.$engine = translationEngine;
                                                                this.$translationMap = concurrentHashMap;
                                                                this.$neededTexts = linkedHashSet;
                                                                this.$lastError = objectRef;
                                                                this.$successCount = intRef;
                                                                super(2, continuation);
                                                            }

                                                            public final Continuation<Unit> create(Object object, Continuation<?> continuation) {
                                                                return (Continuation)new /* invalid duplicate definition of identical inner class */;
                                                            }

                                                            public final Object invoke(CoroutineScope coroutineScope, Continuation<Object> continuation) {
                                                                return (this.create(coroutineScope, continuation)).invokeSuspend(Unit.INSTANCE);
                                                            }

                                                            /*
                                                             * Loose catch block
                                                             * Enabled aggressive block sorting
                                                             * Enabled unnecessary exception pruning
                                                             * Enabled aggressive exception aggregation
                                                             * Converted monitor instructions to comments
                                                             * Lifted jumps to return sites
                                                             */
                                                            public final Object invokeSuspend(Object object) {
                                                                Object object2;
                                                                block20: {
                                                                    Object object3;
                                                                    block21: {
                                                                        Object object4;
                                                                        Object object5;
                                                                        Object object6;
                                                                        Object object7;
                                                                        Object object8;
                                                                        block18: {
                                                                            block19: {
                                                                                block17: {
                                                                                    int n;
                                                                                    Object object9;
                                                                                    Object object10;
                                                                                    object8 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                                                                    switch (this.label) {
                                                                                        default: {
                                                                                            throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                                                                        }
                                                                                        case 2: {
                                                                                            int n2 = this.I$1;
                                                                                            n2 = this.I$0;
                                                                                            object7 = (Ref.IntRef)this.L$6;
                                                                                            object10 = (Ref.ObjectRef)this.L$5;
                                                                                            object9 = (LinkedHashSet)this.L$4;
                                                                                            object8 = (ConcurrentHashMap)this.L$3;
                                                                                            object6 = (TranslationEngine)((Object)this.L$2);
                                                                                            object2 = (String)this.L$1;
                                                                                            object3 = object5 = (Semaphore)this.L$0;
                                                                                            ResultKt.throwOnFailure((Object)object);
                                                                                            object3 = object5;
                                                                                            object4 = ((Result)object).unbox-impl();
                                                                                            object = object5;
                                                                                            break block17;
                                                                                        }
                                                                                        case 1: {
                                                                                            n = this.I$0;
                                                                                            object7 = (Ref.IntRef)this.L$6;
                                                                                            object5 = (Semaphore)this.L$5;
                                                                                            object6 = (LinkedHashSet<String>)this.L$4;
                                                                                            object2 = (ConcurrentHashMap)this.L$3;
                                                                                            object9 = (TranslationEngine)((Object)this.L$2);
                                                                                            object3 = (String)this.L$1;
                                                                                            object10 = (Semaphore)this.L$0;
                                                                                            ResultKt.throwOnFailure((Object)object);
                                                                                            object = object10;
                                                                                            break;
                                                                                        }
                                                                                        case 0: {
                                                                                            ResultKt.throwOnFailure((Object)object);
                                                                                            object = this.$semaphore;
                                                                                            object3 = this.$needed;
                                                                                            object9 = this.$engine;
                                                                                            object2 = this.$translationMap;
                                                                                            object6 = this.$neededTexts;
                                                                                            object5 = this.$lastError;
                                                                                            object7 = this.$successCount;
                                                                                            n = 0;
                                                                                            object10 = (Continuation)this;
                                                                                            this.L$0 = object;
                                                                                            this.L$1 = object3;
                                                                                            this.L$2 = object9;
                                                                                            this.L$3 = object2;
                                                                                            this.L$4 = object6;
                                                                                            this.L$5 = object5;
                                                                                            this.L$6 = object7;
                                                                                            this.I$0 = 0;
                                                                                            this.label = 1;
                                                                                            if (object.acquire((Continuation)object10) != object8) break;
                                                                                            return object8;
                                                                                        }
                                                                                    }
                                                                                    object10 = BengaliTranslator.INSTANCE;
                                                                                    this.L$0 = object;
                                                                                    this.L$1 = object3;
                                                                                    this.L$2 = object9;
                                                                                    this.L$3 = object2;
                                                                                    this.L$4 = object6;
                                                                                    this.L$5 = object5;
                                                                                    this.L$6 = object7;
                                                                                    this.I$0 = n;
                                                                                    this.I$1 = 0;
                                                                                    this.label = 2;
                                                                                    object4 = BengaliTranslator.translateSingleTextCoalesced-BWLJW6A$default((BengaliTranslator)object10, (String)object3, object9, null, (Continuation)this, 4, null);
                                                                                    if (object4 == object8) {
                                                                                        return object8;
                                                                                    }
                                                                                    object10 = object6;
                                                                                    object8 = object2;
                                                                                    object2 = object3;
                                                                                    object6 = object9;
                                                                                    object9 = object10;
                                                                                    object10 = object5;
                                                                                }
                                                                                object3 = object;
                                                                                if (!Result.isSuccess-impl((Object)object4)) break block18;
                                                                                object3 = object;
                                                                                ResultKt.throwOnFailure((Object)object4);
                                                                                object3 = object;
                                                                                object6 = (String)object4;
                                                                                object3 = object;
                                                                                if (StringsKt.isBlank((CharSequence)((CharSequence)object6))) break block19;
                                                                                object3 = object;
                                                                                ((Map)object8).put(object2, object6);
                                                                                object3 = object;
                                                                                // MONITORENTER : object9
                                                                                try {
                                                                                    ++((Ref.IntRef)object7).element;
                                                                                    object3 = object;
                                                                                }
                                                                                catch (Throwable throwable) {
                                                                                    object3 = object;
                                                                                    // MONITOREXIT : object9
                                                                                    object3 = object;
                                                                                    throw throwable;
                                                                                }
                                                                                try {
                                                                                    // MONITOREXIT : object9
                                                                                    break block19;
                                                                                }
                                                                                catch (Throwable throwable) {
                                                                                    object = object3;
                                                                                }
                                                                                break block20;
                                                                            }
                                                                            object3 = object;
                                                                            object3 = object2 = Unit.INSTANCE;
                                                                            break block21;
                                                                        }
                                                                        object3 = object;
                                                                        ((Map)object8).put(object2, object2);
                                                                        object3 = object;
                                                                        object10.element = Result.exceptionOrNull-impl((Object)object4);
                                                                        object3 = object;
                                                                        object5 = Result.exceptionOrNull-impl((Object)object4);
                                                                        if (object5 != null) {
                                                                            object3 = object;
                                                                            object5 = ((Throwable)object5).getMessage();
                                                                        } else {
                                                                            object5 = null;
                                                                        }
                                                                        object3 = object;
                                                                        object3 = object;
                                                                        object7 = new StringBuilder();
                                                                        object3 = object;
                                                                        object3 = object2 = Boxing.boxInt((int)Log.w((String)"BengaliTranslator", (String)((StringBuilder)object7).append("Translation error (").append(object6).append(") for '").append((String)object2).append("': ").append((String)object5).toString()));
                                                                    }
                                                                    object.release();
                                                                    return object3;
                                                                    catch (Throwable throwable) {
                                                                        object2 = throwable;
                                                                    }
                                                                }
                                                                object.release();
                                                                throw object2;
                                                            }
                                                        }), (int)3, null);
                                                        object.add(deferred);
                                                    }
                                                    object4 = (List)object;
                                                    object = (Collection)object4;
                                                    semaphore = (Continuation)this;
                                                    this.L$0 = SpillingKt.nullOutSpilledVariable((Object)coroutineScope);
                                                    this.L$1 = SpillingKt.nullOutSpilledVariable((Object)object4);
                                                    this.label = 1;
                                                    if ((object = AwaitKt.awaitAll((Collection)object, (Continuation)semaphore)) != object2) break;
                                                    return object2;
                                                }
                                            }
                                            return object;
                                        }
                                    };
                                    var10_47 /* !! */  = (Continuation)this;
                                    this.L$0 = var6_40 /* !! */ ;
                                    this.L$1 = var7_39 /* !! */ ;
                                    this.L$2 = SpillingKt.nullOutSpilledVariable((Object)var11_46);
                                    this.L$3 = var8_34;
                                    this.L$4 = var1_21;
                                    this.label = 3;
                                    if (CoroutineScopeKt.coroutineScope((Function2)var9_48, (Continuation)var10_47 /* !! */ ) == var5_24 /* !! */ ) {
                                        return var5_24 /* !! */ ;
                                    }
                                    var5_24 /* !! */  = var6_40 /* !! */ ;
                                    var6_40 /* !! */  = var7_39 /* !! */ ;
                                    var7_39 /* !! */  = var8_34;
                                }
                            }
                            var8_36 = (Map)var5_24 /* !! */ ;
                            if (var8_36.isEmpty()) {
                                var2_42 = 0;
                            } else {
                                var2_42 = 0;
                                for (Map.Entry var8_38 : var8_36.entrySet()) {
                                    if (Intrinsics.areEqual((Object)((String)var8_38.getKey()), (Object)((String)var8_38.getValue()))) continue;
                                    ++var2_42;
                                }
                            }
                            if (!((Collection)var6_40 /* !! */ ).isEmpty() && var7_39 /* !! */ .element == 0 && var2_42 == 0) {
                                var5_24 /* !! */  = Result.Companion;
                                var5_24 /* !! */  = this.$engine.getDisplayName();
                                return Result.box-impl((Object)Result.constructor-impl((Object)ResultKt.createFailure((Throwable)new IllegalStateException("Translation unavailable with " + (String)var5_24 /* !! */  + ". Please try again.", (Throwable)var1_22.element))));
                            }
                            var1_23 = Result.Companion;
                            return Result.box-impl((Object)Result.constructor-impl(translateBatch.2.invokeSuspend$mapFinalResults(this.$texts, (ConcurrentHashMap<String, String>)var5_24 /* !! */ )));
                        }
                        while (true) {
                            block39: {
                                block35: {
                                    block36: {
                                        if (!var12_51.hasNext()) break block35;
                                        var10_47 /* !! */  = (List)var12_51.next();
                                        var14_49 = BengaliTranslator.INSTANCE;
                                        var13_50 = (Continuation)var7_39 /* !! */ ;
                                        var7_39 /* !! */ .L$0 = var6_40 /* !! */ ;
                                        var7_39 /* !! */ .L$1 = var8_33;
                                        var7_39 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable((Object)var11_44 /* !! */ );
                                        var7_39 /* !! */ .L$3 = var5_24 /* !! */ ;
                                        var7_39 /* !! */ .L$4 = var12_51;
                                        var7_39 /* !! */ .L$5 = var10_47 /* !! */ ;
                                        var7_39 /* !! */ .L$6 = null;
                                        var7_39 /* !! */ .L$7 = null;
                                        var7_39 /* !! */ .L$8 = null;
                                        var7_39 /* !! */ .I$0 = var2_41;
                                        var7_39 /* !! */ .label = 1;
                                        if ((var13_50 = var14_49.translateBatchWithLibreTranslate-gIAlu-s$app((List<String>)var10_47 /* !! */ , (Continuation<? super Result<? extends List<String>>>)var13_50)) == var1_12) {
                                            return var1_12;
                                        }
                                        var15_43 /* !! */  = var7_39 /* !! */ ;
                                        var7_39 /* !! */  = var10_47 /* !! */ ;
                                        var10_47 /* !! */  = var6_40 /* !! */ ;
                                        var14_49 = var5_24 /* !! */ ;
                                        var6_40 /* !! */  = var15_43 /* !! */ ;
                                        var5_24 /* !! */  = var9_48;
lbl165:
                                        // 2 sources

                                        if (!Result.isSuccess-impl(var13_50)) break block36;
                                        ResultKt.throwOnFailure(var13_50);
                                        var16_52 = var13_50;
                                        var4_54 = ((Collection)var7_39 /* !! */ ).size();
                                        for (var3_53 = 0; var3_53 < var4_54; ++var3_53) {
                                            block38: {
                                                block37: {
                                                    var13_50 = (String)var7_39 /* !! */ .get(var3_53);
                                                    var9_48 = (String)CollectionsKt.getOrNull((List)var16_52, (int)var3_53);
                                                    if (var9_48 == null) break block37;
                                                    var15_43 /* !! */  = StringsKt.trim((CharSequence)((CharSequence)var9_48)).toString();
                                                    var9_48 = var15_43 /* !! */ ;
                                                    if (var15_43 /* !! */  != null) break block38;
                                                }
                                                var9_48 = var13_50;
                                            }
                                            if (StringsKt.isBlank((CharSequence)((CharSequence)var9_48)) || Intrinsics.areEqual((Object)var9_48, (Object)var13_50)) continue;
                                            var15_43 /* !! */  = BengaliTranslator.cacheKey$default(BengaliTranslator.INSTANCE, var6_40 /* !! */ .$engine, (String)var13_50, null, null, 12, null);
                                            BengaliTranslator.INSTANCE.putInCache((String)var15_43 /* !! */ , (String)var9_48);
                                            ((Map)var10_47 /* !! */ ).put(var13_50, var9_48);
                                            ++var2_41;
                                        }
                                        var7_39 /* !! */  = var6_40 /* !! */ ;
                                        var9_48 = var5_24 /* !! */ ;
                                        var5_24 /* !! */  = var14_49;
                                        var6_40 /* !! */  = var10_47 /* !! */ ;
                                        continue;
                                    }
                                    var14_49 = Result.exceptionOrNull-impl(var13_50);
                                    var9_48 = var14_49 != null ? var14_49.getMessage() : null;
                                    Log.w((String)"BengaliTranslator", (String)("LibreTranslate batch error: " + (String)var9_48));
                                    var17_55 /* !! */  = var7_39 /* !! */ .iterator();
                                    var15_43 /* !! */  = var12_51;
                                    var12_51 = var11_44 /* !! */ ;
                                    var9_48 = var7_39 /* !! */ ;
                                    var16_52 = var5_24 /* !! */ ;
                                    var11_44 /* !! */  = var17_55 /* !! */ ;
                                    var7_39 /* !! */  = var6_40 /* !! */ ;
                                    var6_40 /* !! */  = var10_47 /* !! */ ;
                                    var10_47 /* !! */  = var13_50;
                                    var13_50 = var14_49;
                                    var14_49 = var15_43 /* !! */ ;
                                    var5_24 /* !! */  = var1_12;
                                    var1_13 = var16_52;
                                    break block39;
                                }
                                var1_16 = (Map)var6_40 /* !! */ ;
                                if (var1_16.isEmpty()) {
                                    var3_53 = 0;
                                } else {
                                    var3_53 = 0;
                                    for (Map.Entry var1_18 : var1_16.entrySet()) {
                                        if (Intrinsics.areEqual((Object)((String)var1_18.getKey()), (Object)((String)var1_18.getValue()))) continue;
                                        ++var3_53;
                                    }
                                }
                                if (!((Collection)var8_33).isEmpty() && var2_41 == 0 && var3_53 == 0) {
                                    var1_19 = Result.Companion;
                                    return Result.box-impl((Object)Result.constructor-impl((Object)ResultKt.createFailure((Throwable)new IllegalStateException("Translation unavailable with LibreTranslate. Please try again.", (Throwable)var5_24 /* !! */ ))));
                                }
                                var1_20 = Result.Companion;
                                return Result.box-impl((Object)Result.constructor-impl(translateBatch.2.invokeSuspend$mapFinalResults(var7_39 /* !! */ .$texts, var6_40 /* !! */ )));
                            }
                            while (var11_44 /* !! */ .hasNext()) {
                                var15_43 /* !! */  = (String)var11_44 /* !! */ .next();
                                var18_56 = BengaliTranslator.INSTANCE;
                                var17_55 /* !! */  = TranslationEngine.LIBRE_TRANSLATE;
                                var16_52 = (Continuation)var7_39 /* !! */ ;
                                var7_39 /* !! */ .L$0 = var6_40 /* !! */ ;
                                var7_39 /* !! */ .L$1 = var8_33;
                                var7_39 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable((Object)var12_51);
                                var7_39 /* !! */ .L$3 = var13_50;
                                var7_39 /* !! */ .L$4 = var14_49;
                                var7_39 /* !! */ .L$5 = SpillingKt.nullOutSpilledVariable((Object)var9_48);
                                var7_39 /* !! */ .L$6 = SpillingKt.nullOutSpilledVariable((Object)var10_47 /* !! */ );
                                var7_39 /* !! */ .L$7 = var11_44 /* !! */ ;
                                var7_39 /* !! */ .L$8 = var15_43 /* !! */ ;
                                var7_39 /* !! */ .I$0 = var2_41;
                                var7_39 /* !! */ .label = 2;
                                if ((var16_52 = BengaliTranslator.translateSingleTextCoalesced-BWLJW6A$default(var18_56, var15_43 /* !! */ , var17_55 /* !! */ , null, (Continuation)var16_52, 4, null)) == var5_24 /* !! */ ) {
                                    return var5_24 /* !! */ ;
                                }
                                var17_55 /* !! */  = var7_39 /* !! */ ;
                                var7_39 /* !! */  = var6_40 /* !! */ ;
                                var6_40 /* !! */  = var17_55 /* !! */ ;
lbl247:
                                // 2 sources

                                if (Result.isSuccess-impl((Object)var16_52)) {
                                    ResultKt.throwOnFailure((Object)var16_52);
                                    var16_52 = (String)var16_52;
                                    if (!StringsKt.isBlank((CharSequence)((CharSequence)var16_52))) {
                                        BengaliTranslator.INSTANCE.putInCache(BengaliTranslator.cacheKey$default(BengaliTranslator.INSTANCE, var6_40 /* !! */ .$engine, (String)var15_43 /* !! */ , null, null, 12, null), (String)var16_52);
                                        ((Map)var7_39 /* !! */ ).put(var15_43 /* !! */ , var16_52);
                                        ++var2_41;
                                        var15_43 /* !! */  = var6_40 /* !! */ ;
                                        var6_40 /* !! */  = var7_39 /* !! */ ;
                                        var7_39 /* !! */  = var15_43 /* !! */ ;
                                        continue;
                                    }
                                }
                                var15_43 /* !! */  = var6_40 /* !! */ ;
                                var6_40 /* !! */  = var7_39 /* !! */ ;
                                var7_39 /* !! */  = var15_43 /* !! */ ;
                            }
                            var11_44 /* !! */  = var12_51;
                            var9_48 = var1_14;
                            var1_15 /* !! */  = var5_24 /* !! */ ;
                            var12_51 = var14_49;
                            var5_24 /* !! */  = var13_50;
                        }
                    }
                };
                var3_3 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1 /* !! */ );
                var3_3 /* !! */ .L$1 = SpillingKt.nullOutSpilledVariable((Object)var2_2 /* !! */ );
                var3_3 /* !! */ .label = 1;
                var2_2 /* !! */  = BuildersKt.withContext((CoroutineContext)var6_6, (Function2)var4_4 /* !! */ , var3_3 /* !! */ );
                var1_1 /* !! */  = var2_2 /* !! */ ;
                if (var2_2 /* !! */  != var5_5) break;
                return var5_5;
            }
        }
        return ((Result)var1_1 /* !! */ ).unbox-impl();
    }

    /*
     * Exception decompiling
     */
    public final Object translateBatchWithLibreTranslate-gIAlu-s$app(List<String> var1_1, Continuation<? super Result<? extends List<String>>> var2_11) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Back jump on a try block [egrp 41[TRYBLOCK] [78, 79 : 1610->1620)] java.lang.Exception
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.insertExceptionBlocks(Op02WithProcessedDataAndRefs.java:2283)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:415)
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

    /*
     * Unable to fully structure code
     * Could not resolve type clashes
     */
    public final Object translateSingleTextCoalesced-BWLJW6A(String var1_1, TranslationEngine var2_2, String var3_7, Continuation<? super Result<String>> var4_9) {
        block27: {
            block26: {
                block25: {
                    block29: {
                        block24: {
                            block28: {
                                if (!(var4_9 /* !! */  instanceof translateSingleTextCoalesced.1)) ** GOTO lbl-1000
                                var7_10 /* !! */  = var4_9 /* !! */ ;
                                if ((var7_10 /* !! */ .label & -2147483648) != 0) {
                                    var7_10 /* !! */ .label += -2147483648;
                                    var4_9 /* !! */  = var7_10 /* !! */ ;
                                } else lbl-1000:
                                // 2 sources

                                {
                                    var4_9 /* !! */  = new ContinuationImpl(this, (Continuation<? super translateSingleTextCoalesced.1>)var4_9 /* !! */ ){
                                        Object L$0;
                                        Object L$1;
                                        Object L$2;
                                        Object L$3;
                                        Object L$4;
                                        Object L$5;
                                        Object L$6;
                                        int label;
                                        Object result;
                                        final BengaliTranslator this$0;
                                        {
                                            this.this$0 = bengaliTranslator;
                                            super(continuation);
                                        }

                                        public final Object invokeSuspend(Object object) {
                                            this.result = object;
                                            this.label |= Integer.MIN_VALUE;
                                            object = this.this$0.translateSingleTextCoalesced-BWLJW6A(null, null, null, (Continuation<? super Result<String>>)((Continuation)this));
                                            if (object == IntrinsicsKt.getCOROUTINE_SUSPENDED()) {
                                                return object;
                                            }
                                            return Result.box-impl((Object)object);
                                        }
                                    };
                                }
                                var7_10 /* !! */  = var4_9 /* !! */ .result;
                                var9_11 = IntrinsicsKt.getCOROUTINE_SUSPENDED();
                                switch (var4_9 /* !! */ .label) {
                                    default: {
                                        throw new IllegalStateException("call to 'resume' before 'invoke' with coroutine");
                                    }
                                    case 2: {
                                        var1_1 /* !! */  = (CompletableDeferred)var4_9 /* !! */ .L$6;
                                        var1_1 /* !! */  = (CompletableDeferred)var4_9 /* !! */ .L$5;
                                        var2_2 /* !! */  = (String)var4_9 /* !! */ .L$4;
                                        var3_7 /* !! */  = (String)var4_9 /* !! */ .L$3;
                                        var3_7 /* !! */  = (String)var4_9 /* !! */ .L$2;
                                        var3_7 /* !! */  = (TranslationEngine)var4_9 /* !! */ .L$1;
                                        var3_7 /* !! */  = (String)var4_9 /* !! */ .L$0;
                                        try {
                                            ResultKt.throwOnFailure((Object)var7_10 /* !! */ );
                                            var3_7 /* !! */  = ((Result)var7_10 /* !! */ ).unbox-impl();
                                            var4_9 /* !! */  = var2_2 /* !! */ ;
                                            var2_2 /* !! */  = var3_7 /* !! */ ;
                                            var3_7 /* !! */  = var1_1 /* !! */ ;
                                            var1_1 /* !! */  = var4_9 /* !! */ ;
                                            break block24;
                                        }
                                        catch (Throwable var3_8) {
                                            var4_9 /* !! */  = var2_2 /* !! */ ;
                                            var2_2 /* !! */  = var3_8;
                                            var3_7 /* !! */  = var1_1 /* !! */ ;
                                            var1_1 /* !! */  = var4_9 /* !! */ ;
                                            break block25;
                                        }
                                    }
                                    case 1: {
                                        var1_1 /* !! */  = (CompletableDeferred)var4_9 /* !! */ .L$6;
                                        var1_1 /* !! */  = (CompletableDeferred)var4_9 /* !! */ .L$5;
                                        var1_1 /* !! */  = (String)var4_9 /* !! */ .L$4;
                                        var1_1 /* !! */  = (String)var4_9 /* !! */ .L$3;
                                        var1_1 /* !! */  = (String)var4_9 /* !! */ .L$2;
                                        var1_1 /* !! */  = (TranslationEngine)var4_9 /* !! */ .L$1;
                                        var1_1 /* !! */  = (String)var4_9 /* !! */ .L$0;
                                        ResultKt.throwOnFailure((Object)var7_10 /* !! */ );
                                        var1_1 /* !! */  = var7_10 /* !! */ ;
                                        break;
                                    }
                                    case 0: {
                                        ResultKt.throwOnFailure((Object)var7_10 /* !! */ );
                                        var10_12 = StringsKt.trim((CharSequence)((CharSequence)var1_1 /* !! */ )).toString();
                                        var5_13 = ((CharSequence)var10_12).length() == 0;
                                        if (var5_13 || this.shouldSkipText(var10_12)) break block27;
                                        var8_14 = BengaliTranslator.cacheKey$default(this, var2_2 /* !! */ , var10_12, (String)var3_7 /* !! */ , null, 8, null);
                                        var7_10 /* !! */  = BengaliTranslator.lruCache.get(var8_14);
                                        if (var7_10 /* !! */  != null) {
                                            var1_1 /* !! */  = Result.Companion;
                                            return Result.constructor-impl((Object)var7_10 /* !! */ );
                                        }
                                        var7_10 /* !! */  = CompletableDeferredKt.CompletableDeferred$default(null, (int)1, null);
                                        var12_15 = BengaliTranslator.inFlightRequests.putIfAbsent(var8_14, (CompletableDeferred<Result<String>>)var7_10 /* !! */ );
                                        if (var12_15 != null) {
                                            var11_16 = BengaliTranslator.lruCache.get(var8_14);
                                            if (var11_16 != null) {
                                                var1_1 /* !! */  = Result.Companion;
                                                return Result.constructor-impl((Object)var11_16);
                                            }
                                            var4_9 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable((Object)var1_1 /* !! */ );
                                            var4_9 /* !! */ .L$1 = SpillingKt.nullOutSpilledVariable((Object)var2_2 /* !! */ );
                                            var4_9 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable(var3_7 /* !! */ );
                                            var4_9 /* !! */ .L$3 = SpillingKt.nullOutSpilledVariable((Object)var10_12);
                                            var4_9 /* !! */ .L$4 = SpillingKt.nullOutSpilledVariable((Object)var8_14);
                                            var4_9 /* !! */ .L$5 = SpillingKt.nullOutSpilledVariable((Object)var7_10 /* !! */ );
                                            var4_9 /* !! */ .L$6 = SpillingKt.nullOutSpilledVariable(var12_15);
                                            var4_9 /* !! */ .label = 1;
                                            var1_1 /* !! */  = var12_15.await((Continuation)var4_9 /* !! */ );
                                            if (var1_1 /* !! */  != var9_11) break;
                                            return var9_11;
                                        }
                                        break block28;
                                    }
                                }
                                return ((Result)var1_1 /* !! */ ).unbox-impl();
                            }
                            var4_9 /* !! */ .L$0 = SpillingKt.nullOutSpilledVariable(var1_1 /* !! */ );
                            var4_9 /* !! */ .L$1 = SpillingKt.nullOutSpilledVariable((Object)var2_2 /* !! */ );
                            var4_9 /* !! */ .L$2 = SpillingKt.nullOutSpilledVariable(var3_7 /* !! */ );
                            var4_9 /* !! */ .L$3 = SpillingKt.nullOutSpilledVariable((Object)var10_12);
                            var4_9 /* !! */ .L$4 = var8_14;
                            var4_9 /* !! */ .L$5 = var7_10 /* !! */ ;
                            var4_9 /* !! */ .L$6 = SpillingKt.nullOutSpilledVariable(var12_15);
                            var4_9 /* !! */ .label = 2;
                            try {
                                var2_2 /* !! */  = this.translateSingleText-BWLJW6A(var10_12, var2_2 /* !! */ , (String)var3_7 /* !! */ , (Continuation<? super Result<String>>)var4_9 /* !! */ );
                                if (var2_2 /* !! */  == var9_11) {
                                    return var9_11;
                                }
                                var1_1 /* !! */  = var8_14;
                                var3_7 /* !! */  = var7_10 /* !! */ ;
                            }
                            catch (Throwable var2_4) {}
                        }
                        try {
                            if (Result.isSuccess-impl((Object)var2_2 /* !! */ )) {
                                ResultKt.throwOnFailure((Object)var2_2 /* !! */ );
                                var4_9 /* !! */  = (String)var2_2 /* !! */ ;
                                if (!StringsKt.isBlank((CharSequence)((CharSequence)var4_9 /* !! */ ))) {
                                    BengaliTranslator.lruCache.put((String)var1_1 /* !! */ , (String)var4_9 /* !! */ );
                                }
                            }
                            var3_7 /* !! */ .complete((Object)Result.box-impl((Object)var2_2 /* !! */ ));
                            BengaliTranslator.inFlightRequests.remove(var1_1 /* !! */ , var3_7 /* !! */ );
                        }
                        catch (Throwable var2_3) {}
                        return var2_2 /* !! */ ;
                        break block25;
                        break block29;
                        catch (Throwable var2_5) {
                            // empty catch block
                        }
                    }
                    var1_1 /* !! */  = var8_14;
                    var3_7 /* !! */  = var7_10 /* !! */ ;
                }
                try {
                    var4_9 /* !! */  = Result.Companion;
                    var4_9 /* !! */  = Result.constructor-impl((Object)ResultKt.createFailure((Throwable)var2_2 /* !! */ ));
                    var3_7 /* !! */ .complete((Object)Result.box-impl((Object)var4_9 /* !! */ ));
                    var6_17 = var2_2 /* !! */  instanceof CancellationException;
                    if (var6_17) break block26;
                    BengaliTranslator.inFlightRequests.remove(var1_1 /* !! */ , var3_7 /* !! */ );
                }
                catch (Throwable var2_6) {
                    BengaliTranslator.inFlightRequests.remove(var1_1 /* !! */ , var3_7 /* !! */ );
                    throw var2_6;
                }
                return var4_9 /* !! */ ;
            }
            throw var2_2 /* !! */ ;
        }
        var2_2 /* !! */  = Result.Companion;
        return Result.constructor-impl((Object)var1_1 /* !! */ );
    }

    /*
     * Exception decompiling
     */
    public final Object translateWithLibreTranslate-gIAlu-s$app(String var1_1, Continuation<? super Result<String>> var2_4) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Back jump on a try block [egrp 13[TRYBLOCK] [27, 26 : 958->968)] java.lang.Throwable
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op02WithProcessedDataAndRefs.insertExceptionBlocks(Op02WithProcessedDataAndRefs.java:2283)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:415)
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

    /*
     * Exception decompiling
     */
    public final Object translateWithMyMemory-gIAlu-s$app(String var1_1, String var2_2) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
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

    @Metadata(d1={"\u0000)\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0004*\u0001\u0007\b\u0007\u0018\u00002\u00020\u0001B\u0011\u0012\b\b\u0002\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0004\b\u0004\u0010\u0005J\u0010\u0010\t\u001a\u0004\u0018\u00010\n2\u0006\u0010\u000b\u001a\u00020\nJ\u0016\u0010\f\u001a\u00020\r2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\nJ\u0006\u0010\u000f\u001a\u00020\rJ\u0006\u0010\u0010\u001a\u00020\u0003R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0010\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0004\n\u0002\u0010\b\u00a8\u0006\u0011"}, d2={"Lcom/muslim/browser/pro/browser/BengaliTranslator$TranslationLruCache;", "", "maxSize", "", "<init>", "(I)V", "map", "com/muslim/browser/pro/browser/BengaliTranslator$TranslationLruCache$map$1", "Lcom/muslim/browser/pro/browser/BengaliTranslator$TranslationLruCache$map$1;", "get", "", "key", "put", "", "value", "clear", "size", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static final class TranslationLruCache {
        public static final int $stable = 8;
        private final map.1 map;
        private final int maxSize;

        public TranslationLruCache() {
            this(0, 1, null);
        }

        public TranslationLruCache(int n) {
            this.maxSize = n;
            this.map = new LinkedHashMap<String, String>(this, this.maxSize){
                final TranslationLruCache this$0;
                {
                    this.this$0 = translationLruCache;
                    super(n, 0.75f, true);
                }

                protected boolean removeEldestEntry(Map.Entry<String, String> entry) {
                    boolean bl = this.size() > TranslationLruCache.access$getMaxSize$p(this.this$0);
                    return bl;
                }
            };
        }

        public /* synthetic */ TranslationLruCache(int n, int n2, DefaultConstructorMarker defaultConstructorMarker) {
            if ((n2 & 1) != 0) {
                n = 500;
            }
            this(n);
        }

        public static final /* synthetic */ int access$getMaxSize$p(TranslationLruCache translationLruCache) {
            return translationLruCache.maxSize;
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public final void clear() {
            synchronized (this) {
                this.map.clear();
                return;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public final String get(String string) {
            synchronized (this) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"key");
                return (String)this.map.get((Object)string);
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public final void put(String string, String string2) {
            synchronized (this) {
                Intrinsics.checkNotNullParameter((Object)string, (String)"key");
                Intrinsics.checkNotNullParameter((Object)string2, (String)"value");
                ((Map)this.map).put(string, string2);
                return;
            }
        }

        /*
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public final int size() {
            synchronized (this) {
                return this.map.size();
            }
        }
    }

    @Metadata(d1={"\u0000&\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0003\n\u0002\u0010\b\n\u0002\b\u0011\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0003\n\u0002\u0010\u0002\n\u0000\b\u00c1\u0002\u0018\u00002\u00020\u0001B\t\b\u0002\u00a2\u0006\u0004\b\u0002\u0010\u0003J\u0006\u0010\u001b\u001a\u00020\u001cR\u001a\u0010\u0004\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0006\u0010\u0007\"\u0004\b\b\u0010\tR\u001a\u0010\n\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\u0007\"\u0004\b\f\u0010\tR\u001a\u0010\r\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u0007\"\u0004\b\u000f\u0010\tR\u001a\u0010\u0010\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0007\"\u0004\b\u0012\u0010\tR\u001a\u0010\u0013\u001a\u00020\u0005X\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0014\u0010\u0007\"\u0004\b\u0015\u0010\tR\u0017\u0010\u0016\u001a\b\u0012\u0004\u0012\u00020\u00180\u0017\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001a\u00a8\u0006\u001d"}, d2={"Lcom/muslim/browser/pro/browser/BengaliTranslator$TranslationStats;", "", "<init>", "()V", "totalRequests", "", "getTotalRequests", "()I", "setTotalRequests", "(I)V", "successfulRequests", "getSuccessfulRequests", "setSuccessfulRequests", "failedRequests", "getFailedRequests", "setFailedRequests", "http429Count", "getHttp429Count", "setHttp429Count", "retryCount", "getRetryCount", "setRetryCount", "requestDurations", "Ljava/util/concurrent/CopyOnWriteArrayList;", "", "getRequestDurations", "()Ljava/util/concurrent/CopyOnWriteArrayList;", "reset", "", "app"}, k=1, mv={2, 2, 0}, xi=48)
    public static final class TranslationStats {
        public static final int $stable;
        public static final TranslationStats INSTANCE;
        private static volatile int failedRequests;
        private static volatile int http429Count;
        private static final CopyOnWriteArrayList<Long> requestDurations;
        private static volatile int retryCount;
        private static volatile int successfulRequests;
        private static volatile int totalRequests;

        static {
            INSTANCE = new TranslationStats();
            requestDurations = new CopyOnWriteArrayList();
            $stable = 8;
        }

        private TranslationStats() {
        }

        public final int getFailedRequests() {
            return failedRequests;
        }

        public final int getHttp429Count() {
            return http429Count;
        }

        public final CopyOnWriteArrayList<Long> getRequestDurations() {
            return requestDurations;
        }

        public final int getRetryCount() {
            return retryCount;
        }

        public final int getSuccessfulRequests() {
            return successfulRequests;
        }

        public final int getTotalRequests() {
            return totalRequests;
        }

        public final void reset() {
            totalRequests = 0;
            successfulRequests = 0;
            failedRequests = 0;
            http429Count = 0;
            retryCount = 0;
            requestDurations.clear();
        }

        public final void setFailedRequests(int n) {
            failedRequests = n;
        }

        public final void setHttp429Count(int n) {
            http429Count = n;
        }

        public final void setRetryCount(int n) {
            retryCount = n;
        }

        public final void setSuccessfulRequests(int n) {
            successfulRequests = n;
        }

        public final void setTotalRequests(int n) {
            totalRequests = n;
        }
    }

    @Metadata(k=3, mv={2, 2, 0}, xi=48)
    public static final class WhenMappings {
        public static final int[] $EnumSwitchMapping$0;

        static {
            int[] nArray = new int[TranslationEngine.values().length];
            try {
                nArray[TranslationEngine.LIBRE_TRANSLATE.ordinal()] = 1;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            try {
                nArray[TranslationEngine.MYMEMORY.ordinal()] = 2;
            }
            catch (NoSuchFieldError noSuchFieldError) {
                // empty catch block
            }
            $EnumSwitchMapping$0 = nArray;
        }
    }
}

