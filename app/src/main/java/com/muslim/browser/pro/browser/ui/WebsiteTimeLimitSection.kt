package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.WebsiteTimeLimitPolicy
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun WebsiteTimeLimitSection(
    timeLimitedDomains: Set<String>,
    domainUsageSeconds: Map<String, Long>,
    onAddDomain: (String) -> Boolean
) {
    val colors = LocalAppColors.current
    var domainInput by remember { mutableStateOf("") }
    var domainError by remember { mutableStateOf<String?>(null) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("section_website_time_limit"),
        shape = RoundedCornerShape(8.dp),
        color = colors.surfaceVariant,
        border = BorderStroke(1.dp, colors.border)
    ) {
        Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
            // Header Row: Name & Fixed 20m Badge
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.HourglassTop,
                        contentDescription = null,
                        tint = colors.iconTint,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Website Time Limit",
                        color = colors.textPrimary,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = colors.accent.copy(alpha = 0.15f),
                    border = BorderStroke(1.dp, colors.accent)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Permanent Limit",
                            tint = colors.accent,
                            modifier = Modifier.size(10.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "20m / day",
                            color = colors.accent,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Daily 20m allowance per site. Added domains are permanent.",
                color = colors.textSecondary,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Add Domain Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = domainInput,
                    onValueChange = {
                        domainInput = it
                        domainError = null
                    },
                    placeholder = {
                        Text("e.g. facebook.com", color = colors.textSecondary, fontSize = 11.sp)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("time_limit_domain_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary,
                        focusedBorderColor = colors.accent,
                        unfocusedBorderColor = colors.border,
                        cursorColor = colors.accent
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                Button(
                    onClick = {
                        val trimmed = domainInput.trim()
                        if (trimmed.isBlank()) {
                            domainError = "Please enter a domain"
                            return@Button
                        }
                        val success = onAddDomain(trimmed)
                        if (success) {
                            domainInput = ""
                            domainError = null
                        } else {
                            domainError = "Invalid domain or already registered"
                        }
                    },
                    shape = RoundedCornerShape(6.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colors.buttonBackground,
                        contentColor = colors.buttonText
                    ),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("add_time_limit_domain_button")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Add", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            if (domainError != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = domainError ?: "",
                    color = Color(0xFFEF5350),
                    fontSize = 10.5.sp
                )
            }

            // Display all added domains with their daily usage status
            if (timeLimitedDomains.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    timeLimitedDomains.sorted().forEach { domain ->
                        val usedSeconds = domainUsageSeconds[domain] ?: 0L
                        val isReached = usedSeconds >= WebsiteTimeLimitPolicy.DAILY_LIMIT_SECONDS
                        val usageText = WebsiteTimeLimitPolicy.formatUsageStatus(usedSeconds)

                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("time_limited_domain_item_$domain"),
                            shape = RoundedCornerShape(6.dp),
                            color = if (isReached) Color(0xFFD32F2F).copy(alpha = 0.12f) else colors.surface,
                            border = BorderStroke(
                                0.5.dp,
                                if (isReached) Color(0xFFD32F2F).copy(alpha = 0.5f) else colors.border
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = "Permanently Registered",
                                        tint = if (isReached) Color(0xFFD32F2F) else colors.textSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = domain,
                                        color = colors.textPrimary,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }

                                Text(
                                    text = usageText,
                                    color = if (isReached) Color(0xFFD32F2F) else colors.textSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isReached) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
