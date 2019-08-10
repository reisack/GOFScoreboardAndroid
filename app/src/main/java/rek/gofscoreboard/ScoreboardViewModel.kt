package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
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
    private val scoresList: MutableList<String> = mutableListOf()

    fun getAdapterScoresList() = ScoresListAdapter(scoresList.toTypedArray())

    fun addScoresRound() {
        if (canAddScoresRound()) {
            invertedTurn = !invertedTurn

            playerOneTotalScore += calculateScore(playerOneScoreText.value!!.toInt())
            playerTwoTotalScore += calculateScore(playerTwoScoreText.value!!.toInt())
            playerThreeTotalScore += calculateScore(playerThreeScoreText.value!!.toInt())
            if (nbPlayer == 4) {
                playerFourTotalScore += calculateScore(playerFourScoreText.value!!.toInt())
            }

            scoresList.add(if (invertedTurn) "<=" else "=>")
            scoresList.add(playerOneTotalScore.toString())
            scoresList.add(playerTwoTotalScore.toString())
            scoresList.add(playerThreeTotalScore.toString())
            if (nbPlayer == 4) {
                scoresList.add(playerOneTotalScore.toString())
            }

            playerOneScoreText.value = ""
            playerTwoScoreText.value = ""
            playerThreeScoreText.value = ""
            playerFourScoreText.value = ""
        }

    }

    fun removePreviousScoresRound() {
        if (scoresList.size >= 5) {
            invertedTurn = !invertedTurn

            if (nbPlayer == 4) {
                scoresList.removeAt(scoresList.size - 1).toInt() // Remove Score player 4
            }

            scoresList.removeAt(scoresList.size - 1).toInt() // Remove Score player 3
            scoresList.removeAt(scoresList.size - 1).toInt() // Remove Score player 2
            scoresList.removeAt(scoresList.size - 1).toInt() // Remove Score player 1
            scoresList.removeAt(scoresList.size - 1) // Remove the turn direction

            if (scoresList.size >= 5) {
                if (nbPlayer == 4) {
                    playerFourTotalScore = scoresList[scoresList.size - 1].toInt()
                    playerThreeTotalScore = scoresList[scoresList.size - 2].toInt()
                    playerTwoTotalScore = scoresList[scoresList.size - 3].toInt()
                    playerOneTotalScore = scoresList[scoresList.size - 4].toInt()
                }
                else {
                    playerThreeTotalScore = scoresList[scoresList.size - 1].toInt()
                    playerTwoTotalScore = scoresList[scoresList.size - 2].toInt()
                    playerOneTotalScore = scoresList[scoresList.size - 3].toInt()
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

    private fun canAddScoresRound(): Boolean {
        return !playerOneScoreText.value.isNullOrEmpty()
                && !playerTwoScoreText.value.isNullOrEmpty()
                && !playerThreeScoreText.value.isNullOrEmpty()
                && (nbPlayer == 3 || !playerFourScoreText.value.isNullOrEmpty())
    }

    private fun calculateScore(nbCardsLeft: Int) : Int {
        return when (nbCardsLeft) {
            in 1..7 -> nbCardsLeft
            in 8..10 -> nbCardsLeft * 2
            in 11..13 -> nbCardsLeft * 3
            14, 15 -> nbCardsLeft * 4
            16 -> 80
            else -> 0
        }
    }
}