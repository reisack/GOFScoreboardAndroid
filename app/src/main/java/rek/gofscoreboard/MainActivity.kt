package rek.gofscoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun getNbPlayers(): Int {
        val checkedNbPlayers: RadioButton = findViewById(radioGroupNbPlayers.checkedRadioButtonId)
        return Integer.parseInt(checkedNbPlayers.text.toString())
    }

    fun onStartGame(view: View) {

        fun getPlayerName(editTextPlayerName: EditText, index: Int): String {
            return if (editTextPlayerName.text.toString() == "") "P$index"
            else editTextPlayerName.text.toString()
        }

        val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)

        val nbPlayers = getNbPlayers()
        val playerOneName = getPlayerName(editPlayerOneName, 1)
        val playerTwoName = getPlayerName(editPlayerTwoName, 2)
        val playerThreeName = getPlayerName(editPlayerThreeName, 3)
        val playerFourName = getPlayerName(editPlayerFourName, 4)

        scoreboardIntent.putExtra(ScoreboardActivity.NB_PLAYERS, nbPlayers)
        scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_ONE_NAME, playerOneName)
        scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_TWO_NAME, playerTwoName)
        scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_THREE_NAME, playerThreeName)
        scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_FOUR_NAME, playerFourName)

        startActivity(scoreboardIntent)
    }

    fun onNbPlayersChanged(view: View) {
        val nbPlayers = getNbPlayers()
        val playerFourVisible = if (nbPlayers == 3) View.INVISIBLE else View.VISIBLE

        labelPlayerFourName.visibility = playerFourVisible
        editPlayerFourName.visibility = playerFourVisible
    }

}
