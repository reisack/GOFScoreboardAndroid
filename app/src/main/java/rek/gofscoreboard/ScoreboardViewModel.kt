package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
    val toastMessage = MutableLiveData<Int>()
    val finishAlertDialogFinalRanking = MutableLiveData<List<Player>>()

    var isFourPlayersMode: Boolean = true

    private var invertedTurn: Boolean = false
    private lateinit var playersList: List<Player>

    private lateinit var scoreboardHeader: List<String>
    private val scoreboard: MutableList<String> = mutableListOf()

    fun getPlayerBindingByIndex(index: Int): Player? {
        return playersList.getOrNull(index)
    }

    fun getScoreboardColumnsCount() = if (isFourPlayersMode) 5 else 4

    fun getScoreboardAdapter() = ScoreboardAdapter(scoreboard.toTypedArray())
    fun getScoreboardHeaderAdapter() = ScoreboardAdapter(scoreboardHeader.toTypedArray())

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String) {
        val playerOne = Player(playerOneName)
        val playerTwo = Player(playerTwoName)
        val playerThree = Player(playerThreeName)

        playersList = listOf(playerOne, playerTwo, playerThree)
        isFourPlayersMode = false
    }

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String
                       , playerFourName: String) {
        val playerOne = Player(playerOneName)
        val playerTwo = Player(playerTwoName)
        val playerThree = Player(playerThreeName)
        val playerFour = Player(playerFourName)

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
    fun addScoresSet(): Boolean {
        if (!canAddScoresSet()) {
            toastMessage.value = R.string.enter_players_scores
            return false
        }
        else if (!areScoresValid()) {
            toastMessage.value = R.string.enter_valid_players_scores
            return false
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
                finishAlertDialogFinalRanking.value = finalRanking
            }
        }
        return true
    }

    fun canRemovePreviousScoresSet(): Boolean {
        // We just check the first player stackedScore
        // It's always the same size for all players during the game
        return playersList[0].stackedScore.isNotEmpty()
    }

    fun removePreviousScoresSet() {
        if (canRemovePreviousScoresSet()) {
            invertedTurn = !invertedTurn
            scoreboard.removeAt(scoreboard.size - 1) // Remove the turn direction
            playersList.forEach { player ->
                // Remove score player from scoreboard for each one
                scoreboard.removeAt(scoreboard.size - 1).toInt()
                player.stackedScore.pop()
            }
        }
    }

    private fun canAddScoresSet(): Boolean {
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
}