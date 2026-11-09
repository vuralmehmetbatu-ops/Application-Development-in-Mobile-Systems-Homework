package com.example.week3_assignment

import android.os.ParcelFileDescriptor
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.AnnotatedString
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Exercises the real Activity, ViewModel, and repository together. */
@RunWith(AndroidJUnit4::class)
class HomeScreenTest {
    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun awaitInitialDevices() {
        awaitNode(hasTestTag("total_power") and hasText("106.0 W"))
    }

    @Test
    fun togglingAnOnlineDeviceUpdatesTheHouseTotalInBothDirections() {
        scrollToTag("device_count")
        composeRule.onNodeWithTag("device_count").assertTextEquals("10 cihaz")

        scrollToTag("device_1")
        composeRule.onNodeWithTag("toggle_1").performClick()
        awaitNode(hasTestTag("toggle_1") and hasText("Aç"))
        assertHouseTotal("91.0 W")

        scrollToTag("device_1")
        composeRule.onNodeWithTag("toggle_1").performClick()
        awaitNode(hasTestTag("toggle_1") and hasText("Kapat"))
        assertHouseTotal("106.0 W")
    }

    @Test
    fun roomAndNameFiltersCombineWithoutChangingTheHouseTotal() {
        selectRoom("room_Salon")
        assertDeviceCount("3 cihaz")
        replaceSearch("  LAMBASI  ")
        assertDeviceCount("1 cihaz")

        scrollToTag("device_1")
        composeRule.onNodeWithTag("device_1").assertIsDisplayed()
        composeRule.onNodeWithTag("device_10").assertDoesNotExist()
        assertHouseTotal("106.0 W")

        // Clearing only the room must retain the name filter and reveal the office lamp.
        selectRoom("room_all")
        assertDeviceCount("2 cihaz")
        scrollToTag("device_10")
        composeRule.onNodeWithTag("device_10").assertIsDisplayed()
        assertHouseTotal("106.0 W")
    }

    @Test
    fun rapidRealKeyEventsPreserveTheSearchTextAndCombineWithTheRoomFilter() {
        scrollToTag("search")
        composeRule.onNodeWithTag("search").performClick()

        // A single semantic text replacement cannot catch characters lost between frames.
        // Android injects this sequence without waiting for Compose to idle after each key.
        val commandOutput = InstrumentationRegistry.getInstrumentation().uiAutomation
            .executeShellCommand("input text lamba")
        ParcelFileDescriptor.AutoCloseInputStream(commandOutput).use { it.readBytes() }

        val expectedText = SemanticsMatcher.expectValue(
            SemanticsProperties.EditableText,
            AnnotatedString("lamba"),
        )
        awaitNode(hasTestTag("search") and expectedText)
        composeRule.onNodeWithTag("search").assert(expectedText)
        dismissKeyboard()
        assertDeviceCount("2 cihaz")

        selectRoom("room_Salon")
        assertDeviceCount("1 cihaz")
        scrollToTag("device_1")
        composeRule.onNodeWithTag("device_1").assertIsDisplayed()
        assertHouseTotal("106.0 W")
    }

    @Test
    fun emptySearchResultsCanBeResetTogetherWithTheSelectedRoom() {
        selectRoom("room_Salon")
        replaceSearch("olmayan cihaz")
        assertDeviceCount("0 cihaz")
        composeRule.onNodeWithTag("home_list")
            .performScrollToNode(hasText("Cihaz bulunamadı"))
        composeRule.onNodeWithText("Cihaz bulunamadı").assertIsDisplayed()
        composeRule.onNodeWithText("Filtreleri temizle").performClick()

        assertDeviceCount("10 cihaz")
        scrollToTag("search")
        composeRule.onNodeWithTag("search").assert(
            SemanticsMatcher.expectValue(SemanticsProperties.EditableText, AnnotatedString("")),
        )
        scrollToTag("room_all")
        composeRule.onNodeWithTag("room_all").assertIsSelected()
        assertHouseTotal("106.0 W")
    }

    @Test
    fun offlineDeviceHasADisabledControlAndDoesNotAffectTheHouseTotal() {
        replaceSearch("Balkon")
        assertDeviceCount("1 cihaz")
        scrollToTag("device_9")
        composeRule.onNodeWithText("Çevrimdışı").assertIsDisplayed()
        composeRule.onNodeWithTag("toggle_9").assertIsNotEnabled()
        assertHouseTotal("106.0 W")
    }

    private fun selectRoom(tag: String) {
        scrollToTag(tag)
        composeRule.onNodeWithTag(tag).performClick()
        awaitNode(hasTestTag(tag) and androidx.compose.ui.test.isSelected())
    }

    private fun replaceSearch(query: String) {
        scrollToTag("search")
        composeRule.onNodeWithTag("search").performTextReplacement(query)
        dismissKeyboard()
    }

    private fun dismissKeyboard() {
        composeRule.activityRule.scenario.onActivity { activity ->
            WindowInsetsControllerCompat(activity.window, activity.window.decorView)
                .hide(WindowInsetsCompat.Type.ime())
        }
        composeRule.waitForIdle()
    }

    private fun assertDeviceCount(expected: String) {
        scrollToTag("device_count")
        awaitNode(hasTestTag("device_count") and hasText(expected))
        composeRule.onNodeWithTag("device_count").assertTextEquals(expected)
    }

    private fun assertHouseTotal(expected: String) {
        scrollToTag("total_power")
        awaitNode(hasTestTag("total_power") and hasText(expected))
        composeRule.onNodeWithTag("total_power").assertTextEquals(expected)
    }

    private fun scrollToTag(tag: String) {
        composeRule.onNodeWithTag("home_list").performScrollToNode(hasTestTag(tag))
    }

    private fun awaitNode(matcher: SemanticsMatcher) {
        // The VM intentionally derives state on Dispatchers.Default, outside Compose idling.
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodes(matcher).fetchSemanticsNodes().isNotEmpty()
        }
    }
}
