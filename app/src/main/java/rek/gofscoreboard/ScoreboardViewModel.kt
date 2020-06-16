package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.lang.StringBuilder
import java.util.*

class ScoreboardViewModel : ViewModel() {
    val toastMessage = MutableLiveData<Int>()
    val finishAlertDialogFinalRanking = MutableLiveData<List<Player>>()

    var isFourPlayersMode: Boolean = true
    val gameFinished = MutableLiveData<Boolean>()

    private var invertedTurn: Boolean = false
    private lateinit var playersList: List<Player>

    private lateinit var scoreboardHeader: List<String>
    private val scoreboard: MutableList<String> = mutableListOf()

    init {
        gameFinished.value = false
    }

    fun getPlayerBindingByIndex(index: Int): Player? {
        return playersList.getOrNull(index)
    }

    fun getScoreboardColumnsCount() = if (isFourPlayersMode) 5 else 4

    fun getScoreboardAdapter() = ScoreboardAdapter(scoreboard.toTypedArray())
    fun getScoreboardHeaderAdapter() = ScoreboardAdapter(scoreboardHeader.toTypedArray())

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String) {
        val playerOne = Player(1, playerOneName)
        val playerTwo = Player(2,playerTwoName)
        val playerThree = Player(3, playerThreeName)

        playersList = listOf(playerOne, playerTwo, playerThree)
        isFourPlayersMode = false
    }

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String
                       , playerFourName: String) {
        val playerOne = Player(1, playerOneName)
        val playerTwo = Player(2,playerTwoName)
        val playerThree = Player(3, playerThreeName)
        val playerFour = Player(4, playerFourName)

        playersList = listOf(playerOne, playerTwo, playerThree, playerFour)
        isFourPlayersMode = true
    }

    fun createScoreboardHeader() {
        val header = mutableListOf<String>()
        header.add("")
        playersList.forEach { player -> header.add(player.name) }
        scoreboardHeader = header.toList()

        displayTurnDirection()
    }

    /**
     * Add players score set to the scoreboard
     * @return true if scores could have been added to the scoreboard, otherwise return false
     */
    fun addScoresRound(): Boolean {
        if (!canAddScoresRound()) {
            toastMessage.value = R.string.enter_players_scores
            return false
        }
        else if (!areScoresValid()) {
            toastMessage.value = R.string.enter_valid_players_scores
            return false
        }
        else if (!onlyOneWinnerExistsForThisRound()) {
            toastMessage.value = R.string.only_one_winner_on_round
        }
        else {
            playersList.forEach { player ->
                player.stackedScore.push(calculateScore(player.nbCardsLeft.value!!.toInt()))
                scoreboard.add(player.getTotalScore().toString())
                player.nbCardsLeft.value = ""
            }

            displayTurnDirection()

            // Game is finished when we have a final ranking
            val finalRanking = getFinalRanking()
            if (finalRanking != null) {
                gameFinished.value = true
                finishAlertDialogFinalRanking.value = finalRanking
            }
        }
        return true
    }

    fun canRemovePreviousScoresRound(): Boolean {
        // We just check the first player stackedScore
        // It's always the same size for all players during the game
        return playersList[0].stackedScore.isNotEmpty()
    }

    fun removePreviousScoresRound() {
        if (canRemovePreviousScoresRound()) {
            invertedTurn = !invertedTurn
            scoreboard.removeAt(scoreboard.size - 1) // Remove the turn direction
            playersList.forEach { player ->
                // Remove score player from scoreboard for each one
                scoreboard.removeAt(scoreboard.size - 1).toInt()
                player.stackedScore.pop()
            }
            toastMessage.value = R.string.previous_scores_round_deleted
        }
    }

    private fun canAddScoresRound(): Boolean {
        val nbInvalidScores = playersList.count { player ->
            player.nbCardsLeft.value.isNullOrEmpty()
        }

        return nbInvalidScores == 0
    }

    private fun displayTurnDirection() {
        scoreboard.add(if (invertedTurn) "\u21e6" else "\u21e8")
        invertedTurn = !invertedTurn
    }

    private fun areScoresValid(): Boolean {
        val nbValidScores = playersList.count { player ->
            player.nbCardsLeft.value?.toInt() in 0..16
        }

        return nbValidScores == playersList.count()
    }

    private fun onlyOneWinnerExistsForThisRound(): Boolean {
        val nbPlayersWithNoCardsLeft = playersList.count { player ->
            player.nbCardsLeft.value?.toInt() == 0
        }

        return nbPlayersWithNoCardsLeft == 1
    }

    private fun calculateScore(nbCardsLeft: Int) : Int =
        when (nbCardsLeft) {
            in 1..7 -> nbCardsLeft
            in 8..10 -> nbCardsLeft * 2
            in 11..13 -> nbCardsLeft * 3
            14, 15 -> nbCardsLeft * 4
            16 -> 80
            else -> 0
        }

    private fun getFinalRanking() : List<Player>? {
        val finishScore = 100
        var finalRanking: List<Player>? = null

        val playerWithMaxScore = playersList.maxBy { player -> player.getTotalScore() }

        if (playerWithMaxScore != null && playerWithMaxScore.getTotalScore() >= finishScore) {
            finalRanking = playersList.sortedBy { player -> player.getTotalScore() }
        }

        return finalRanking
    }

    fun getSaveFileContent(): String {
        val content = StringBuilder()
        content.append("isFourPlayersMode|$isFourPlayersMode,")

        playersList.forEach { player ->
            content.append("name${player.index}|${player.name},")
            val stackedScoreClone: Stack<Int> = player.stackedScore.clone() as Stack<Int>
            val listedScore = stackedScoreClone.toList()
            val score = StringBuilder()
            listedScore.forEach { currentScore ->
                score.append("$currentScore#")
                score.trimEnd('#')
            }
            content.append("score${player.index}|$score,")
        }

        return content.trimEnd(',').toString()
    }
}