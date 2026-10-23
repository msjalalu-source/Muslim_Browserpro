package com.muslim.browser.pro.browser

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * Manages local-only persistent storage for the user's Smart Form Autofill profile.
 * Completely isolated from unrelated browser preferences in a dedicated SharedPreferences file.
 * Preserves exact Unicode Bengali strings without alteration or loss.
 * Handles backward-compatible migration from earlier schemas cleanly.
 */
object AutofillRepository {

    private const val PREFS_NAME = "autofill_profile_prefs"
    private const val KEY_PROFILE_JSON = "key_profile_json"
    private const val KEY_HAS_SAVED = "key_has_saved_profile"

    @Volatile
    private var cachedProfile: AutofillProfile? = null

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * Checks if the user has explicitly saved an autofill profile.
     */
    fun hasSavedProfile(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_HAS_SAVED, false)
    }

    /**
     * Loads the saved profile from disk. If no saved profile exists, returns a clean default profile.
     * Enforces schema bounds on loaded data and performs migration if needed.
     */
    fun loadProfile(context: Context): AutofillProfile {
        cachedProfile?.let { return it }
        val prefs = getPrefs(context)
        val jsonStr = prefs.getString(KEY_PROFILE_JSON, null) ?: return AutofillProfile()

        val loaded = try {
            deserializeProfile(jsonStr)
        } catch (_: Exception) {
            AutofillProfile()
        }
        cachedProfile = loaded
        return loaded
    }

    /**
     * Persists the profile to private local storage.
     * Synchronously commits to ensure durability across app restarts.
     */
    fun saveProfile(context: Context, profile: AutofillProfile): Boolean {
        cachedProfile = profile
        val jsonStr = serializeProfile(profile)
        return getPrefs(context).edit()
            .putString(KEY_PROFILE_JSON, jsonStr)
            .putBoolean(KEY_HAS_SAVED, true)
            .commit()
    }

    /**
     * Clears all saved profile data from private local storage.
     */
    fun clearProfile(context: Context): Boolean {
        cachedProfile = null
        return getPrefs(context).edit()
            .clear()
            .commit()
    }

    fun serializeProfile(profile: AutofillProfile): String {
        val root = JSONObject()
        root.put("isEnabled", profile.isEnabled)
        root.put("overwriteExistingFields", profile.overwriteExistingFields)

        // Personal Info
        val p = profile.personalInfo
        val pJson = JSONObject()
        pJson.put("userNameEn", p.userNameEn)
        pJson.put("userNameBn", p.userNameBn)
        pJson.put("fatherNameEn", p.fatherNameEn)
        pJson.put("fatherNameBn", p.fatherNameBn)
        pJson.put("motherNameEn", p.motherNameEn)
        pJson.put("motherNameBn", p.motherNameBn)
        pJson.put("address", p.address)
        pJson.put("phone", p.phone)
        pJson.put("email", p.email)
        pJson.put("dateOfBirth", p.dateOfBirth)
        pJson.put("nationality", p.nationality)
        pJson.put("gender", p.gender)
        pJson.put("maritalStatus", p.maritalStatus)
        pJson.put("bloodGroup", p.bloodGroup)
        pJson.put("height", p.height)
        pJson.put("nationalId", p.nationalId)
        pJson.put("presentCareOf", p.presentCareOf)
        pJson.put("presentVillageRoad", p.presentVillageRoad)
        pJson.put("presentDistrict", p.presentDistrict)
        pJson.put("presentUpazila", p.presentUpazila)
        pJson.put("presentPostOffice", p.presentPostOffice)
        pJson.put("presentPostCode", p.presentPostCode)
        pJson.put("permanentCareOf", p.permanentCareOf)
        pJson.put("permanentVillageRoad", p.permanentVillageRoad)
        pJson.put("permanentDistrict", p.permanentDistrict)
        pJson.put("permanentUpazila", p.permanentUpazila)
        pJson.put("permanentPostOffice", p.permanentPostOffice)
        pJson.put("permanentPostCode", p.permanentPostCode)
        root.put("personalInfo", pJson)

        // Professional Profile
        root.put("professionalProfile", profile.professionalProfile)

        // Work Experience (Max 3)
        val weArray = JSONArray()
        profile.workExperience.take(AutofillProfile.MAX_WORK_EXP_COUNT).forEach { exp ->
            val weObj = JSONObject()
            weObj.put("employer", exp.employer)
            weObj.put("designation", exp.designation)
            weObj.put("period", exp.period)
            weObj.put("responsibilities", exp.responsibilities)
            weObj.put("serviceStart", exp.serviceStart)
            weObj.put("serviceEnd", exp.serviceEnd)
            weObj.put("jobDescription", exp.jobDescription)
            weArray.put(weObj)
        }
        root.put("workExperience", weArray)

        // Education (Max 3)
        val eduArray = JSONArray()
        profile.education.take(AutofillProfile.MAX_EDUCATION_COUNT).forEach { edu ->
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

        // Skills (Max 5)
        val skillsArray = JSONArray()
        profile.skills.take(AutofillProfile.MAX_SKILLS_COUNT).forEach { skill ->
            skillsArray.put(skill)
        }
        root.put("skills", skillsArray)

        // Languages (Max 2)
        val langArray = JSONArray()
        profile.languages.take(AutofillProfile.MAX_LANGUAGES_COUNT).forEach { lang ->
            langArray.put(lang)
        }
        root.put("languages", langArray)

        // Interests (Max 5)
        val intArray = JSONArray()
        profile.interests.take(AutofillProfile.MAX_INTERESTS_COUNT).forEach { interest ->
            intArray.put(interest)
        }
        root.put("interests", intArray)

        // Declaration
        root.put("declaration", profile.declaration)

        return root.toString()
    }

    fun deserializeProfile(jsonStr: String): AutofillProfile {
        val root = JSONObject(jsonStr)
        val isEnabled = root.optBoolean("isEnabled", false)
        val overwriteExisting = root.optBoolean("overwriteExistingFields", false)

        val pJson = root.optJSONObject("personalInfo") ?: JSONObject()

        // Backward compatibility migration for name fields:
        // Check new fields first; if absent, migrate from legacy fullName
        val userNameEn: String
        val userNameBn: String
        if (pJson.has("userNameEn") || pJson.has("userNameBn")) {
            userNameEn = pJson.optString("userNameEn", "")
            userNameBn = pJson.optString("userNameBn", "")
        } else {
            val fnObj = pJson.optJSONObject("fullName")
            if (fnObj != null) {
                userNameEn = fnObj.optString("en", "")
                userNameBn = fnObj.optString("bn", "")
            } else {
                userNameEn = pJson.optString("fullName", "")
                userNameBn = ""
            }
        }

        val fatherNameEn = pJson.optString("fatherNameEn", "")
        val fatherNameBn = pJson.optString("fatherNameBn", "")
        val motherNameEn = pJson.optString("motherNameEn", "")
        val motherNameBn = pJson.optString("motherNameBn", "")

        val personalInfo = PersonalInfo(
            userNameEn = userNameEn,
            userNameBn = userNameBn,
            fatherNameEn = fatherNameEn,
            fatherNameBn = fatherNameBn,
            motherNameEn = motherNameEn,
            motherNameBn = motherNameBn,
            address = extractString(pJson, "address"),
            phone = pJson.optString("phone", ""),
            email = pJson.optString("email", ""),
            dateOfBirth = pJson.optString("dateOfBirth", ""),
            nationality = extractString(pJson, "nationality", ""),
            gender = extractString(pJson, "gender", ""),
            maritalStatus = extractString(pJson, "maritalStatus", ""),
            bloodGroup = pJson.optString("bloodGroup", ""),
            height = pJson.optString("height", ""),
            nationalId = pJson.optString("nationalId", ""),
            presentCareOf = pJson.optString("presentCareOf", ""),
            presentVillageRoad = pJson.optString("presentVillageRoad", ""),
            presentDistrict = pJson.optString("presentDistrict", ""),
            presentUpazila = pJson.optString("presentUpazila", ""),
            presentPostOffice = pJson.optString("presentPostOffice", ""),
            presentPostCode = pJson.optString("presentPostCode", ""),
            permanentCareOf = pJson.optString("permanentCareOf", ""),
            permanentVillageRoad = pJson.optString("permanentVillageRoad", ""),
            permanentDistrict = pJson.optString("permanentDistrict", ""),
            permanentUpazila = pJson.optString("permanentUpazila", ""),
            permanentPostOffice = pJson.optString("permanentPostOffice", ""),
            permanentPostCode = pJson.optString("permanentPostCode", "")
        )

        val profProfile = extractString(root, "professionalProfile")

        // Work experience: Exactly 3 slots bounded
        val weList = mutableListOf<WorkExperienceEntry>()
        val weArray = root.optJSONArray("workExperience")
        if (weArray != null) {
            val count = minOf(weArray.length(), AutofillProfile.MAX_WORK_EXP_COUNT)
            for (i in 0 until count) {
                val obj = weArray.optJSONObject(i) ?: continue
                weList.add(
                    WorkExperienceEntry(
                        employer = extractString(obj, "employer"),
                        designation = extractString(obj, "designation"),
                        period = extractString(obj, "period"),
                        responsibilities = extractString(obj, "responsibilities"),
                        serviceStart = obj.optString("serviceStart", ""),
                        serviceEnd = obj.optString("serviceEnd", ""),
                        jobDescription = obj.optString("jobDescription", "")
                    )
                )
            }
        }
        while (weList.size < AutofillProfile.MAX_WORK_EXP_COUNT) {
            weList.add(WorkExperienceEntry())
        }

        // Education: Strictly 3 slots bounded without falling back to hardcoded defaults
        val eduList = mutableListOf<EducationEntry>()
        val eduArray = root.optJSONArray("education")
        if (eduArray != null) {
            val count = minOf(eduArray.length(), AutofillProfile.MAX_EDUCATION_COUNT)
            for (i in 0 until count) {
                val obj = eduArray.optJSONObject(i) ?: continue
                eduList.add(
                    EducationEntry(
                        qualification = extractString(obj, "qualification"),
                        institution = extractString(obj, "institution"),
                        year = obj.optString("year", ""),
                        result = extractString(obj, "result"),
                        board = obj.optString("board", ""),
                        rollNo = obj.optString("rollNo", ""),
                        groupSubject = obj.optString("groupSubject", ""),
                        courseDuration = obj.optString("courseDuration", "")
                    )
                )
            }
        }
        while (eduList.size < AutofillProfile.MAX_EDUCATION_COUNT) {
            eduList.add(EducationEntry())
        }

        // Skills: Exactly 5 slots bounded without falling back to hardcoded defaults
        val skillsList = mutableListOf<String>()
        val skillsArray = root.optJSONArray("skills")
        if (skillsArray != null) {
            val count = minOf(skillsArray.length(), AutofillProfile.MAX_SKILLS_COUNT)
            for (i in 0 until count) {
                val item = skillsArray.opt(i)
                if (item is JSONObject) {
                    skillsList.add(item.optString("en", ""))
                } else if (item != null) {
                    skillsList.add(item.toString())
                } else {
                    skillsList.add("")
                }
            }
        }
        while (skillsList.size < AutofillProfile.MAX_SKILLS_COUNT) {
            skillsList.add("")
        }

        // Languages: Exactly 2 slots bounded without falling back to hardcoded defaults
        val langList = mutableListOf<String>()
        val langArray = root.optJSONArray("languages")
        if (langArray != null) {
            val count = minOf(langArray.length(), AutofillProfile.MAX_LANGUAGES_COUNT)
            for (i in 0 until count) {
                val item = langArray.opt(i)
                if (item is JSONObject) {
                    langList.add(item.optString("en", ""))
                } else if (item != null) {
                    langList.add(item.toString())
                } else {
                    langList.add("")
                }
            }
        }
        while (langList.size < AutofillProfile.MAX_LANGUAGES_COUNT) {
            langList.add("")
        }

        // Interests: Exactly 5 slots bounded without falling back to hardcoded defaults
        val intList = mutableListOf<String>()
        val intArray = root.optJSONArray("interests")
        if (intArray != null) {
            val count = minOf(intArray.length(), AutofillProfile.MAX_INTERESTS_COUNT)
            for (i in 0 until count) {
                val item = intArray.opt(i)
                if (item is JSONObject) {
                    intList.add(item.optString("en", ""))
                } else if (item != null) {
                    intList.add(item.toString())
                } else {
                    intList.add("")
                }
            }
        }
        while (intList.size < AutofillProfile.MAX_INTERESTS_COUNT) {
            intList.add("")
        }

        val declaration = extractString(root, "declaration")

        return AutofillProfile(
            isEnabled = isEnabled,
            overwriteExistingFields = overwriteExisting,
            personalInfo = personalInfo,
            professionalProfile = profProfile,
            workExperience = weList,
            education = eduList,
            skills = skillsList,
            languages = langList,
            interests = intList,
            declaration = declaration
        )
    }

    /**
     * Backward-compatible extractor: if the value is an old BilingualValue object with an "en" key,
     * extracts that string. Otherwise treats it as a direct string or fallback.
     */
    private fun extractString(root: JSONObject, key: String, fallback: String = ""): String {
        val opt = root.opt(key) ?: return fallback
        if (opt is JSONObject) {
            return opt.optString("en", fallback)
        }
        val str = opt.toString().trim()
        return if (str == "null") fallback else str
    }
}
