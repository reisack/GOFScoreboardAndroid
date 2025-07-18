package rek.gofscoreboard

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.ext.junit.rules.ActivityScenarioRule

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class MainInstrumentedTests {
    /**
     * Use [ActivityScenarioRule] to create and launch the activity under test before each test,
     * and close it after each test. This is a replacement for
     * [androidx.test.rule.ActivityTestRule].
     */
    @get:Rule
    val activityScenarioRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("rek.gofscoreboard", appContext.packageName)
    }

    @Test
    fun shouldNotDisplayPlayerFourWhenThreePlayersModeSelected() {
        onView(withId(R.id.radioButton3players)).perform(click())

        // Assert
        onView(withId(R.id.labelPlayerFourName)).check(matches(not(isDisplayed())))
        onView(withId(R.id.editPlayerFourName)).check(matches(not(isDisplayed())))
    }

    @Test
    fun writePlayerNamesWithTechnicalLimitations() {
        onView(withId(R.id.radioButton4players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("Stan"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("Kyle]"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("Ken|ny"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourName)).perform(typeText("[Clyde"), closeSoftKeyboard())

        // Asserted names
        onView(withId(R.id.editPlayerOneName)).check(matches(withText("Stan")))
        onView(withId(R.id.editPlayerTwoName)).check(matches(withText("Kyle")))
        onView(withId(R.id.editPlayerThreeName)).check(matches(withText("Kenny")))
        onView(withId(R.id.editPlayerFourName)).check(matches(withText("Clyde")))
    }

    @Test
    fun shouldNotStartGameIfAllNamesAreNotProvided_fourPlayers() {
        onView(withId(R.id.radioButton4players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("p2"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityDoesNotExist()
    }

    @Test
    fun shouldNotStartGameIfAllNamesAreNotDifferent_fourPlayers() {
        onView(withId(R.id.radioButton4players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("p2"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourName)).perform(typeText("P2"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityDoesNotExist()
    }

    @Test
    fun shouldNotStartGameIfAllNamesAreNotProvided_threePlayers() {
        onView(withId(R.id.radioButton3players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityDoesNotExist()
    }

    @Test
    fun shouldNotStartGameIfAllNamesAreNotDifferent_threePlayers() {
        onView(withId(R.id.radioButton3players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityDoesNotExist()
    }

    @Test
    fun writePlayerNamesAndStartGame_fourPlayers() {
        onView(withId(R.id.radioButton4players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("p2"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourName)).perform(typeText("p4"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityExists()
    }

    @Test
    fun writePlayerNamesAndStartGame_threePlayers() {
        onView(withId(R.id.radioButton3players)).perform(click())

        // Fill player name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("p1"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("p2"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("p3"), closeSoftKeyboard())

        onView(withId(R.id.buttonStartGame)).perform(click())

        // Assert
        checkScoreboardActivityExists()
    }

    @Test
    fun shouldOpenPrivacyInBrowserWhenPrivacyMenuClicked() {
        // Initialize Espresso Intents
        Intents.init()

        // Open the overflow menu (three dots)
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)

        // Click the "Privacy" menu item
        onView(withText(R.string.privacy)).perform(click())

        // Verify the correct intent was fired
        Intents.intended(
            allOf(
                IntentMatchers.hasAction(Intent.ACTION_VIEW),
                IntentMatchers.hasData("https://reisack.github.io/GOF/privacy.html")
            )
        )

        // Release Espresso Intents
        Intents.release()
    }

    private fun checkScoreboardActivityExists() {
        onView(withId(R.id.labelNewScoreText)).check(matches(isDisplayed()))
    }

    private fun checkScoreboardActivityDoesNotExist() {
        onView(withId(R.id.labelNewScoreText)).check(doesNotExist())
    }
}
