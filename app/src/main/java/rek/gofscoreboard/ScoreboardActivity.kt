package rek.gofscoreboard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import rek.gofscoreboard.databinding.ActivityScoreboardBinding

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreboardBinding
    private lateinit var viewModel: ScoreboardViewModel

    companion object PropertyNames {
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

        initPlayersInformations()
        setPlayerFourVisibility()
        initScoreboard()
        setToastMessageObserver()

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

    private fun initPlayersInformations() {
        viewModel.nbPlayers = intent.getIntExtra(NB_PLAYERS, 0)
        viewModel.playerOneName = intent.getStringExtra(PLAYER_ONE_NAME)
        viewModel.playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME)
        viewModel.playerThreeName = intent.getStringExtra(PLAYER_THREE_NAME)
        viewModel.playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)
    }

    private fun setPlayerFourVisibility() {
        val playerFourVisibility = if (viewModel.nbPlayers == 4) View.VISIBLE else View.INVISIBLE
        binding.labelPlayerFourScore.visibility = playerFourVisibility
        binding.editPlayerFourScore.visibility = playerFourVisibility
    }

    private fun initScoreboard() {
        binding.scoreboard.setHasFixedSize(true)
        val scoreGridColumnsCount = if (viewModel.nbPlayers == 3) 4 else 5
        binding.scoreboard.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, scoreGridColumnsCount,
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        viewModel.createScoreboardHeader()
        refreshScoreboard()
    }

    private fun setToastMessageObserver() {
        val context = this
        viewModel.toastMessage.observe(this, Observer { resourceId ->
            if (resourceId != null) {
                val message = getString(resourceId)
                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun refreshScoreboard() {
        binding.scoreboard.adapter = viewModel.getAdapterScoresList()
    }
}
