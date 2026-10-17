package com.muslim.browser.pro.browser

import android.content.Context
import android.webkit.WebView
import org.json.JSONObject

/**
 * Executes conservative DOM inspection and population on the active WebView.
 * Never auto-submits. Excludes passwords, OTPs, CAPTCHAs, payment cards, and sensitive fields.
 * Safely passes data to an anonymous IIFE without persisting global state.
 */
object AutofillEngine {

    /**
     * Executes the form autofill on the provided active WebView using the saved profile.
     */
    fun executeAutofill(
        context: Context,
        webView: WebView?,
        onResult: (filled: Int, skipped: Int, message: String) -> Unit
    ) {
        if (webView == null) {
            onResult(0, 0, "No active web page available")
            return
        }

        val profile = AutofillRepository.loadProfile(context)
        if (!profile.isEnabled) {
            onResult(0, 0, "Autofill is disabled. Enable it in Autofill Settings.")
            return
        }

        val dataPayload = buildAutofillPayloadJson(profile)
        val overwrite = profile.overwriteExistingFields

        val script = """
(function() {
    var data = $dataPayload;
    var overwrite = $overwrite;
    
    var filledCount = 0;
    var skippedCount = 0;
    
    // Explicit security exclusions
    var SENSITIVE_KEYWORDS = [
        'password', 'passwd', 'pwd', 'pass', 'token', 'otp', 'captcha', 
        'card', 'credit', 'debit', 'cvv', 'cvc', 'securitycode', 'pin', 
        'accountnumber', 'bank', 'ssn', 'taxid', 'nationalid', 'nid'
    ];
    
    function isSensitive(str) {
        if (!str) return false;
        var s = str.toLowerCase();
        for (var i = 0; i < SENSITIVE_KEYWORDS.length; i++) {
            if (s.indexOf(SENSITIVE_KEYWORDS[i]) !== -1) return true;
        }
        return false;
    }
    
    function isBengaliText(str) {
        if (!str) return false;
        return /[\u0980-\u09FF]/.test(str);
    }
    
    function getAssociatedLabelText(el) {
        var labelText = '';
        try {
            if (el.labels && el.labels.length > 0) {
                for (var i = 0; i < el.labels.length; i++) {
                    labelText += ' ' + (el.labels[i].innerText || el.labels[i].textContent || '');
                }
            }
            if (el.id) {
                var explicitLabel = document.querySelector('label[for="' + el.id + '"]');
                if (explicitLabel) {
                    labelText += ' ' + (explicitLabel.innerText || explicitLabel.textContent || '');
                }
            }
            var p = el.parentElement;
            if (p && p.tagName === 'LABEL') {
                labelText += ' ' + (p.innerText || p.textContent || '');
            }
        } catch(e) {}
        return labelText.trim();
    }
    
    function matchField(el) {
        var type = (el.type || 'text').toLowerCase();
        if (type === 'password' || type === 'hidden' || type === 'submit' || 
            type === 'button' || type === 'reset' || type === 'file' || 
            type === 'checkbox' || type === 'radio' || type === 'image') {
            return null;
        }
        if (el.disabled || el.readOnly) return null;
        
        var name = (el.name || '').toLowerCase();
        var id = (el.id || '').toLowerCase();
        var ac = (el.autocomplete || '').toLowerCase();
        var ph = (el.placeholder || '').toLowerCase();
        var aria = (el.getAttribute('aria-label') || '').toLowerCase();
        var label = getAssociatedLabelText(el);
        var labelLower = label.toLowerCase();
        
        var combined = (name + ' ' + id + ' ' + ac + ' ' + ph + ' ' + aria + ' ' + labelLower).trim();
        if (isSensitive(combined)) return null;
        
        var isBn = isBengaliText(label) || isBengaliText(ph) || el.getAttribute('lang') === 'bn' || (document.documentElement && document.documentElement.lang === 'bn');
        
        // --- 1. Father's Name Matching ---
        var isFather = combined.indexOf("father's name") !== -1 ||
                       combined.indexOf("fathers name") !== -1 ||
                       combined.indexOf("father name") !== -1 ||
                       combined.indexOf("father_name") !== -1 ||
                       combined.indexOf("fathername") !== -1 ||
                       combined.indexOf("father") !== -1 ||
                       combined.indexOf("পিতার নাম") !== -1 ||
                       combined.indexOf("বাবার নাম") !== -1 ||
                       combined.indexOf("পিতা") !== -1 ||
                       combined.indexOf("বাবা") !== -1;
        if (isFather) {
            // Disambiguate against mother
            if (combined.indexOf("mother") !== -1 || combined.indexOf("মাতা") !== -1 || combined.indexOf("মা") !== -1) return null;
            var fVal = isBn ? (data.fatherNameBn || data.fatherNameEn) : data.fatherNameEn;
            if (fVal && fVal.trim().length > 0) return fVal.trim();
            return null;
        }
        
        // --- 2. Mother's Name Matching ---
        var isMother = combined.indexOf("mother's name") !== -1 ||
                       combined.indexOf("mothers name") !== -1 ||
                       combined.indexOf("mother name") !== -1 ||
                       combined.indexOf("mother_name") !== -1 ||
                       combined.indexOf("mothername") !== -1 ||
                       combined.indexOf("mother") !== -1 ||
                       combined.indexOf("মাতার নাম") !== -1 ||
                       combined.indexOf("মায়ের নাম") !== -1 ||
                       combined.indexOf("মাতা") !== -1 ||
                       combined.indexOf("মা") !== -1;
        if (isMother) {
            if (combined.indexOf("father") !== -1 || combined.indexOf("পিতা") !== -1 || combined.indexOf("বাবা") !== -1) return null;
            var mVal = isBn ? (data.motherNameBn || data.motherNameEn) : data.motherNameEn;
            if (mVal && mVal.trim().length > 0) return mVal.trim();
            return null;
        }
        
        // --- 3. User's Name Matching ---
        // Ensure no collision with father or mother
        var isExcludedFromName = combined.indexOf("father") !== -1 || combined.indexOf("mother") !== -1 ||
                                 combined.indexOf("পিতা") !== -1 || combined.indexOf("মাতা") !== -1 ||
                                 combined.indexOf("বাবা") !== -1 || combined.indexOf("মা") !== -1 ||
                                 combined.indexOf("first") !== -1 || combined.indexOf("last") !== -1 ||
                                 combined.indexOf("sur") !== -1 || combined.indexOf("company") !== -1 ||
                                 combined.indexOf("user id") !== -1 || combined.indexOf("userid") !== -1 ||
                                 combined.indexOf("username") !== -1;
                                 
        if (!isExcludedFromName) {
            if (ac === 'name' || 
                combined.indexOf('full name') !== -1 || combined.indexOf('fullname') !== -1 || 
                combined.indexOf('applicant name') !== -1 || combined.indexOf('candidate name') !== -1 ||
                combined.indexOf('পূর্ণ নাম') !== -1 || combined.indexOf('আপনার নাম') !== -1 || combined.indexOf('প্রার্থীর নাম') !== -1 ||
                ((combined.indexOf('name') !== -1 || combined.indexOf('নাম') !== -1) && combined.indexOf('file') === -1)) {
                var uVal = isBn ? (data.userNameBn || data.userNameEn) : data.userNameEn;
                if (uVal && uVal.trim().length > 0) return uVal.trim();
                return null;
            }
        }
        
        // --- 4. Non-Name Fields (Always use single English value, even on Bengali forms) ---
        // Email
        if (type === 'email' || ac === 'email' || combined.indexOf('email') !== -1 || combined.indexOf('ইমেইল') !== -1 || combined.indexOf('ই-মেইল') !== -1) {
            return (data.email && data.email.trim().length > 0) ? data.email.trim() : null;
        }
        
        // Phone / Mobile
        if (type === 'tel' || ac === 'tel' || combined.indexOf('phone') !== -1 || combined.indexOf('mobile') !== -1 || combined.indexOf('মোবাইল') !== -1 || combined.indexOf('ফোন') !== -1 || combined.indexOf('contact no') !== -1) {
            return (data.phone && data.phone.trim().length > 0) ? data.phone.trim() : null;
        }
        
        // Address
        if (combined.indexOf('street-address') !== -1 || combined.indexOf('present address') !== -1 || combined.indexOf('permanent address') !== -1 || combined.indexOf('address') !== -1 || combined.indexOf('ঠিকানা') !== -1) {
            return (data.address && data.address.trim().length > 0) ? data.address.trim() : null;
        }
        
        // Date of Birth
        if (type === 'date' || ac === 'bday' || combined.indexOf('dob') !== -1 || combined.indexOf('birth') !== -1 || combined.indexOf('জন্ম তারিখ') !== -1) {
            return (data.dateOfBirth && data.dateOfBirth.trim().length > 0) ? data.dateOfBirth.trim() : null;
        }
        
        // Gender
        if (combined.indexOf('gender') !== -1 || combined.indexOf('sex') !== -1 || combined.indexOf('লিঙ্গ') !== -1) {
            return (data.gender && data.gender.trim().length > 0) ? data.gender.trim() : null;
        }
        
        // Nationality
        if (combined.indexOf('nationality') !== -1 || combined.indexOf('citizenship') !== -1 || combined.indexOf('জাতীয়তা') !== -1) {
            return (data.nationality && data.nationality.trim().length > 0) ? data.nationality.trim() : null;
        }
        
        // Marital Status
        if (combined.indexOf('marital') !== -1 || combined.indexOf('বৈবাহিক') !== -1) {
            return (data.maritalStatus && data.maritalStatus.trim().length > 0) ? data.maritalStatus.trim() : null;
        }
        
        // Blood Group
        if (combined.indexOf('blood') !== -1 || combined.indexOf('রক্তের গ্রুপ') !== -1) {
            return (data.bloodGroup && data.bloodGroup.trim().length > 0) ? data.bloodGroup.trim() : null;
        }
        
        // Height
        if (combined.indexOf('height') !== -1 || combined.indexOf('উচ্চতা') !== -1) {
            return (data.height && data.height.trim().length > 0) ? data.height.trim() : null;
        }
        
        // Professional Profile
        if (combined.indexOf('summary') !== -1 || combined.indexOf('objective') !== -1 || combined.indexOf('about me') !== -1 || combined.indexOf('bio') !== -1 || combined.indexOf('profile') !== -1) {
            return (data.professionalProfile && data.professionalProfile.trim().length > 0) ? data.professionalProfile.trim() : null;
        }
        
        // Declaration
        if (combined.indexOf('declaration') !== -1 || combined.indexOf('statement') !== -1 || combined.indexOf('ঘোষণা') !== -1) {
            return (data.declaration && data.declaration.trim().length > 0) ? data.declaration.trim() : null;
        }
        
        return null;
    }
    
    var inputs = document.querySelectorAll('input, textarea, select');
    for (var i = 0; i < inputs.length; i++) {
        var el = inputs[i];
        try {
            var val = matchField(el);
            if (val && val.trim().length > 0) {
                var currentVal = (el.value || '').trim();
                if (currentVal.length > 0 && !overwrite) {
                    skippedCount++;
                    continue;
                }
                
                el.focus();
                el.value = val;
                el.dispatchEvent(new Event('input', { bubbles: true }));
                el.dispatchEvent(new Event('change', { bubbles: true }));
                el.blur();
                filledCount++;
            }
        } catch(e) {
            skippedCount++;
        }
    }
    
    return JSON.stringify({
        filled: filledCount,
        skipped: skippedCount
    });
})();
""".trimIndent()

        try {
            webView.evaluateJavascript(script) { resultJson ->
                try {
                    val raw = resultJson?.trim('"', ' ', '\n', '\r', '\\') ?: "{}"
                    val unescaped = raw.replace("\\\"", "\"")
                    val json = JSONObject(unescaped)
                    val filled = json.optInt("filled", 0)
                    val skipped = json.optInt("skipped", 0)
                    val msg = if (filled > 0) {
                        "Filled $filled fields" + (if (skipped > 0) " ($skipped skipped)" else "")
                    } else if (skipped > 0) {
                        "No matching fields filled ($skipped skipped)"
                    } else {
                        "No matching supported form fields found on this page"
                    }
                    onResult(filled, skipped, msg)
                } catch (_: Exception) {
                    onResult(0, 0, "Autofill evaluated on current page")
                }
            }
        } catch (t: Throwable) {
            onResult(0, 0, "Failed to inspect page: ${t.message ?: "Unknown error"}")
        }
    }

    private fun buildAutofillPayloadJson(profile: AutofillProfile): String {
        val root = JSONObject()
        val p = profile.personalInfo

        // Exactly 6 independently editable name fields
        root.put("userNameEn", p.userNameEn)
        root.put("userNameBn", p.userNameBn)
        root.put("fatherNameEn", p.fatherNameEn)
        root.put("fatherNameBn", p.fatherNameBn)
        root.put("motherNameEn", p.motherNameEn)
        root.put("motherNameBn", p.motherNameBn)

        // Single English non-name values
        root.put("address", p.address)
        root.put("phone", p.phone)
        root.put("email", p.email)
        root.put("dateOfBirth", p.dateOfBirth)
        root.put("nationality", p.nationality)
        root.put("gender", p.gender)
        root.put("maritalStatus", p.maritalStatus)
        root.put("bloodGroup", p.bloodGroup)
        root.put("height", p.height)
        root.put("professionalProfile", profile.professionalProfile)
        root.put("declaration", profile.declaration)

        return root.toString()
    }
}
