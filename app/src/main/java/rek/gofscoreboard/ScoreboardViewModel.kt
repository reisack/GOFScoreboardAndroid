package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
    val toastMessage = MutableLiveData<Int>()

    var nbPlayer: Int = 4
    lateinit var playerOneName: String
    lateinit var playerTwoName: String
    lateinit var playerThreeName: String
    lateinit var playerFourName: String

    val playerOneScoreText = MutableLiveData<String>()
    val playerTwoScoreText = MutableLiveData<String>()
    val playerThreeScoreText = MutableLiveData<String>()
    val playerFourScoreText = MutableLiveData<String>()

    private var playerOneTotalScore: Int = 0
    private var playerTwoTotalScore: Int = 0
    private var playerThreeTotalScore: Int = 0
    private var playerFourTotalScore: Int = 0

    private var invertedTurn: Boolean = true
    private val scoreboard: MutableList<String> = mutableListOf()

    fun getAdapterScoresList() = ScoresListAdapter(scoreboard.toTypedArray())

    fun createScoreboardHeader() {
        scoreboard.add("")
        scoreboard.add(playerOneName)
        scoreboard.add(playerTwoName)
        scoreboard.add(playerThreeName)
        if (nbPlayer == 4) {
            scoreboard.add(playerFourName)
        }
    }

    fun addScoresRound() {
        invertedTurn = !invertedTurn

        playerOneTotalScore += calculateScore(playerOneScoreText.value!!.toInt())
        playerTwoTotalScore += calculateScore(playerTwoScoreText.value!!.toInt())
        playerThreeTotalScore += calculateScore(playerThreeScoreText.value!!.toInt())
        if (nbPlayer == 4) {
            playerFourTotalScore += calculateScore(playerFourScoreText.value!!.toInt())
        }

        scoreboard.add(if (invertedTurn) "<=" else "=>")
        scoreboard.add(playerOneTotalScore.toString())
        scoreboard.add(playerTwoTotalScore.toString())
        scoreboard.add(playerThreeTotalScore.toString())
        if (nbPlayer == 4) {
            scoreboard.add(playerFourTotalScore.toString())
        }

        playerOneScoreText.value = ""
        playerTwoScoreText.value = ""
        playerThreeScoreText.value = ""
        playerFourScoreText.value = ""
    }

    fun removePreviousScoresRound() {
        val minimumScoreboardSize = if (nbPlayer == 4) 10 else 8
        if (scoreboard.size >= minimumScoreboardSize) {
            invertedTurn = !invertedTurn

            if (nbPlayer == 4) {
                scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 4
            }

            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 3
            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 2
            scoreboard.removeAt(scoreboard.size - 1).toInt() // Remove Score player 1
            scoreboard.removeAt(scoreboard.size - 1) // Remove the turn direction

            if (scoreboard.size >= minimumScoreboardSize) {
                if (nbPlayer == 4) {
                    playerFourTotalScore = scoreboard[scoreboard.size - 1].toInt()
                    playerThreeTotalScore = scoreboard[scoreboard.size - 2].toInt()
                    playerTwoTotalScore = scoreboard[scoreboard.size - 3].toInt()
                    playerOneTotalScore = scoreboard[scoreboard.size - 4].toInt()
                }
                else {
                    playerThreeTotalScore = scoreboard[scoreboard.size - 1].toInt()
                    playerTwoTotalScore = scoreboard[scoreboard.size - 2].toInt()
                    playerOneTotalScore = scoreboard[scoreboard.size - 3].toInt()
                }
            }
            else {
                playerOneTotalScore = 0
                playerTwoTotalScore = 0
                playerThreeTotalScore = 0
                playerFourTotalScore = 0
            }
        }
    }

    fun canAddScoresRound(): Boolean =
        !playerOneScoreText.value.isNullOrEmpty()
        && !playerTwoScoreText.value.isNullOrEmpty()
        && !playerThreeScoreText.value.isNullOrEmpty()
        && (nbPlayer == 3 || !playerFourScoreText.value.isNullOrEmpty())

    fun areScoresValid(): Boolean =
        playerOneScoreText.value!!.toInt() in 0..16
        && playerTwoScoreText.value!!.toInt() in 0..16
        && playerThreeScoreText.value!!.toInt() in 0..16
        && (nbPlayer == 3 || playerFourScoreText.value!!.toInt() in 0..16)

    private fun calculateScore(nbCardsLeft: Int) : Int =
        when (nbCardsLeft) {
            in 1..7 -> nbCardsLeft
            in 8..10 -> nbCardsLeft * 2
            in 11..13 -> nbCardsLeft * 3
            14, 15 -> nbCardsLeft * 4
            16 -> 80
            else -> 0
        }
}