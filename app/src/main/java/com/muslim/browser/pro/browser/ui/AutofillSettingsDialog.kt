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

    // Load persisted profile into state
    val initialProfile = remember { AutofillRepository.loadProfile(context) }

    var isEnabled by remember { mutableStateOf(initialProfile.isEnabled) }
    var overwriteExisting by remember { mutableStateOf(initialProfile.overwriteExistingFields) }

    // Exactly 6 independently editable name fields
    var userNameEn by remember { mutableStateOf(initialProfile.personalInfo.userNameEn) }
    var userNameBn by remember { mutableStateOf(initialProfile.personalInfo.userNameBn) }
    var fatherNameEn by remember { mutableStateOf(initialProfile.personalInfo.fatherNameEn) }
    var fatherNameBn by remember { mutableStateOf(initialProfile.personalInfo.fatherNameBn) }
    var motherNameEn by remember { mutableStateOf(initialProfile.personalInfo.motherNameEn) }
    var motherNameBn by remember { mutableStateOf(initialProfile.personalInfo.motherNameBn) }

    // Single English non-name personal info
    var address by remember { mutableStateOf(initialProfile.personalInfo.address) }
    var phone by remember { mutableStateOf(initialProfile.personalInfo.phone) }
    var email by remember { mutableStateOf(initialProfile.personalInfo.email) }
    var dateOfBirth by remember { mutableStateOf(initialProfile.personalInfo.dateOfBirth) }
    var nationality by remember { mutableStateOf(initialProfile.personalInfo.nationality) }
    var gender by remember { mutableStateOf(initialProfile.personalInfo.gender) }
    var maritalStatus by remember { mutableStateOf(initialProfile.personalInfo.maritalStatus) }
    var bloodGroup by remember { mutableStateOf(initialProfile.personalInfo.bloodGroup) }
    var height by remember { mutableStateOf(initialProfile.personalInfo.height) }

    // Professional Profile & Declaration (English-only)
    var profProfile by remember { mutableStateOf(initialProfile.professionalProfile) }
    var declaration by remember { mutableStateOf(initialProfile.declaration) }

    // Education (Bounded 3 entries from CV)
    var eduList by remember { mutableStateOf(initialProfile.education) }

    // Work Experience (Bounded 3 entries from CV)
    var workExp by remember { mutableStateOf(initialProfile.workExperience) }

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
                                text = if (isEnabled) "Active (fills supported forms)" else "Disabled (manual filling only)",
                                fontSize = 11.sp,
                                color = if (isEnabled) colors.accent else colors.textSecondary
                            )
                        }
                        Switch(
                            checked = isEnabled,
                            onCheckedChange = { isEnabled = it },
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
                            checked = overwriteExisting,
                            onCheckedChange = { overwriteExisting = it },
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
                        0 -> PersonalInfoTab(
                            userNameEn = userNameEn, onUserNameEnChange = { userNameEn = it },
                            userNameBn = userNameBn, onUserNameBnChange = { userNameBn = it },
                            fatherNameEn = fatherNameEn, onFatherNameEnChange = { fatherNameEn = it },
                            fatherNameBn = fatherNameBn, onFatherNameBnChange = { fatherNameBn = it },
                            motherNameEn = motherNameEn, onMotherNameEnChange = { motherNameEn = it },
                            motherNameBn = motherNameBn, onMotherNameBnChange = { motherNameBn = it },
                            address = address, onAddressChange = { address = it },
                            phone = phone, onPhoneChange = { phone = it },
                            email = email, onEmailChange = { email = it },
                            dob = dateOfBirth, onDobChange = { dateOfBirth = it },
                            nat = nationality, onNatChange = { nationality = it },
                            gender = gender, onGenderChange = { gender = it },
                            marital = maritalStatus, onMaritalChange = { maritalStatus = it },
                            blood = bloodGroup, onBloodChange = { bloodGroup = it },
                            height = height, onHeightChange = { height = it }
                        )
                        1 -> SummaryAndDecTab(
                            profProfile = profProfile, onProfProfileChange = { profProfile = it },
                            declaration = declaration, onDeclarationChange = { declaration = it }
                        )
                        2 -> EducationTab(
                            education = eduList,
                            onUpdateEntry = { idx, updated ->
                                val list = eduList.toMutableList()
                                list[idx] = updated
                                eduList = list
                            }
                        )
                        3 -> WorkExperienceTab(
                            workExp = workExp,
                            onUpdateEntry = { idx, updated ->
                                val list = workExp.toMutableList()
                                list[idx] = updated
                                workExp = list
                            }
                        )
                        4 -> SkillsAndLanguagesTab(initialProfile)
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
                            // Reset local fields
                            val def = AutofillProfile()
                            isEnabled = false
                            overwriteExisting = false
                            userNameEn = ""
                            userNameBn = ""
                            fatherNameEn = ""
                            fatherNameBn = ""
                            motherNameEn = ""
                            motherNameBn = ""
                            address = ""
                            phone = ""
                            email = ""
                            dateOfBirth = ""
                            nationality = def.personalInfo.nationality
                            gender = def.personalInfo.gender
                            maritalStatus = ""
                            bloodGroup = ""
                            height = ""
                            profProfile = ""
                            declaration = ""
                            eduList = AutofillProfile.defaultEducationRecords()
                            workExp = AutofillProfile.defaultWorkExperienceSlots()
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
                                val updatedProfile = AutofillProfile(
                                    isEnabled = isEnabled,
                                    overwriteExistingFields = overwriteExisting,
                                    personalInfo = PersonalInfo(
                                        userNameEn = userNameEn.trim(),
                                        userNameBn = userNameBn.trim(),
                                        fatherNameEn = fatherNameEn.trim(),
                                        fatherNameBn = fatherNameBn.trim(),
                                        motherNameEn = motherNameEn.trim(),
                                        motherNameBn = motherNameBn.trim(),
                                        address = address.trim(),
                                        phone = phone.trim(),
                                        email = email.trim(),
                                        dateOfBirth = dateOfBirth.trim(),
                                        nationality = nationality.trim().ifBlank { "Bangladeshi" },
                                        gender = gender.trim().ifBlank { "Male" },
                                        maritalStatus = maritalStatus.trim(),
                                        bloodGroup = bloodGroup.trim(),
                                        height = height.trim()
                                    ),
                                    professionalProfile = profProfile.trim(),
                                    workExperience = workExp,
                                    education = eduList,
                                    skills = initialProfile.skills,
                                    languages = initialProfile.languages,
                                    interests = initialProfile.interests,
                                    declaration = declaration.trim()
                                )

                                AutofillRepository.saveProfile(context, updatedProfile)
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
    userNameEn: String, onUserNameEnChange: (String) -> Unit,
    userNameBn: String, onUserNameBnChange: (String) -> Unit,
    fatherNameEn: String, onFatherNameEnChange: (String) -> Unit,
    fatherNameBn: String, onFatherNameBnChange: (String) -> Unit,
    motherNameEn: String, onMotherNameEnChange: (String) -> Unit,
    motherNameBn: String, onMotherNameBnChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    phone: String, onPhoneChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    dob: String, onDobChange: (String) -> Unit,
    nat: String, onNatChange: (String) -> Unit,
    gender: String, onGenderChange: (String) -> Unit,
    marital: String, onMaritalChange: (String) -> Unit,
    blood: String, onBloodChange: (String) -> Unit,
    height: String, onHeightChange: (String) -> Unit
) {
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
                enVal = userNameEn,
                onEnChange = onUserNameEnChange,
                bnVal = userNameBn,
                onBnChange = onUserNameBnChange,
                phEn = "e.g. John Doe",
                phBn = "উদাঃ জন ডো",
                tagEn = "input_user_name_en",
                tagBn = "input_user_name_bn"
            )
        }
        item {
            BilingualNameRow(
                label = "Father's Name",
                enVal = fatherNameEn,
                onEnChange = onFatherNameEnChange,
                bnVal = fatherNameBn,
                onBnChange = onFatherNameBnChange,
                phEn = "e.g. Robert Doe",
                phBn = "উদাঃ রবার্ট ডো",
                tagEn = "input_father_name_en",
                tagBn = "input_father_name_bn"
            )
        }
        item {
            BilingualNameRow(
                label = "Mother's Name",
                enVal = motherNameEn,
                onEnChange = onMotherNameEnChange,
                bnVal = motherNameBn,
                onBnChange = onMotherNameBnChange,
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
            CanonicalInputRow("Address", address, onAddressChange, "e.g. Dhaka, Bangladesh")
        }
        item {
            CanonicalInputRow("Phone Number", phone, onPhoneChange, "+8801XXXXXXXXX", KeyboardType.Phone)
        }
        item {
            CanonicalInputRow("Email Address", email, onEmailChange, "user@example.com", KeyboardType.Email)
        }
        item {
            CanonicalInputRow("Date of Birth", dob, onDobChange, "YYYY-MM-DD or DD/MM/YYYY")
        }
        item {
            CanonicalInputRow("Nationality", nat, onNatChange, "Bangladeshi")
        }
        item {
            CanonicalInputRow("Gender", gender, onGenderChange, "Male / Female")
        }
        item {
            CanonicalInputRow("Marital Status", marital, onMaritalChange, "Single / Married")
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Blood Group", blood, onBloodChange, "e.g. O+, A+")
                }
                Box(modifier = Modifier.weight(1f)) {
                    CanonicalInputRow("Height", height, onHeightChange, "e.g. 5'7\"")
                }
            }
        }
    }
}

@Composable
private fun SummaryAndDecTab(
    profProfile: String, onProfProfileChange: (String) -> Unit,
    declaration: String, onDeclarationChange: (String) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Professional Profile & Summary (English Only)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = profProfile,
                onValueChange = onProfProfileChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = { Text("Professional summary from reference CV", fontSize = 11.sp) },
                colors = customTextFieldColors()
            )
        }

        item {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Declaration (English Only)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = declaration,
                onValueChange = onDeclarationChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                placeholder = { Text("Declaration statement", fontSize = 11.sp) },
                colors = customTextFieldColors()
            )
        }
    }
}

@Composable
private fun EducationTab(
    education: List<EducationEntry>,
    onUpdateEntry: (Int, EducationEntry) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Education Records (Strictly 3 records from reference CV)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        itemsIndexed(education) { index, entry ->
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

                    CanonicalInputRow("Passing Year", entry.year, { newYear ->
                        onUpdateEntry(index, entry.copy(year = newYear))
                    }, "Year (e.g. 2020)")

                    CanonicalInputRow("Institution", entry.institution, { newInst ->
                        onUpdateEntry(index, entry.copy(institution = newInst))
                    }, "Institution Name")

                    CanonicalInputRow("Result / Division", entry.result, { newRes ->
                        onUpdateEntry(index, entry.copy(result = newRes))
                    }, "Result (e.g. 2nd Class, Grade A)")
                }
            }
        }
    }
}

@Composable
private fun WorkExperienceTab(
    workExp: List<WorkExperienceEntry>,
    onUpdateEntry: (Int, WorkExperienceEntry) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        item {
            Text(
                text = "Work Experience (Strictly 3 records from reference CV)",
                fontSize = 11.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
        itemsIndexed(workExp) { index, exp ->
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

                    CanonicalInputRow("Employer / Organization", exp.employer, { newEmp ->
                        onUpdateEntry(index, exp.copy(employer = newEmp))
                    }, "Company Name")

                    CanonicalInputRow("Designation / Role", exp.designation, { newDes ->
                        onUpdateEntry(index, exp.copy(designation = newDes))
                    }, "Job Title")

                    CanonicalInputRow("Employment Period", exp.period, { newPer ->
                        onUpdateEntry(index, exp.copy(period = newPer))
                    }, "e.g. 2021 - Present")

                    CanonicalInputRow("Responsibilities", exp.responsibilities, { newResp ->
                        onUpdateEntry(index, exp.copy(responsibilities = newResp))
                    }, "Key duties")
                }
            }
        }
    }
}

@Composable
private fun SkillsAndLanguagesTab(profile: AutofillProfile) {
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
