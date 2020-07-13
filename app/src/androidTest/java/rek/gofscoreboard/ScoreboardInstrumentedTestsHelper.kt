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

    fun removePreviousScoresWithDialogToClose() {
        removePreviousScores()
        onView(withText(R.string.yes)).inRoot(isDialog()).perform(click())
    }

    fun checkScoreboardActivityIsDisplayed() {
        onView(withId(R.id.labelNewScoreText)).check(matches(isDisplayed()))
    }
}