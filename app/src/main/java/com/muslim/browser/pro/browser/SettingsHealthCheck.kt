package com.muslim.browser.pro.browser

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.muslim.browser.pro.MuslimBrowserApp
import com.muslim.browser.pro.ui.theme.AppTheme
import com.muslim.browser.pro.ui.theme.LocalAppColors
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.File
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

/**
 * সেটিং স্বাস্থ্য পরীক্ষা (Settings Health Check)
 * অ্যাপের সেটিংস ও পারফরম্যান্স বিশ্লেষণ, সমস্যা ও পরামর্শ চিহ্নিতকরণ, ব্যবহারকারীর অনুমোদন সাপেক্ষে
 * সংশোধন, রিভার্ট, ফাইল পাথ ও সেটিং কী সংবলিত টেক্সট ও JSON রিপোর্ট এবং Header-ভিত্তিক Gemini AI বিশ্লেষণ।
 */

enum class HealthActionStatus {
    PENDING,   // অমীমাংসিত
    APPROVED,  // অনুমোদিত ও সংশোধিত / জ্ঞাত
    REJECTED,  // বাতিলকৃত
    REVERTED   // পূর্বাবস্থায় ফেরানো
}

data class HealthIssue(
    val id: String,
    val settingName: String,
    val settingKey: String,
    val filePath: String,
    val currentStatus: String,
    val problemDescription: String,
    val recommendation: String,
    val isSuggestionOnly: Boolean = false, // সমস্যা নাকি পরামর্শ
    val hasAutoFix: Boolean = true,        // স্বয়ংক্রিয় সমাধান আছে কি না
    val needsCodeChange: Boolean = false,  // কোড পরিবর্তন প্রয়োজন কি না
    val actionStatus: HealthActionStatus = HealthActionStatus.PENDING,
    val actionMessage: String? = null,
    val previousValue: String? = null
)

object SettingsHealthChecker {

    private const val PREFS_HEALTH_CHECK = "settings_health_check_prefs"
    private const val KEY_GEMINI_API_KEY = "key_gemini_api_key"
    private const val KEY_ENABLE_GEMINI = "key_enable_gemini_ai"

    fun getSavedApiKey(context: Context): String {
        return context.getSharedPreferences(PREFS_HEALTH_CHECK, Context.MODE_PRIVATE)
            .getString(KEY_GEMINI_API_KEY, "") ?: ""
    }

    fun saveApiKey(context: Context, key: String) {
        context.getSharedPreferences(PREFS_HEALTH_CHECK, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_GEMINI_API_KEY, key.trim())
            .apply()
    }

    fun isGeminiEnabled(context: Context): Boolean {
        return context.getSharedPreferences(PREFS_HEALTH_CHECK, Context.MODE_PRIVATE)
            .getBoolean(KEY_ENABLE_GEMINI, false)
    }

    fun setGeminiEnabled(context: Context, enabled: Boolean) {
        context.getSharedPreferences(PREFS_HEALTH_CHECK, Context.MODE_PRIVATE)
            .edit()
            .putBoolean(KEY_ENABLE_GEMINI, enabled)
            .apply()
    }

    /**
     * অ্যাপের ক্যাশ মেমোরির আকার নির্ণয়
     */
    fun calculateCacheSizeBytes(context: Context): Long {
        var total = 0L
        fun dirSize(dir: File?): Long {
            if (dir == null || !dir.exists()) return 0L
            var sum = 0L
            dir.listFiles()?.forEach { file ->
                sum += if (file.isDirectory) dirSize(file) else file.length()
            }
            return sum
        }
        total += dirSize(context.cacheDir)
        total += dirSize(context.codeCacheDir)
        return total
    }

    fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 -> String.format(Locale.US, "%.2f MB", bytes.toDouble() / (1024 * 1024))
            bytes >= 1024 -> String.format(Locale.US, "%.1f KB", bytes.toDouble() / 1024)
            else -> "$bytes Bytes"
        }
    }

    /**
     * স্বাস্থ্য পরীক্ষা পরিচালনা এবং সমস্যা ও পরামর্শের তালিকা তৈরি
     */
    fun runDiagnostics(context: Context, uiState: BrowserUiState): List<HealthIssue> {
        val issues = mutableListOf<HealthIssue>()

        // ১. ক্যাশ মেমোরি পরীক্ষা
        val cacheSize = calculateCacheSizeBytes(context)
        if (cacheSize > 3 * 1024 * 1024) { // ৩ MB এর বেশি হলে
            issues.add(
                HealthIssue(
                    id = "cache_size",
                    settingName = "ক্যাশ মেমোরি",
                    settingKey = "context.cacheDir, context.codeCacheDir",
                    filePath = "Android Cache Storage (context.cacheDir)",
                    currentStatus = "${formatBytes(cacheSize)} ক্যাশ জমা রয়েছে",
                    problemDescription = "অতিরিক্ত টেম্পোরারি ক্যাশ জমে ফোনের মেমোরি দখল করছে এবং ব্রাউজারকে ধীর করতে পারে।",
                    recommendation = "ক্যাশ মুছে মেমোরি হালকা করুন। কোনো ব্যক্তিগত তথ্য বা পাসওয়ার্ড মুছবে না।",
                    isSuggestionOnly = false,
                    hasAutoFix = true,
                    needsCodeChange = false
                )
            )
        }

        // ২. বিজ্ঞাপন রোধ (Ad Blocking) - পরামর্শ
        if (!uiState.isAdBlockingEnabled) {
            issues.add(
                HealthIssue(
                    id = "ad_blocking",
                    settingName = "বিজ্ঞাপন রোধ (Ad Blocking)",
                    settingKey = "BrowserUiState.isAdBlockingEnabled / SettingsRepository",
                    filePath = "app/src/main/java/com/muslim/browser/pro/browser/ui/BrowserMenuSheet.kt",
                    currentStatus = "বর্তমানে বন্ধ রয়েছে",
                    problemDescription = "পরামর্শ: বিজ্ঞাপন রোধ বন্ধ থাকায় ওয়েবপেজে অযাচিত ট্র্যাকার ও বিজ্ঞাপন লোড হয়ে ব্যান্ডউইথ খরচ ও লোডিং বিলম্ব হতে পারে।",
                    recommendation = "বিজ্ঞাপন রোধ চালু রাখুন। এটি পৃষ্ঠা লোড দ্রুত করবে ও ডেটা বাঁচাবে।",
                    isSuggestionOnly = true,
                    hasAutoFix = true,
                    needsCodeChange = false
                )
            )
        }

        // ৩. পপ-আপ ব্লকিং - পরামর্শ
        if (!uiState.isPopupBlockingEnabled) {
            issues.add(
                HealthIssue(
                    id = "popup_blocking",
                    settingName = "পপ-আপ রোধ (Pop-up Blocking)",
                    settingKey = "BrowserUiState.isPopupBlockingEnabled / SettingsRepository",
                    filePath = "app/src/main/java/com/muslim/browser/pro/browser/ui/BrowserMenuSheet.kt",
                    currentStatus = "বর্তমানে বন্ধ রয়েছে",
                    problemDescription = "পরামর্শ: পপ-আপ ব্লকিং বন্ধ থাকলে ক্ষতিকর সাইটের নতুন ট্যাব বা বিরক্তিকর পপ-আপ প্রদর্শিত হতে পারে।",
                    recommendation = "নিরাপদ ও বাধাহীন ব্রাউজিংয়ের জন্য পপ-আপ ব্লকিং চালু করুন।",
                    isSuggestionOnly = true,
                    hasAutoFix = true,
                    needsCodeChange = false
                )
            )
        }

        // ৪. অনুবাদ নিশ্চিতকরণ প্রাইভেসি পরীক্ষা
        val transPrefs = context.getSharedPreferences("translation_privacy_prefs", Context.MODE_PRIVATE)
        val skipTranslationConfirm = transPrefs.getBoolean("key_skip_translation_confirmation", false)
        if (skipTranslationConfirm) {
            issues.add(
                HealthIssue(
                    id = "translation_confirm",
                    settingName = "অনুবাদ প্রাইভেসি সতর্কতা",
                    settingKey = "translation_privacy_prefs -> key_skip_translation_confirmation",
                    filePath = "app/src/main/java/com/muslim/browser/pro/browser/ui/BrowserMenuSheet.kt",
                    currentStatus = "সতর্কবার্তা এড়িয়ে যাওয়া সক্রিয় ('আর জিজ্ঞাসা করবেন না')",
                    problemDescription = "অনুবাদ করার আগের নিশ্চয়তা ডায়ালগ বন্ধ রয়েছে। এর ফলে সংবেদনশীল টেক্সট অসাবধানতাবশত রিমোট ট্রান্সলেশনে চলে যাওয়ার ঝুঁকি থাকে।",
                    recommendation = "অনুবাদ নিশ্চিতকরণ ডায়ালগ পুনরায় সক্রিয় রাখুন।",
                    isSuggestionOnly = false,
                    hasAutoFix = true,
                    needsCodeChange = false
                )
            )
        }

        // ৫. স্মার্ট ফর্ম অটোফিল প্রোফাইল পরীক্ষা (স্বয়ংক্রিয় সমাধান নেই, শুধু ম্যানুয়াল প্রোফাইল সেটআপ)
        val hasAutofill = AutofillRepository.hasSavedProfile(context)
        if (!hasAutofill) {
            issues.add(
                HealthIssue(
                    id = "autofill_profile",
                    settingName = "স্মার্ট ফর্ম অটোফিল",
                    settingKey = "autofill_profile_prefs -> key_has_saved_profile, key_profile_json",
                    filePath = "app/src/main/java/com/muslim/browser/pro/browser/AutofillRepository.kt",
                    currentStatus = "কোনো প্রোফাইল সংরক্ষিত নেই",
                    problemDescription = "অটোফিল প্রোফাইল খালি থাকায় বিভিন্ন ওয়েব ফর্মে ব্যক্তিগত তথ্য বারবার হাতে লিখতে হবে।",
                    recommendation = "মেনুর অটোফিল অপশন থেকে আপনার ব্যক্তিগত তথ্য পূরণ করে প্রোফাইল তৈরি করুন।",
                    isSuggestionOnly = true,
                    hasAutoFix = false,
                    needsCodeChange = true // স্বয়ংক্রিয় ফিক্স নেই, ব্যবহারকারীকে নিজে তথ্য দিতে হবে
                )
            )
        }

        // ৬. থিম কনফিগারেশন চেক
        val shieldPrefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
        val savedTheme = shieldPrefs.getString(MuslimBrowserApp.KEY_APP_THEME, null)
        if (savedTheme == MuslimBrowserApp.LEGACY_FORCE_DARK_ON) {
            issues.add(
                HealthIssue(
                    id = "legacy_theme",
                    settingName = "অ্যাপ থিম",
                    settingKey = "focus_shield_prefs -> key_app_theme",
                    filePath = "app/src/main/java/com/muslim/browser/pro/MuslimBrowserApp.kt",
                    currentStatus = "অপ্রচলিত থিম ($savedTheme) সক্রিয়",
                    problemDescription = "পুরনো থিম সেটিংয়ের কারণে কনট্রাস্ট বা ডিসপ্লে অসামঞ্জস্য হতে পারে।",
                    recommendation = "থিমটিকে আধুনিক 'WHITE' মোডে রিসেট করুন।",
                    isSuggestionOnly = false,
                    hasAutoFix = true,
                    needsCodeChange = false,
                    previousValue = savedTheme
                )
            )
        }

        return issues
    }

    /**
     * সমাধান প্রয়োগ (অনুমোদন সাপেক্ষে)
     */
    fun applyFix(
        context: Context,
        issue: HealthIssue,
        onToggleAdBlocking: (Boolean) -> Unit,
        onTogglePopupBlocking: (Boolean) -> Unit,
        onSelectTheme: (AppTheme) -> Unit
    ): Pair<String, String?> {
        return when (issue.id) {
            "cache_size" -> {
                try {
                    context.cacheDir.deleteRecursively()
                    context.codeCacheDir.deleteRecursively()
                    Pair("ক্যাশ সম্পূর্ণ পরিষ্কার করা হয়েছে।", null)
                } catch (e: Exception) {
                    Pair("ক্যাশ পরিষ্কারে ত্রুটি: ${e.message}", null)
                }
            }
            "ad_blocking" -> {
                onToggleAdBlocking(true)
                Pair("বিজ্ঞাপন রোধ চালু করা হয়েছে।", "false")
            }
            "popup_blocking" -> {
                onTogglePopupBlocking(true)
                Pair("পপ-আপ ব্লকিং চালু করা হয়েছে।", "false")
            }
            "translation_confirm" -> {
                context.getSharedPreferences("translation_privacy_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("key_skip_translation_confirmation", false)
                    .apply()
                Pair("অনুবাদ নিশ্চিতকরণ ডায়ালগ পুনরায় সক্রিয় করা হয়েছে।", "true")
            }
            "legacy_theme" -> {
                val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
                val prev = prefs.getString(MuslimBrowserApp.KEY_APP_THEME, MuslimBrowserApp.LEGACY_FORCE_DARK_ON)
                prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, MuslimBrowserApp.THEME_WHITE).apply()
                onSelectTheme(AppTheme.WHITE)
                Pair("থিম সফলভাবে 'WHITE' মোডে রিসেট করা হয়েছে।", prev)
            }
            else -> {
                Pair("পদক্ষেপ সম্পন্ন হয়েছে।", null)
            }
        }
    }

    /**
     * সমাধান পূর্বাবস্থায় ফেরানো (Revert)
     */
    fun revertFix(
        context: Context,
        issue: HealthIssue,
        onToggleAdBlocking: (Boolean) -> Unit,
        onTogglePopupBlocking: (Boolean) -> Unit,
        onSelectTheme: (AppTheme) -> Unit
    ): String {
        return when (issue.id) {
            "ad_blocking" -> {
                onToggleAdBlocking(false)
                "বিজ্ঞাপন রোধ পুনরায় পূর্বের অবস্থায় (বন্ধ) নেওয়া হয়েছে।"
            }
            "popup_blocking" -> {
                onTogglePopupBlocking(false)
                "পপ-আপ ব্লকিং পুনরায় পূর্বের অবস্থায় (বন্ধ) নেওয়া হয়েছে।"
            }
            "translation_confirm" -> {
                context.getSharedPreferences("translation_privacy_prefs", Context.MODE_PRIVATE)
                    .edit()
                    .putBoolean("key_skip_translation_confirmation", true)
                    .apply()
                "অনুবাদ সতর্কবার্তা পুনরায় এড়িয়ে চলার মোডে নেওয়া হয়েছে।"
            }
            "legacy_theme" -> {
                val prev = issue.previousValue ?: MuslimBrowserApp.LEGACY_FORCE_DARK_ON
                val prefs = context.getSharedPreferences(MuslimBrowserApp.PREFS_NAME, Context.MODE_PRIVATE)
                prefs.edit().putString(MuslimBrowserApp.KEY_APP_THEME, prev).apply()
                if (prev == "BLACK_WHITE") {
                    onSelectTheme(AppTheme.BLACK_WHITE)
                } else {
                    onSelectTheme(AppTheme.WHITE)
                }
                "থিম পুনরায় পূর্বের সেটিংসে ($prev) ফিরিয়ে নেওয়া হয়েছে।"
            }
            "cache_size" -> {
                "মুছে ফেলা ক্যাশ ফিরিয়ে আনা সম্ভব নয়। ব্রাউজিং করলে প্রয়োজনে নতুন ক্যাশ নিজে থেকেই তৈরি হবে।"
            }
            else -> {
                "পূর্বাবস্থায় ফেরানো হয়েছে।"
            }
        }
    }

    /**
     * টেক্সট রিপোর্ট তৈরি (ফাইল নাম ও সেটিং কী সহ)
     */
    fun generateTextReport(issues: List<HealthIssue>): String {
        val sdf = SimpleDateFormat("dd MMMM, yyyy - hh:mm a", Locale("bn", "BD"))
        val currentTime = sdf.format(Date())

        val sb = StringBuilder()
        sb.append("=========================================\n")
        sb.append("   Muslim Browser Pro - সেটিং স্বাস্থ্য পরীক্ষা রিপোর্ট\n")
        sb.append("=========================================\n")
        sb.append("তারিখ ও সময়: $currentTime\n")
        sb.append("মোট বিশ্লেষিত বিষয়: ${issues.size}টি\n")
        sb.append("অনুমোদিত সমাধান: ${issues.count { it.actionStatus == HealthActionStatus.APPROVED }}টি\n")
        sb.append("বাতিলকৃত বিষয়: ${issues.count { it.actionStatus == HealthActionStatus.REJECTED }}টি\n")
        sb.append("অমীমাংসিত বিষয়: ${issues.count { it.actionStatus == HealthActionStatus.PENDING }}টি\n")
        sb.append("পূর্বাবস্থায় ফেরানো: ${issues.count { it.actionStatus == HealthActionStatus.REVERTED }}টি\n\n")

        sb.append("---------------- আইটেমভিত্তিক বিস্তারিত বিবরণ ----------------\n\n")

        if (issues.isEmpty()) {
            sb.append("অভিনন্দন! আপনার ব্রাউজারের সকল সেটিং সম্পূর্ণ সুস্থ ও অপ্টিমাইজড অবস্থায় রয়েছে।\n")
        } else {
            issues.forEachIndexed { index, issue ->
                val typeLabel = if (issue.isSuggestionOnly) "পরামর্শ" else "সমস্যা"
                sb.append("${index + 1}. [${typeLabel}] ${issue.settingName}\n")
                sb.append("   - সংশ্লিষ্ট ফাইল: ${issue.filePath}\n")
                sb.append("   - সেটিং কী (Key): ${issue.settingKey}\n")
                sb.append("   - বর্তমান অবস্থা: ${issue.currentStatus}\n")
                sb.append("   - বিবরণ: ${issue.problemDescription}\n")
                sb.append("   - সুপারিশ: ${issue.recommendation}\n")
                val statusText = when (issue.actionStatus) {
                    HealthActionStatus.APPROVED -> "অনুমোদিত (ফলাফল: ${issue.actionMessage ?: "সম্পন্ন"})"
                    HealthActionStatus.REJECTED -> "বাতিল (ব্যবহারকারী পরিবর্তন চাননি)"
                    HealthActionStatus.REVERTED -> "আগের অবস্থায় ফেরত নেওয়া হয়েছে"
                    HealthActionStatus.PENDING -> "অমীমাংসিত"
                }
                sb.append("   - বর্তমান সিদ্ধান্ত: $statusText\n")
                sb.append("   - কোড পরিবর্তন প্রয়োজন: ${if (issue.needsCodeChange) "হ্যাঁ" else "না"}\n\n")
            }
        }

        sb.append("---------------- কোড পরিবর্তন সংক্রান্ত দিকনির্দেশনা ----------------\n")
        val codeChangeItems = issues.filter { it.needsCodeChange }
        if (codeChangeItems.isEmpty()) {
            sb.append("সবগুলো সমাধান সফলভাবে রানটাইম সেটিংসে সম্পন্ন করা হয়েছে। কোনো সোর্স কোড পরিবর্তন করতে হবে না।\n")
        } else {
            sb.append("নিচের বিষয়গুলোর জন্য ডেভেলপারকে সরাসরি সোর্স কোডে বা প্রোফাইল সেটিংসে নজর দিতে হবে:\n")
            codeChangeItems.forEach { item ->
                sb.append("• ফাইল: ${item.filePath}\n")
                sb.append("  সেটিং কী: ${item.settingKey}\n")
                sb.append("  করণীয়: ${item.recommendation}\n\n")
            }
        }
        sb.append("=========================================\n")
        return sb.toString()
    }

    /**
     * JSON রিপোর্ট তৈরি (ফাইল নাম ও সেটিং কী সহ)
     */
    fun generateJsonReport(issues: List<HealthIssue>): String {
        val root = JSONObject()
        root.put("appName", "Muslim Browser Pro")
        root.put("timestamp", System.currentTimeMillis())
        root.put("dateFormatted", SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(Date()))
        root.put("totalIssuesAnalyzed", issues.size)
        root.put("approvedCount", issues.count { it.actionStatus == HealthActionStatus.APPROVED })
        root.put("rejectedCount", issues.count { it.actionStatus == HealthActionStatus.REJECTED })
        root.put("pendingCount", issues.count { it.actionStatus == HealthActionStatus.PENDING })
        root.put("revertedCount", issues.count { it.actionStatus == HealthActionStatus.REVERTED })

        val array = JSONArray()
        issues.forEach { issue ->
            val obj = JSONObject()
            obj.put("id", issue.id)
            obj.put("settingName", issue.settingName)
            obj.put("filePath", issue.filePath)
            obj.put("settingKey", issue.settingKey)
            obj.put("type", if (issue.isSuggestionOnly) "SUGGESTION" else "ISSUE")
            obj.put("currentStatus", issue.currentStatus)
            obj.put("problemDescription", issue.problemDescription)
            obj.put("recommendation", issue.recommendation)
            obj.put("hasAutoFix", issue.hasAutoFix)
            obj.put("decision", issue.actionStatus.name)
            obj.put("actionMessage", issue.actionMessage ?: "")
            obj.put("needsCodeChange", issue.needsCodeChange)
            array.put(obj)
        }
        root.put("items", array)
        return root.toString(2)
    }

    /**
     * ঐচ্ছিক Gemini API পরামর্শ আনা
     * API Key URL-এ নয়, বরং নিরাপদ 'x-goog-api-key' Header-এ পাঠানো হয়।
     */
    suspend fun queryGeminiSuggestions(apiKey: String, issues: List<HealthIssue>): String = withContext(Dispatchers.IO) {
        val cleanKey = apiKey.trim()
        if (cleanKey.isBlank()) {
            return@withContext "ত্রুটি: অনুগ্রহ করে আপনার সঠিক Gemini API Key প্রদান করুন।"
        }

        val promptBuilder = StringBuilder()
        promptBuilder.append("You are an expert Android Browser performance consultant for Muslim Browser Pro.\n")
        promptBuilder.append("Analyze the following health check findings and provide 2-3 clear, gentle optimization suggestions in plain, easy Bengali (সহজ বাংলায়).\n")
        promptBuilder.append("No markdown codeblocks, just friendly, clear advice for a non-programmer user.\n\n")
        promptBuilder.append("Findings:\n")
        issues.forEach { issue ->
            val typeStr = if (issue.isSuggestionOnly) "Suggestion" else "Issue"
            promptBuilder.append("- [$typeStr] Setting: ${issue.settingName}, Key: ${issue.settingKey}, File: ${issue.filePath}, Status: ${issue.currentStatus}\n")
        }

        val requestJson = JSONObject()
        val contentsArray = JSONArray()
        val contentObj = JSONObject()
        val partsArray = JSONArray()
        val partObj = JSONObject()
        partObj.put("text", promptBuilder.toString())
        partsArray.put(partObj)
        contentObj.put("parts", partsArray)
        contentsArray.put(contentObj)
        requestJson.put("contents", contentsArray)

        try {
            // URL-এ API Key ছাড়া বিশুদ্ধ এন্ডপয়েন্ট
            val endpoint = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
            val url = URL(endpoint)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            // API Key পাঠানো হচ্ছে 'x-goog-api-key' হেডার দিয়ে
            connection.setRequestProperty("x-goog-api-key", cleanKey)
            connection.connectTimeout = 30000
            connection.readTimeout = 30000
            connection.doOutput = true

            OutputStreamWriter(connection.outputStream).use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val responseText = connection.inputStream.bufferedReader().use(BufferedReader::readText)
                val responseJson = JSONObject(responseText)
                val candidates = responseJson.optJSONArray("candidates")
                val firstCandidate = candidates?.optJSONObject(0)
                val content = firstCandidate?.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                val text = parts?.optJSONObject(0)?.optString("text")
                text ?: "কোনো পরামর্শ পাওয়া যায়নি।"
            } else {
                val errorText = connection.errorStream?.bufferedReader()?.use(BufferedReader::readText) ?: ""
                "Gemini API ত্রুটি ($responseCode): $errorText"
            }
        } catch (e: Exception) {
            "Gemini সংযোগ ব্যর্থ: ${e.localizedMessage ?: "ইন্টারনেট সংযোগ চেক করুন"}"
        }
    }
}

/**
 * সেটিং স্বাস্থ্য পরীক্ষা প্রধান ডায়ালগ UI
 */
@Composable
fun SettingsHealthCheckDialog(
    uiState: BrowserUiState,
    onToggleAdBlocking: (Boolean) -> Unit,
    onTogglePopupBlocking: (Boolean) -> Unit,
    onSelectTheme: (AppTheme) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current
    val scope = rememberCoroutineScope()

    var issues by remember {
        mutableStateOf(SettingsHealthChecker.runDiagnostics(context, uiState))
    }

    var showReportDialog by remember { mutableStateOf(false) }
    var showAiSettingsDialog by remember { mutableStateOf(false) }

    var geminiAiEnabled by remember {
        mutableStateOf(SettingsHealthChecker.isGeminiEnabled(context))
    }
    var geminiApiKey by remember {
        mutableStateOf(SettingsHealthChecker.getSavedApiKey(context))
    }
    var aiSuggestions by remember { mutableStateOf<String?>(null) }
    var isAiLoading by remember { mutableStateOf(false) }

    val runAiAnalysis = {
        if (!geminiAiEnabled || geminiApiKey.isBlank()) {
            showAiSettingsDialog = true
        } else {
            isAiLoading = true
            scope.launch {
                aiSuggestions = SettingsHealthChecker.queryGeminiSuggestions(geminiApiKey, issues)
                isAiLoading = false
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.HealthAndSafety,
                            contentDescription = null,
                            tint = colors.accent,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "সেটিং স্বাস্থ্য পরীক্ষা",
                            color = colors.textPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "বন্ধ করুন",
                            tint = colors.textSecondary
                        )
                    }
                }

                HorizontalDivider(color = colors.border, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                // Summary Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "চিহ্নিত বিষয়", fontSize = 11.sp, color = colors.textSecondary)
                        Text(
                            text = "${issues.size}টি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (issues.isNotEmpty()) Color(0xFFE53935) else Color(0xFF43A047)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "অনুমোদিত", fontSize = 11.sp, color = colors.textSecondary)
                        Text(
                            text = "${issues.count { it.actionStatus == HealthActionStatus.APPROVED }}টি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF43A047)
                        )
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "বাতিল", fontSize = 11.sp, color = colors.textSecondary)
                        Text(
                            text = "${issues.count { it.actionStatus == HealthActionStatus.REJECTED }}টি",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.textSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar (পুনরায় পরীক্ষা, AI বিশ্লেষণ চালাও, রিপোর্ট)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            issues = SettingsHealthChecker.runDiagnostics(context, uiState)
                            Toast.makeText(context, "পুনরায় স্বাস্থ্য পরীক্ষা সম্পন্ন হয়েছে", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "পুনরায় পরীক্ষা", fontSize = 11.sp)
                    }

                    // মূল স্ক্রিনে আলাদা "AI বিশ্লেষণ চালাও" বোতাম
                    Button(
                        onClick = { runAiAnalysis() },
                        modifier = Modifier.weight(1.25f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        if (isAiLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(15.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(15.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "AI বিশ্লেষণ চালাও", fontSize = 11.sp, color = Color.White)
                    }

                    IconButton(
                        onClick = { showAiSettingsDialog = true },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = "AI সেটিংস", tint = colors.textSecondary, modifier = Modifier.size(18.dp))
                    }

                    OutlinedButton(
                        onClick = { showReportDialog = true },
                        modifier = Modifier.weight(0.9f),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Assessment, contentDescription = null, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(text = "রিপোর্ট", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // AI Suggestions Box if available
                if (aiSuggestions != null) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = colors.surfaceVariant),
                        border = BorderStroke(1.dp, colors.accent.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gemini AI পরামর্শ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = colors.accent)
                                }
                                IconButton(
                                    onClick = { aiSuggestions = null },
                                    modifier = Modifier.size(20.dp)
                                ) {
                                    Icon(Icons.Default.Close, contentDescription = "বন্ধ", modifier = Modifier.size(14.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = aiSuggestions ?: "",
                                fontSize = 11.5.sp,
                                color = colors.textPrimary,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Issues & Suggestions List
                if (issues.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Color(0xFF43A047),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "মাশাআল্লাহ! ব্রাউজারের সকল সেটিং সম্পূর্ণ সুস্থ রয়েছে।",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textPrimary
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(issues, key = { it.id }) { issue ->
                            IssueItemCard(
                                issue = issue,
                                onApprove = {
                                    val (msg, prevVal) = SettingsHealthChecker.applyFix(
                                        context = context,
                                        issue = issue,
                                        onToggleAdBlocking = onToggleAdBlocking,
                                        onTogglePopupBlocking = onTogglePopupBlocking,
                                        onSelectTheme = onSelectTheme
                                    )
                                    // copy() ব্যবহার করে নিশ্চিত রি-কম্পোজিশন
                                    issues = issues.map { item ->
                                        if (item.id == issue.id) {
                                            item.copy(
                                                actionStatus = HealthActionStatus.APPROVED,
                                                actionMessage = msg,
                                                previousValue = prevVal ?: item.previousValue
                                            )
                                        } else item
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                onAcknowledge = {
                                    // যেটির স্বয়ংক্রিয় সমাধান নেই (অটোফিল) - "বুঝেছি" চাপলে
                                    issues = issues.map { item ->
                                        if (item.id == issue.id) {
                                            item.copy(
                                                actionStatus = HealthActionStatus.APPROVED,
                                                actionMessage = "ব্যবহারকারী জ্ঞাত হয়েছেন (ম্যানুয়াল প্রোফাইল তৈরি প্রয়োজন)",
                                                needsCodeChange = true
                                            )
                                        } else item
                                    }
                                    Toast.makeText(context, "অবহিত করা হয়েছে", Toast.LENGTH_SHORT).show()
                                },
                                onReject = {
                                    issues = issues.map { item ->
                                        if (item.id == issue.id) {
                                            item.copy(
                                                actionStatus = HealthActionStatus.REJECTED,
                                                actionMessage = "ব্যবহারকারী পরিবর্তন চাননি"
                                            )
                                        } else item
                                    }
                                },
                                onRevert = {
                                    val msg = SettingsHealthChecker.revertFix(
                                        context = context,
                                        issue = issue,
                                        onToggleAdBlocking = onToggleAdBlocking,
                                        onTogglePopupBlocking = onTogglePopupBlocking,
                                        onSelectTheme = onSelectTheme
                                    )
                                    issues = issues.map { item ->
                                        if (item.id == issue.id) {
                                            item.copy(
                                                actionStatus = HealthActionStatus.REVERTED,
                                                actionMessage = msg
                                            )
                                        } else item
                                    }
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Report Dialog (Text & JSON Export with File Path and Key)
    if (showReportDialog) {
        HealthCheckReportDialog(
            issues = issues,
            onDismiss = { showReportDialog = false }
        )
    }

    // AI Settings Dialog
    if (showAiSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showAiSettingsDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = colors.accent)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ঐচ্ছিক Gemini AI কনফিগারেশন", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Gemini AI বিশ্লেষণ সক্রিয়", fontSize = 13.sp, color = colors.textPrimary)
                        Switch(
                            checked = geminiAiEnabled,
                            onCheckedChange = { checked ->
                                geminiAiEnabled = checked
                                SettingsHealthChecker.setGeminiEnabled(context, checked)
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Gemini API Key:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = geminiApiKey,
                        onValueChange = { geminiApiKey = it },
                        placeholder = { Text("AIzaSy...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "নিরাপত্তা নিশ্চয়তা: এপিআই কী URL-এ নয়, সুরক্ষিত হেডার (x-goog-api-key) দিয়ে পাঠানো হয়।",
                        fontSize = 10.5.sp,
                        color = colors.accent
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        SettingsHealthChecker.saveApiKey(context, geminiApiKey)
                        SettingsHealthChecker.setGeminiEnabled(context, geminiAiEnabled)
                        showAiSettingsDialog = false

                        if (geminiAiEnabled && geminiApiKey.isNotBlank()) {
                            runAiAnalysis()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                ) {
                    Text("সংরক্ষণ করুন", color = Color.White, fontSize = 12.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAiSettingsDialog = false }) {
                    Text("বাতিল", color = colors.textSecondary)
                }
            },
            containerColor = colors.surface,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

/**
 * সমস্যার একক কার্ড ভিউ
 */
@Composable
private fun IssueItemCard(
    issue: HealthIssue,
    onApprove: () -> Unit,
    onAcknowledge: () -> Unit,
    onReject: () -> Unit,
    onRevert: () -> Unit
) {
    val colors = LocalAppColors.current

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(
            1.dp,
            when (issue.actionStatus) {
                HealthActionStatus.APPROVED -> Color(0xFF43A047)
                HealthActionStatus.REJECTED -> colors.border
                HealthActionStatus.REVERTED -> Color(0xFFFFA000)
                HealthActionStatus.PENDING -> if (issue.isSuggestionOnly) colors.accent.copy(alpha = 0.5f) else Color(0xFFE53935).copy(alpha = 0.5f)
            }
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header: Name and Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(3.dp),
                        color = if (issue.isSuggestionOnly) colors.accent.copy(alpha = 0.2f) else Color(0x22E53935)
                    ) {
                        Text(
                            text = if (issue.isSuggestionOnly) "পরামর্শ" else "সমস্যা",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (issue.isSuggestionOnly) colors.accent else Color(0xFFC62828),
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = issue.settingName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp,
                        color = colors.textPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (issue.actionStatus) {
                        HealthActionStatus.APPROVED -> Color(0x3343A047)
                        HealthActionStatus.REJECTED -> Color(0x22888888)
                        HealthActionStatus.REVERTED -> Color(0x33FFA000)
                        HealthActionStatus.PENDING -> Color(0x22E53935)
                    }
                ) {
                    Text(
                        text = when (issue.actionStatus) {
                            HealthActionStatus.APPROVED -> if (!issue.hasAutoFix) "অবহিত" else "অনুমোদিত"
                            HealthActionStatus.REJECTED -> "বাতিল"
                            HealthActionStatus.REVERTED -> "পূর্বাবস্থায়"
                            HealthActionStatus.PENDING -> "অমীমাংসিত"
                        },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = when (issue.actionStatus) {
                            HealthActionStatus.APPROVED -> Color(0xFF2E7D32)
                            HealthActionStatus.REJECTED -> colors.textSecondary
                            HealthActionStatus.REVERTED -> Color(0xFFE65100)
                            HealthActionStatus.PENDING -> Color(0xFFC62828)
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "বর্তমান অবস্থা: ${issue.currentStatus}", fontSize = 11.5.sp, color = colors.textSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = issue.problemDescription, fontSize = 11.5.sp, color = colors.textPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = "সুপারিশ: ${issue.recommendation}", fontSize = 11.5.sp, color = colors.accent, fontWeight = FontWeight.Medium)

            // ফাইল পাথ ও সেটিং কী
            Spacer(modifier = Modifier.height(4.dp))
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = colors.surface.copy(alpha = 0.5f),
                border = BorderStroke(0.5.dp, colors.border.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)) {
                    Text(text = "ফাইল: ${issue.filePath}", fontSize = 9.5.sp, color = colors.textSecondary, fontFamily = FontFamily.Monospace)
                    Text(text = "কী (Key): ${issue.settingKey}", fontSize = 9.5.sp, color = colors.textSecondary, fontFamily = FontFamily.Monospace)
                }
            }

            if (issue.actionMessage != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "ফলাফল: ${issue.actionMessage}", fontSize = 11.sp, color = Color(0xFF2E7D32), fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                when (issue.actionStatus) {
                    HealthActionStatus.PENDING -> {
                        OutlinedButton(
                            onClick = onReject,
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("বাতিল", fontSize = 11.sp, color = colors.textSecondary)
                        }
                        Spacer(modifier = Modifier.width(8.dp))

                        if (!issue.hasAutoFix) {
                            // স্বয়ংক্রিয় সমাধান নেই এমন ক্ষেত্রে "অনুমোদন" এর বদলে "বুঝেছি"
                            Button(
                                onClick = onAcknowledge,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("বুঝেছি", fontSize = 11.sp, color = Color.White)
                            }
                        } else {
                            Button(
                                onClick = onApprove,
                                shape = RoundedCornerShape(6.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color.White)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("অনুমোদন", fontSize = 11.sp, color = Color.White)
                            }
                        }
                    }
                    HealthActionStatus.APPROVED -> {
                        if (issue.hasAutoFix) {
                            OutlinedButton(
                                onClick = onRevert,
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Icon(Icons.Default.Undo, contentDescription = null, modifier = Modifier.size(13.dp), tint = Color(0xFFE65100))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("আগের অবস্থায় ফেরাও", fontSize = 11.sp, color = Color(0xFFE65100))
                            }
                        }
                    }
                    HealthActionStatus.REJECTED, HealthActionStatus.REVERTED -> {
                        Button(
                            onClick = if (!issue.hasAutoFix) onAcknowledge else onApprove,
                            shape = RoundedCornerShape(6.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("পুনরায় অনুমোদন", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

/**
 * রিপোর্ট এক্সপোর্ট ডায়ালগ (Text ও JSON প্রিভিউ, কপি ও শেয়ার)
 */
@Composable
private fun HealthCheckReportDialog(
    issues: List<HealthIssue>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    var selectedFormatIndex by remember { mutableStateOf(0) } // 0: Text, 1: JSON

    val textReport = remember(issues) { SettingsHealthChecker.generateTextReport(issues) }
    val jsonReport = remember(issues) { SettingsHealthChecker.generateJsonReport(issues) }

    val currentContent = if (selectedFormatIndex == 0) textReport else jsonReport

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .fillMaxHeight(0.86f),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "স্বাস্থ্য পরীক্ষা রিপোর্ট এক্সপোর্ট",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = colors.textPrimary
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "বন্ধ", tint = colors.textSecondary)
                    }
                }

                // Format Selector (Text vs JSON)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        onClick = { selectedFormatIndex = 0 },
                        shape = RoundedCornerShape(topStart = 8.dp, bottomStart = 8.dp),
                        color = if (selectedFormatIndex == 0) colors.accent else colors.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(
                                "টেক্সট রিপোর্ট",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFormatIndex == 0) Color.White else colors.textPrimary
                            )
                        }
                    }
                    Surface(
                        onClick = { selectedFormatIndex = 1 },
                        shape = RoundedCornerShape(topEnd = 8.dp, bottomEnd = 8.dp),
                        color = if (selectedFormatIndex == 1) colors.accent else colors.surfaceVariant,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(vertical = 6.dp)) {
                            Text(
                                "JSON ডেটা",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                color = if (selectedFormatIndex == 1) Color.White else colors.textPrimary
                            )
                        }
                    }
                }

                // Content View
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = colors.surfaceVariant,
                    border = BorderStroke(0.5.dp, colors.border)
                ) {
                    LazyColumn(modifier = Modifier.padding(10.dp)) {
                        item {
                            Text(
                                text = currentContent,
                                fontSize = 10.5.sp,
                                fontFamily = if (selectedFormatIndex == 1) FontFamily.Monospace else FontFamily.Default,
                                color = colors.textPrimary,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }

                // Export Actions: Copy & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Muslim Browser Report", currentContent)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "রিপোর্ট ক্লিপবোর্ডে কপি করা হয়েছে!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("কপি করুন", fontSize = 12.sp)
                    }

                    Button(
                        onClick = {
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, currentContent)
                                putExtra(Intent.EXTRA_SUBJECT, "Muslim Browser Pro - সেটিং স্বাস্থ্য পরীক্ষা রিপোর্ট")
                                type = "text/plain"
                            }
                            val shareIntent = Intent.createChooser(sendIntent, "রিপোর্ট শেয়ার করুন")
                            context.startActivity(shareIntent)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = colors.accent)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("শেয়ার করুন", fontSize = 12.sp, color = Color.White)
                    }
                }
            }
        }
    }
}
