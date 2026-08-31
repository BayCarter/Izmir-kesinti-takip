package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.model.OutageCategory
import com.example.model.OutageItem
import com.example.model.OutageStatus
import com.example.model.OutageType
import com.example.ui.components.OutageCard
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
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun outage_card_screenshot() {
    val sampleOutage = OutageItem(
      id = "test-1",
      type = OutageType.WATER,
      category = OutageCategory.FAULT,
      status = OutageStatus.ACTIVE,
      district = "Konak",
      neighborhoods = listOf("Alsancak", "Kültür"),
      title = "Konak Su Şebekesi Arızası",
      reason = "Ana boru arızası ve bakım onarım çalışması",
      startTime = System.currentTimeMillis() - 3600000,
      estimatedEndTime = System.currentTimeMillis() + 7200000,
      announcementDate = System.currentTimeMillis() - 4000000,
      affectedSubscriberCount = 1200,
      isFavoriteAddressAffected = true
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        OutageCard(outage = sampleOutage)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/outage_card.png")
  }
}
