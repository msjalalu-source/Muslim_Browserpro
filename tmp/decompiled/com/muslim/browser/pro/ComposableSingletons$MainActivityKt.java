/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  androidx.compose.foundation.layout.RowScope
 *  androidx.compose.foundation.layout.SizeKt
 *  androidx.compose.material.icons.Icons
 *  androidx.compose.material.icons.Icons$Filled
 *  androidx.compose.material.icons.filled.WarningKt
 *  androidx.compose.material3.IconKt
 *  androidx.compose.material3.MaterialTheme
 *  androidx.compose.material3.TextKt
 *  androidx.compose.runtime.Composer
 *  androidx.compose.runtime.ComposerKt
 *  androidx.compose.runtime.internal.ComposableLambdaKt
 *  androidx.compose.ui.Modifier
 *  androidx.compose.ui.graphics.vector.ImageVector
 *  androidx.compose.ui.unit.Dp
 *  kotlin.Metadata
 *  kotlin.Unit
 *  kotlin.jvm.functions.Function2
 *  kotlin.jvm.functions.Function3
 *  kotlin.jvm.internal.Intrinsics
 */
package com.muslim.browser.pro;

import androidx.compose.foundation.layout.RowScope;
import androidx.compose.foundation.layout.SizeKt;
import androidx.compose.material.icons.Icons;
import androidx.compose.material.icons.filled.WarningKt;
import androidx.compose.material3.IconKt;
import androidx.compose.material3.MaterialTheme;
import androidx.compose.material3.TextKt;
import androidx.compose.runtime.Composer;
import androidx.compose.runtime.ComposerKt;
import androidx.compose.runtime.internal.ComposableLambdaKt;
import androidx.compose.ui.Modifier;
import androidx.compose.ui.graphics.vector.ImageVector;
import androidx.compose.ui.unit.Dp;
import com.muslim.browser.pro.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda0;
import com.muslim.browser.pro.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda1;
import com.muslim.browser.pro.ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda2;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function2;
import kotlin.jvm.functions.Function3;
import kotlin.jvm.internal.Intrinsics;

/*
 * Illegal identifiers - consider using --renameillegalidents true
 */
@Metadata(k=3, mv={2, 2, 0}, xi=48)
public final class ComposableSingletons$MainActivityKt {
    public static final ComposableSingletons$MainActivityKt INSTANCE = new ComposableSingletons$MainActivityKt();
    private static Function3<RowScope, Composer, Integer, Unit> lambda$-2016551255;
    private static Function3<RowScope, Composer, Integer, Unit> lambda$1062284077;
    private static Function2<Composer, Integer, Unit> lambda$174937338;

    static {
        lambda$1062284077 = (Function3)ComposableLambdaKt.composableLambdaInstance((int)1062284077, (boolean)false, (Object)new ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda0());
        lambda$-2016551255 = (Function3)ComposableLambdaKt.composableLambdaInstance((int)-2016551255, (boolean)false, (Object)new ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda1());
        lambda$174937338 = (Function2)ComposableLambdaKt.composableLambdaInstance((int)174937338, (boolean)false, (Object)new ComposableSingletons$MainActivityKt$$ExternalSyntheticLambda2());
    }

    static final Unit lambda_1062284077$lambda$0(RowScope rowScope, Composer composer, int n) {
        Intrinsics.checkNotNullParameter((Object)rowScope, (String)"$this$Button");
        ComposerKt.sourceInformation((Composer)composer, (String)"C1519@66860L15:MainActivity.kt#ijd4h7");
        if ((n & 0x11) == 16 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)1062284077, (int)n, (int)-1, (String)"com.muslim.browser.pro.ComposableSingletons$MainActivityKt.lambda$1062284077.<anonymous> (MainActivity.kt:1519)");
            }
            TextKt.Text--4IGK_g((String)"Proceed", null, (long)0L, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)6, (int)0, (int)131070);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda_174937338$lambda$2(Composer composer, int n) {
        ComposerKt.sourceInformation((Composer)composer, (String)"C1471@64333L11,1468@64158L279:MainActivity.kt#ijd4h7");
        if ((n & 3) == 2 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)174937338, (int)n, (int)-1, (String)"com.muslim.browser.pro.ComposableSingletons$MainActivityKt.lambda$174937338.<anonymous> (MainActivity.kt:1468)");
            }
            ImageVector imageVector = WarningKt.getWarning((Icons.Filled)Icons.INSTANCE.getDefault());
            long l = MaterialTheme.INSTANCE.getColorScheme(composer, MaterialTheme.$stable).getError-0d7_KjU();
            Modifier modifier = SizeKt.size-3ABfNKs((Modifier)((Modifier)Modifier.Companion), (float)Dp.constructor-impl((float)28));
            IconKt.Icon-ww6aTOc((ImageVector)imageVector, null, (Modifier)modifier, (long)l, (Composer)composer, (int)432, (int)0);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    static final Unit lambda__2016551255$lambda$1(RowScope rowScope, Composer composer, int n) {
        Intrinsics.checkNotNullParameter((Object)rowScope, (String)"$this$OutlinedButton");
        ComposerKt.sourceInformation((Composer)composer, (String)"C1530@67371L24:MainActivity.kt#ijd4h7");
        if ((n & 0x11) == 16 && composer.getSkipping()) {
            composer.skipToGroupEnd();
        } else {
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventStart((int)-2016551255, (int)n, (int)-1, (String)"com.muslim.browser.pro.ComposableSingletons$MainActivityKt.lambda$-2016551255.<anonymous> (MainActivity.kt:1530)");
            }
            TextKt.Text--4IGK_g((String)"Cancel / Go Back", null, (long)0L, (long)0L, null, null, null, (long)0L, null, null, (long)0L, (int)0, (boolean)false, (int)0, (int)0, null, null, (Composer)composer, (int)6, (int)0, (int)131070);
            if (ComposerKt.isTraceInProgress()) {
                ComposerKt.traceEventEnd();
            }
        }
        return Unit.INSTANCE;
    }

    public final Function3<RowScope, Composer, Integer, Unit> getLambda$-2016551255$app() {
        return lambda$-2016551255;
    }

    public final Function3<RowScope, Composer, Integer, Unit> getLambda$1062284077$app() {
        return lambda$1062284077;
    }

    public final Function2<Composer, Integer, Unit> getLambda$174937338$app() {
        return lambda$174937338;
    }
}

