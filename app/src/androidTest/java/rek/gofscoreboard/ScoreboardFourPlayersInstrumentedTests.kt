package rek.gofscoreboard

import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog

@RunWith(AndroidJUnit4::class)
class ScoreboardFourPlayersInstrumentedTests {
    private val helper = ScoreboardInstrumentedTestsHelper()

    @get:Rule
    val activityScenarioRule: ActivityTestRule<ScoreboardActivity> = object : ActivityTestRule<ScoreboardActivity>(ScoreboardActivity::class.java) {
        override fun getActivityIntent(): Intent {
            val targetContext = InstrumentationRegistry.getInstrumentation().targetContext
            val intent = Intent(targetContext, ScoreboardActivity::class.java)
            intent.putExtra(ScoreboardActivity.NB_PLAYERS, 4)
            intent.putExtra(ScoreboardActivity.PLAYER_ONE_NAME, "Stan")
            intent.putExtra(ScoreboardActivity.PLAYER_TWO_NAME, "Kyle")
            intent.putExtra(ScoreboardActivity.PLAYER_THREE_NAME, "Kenny")
            intent.putExtra(ScoreboardActivity.PLAYER_FOUR_NAME, "Clyde")
            return intent
        }
    }

    @Test
    fun playAGameThenClickOnNewGame() {
        insertScoresTurn(16, 0, 1, 1)
        insertScoresTurn(16, 0, 1, 1)
        onView(withText(R.string.new_game)).inRoot(isDialog()).perform(click())

        // Still on Scoreboard Activity
        helper.checkScoreboardActivityIsDisplayed()
    }

    @Test
    fun playAGameThenClickOnBackToMain() {
        insertScoresTurn(16, 0, 1, 1)
        insertScoresTurn(16, 0, 1, 1)
        onView(withText(R.string.back_main_screen_button)).inRoot(isDialog()).perform(click())
    }

    @Test
    fun playALongGameThenClickOnBackToScoreboard() {
        playersNamesShouldAppears()

        // Should not crash app
        helper.removePreviousScores()

        insertScoresTurn(3, 6, 9, 0)
        insertScoresTurn(0, 1, 6, 5)

        // No score with 0
        insertScoresTurnShouldFail(7, 8, 9, 11)

        insertScoresTurn(4, 11, 0, 3)

        // More than one score with 0
        insertScoresTurnShouldFail(6, 0, 4, 0)

        insertScoresTurn(7, 0, 4, 13)
        helper.removePreviousScoresWithDialogToClose()

        for (i in 0..10) {
            insertScoresTurn(1, 0, 1, 1)
        }

        insertScoresTurn(3, 9, 2, 0)
        helper.removePreviousScoresWithDialogToClose()

        // More than one score with 0
        insertScoresTurnShouldFail(7, 0, 9, 0)

        // No score with 0
        insertScoresTurnShouldFail(7, 8, 9, 11)

        insertScoresTurn(0, 16, 4, 1)

        onView(withText(R.string.back_to_scoreboard)).inRoot(isDialog()).perform(click())

        // Still on Scoreboard Activity
        helper.checkScoreboardActivityIsDisplayed()
    }

    private fun playersNamesShouldAppears() {
        // Asserted names
        onView(withId(R.id.labelPlayerOneScore)).check(matches(withText("Stan")))
        onView(withId(R.id.labelPlayerTwoScore)).check(matches(withText("Kyle")))
        onView(withId(R.id.labelPlayerThreeScore)).check(matches(withText("Kenny")))
        onView(withId(R.id.labelPlayerFourScore)).check(matches(withText("Clyde")))
    }

    private fun insertScoresTurn(scoreP1: Int, scoreP2: Int, scoreP3: Int, scoreP4: Int) {
        // Write scores
        onView(withId(R.id.editPlayerOneScore)).perform(typeText(scoreP1.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoScore)).perform(typeText(scoreP2.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeScore)).perform(typeText(scoreP3.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourScore)).perform(typeText(scoreP4.toString()), closeSoftKeyboard())

        // Validate turn
        onView(withId(R.id.btnAddScore)).perform(click())
    }

    private fun insertScoresTurnShouldFail(scoreP1: Int, scoreP2: Int, scoreP3: Int, scoreP4: Int) {
        insertScoresTurn(scoreP1, scoreP2, scoreP3, scoreP4)

        onView(withId(R.id.editPlayerOneScore))
            .check(matches(withText(scoreP1.toString())))
            .perform(clearText())

        onView(withId(R.id.editPlayerTwoScore))
            .check(matches(withText(scoreP2.toString())))
            .perform(clearText())

        onView(withId(R.id.editPlayerThreeScore))
            .check(matches(withText(scoreP3.toString())))
            .perform(clearText())

        onView(withId(R.id.editPlayerFourScore))
            .check(matches(withText(scoreP4.toString())))
            .perform(clearText())
    }
}