package rek.gofscoreboard

import androidx.test.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import androidx.test.rule.ActivityTestRule

import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
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
    val activityScenarioRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getTargetContext()
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

    private fun checkScoreboardActivityExists() {
        onView(withId(R.id.labelNewScoreText)).check(matches(isDisplayed()))
    }

    private fun checkScoreboardActivityDoesNotExist() {
        onView(withId(R.id.labelNewScoreText)).check(doesNotExist())
    }
}
