package rek.gofscoreboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun startGame(view: View) {
        val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)
        val checkedNbPlayers: RadioButton = findViewById(radioGroupNbPlayers.checkedRadioButtonId)
        val nbPlayers = Integer.parseInt(checkedNbPlayers.text.toString())

        scoreboardIntent.putExtra(ScoreboardActivity.NB_PLAYERS, nbPlayers)

        startActivity(scoreboardIntent)
    }
}
