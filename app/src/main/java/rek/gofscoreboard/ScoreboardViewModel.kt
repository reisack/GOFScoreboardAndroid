package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
    val toastMessage = MutableLiveData<Int>()
    val finishAlertDialogFinalRanking = MutableLiveData<List<Player?>>()

    lateinit var playerOne: Player
    lateinit var playerTwo: Player
    lateinit var playerThree: Player
    var playerFour: Player? = null

    var isFourPlayersMode: Boolean = true
    private var invertedTurn: Boolean = true

    private val scoreboard: MutableList<String> = mutableListOf()

    fun getAdapterScoresList() = ScoresListAdapter(scoreboard.toTypedArray())

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String) {
        playerOne = Player(playerOneName)
        playerTwo = Player(playerTwoName)
        playerThree = Player(playerThreeName)
        isFourPlayersMode = false
    }

    fun initializeGame(playerOneName: String
                       , playerTwoName: String, playerThreeName: String
                       , playerFourName: String) {
        initializeGame(playerOneName, playerTwoName, playerThreeName)
        playerFour = Player(playerFourName)
        isFourPlayersMode = true
    }

    fun createScoreboardHeader() {
        scoreboard.add("")
        scoreboard.add(playerOne.name)
        scoreboard.add(playerTwo.name)
        scoreboard.add(playerThree.name)
        scoreboard.addIfNotNull(playerFour?.name)
    }

    fun addScoresRound() {
        if (!canAddScoresRound()) {
            toastMessage.value = R.string.enter_players_scores
        }
        else if (!areScoresValid()) {
            toastMessage.value = R.string.enter_valid_players_scores
        }
        else {
            invertedTurn = !invertedTurn

            playerOne.stackedScore.push(calculateScore(playerOne.nbCardsLeft.value!!.toInt()))
            playerTwo.stackedScore.push(calculateScore(playerTwo.nbCardsLeft.value!!.toInt()))
            playerThree.stackedScore.push(calculateScore(playerThree.nbCardsLeft.value!!.toInt()))
            playerFour?.stackedScore?.push(calculateScore(playerFour?.nbCardsLeft?.value!!.toInt()))

            scoreboard.add(if (invertedTurn) "<=" else "=>")
            scoreboard.add(playerOne.getTotalScore().toString())
            scoreboard.add(playerTwo.getTotalScore().toString())
            scoreboard.add(playerThree.getTotalScore().toString())
            scoreboard.addIfNotNull(playerFour?.getTotalScore()?.toString())

            playerOne.nbCardsLeft.value = ""
            playerTwo.nbCardsLeft.value = ""
            playerThree.nbCardsLeft.value = ""
            playerFour?.nbCardsLeft?.value = ""

            // Game is finished when we have a final ranking
            val finalRanking = getFinalRanking()
            if (finalRanking != null) {
                finishAlertDialogFinalRanking.value = finalRanking
            }
        }
    }

    fun canRemovePreviousScoresRound(): Boolean {
        // We just check the first player stackedScore
        // It's always the same size for all players during the game
        return playerOne.stackedScore.isNotEmpty()
    }

    fun removePreviousScoresRound() {
        if (canRemovePreviousScoresRound()) {
            invertedTurn = !invertedTurn

            if (isFourPlayersMode) {
                scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 4
            }
            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 3
            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 2
            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 1
            scoreboard.removeAt(scoreboard.size - 1) // Remove the turn direction

            playerOne.stackedScore.pop()
            playerTwo.stackedScore.pop()
            playerThree.stackedScore.pop()
            playerFour?.stackedScore?.pop()
        }
    }

    private fun canAddScoresRound(): Boolean =
        !playerOne.nbCardsLeft.value.isNullOrEmpty()
        && !playerTwo.nbCardsLeft.value.isNullOrEmpty()
        && !playerThree.nbCardsLeft.value.isNullOrEmpty()
        && (!isFourPlayersMode || !playerFour!!.nbCardsLeft.value.isNullOrEmpty())

    private fun areScoresValid(): Boolean =
        playerOne.nbCardsLeft.value!!.toInt() in 0..16
        && playerTwo.nbCardsLeft.value!!.toInt() in 0..16
        && playerThree.nbCardsLeft.value!!.toInt() in 0..16
        && (!isFourPlayersMode || playerFour!!.nbCardsLeft.value!!.toInt() in 0..16)

    private fun calculateScore(nbCardsLeft: Int) : Int =
        when (nbCardsLeft) {
            in 1..7 -> nbCardsLeft
            in 8..10 -> nbCardsLeft * 2
            in 11..13 -> nbCardsLeft * 3
            14, 15 -> nbCardsLeft * 4
            16 -> 80
            else -> 0
        }

    private fun getFinalRanking() : List<Player?>? {
        val finishScore = 100
        var finalRanking: List<Player?>? = null

        val players = if (isFourPlayersMode) {
            listOf(playerOne, playerTwo, playerThree, playerFour)
        }
        else {
            listOf(playerOne, playerTwo, playerThree)
        }

        val playerWithMaxScore = players.maxBy { player -> player!!.getTotalScore() }

        if (playerWithMaxScore != null && playerWithMaxScore.getTotalScore() >= finishScore) {
            finalRanking = players.sortedBy { player -> player!!.getTotalScore() }
        }

        return finalRanking
    }
}