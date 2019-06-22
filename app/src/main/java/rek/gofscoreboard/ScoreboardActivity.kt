package rek.gofscoreboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        nbPlayerValue = intent.getIntExtra(NB_PLAYERS, 0)
        playerOneName = intent.getStringExtra(PLAYER_ONE_NAME)
        playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME)
        playerThreeName = intent.getStringExtra(PLAYER_THREE_NAME)
        playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)
    }
}
