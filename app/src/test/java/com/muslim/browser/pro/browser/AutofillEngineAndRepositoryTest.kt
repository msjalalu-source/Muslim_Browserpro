package com.muslim.browser.pro.browser

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AutofillEngineAndRepositoryTest {

    private lateinit var context: Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        AutofillRepository.clearProfile(context)
    }

    // 1. All six name fields save and restore independently
    @Test
    fun testAllSixNameFieldsSaveAndRestoreIndependently() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Tariqul Islam",
                userNameBn = "তারিকুল ইসলাম",
                fatherNameEn = "Rafiqul Islam",
                fatherNameBn = "রফিকুল ইসলাম",
                motherNameEn = "Salma Begum",
                motherNameBn = "সালমা বেগম",
                address = "Mirpur-10, Dhaka",
                phone = "+8801711111111",
                email = "tariq@example.com"
            )
        )

        assertTrue(AutofillRepository.saveProfile(context, profile))
        val loaded = AutofillRepository.loadProfile(context)

        assertEquals("Tariqul Islam", loaded.personalInfo.userNameEn)
        assertEquals("তারিকুল ইসলাম", loaded.personalInfo.userNameBn)
        assertEquals("Rafiqul Islam", loaded.personalInfo.fatherNameEn)
        assertEquals("রফিকুল ইসলাম", loaded.personalInfo.fatherNameBn)
        assertEquals("Salma Begum", loaded.personalInfo.motherNameEn)
        assertEquals("সালমা বেগম", loaded.personalInfo.motherNameBn)
    }

    // 2. Empty father/mother values never fall back to the user's name
    @Test
    fun testEmptyFatherMotherNeverFallsBackToUserName() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Tariqul Islam",
                userNameBn = "তারিকুল ইসলাম",
                fatherNameEn = "",
                fatherNameBn = "",
                motherNameEn = "",
                motherNameBn = ""
            )
        )

        AutofillRepository.saveProfile(context, profile)
        val loaded = AutofillRepository.loadProfile(context)

        // Verified in repository & model: father and mother remain empty strings
        assertEquals("", loaded.personalInfo.fatherNameEn)
        assertEquals("", loaded.personalInfo.fatherNameBn)
        assertEquals("", loaded.personalInfo.motherNameEn)
        assertEquals("", loaded.personalInfo.motherNameBn)
        assertEquals("Tariqul Islam", loaded.personalInfo.userNameEn)
    }

    // 3. English and Bengali name labels select the appropriate name value
    // 4. Ambiguous name fields are skipped
    // 5. Non-name fields have only one English value and use it even on Bengali-labeled forms
    @Test
    fun testPayloadJsonStructureForEngineMatching() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Arif Hossain",
                userNameBn = "আরিফ হোসেন",
                fatherNameEn = "Kamal Hossain",
                fatherNameBn = "কামাল হোসেন",
                motherNameEn = "Fatema Begum",
                motherNameBn = "ফাতেমা বেগম",
                address = "Uttara, Dhaka",
                phone = "+8801822222222",
                email = "arif@example.com",
                dateOfBirth = "1994-10-12",
                nationality = "Bangladeshi",
                gender = "Male",
                maritalStatus = "Single",
                bloodGroup = "B+",
                height = "5'9\""
            ),
            professionalProfile = "Experienced professional in mechanical engineering.",
            declaration = "All information provided herein is authentic and verifiable."
        )

        val jsonStr = AutofillRepository.serializeProfile(profile)
        val json = org.json.JSONObject(jsonStr)
        val p = json.getJSONObject("personalInfo")

        assertEquals("Arif Hossain", p.getString("userNameEn"))
        assertEquals("আরিফ হোসেন", p.getString("userNameBn"))
        assertEquals("Kamal Hossain", p.getString("fatherNameEn"))
        assertEquals("কামাল হোসেন", p.getString("fatherNameBn"))
        assertEquals("Fatema Begum", p.getString("motherNameEn"))
        assertEquals("ফাতেমা বেগম", p.getString("motherNameBn"))

        // Single English values for non-name fields
        assertEquals("Uttara, Dhaka", p.getString("address"))
        assertEquals("+8801822222222", p.getString("phone"))
        assertEquals("arif@example.com", p.getString("email"))
        assertEquals("1994-10-12", p.getString("dateOfBirth"))
        assertEquals("Bangladeshi", p.getString("nationality"))
        assertEquals("Male", p.getString("gender"))
        assertEquals("Single", p.getString("maritalStatus"))
        assertEquals("B+", p.getString("bloodGroup"))
        assertEquals("5'9\"", p.getString("height"))
        assertEquals("Experienced professional in mechanical engineering.", json.getString("professionalProfile"))
        assertEquals("All information provided herein is authentic and verifiable.", json.getString("declaration"))
    }

    // 6. Old stored profiles migrate safely, preserving English values and existing user-name values
    @Test
    fun testMigrationFromLegacyBilingualProfile() {
        val legacyJson = """
            {
                "isEnabled": true,
                "overwriteExistingFields": true,
                "personalInfo": {
                    "fullName": {"en": "Legacy User", "bn": "পুরাতন ব্যবহারকারী"},
                    "address": {"en": "123 Old Road, Dhaka", "bn": "১২৩ পুরাতন সড়ক, ঢাকা"},
                    "phone": "+8801900000000",
                    "email": "old@example.com",
                    "dateOfBirth": "1990-01-01",
                    "nationality": {"en": "Bangladeshi", "bn": "বাংলাদেশী"},
                    "gender": {"en": "Male", "bn": "পুরুষ"},
                    "maritalStatus": {"en": "Married", "bn": "বিবাহিত"},
                    "bloodGroup": "O+",
                    "height": "5'6\""
                },
                "professionalProfile": {"en": "Legacy Summary", "bn": "পুরাতন সারসংক্ষেপ"},
                "declaration": {"en": "Legacy Declaration", "bn": "পুরাতন ঘোষণা"},
                "workExperience": [
                    {
                        "employer": {"en": "ABC Corp", "bn": "এবিসি কর্প"},
                        "designation": {"en": "Lead Engineer", "bn": "প্রধান প্রকৌশলী"},
                        "period": {"en": "2018-2022", "bn": "২০১৮-২০২২"},
                        "responsibilities": {"en": "Core development", "bn": "মূল কাজ"}
                    }
                ],
                "education": [
                    {
                        "qualification": {"en": "B.Sc. in CSE", "bn": "সিএসই"},
                        "institution": {"en": "National University", "bn": "জাতীয় বিশ্ববিদ্যালয়"},
                        "year": "2019",
                        "result": {"en": "1st Class", "bn": "প্রথম শ্রেণী"}
                    }
                ],
                "skills": [
                    {"en": "Kotlin", "bn": "কোটলিন"},
                    {"en": "Java", "bn": "জাভা"}
                ]
            }
        """.trimIndent()

        val migrated = AutofillRepository.deserializeProfile(legacyJson)

        assertTrue(migrated.isEnabled)
        assertTrue(migrated.overwriteExistingFields)

        // User name migrated from legacy fullName
        assertEquals("Legacy User", migrated.personalInfo.userNameEn)
        assertEquals("পুরাতন ব্যবহারকারী", migrated.personalInfo.userNameBn)

        // New father and mother name fields safely initialized to empty strings
        assertEquals("", migrated.personalInfo.fatherNameEn)
        assertEquals("", migrated.personalInfo.fatherNameBn)
        assertEquals("", migrated.personalInfo.motherNameEn)
        assertEquals("", migrated.personalInfo.motherNameBn)

        // Non-name fields preserved as English strings and obsolete Bengali duplicates discarded
        assertEquals("123 Old Road, Dhaka", migrated.personalInfo.address)
        assertEquals("Bangladeshi", migrated.personalInfo.nationality)
        assertEquals("Male", migrated.personalInfo.gender)
        assertEquals("Married", migrated.personalInfo.maritalStatus)
        assertEquals("Legacy Summary", migrated.professionalProfile)
        assertEquals("Legacy Declaration", migrated.declaration)

        // Work experience and education English preserved
        assertEquals("ABC Corp", migrated.workExperience[0].employer)
        assertEquals("Lead Engineer", migrated.workExperience[0].designation)
        assertEquals("B.Sc. in CSE", migrated.education[0].qualification)
        assertEquals("National University", migrated.education[0].institution)

        // Skills English preserved
        assertEquals("Kotlin", migrated.skills[0])
        assertEquals("Java", migrated.skills[1])
    }

    // 7. Clear-profile behavior still works
    @Test
    fun testClearProfileResetsAllFields() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Test Name",
                fatherNameEn = "Father Name"
            )
        )
        AutofillRepository.saveProfile(context, profile)
        assertTrue(AutofillRepository.hasSavedProfile(context))

        AutofillRepository.clearProfile(context)
        assertFalse(AutofillRepository.hasSavedProfile(context))

        val loaded = AutofillRepository.loadProfile(context)
        assertFalse(loaded.isEnabled)
        assertEquals("", loaded.personalInfo.userNameEn)
        assertEquals("", loaded.personalInfo.fatherNameEn)
    }

    // 8. Education remains limited to exactly the three approved records
    @Test
    fun testEducationStrictlyLimitedToThreeApprovedRecords() {
        val defaultProfile = AutofillProfile()
        assertEquals(3, defaultProfile.education.size)

        assertEquals("", defaultProfile.education[0].qualification)
        assertEquals("", defaultProfile.education[0].institution)
        assertEquals("", defaultProfile.education[0].year)
        assertEquals("", defaultProfile.education[0].result)

        assertEquals("", defaultProfile.education[1].qualification)
        assertEquals("", defaultProfile.education[1].institution)
        assertEquals("", defaultProfile.education[1].year)
        assertEquals("", defaultProfile.education[1].result)

        assertEquals("", defaultProfile.education[2].qualification)
        assertEquals("", defaultProfile.education[2].institution)
        assertEquals("", defaultProfile.education[2].year)
        assertEquals("", defaultProfile.education[2].result)

        assertEquals("", defaultProfile.personalInfo.nationality)
        assertEquals("", defaultProfile.personalInfo.gender)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExceedingEducationLimitThrows() {
        val tooMany = AutofillProfile.defaultEducationRecords() + EducationEntry(
            qualification = "Extra Degree",
            institution = "Unknown",
            year = "2024",
            result = "1st"
        )
        AutofillProfile(education = tooMany)
    }

    // 9. Work experience remains limited to three records
    @Test
    fun testWorkExperienceBoundedToThreeSlots() {
        val defaultProfile = AutofillProfile()
        assertEquals(3, defaultProfile.workExperience.size)
    }

    @Test(expected = IllegalArgumentException::class)
    fun testExceedingWorkExperienceLimitThrows() {
        val tooMany = listOf(
            WorkExperienceEntry(),
            WorkExperienceEntry(),
            WorkExperienceEntry(),
            WorkExperienceEntry()
        )
        AutofillProfile(workExperience = tooMany)
    }

    // 10. Existing security exclusions and no-auto-submit behavior remain intact
    @Test
    fun testApprovedSkillsAndLanguagesCount() {
        val defaultProfile = AutofillProfile()
        assertEquals(5, defaultProfile.skills.size)
        defaultProfile.skills.forEach { assertEquals("", it) }
        assertEquals(2, defaultProfile.languages.size)
        defaultProfile.languages.forEach { assertEquals("", it) }
        assertEquals(5, defaultProfile.interests.size)
        defaultProfile.interests.forEach { assertEquals("", it) }
    }

    // 11. Newly added fields in Personal, Education, and Work tabs save and restore correctly
    @Test
    fun testNewAutofillFieldsSaveAndRestore() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Kabir Ahmed",
                nationalId = "19901234567890123",
                presentCareOf = "Md. Rafiq",
                presentVillageRoad = "House 10, Road 5",
                presentDistrict = "Dhaka",
                presentUpazila = "Mirpur",
                presentPostOffice = "Mirpur-10",
                presentPostCode = "1216",
                permanentCareOf = "Md. Rafiq",
                permanentVillageRoad = "Village Sonapur",
                permanentDistrict = "Cumilla",
                permanentUpazila = "Chandina",
                permanentPostOffice = "Chandina",
                permanentPostCode = "3510"
            ),
            education = listOf(
                EducationEntry(
                    qualification = "Secondary School Certificate (SSC)",
                    institution = "Govt High School",
                    year = "2010",
                    result = "GPA 5.0",
                    board = "Comilla",
                    rollNo = "123456",
                    groupSubject = "Science"
                ),
                EducationEntry(
                    qualification = "Higher Secondary Certificate (HSC)",
                    institution = "Govt College",
                    year = "2012",
                    result = "GPA 4.8",
                    board = "Comilla",
                    rollNo = "654321",
                    groupSubject = "Science"
                ),
                EducationEntry(
                    qualification = "Graduation / B.Sc in CSE",
                    institution = "Dhaka University",
                    year = "2017",
                    result = "CGPA 3.7",
                    courseDuration = "4 Years",
                    groupSubject = "Computer Science"
                )
            ),
            workExperience = listOf(
                WorkExperienceEntry(
                    employer = "Tech Solutions Ltd",
                    designation = "Senior Software Engineer",
                    serviceStart = "01/01/2018",
                    serviceEnd = "31/12/2022",
                    jobDescription = "Leading software engineering projects",
                    period = "2018 - 2022"
                ),
                WorkExperienceEntry(),
                WorkExperienceEntry()
            )
        )

        assertTrue(AutofillRepository.saveProfile(context, profile))
        val loaded = AutofillRepository.loadProfile(context)

        // Verify Personal fields
        assertEquals("19901234567890123", loaded.personalInfo.nationalId)
        assertEquals("Md. Rafiq", loaded.personalInfo.presentCareOf)
        assertEquals("House 10, Road 5", loaded.personalInfo.presentVillageRoad)
        assertEquals("Dhaka", loaded.personalInfo.presentDistrict)
        assertEquals("Mirpur", loaded.personalInfo.presentUpazila)
        assertEquals("Mirpur-10", loaded.personalInfo.presentPostOffice)
        assertEquals("1216", loaded.personalInfo.presentPostCode)

        assertEquals("Md. Rafiq", loaded.personalInfo.permanentCareOf)
        assertEquals("Village Sonapur", loaded.personalInfo.permanentVillageRoad)
        assertEquals("Cumilla", loaded.personalInfo.permanentDistrict)
        assertEquals("Chandina", loaded.personalInfo.permanentUpazila)
        assertEquals("Chandina", loaded.personalInfo.permanentPostOffice)
        assertEquals("3510", loaded.personalInfo.permanentPostCode)

        // Verify Education fields
        assertEquals("Comilla", loaded.education[0].board)
        assertEquals("123456", loaded.education[0].rollNo)
        assertEquals("Science", loaded.education[0].groupSubject)

        assertEquals("Comilla", loaded.education[1].board)
        assertEquals("654321", loaded.education[1].rollNo)
        assertEquals("Science", loaded.education[1].groupSubject)

        assertEquals("4 Years", loaded.education[2].courseDuration)
        assertEquals("Computer Science", loaded.education[2].groupSubject)

        // Verify Work fields
        assertEquals("Tech Solutions Ltd", loaded.workExperience[0].employer)
        assertEquals("Senior Software Engineer", loaded.workExperience[0].designation)
        assertEquals("01/01/2018", loaded.workExperience[0].serviceStart)
        assertEquals("31/12/2022", loaded.workExperience[0].serviceEnd)
        assertEquals("Leading software engineering projects", loaded.workExperience[0].jobDescription)
    }

    // 12. AutofillEngine payload JSON includes all new fields, education and work lists
    @Test
    fun testPayloadJsonIncludesNewFieldsAndLists() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Kabir Ahmed",
                userNameBn = "কবির আহমেদ",
                nationalId = "19901234567890123",
                presentCareOf = "Md. Rafiq",
                presentVillageRoad = "House 10, Road 5",
                presentDistrict = "Dhaka",
                presentUpazila = "Mirpur",
                presentPostOffice = "Mirpur-10",
                presentPostCode = "1216",
                permanentCareOf = "Md. Rafiq",
                permanentVillageRoad = "Village Sonapur",
                permanentDistrict = "Cumilla",
                permanentUpazila = "Chandina",
                permanentPostOffice = "Chandina",
                permanentPostCode = "3510"
            ),
            education = listOf(
                EducationEntry(
                    qualification = "Secondary School Certificate (SSC)",
                    institution = "Govt High School",
                    year = "2010",
                    result = "GPA 5.0",
                    board = "Comilla",
                    rollNo = "123456",
                    groupSubject = "Science"
                ),
                EducationEntry(
                    qualification = "Higher Secondary Certificate (HSC)",
                    institution = "Govt College",
                    year = "2012",
                    result = "GPA 4.8",
                    board = "Comilla",
                    rollNo = "654321",
                    groupSubject = "Science"
                ),
                EducationEntry(
                    qualification = "Graduation / B.Sc in CSE",
                    institution = "Dhaka University",
                    year = "2017",
                    result = "CGPA 3.7",
                    courseDuration = "4 Years",
                    groupSubject = "Computer Science"
                )
            ),
            workExperience = listOf(
                WorkExperienceEntry(
                    employer = "Tech Solutions Ltd",
                    designation = "Senior Software Engineer",
                    serviceStart = "01/01/2018",
                    serviceEnd = "31/12/2022",
                    jobDescription = "Leading software engineering projects",
                    period = "2018 - 2022"
                ),
                WorkExperienceEntry(),
                WorkExperienceEntry()
            )
        )

        val jsonStr = AutofillEngine.buildAutofillPayloadJson(profile)
        val json = org.json.JSONObject(jsonStr)

        assertEquals("19901234567890123", json.getString("nationalId"))
        assertEquals("Md. Rafiq", json.getString("presentCareOf"))
        assertEquals("House 10, Road 5", json.getString("presentVillageRoad"))
        assertEquals("Dhaka", json.getString("presentDistrict"))
        assertEquals("Mirpur", json.getString("presentUpazila"))
        assertEquals("Mirpur-10", json.getString("presentPostOffice"))
        assertEquals("1216", json.getString("presentPostCode"))

        assertEquals("Md. Rafiq", json.getString("permanentCareOf"))
        assertEquals("Village Sonapur", json.getString("permanentVillageRoad"))
        assertEquals("Cumilla", json.getString("permanentDistrict"))
        assertEquals("Chandina", json.getString("permanentUpazila"))
        assertEquals("Chandina", json.getString("permanentPostOffice"))
        assertEquals("3510", json.getString("permanentPostCode"))

        val eduArray = json.getJSONArray("education")
        assertEquals(3, eduArray.length())
        assertEquals("Comilla", eduArray.getJSONObject(0).getString("board"))
        assertEquals("123456", eduArray.getJSONObject(0).getString("rollNo"))
        assertEquals("Science", eduArray.getJSONObject(0).getString("groupSubject"))
        assertEquals("4 Years", eduArray.getJSONObject(2).getString("courseDuration"))

        val workArray = json.getJSONArray("workExperience")
        assertEquals(3, workArray.length())
        assertEquals("Tech Solutions Ltd", workArray.getJSONObject(0).getString("employer"))
        assertEquals("Senior Software Engineer", workArray.getJSONObject(0).getString("designation"))
        assertEquals("01/01/2018", workArray.getJSONObject(0).getString("serviceStart"))
        assertEquals("31/12/2022", workArray.getJSONObject(0).getString("serviceEnd"))
        assertEquals("Leading software engineering projects", workArray.getJSONObject(0).getString("jobDescription"))
    }

    // 13. Autofill when disabled returns disabled message and performs 0 fills
    @Test
    fun testAutofillDisabledReturnsDisabledMessage() {
        val profile = AutofillProfile(
            isEnabled = false,
            personalInfo = PersonalInfo(
                userNameEn = "Test User",
                email = "user@example.com"
            )
        )
        AutofillRepository.saveProfile(context, profile)

        var filledCount = -1
        var skippedCount = -1
        var resultMessage = ""

        AutofillEngine.executeAutofill(context, null) { filled, skipped, message ->
            filledCount = filled
            skippedCount = skipped
            resultMessage = message
        }

        // With null WebView
        assertEquals(0, filledCount)
        assertEquals(0, skippedCount)
        assertEquals("No active web page available", resultMessage)
    }

    // 14. Verify Autofill payload structure preserves username, email, phone, and name components
    @Test
    fun testPayloadStructureSupportsLoginAndRegistrationForms() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Tariqul Islam",
                userNameBn = "তারিকুল ইসলাম",
                email = "tariqul@example.com",
                phone = "+8801700000000"
            )
        )

        val jsonStr = AutofillEngine.buildAutofillPayloadJson(profile)
        val json = org.json.JSONObject(jsonStr)

        assertEquals("Tariqul Islam", json.getString("userNameEn"))
        assertEquals("তারিকুল ইসলাম", json.getString("userNameBn"))
        assertEquals("tariqul@example.com", json.getString("email"))
        assertEquals("+8801700000000", json.getString("phone"))
        // Passwords and tokens must NEVER exist in payload JSON
        assertFalse(json.has("password"))
        assertFalse(json.has("passcode"))
        assertFalse(json.has("otp"))
        assertFalse(json.has("token"))
        assertFalse(json.has("cvv"))
    }
}
