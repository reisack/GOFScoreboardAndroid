package rek.gofscoreboard

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import org.hamcrest.Matchers.not

@RunWith(AndroidJUnit4::class)
class MultiActivitiesInstrumentedTests {
    private val helper = ScoreboardInstrumentedTestsHelper()

    @get:Rule
    val activityScenarioRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun saveAndLoadGame_fourPlayers() {
        onView(withId(R.id.radioButton4players)).perform(click())

        // Fill players name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("Stan"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("Kyle"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("Kenny"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourName)).perform(typeText("Clyde"), closeSoftKeyboard())

        // Start game
        onView(withId(R.id.buttonStartGame)).perform(click())

        // Insert some scores
        helper.insertScoresTurn(0, 6, 2, 1)
        helper.insertScoresTurn(4, 0, 3, 9)
        helper.insertScoresTurn(5, 3, 0, 3)

        saveGame()
        goBackToMainActivity()
        Thread.sleep(1000)
        loadSavedGame()
        Thread.sleep(1000)

        // Check
        onView(withId(R.id.labelPlayerOneScore)).check(matches(withText("Stan")))
        onView(withId(R.id.labelPlayerTwoScore)).check(matches(withText("Kyle")))
        onView(withId(R.id.labelPlayerThreeScore)).check(matches(withText("Kenny")))
        onView(withId(R.id.labelPlayerFourScore)).check(matches(withText("Clyde")))
    }

    @Test
    fun saveAndLoadGame_threePlayers() {
        onView(withId(R.id.radioButton3players)).perform(click())

        // Fill players name
        onView(withId(R.id.editPlayerOneName)).perform(typeText("Stan"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoName)).perform(typeText("Kyle"), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeName)).perform(typeText("Kenny"), closeSoftKeyboard())

        // Start game
        onView(withId(R.id.buttonStartGame)).perform(click())

        // Insert some scores
        helper.insertScoresTurn(0, 6, 2)
        helper.insertScoresTurn(4, 0, 3)
        helper.insertScoresTurn(5, 3, 0)

        saveGame()
        goBackToMainActivity()
        Thread.sleep(1000)
        loadSavedGame()
        Thread.sleep(1000)

        // Check
        onView(withId(R.id.labelPlayerOneScore)).check(matches(withText("Stan")))
        onView(withId(R.id.labelPlayerTwoScore)).check(matches(withText("Kyle")))
        onView(withId(R.id.labelPlayerThreeScore)).check(matches(withText("Kenny")))
        onView(withId(R.id.labelPlayerFourScore)).check(matches(not(isDisplayed())))
        onView(withId(R.id.editPlayerFourScore)).check(matches(not(isDisplayed())))
    }

    private fun saveGame() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText(R.string.save_game)).perform(click())
    }

    private fun loadSavedGame() {
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().context)
        onView(withText(R.string.load_saved_game)).perform(click())
    }

    private fun goBackToMainActivity() {
        pressBack()
        helper.clickYesToDialogAlert()
    }
}