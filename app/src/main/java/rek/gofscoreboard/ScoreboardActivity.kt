package rek.gofscoreboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import rek.gofscoreboard.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreboardBinding
    private lateinit var viewModel: ScoreboardViewModel

    companion object PropertiesNames {
        const val NB_PLAYERS = "NB_PLAYERS"
        const val PLAYER_ONE_NAME = "PLAYER1_NAME"
        const val PLAYER_TWO_NAME = "PLAYER2_NAME"
        const val PLAYER_THREE_NAME = "PLAYER3_NAME"
        const val PLAYER_FOUR_NAME = "PLAYER4_NAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this).get(ScoreboardViewModel::class.java)
        binding.scoreboardViewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.nbPlayer = intent.getIntExtra(NB_PLAYERS, 0)
        viewModel.playerOneName = intent.getStringExtra(PLAYER_ONE_NAME)
        viewModel.playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME)
        viewModel.playerThreeName = intent.getStringExtra(PLAYER_THREE_NAME)
        viewModel.playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)

        val playerFourVisibility = if (viewModel.nbPlayer == 4) View.VISIBLE else View.INVISIBLE
        binding.labelPlayerFourScore.visibility = playerFourVisibility
        binding.editPlayerFourScore.visibility = playerFourVisibility

        binding.scoreboard.setHasFixedSize(true)
        val scoreGridColumnsCount = if (viewModel.nbPlayer == 3) 4 else 5
        binding.scoreboard.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, scoreGridColumnsCount,
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        viewModel.createScoreboardHeader()
        refreshScoreboard()

        super.onCreate(savedInstanceState)
    }

    fun onAddScore(view: View) {
        viewModel.addScoresRound()
        refreshScoreboard()
    }

    fun onRemovePreviousScore(view: View) {
        viewModel.removePreviousScoresRound()
        refreshScoreboard()
    }

    private fun refreshScoreboard() {
        binding.scoreboard.adapter = viewModel.getAdapterScoresList()
    }
}
