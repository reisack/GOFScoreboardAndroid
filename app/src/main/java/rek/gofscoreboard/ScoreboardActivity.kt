package rek.gofscoreboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_scoreboard.*

class ScoreboardActivity : AppCompatActivity() {

    companion object {
        const val NB_PLAYERS = "NB_PLAYERS"
        var nbPlayerValue: Int? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scoreboard)

        nbPlayerValue = intent.getIntExtra(NB_PLAYERS, 0)
    }
}
