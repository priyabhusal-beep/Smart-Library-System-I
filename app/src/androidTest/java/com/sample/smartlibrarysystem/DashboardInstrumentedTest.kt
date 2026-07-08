package com.sample.smartlibrarysystem

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DashboardInstrumentedTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<Dashboard>()

    @Test
    fun dashboard_shows_main_sections() {
        composeRule.onNodeWithText("Types of Books").assertIsDisplayed()
        composeRule.onNodeWithText("Recommended for You").assertIsDisplayed()
        composeRule.onNodeWithText("Borrowed Books").assertIsDisplayed()
    }

    @Test
    fun bottom_navigation_shows_items() {
        composeRule.onNodeWithText("Home").assertIsDisplayed()
        composeRule.onNodeWithText("Search").assertIsDisplayed()
        composeRule.onNodeWithText("History").assertIsDisplayed()
        composeRule.onNodeWithText("Profile").assertIsDisplayed()
    }
}