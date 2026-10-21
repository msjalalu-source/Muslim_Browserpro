package com.muslim.browser.pro.browser.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
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
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Add",
            fontSize = 11.5.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF0284C7),
            maxLines = 1,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun FavoriteSiteItem(
    site: FavoriteSite,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colors = LocalAppColors.current

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .testTag("fav_site_${site.id}")
            .clickable(onClick = onClick)
            .padding(4.dp)
    ) {
        val isCustomRed = site.badgeColor == 0xFFD32F2F
        val cardColor = if (isCustomRed) Color(0xFFD32F2F) else Color.White
        val cardBorder = if (isCustomRed) null else BorderStroke(1.dp, Color(0xFFE2E8F0))

        Surface(
            modifier = Modifier.size(58.dp),
            shape = RoundedCornerShape(14.dp),
            color = cardColor,
            border = cardBorder,
            shadowElevation = 1.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize().padding(4.dp)
            ) {
                when (site.name) {
                    "MoldovaLive" -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "MD",
                                color = Color(0xFFD32F2F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Spacer(modifier = Modifier.width(1.dp))
                            Text(
                                text = "live",
                                color = Color(0xFF212121),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                    "Moldova1" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "M",
                                    color = Color(0xFF1976D2),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                                Text(
                                    text = "1",
                                    color = Color(0xFFD32F2F),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = "MOLDOVA",
                                fontSize = 7.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1976D2)
                            )
                        }
                    }
                    "Google AI Studio" -> {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFFF1F5F9)),
                            contentAlignment = Alignment.Center
                        ) {
                            Row {
                                Text("A", color = Color(0xFF4285F4), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("I", color = Color(0xFF34A853), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                    "GitHub" -> {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF24292F)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Git",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                    "Prothom Alo" -> {
                        Text(
                            text = "PA",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    "The Daily Star" -> {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "★",
                                color = Color(0xFF212121),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Star",
                                color = Color(0xFF212121),
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    "Ittefaq" -> {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .border(1.5.dp, Color(0xFF212121), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "ই",
                                color = Color(0xFF212121),
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp
                            )
                        }
                    }
                    "BangiNews" -> {
                        Text(
                            text = "bangi",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                    else -> {
                        Text(
                            text = site.iconLetter,
                            color = if (isCustomRed) Color.White else colors.textPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = site.name,
            fontSize = 11.5.sp,
            color = colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center
        )
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // 1. Header: Shield Icon + App Name + Subtitle (Matching Screenshot)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE1F0FA)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Shield Protection",
                    tint = Color(0xFF1E88E5),
                    modifier = Modifier.size(26.dp)
                )
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(
                    text = "MUSLIM BROWSER PRO",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = colors.textPrimary,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ultra-Lightweight • Protected Browser",
                    fontSize = 13.sp,
                    color = colors.textSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(28.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Section Title: FAVOURITE WEBSITES
        Text(
            text = "FAVOURITE WEBSITES",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = colors.textSecondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 4. 4-Column Grid of Favorite Sites & Add Tile
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
