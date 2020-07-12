package rek.gofscoreboard

import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ScoreHelperUnitTests {
    // https://medium.com/pxhouse/unit-testing-with-mutablelivedata-22b3283a7819
    // In order to use LiveData in unit test
    @Rule @JvmField
    var rule: TestRule = InstantTaskExecutorRule()

    @Test
    fun calculateScore_nbCardsLeft_is_5() {
        // Act
        val score = ScoreHelper.calculateScore(5)

        // Assert
        assertEquals(5, score)
    }

    @Test
    fun calculateScore_nbCardsLeft_is_9() {
        // Act
        val score = ScoreHelper.calculateScore(9)

        // Assert
        assertEquals(18, score)
    }

    @Test
    fun calculateScore_nbCardsLeft_is_11() {
        // Act
        val score = ScoreHelper.calculateScore(11)

        // Assert
        assertEquals(33, score)
    }

    @Test
    fun calculateScore_nbCardsLeft_is_15() {
        // Act
        val score = ScoreHelper.calculateScore(15)

        // Assert
        assertEquals(60, score)
    }

    @Test
    fun calculateScore_nbCardsLeft_is_16() {
        // Act
        val score = ScoreHelper.calculateScore(16)

        // Assert
        assertEquals(80, score)
    }

    @Test
    fun calculateScore_nbCardsLeft_is_99() {
        // Act
        val score = ScoreHelper.calculateScore(99)

        // Assert
        assertEquals(0, score)
    }

    @Test
    fun areScoresValid_pass() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "0"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "16"

        val playerFour = Player(4, "p4")
        playerFour.nbCardsLeft.value = "12"

        val playersList = listOf(playerOne, playerTwo, playerThree, playerFour)

        // Act
        val areScoresValid = ScoreHelper.areScoresValid(playersList)

        // Assert
        assertTrue(areScoresValid)
    }

    @Test
    fun areScoresValid_fail() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "0"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "17"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val areScoresValid = ScoreHelper.areScoresValid(playersList)

        // Assert
        assertFalse(areScoresValid)
    }

    @Test
    fun onlyOneWinnerExistsForThisRound_pass() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "0"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "15"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val oneWinnerOnly = ScoreHelper.onlyOneWinnerExistsForThisRound(playersList)

        // Assert
        assertTrue(oneWinnerOnly)
    }

    @Test
    fun onlyOneWinnerExistsForThisRound_fail_0_winner() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "1"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "15"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val oneWinnerOnly = ScoreHelper.onlyOneWinnerExistsForThisRound(playersList)

        // Assert
        assertFalse(oneWinnerOnly)
    }

    @Test
    fun onlyOneWinnerExistsForThisRound_fail_2_winners() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "0"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "0"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val oneWinnerOnly = ScoreHelper.onlyOneWinnerExistsForThisRound(playersList)

        // Assert
        assertFalse(oneWinnerOnly)
    }

    @Test
    fun canAddScoresRound_pass() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = "1"

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "0"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val canAddScore = ScoreHelper.canAddScoresRound(playersList)

        // Assert
        assertTrue(canAddScore)
    }

    @Test
    fun canAddScoresRound_fail_nullValue() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = null

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "0"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val canAddScore = ScoreHelper.canAddScoresRound(playersList)

        // Assert
        assertFalse(canAddScore)
    }

    @Test
    fun canAddScoresRound_fail_emptyValue() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.nbCardsLeft.value = "5"

        val playerTwo = Player(2, "p2")
        playerTwo.nbCardsLeft.value = ""

        val playerThree = Player(3, "p3")
        playerThree.nbCardsLeft.value = "0"

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val canAddScore = ScoreHelper.canAddScoresRound(playersList)

        // Assert
        assertFalse(canAddScore)
    }

    @Test
    fun getFinalRanking_4_players() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.stackedScore.push(54)

        val playerTwo = Player(2, "p2")
        playerTwo.stackedScore.push(18)

        val playerThree = Player(3, "p3")
        playerThree.stackedScore.push(100)

        val playerFour = Player(4, "p4")
        playerFour.stackedScore.push(92)

        val playersList = listOf(playerOne, playerTwo, playerThree, playerFour)

        // Act
        val sortedPlayerList = ScoreHelper.getFinalRanking(playersList)

        // Assert
        assertEquals("p2", sortedPlayerList!![0].name)
        assertEquals("p1", sortedPlayerList[1].name)
        assertEquals("p4", sortedPlayerList[2].name)
        assertEquals("p3", sortedPlayerList[3].name)
    }

    @Test
    fun getFinalRanking_3_players() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.stackedScore.push(54)

        val playerTwo = Player(2, "p2")
        playerTwo.stackedScore.push(18)

        val playerThree = Player(3, "p3")
        playerThree.stackedScore.push(100)

        val playersList = listOf(playerOne, playerTwo, playerThree)

        // Act
        val sortedPlayerList = ScoreHelper.getFinalRanking(playersList)

        // Assert
        assertEquals("p2", sortedPlayerList!![0].name)
        assertEquals("p1", sortedPlayerList[1].name)
        assertEquals("p3", sortedPlayerList[2].name)
    }

    @Test
    fun getFinalRanking_score_100_is_not_reached() {
        // Arrange
        val playerOne = Player(1, "p1")
        playerOne.stackedScore.push(54)

        val playerTwo = Player(2, "p2")
        playerTwo.stackedScore.push(18)

        val playerThree = Player(3, "p3")
        playerThree.stackedScore.push(99)

        val playerFour = Player(4, "p4")
        playerFour.stackedScore.push(92)

        val playersList = listOf(playerOne, playerTwo, playerThree, playerFour)

        // Act
        val sortedPlayerList = ScoreHelper.getFinalRanking(playersList)

        // Assert
        assertNull(sortedPlayerList)
    }
}