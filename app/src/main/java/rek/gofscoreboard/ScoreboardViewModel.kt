package rek.gofscoreboard

import android.util.Log
import androidx.lifecycle.ViewModel

class ScoreboardViewModel : ViewModel() {
    var nbPlayerValue: Int = 4
    lateinit var playerOneName: String
    lateinit var playerTwoName: String
    lateinit var playerThreeName: String
    lateinit var playerFourName: String

    private lateinit var scoresList: MutableList<String>

    fun getAdapterScoresList() = ScoresListAdapter(scoresList.toTypedArray())

    init {
        Log.i("ScoreboardViewModel", "ScoreboardViewModel created")
        scoresList = mutableListOf()
    }

    fun addScoresRound(scorePlayerOne: String, scorePlayerTwo: String,
                       scorePlayerThree: String, scorePlayerFour: String) {
        scoresList.add("=>")
        scoresList.add(scorePlayerOne)
        scoresList.add(scorePlayerTwo)
        scoresList.add(scorePlayerThree)
        scoresList.add(scorePlayerFour)
    }
}