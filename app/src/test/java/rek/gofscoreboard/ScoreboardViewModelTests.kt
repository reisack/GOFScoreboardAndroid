package rek.gofscoreboard

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ScoreboardViewModelTests {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: ScoreboardViewModel

    @Before
    fun setup() {
        viewModel = ScoreboardViewModel()
    }

    @Test
    fun `initializeGame with three players configures players and columns`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")

        assertFalse(viewModel.isFourPlayersMode)
        assertEquals(4, viewModel.getScoreboardColumnsCount())
        assertEquals("Stan", viewModel.getPlayerByIndex(0)?.name)
        assertEquals("Kyle", viewModel.getPlayerByIndex(1)?.name)
        assertEquals("Kenny", viewModel.getPlayerByIndex(2)?.name)
        assertNull(viewModel.getPlayerByIndex(3))
    }

    @Test
    fun `initializeGame with four players configures players and columns`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny", "Clyde")

        assertTrue(viewModel.isFourPlayersMode)
        assertEquals(5, viewModel.getScoreboardColumnsCount())
        assertEquals("Clyde", viewModel.getPlayerByIndex(3)?.name)
        assertNull(viewModel.getPlayerByIndex(4))
    }

    @Test
    fun `createScoreboardHeader adds player names and initial turn direction`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")

        viewModel.createScoreboardHeader()

        assertEquals(4, viewModel.getScoreboardHeaderAdapter().itemCount)
        assertEquals(1, viewModel.getScoreboardAdapter().itemCount)
    }

    @Test
    fun `addScoresRound with missing score emits enter scores toast and keeps scoreboard unchanged`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.createScoreboardHeader()
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "5"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = ""
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "0"

        val added = viewModel.addScoresRound()

        assertFalse(added)
        assertEquals(R.string.enter_players_scores, viewModel.toastMessage.value)
        assertEquals(1, viewModel.getScoreboardAdapter().itemCount)
    }

    @Test
    fun `addScoresRound with score outside valid range emits valid scores toast`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "17"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "2"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "0"

        val added = viewModel.addScoresRound()

        assertFalse(added)
        assertEquals(R.string.enter_valid_players_scores, viewModel.toastMessage.value)
    }

    @Test
    fun `addScoresRound with no round winner emits one winner toast`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "5"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "2"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "1"

        val added = viewModel.addScoresRound()

        assertFalse(added)
        assertEquals(R.string.only_one_winner_on_round, viewModel.toastMessage.value)
    }

    @Test
    fun `addScoresRound with two round winners emits one winner toast`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "2"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "0"

        val added = viewModel.addScoresRound()

        assertFalse(added)
        assertEquals(R.string.only_one_winner_on_round, viewModel.toastMessage.value)
    }

    @Test
    fun `addScoresRound stores calculated totals clears inputs and appends next turn`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.createScoreboardHeader()
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "8"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "16"

        val added = viewModel.addScoresRound()

        assertTrue(added)
        assertEquals(16, viewModel.getPlayerByIndex(0)?.getTotalScore())
        assertEquals(0, viewModel.getPlayerByIndex(1)?.getTotalScore())
        assertEquals(80, viewModel.getPlayerByIndex(2)?.getTotalScore())
        assertEquals("", viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value)
        assertEquals("", viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value)
        assertEquals("", viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value)
        assertEquals(5, viewModel.getScoreboardAdapter().itemCount)
        assertFalse(viewModel.gameFinished.value!!)
    }

    @Test
    fun `addScoresRound reaching finish score emits ranking sorted by lowest total`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny", "Clyde")
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "16"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "10"
        viewModel.getPlayerByIndex(3)?.nbCardsLeft?.value = "5"
        assertTrue(viewModel.addScoresRound())

        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "10"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "7"
        viewModel.getPlayerByIndex(3)?.nbCardsLeft?.value = "6"

        val added = viewModel.addScoresRound()

        assertTrue(added)
        assertTrue(viewModel.gameFinished.value!!)
        val ranking = viewModel.finishAlertDialogFinalRanking.value!!
        assertEquals(listOf("Kyle", "Clyde", "Kenny", "Stan"), ranking.map { it.name })
    }

    @Test
    fun `removePreviousScoresRound returns false with no previous score`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")

        assertFalse(viewModel.canRemovePreviousScoresRound())

        viewModel.removePreviousScoresRound()

        assertNull(viewModel.toastMessage.value)
    }

    @Test
    fun `removePreviousScoresRound removes last round and emits deleted toast`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.createScoreboardHeader()
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "4"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "9"
        assertTrue(viewModel.addScoresRound())

        viewModel.removePreviousScoresRound()

        assertEquals(0, viewModel.getPlayerByIndex(0)?.stackedScore?.size)
        assertEquals(0, viewModel.getPlayerByIndex(1)?.stackedScore?.size)
        assertEquals(0, viewModel.getPlayerByIndex(2)?.stackedScore?.size)
        assertFalse(viewModel.canRemovePreviousScoresRound())
        assertEquals(1, viewModel.getScoreboardAdapter().itemCount)
        assertEquals(R.string.previous_scores_round_deleted, viewModel.toastMessage.value)
    }

    @Test
    fun `getContentForSaveFile serializes three player game with scores`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny")
        viewModel.getPlayerByIndex(0)?.nbCardsLeft?.value = "4"
        viewModel.getPlayerByIndex(1)?.nbCardsLeft?.value = "0"
        viewModel.getPlayerByIndex(2)?.nbCardsLeft?.value = "9"
        assertTrue(viewModel.addScoresRound())

        val content = viewModel.getContentForSaveFile()

        assertEquals(
            "isFourPlayersMode|false,name1|Stan,score1|4,name2|Kyle,score2|0,name3|Kenny,score3|18",
            content
        )
    }

    @Test
    fun `getContentForSaveFile serializes four player game without scores`() {
        viewModel.initializeGame("Stan", "Kyle", "Kenny", "Clyde")

        val content = viewModel.getContentForSaveFile()

        assertEquals(
            "isFourPlayersMode|true,name1|Stan,score1|,name2|Kyle,score2|,name3|Kenny,score3|,name4|Clyde,score4|",
            content
        )
    }

    @Test
    fun `initializeGameWithSave loads three player names and saved totals`() {
        val saveFileContent =
            "isFourPlayersMode|false,name1|Stan,score1|4#16,name2|Kyle,score2|0#0,name3|Kenny,score3|18#7"

        viewModel.initializeGameWithSave(saveFileContent)
        viewModel.createScoreboardHeader()
        viewModel.loadScoreFromSave()

        assertFalse(viewModel.isFourPlayersMode)
        assertEquals("Stan", viewModel.getPlayerByIndex(0)?.name)
        assertEquals(20, viewModel.getPlayerByIndex(0)?.getTotalScore())
        assertEquals(0, viewModel.getPlayerByIndex(1)?.getTotalScore())
        assertEquals(25, viewModel.getPlayerByIndex(2)?.getTotalScore())
        assertEquals(9, viewModel.getScoreboardAdapter().itemCount)
    }

    @Test
    fun `initializeGameWithSave loads four player names and saved totals`() {
        val saveFileContent =
            "isFourPlayersMode|true,name1|Stan,score1|4,name2|Kyle,score2|0,name3|Kenny,score3|18,name4|Clyde,score4|6"

        viewModel.initializeGameWithSave(saveFileContent)
        viewModel.createScoreboardHeader()
        viewModel.loadScoreFromSave()

        assertTrue(viewModel.isFourPlayersMode)
        assertEquals("Clyde", viewModel.getPlayerByIndex(3)?.name)
        assertEquals(4, viewModel.getPlayerByIndex(0)?.getTotalScore())
        assertEquals(0, viewModel.getPlayerByIndex(1)?.getTotalScore())
        assertEquals(18, viewModel.getPlayerByIndex(2)?.getTotalScore())
        assertEquals(6, viewModel.getPlayerByIndex(3)?.getTotalScore())
        assertEquals(6, viewModel.getScoreboardAdapter().itemCount)
    }

    @Test
    fun `loadScoreFromSave keeps scoreboard unchanged when save has no scores`() {
        val saveFileContent =
            "isFourPlayersMode|false,name1|Stan,score1|,name2|Kyle,score2|,name3|Kenny,score3|"

        viewModel.initializeGameWithSave(saveFileContent)
        viewModel.createScoreboardHeader()
        viewModel.loadScoreFromSave()

        assertEquals(0, viewModel.getPlayerByIndex(0)?.getTotalScore())
        assertEquals(0, viewModel.getPlayerByIndex(1)?.getTotalScore())
        assertEquals(0, viewModel.getPlayerByIndex(2)?.getTotalScore())
        assertEquals(1, viewModel.getScoreboardAdapter().itemCount)
    }
}
