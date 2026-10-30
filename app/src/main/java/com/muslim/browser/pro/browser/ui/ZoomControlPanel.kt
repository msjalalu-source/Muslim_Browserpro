package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.browser.ZoomController
import kotlin.math.roundToInt

/**
 * ZoomControlPanel matching the reference screenshot design:
 * - Floating pure black container with smooth rounded corners
 * - Header with bold white "Page Zoom" title on the left
 * - Header with permanently visible and stable "Reset" button and light-blue percentage badge on the right
 * - Smooth horizontal slider with light-blue thumb and active track, dark inactive track
 * - Real-time continuous zoom response without lag or stutter
 */
@Composable
fun ZoomControlPanel(
    currentZoom: Int,
    defaultZoom: Int = ZoomController.DEFAULT_ZOOM,
    onZoomChange: (Int) -> Unit,
    onReset: () -> Unit,
    onSaveDefault: () -> Unit = {},
    onClose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var sliderZoom by remember { mutableFloatStateOf(currentZoom.toFloat()) }

    LaunchedEffect(currentZoom) {
        sliderZoom = currentZoom.toFloat()
    }

    val displayZoom = sliderZoom.roundToInt().coerceIn(ZoomController.MIN_ZOOM, ZoomController.MAX_ZOOM)

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .testTag("zoom_control_panel"),
        shape = RoundedCornerShape(18.dp),
        color = Color(0xFF000000),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
        border = BorderStroke(1.dp, Color(0xFF1E2028))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header Row: Title on Left, Reset Action and Percentage Badge on Right
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Page Zoom",
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.weight(1f))

                // Reset Action Text Button - permanently available, stable layout & position
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(color = Color.White.copy(alpha = 0.2f), bounded = true)
                        ) {
                            sliderZoom = ZoomController.DEFAULT_ZOOM.toFloat()
                            onReset()
                        }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("zoom_reset_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Reset",
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color(0xFFB0B3BA)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Percentage Badge Container Pill with stable min-width so Reset button never shifts horizontally
                Box(
                    modifier = Modifier
                        .defaultMinSize(minWidth = 54.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF1B2232))
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("zoom_percentage_badge"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "$displayZoom%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF8AB4F8),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Smooth Slider with Screenshot Colors
            Slider(
                value = sliderZoom,
                onValueChange = { newValue ->
                    sliderZoom = newValue
                    val rounded = newValue.roundToInt().coerceIn(ZoomController.MIN_ZOOM, ZoomController.MAX_ZOOM)
                    onZoomChange(rounded)
                },
                valueRange = ZoomController.MIN_ZOOM.toFloat()..ZoomController.MAX_ZOOM.toFloat(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("zoom_slider"),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFF8AB4F8),
                    activeTrackColor = Color(0xFF8AB4F8),
                    inactiveTrackColor = Color(0xFF282B33)
                )
            )
        }
    }
}
