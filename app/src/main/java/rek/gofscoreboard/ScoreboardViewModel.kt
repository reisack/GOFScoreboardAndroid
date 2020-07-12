package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
    val toastMessage = MutableLiveData<Int>()
    val finishAlertDialogFinalRanking = MutableLiveData<List<Player>>()

    var isFourPlayersMode: Boolean = true
    val gameFinished = MutableLiveData<Boolean>()

    private var invertedTurn: Boolean = false
    private lateinit var playersList: List<Player>

    private lateinit var scoreboardHeader: List<String>
    private val scoreboard: MutableList<String> = mutableListOf()

    private val savedData: SavedData

    init {
        gameFinished.value = false
        savedData = SavedData()
    }

    fun getPlayerByIndex(index: Int): Player? {
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

    fun initializeGameWithSave(saveFileContent: String) {
        savedData.fillSavedData(saveFileContent)

        isFourPlayersMode = savedData.isFourPlayersMode
        val playerOne = Player(1, savedData.namePlayerOne)
        val playerTwo = Player(2, savedData.namePlayerTwo)
        val playerThree = Player(3, savedData.namePlayerThree)

        playersList = if (isFourPlayersMode) {
            val playerFour = Player(4, savedData.namePlayerFour)
            listOf(playerOne, playerTwo, playerThree, playerFour)
        } else {
            listOf(playerOne, playerTwo, playerThree)
        }
    }

    fun loadScoreFromSave() {
        if (savedData.scorePlayerOne != null) {
            val count = savedData.scorePlayerOne!!.count()

            for (i in 0 until count) {
                loadPlayerScore(playersList[0], savedData.scorePlayerOne!![i])
                loadPlayerScore(playersList[1], savedData.scorePlayerTwo!![i])
                loadPlayerScore(playersList[2], savedData.scorePlayerThree!![i])

                if (isFourPlayersMode) {
                    loadPlayerScore(playersList[3], savedData.scorePlayerFour!![i])
                }

                displayTurnDirection()
            }
        }
    }

    private fun loadPlayerScore(player: Player, score: Int) {
        player.stackedScore.push(score)
        scoreboard.add(player.getTotalScore().toString())
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
        if (validateScores()) {
            playersList.forEach { player ->
                player.stackedScore.push(ScoreHelper.calculateScore(player.nbCardsLeft.value!!.toInt()))
                scoreboard.add(player.getTotalScore().toString())
                player.nbCardsLeft.value = ""
            }

            displayTurnDirection()
            checkRanking()
            return true
        }

        return false
    }

    private fun validateScores(): Boolean {
        if (!ScoreHelper.canAddScoresRound(playersList)) {
            toastMessage.value = R.string.enter_players_scores
            return false
        }

        if (!ScoreHelper.areScoresValid(playersList)) {
            toastMessage.value = R.string.enter_valid_players_scores
            return false
        }

        if (!ScoreHelper.onlyOneWinnerExistsForThisRound(playersList)) {
            toastMessage.value = R.string.only_one_winner_on_round
            return false
        }

        return true
    }

    private fun checkRanking() {
        // Game is finished when we have a final ranking
        val finalRanking = ScoreHelper.getFinalRanking(playersList)
        if (finalRanking != null) {
            gameFinished.value = true
            finishAlertDialogFinalRanking.value = finalRanking
        }
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

    private fun displayTurnDirection() {
        scoreboard.add(if (invertedTurn) "\u21e6" else "\u21e8")
        invertedTurn = !invertedTurn
    }

    fun getContentForSaveFile(): String {
        val content = StringBuilder()
        content.append("isFourPlayersMode|$isFourPlayersMode,")

        playersList.forEach { player ->
            content.append("name${player.index}|${player.name},")
            val listedScore = player.stackedScore.toList()
            val score = StringBuilder()
            listedScore.forEach { currentScore ->
                score.append("$currentScore#")
            }
            content.append("score${player.index}|${score.trimEnd('#')},")
        }

        return content.trimEnd(',').toString()
    }
}