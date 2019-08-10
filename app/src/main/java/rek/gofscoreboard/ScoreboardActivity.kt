package rek.gofscoreboard

import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_scoreboard.*
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

        binding.listScores.setHasFixedSize(true)
        val scoreGridColumnsCount = if (viewModel.nbPlayer == 3) 4 else 5
        binding.listScores.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, scoreGridColumnsCount,
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        super.onCreate(savedInstanceState)
    }

    fun onAddScore(view: View) {
        // TESTS
        //viewModel.addScoresRound("10", "5", "0", "100")

        viewModel.addScoresRound()
        binding.listScores.adapter = viewModel.getAdapterScoresList()
    }

    fun onRemovePreviousScore(view: View) {
        viewModel.removePreviousScoresRound()
        binding.listScores.adapter = viewModel.getAdapterScoresList()
    }
}
