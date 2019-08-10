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

            scoresList.add(if (invertedTurn) "<=" else "=>")
            scoresList.add(playerOneScoreText.value.toString())
            scoresList.add(playerTwoScoreText.value.toString())
            scoresList.add(playerThreeScoreText.value.toString())
            scoresList.add(if (nbPlayer == 4) playerFourScoreText.value.toString() else "")

            playerOneTotalScore += playerOneScoreText.value!!.toInt()
            playerTwoTotalScore += playerTwoScoreText.value!!.toInt()
            playerThreeTotalScore += playerThreeScoreText.value!!.toInt()
            playerFourTotalScore += playerFourScoreText.value!!.toInt()

            playerOneScoreText.value = ""
            playerTwoScoreText.value = ""
            playerThreeScoreText.value = ""
            playerFourScoreText.value = ""
        }

    }

    fun removePreviousScoresRound() {
        if (scoresList.size >= 5) {
            invertedTurn = !invertedTurn

            playerFourTotalScore -= scoresList.removeAt(scoresList.size - 1).toInt()
            playerThreeTotalScore -= scoresList.removeAt(scoresList.size - 1).toInt()
            playerTwoTotalScore -= scoresList.removeAt(scoresList.size - 1).toInt()
            playerOneTotalScore -= scoresList.removeAt(scoresList.size - 1).toInt()

            // Remove the turn direction
            scoresList.removeAt(scoresList.size - 1)
        }
    }

    fun canAddScoresRound(): Boolean {
        return !playerOneScoreText.value.isNullOrEmpty()
                && !playerTwoScoreText.value.isNullOrEmpty()
                && !playerThreeScoreText.value.isNullOrEmpty()
                && (nbPlayer == 3 || !playerFourScoreText.value.isNullOrEmpty())
    }
}