package rek.gofscoreboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import rek.gofscoreboard.databinding.ActivityScoreboardBinding
import java.lang.StringBuilder

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

        initializeGame()
        setPlayerFourVisibility()
        initScoreboard()
        setToastMessageObserver()
        setFinishAlertDialogObserver()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            menuInflater.inflate(R.menu.activity_scoreboard_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        // The good way to do it is to use itemId property
        // For an unknown reason, item?.itemId doesn't match with R.id.menu_new_game
        // I have done some research and found nothing for this problem
        // So I do the check with the title item and its localized string
        // This is absolutely horrible but I cannot do otherwise
        return when (item?.title) {
            getString(R.string.new_game) -> {
                newGameActionMenu()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
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

    private fun initializeGame() {
        val nbPlayers = intent.getIntExtra(NB_PLAYERS, 3)
        val playerOneName = intent.getStringExtra(PLAYER_ONE_NAME)
        val playerTwoName = intent.getStringExtra(PLAYER_TWO_NAME)
        val playerThreeName = intent.getStringExtra(PLAYER_THREE_NAME)

        if (nbPlayers == 3) {
            viewModel.initializeGame(playerOneName, playerTwoName, playerThreeName)
        }
        else {
            val playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)
            viewModel.initializeGame(playerOneName, playerTwoName, playerThreeName, playerFourName)
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
        val scoreGridColumnsCount = if (viewModel.isFourPlayersMode) 5 else 4
        binding.scoreboard.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, scoreGridColumnsCount,
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        viewModel.createScoreboardHeader()
        refreshScoreboard()
    }

    private fun setToastMessageObserver() {
        val activityContext = this
        viewModel.toastMessage.observe(activityContext, Observer { resourceId ->
            if (resourceId != null) {
                val message = getString(resourceId)
                Toast.makeText(activityContext, message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun setFinishAlertDialogObserver() {
        val activityContext = this
        viewModel.finishAlertDialogFinalRanking.observe(activityContext, Observer { ranking ->
            AlertDialog.Builder(activityContext)
                .setCancelable(false)
                .setTitle(R.string.game_over_dialog_title)
                .setMessage(activityContext.getFinalRankingMessage(ranking))
                .setNegativeButton(R.string.button_back_main_screen) { _, _ ->
                    activityContext.finish()
                }
                .setPositiveButton(R.string.new_game) { _, _ ->
                    activityContext.launchNewGame()
                }
                .show()
        })
    }

    private fun getFinalRankingMessage(ranking: List<Player>): String {
        var winnerMessage = ""
        val rankingMessage = StringBuilder()

        if (ranking.isNotEmpty()) {
            val winner = ranking[0]
            winnerMessage = getString(
                R.string.game_over_dialog_message,
                winner.name,
                winner.getTotalScore()
            )

            var playerRank = 0
            ranking.forEach { player ->
                rankingMessage.appendln("${++playerRank}. ${player.name} ${player.getTotalScore()} pts")
            }
        }
        else {
            Toast.makeText(this, R.string.generic_error_message, Toast.LENGTH_LONG).show()
        }

        return winnerMessage + rankingMessage
    }

    private fun newGameActionMenu() {
        AlertDialog.Builder(this)
            .setTitle(R.string.new_game)
            .setMessage(R.string.new_game_message)
            .setNegativeButton(R.string.no, null)
            .setPositiveButton(R.string.yes) { _, _ ->
                launchNewGame()
            }
            .show()
    }

    private fun launchNewGame() {
        finish()
        startActivity(intent)
    }

    private fun refreshScoreboard() {
        binding.scoreboard.adapter = viewModel.getScoreboardAdapter()
    }
}