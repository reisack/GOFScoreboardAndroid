package rek.gofscoreboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.TextView
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
        val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)
        val nbPlayers = getNbPlayers()

        scoreboardIntent.putExtra(ScoreboardActivity.NB_PLAYERS, nbPlayers)
        startActivity(scoreboardIntent)
    }

    fun onNbPlayersChanged(view: View) {
        val nbPlayers = getNbPlayers()
        val playerFourVisible = if (nbPlayers == 3) View.INVISIBLE else View.VISIBLE

        labelPlayerFourName.visibility = playerFourVisible
        editPlayerFourName.visibility = playerFourVisible
    }


}
