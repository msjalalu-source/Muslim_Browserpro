package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.R
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.FavoriteSite
import com.muslim.browser.pro.browser.rememberFavicon

@Composable
fun HomePage(
    uiState: BrowserUiState,
    favoriteSites: List<FavoriteSite>,
    onQueryChange: (String) -> Unit,
    onSubmitQuery: (String) -> Unit,
    onAddFavorite: (name: String, url: String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State for Add Favorite Dialog
    var showFavoriteDialog by remember { mutableStateOf(false) }
    var dialogName by remember { mutableStateOf("") }
    var dialogUrl by remember { mutableStateOf("") }

    fun openAddDialog() {
        dialogName = ""
        dialogUrl = ""
        showFavoriteDialog = true
    }

    val scrimBrush = remember {
        Brush.verticalGradient(
            colors = listOf(
                Color(0x880A0F1D),
                Color(0xAA080B14),
                Color(0xCC05070D)
            )
        )
    }
    val searchBarBorder = remember { BorderStroke(1.dp, Color(0x5542A5F5)) }
    val quoteAreaBorder = remember { BorderStroke(1.dp, Color(0x3300E5FF)) }

    Box(modifier = modifier.fillMaxSize()) {
        // Background wallpaper image
        Image(
            painter = painterResource(id = R.drawable.home_bg),
            contentDescription = "Home background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Dark gradient scrim overlay for visual contrast
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(scrimBrush)
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand Logo & Status Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 28.dp, bottom = 8.dp)
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color(0x3300E5FF),
                    modifier = Modifier.size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = "Shield Protection",
                            tint = Color(0xFF00E5FF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "MUSLIM BROWSER PRO",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Ultra-Lightweight • Protected Browser",
                        color = Color(0xFF90CAF9),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Lower the Address / Search Bar position vertically
            Spacer(modifier = Modifier.height(48.dp))

            // Search / Address Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("home_search_bar"),
                shape = RoundedCornerShape(28.dp),
                color = Color(0xCC1A233A),
                border = searchBarBorder,
                shadowElevation = 8.dp
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF81D4FA),
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    TextField(
                        value = uiState.searchInput,
                        onValueChange = onQueryChange,
                        modifier = Modifier
                            .weight(1f)
                            .testTag("search_text_input"),
                        placeholder = {
                            Text(
                                text = "Search web or enter URL...",
                                color = Color(0xFF90A4AE),
                                fontSize = 14.sp
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color(0xFF00E5FF),
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                        keyboardActions = KeyboardActions(onGo = { onSubmitQuery(uiState.searchInput) })
                    )

                    if (uiState.searchInput.isNotEmpty()) {
                        IconButton(
                            onClick = { onQueryChange("") },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = Color(0xFFB0BEC5),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    IconButton(
                        onClick = { onSubmitQuery(uiState.searchInput) },
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("submit_search_button")
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color(0xFF00E5FF),
                            modifier = Modifier.size(32.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Go",
                                    tint = Color(0xFF0A0F1D),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Favourite websites header (clean, no Add button on top)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 4.dp, end = 4.dp, bottom = 10.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "FAVOURITE WEBSITES",
                    color = Color(0xFFB0BEC5),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.2.sp
                )
            }

            // Favorite Websites Grid with "+ Add" tile matching identical dimensions
            LazyVerticalGrid(
                columns = GridCells.Fixed(4),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(2.dp)
            ) {
                items(favoriteSites, key = { it.id }) { site ->
                    FavoriteSiteItem(
                        site = site,
                        onClick = { onSubmitQuery(site.url) }
                    )
                }

                // Add Tile positioned right after the last tile in the same grid
                item(key = "add_favorite_tile") {
                    AddFavoriteSiteTile(
                        onClick = { openAddDialog() }
                    )
                }
            }

            // Quote / Text Area
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("home_quote_area"),
                shape = RoundedCornerShape(16.dp),
                color = Color(0x4D0D1B2A),
                border = quoteAreaBorder
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "\"Clean browsing is clear thinking. Stay focused on your goals.\"",
                        color = Color(0xFFECEFF1),
                        fontSize = 12.sp,
                        fontStyle = FontStyle.Italic,
                        textAlign = TextAlign.Center,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Focus Shield Safe Engine Active",
                        color = Color(0xFF00E5FF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Add Favorite Dialog
        if (showFavoriteDialog) {
            AlertDialog(
                onDismissRequest = { showFavoriteDialog = false },
                title = {
                    Text(
                        text = "Add Favorite Website",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = dialogName,
                            onValueChange = { dialogName = it },
                            label = { Text("Website Name") },
                            placeholder = { Text("e.g. Google, Wikipedia") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fav_dialog_name_input")
                        )
                        OutlinedTextField(
                            value = dialogUrl,
                            onValueChange = { dialogUrl = it },
                            label = { Text("Website URL") },
                            placeholder = { Text("e.g. https://www.google.com") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fav_dialog_url_input")
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (dialogName.isNotBlank() && dialogUrl.isNotBlank()) {
                                onAddFavorite(dialogName, dialogUrl)
                                showFavoriteDialog = false
                            }
                        },
                        modifier = Modifier.testTag("save_favorite_button")
                    ) {
                        Text("Save", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { showFavoriteDialog = false },
                        modifier = Modifier.testTag("cancel_favorite_button")
                    ) {
                        Text("Cancel", color = Color(0xFF90A4AE))
                    }
                },
                containerColor = Color(0xFF1E293B),
                shape = RoundedCornerShape(16.dp)
            )
        }
    }
}

@Composable
fun FavoriteSiteItem(
    site: FavoriteSite,
    onClick: () -> Unit
) {
    val faviconBitmap = rememberFavicon(site.url)
    val badgeBgColor = remember(site.badgeColor) { Color(site.badgeColor) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag("fav_site_${site.name}")
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = if (faviconBitmap != null) Color(0xFF162032) else badgeBgColor,
            border = BorderStroke(1.dp, if (faviconBitmap != null) Color(0x3342A5F5) else Color(0x22FFFFFF)),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                if (faviconBitmap != null) {
                    Image(
                        bitmap = faviconBitmap.asImageBitmap(),
                        contentDescription = site.name,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    // Fallback to website letter badge
                    Text(
                        text = site.iconLetter,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = site.name,
            color = Color(0xFFECEFF1),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun AddFavoriteSiteTile(
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp)
            .testTag("add_favorite_button")
    ) {
        Surface(
            modifier = Modifier.size(52.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0x2600E5FF),
            border = BorderStroke(1.dp, Color(0x6600E5FF)),
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Website",
                    tint = Color(0xFF00E5FF),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Add",
            color = Color(0xFF00E5FF),
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
