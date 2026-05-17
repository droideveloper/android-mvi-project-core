package com.mvi.core.testing

import android.os.Build
import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import org.junit.Rule
import org.robolectric.annotation.Config

@Config(
    sdk = [
        Build.VERSION_CODES.N,
    ],
)
abstract class AbsAndroidUnitTest {

    @get:Rule
    val testRule: ComposeTestRule = createAndroidComposeRule<ComponentActivity>()

    fun ComposeTestRule.setScreen(
        content: @Composable () -> Unit,
    ) {
        if (this is ComposeContentTestRule) {
            setContent { content() }
        }
    }
}
