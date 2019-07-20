package rek.gofscoreboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.View
import kotlinx.android.synthetic.main.activity_scoreboard.*

class ScoreboardActivity : AppCompatActivity() {

    companion object {
        const val NB_PLAYERS = "NB_PLAYERS"
        const val PLAYER_ONE_NAME = "PLAYER1_NAME"
        const val PLAYER_TWO_NAME = "PLAYER2_NAME"
        const val PLAYER_THREE_NAME = "PLAYER3_NAME"
        const val PLAYER_FOUR_NAME = "PLAYER4_NAME"

        var nbPlayerValue: Int? = null
        lateinit var playerOneName: String
        lateinit var playerTwoName: String
        lateinit var playerThreeName: String
        lateinit var playerFourName: String

        val scoresList: MutableList<String> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        fun setValuesFromPreviousActivity() {
            nbPlayerValue = intent.getIntExtra(NB_PLAYERS, 0)
            playerOneName = intent.getStringExtra(PLAYER_ONE_NAME)
            playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME)
            playerThreeName = intent.getStringExtra(PLAYER_THREE_NAME)
            playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        setValuesFromPreviousActivity()

        listScores.setHasFixedSize(true)

        val scoreGridColumnsCount = if (nbPlayerValue == 3) 4 else 5
        listScores.layoutManager = GridLayoutManager(this, scoreGridColumnsCount,
            GridLayoutManager.VERTICAL, false)
    }

    fun onAddScore(view: View) {
        scoresList.add("Test1")
        scoresList.add("Test2")
        scoresList.add("Test3")
        scoresList.add("Test4")
        scoresList.add("Test5")
        val adapterScore = ScoresListAdapter(scoresList.toTypedArray())
        listScores.adapter = adapterScore
    }
}
