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

        val dataPayload = buildAutofillPayloadJson(profile)
        val overwrite = profile.overwriteExistingFields

        val script = """
(function() {
    var data = $dataPayload;
    var overwrite = $overwrite;
    
    var filledCount = 0;
    var skippedCount = 0;
    
    // Explicit security exclusions (passwords, payment cards, OTP, token, bank account)
    var SENSITIVE_KEYWORDS = [
        'password', 'passwd', 'pwd', 'token', 'otp', 'captcha', 
        'card', 'credit', 'debit', 'cvv', 'cvc', 'securitycode', 'pin', 
        'accountnumber', 'bank', 'ssn', 'taxid'
    ];
    
    function isSensitive(str) {
        if (!str) return false;
        var s = str.toLowerCase();
        // Do not flag academic passing year as password/pass
        if (s.indexOf('passing') !== -1 || s.indexOf('pass year') !== -1 || s.indexOf('pass_year') !== -1) {
            // Checked below safely
        } else if (/\bpass\b|\bpasswd\b|\bpwd\b|\bpasscode\b|\bpassphrase\b/.test(s)) {
            return true;
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

    function getNearbyHeadingText(el) {
        var text = '';
        try {
            var curr = el.parentElement;
            var depth = 0;
            while (curr && depth < 5) {
                var headings = curr.querySelectorAll('legend, h1, h2, h3, h4, h5, h6, th, [class*="header"], [class*="title"], caption');
                for (var h = 0; h < headings.length; h++) {
                    if (!headings[h].contains(el)) {
                        text += ' ' + (headings[h].innerText || headings[h].textContent || '');
                    }
                }
                curr = curr.parentElement;
                depth++;
            }
        } catch(e) {}
        return text.trim();
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
        var heading = getNearbyHeadingText(el);
        var headingLower = heading.toLowerCase();
        
        var combined = (name + ' ' + id + ' ' + ac + ' ' + ph + ' ' + aria + ' ' + labelLower).trim();
        if (isSensitive(combined)) return null;
        
        var isBn = isBengaliText(label) || isBengaliText(ph) || isBengaliText(heading) || el.getAttribute('lang') === 'bn' || (document.documentElement && document.documentElement.lang === 'bn');
        var fullContext = (combined + ' ' + headingLower).trim();

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
            if (combined.indexOf("mother") !== -1 || combined.indexOf("মাতা") !== -1 || combined.indexOf("মায়ের নাম") !== -1) return null;
            var fVal = isBn ? (data.fatherNameBn || data.fatherNameEn) : data.fatherNameEn;
            if (fVal && fVal.trim().length > 0) return fVal.trim();
            return null;
        }
        
        // --- 2. Mother's Name Matching (Strict: do NOT match on lone 'মা') ---
        var isMother = combined.indexOf("mother's name") !== -1 ||
                       combined.indexOf("mothers name") !== -1 ||
                       combined.indexOf("mother name") !== -1 ||
                       combined.indexOf("mother_name") !== -1 ||
                       combined.indexOf("mothername") !== -1 ||
                       combined.indexOf("mother") !== -1 ||
                       combined.indexOf("মাতার নাম") !== -1 ||
                       combined.indexOf("মায়ের নাম") !== -1 ||
                       combined.indexOf("মাতা") !== -1;
        if (isMother) {
            if (combined.indexOf("father") !== -1 || combined.indexOf("পিতা") !== -1 || combined.indexOf("বাবা") !== -1) return null;
            var mVal = isBn ? (data.motherNameBn || data.motherNameEn) : data.motherNameEn;
            if (mVal && mVal.trim().length > 0) return mVal.trim();
            return null;
        }
        
        // --- 3. User's Name Matching ---
        // Disallow when "name" refers to school, institution, board, organization, employer, company, district, upazila, post office, reference, nominee, spouse, guardian, care of, father, mother, etc.
        var isExcludedFromName = combined.indexOf("father") !== -1 || combined.indexOf("mother") !== -1 ||
                                 combined.indexOf("পিতা") !== -1 || combined.indexOf("মাতা") !== -1 ||
                                 combined.indexOf("বাবা") !== -1 || combined.indexOf("মায়ের নাম") !== -1 || combined.indexOf("মাতার নাম") !== -1 ||
                                 combined.indexOf("first") !== -1 || combined.indexOf("last") !== -1 ||
                                 combined.indexOf("sur") !== -1 || combined.indexOf("user id") !== -1 ||
                                 combined.indexOf("userid") !== -1 || combined.indexOf("username") !== -1 ||
                                 combined.indexOf("school") !== -1 || combined.indexOf("institution") !== -1 ||
                                 combined.indexOf("board") !== -1 || combined.indexOf("organization") !== -1 ||
                                 combined.indexOf("organisation") !== -1 || combined.indexOf("employer") !== -1 ||
                                 combined.indexOf("company") !== -1 || combined.indexOf("district") !== -1 ||
                                 combined.indexOf("upazila") !== -1 || combined.indexOf("upazilla") !== -1 ||
                                 combined.indexOf("post office") !== -1 || combined.indexOf("postoffice") !== -1 ||
                                 combined.indexOf("reference") !== -1 || combined.indexOf("nominee") !== -1 ||
                                 combined.indexOf("spouse") !== -1 || combined.indexOf("guardian") !== -1 ||
                                 combined.indexOf("care of") !== -1 || combined.indexOf("careof") !== -1 || combined.indexOf("c/o") !== -1 ||
                                 combined.indexOf("বিদ্যালয়") !== -1 || combined.indexOf("প্রতিষ্ঠান") !== -1 ||
                                 combined.indexOf("বোর্ড") !== -1 || combined.indexOf("সংস্থা") !== -1 ||
                                 combined.indexOf("কোম্পানি") !== -1 || combined.indexOf("জেলা") !== -1 ||
                                 combined.indexOf("উপজেলা") !== -1 || combined.indexOf("থানা") !== -1 ||
                                 combined.indexOf("ডাকঘর") !== -1 || combined.indexOf("অভিভাবক") !== -1 ||
                                 combined.indexOf("স্বামী") !== -1 || combined.indexOf("স্ত্রী") !== -1 ||
                                 combined.indexOf("রেফারেন্স") !== -1 || combined.indexOf("নমিনি") !== -1;
                                  
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
        
        // --- 4. National ID / NID ---
        if (combined.indexOf('national id') !== -1 || combined.indexOf('national_id') !== -1 || combined.indexOf('nationalid') !== -1 || combined.indexOf('nid') !== -1 || combined.indexOf('জাতীয় পরিচয়পত্র') !== -1 || combined.indexOf('এনআইডি') !== -1 || combined.indexOf('পরিচয়পত্র নম্বর') !== -1) {
            if (data.nationalId && data.nationalId.trim().length > 0) return data.nationalId.trim();
            return null;
        }

        // --- 5. Education Fields Matching (SSC / HSC / Graduation / Masters) ---
        var eduLevel = detectEducationLevel(fullContext);
        if (eduLevel) {
            var eduObj = (eduLevel === 'ssc') ? ssc : (eduLevel === 'hsc') ? hsc : (eduLevel === 'masters') ? masters : grad;
            if (eduObj) {
                // Examination / Degree
                if (combined.indexOf('exam') !== -1 || combined.indexOf('examination') !== -1 || combined.indexOf('degree') !== -1 || combined.indexOf('qualification') !== -1 || combined.indexOf('পরীক্ষা') !== -1 || combined.indexOf('ডিগ্রি') !== -1) {
                    if (eduObj.qualification && eduObj.qualification.trim().length > 0) return eduObj.qualification.trim();
                }
                // Board
                if (combined.indexOf('board') !== -1 || combined.indexOf('বোর্ড') !== -1) {
                    if (eduObj.board && eduObj.board.trim().length > 0) return eduObj.board.trim();
                }
                // Roll No
                if (combined.indexOf('roll') !== -1 || combined.indexOf('রোল') !== -1) {
                    if (eduObj.rollNo && eduObj.rollNo.trim().length > 0) return eduObj.rollNo.trim();
                }
                // Group / Subject / Major
                if (combined.indexOf('group') !== -1 || combined.indexOf('subject') !== -1 || combined.indexOf('major') !== -1 || combined.indexOf('department') !== -1 || combined.indexOf('গ্রুপ') !== -1 || combined.indexOf('বিষয়') !== -1 || combined.indexOf('বিভাগ') !== -1) {
                    if (eduObj.groupSubject && eduObj.groupSubject.trim().length > 0) return eduObj.groupSubject.trim();
                }
                // Institution / University / School / College
                if (combined.indexOf('inst') !== -1 || combined.indexOf('university') !== -1 || combined.indexOf('varsity') !== -1 || combined.indexOf('college') !== -1 || combined.indexOf('school') !== -1 || combined.indexOf('প্রতিষ্ঠান') !== -1 || combined.indexOf('বিশ্ববিদ্যালয়') !== -1 || combined.indexOf('স্কুল') !== -1 || combined.indexOf('কলেজ') !== -1) {
                    if (eduObj.institution && eduObj.institution.trim().length > 0) return eduObj.institution.trim();
                }
                // Passing Year
                if (combined.indexOf('passing') !== -1 || combined.indexOf('pass year') !== -1 || combined.indexOf('pass_year') !== -1 || combined.indexOf('year') !== -1 || combined.indexOf('বছর') !== -1 || combined.indexOf('সন') !== -1) {
                    if (eduObj.year && eduObj.year.trim().length > 0) return eduObj.year.trim();
                }
                // Result / Division / GPA
                if (combined.indexOf('result') !== -1 || combined.indexOf('gpa') !== -1 || combined.indexOf('cgpa') !== -1 || combined.indexOf('division') !== -1 || combined.indexOf('grade') !== -1 || combined.indexOf('ফলাফল') !== -1 || combined.indexOf('জিপিএ') !== -1) {
                    if (eduObj.result && eduObj.result.trim().length > 0) return eduObj.result.trim();
                }
                // Course Duration
                if (combined.indexOf('duration') !== -1 || combined.indexOf('course duration') !== -1 || combined.indexOf('সময়কাল') !== -1 || combined.indexOf('মেয়াদ') !== -1) {
                    if (eduObj.courseDuration && eduObj.courseDuration.trim().length > 0) return eduObj.courseDuration.trim();
                }
            }
        }

        // --- 6. Address Fields Matching (Present vs Permanent) ---
        var isPermanent = combined.indexOf('permanent') !== -1 || combined.indexOf('স্থায়ী') !== -1 || combined.indexOf('স্থায়ী') !== -1 || headingLower.indexOf('permanent') !== -1 || headingLower.indexOf('স্থায়ী') !== -1 || headingLower.indexOf('স্থায়ী') !== -1;
        
        // Care Of
        if (combined.indexOf('care of') !== -1 || combined.indexOf('care_of') !== -1 || combined.indexOf('careof') !== -1 || combined.indexOf('c/o') !== -1 || combined.indexOf('প্রযত্নে') !== -1 || (combined.indexOf('guardian') !== -1 && combined.indexOf('name') !== -1)) {
            var cVal = isPermanent ? (data.permanentCareOf || data.presentCareOf) : (data.presentCareOf || data.permanentCareOf);
            if (cVal && cVal.trim().length > 0) return cVal.trim();
            return null;
        }

        // Village / Road / House / Flat
        if (combined.indexOf('village') !== -1 || combined.indexOf('road') !== -1 || combined.indexOf('house') !== -1 || combined.indexOf('flat') !== -1 || combined.indexOf('holding') !== -1 || combined.indexOf('street') !== -1 || combined.indexOf('গ্রাম') !== -1 || combined.indexOf('রাস্তা') !== -1 || combined.indexOf('বাসা') !== -1 || combined.indexOf('রোড') !== -1 || combined.indexOf('বাড়ি') !== -1 || combined.indexOf('বাড়ি') !== -1) {
            var vVal = isPermanent ? (data.permanentVillageRoad || data.presentVillageRoad) : (data.presentVillageRoad || data.permanentVillageRoad);
            if (vVal && vVal.trim().length > 0) return vVal.trim();
        }

        // District
        if (combined.indexOf('district') !== -1 || combined.indexOf('zilla') !== -1 || combined.indexOf('zila') !== -1 || combined.indexOf('জেলা') !== -1) {
            var dVal = isPermanent ? (data.permanentDistrict || data.presentDistrict) : (data.presentDistrict || data.permanentDistrict);
            if (dVal && dVal.trim().length > 0) return dVal.trim();
        }

        // Upazila / Thana / Police Station
        if (combined.indexOf('upazila') !== -1 || combined.indexOf('upazilla') !== -1 || combined.indexOf('thana') !== -1 || combined.indexOf('police station') !== -1 || combined.indexOf('p.s') !== -1 || combined.indexOf('উপজেলা') !== -1 || combined.indexOf('থানা') !== -1) {
            var uVal = isPermanent ? (data.permanentUpazila || data.presentUpazila) : (data.presentUpazila || data.permanentUpazila);
            if (uVal && uVal.trim().length > 0) return uVal.trim();
        }

        // Post Office
        if (combined.indexOf('post office') !== -1 || combined.indexOf('post_office') !== -1 || combined.indexOf('postoffice') !== -1 || combined.indexOf('ডাকঘর') !== -1 || combined.indexOf('পোস্ট অফিস') !== -1) {
            var poVal = isPermanent ? (data.permanentPostOffice || data.presentPostOffice) : (data.presentPostOffice || data.permanentPostOffice);
            if (poVal && poVal.trim().length > 0) return poVal.trim();
        }

        // Post Code / Postal Code
        if (combined.indexOf('post code') !== -1 || combined.indexOf('post_code') !== -1 || combined.indexOf('postcode') !== -1 || combined.indexOf('postal') !== -1 || combined.indexOf('zip') !== -1 || combined.indexOf('পোস্ট কোড') !== -1 || combined.indexOf('পোস্টাল কোড') !== -1) {
            var pcVal = isPermanent ? (data.permanentPostCode || data.presentPostCode) : (data.presentPostCode || data.permanentPostCode);
            if (pcVal && pcVal.trim().length > 0) return pcVal.trim();
        }

        // --- 7. Work Experience Fields Matching ---
        if (primaryWork) {
            // Organization / Employer
            if (combined.indexOf('organization') !== -1 || combined.indexOf('organisation') !== -1 || combined.indexOf('employer') !== -1 || combined.indexOf('company') !== -1 || combined.indexOf('সংস্থা') !== -1 || combined.indexOf('কোম্পানি') !== -1) {
                if (combined.indexOf('school') === -1 && combined.indexOf('college') === -1 && combined.indexOf('university') === -1 && combined.indexOf('board') === -1 && !eduLevel) {
                    if (primaryWork.employer && primaryWork.employer.trim().length > 0) return primaryWork.employer.trim();
                }
            }

            // Designation / Post
            if (combined.indexOf('designation') !== -1 || combined.indexOf('job title') !== -1 || combined.indexOf('পদবি') !== -1 || combined.indexOf('পদবী') !== -1 || (combined.indexOf('post') !== -1 && combined.indexOf('post office') === -1 && combined.indexOf('postcode') === -1 && combined.indexOf('postal') === -1 && combined.indexOf('ডাকঘর') === -1)) {
                if (primaryWork.designation && primaryWork.designation.trim().length > 0) return primaryWork.designation.trim();
            }

            // Service Start / Joining Date
            if (combined.indexOf('service start') !== -1 || combined.indexOf('service_start') !== -1 || combined.indexOf('joining') !== -1 || combined.indexOf('start date') !== -1 || combined.indexOf('from date') !== -1 || combined.indexOf('যোগদানের তারিখ') !== -1) {
                if (primaryWork.serviceStart && primaryWork.serviceStart.trim().length > 0) return primaryWork.serviceStart.trim();
            }

            // Service End / Leaving Date
            if (combined.indexOf('service end') !== -1 || combined.indexOf('service_end') !== -1 || combined.indexOf('leaving') !== -1 || combined.indexOf('end date') !== -1 || combined.indexOf('to date') !== -1 || combined.indexOf('সমাপ্তির তারিখ') !== -1) {
                if (primaryWork.serviceEnd && primaryWork.serviceEnd.trim().length > 0) return primaryWork.serviceEnd.trim();
            }

            // Job Description / Responsibilities
            if (combined.indexOf('job description') !== -1 || combined.indexOf('job_description') !== -1 || combined.indexOf('responsibilities') !== -1 || combined.indexOf('responsibility') !== -1 || combined.indexOf('duty') !== -1 || combined.indexOf('দায়িত্ব') !== -1 || combined.indexOf('কাজের বিবরণ') !== -1) {
                var jd = (primaryWork.jobDescription && primaryWork.jobDescription.trim().length > 0) ? primaryWork.jobDescription.trim() : primaryWork.responsibilities;
                if (jd && jd.trim().length > 0) return jd.trim();
            }
        }

        // --- 8. General Non-Name Profile Fields ---
        // Email
        if (type === 'email' || ac === 'email' || combined.indexOf('email') !== -1 || combined.indexOf('ইমেইল') !== -1 || combined.indexOf('ই-মেইল') !== -1) {
            return (data.email && data.email.trim().length > 0) ? data.email.trim() : null;
        }
        
        // Phone / Mobile
        if (type === 'tel' || ac === 'tel' || combined.indexOf('phone') !== -1 || combined.indexOf('mobile') !== -1 || combined.indexOf('মোবাইল') !== -1 || combined.indexOf('ফোন') !== -1 || combined.indexOf('contact no') !== -1) {
            return (data.phone && data.phone.trim().length > 0) ? data.phone.trim() : null;
        }
        
        // General Full Address (if not broken down)
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

    // Select matching dropdown option case-insensitively, returning true only if matched
    function fillSelect(el, targetVal) {
        if (!targetVal || !el.options || el.options.length === 0) return false;
        var t = targetVal.trim().toLowerCase();
        var bestIndex = -1;
        
        // 1. Exact match on value or text
        for (var j = 0; j < el.options.length; j++) {
            var opt = el.options[j];
            var oVal = (opt.value || '').trim().toLowerCase();
            var oTxt = (opt.text || '').trim().toLowerCase();
            if (oVal === t || oTxt === t) {
                bestIndex = j;
                break;
            }
        }
        
        // 2. Substring / partial match (ignoring initial placeholder option)
        if (bestIndex === -1) {
            for (var j = 0; j < el.options.length; j++) {
                var opt = el.options[j];
                var oVal = (opt.value || '').trim().toLowerCase();
                var oTxt = (opt.text || '').trim().toLowerCase();
                if (j === 0 && (oVal === '' || oVal === '0' || oVal === '-1' || oTxt.indexOf('select') !== -1 || oTxt.indexOf('choose') !== -1)) {
                    continue;
                }
                if (oTxt && (oTxt.indexOf(t) !== -1 || t.indexOf(oTxt) !== -1)) {
                    bestIndex = j;
                    break;
                }
                if (oVal && (oVal.indexOf(t) !== -1 || t.indexOf(oVal) !== -1)) {
                    bestIndex = j;
                    break;
                }
            }
        }

        if (bestIndex !== -1) {
            el.focus();
            el.selectedIndex = bestIndex;
            setNativeValue(el, el.options[bestIndex].value);
            el.dispatchEvent(new Event('input', { bubbles: true }));
            el.dispatchEvent(new Event('change', { bubbles: true }));
            el.blur();
            return true;
        }
        return false;
    }

    var inputs = document.querySelectorAll('input, textarea, select');
    for (var i = 0; i < inputs.length; i++) {
        var el = inputs[i];
        try {
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
                } else {
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
