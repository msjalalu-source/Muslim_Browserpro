package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.browser.BrowserUiState
import com.example.browser.FavoriteSite
import com.example.browser.ui.HomePage
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [34])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun homePage_screenshot() {
    val mockSites = listOf(
      FavoriteSite("Google", "https://www.google.com", "Tools", "G", 0xFF4285F4),
      FavoriteSite("Wikipedia", "https://www.wikipedia.org", "Study", "W", 0xFF333333)
    )
    composeTestRule.setContent {
      MyApplicationTheme {
        HomePage(
          uiState = BrowserUiState(),
          favoriteSites = mockSites,
          onQueryChange = {},
          onSubmitQuery = {},
          onSelectCategory = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

