package com.muslim.browser.pro.browser

/**
 * Represents a work experience entry strictly bounded to the European Standard CV schema.
 * All fields are single English strings.
 */
data class WorkExperienceEntry(
    val employer: String = "",
    val designation: String = "",
    val period: String = "",
    val responsibilities: String = "",
    val serviceStart: String = "",
    val serviceEnd: String = "",
    val jobDescription: String = ""
)

/**
 * Represents an education entry strictly bounded to the 3 permitted CV records.
 * All fields are single English strings with canonical year.
 */
data class EducationEntry(
    val qualification: String,
    val institution: String,
    val year: String, // Canonical academic year (e.g. "2020", "2015", "2013")
    val result: String,
    val board: String = "",
    val rollNo: String = "",
    val groupSubject: String = "",
    val courseDuration: String = ""
)

/**
 * Personal information strictly following the Phase 2.1 language policy:
 * Exactly six independently editable name fields:
 * - userNameEn, userNameBn
 * - fatherNameEn, fatherNameBn
 * - motherNameEn, motherNameBn
 * All other approved fields are single English strings (or canonical format).
 */
data class PersonalInfo(
    val userNameEn: String = "",
    val userNameBn: String = "",
    val fatherNameEn: String = "",
    val fatherNameBn: String = "",
    val motherNameEn: String = "",
    val motherNameBn: String = "",

    val address: String = "",
    val phone: String = "",
    val email: String = "",
    val dateOfBirth: String = "",
    val nationality: String = "Bangladeshi",
    val gender: String = "Male",
    val maritalStatus: String = "",
    val bloodGroup: String = "",
    val height: String = "",

    val nationalId: String = "",

    // Present Address
    val presentCareOf: String = "",
    val presentVillageRoad: String = "",
    val presentDistrict: String = "",
    val presentUpazila: String = "",
    val presentPostOffice: String = "",
    val presentPostCode: String = "",

    // Permanent Address
    val permanentCareOf: String = "",
    val permanentVillageRoad: String = "",
    val permanentDistrict: String = "",
    val permanentUpazila: String = "",
    val permanentPostOffice: String = "",
    val permanentPostCode: String = ""
)

/**
 * Complete and exclusive Autofill profile schema defined strictly by the European Standard CV.
 * Contains fixed, typed fields and bounded collections. No dynamic or out-of-schema entries permitted.
 */
data class AutofillProfile(
    val isEnabled: Boolean = false,
    val overwriteExistingFields: Boolean = false,

    // A. Personal Information (6 name fields + 9 approved personal fields)
    val personalInfo: PersonalInfo = PersonalInfo(),

    // B. Professional Profile (Single English string)
    val professionalProfile: String = "",

    // C. Work Experience (Strict ceiling of exactly 3 slots)
    val workExperience: List<WorkExperienceEntry> = defaultWorkExperienceSlots(),

    // D. Education (Strict ceiling of exactly 3 qualifications from CV)
    val education: List<EducationEntry> = defaultEducationRecords(),

    // E. Skills (Strict ceiling of exactly 5 approved entries, English-only)
    val skills: List<String> = defaultSkillsList(),

    // F. Languages (Strict ceiling of exactly 2 approved entries, English-only)
    val languages: List<String> = defaultLanguagesList(),

    // G. Interests (Strict ceiling of exactly 5 approved entries, English-only)
    val interests: List<String> = defaultInterestsList(),

    // H. Declaration (Single English string)
    val declaration: String = ""
) {
    init {
        require(education.size <= MAX_EDUCATION_COUNT) {
            "Education list cannot exceed $MAX_EDUCATION_COUNT records under the strict PDF schema."
        }
        require(workExperience.size <= MAX_WORK_EXP_COUNT) {
            "Work experience cannot exceed $MAX_WORK_EXP_COUNT records under the strict PDF schema."
        }
        require(skills.size <= MAX_SKILLS_COUNT) {
            "Skills list cannot exceed $MAX_SKILLS_COUNT entries."
        }
        require(languages.size <= MAX_LANGUAGES_COUNT) {
            "Languages list cannot exceed $MAX_LANGUAGES_COUNT entries."
        }
        require(interests.size <= MAX_INTERESTS_COUNT) {
            "Interests list cannot exceed $MAX_INTERESTS_COUNT entries."
        }
    }

    companion object {
        const val MAX_EDUCATION_COUNT = 3
        const val MAX_WORK_EXP_COUNT = 3
        const val MAX_SKILLS_COUNT = 5
        const val MAX_LANGUAGES_COUNT = 2
        const val MAX_INTERESTS_COUNT = 5

        /**
         * Exactly the 3 verified education records from the reference CV.
         */
        fun defaultEducationRecords(): List<EducationEntry> = listOf(
            EducationEntry(
                qualification = "B.Sc. (Hons) in Mathematics",
                institution = "Hazi Asmat College, National University",
                year = "2020",
                result = "2nd Class"
            ),
            EducationEntry(
                qualification = "Higher Secondary Certificate (HSC)",
                institution = "Scholastica Model College",
                year = "2015",
                result = "Grade A"
            ),
            EducationEntry(
                qualification = "Secondary School Certificate (SSC)",
                institution = "Satirpara K.K Institution School & College",
                year = "2013",
                result = "Grade A"
            )
        )

        fun defaultWorkExperienceSlots(): List<WorkExperienceEntry> = listOf(
            WorkExperienceEntry(),
            WorkExperienceEntry(),
            WorkExperienceEntry()
        )

        fun defaultSkillsList(): List<String> = listOf(
            "6G, TIG, 3G and 4G Welding",
            "MS Word, MS Excel and PowerPoint",
            "Adobe After Effects",
            "HTML and Internet Browsing",
            "Teamwork and Communication Skills"
        )

        fun defaultLanguagesList(): List<String> = listOf(
            "Bengali (Native)",
            "English (Good Reading, Writing, Listening & Medium Speaking)"
        )

        fun defaultInterestsList(): List<String> = listOf(
            "Traveling",
            "Reading Books",
            "Listening Music",
            "Learning New Skills",
            "Internet Browsing"
        )
    }
}
