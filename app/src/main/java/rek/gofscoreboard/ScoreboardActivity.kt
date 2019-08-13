package rek.gofscoreboard

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
        setFinishAlertDialogObserver()

        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle(R.string.back_to_main_screen_dialog_title)
            .setMessage(R.string.back_to_main_screen_dialog_message)
            .setPositiveButton(R.string.yes) { _, _ ->
                // Return to main activity
                super.onBackPressed()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun onAddScore(view: View) {
        viewModel.addScoresRound()
        refreshScoreboard()
    }

    fun onRemovePreviousScore(view: View) {
        if (viewModel.canRemovePreviousScoresRound()) {
            AlertDialog.Builder(this)
                .setTitle(R.string.delete_score_dialog_title)
                .setMessage(R.string.delete_score_dialog_message)
                .setPositiveButton(R.string.yes) { _, _ ->
                    viewModel.removePreviousScoresRound()
                    refreshScoreboard()
                }
                .setNegativeButton(R.string.no, null)
                .show()
        }
        else {
            Toast.makeText(this, R.string.no_score_to_delete, Toast.LENGTH_LONG).show()
        }
    }

    private fun initPlayersInformations() {
        viewModel.nbPlayers = intent.getIntExtra(NB_PLAYERS, 0)
        viewModel.isFourPlayersMode = viewModel.nbPlayers == 4

        viewModel.playerOne = Player(intent.getStringExtra(PLAYER_ONE_NAME))
        viewModel.playerTwo = Player(intent.getStringExtra(PLAYER_TWO_NAME))
        viewModel.playerThree = Player(intent.getStringExtra(PLAYER_THREE_NAME))
        if (viewModel.isFourPlayersMode) {
            viewModel.playerFour = Player(intent.getStringExtra(PLAYER_FOUR_NAME))
        }
    }

    private fun setPlayerFourVisibility() {
        val playerFourVisibility = if (viewModel.isFourPlayersMode) View.VISIBLE else View.INVISIBLE
        binding.labelPlayerFourScore.visibility = playerFourVisibility
        binding.editPlayerFourScore.visibility = playerFourVisibility
    }

    @SuppressLint("WrongConstant")
    private fun initScoreboard() {
        binding.scoreboard.setHasFixedSize(true)
        val scoreGridColumnsCount = if (!viewModel.isFourPlayersMode) 4 else 5
        binding.scoreboard.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, scoreGridColumnsCount,
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        viewModel.createScoreboardHeader()
        refreshScoreboard()
    }

    private fun setToastMessageObserver() {
        val activityContext = this
        viewModel.toastMessage.observe(this, Observer { resourceId ->
            if (resourceId != null) {
                val message = getString(resourceId)
                Toast.makeText(activityContext, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setFinishAlertDialogObserver() {
        val activityContext = this
        viewModel.finishAlertDialogFinalRanking.observe(this, Observer { ranking ->
            AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(R.string.game_over_dialog_title)
                .setMessage(activityContext.getFinalRankingMessage(ranking))
                .setNegativeButton(R.string.button_back_main_screen) { _, _ ->
                    activityContext.finish()
                }
                .setPositiveButton(R.string.button_new_game) { _, _ ->
                    activityContext.finish()
                    activityContext.startActivity(activityContext.intent)
                }
                .show()
        })
    }

    private fun getFinalRankingMessage(ranking: List<Player?>): String {
        val winner = ranking[0]
        val winnerMessage = getString(
            R.string.game_over_dialog_message,
            winner!!.name,
            winner.getTotalScore()
        )

        var rankingMessage = ""
        var playerRank = 0
        ranking.forEach { player ->
            rankingMessage += (++playerRank).toString() + ". " + player!!.name + " " + player.getTotalScore() + "\n"
        }

        return winnerMessage + rankingMessage
    }

    private fun refreshScoreboard() {
        binding.scoreboard.adapter = viewModel.getAdapterScoresList()
    }
}
