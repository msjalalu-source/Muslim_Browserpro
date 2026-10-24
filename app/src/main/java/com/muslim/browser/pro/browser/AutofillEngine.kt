package com.muslim.browser.pro.browser

import android.content.Context
import android.webkit.WebView
import org.json.JSONArray
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

        val script = buildAutofillScript(profile)
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

    internal fun buildAutofillScript(profile: AutofillProfile): String {
        val dataPayload = buildAutofillPayloadJson(profile)
        val overwrite = profile.overwriteExistingFields

        return """
(function() {
    var data = $dataPayload;
    var overwrite = $overwrite;
    
    var filledCount = 0;
    var skippedCount = 0;
    
    // Explicit security exclusions (passwords, payment cards, OTP, token, bank account)
    // "pass" alone is excluded to ensure "Passing Year" is never blocked
    var SENSITIVE_KEYWORDS = [
        'password', 'passwd', 'pwd', 'passcode', 'passphrase', 'token', 'otp', 'captcha', 
        'card', 'credit', 'debit', 'cvv', 'cvc', 'securitycode', 'pin', 
        'accountnumber', 'bank', 'ssn', 'taxid'
    ];
    
    function isSensitive(str) {
        if (!str) return false;
        var s = str.toLowerCase();
        // Passing year or pass year is explicitly safe
        if (s.indexOf('passing') !== -1 || s.indexOf('pass year') !== -1 || s.indexOf('pass_year') !== -1 || s.indexOf('passing_year') !== -1) {
            return false;
        }
        for (var i = 0; i < SENSITIVE_KEYWORDS.length; i++) {
            if (s.indexOf(SENSITIVE_KEYWORDS[i]) !== -1) return true;
        }
        return false;
    }
    
    function isBengaliText(str) {
        if (!str) return false;
        return /[\u0980-\u09FF]/.test(str);
    }

    function normalizeText(str) {
        if (!str) return '';
        return str.toString()
            .toLowerCase()
            .replace(/[\u200B-\u200D\uFEFF]/g, '')
            .replace(/[০-৯]/g, function(d) {
                return String.fromCharCode(d.charCodeAt(0) - 0x09E6 + 48);
            })
            .replace(/[\s\t\r\n\u00A0]+/g, ' ')
            .trim();
    }

    function normalizePunctuation(str) {
        if (!str) return '';
        return normalizeText(str)
            .replace(/[.,/#!%^&*;:{}=\-_`~()'"?।\u0024]/g, ' ')
            .replace(/\s+/g, ' ')
            .trim();
    }

    function tokenize(str) {
        if (!str) return [];
        var s = str.toString()
            .replace(/([a-z\u0980-\u09FF])([A-Z])/g, function(_, a, b) { return a + ' ' + b; })
            .toLowerCase();
        s = s.replace(/[^a-z0-9\u0980-\u09FF]+/g, ' ');
        var raw = s.trim().split(/\s+/);
        var res = [];
        for (var i = 0; i < raw.length; i++) {
            if (raw[i].length > 0) res.push(raw[i]);
        }
        return res;
    }

    function hasToken(tokens, target) {
        if (!tokens || !target) return false;
        var t = target.toLowerCase();
        for (var i = 0; i < tokens.length; i++) {
            if (tokens[i] === t) return true;
        }
        return false;
    }

    function hasBnKeyword(str, kw) {
        if (!str || !kw) return false;
        if (kw === 'জেলা') {
            var stripped = str.replace(/উপজেলা/g, '').replace(/উপ-জেলা/g, '');
            return stripped.indexOf('জেলা') !== -1;
        }
        return str.indexOf(kw) !== -1;
    }

    function splitName(fullName) {
        if (!fullName) return { first: '', last: '' };
        var trimmed = fullName.trim();
        var spaceIdx = trimmed.indexOf(' ');
        if (spaceIdx === -1) {
            return { first: trimmed, last: '' };
        }
        return {
            first: trimmed.substring(0, spaceIdx).trim(),
            last: trimmed.substring(spaceIdx + 1).trim()
        };
    }
    
    function getAssociatedLabelText(el) {
        if (el._cachedLabel !== undefined) return el._cachedLabel;
        var labelText = '';
        try {
            if (el.labels && el.labels.length > 0) {
                for (var i = 0; i < el.labels.length; i++) {
                    labelText += ' ' + (el.labels[i].innerText || el.labels[i].textContent || '');
                }
            }
            if (el.id && el.ownerDocument) {
                var explicitLabel = el.ownerDocument.querySelector('label[for="' + el.id + '"]');
                if (explicitLabel) {
                    labelText += ' ' + (explicitLabel.innerText || explicitLabel.textContent || '');
                }
            }
            var p = el.parentElement;
            if (p && p.tagName === 'LABEL') {
                labelText += ' ' + (p.innerText || p.textContent || '');
            }
            if (el.type === 'radio' && p) {
                labelText += ' ' + (p.innerText || p.textContent || '');
            }
        } catch(e) {}
        var res = labelText.trim();
        el._cachedLabel = res;
        return res;
    }

    function getNearbyHeadingText(el) {
        try {
            var curr = el.parentElement;
            var depth = 0;
            while (curr && depth < 4) {
                if (curr._cachedHeading !== undefined) {
                    return curr._cachedHeading;
                }
                var headings = curr.querySelectorAll('legend, h1, h2, h3, h4, h5, h6, th, [class*="header"], [class*="title"], caption');
                if (headings.length > 0) {
                    var text = '';
                    for (var h = 0; h < headings.length; h++) {
                        if (!headings[h].contains(el)) {
                            text += ' ' + (headings[h].innerText || headings[h].textContent || '');
                        }
                    }
                    var res = text.trim();
                    curr._cachedHeading = res;
                    return res;
                }
                curr = curr.parentElement;
                depth++;
            }
        } catch(e) {}
        return '';
    }

    // Helper: Map education records
    var ssc = null, hsc = null, grad = null, masters = null;
    if (data.education && data.education.length) {
        for (var eIdx = 0; eIdx < data.education.length; eIdx++) {
            var entry = data.education[eIdx];
            var q = (entry.qualification || '').toLowerCase();
            if (!ssc && (q.indexOf('ssc') !== -1 || q.indexOf('secondary') !== -1 || q.indexOf('dakhil') !== -1 || q.indexOf('দাখিল') !== -1 || q.indexOf('মাধ্যমিক') !== -1)) {
                ssc = entry;
            } else if (!hsc && (q.indexOf('hsc') !== -1 || q.indexOf('higher secondary') !== -1 || q.indexOf('alim') !== -1 || q.indexOf('আলিম') !== -1 || q.indexOf('উচ্চ মাধ্যমিক') !== -1)) {
                hsc = entry;
            } else if (!masters && (q.indexOf('master') !== -1 || q.indexOf('m.sc') !== -1 || q.indexOf('msc') !== -1 || q.indexOf('m.a') !== -1 || q.indexOf('post graduate') !== -1 || q.indexOf('মাস্টার্স') !== -1 || q.indexOf('স্নাতকোত্তর') !== -1)) {
                masters = entry;
            } else if (!grad && (q.indexOf('grad') !== -1 || q.indexOf('bachelor') !== -1 || q.indexOf('b.sc') !== -1 || q.indexOf('bsc') !== -1 || q.indexOf('b.a') !== -1 || q.indexOf('hons') !== -1 || q.indexOf('degree') !== -1 || q.indexOf('স্নাতক') !== -1)) {
                grad = entry;
            }
        }
        if (!grad && data.education[0]) grad = data.education[0];
        if (!hsc && data.education[1]) hsc = data.education[1];
        if (!ssc && data.education[2]) ssc = data.education[2];
    }

    var primaryWork = (data.workExperience && data.workExperience.length > 0) ? data.workExperience[0] : null;

    function detectEducationLevel(str) {
        if (!str) return null;
        var s = str.toLowerCase();
        if (s.indexOf('ssc') !== -1 || s.indexOf('s.s.c') !== -1 || s.indexOf('secondary') !== -1 || s.indexOf('dakhil') !== -1 || s.indexOf('দাখিল') !== -1 || s.indexOf('মাধ্যমিক') !== -1) {
            return 'ssc';
        }
        if (s.indexOf('hsc') !== -1 || s.indexOf('h.s.c') !== -1 || s.indexOf('higher secondary') !== -1 || s.indexOf('alim') !== -1 || s.indexOf('আলিম') !== -1 || s.indexOf('উচ্চ মাধ্যমিক') !== -1) {
            return 'hsc';
        }
        if (s.indexOf('master') !== -1 || s.indexOf('m.sc') !== -1 || s.indexOf('msc') !== -1 || s.indexOf('m.a') !== -1 || s.indexOf('post graduate') !== -1 || s.indexOf('post-graduate') !== -1 || s.indexOf('মাস্টার্স') !== -1 || s.indexOf('স্নাতকোত্তর') !== -1) {
            return 'masters';
        }
        if (s.indexOf('graduat') !== -1 || s.indexOf('bachelor') !== -1 || s.indexOf('b.sc') !== -1 || s.indexOf('bsc') !== -1 || s.indexOf('b.a') !== -1 || s.indexOf('hons') !== -1 || s.indexOf('honours') !== -1 || s.indexOf('degree') !== -1 || s.indexOf('স্নাতক') !== -1) {
            return 'grad';
        }
        return null;
    }

    function matchField(el) {
        var type = (el.type || 'text').toLowerCase();
        if (type === 'password' || type === 'hidden' || type === 'submit' || 
            type === 'button' || type === 'reset' || type === 'file' || 
            type === 'checkbox' || type === 'image') {
            return null;
        }
        if (el.disabled || el.readOnly) return null;
        
        var name = (el.name || '').trim();
        var id = (el.id || '').trim();
        var ac = (el.autocomplete || '').trim().toLowerCase();
        var ph = (el.placeholder || '').trim();
        var aria = (el.getAttribute('aria-label') || '').trim();
        var label = getAssociatedLabelText(el);
        var heading = getNearbyHeadingText(el);
        
        var combined = (name + ' ' + id + ' ' + ac + ' ' + ph + ' ' + aria + ' ' + label).toLowerCase();
        if (isSensitive(combined)) return null;
        
        var isBn = isBengaliText(label) || isBengaliText(ph) || isBengaliText(heading) ||
                   el.getAttribute('lang') === 'bn' ||
                   (document.documentElement && document.documentElement.lang === 'bn');
        
        var fullContext = (combined + ' ' + heading).toLowerCase();
        var tokens = tokenize(name + ' ' + id + ' ' + ph + ' ' + aria);
        var labelTokens = tokenize(label);
        var allTokens = tokens.concat(labelTokens);

        // --- 1. Father's Name Matching ---
        var isFather = hasToken(allTokens, 'father') || hasToken(allTokens, 'fathers') ||
                       hasToken(allTokens, 'fathername') || hasToken(allTokens, 'paternal') ||
                       combined.indexOf("father's name") !== -1 || combined.indexOf("fathers name") !== -1 ||
                       combined.indexOf("father_name") !== -1 || combined.indexOf("father name") !== -1 ||
                       hasBnKeyword(combined, "পিতার নাম") || hasBnKeyword(combined, "বাবার নাম") ||
                       hasBnKeyword(combined, "পিতা") || hasBnKeyword(combined, "বাবা");

        // --- 2. Mother's Name Matching ---
        var isMother = hasToken(allTokens, 'mother') || hasToken(allTokens, 'mothers') ||
                       hasToken(allTokens, 'mothername') || hasToken(allTokens, 'maternal') ||
                       combined.indexOf("mother's name") !== -1 || combined.indexOf("mothers name") !== -1 ||
                       combined.indexOf("mother_name") !== -1 || combined.indexOf("mother name") !== -1 ||
                       hasBnKeyword(combined, "মাতার নাম") || hasBnKeyword(combined, "মায়ের নাম") ||
                       hasBnKeyword(combined, "মাতা") || hasBnKeyword(combined, "মায়ের");

        // Conflict check between father and mother
        if (isFather && isMother) return null;

        if (isFather) {
            var fVal = isBn ? (data.fatherNameBn || data.fatherNameEn) : data.fatherNameEn;
            if (fVal && fVal.trim().length > 0) return fVal.trim();
            return null; // NEVER fall back to userName
        }
        
        if (isMother) {
            var mVal = isBn ? (data.motherNameBn || data.motherNameEn) : data.motherNameEn;
            if (mVal && mVal.trim().length > 0) return mVal.trim();
            return null; // NEVER fall back to userName
        }
        
        // --- 3. User's Name, First/Last Name, and Username Matching ---
        // 3a. Username / User ID / Login ID
        var isUsername = ac === 'username' ||
                         hasToken(allTokens, 'username') || hasToken(allTokens, 'userid') ||
                         combined.indexOf('user_name') !== -1 || combined.indexOf('user_id') !== -1 ||
                         combined.indexOf('login id') !== -1 || combined.indexOf('login_id') !== -1 || combined.indexOf('loginid') !== -1 ||
                         hasBnKeyword(combined, 'ইউজারনেম') || hasBnKeyword(combined, 'ব্যবহারকারীর নাম');
        if (isUsername) {
            var uVal = (data.userNameEn && data.userNameEn.trim().length > 0) ? data.userNameEn.trim() : (data.email && data.email.trim().length > 0 ? data.email.trim() : null);
            return uVal;
        }

        // 3b. First Name / Given Name
        var isFirstName = ac === 'given-name' ||
                          hasToken(allTokens, 'firstname') || hasToken(allTokens, 'fname') || hasToken(allTokens, 'forename') ||
                          combined.indexOf('first name') !== -1 || combined.indexOf('first_name') !== -1 ||
                          combined.indexOf('given name') !== -1 || combined.indexOf('given_name') !== -1 ||
                          hasBnKeyword(combined, 'প্রথম নাম');
        if (isFirstName) {
            var rawName = isBn ? (data.userNameBn || data.userNameEn) : data.userNameEn;
            var split = splitName(rawName);
            var fnVal = (split.first && split.first.length > 0) ? split.first : rawName;
            if (fnVal && fnVal.trim().length > 0) return fnVal.trim();
            return null;
        }

        // 3c. Last Name / Surname / Family Name
        var isLastName = ac === 'family-name' ||
                         hasToken(allTokens, 'lastname') || hasToken(allTokens, 'lname') || hasToken(allTokens, 'surname') ||
                         combined.indexOf('last name') !== -1 || combined.indexOf('last_name') !== -1 ||
                         combined.indexOf('family name') !== -1 || combined.indexOf('family_name') !== -1 ||
                         hasBnKeyword(combined, 'শেষ নাম') || hasBnKeyword(combined, 'বংশনাম');
        if (isLastName) {
            var rawName = isBn ? (data.userNameBn || data.userNameEn) : data.userNameEn;
            var split = splitName(rawName);
            var lnVal = (split.last && split.last.length > 0) ? split.last : (split.first || rawName);
            if (lnVal && lnVal.trim().length > 0) return lnVal.trim();
            return null;
        }

        // 3d. Full User's Name
        // Strict exclusions to prevent matching institutional, relative, or entity names
        var isExcludedFromName = isFather || isMother ||
                                 hasToken(allTokens, 'guardian') || hasToken(allTokens, 'spouse') ||
                                 hasToken(allTokens, 'nominee') || hasToken(allTokens, 'reference') ||
                                 hasToken(allTokens, 'referee') || hasToken(allTokens, 'careof') ||
                                 hasToken(allTokens, 'school') || hasToken(allTokens, 'college') ||
                                 hasToken(allTokens, 'university') || hasToken(allTokens, 'institution') ||
                                 hasToken(allTokens, 'board') || hasToken(allTokens, 'employer') ||
                                 hasToken(allTokens, 'company') || hasToken(allTokens, 'organization') ||
                                 hasToken(allTokens, 'district') || hasToken(allTokens, 'upazila') ||
                                 hasToken(allTokens, 'thana') || hasToken(allTokens, 'postoffice') ||
                                 hasToken(allTokens, 'file') || hasToken(allTokens, 'filename') ||
                                 combined.indexOf('care of') !== -1 || combined.indexOf('care_of') !== -1 || combined.indexOf('c/o') !== -1 ||
                                 hasBnKeyword(combined, 'অভিভাবক') || hasBnKeyword(combined, 'স্বামী') || hasBnKeyword(combined, 'স্ত্রী') ||
                                 hasBnKeyword(combined, 'নমিনি') || hasBnKeyword(combined, 'রেফারেন্স') || hasBnKeyword(combined, 'প্রযত্নে') ||
                                 hasBnKeyword(combined, 'বিদ্যালয়') || hasBnKeyword(combined, 'প্রতিষ্ঠান') || hasBnKeyword(combined, 'বোর্ড') ||
                                 hasBnKeyword(combined, 'কোম্পানি') || hasBnKeyword(combined, 'সংস্থা') || hasBnKeyword(combined, 'জেলা') ||
                                 hasBnKeyword(combined, 'উপজেলা') || hasBnKeyword(combined, 'থানা') || hasBnKeyword(combined, 'ডাকঘর');

        if (!isExcludedFromName) {
            if (ac === 'name' || 
                combined.indexOf('full name') !== -1 || combined.indexOf('fullname') !== -1 || 
                combined.indexOf('applicant name') !== -1 || combined.indexOf('candidate name') !== -1 ||
                hasBnKeyword(combined, 'পূর্ণ নাম') || hasBnKeyword(combined, 'আপনার নাম') || hasBnKeyword(combined, 'প্রার্থীর নাম') ||
                hasBnKeyword(combined, 'আবেদনকারীর নাম') ||
                ((hasToken(allTokens, 'name') || hasBnKeyword(combined, 'নাম')) && combined.indexOf('file') === -1)) {
                var uVal = isBn ? (data.userNameBn || data.userNameEn) : data.userNameEn;
                if (uVal && uVal.trim().length > 0) return uVal.trim();
                return null;
            }
        }
        
        // --- 4. National ID / NID ---
        if (hasToken(allTokens, 'nid') || hasToken(allTokens, 'nationalid') ||
            combined.indexOf('national id') !== -1 || combined.indexOf('national_id') !== -1 ||
            hasBnKeyword(combined, 'জাতীয় পরিচয়পত্র') || hasBnKeyword(combined, 'এনআইডি') || hasBnKeyword(combined, 'পরিচয়পত্র নম্বর')) {
            if (data.nationalId && data.nationalId.trim().length > 0) return data.nationalId.trim();
            return null;
        }

        // --- 5. Education Fields Matching (SSC / HSC / Graduation / Masters) ---
        var eduLevel = detectEducationLevel(fullContext);
        var eduObj = null;
        if (eduLevel) {
            eduObj = (eduLevel === 'ssc') ? ssc : (eduLevel === 'hsc') ? hsc : (eduLevel === 'masters') ? masters : grad;
        } else if (combined.indexOf('passing') !== -1 || combined.indexOf('pass year') !== -1 || combined.indexOf('pass_year') !== -1 ||
                   hasToken(allTokens, 'board') || hasToken(allTokens, 'roll') || hasToken(allTokens, 'rollno') ||
                   hasToken(allTokens, 'gpa') || hasToken(allTokens, 'cgpa') || hasToken(allTokens, 'exam') ||
                   hasToken(allTokens, 'examination') || hasToken(allTokens, 'degree') || hasToken(allTokens, 'qualification') ||
                   hasBnKeyword(combined, 'পরীক্ষা') || hasBnKeyword(combined, 'ডিগ্রি') || hasBnKeyword(combined, 'বোর্ড') || hasBnKeyword(combined, 'রোল')) {
            eduObj = ssc || grad || (data.education && data.education[0] ? data.education[0] : null);
        }
        if (eduObj) {
                // Examination / Degree
                if (hasToken(allTokens, 'exam') || hasToken(allTokens, 'examination') || hasToken(allTokens, 'degree') || hasToken(allTokens, 'qualification') ||
                    hasBnKeyword(combined, 'পরীক্ষা') || hasBnKeyword(combined, 'ডিগ্রি')) {
                    if (eduObj.qualification && eduObj.qualification.trim().length > 0) return eduObj.qualification.trim();
                }
                // Board
                if (hasToken(allTokens, 'board') || hasBnKeyword(combined, 'বোর্ড')) {
                    if (eduObj.board && eduObj.board.trim().length > 0) return eduObj.board.trim();
                }
                // Roll No
                if (hasToken(allTokens, 'roll') || hasToken(allTokens, 'rollno') || hasBnKeyword(combined, 'রোল')) {
                    if (eduObj.rollNo && eduObj.rollNo.trim().length > 0) return eduObj.rollNo.trim();
                }
                // Group / Subject / Major
                if (hasToken(allTokens, 'group') || hasToken(allTokens, 'subject') || hasToken(allTokens, 'major') || hasToken(allTokens, 'department') ||
                    hasBnKeyword(combined, 'গ্রুপ') || hasBnKeyword(combined, 'বিষয়') || hasBnKeyword(combined, 'বিভাগ')) {
                    if (eduObj.groupSubject && eduObj.groupSubject.trim().length > 0) return eduObj.groupSubject.trim();
                }
                // Institution / University / School / College
                if (hasToken(allTokens, 'inst') || hasToken(allTokens, 'institution') || hasToken(allTokens, 'university') || hasToken(allTokens, 'varsity') ||
                    hasToken(allTokens, 'college') || hasToken(allTokens, 'school') ||
                    hasBnKeyword(combined, 'প্রতিষ্ঠান') || hasBnKeyword(combined, 'বিশ্ববিদ্যালয়') || hasBnKeyword(combined, 'স্কুল') || hasBnKeyword(combined, 'কলেজ') || hasBnKeyword(combined, 'মাদ্রাসা')) {
                    if (eduObj.institution && eduObj.institution.trim().length > 0) return eduObj.institution.trim();
                }
                // Passing Year
                if (combined.indexOf('passing') !== -1 || combined.indexOf('pass year') !== -1 || combined.indexOf('pass_year') !== -1 ||
                    hasToken(allTokens, 'year') || hasBnKeyword(combined, 'বছর') || hasBnKeyword(combined, 'সন') || hasBnKeyword(combined, 'শিক্ষাবর্ষ')) {
                    if (eduObj.year && eduObj.year.trim().length > 0) return eduObj.year.trim();
                }
                // Result / Division / GPA
                if (hasToken(allTokens, 'result') || hasToken(allTokens, 'gpa') || hasToken(allTokens, 'cgpa') || hasToken(allTokens, 'division') || hasToken(allTokens, 'grade') ||
                    hasBnKeyword(combined, 'ফলাফল') || hasBnKeyword(combined, 'জিপিএ')) {
                    if (eduObj.result && eduObj.result.trim().length > 0) return eduObj.result.trim();
                }
                // Course Duration
                if (combined.indexOf('course duration') !== -1 || combined.indexOf('course_duration') !== -1 || hasToken(allTokens, 'duration') ||
                    hasBnKeyword(combined, 'সময়কাল') || hasBnKeyword(combined, 'মেয়াদ') || hasBnKeyword(combined, 'মেয়াদ')) {
                    if (eduObj.courseDuration && eduObj.courseDuration.trim().length > 0) return eduObj.courseDuration.trim();
                }
            }
        }

        // --- 6. Address Fields Matching (Present vs Permanent) ---
        var isPermanent = hasToken(allTokens, 'permanent') || hasToken(allTokens, 'perm') ||
                          hasBnKeyword(combined, 'স্থায়ী') || hasBnKeyword(combined, 'স্থায়ী') ||
                          heading.toLowerCase().indexOf('permanent') !== -1 ||
                          hasBnKeyword(heading, 'স্থায়ী') || hasBnKeyword(heading, 'স্থায়ী');

        var isPresent = hasToken(allTokens, 'present') || hasToken(allTokens, 'current') ||
                        hasToken(allTokens, 'pres') ||
                        hasBnKeyword(combined, 'বর্তমান') ||
                        heading.toLowerCase().indexOf('present') !== -1 ||
                        heading.toLowerCase().indexOf('current') !== -1 ||
                        hasBnKeyword(heading, 'বর্তমান');

        // If both present and permanent signals exist on the exact same field, skip to avoid incorrect cross-filling
        if (isPermanent && isPresent) return null;

        // Care Of
        if (combined.indexOf('care of') !== -1 || combined.indexOf('care_of') !== -1 || combined.indexOf('careof') !== -1 ||
            combined.indexOf('c/o') !== -1 || hasBnKeyword(combined, 'প্রযত্নে') || (hasToken(allTokens, 'guardian') && hasToken(allTokens, 'name'))) {
            var cVal = isPermanent ? data.permanentCareOf : (isPresent ? data.presentCareOf : (data.presentCareOf || data.permanentCareOf));
            if (cVal && cVal.trim().length > 0) return cVal.trim();
            return null;
        }

        // Village / Road / House / Flat
        if (hasToken(allTokens, 'village') || hasToken(allTokens, 'road') || hasToken(allTokens, 'house') ||
            hasToken(allTokens, 'flat') || hasToken(allTokens, 'holding') || hasToken(allTokens, 'street') ||
            hasBnKeyword(combined, 'গ্রাম') || hasBnKeyword(combined, 'রাস্তা') || hasBnKeyword(combined, 'বাসা') ||
            hasBnKeyword(combined, 'রোড') || hasBnKeyword(combined, 'বাড়ি') || hasBnKeyword(combined, 'বাড়ি')) {
            var vVal = isPermanent ? data.permanentVillageRoad : (isPresent ? data.presentVillageRoad : (data.presentVillageRoad || data.permanentVillageRoad || data.address));
            if (vVal && vVal.trim().length > 0) return vVal.trim();
            return null;
        }

        // District vs Upazila: Disambiguate rigorously
        var isDistSignal = hasToken(allTokens, 'district') || hasToken(allTokens, 'zila') || hasToken(allTokens, 'zilla') || hasBnKeyword(combined, 'জেলা');
        var isUpzSignal = hasToken(allTokens, 'upazila') || hasToken(allTokens, 'upazilla') || hasToken(allTokens, 'thana') ||
                          hasToken(allTokens, 'ps') || combined.indexOf('police station') !== -1 || combined.indexOf('police_station') !== -1 ||
                          hasBnKeyword(combined, 'উপজেলা') || hasBnKeyword(combined, 'থানা');

        // If field conflates both district and upazila, skip as ambiguous
        if (isDistSignal && isUpzSignal) return null;

        // District
        if (isDistSignal) {
            var dVal = isPermanent ? data.permanentDistrict : (isPresent ? data.presentDistrict : (data.presentDistrict || data.permanentDistrict));
            if (dVal && dVal.trim().length > 0) return dVal.trim();
            return null;
        }

        // Upazila / Thana
        if (isUpzSignal) {
            var uVal = isPermanent ? data.permanentUpazila : (isPresent ? data.presentUpazila : (data.presentUpazila || data.permanentUpazila));
            if (uVal && uVal.trim().length > 0) return uVal.trim();
            return null;
        }

        // Post Office
        if (combined.indexOf('post office') !== -1 || combined.indexOf('post_office') !== -1 || combined.indexOf('postoffice') !== -1 ||
            hasBnKeyword(combined, 'ডাকঘর') || hasBnKeyword(combined, 'পোস্ট অফিস')) {
            var poVal = isPermanent ? data.permanentPostOffice : (isPresent ? data.presentPostOffice : (data.presentPostOffice || data.permanentPostOffice));
            if (poVal && poVal.trim().length > 0) return poVal.trim();
            return null;
        }

        // Post Code / Postal Code
        if (combined.indexOf('post code') !== -1 || combined.indexOf('post_code') !== -1 || combined.indexOf('postcode') !== -1 ||
            hasToken(allTokens, 'postal') || hasToken(allTokens, 'zip') ||
            hasBnKeyword(combined, 'পোস্ট কোড') || hasBnKeyword(combined, 'পোস্টাল কোড')) {
            var pcVal = isPermanent ? data.permanentPostCode : (isPresent ? data.presentPostCode : (data.presentPostCode || data.permanentPostCode));
            if (pcVal && pcVal.trim().length > 0) return pcVal.trim();
            return null;
        }

        // --- 7. Work Experience Fields Matching ---
        // Ensure work experience fields are not confused with education fields
        if (primaryWork && !eduLevel) {
            // Organization / Employer
            if (hasToken(allTokens, 'organization') || hasToken(allTokens, 'organisation') ||
                hasToken(allTokens, 'employer') || hasToken(allTokens, 'company') ||
                hasBnKeyword(combined, 'সংস্থা') || hasBnKeyword(combined, 'কোম্পানি') || hasBnKeyword(combined, 'কর্মসংস্থান')) {
                if (!hasToken(allTokens, 'school') && !hasToken(allTokens, 'college') && !hasToken(allTokens, 'university') && !hasToken(allTokens, 'board')) {
                    if (primaryWork.employer && primaryWork.employer.trim().length > 0) return primaryWork.employer.trim();
                }
            }

            // Designation / Post
            if (hasToken(allTokens, 'designation') || combined.indexOf('job title') !== -1 || combined.indexOf('job_title') !== -1 ||
                hasBnKeyword(combined, 'পদবি') || hasBnKeyword(combined, 'পদবী') ||
                (hasToken(allTokens, 'post') && !isDistSignal && !isUpzSignal && combined.indexOf('post office') === -1 && combined.indexOf('postcode') === -1 && combined.indexOf('postal') === -1 && combined.indexOf('ডাকঘর') === -1)) {
                if (primaryWork.designation && primaryWork.designation.trim().length > 0) return primaryWork.designation.trim();
            }

            // Service Start / Joining Date
            if (combined.indexOf('service start') !== -1 || combined.indexOf('service_start') !== -1 ||
                hasToken(allTokens, 'joining') || combined.indexOf('start date') !== -1 || combined.indexOf('start_date') !== -1 ||
                combined.indexOf('from date') !== -1 || combined.indexOf('from_date') !== -1 || hasBnKeyword(combined, 'যোগদানের তারিখ')) {
                if (primaryWork.serviceStart && primaryWork.serviceStart.trim().length > 0) return primaryWork.serviceStart.trim();
            }

            // Service End / Leaving Date
            if (combined.indexOf('service end') !== -1 || combined.indexOf('service_end') !== -1 ||
                hasToken(allTokens, 'leaving') || combined.indexOf('end date') !== -1 || combined.indexOf('end_date') !== -1 ||
                combined.indexOf('to date') !== -1 || combined.indexOf('to_date') !== -1 || hasBnKeyword(combined, 'সমাপ্তির তারিখ')) {
                if (primaryWork.serviceEnd && primaryWork.serviceEnd.trim().length > 0) return primaryWork.serviceEnd.trim();
            }

            // Job Description / Responsibilities
            if (combined.indexOf('job description') !== -1 || combined.indexOf('job_description') !== -1 ||
                hasToken(allTokens, 'responsibilities') || hasToken(allTokens, 'responsibility') || hasToken(allTokens, 'duty') ||
                hasBnKeyword(combined, 'দায়িত্ব') || hasBnKeyword(combined, 'দায়িত্ব') || hasBnKeyword(combined, 'কাজের বিবরণ')) {
                var jd = (primaryWork.jobDescription && primaryWork.jobDescription.trim().length > 0) ? primaryWork.jobDescription.trim() : primaryWork.responsibilities;
                if (jd && jd.trim().length > 0) return jd.trim();
            }
        }

        // --- 8. General Non-Name Profile Fields ---
        // Email & Email Confirmation
        if (type === 'email' || ac === 'email' || 
            hasToken(allTokens, 'email') || combined.indexOf('e-mail') !== -1 ||
            combined.indexOf('confirm email') !== -1 || combined.indexOf('confirm_email') !== -1 ||
            hasBnKeyword(combined, 'ইমেইল') || hasBnKeyword(combined, 'ই-মেইল')) {
            return (data.email && data.email.trim().length > 0) ? data.email.trim() : null;
        }
        
        // Phone / Mobile & Phone Confirmation
        if (type === 'tel' || ac === 'tel' || 
            hasToken(allTokens, 'phone') || hasToken(allTokens, 'mobile') ||
            combined.indexOf('contact no') !== -1 || combined.indexOf('contact_no') !== -1 ||
            hasBnKeyword(combined, 'মোবাইল') || hasBnKeyword(combined, 'ফোন')) {
            return (data.phone && data.phone.trim().length > 0) ? data.phone.trim() : null;
        }
        
        // General Full Address (only if not a specific component like district/upazila)
        if (!isDistSignal && !isUpzSignal &&
            (ac === 'street-address' || combined.indexOf('present address') !== -1 || combined.indexOf('permanent address') !== -1 ||
             combined.indexOf('address') !== -1 || hasBnKeyword(combined, 'ঠিকানা'))) {
            return (data.address && data.address.trim().length > 0) ? data.address.trim() : null;
        }
        
        // Date of Birth: STRICTLY requires dob/birth/জন্ম/bday keywords (never match all date fields)
        var isBirthDate = ac === 'bday' || hasToken(allTokens, 'dob') || hasToken(allTokens, 'birth') ||
                          combined.indexOf('date of birth') !== -1 || combined.indexOf('date_of_birth') !== -1 ||
                          hasBnKeyword(combined, 'জন্ম তারিখ') || hasBnKeyword(combined, 'জন্মতারিখ') || hasBnKeyword(combined, 'জন্ম');
        if (isBirthDate) {
            return (data.dateOfBirth && data.dateOfBirth.trim().length > 0) ? data.dateOfBirth.trim() : null;
        }
        
        // Gender
        if (hasToken(allTokens, 'gender') || hasToken(allTokens, 'sex') || hasBnKeyword(combined, 'লিঙ্গ')) {
            return (data.gender && data.gender.trim().length > 0) ? data.gender.trim() : null;
        }
        
        // Nationality
        if (hasToken(allTokens, 'nationality') || hasToken(allTokens, 'citizenship') || hasBnKeyword(combined, 'জাতীয়তা')) {
            return (data.nationality && data.nationality.trim().length > 0) ? data.nationality.trim() : null;
        }
        
        // Marital Status
        if (hasToken(allTokens, 'marital') || combined.indexOf('marital status') !== -1 || hasBnKeyword(combined, 'বৈবাহিক')) {
            return (data.maritalStatus && data.maritalStatus.trim().length > 0) ? data.maritalStatus.trim() : null;
        }
        
        // Blood Group
        if (hasToken(allTokens, 'blood') || combined.indexOf('blood group') !== -1 || hasBnKeyword(combined, 'রক্তের গ্রুপ')) {
            return (data.bloodGroup && data.bloodGroup.trim().length > 0) ? data.bloodGroup.trim() : null;
        }
        
        // Height
        if (hasToken(allTokens, 'height') || hasBnKeyword(combined, 'উচ্চতা')) {
            return (data.height && data.height.trim().length > 0) ? data.height.trim() : null;
        }
        
        // Professional Profile
        if (hasToken(allTokens, 'summary') || hasToken(allTokens, 'objective') || combined.indexOf('about me') !== -1 ||
            hasToken(allTokens, 'bio') || hasToken(allTokens, 'profile')) {
            return (data.professionalProfile && data.professionalProfile.trim().length > 0) ? data.professionalProfile.trim() : null;
        }
        
        // Declaration
        if (hasToken(allTokens, 'declaration') || hasToken(allTokens, 'statement') || hasBnKeyword(combined, 'ঘোষণা')) {
            return (data.declaration && data.declaration.trim().length > 0) ? data.declaration.trim() : null;
        }
        
        return null;
    }
    
    // Native value setter supporting React, Vue, and Angular
    function setNativeValue(el, val) {
        try {
            var proto = Object.getPrototypeOf(el);
            var desc = Object.getOwnPropertyDescriptor(proto, 'value');
            if (!desc || !desc.set) {
                if (el.tagName === 'SELECT') {
                    desc = Object.getOwnPropertyDescriptor(window.HTMLSelectElement.prototype, 'value');
                } else if (el.tagName === 'TEXTAREA') {
                    desc = Object.getOwnPropertyDescriptor(window.HTMLTextAreaElement.prototype, 'value');
                } else {
                    desc = Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'value');
                }
            }
            if (desc && desc.set) {
                desc.set.call(el, val);
            } else {
                el.value = val;
            }
        } catch(e) {
            el.value = val;
        }
    }

    // Native checked setter for radio and checkbox
    function setNativeChecked(el, checked) {
        try {
            var proto = Object.getPrototypeOf(el);
            var desc = Object.getOwnPropertyDescriptor(proto, 'checked') || Object.getOwnPropertyDescriptor(window.HTMLInputElement.prototype, 'checked');
            if (desc && desc.set) {
                desc.set.call(el, checked);
            } else {
                el.checked = checked;
            }
        } catch(e) {
            el.checked = checked;
        }
    }

    // Determine if a <select> element is genuinely filled or still showing placeholder
    function isSelectAlreadyFilled(el) {
        if (!el.value || el.value === '' || el.value === '0' || el.value === '-1') return false;
        if (el.selectedIndex <= 0) {
            var optText = (el.options && el.options[0]) ? (el.options[0].text || '').trim().toLowerCase() : '';
            if (optText.indexOf('select') !== -1 || optText.indexOf('choose') !== -1 || 
                optText.indexOf('নির্বাচন') !== -1 || optText.indexOf('বাছাই') !== -1 ||
                optText === '--' || optText === '' || optText.indexOf('---') !== -1) {
                return false;
            }
        }
        return true;
    }

    // Select matching dropdown option: exact -> normalized exact -> safe unambiguous word match
    function fillSelect(el, targetVal) {
        if (!targetVal || !el.options || el.options.length === 0) return false;
        var rawTarget = targetVal.trim();
        var normTarget = normalizePunctuation(rawTarget);
        if (!normTarget) return false;

        var bestIndex = -1;

        // 1. Exact match (case-insensitive) on raw trimmed value or text
        var tLower = rawTarget.toLowerCase();
        for (var j = 0; j < el.options.length; j++) {
            var opt = el.options[j];
            var oVal = (opt.value || '').trim().toLowerCase();
            var oTxt = (opt.text || '').trim().toLowerCase();
            if (oVal === tLower || oTxt === tLower) {
                bestIndex = j;
                break;
            }
        }

        // 2. Normalized exact match (ignoring whitespace and punctuation differences)
        if (bestIndex === -1) {
            for (var j = 0; j < el.options.length; j++) {
                var opt = el.options[j];
                var oValNorm = normalizePunctuation(opt.value || '');
                var oTxtNorm = normalizePunctuation(opt.text || '');
                if (oValNorm === normTarget || oTxtNorm === normTarget) {
                    bestIndex = j;
                    break;
                }
            }
        }

        // 3. Safe, unambiguous word match: collect ALL matching options.
        // If multiple options match (e.g. "Dhaka North" vs "Dhaka South"), DO NOT select!
        if (bestIndex === -1) {
            var matchingIndices = [];
            for (var j = 0; j < el.options.length; j++) {
                var opt = el.options[j];
                var oVal = (opt.value || '').trim();
                var oTxt = (opt.text || '').trim();

                // Skip initial placeholder option
                if (j === 0 && (oVal === '' || oVal === '0' || oVal === '-1' || 
                    /select|choose|নির্বাচন|বাছাই|--/i.test(oTxt))) {
                    continue;
                }

                var oTxtNorm = normalizePunctuation(oTxt);
                var oValNorm = normalizePunctuation(oVal);

                var isWordMatch = false;
                var txtWords = oTxtNorm.split(' ');
                for (var tw = 0; tw < txtWords.length; tw++) {
                    if (txtWords[tw] === normTarget) { isWordMatch = true; break; }
                }
                if (!isWordMatch) {
                    var valWords = oValNorm.split(' ');
                    for (var vw = 0; vw < valWords.length; vw++) {
                        if (valWords[vw] === normTarget) { isWordMatch = true; break; }
                    }
                }
                if (isWordMatch) {
                    matchingIndices.push(j);
                }
            }

            if (matchingIndices.length === 1) {
                bestIndex = matchingIndices[0];
            }
        }

        if (bestIndex !== -1) {
            var selectedOpt = el.options[bestIndex];
            var targetOptionValue = selectedOpt.value;
            el.focus();
            el.selectedIndex = bestIndex;
            setNativeValue(el, targetOptionValue);
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new Event('change', { bubbles: true }));
            el.blur();

            // Verify selection after filling
            if (el.selectedIndex === bestIndex || el.value === targetOptionValue) {
                return true;
            }
        }
        return false;
    }

    // Helper: Select radio button matching label/value
    function fillRadio(el, targetVal) {
        if (!targetVal) return false;
        var t = targetVal.trim().toLowerCase();
        var rVal = (el.value || '').trim().toLowerCase();
        var rLabel = getAssociatedLabelText(el).toLowerCase();
        
        var isMatch = (rVal === t || rLabel.indexOf(t) !== -1 || t.indexOf(rVal) !== -1);
        // Bengali gender mappings
        if (!isMatch && (t === 'male' || t === 'পুরুষ')) {
            if (rVal === 'male' || rVal === 'm' || rVal === '1' || rLabel.indexOf('male') !== -1 || rLabel.indexOf('পুরুষ') !== -1) isMatch = true;
        } else if (!isMatch && (t === 'female' || t === 'মহিলা' || t === 'নারী')) {
            if (rVal === 'female' || rVal === 'f' || rVal === '2' || rLabel.indexOf('female') !== -1 || rLabel.indexOf('মহিলা') !== -1 || rLabel.indexOf('নারী') !== -1) isMatch = true;
        }

        if (isMatch) {
            el.focus();
            setNativeChecked(el, true);
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new Event('change', { bubbles: true }));
            try { el.click(); } catch(e) {}
            el.blur();
            return true;
        }
        return false;
    }

    // Gather document and all same-origin iframe documents
    function getDocuments() {
        var docs = [document];
        try {
            var iframes = document.querySelectorAll('iframe');
            for (var f = 0; f < iframes.length; f++) {
                try {
                    var iDoc = iframes[f].contentDocument || iframes[f].contentWindow.document;
                    if (iDoc) docs.push(iDoc);
                } catch(e) {}
            }
        } catch(e) {}
        return docs;
    }

    // Flat input collector that traverses standard DOM and non-recursive shadow roots efficiently
    function collectInputs(root, list) {
        if (!root) return;
        try {
            var found = root.querySelectorAll('input, textarea, select');
            for (var k = 0; k < found.length; k++) {
                list.push(found[k]);
            }
            var allElements = root.querySelectorAll('*');
            for (var a = 0; a < allElements.length; a++) {
                var sr = allElements[a].shadowRoot;
                if (sr) {
                    var sub = sr.querySelectorAll('input, textarea, select');
                    for (var s = 0; s < sub.length; s++) {
                        list.push(sub[s]);
                    }
                }
            }
        } catch(e) {}
    }

    var allDocs = getDocuments();
    for (var dIdx = 0; dIdx < allDocs.length; dIdx++) {
        var targetDoc = allDocs[dIdx];
        var inputs = [];
        collectInputs(targetDoc, inputs);
        
        var filledRadioGroups = {};
        var pendingSelects = [];
        var anySelectChanged = false;

        for (var i = 0; i < inputs.length; i++) {
            var el = inputs[i];
            try {
                var type = (el.type || 'text').toLowerCase();
                
                // Radio buttons
                if (type === 'radio') {
                    var grpName = el.name || 'unnamed_radio';
                    if (filledRadioGroups[grpName]) continue;
                    
                    var rLabel = getAssociatedLabelText(el).toLowerCase();
                    var rHeading = getNearbyHeadingText(el).toLowerCase();
                    var rCombined = (el.name + ' ' + el.id + ' ' + rLabel + ' ' + rHeading).toLowerCase();
                    
                    if (rCombined.indexOf('gender') !== -1 || rCombined.indexOf('sex') !== -1 || rCombined.indexOf('লিঙ্গ') !== -1 ||
                        rCombined.indexOf('male') !== -1 || rCombined.indexOf('female') !== -1 || rCombined.indexOf('পুরুষ') !== -1 || rCombined.indexOf('মহিলা') !== -1) {
                        if (data.gender && data.gender.trim().length > 0) {
                            if (el.checked && !overwrite) {
                                skippedCount++;
                                filledRadioGroups[grpName] = true;
                                continue;
                            }
                            var rSuccess = fillRadio(el, data.gender.trim());
                            if (rSuccess) {
                                filledCount++;
                                filledRadioGroups[grpName] = true;
                            }
                        }
                    }
                    continue;
                }

                var val = matchField(el);
                if (!val || val.trim().length === 0) continue;
                val = val.trim();

                if (el.tagName === 'SELECT') {
                    var isFilled = isSelectAlreadyFilled(el);
                    if (isFilled && !overwrite) {
                        skippedCount++;
                        continue;
                    }
                    var success = fillSelect(el, val);
                    if (success) {
                        filledCount++;
                        anySelectChanged = true;
                    } else {
                        // Keep track for dependent dropdown re-evaluation
                        pendingSelects.push({ el: el, targetVal: val });
                        skippedCount++;
                    }
                } else {
                    var currentVal = (el.value || '').trim();
                    if (currentVal.length > 0 && !overwrite) {
                        skippedCount++;
                        continue;
                    }
                    
                    el.focus();
                    setNativeValue(el, val);
                    el.dispatchEvent(new Event('input', { bubbles: true }));
                    el.dispatchEvent(new Event('change', { bubbles: true }));
                    el.blur();
                    filledCount++;
                }
            } catch(e) {
                skippedCount++;
            }
        }

        // Targeted second pass for dependent dropdowns:
        // If parent select changed and child select was dynamically reloaded, re-evaluate child select safely
        if (anySelectChanged && pendingSelects.length > 0) {
            for (var p = 0; p < pendingSelects.length; p++) {
                var pItem = pendingSelects[p];
                try {
                    if (pItem.el.options && pItem.el.options.length > 1) {
                        var pSuccess = fillSelect(pItem.el, pItem.targetVal);
                        if (pSuccess) {
                            filledCount++;
                            if (skippedCount > 0) skippedCount--;
                        }
                    }
                } catch(e) {}
            }
        }
    }
    
    return JSON.stringify({
        filled: filledCount,
        skipped: skippedCount
    });
})();
""".trimIndent()
    }

    internal fun buildAutofillPayloadJson(profile: AutofillProfile): String {
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
        root.put("nationalId", p.nationalId)

        // Present Address
        root.put("presentCareOf", p.presentCareOf)
        root.put("presentVillageRoad", p.presentVillageRoad)
        root.put("presentDistrict", p.presentDistrict)
        root.put("presentUpazila", p.presentUpazila)
        root.put("presentPostOffice", p.presentPostOffice)
        root.put("presentPostCode", p.presentPostCode)

        // Permanent Address
        root.put("permanentCareOf", p.permanentCareOf)
        root.put("permanentVillageRoad", p.permanentVillageRoad)
        root.put("permanentDistrict", p.permanentDistrict)
        root.put("permanentUpazila", p.permanentUpazila)
        root.put("permanentPostOffice", p.permanentPostOffice)
        root.put("permanentPostCode", p.permanentPostCode)

        // Education entries
        val eduArray = JSONArray()
        for (edu in profile.education) {
            val eduObj = JSONObject()
            eduObj.put("qualification", edu.qualification)
            eduObj.put("institution", edu.institution)
            eduObj.put("year", edu.year)
            eduObj.put("result", edu.result)
            eduObj.put("board", edu.board)
            eduObj.put("rollNo", edu.rollNo)
            eduObj.put("groupSubject", edu.groupSubject)
            eduObj.put("courseDuration", edu.courseDuration)
            eduArray.put(eduObj)
        }
        root.put("education", eduArray)

        // Work experience entries
        val workArray = JSONArray()
        for (w in profile.workExperience) {
            val wObj = JSONObject()
            wObj.put("employer", w.employer)
            wObj.put("designation", w.designation)
            wObj.put("period", w.period)
            wObj.put("responsibilities", w.responsibilities)
            wObj.put("serviceStart", w.serviceStart)
            wObj.put("serviceEnd", w.serviceEnd)
            wObj.put("jobDescription", w.jobDescription)
            workArray.put(wObj)
        }
        root.put("workExperience", workArray)

        root.put("professionalProfile", profile.professionalProfile)
        root.put("declaration", profile.declaration)

        return root.toString()
    }
}
