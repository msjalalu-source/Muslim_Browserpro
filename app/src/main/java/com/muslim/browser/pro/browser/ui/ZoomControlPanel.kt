package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.ZoomController
import com.muslim.browser.pro.ui.theme.LocalAppColors
import kotlin.math.roundToInt

@Composable
fun ZoomControlPanel(
    currentZoom: Int,
    defaultZoom: Int,
    onZoomChange: (Int) -> Unit,
    onReset: () -> Unit,
    onSaveDefault: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val isDefault = currentZoom == defaultZoom

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("zoom_control_panel"),
        shape = RoundedCornerShape(16.dp),
        color = colors.surface,
        tonalElevation = 6.dp,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, colors.border.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Header Row: Icon, Title, Current Zoom Badge, and Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ZoomIn,
                    contentDescription = "Zoom Icon",
                    modifier = Modifier.size(20.dp),
                    tint = colors.accent
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Page Zoom",
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Current Percentage Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(colors.accent.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("zoom_percentage_badge")
                ) {
                    Text(
                        text = "$currentZoom%",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.accent
                    )
                }

                if (isDefault) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "(Default)",
                        fontSize = 11.sp,
                        color = colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                IconButton(
                    onClick = onClose,
                    modifier = Modifier
                        .size(36.dp)
                        .testTag("zoom_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close Zoom Panel",
                        modifier = Modifier.size(18.dp),
                        tint = colors.textSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Main Controls: Zoom Out (-) Button, Slider, Zoom In (+) Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { onZoomChange((currentZoom - ZoomController.STEP_ZOOM).coerceAtLeast(ZoomController.MIN_ZOOM)) },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_out_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.border.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Zoom Out",
                            modifier = Modifier.size(18.dp),
                            tint = colors.textPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                Slider(
                    value = currentZoom.toFloat(),
                    onValueChange = { onZoomChange(it.roundToInt().coerceIn(ZoomController.MIN_ZOOM, ZoomController.MAX_ZOOM)) },
                    valueRange = ZoomController.MIN_ZOOM.toFloat()..ZoomController.MAX_ZOOM.toFloat(),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("zoom_slider"),
                    colors = SliderDefaults.colors(
                        thumbColor = colors.accent,
                        activeTrackColor = colors.accent,
                        inactiveTrackColor = colors.border.copy(alpha = 0.35f)
                    )
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { onZoomChange((currentZoom + ZoomController.STEP_ZOOM).coerceAtMost(ZoomController.MAX_ZOOM)) },
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("zoom_in_button")
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(colors.border.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Zoom In",
                            modifier = Modifier.size(18.dp),
                            tint = colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Preset Chips (50%, 100%, 125%, 150%, 200%)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(50, 100, 125, 150, 200).forEach { preset ->
                    val isSelected = currentZoom == preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) colors.accent else colors.border.copy(alpha = 0.15f))
                            .clickable { onZoomChange(preset) }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .testTag("zoom_preset_$preset")
                    ) {
                        Text(
                            text = "$preset%",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else colors.textPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Footer Action Row: Reset and Save as Default
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Reset Button
                OutlinedButton(
                    onClick = onReset,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("zoom_reset_button"),
                    border = BorderStroke(1.dp, colors.border.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = colors.textPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Zoom",
                        modifier = Modifier.size(14.dp),
                        tint = colors.textSecondary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Reset", fontSize = 11.5.sp)
                }

                // Save as Default Button
                OutlinedButton(
                    onClick = onSaveDefault,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("zoom_save_default_button"),
                    border = BorderStroke(
                        1.dp,
                        if (isDefault) colors.accent else colors.border.copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = if (isDefault) colors.accent else colors.textPrimary
                    )
                ) {
                    if (isDefault) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Saved Default",
                            modifier = Modifier.size(14.dp),
                            tint = colors.accent
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                    Text(
                        text = if (isDefault) "Default Saved" else "Save as Default",
                        fontSize = 11.5.sp,
                        fontWeight = if (isDefault) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
