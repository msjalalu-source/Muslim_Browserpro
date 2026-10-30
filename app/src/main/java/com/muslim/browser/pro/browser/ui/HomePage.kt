package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import com.muslim.browser.pro.ui.theme.GoogleSansFlexFontFamily
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.muslim.browser.pro.R
import com.muslim.browser.pro.browser.BrowserUiState
import com.muslim.browser.pro.browser.FavoriteSite
import com.muslim.browser.pro.browser.FaviconManager
import com.muslim.browser.pro.browser.SearchEngine
import com.muslim.browser.pro.ui.theme.LocalAppColors

@Composable
fun AddFavoriteSiteTile(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("add_favorite_button")
            .testTag("add_favorite_tile")
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(58.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color(0xFFE0F2FE),
            border = BorderStroke(1.2.dp, Color(0xFF0284C7)),
            shadowElevation = 1.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Website",
                    tint = Color(0xFF0284C7),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun FavoriteSiteItem(
    site: FavoriteSite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    val context = LocalContext.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("fav_site_${site.id}")
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        Surface(
            modifier = Modifier.size(58.dp),
            shape = RoundedCornerShape(14.dp),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE2E8F0)),
            shadowElevation = 1.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                val urlLower = site.url.lowercase()
                when {
                    urlLower.contains("google.com") || urlLower.contains("google.") -> {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search_google),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    urlLower.contains("duckduckgo.com") -> {
                        Image(
                            painter = painterResource(id = R.drawable.ic_search_duckduckgo),
                            contentDescription = null,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    else -> {
                        var faviconBitmap by remember(site.url) {
                            mutableStateOf(FaviconManager.getMemoryCached(site.url))
                        }

                        LaunchedEffect(site.url) {
                            if (faviconBitmap == null) {
                                val loaded = FaviconManager.loadFavicon(context, site.url)
                                if (loaded != null) {
                                    faviconBitmap = loaded
                                }
                            }
                        }

                        val bitmap = faviconBitmap
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .size(30.dp)
                                    .clip(RoundedCornerShape(6.dp))
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = colors.accent,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HomePage(
    uiState: BrowserUiState,
    favoriteSites: List<FavoriteSite>,
    onNavigate: (String) -> Unit,
    onSearch: (String) -> Unit,
    onAddFavorite: (String, String) -> Unit,
    onSelectSearchEngine: (SearchEngine) -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current
    var queryText by remember { mutableStateOf("") }
    var showSearchEngineMenu by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }

    var newSiteName by remember { mutableStateOf("") }
    var newSiteUrl by remember { mutableStateOf("") }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .windowInsetsPadding(WindowInsets.statusBars.union(WindowInsets.displayCutout.only(WindowInsetsSides.Top)))
    ) {
        val topOffset = maxHeight * 0.20f

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(topOffset))

            // 1. Header: Centered App Title & Subtitle
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Muslim Browser",
                    fontFamily = GoogleSansFlexFontFamily,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colors.textPrimary,
                    letterSpacing = 0.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "Ultra-Lightweight • Protected Browser",
                    fontFamily = GoogleSansFlexFontFamily,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = colors.textSecondary,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

        // 2. Pill-shaped Search Bar (Matching Screenshot)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(2.dp, shape = RoundedCornerShape(50))
                .testTag("home_search_bar"),
            shape = RoundedCornerShape(50),
            color = Color.White,
            border = BorderStroke(1.dp, Color(0xFFE0E0E0))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Search Engine Selector with Dropdown
                Box {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .clickable { showSearchEngineMenu = !showSearchEngineMenu }
                            .testTag("search_engine_selector_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        when (uiState.selectedSearchEngine) {
                            SearchEngine.DUCKDUCKGO -> {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_duckduckgo),
                                    contentDescription = "DuckDuckGo",
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            SearchEngine.GOOGLE -> {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_google),
                                    contentDescription = "Google",
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = showSearchEngineMenu,
                        onDismissRequest = { showSearchEngineMenu = false },
                        modifier = Modifier.testTag("search_engine_dropdown_menu")
                    ) {
                        DropdownMenuItem(
                            text = { Text("DuckDuckGo (Safe Search)") },
                            onClick = {
                                onSelectSearchEngine(SearchEngine.DUCKDUCKGO)
                                showSearchEngineMenu = false
                            },
                            modifier = Modifier.testTag("search_engine_option_duckduckgo"),
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_duckduckgo),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Google") },
                            onClick = {
                                onSelectSearchEngine(SearchEngine.GOOGLE)
                                showSearchEngineMenu = false
                            },
                            modifier = Modifier.testTag("search_engine_option_google"),
                            leadingIcon = {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_search_google),
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Text Input
                BasicTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input"),
                    textStyle = TextStyle(
                        fontSize = 15.sp,
                        color = Color(0xFF202124)
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(colors.accent),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search,
                        keyboardType = KeyboardType.Uri
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = {
                            if (queryText.isNotBlank()) {
                                onSearch(queryText.trim())
                            }
                        }
                    ),
                    decorationBox = { innerTextField ->
                        if (queryText.isEmpty()) {
                            Text(
                                text = "Search web or enter URL...",
                                fontSize = 15.sp,
                                color = Color(0xFF757575)
                            )
                        }
                        innerTextField()
                    }
                )

                if (queryText.isNotBlank()) {
                    IconButton(
                        onClick = { queryText = "" },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear",
                            tint = Color(0xFF757575),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { onSearch(queryText.trim()) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("submit_search_button")
                    ) {
                        Text(
                            text = "Go",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = colors.accent
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

        // 3. 4-Column Grid of Favorite Sites & Add Tile
        val allItems = favoriteSites
        val rows = allItems.chunked(4)

        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                rowItems.forEach { site ->
                    FavoriteSiteItem(
                        site = site,
                        onClick = { onNavigate(site.url) },
                        modifier = Modifier.weight(1f)
                    )
                }
                // Fill empty slots if less than 4 in row
                val emptySlots = 4 - rowItems.size
                repeat(emptySlots) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Add Favorite Tile Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Start
        ) {
            AddFavoriteSiteTile(
                onClick = { showAddDialog = true },
                modifier = Modifier.fillMaxWidth(0.25f)
            )
            Spacer(modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(36.dp))

        // 5. Quote and Safe Engine Active Card (Matching Screenshot)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_quote_area"),
            shape = RoundedCornerShape(20.dp),
            color = Color(0xFFEDF4FA),
            border = BorderStroke(1.dp, Color(0xFFD3E3F0))
        ) {
            Column(
                modifier = Modifier.padding(vertical = 18.dp, horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "\"Clean browsing is clear thinking. Stay focused on your goals.\"",
                    fontStyle = FontStyle.Italic,
                    fontSize = 13.5.sp,
                    color = Color(0xFF37474F),
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Focus Shield Safe Engine Active",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF0284C7),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
    }

    // Add Website Dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddDialog = false
                newSiteName = ""
                newSiteUrl = ""
            },
            title = { Text(text = "Add Website", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newSiteName,
                        onValueChange = { newSiteName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newSiteUrl,
                        onValueChange = { newSiteUrl = it },
                        label = { Text("URL (e.g. https://example.com)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val name = newSiteName.trim()
                        var url = newSiteUrl.trim()
                        if (name.isNotBlank() && url.isNotBlank()) {
                            if (!url.startsWith("http://") && !url.startsWith("https://")) {
                                url = "https://$url"
                            }
                            onAddFavorite(name, url)
                            showAddDialog = false
                            newSiteName = ""
                            newSiteUrl = ""
                        }
                    },
                    modifier = Modifier.testTag("save_favorite_button")
                ) {
                    Text("Save", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddDialog = false
                        newSiteName = ""
                        newSiteUrl = ""
                    },
                    modifier = Modifier.testTag("cancel_favorite_button")
                ) {
                    Text("Cancel")
                }
            }
        )
    }
}
