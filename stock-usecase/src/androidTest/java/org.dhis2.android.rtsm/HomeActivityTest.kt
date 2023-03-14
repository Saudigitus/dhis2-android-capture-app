package org.dhis2.android.rtsm

import androidx.compose.material.Text
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.junit4.createComposeRule
import org.dhis2.android.rtsm.ui.home.HomeActivity
import org.junit.Rule
import org.junit.Test

class HomeActivityTest {

    @get:Rule
//    val composeTestRule = createAndroidComposeRule(HomeActivity::class.java)
    val composeTestRule = createComposeRule()

    @Test
    fun myTest() {
        composeTestRule.setContent {
            Text("You can set any Compose content!")
        }
    }


}