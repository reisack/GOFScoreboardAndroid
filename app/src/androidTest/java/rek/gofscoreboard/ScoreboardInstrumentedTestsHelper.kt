package rek.gofscoreboard

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog

class ScoreboardInstrumentedTestsHelper {
    fun removePreviousScores() {
        // Click on button : Remove previous scores
        onView(withId(R.id.btnRemovePreviousScore)).perform(click())
    }

    fun clickYesToDialogAlert() {
        onView(withText(R.string.yes)).inRoot(isDialog()).perform(click())
    }

    fun checkScoreboardActivityIsDisplayed() {
        onView(withId(R.id.labelNewScoreText)).check(matches(isDisplayed()))
    }

    fun insertScoresTurn(scoreP1: Int, scoreP2: Int, scoreP3: Int) {
        // Write scores
        onView(withId(R.id.editPlayerOneScore)).perform(typeText(scoreP1.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoScore)).perform(typeText(scoreP2.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeScore)).perform(typeText(scoreP3.toString()), closeSoftKeyboard())

        // Validate turn
        onView(withId(R.id.btnAddScore)).perform(click())
    }

    fun insertScoresTurn(scoreP1: Int, scoreP2: Int, scoreP3: Int, scoreP4: Int) {
        // Write scores
        onView(withId(R.id.editPlayerOneScore)).perform(typeText(scoreP1.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerTwoScore)).perform(typeText(scoreP2.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerThreeScore)).perform(typeText(scoreP3.toString()), closeSoftKeyboard())
        onView(withId(R.id.editPlayerFourScore)).perform(typeText(scoreP4.toString()), closeSoftKeyboard())

        // Validate turn
        onView(withId(R.id.btnAddScore)).perform(click())
    }
}