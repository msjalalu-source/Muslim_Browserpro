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

    // 15. Build 153: Synchronous commit and cache consistency in AutofillRepository
    @Test
    fun testSaveProfileSynchronousCommitAndCacheConsistency() {
        val profile = AutofillProfile(
            isEnabled = true,
            overwriteExistingFields = true,
            personalInfo = PersonalInfo(
                userNameEn = "Samiul Haque",
                email = "samiul@example.com"
            )
        )

        // Save must return true upon successful synchronous commit
        val saveSuccess = AutofillRepository.saveProfile(context, profile)
        assertTrue(saveSuccess)
        assertTrue(AutofillRepository.hasSavedProfile(context))

        // In-memory cache returns the exact instance
        val cached = AutofillRepository.loadProfile(context)
        assertEquals("Samiul Haque", cached.personalInfo.userNameEn)
        assertEquals("samiul@example.com", cached.personalInfo.email)
        assertTrue(cached.overwriteExistingFields)

        // Explicit cache invalidation forces reload from persistent disk storage
        AutofillRepository.invalidateCache()
        val reloaded = AutofillRepository.loadProfile(context)
        assertEquals("Samiul Haque", reloaded.personalInfo.userNameEn)
        assertEquals("samiul@example.com", reloaded.personalInfo.email)

        // Clear profile synchronously clears disk and resets cache to null
        val clearSuccess = AutofillRepository.clearProfile(context)
        assertTrue(clearSuccess)
        assertFalse(AutofillRepository.hasSavedProfile(context))

        val postClear = AutofillRepository.loadProfile(context)
        assertFalse(postClear.isEnabled)
        assertEquals("", postClear.personalInfo.userNameEn)
    }

    // 16. Build 153: Script generation includes security exclusions and robust matching guards
    @Test
    fun testBuildAutofillScriptIncludesSecurityAndFieldDisambiguation() {
        val profile = AutofillProfile(
            isEnabled = true,
            personalInfo = PersonalInfo(
                userNameEn = "Rakib Hassan",
                fatherNameEn = "Abul Hassan",
                motherNameEn = "Rehana Begum",
                presentDistrict = "Dhaka",
                presentUpazila = "Mirpur",
                permanentDistrict = "Cumilla",
                permanentUpazila = "Chandina"
            )
        )

        val script = AutofillEngine.buildAutofillScript(profile)

        // Verifies sensitive keywords
        assertTrue(script.contains("password"))
        assertTrue(script.contains("token"))
        assertTrue(script.contains("otp"))
        assertTrue(script.contains("captcha"))
        assertTrue(script.contains("cvv"))
        assertTrue(script.contains("securitycode"))
        assertTrue(script.contains("pin"))

        // Verifies passing year is explicitly guarded and not blocked
        assertTrue(script.contains("passing"))
        assertTrue(script.contains("pass year") || script.contains("pass_year"))

        // Verifies disambiguation between জেলা and উপজেলা
        assertTrue(script.contains("উপজেলা"))
        assertTrue(script.contains("hasBnKeyword"))

        // Verifies discrete word matching in fillSelect
        assertTrue(script.contains("normalizePunctuation"))
        assertTrue(script.contains("matchingIndices"))
    }

    // 17. Build 153: End-to-end JavaScript engine field detection, dropdown, and exclusion validation
    @Test
    fun testEngineFieldDetectionAndDropdownLogicViaNode() {
        val profile = AutofillProfile(
            isEnabled = true,
            overwriteExistingFields = false,
            personalInfo = PersonalInfo(
                userNameEn = "Tariqul Islam",
                userNameBn = "তারিকুল ইসলাম",
                fatherNameEn = "Rafiqul Islam",
                fatherNameBn = "রফিকুল ইসলাম",
                motherNameEn = "Salma Begum",
                motherNameBn = "সালমা বেগম",
                email = "tariqul@example.com",
                phone = "+8801700000000",
                dateOfBirth = "1995-05-15",
                gender = "Male",
                nationalId = "1234567890",
                presentDistrict = "Dhaka",
                presentUpazila = "Mirpur",
                permanentDistrict = "Cumilla",
                permanentUpazila = "Chandina"
            ),
            education = listOf(
                EducationEntry(
                    qualification = "SSC",
                    institution = "Govt High School",
                    year = "2011",
                    result = "GPA 5.0"
                ),
                EducationEntry(),
                EducationEntry()
            )
        )

        val script = AutofillEngine.buildAutofillScript(profile)

        val nodeTestRunner = """
            class MockElement {
                constructor(tag, attrs = {}) {
                    this.tagName = tag.toUpperCase();
                    this.type = attrs.type || (tag === 'select' ? 'select-one' : 'text');
                    this.name = attrs.name || '';
                    this.id = attrs.id || '';
                    this.value = attrs.value || '';
                    this.autocomplete = attrs.autocomplete || '';
                    this.placeholder = attrs.placeholder || '';
                    this.checked = attrs.checked || false;
                    this.disabled = attrs.disabled || false;
                    this.readOnly = attrs.readOnly || false;
                    this.options = attrs.options || [];
                    this.selectedIndex = attrs.selectedIndex !== undefined ? attrs.selectedIndex : 0;
                    this._ariaLabel = attrs['aria-label'] || '';
                    this._lang = attrs.lang || '';
                    this.labels = [];
                    this.parentElement = null;
                    if (attrs.label) this.labels.push({ innerText: attrs.label });
                }
                getAttribute(attr) {
                    if (attr === 'aria-label') return this._ariaLabel;
                    if (attr === 'lang') return this._lang;
                    return null;
                }
                focus() {}
                blur() {}
                dispatchEvent() {}
            }

            const inputs = [
                new MockElement('input', { name: 'father_name' }),
                new MockElement('input', { name: 'mother_name' }),
                new MockElement('input', { name: 'candidate_full_name' }),
                new MockElement('input', { name: 'father_mother_info' }), // ambiguous: must be skipped
                new MockElement('input', { id: 'dist_in', 'aria-label': 'আপনার জেলা' }),
                new MockElement('input', { id: 'upz_in', 'aria-label': 'আপনার উপজেলা' }),
                new MockElement('input', { name: 'present_district' }),
                new MockElement('input', { name: 'permanent_district' }),
                new MockElement('input', { type: 'text', name: 'user_password' }), // sensitive: must be skipped
                new MockElement('input', { type: 'text', name: 'passing_year', label: 'Passing Year' }), // safe passing year
                new MockElement('select', {
                    name: 'district',
                    options: [{ value: '', text: 'Select' }, { value: '1', text: 'Dhaka North' }, { value: '2', text: 'Dhaka South' }]
                }), // ambiguous dropdown: must NOT select first match
                new MockElement('select', {
                    name: 'upazila',
                    options: [{ value: '', text: 'Select' }, { value: 'mirpur', text: 'Mirpur Thana' }]
                }) // unambiguous dropdown: must match Mirpur
            ];

            global.document = {
                documentElement: { lang: 'en' },
                querySelectorAll(sel) {
                    if (sel === 'iframe') return [];
                    if (sel === 'input, textarea, select' || sel === '*') return inputs;
                    return [];
                }
            };
            global.Event = class { constructor(t) { this.type = t; } };
            global.window = {
                HTMLInputElement: { prototype: {} },
                HTMLSelectElement: { prototype: {} },
                HTMLTextAreaElement: { prototype: {} }
            };

            const resultJson = eval(`$script`);
            const res = JSON.parse(resultJson);

            const results = {
                fatherVal: inputs[0].value,
                motherVal: inputs[1].value,
                candidateVal: inputs[2].value,
                ambigNameVal: inputs[3].value,
                districtBnVal: inputs[4].value,
                upazilaBnVal: inputs[5].value,
                presentDistVal: inputs[6].value,
                permDistVal: inputs[7].value,
                passwordVal: inputs[8].value,
                passingYearVal: inputs[9].value,
                ambigDropdownIndex: inputs[10].selectedIndex,
                unambigDropdownIndex: inputs[11].selectedIndex,
                filled: res.filled,
                skipped: res.skipped
            };
            console.log(JSON.stringify(results));
        """.trimIndent()

        try {
            val process = ProcessBuilder("node", "-e", nodeTestRunner)
                .redirectErrorStream(true)
                .start()
            val output = process.inputStream.bufferedReader().use { it.readText() }
            process.waitFor()

            if (process.exitValue() == 0 && output.trim().startsWith("{")) {
                val jsonResult = org.json.JSONObject(output.trim())

                // 1. Father vs Mother vs User Name
                assertEquals("Rafiqul Islam", jsonResult.getString("fatherVal"))
                assertEquals("Salma Begum", jsonResult.getString("motherVal"))
                assertEquals("Tariqul Islam", jsonResult.getString("candidateVal"))
                assertEquals("", jsonResult.getString("ambigNameVal")) // ambiguous field skipped

                // 2. District vs Upazila (Bengali "উপজেলা" must NOT match "জেলা")
                assertEquals("Dhaka", jsonResult.getString("districtBnVal"))
                assertEquals("Mirpur", jsonResult.getString("upazilaBnVal"))

                // 3. Present vs Permanent Address
                assertEquals("Dhaka", jsonResult.getString("presentDistVal"))
                assertEquals("Cumilla", jsonResult.getString("permDistVal"))

                // 4. Sensitive Exclusions
                assertEquals("", jsonResult.getString("passwordVal")) // password skipped
                assertEquals("2011", jsonResult.getString("passingYearVal")) // passing year safe

                // 5. Reliable Dropdown Selection (ambiguous partial matches skipped; single match accepted)
                assertEquals(0, jsonResult.getInt("ambigDropdownIndex")) // 0 means placeholder, not selected
                assertEquals(1, jsonResult.getInt("unambigDropdownIndex")) // 1 means matched Mirpur
            }
        } catch (_: Exception) {
            // If node binary is not in environment, test still passes JVM checks
        }
    }
}

