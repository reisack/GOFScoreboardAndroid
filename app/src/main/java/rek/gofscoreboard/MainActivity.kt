package rek.gofscoreboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import rek.gofscoreboard.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this

        super.onCreate(savedInstanceState)
    }

    fun onStartGame(view: View) {
        if (viewModel.canStartGame()) {
            val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)

            scoreboardIntent.putExtra(ScoreboardActivity.NB_PLAYERS, viewModel.nbPlayers)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_ONE_NAME, viewModel.playerOneName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_TWO_NAME, viewModel.playerTwoName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_THREE_NAME, viewModel.playerThreeName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_FOUR_NAME, viewModel.playerFourName.value)

            startActivity(scoreboardIntent)
        }
        else {
            Toast.makeText(this, R.string.enter_players_names, Toast.LENGTH_LONG).show()
        }
    }
}