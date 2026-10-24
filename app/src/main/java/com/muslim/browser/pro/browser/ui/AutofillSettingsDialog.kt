package com.muslim.browser.pro.browser.ui

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.muslim.browser.pro.browser.AutofillProfile
import com.muslim.browser.pro.browser.AutofillRepository
import com.muslim.browser.pro.browser.EducationEntry
import com.muslim.browser.pro.browser.PersonalInfo
import com.muslim.browser.pro.browser.WorkExperienceEntry
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun AutofillSettingsDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val colors = LocalAppColors.current

    // Single unified state for the autofill profile
    var profile by remember { mutableStateOf(AutofillRepository.loadProfile(context)) }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles = listOf("Personal", "Profile", "Education", "Work", "Skills")
    var statusMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
                .testTag("autofill_settings_dialog"),
            shape = RoundedCornerShape(16.dp),
            color = colors.surface,
            border = BorderStroke(1.dp, colors.border),
            shadowElevation = 8.dp
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Smart Form Autofill",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Bilingual Names • CV-Restricted Profile",
                            fontSize = 11.5.sp,
                            color = colors.textSecondary
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("close_autofill_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = colors.textSecondary
                        )
                    }
                }

                HorizontalDivider(thickness = 0.5.dp, color = colors.border)

                // Master Toggle & Overwrite Controls
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(colors.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Enable Smart Autofill",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.5.sp,
                                color = colors.textPrimary
                            )
                            Text(
                                text = if (profile.isEnabled) "Active (fills supported forms)" else "Disabled (manual filling only)",
                                fontSize = 11.sp,
                                color = if (profile.isEnabled) colors.accent else colors.textSecondary
                            )
                        }
                        Switch(
                            checked = profile.isEnabled,
                            onCheckedChange = { profile = profile.copy(isEnabled = it) },
                            modifier = Modifier.testTag("autofill_master_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.accent,
                                checkedTrackColor = colors.accent.copy(alpha = 0.35f)
                            )
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overwrite existing non-empty values",
                            fontSize = 12.sp,
                            color = colors.textSecondary
                        )
                        Switch(
                            checked = profile.overwriteExistingFields,
                            onCheckedChange = { profile = profile.copy(overwriteExistingFields = it) },
                            modifier = Modifier.testTag("autofill_overwrite_switch"),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = colors.accent,
                                checkedTrackColor = colors.accent.copy(alpha = 0.35f)
                            )
                        )
                    }
                }

                // Tab Row
                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = colors.surface,
                    contentColor = colors.accent,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = colors.accent
                        )
                    }
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    maxLines = 1,
                                    color = if (selectedTabIndex == index) colors.accent else colors.textSecondary
                                )
                            }
                        )
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    when (selectedTabIndex) {
                        0 -> PersonalInfoTab(profile = profile, onProfileChange = { profile = it })
                        1 -> SummaryAndDecTab(profile = profile, onProfileChange = { profile = it })
                        2 -> EducationTab(profile = profile, onProfileChange = { profile = it })
                        3 -> WorkExperienceTab(profile = profile, onProfileChange = { profile = it })
                        4 -> SkillsAndLanguagesTab(profile = profile, onProfileChange = { profile = it })
                    }
                }

                statusMessage?.let { msg ->
                    Text(
                        text = msg,
                        fontSize = 11.5.sp,
                        color = colors.accent,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 2.dp)
                    )
                }

                HorizontalDivider(thickness = 0.5.dp, color = colors.border)

                // Footer Buttons (Save, Clear, Close)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedButton(
                        onClick = {
                            AutofillRepository.clearProfile(context)
                            profile = AutofillProfile()
                            statusMessage = "All autofill data cleared"
                            Toast.makeText(context, "Autofill profile cleared", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textSecondary),
                        modifier = Modifier.testTag("clear_autofill_profile_button")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Clear Data", fontSize = 12.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                AutofillRepository.saveProfile(context, profile)
                                statusMessage = "Profile saved locally"
                                Toast.makeText(context, "Autofill profile saved", Toast.LENGTH_SHORT).show()
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = colors.accent),
                            modifier = Modifier.testTag("save_autofill_profile_button")
                        ) {
                            Icon(imageVector = Icons.Default.Save, contentDescription = "Save", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Profile", fontSize = 12.sp, color = colors.buttonText)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PersonalInfoTab(
    profile: AutofillProfile,
    onProfileChange: (AutofillProfile) -> Unit
) {
    val info = profile.personalInfo
    fun updateInfo(transform: PersonalInfo.() -> PersonalInfo) {
        onProfileChange(profile.copy(personalInfo = profile.personalInfo.transform()))
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Section: Mandatory 6 Name Fields
        item {
            Text(
                text = "Name Fields (English & বাংলা)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.accent
            )
        }
        item {
            BilingualNameRow(
                label = "User's Name",
                enVal = info.userNameEn,
                onEnChange = { updateInfo { copy(userNameEn = it) } },
                bnVal = info.userNameBn,
                onBnChange = { updateInfo { copy(userNameBn = it) } },
                phEn = "e.g. John Doe",
                phBn = "উদাঃ জন ডো",
                tagEn = "input_user_name_en",
                tagBn = "input_user_name_bn"
            )
        }
        item {
            BilingualNameRow(
                label = "Father's Name",
                enVal = info.fatherNameEn,
                onEnChange = { updateInfo { copy(fatherNameEn = it) } },
                bnVal = info.fatherNameBn,
                onBnChange = { updateInfo { copy(fatherNameBn = it) } },
                phEn = "e.g. Robert Doe",
                phBn = "উদাঃ রবার্ট ডো",
                tagEn = "input_father_name_en",
                tagBn = "input_father_name_bn"
            )
        }
        item {
            BilingualNameRow(
                label = "Mother's Name",
                enVal = info.motherNameEn,
                onEnChange = { updateInfo { copy(motherNameEn = it) } },
                bnVal = info.motherNameBn,
                onBnChange = { updateInfo { copy(motherNameBn = it) } },
                phEn = "e.g. Mary Doe",
                phBn = "উদাঃ মেরি ডো",
                tagEn = "input_mother_name_en",
                tagBn = "input_mother_name_bn"
            )
        }

        // Section: English-Only Non-Name Fields
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Personal Details (English Only)",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.accent
            )
        }
        item {
            CanonicalInputRow("Address", info.address, { updateInfo { copy(address = it) } }, "e.g. Dhaka, Bangladesh")
        }
        item {
            CanonicalInputRow("Phone Number", info.phone, { updateInfo { copy(phone = it) } }, "+8801XXXXXXXXX", KeyboardType.Phone)
        }
        item {
            CanonicalInputRow("Email Address", info.email, { updateInfo { copy(email = it) } }, "user@example.com", KeyboardType.Email)
        }
        item {
            CanonicalInputRow("Date of Birth", info.dateOfBirth, { updateInfo { copy(dateOfBirth = it) } }, "YYYY-MM-DD or DD/MM/YYYY")
        }
        item {
            CanonicalInputRow("National ID Number", info.nationalId, { updateInfo { copy(nationalId = it) } }, "e.g. 199XXXXXXXXXXXXXX", KeyboardType.Number)
        }
        item {
            CanonicalInputRow("Nationality", info.nationality, { updateInfo { copy(nationality = it) } }, "Bangladeshi")
        }
        item {
            CanonicalInputRow("Religion", info.religion, { updateInfo { copy(religion = it) } }, "e.g. Islam")
        }
        item {
            CanonicalInputRow("Gender", info.gender, { updateInfo { copy(gender = it) } }, "Male / Female")
        }
        item {
            CanonicalInputRow("Marital Status", info.maritalStatus, { updateInfo { copy(maritalStatus = it) } }, "Single / Married")
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Blood Group", info.bloodGroup, { updateInfo { copy(bloodGroup = it) } }, "e.g. O+, A+")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Height", info.height, { updateInfo { copy(height = it) } }, "e.g. 5'7\"")
                }
            }
        }
        item {
            CanonicalInputRow("Birth Registration", info.birthRegistration, { updateInfo { copy(birthRegistration = it) } }, "Birth Registration No.", KeyboardType.Text)
        }
        item {
            CanonicalInputRow("Passport ID", info.passportId, { updateInfo { copy(passportId = it) } }, "e.g. A01234567", KeyboardType.Text)
        }
        item {
            CanonicalInputRow("Quota", info.quota, { updateInfo { copy(quota = it) } }, "e.g. Freedom Fighter, None")
        }
        item {
            CanonicalInputRow("Departmental Status", info.departmentalStatus, { updateInfo { copy(departmentalStatus = it) } }, "e.g. Government Employee, None")
        }

        // Section: Present Address
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Present Address",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.accent
            )
        }
        item {
            CanonicalInputRow("Care Of", info.presentCareOf, { updateInfo { copy(presentCareOf = it) } }, "e.g. Guardian's name")
        }
        item {
            CanonicalInputRow("Village / Road / House / Flat", info.presentVillageRoad, { updateInfo { copy(presentVillageRoad = it) } }, "e.g. House 12, Road 4")
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("District", info.presentDistrict, { updateInfo { copy(presentDistrict = it) } }, "e.g. Dhaka")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Upazila / P.S.", info.presentUpazila, { updateInfo { copy(presentUpazila = it) } }, "e.g. Uttara")
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Post Office", info.presentPostOffice, { updateInfo { copy(presentPostOffice = it) } }, "e.g. Uttara H.P.O")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Post Code", info.presentPostCode, { updateInfo { copy(presentPostCode = it) } }, "e.g. 1230", KeyboardType.Number)
                }
            }
        }

        // Section: Permanent Address
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Permanent Address",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = LocalAppColors.current.accent
            )
        }
        item {
            CanonicalInputRow("Care Of", info.permanentCareOf, { updateInfo { copy(permanentCareOf = it) } }, "e.g. Guardian's name")
        }
        item {
            CanonicalInputRow("Village / Road / House / Flat", info.permanentVillageRoad, { updateInfo { copy(permanentVillageRoad = it) } }, "e.g. Village Name, Ward No")
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("District", info.permanentDistrict, { updateInfo { copy(permanentDistrict = it) } }, "e.g. Narsingdi")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Upazila / P.S.", info.permanentUpazila, { updateInfo { copy(permanentUpazila = it) } }, "e.g. Shibpur")
                }
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Post Office", info.permanentPostOffice, { updateInfo { copy(permanentPostOffice = it) } }, "e.g. Shibpur")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Post Code", info.permanentPostCode, { updateInfo { copy(permanentPostCode = it) } }, "e.g. 1620", KeyboardType.Number)
                }
            }
        }
    }
}

@Composable
private fun SummaryAndDecTab(
    profile: AutofillProfile,
    onProfileChange: (AutofillProfile) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            MultilineInputSection(
                title = "Professional Profile & Summary (English Only)",
                value = profile.professionalProfile,
                onValueChange = { onProfileChange(profile.copy(professionalProfile = it)) },
                placeholder = "Professional summary from reference CV"
            )
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            MultilineInputSection(
                title = "Declaration (English Only)",
                value = profile.declaration,
                onValueChange = { onProfileChange(profile.copy(declaration = it)) },
                placeholder = "Declaration statement"
            )
        }
    }
}

@Composable
private fun MultilineInputSection(
    title: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Text(
        text = title,
        fontSize = 11.5.sp,
        fontWeight = FontWeight.SemiBold
    )
    Spacer(modifier = Modifier.height(4.dp))
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp),
        placeholder = { Text(placeholder, fontSize = 11.sp) },
        colors = customTextFieldColors()
    )
}

@Composable
private fun EducationTab(
    profile: AutofillProfile,
    onProfileChange: (AutofillProfile) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Education Records (Strictly 3 records from reference CV)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        itemsIndexed(profile.education) { index, entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Qualification ${index + 1}: ${entry.qualification}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = LocalAppColors.current.accent
                    )

                    fun updateEntry(transform: EducationEntry.() -> EducationEntry) {
                        val list = profile.education.toMutableList()
                        list[index] = entry.transform()
                        onProfileChange(profile.copy(education = list))
                    }

                    val isSscHsc = entry.qualification.contains("SSC", ignoreCase = true) ||
                        entry.qualification.contains("HSC", ignoreCase = true)

                    CanonicalInputRow("Examination", entry.qualification, { newExam ->
                        updateEntry { copy(qualification = newExam) }
                    }, if (isSscHsc) "e.g. SSC, HSC" else "e.g. Graduation, Masters, B.Sc")

                    if (isSscHsc) {
                        CanonicalInputRow("Board", entry.board, { newBoard ->
                            updateEntry { copy(board = newBoard) }
                        }, "Education Board (e.g. Dhaka)")

                        CanonicalInputRow("Roll No", entry.rollNo, { newRoll ->
                            updateEntry { copy(rollNo = newRoll) }
                        }, "Roll Number", KeyboardType.Number)

                        CanonicalInputRow("Group / Subject", entry.groupSubject, { newGroup ->
                            updateEntry { copy(groupSubject = newGroup) }
                        }, "e.g. Science, Commerce, Arts")

                        CanonicalInputRow("Institution", entry.institution, { newInst ->
                            updateEntry { copy(institution = newInst) }
                        }, "School / College Name")

                        CanonicalInputRow("Passing Year", entry.year, { newYear ->
                            updateEntry { copy(year = newYear) }
                        }, "Year (e.g. 2013)")

                        CanonicalInputRow("Result / Division", entry.result, { newRes ->
                            updateEntry { copy(result = newRes) }
                        }, "Result (e.g. Grade A, GPA 5.0)")
                    } else {
                        CanonicalInputRow("Subject / Degree", entry.groupSubject, { newSub ->
                            updateEntry { copy(groupSubject = newSub) }
                        }, "Subject / Degree (e.g. Mathematics)")

                        CanonicalInputRow("University / Institution", entry.institution, { newInst ->
                            updateEntry { copy(institution = newInst) }
                        }, "University / Institution Name")

                        CanonicalInputRow("Result", entry.result, { newRes ->
                            updateEntry { copy(result = newRes) }
                        }, "Result (e.g. 2nd Class, CGPA 3.5)")

                        CanonicalInputRow("Passing Year", entry.year, { newYear ->
                            updateEntry { copy(year = newYear) }
                        }, "Year (e.g. 2020)")

                        CanonicalInputRow("Course Duration", entry.courseDuration, { newDur ->
                            updateEntry { copy(courseDuration = newDur) }
                        }, "Course Duration (e.g. 4 Years, 1 Year)")
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkExperienceTab(
    profile: AutofillProfile,
    onProfileChange: (AutofillProfile) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Work Experience (Strictly 3 records from reference CV)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        itemsIndexed(profile.workExperience) { index, exp ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, LocalAppColors.current.border),
                colors = CardDefaults.cardColors(containerColor = LocalAppColors.current.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        text = "Employment Record ${index + 1}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.5.sp,
                        color = LocalAppColors.current.accent
                    )

                    fun updateExp(transform: WorkExperienceEntry.() -> WorkExperienceEntry) {
                        val list = profile.workExperience.toMutableList()
                        list[index] = exp.transform()
                        onProfileChange(profile.copy(workExperience = list))
                    }

                    CanonicalInputRow("Organization", exp.employer, { newEmp ->
                        updateExp { copy(employer = newEmp) }
                    }, "Company / Organization Name")

                    CanonicalInputRow("Designation / Post", exp.designation, { newDes ->
                        updateExp { copy(designation = newDes) }
                    }, "Job Title / Post")

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            CanonicalInputRow("Length of Service - Start", exp.serviceStart, { newStart ->
                                updateExp { copy(serviceStart = newStart) }
                            }, "DD/MM/YYYY")
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            CanonicalInputRow("Length of Service - End", exp.serviceEnd, { newEnd ->
                                updateExp { copy(serviceEnd = newEnd) }
                            }, "DD/MM/YYYY or Present")
                        }
                    }

                    CanonicalInputRow("Job Description", if (exp.jobDescription.isNotEmpty()) exp.jobDescription else exp.responsibilities, { newDesc ->
                        updateExp { copy(jobDescription = newDesc, responsibilities = newDesc) }
                    }, "Key responsibilities and duties")

                    CanonicalInputRow("Employment Period", exp.period, { newPer ->
                        updateExp { copy(period = newPer) }
                    }, "e.g. 2021 - Present")
                }
            }
        }
    }
}

@Composable
private fun SkillsAndLanguagesTab(
    profile: AutofillProfile,
    onProfileChange: (AutofillProfile) -> Unit = {}
) {
    val colors = LocalAppColors.current
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text("Approved Skills (5 entries)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            profile.skills.forEachIndexed { i, s ->
                Text(
                    text = "${i + 1}. $s",
                    fontSize = 12.sp,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Approved Languages (2 entries)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            profile.languages.forEachIndexed { i, l ->
                Text(
                    text = "${i + 1}. $l",
                    fontSize = 12.sp,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text("Approved Interests (5 entries)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(4.dp))
            profile.interests.forEachIndexed { i, interest ->
                Text(
                    text = "${i + 1}. $interest",
                    fontSize = 12.sp,
                    color = colors.textPrimary,
                    modifier = Modifier.padding(vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
private fun BilingualNameRow(
    label: String,
    enVal: String,
    onEnChange: (String) -> Unit,
    bnVal: String,
    onBnChange: (String) -> Unit,
    phEn: String = "",
    phBn: String = "",
    tagEn: String = "",
    tagBn: String = ""
) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            OutlinedTextField(
                value = enVal,
                onValueChange = onEnChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag(tagEn),
                label = { Text("English", fontSize = 10.sp) },
                placeholder = { if (phEn.isNotBlank()) Text(phEn, fontSize = 10.sp) },
                singleLine = true,
                colors = customTextFieldColors()
            )
            OutlinedTextField(
                value = bnVal,
                onValueChange = onBnChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag(tagBn),
                label = { Text("বাংলা", fontSize = 10.sp) },
                placeholder = { if (phBn.isNotBlank()) Text(phBn, fontSize = 10.sp) },
                singleLine = true,
                colors = customTextFieldColors()
            )
        }
    }
}

@Composable
private fun CanonicalInputRow(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text
) {
    val colors = LocalAppColors.current
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(text = label, fontSize = 11.5.sp, fontWeight = FontWeight.SemiBold, color = colors.textPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { if (placeholder.isNotBlank()) Text(placeholder, fontSize = 11.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = customTextFieldColors()
        )
    }
}

@Composable
private fun customTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = LocalAppColors.current.accent,
    unfocusedBorderColor = LocalAppColors.current.border,
    focusedTextColor = LocalAppColors.current.textPrimary,
    unfocusedTextColor = LocalAppColors.current.textPrimary
)
