package rek.gofscoreboard

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import rek.gofscoreboard.databinding.ActivityScoreboardBinding
import java.io.File
import java.lang.Exception
import java.lang.StringBuilder

class ScoreboardActivity : AppCompatActivity() {
    private lateinit var binding: ActivityScoreboardBinding
    private lateinit var viewModel: ScoreboardViewModel
    private var menu: Menu? = null

    companion object PropertyNames {
        const val NB_PLAYERS = "NB_PLAYERS"
        const val PLAYER_ONE_NAME = "PLAYER1_NAME"
        const val PLAYER_TWO_NAME = "PLAYER2_NAME"
        const val PLAYER_THREE_NAME = "PLAYER3_NAME"
        const val PLAYER_FOUR_NAME = "PLAYER4_NAME"
        const val LOAD_SAVED_GAME = "LOAD_SAVED_GAME"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_scoreboard)
        setContentView(binding.root)

        viewModel = ViewModelProviders.of(this).get(ScoreboardViewModel::class.java)
        binding.scoreboardViewModel = viewModel
        binding.lifecycleOwner = this

        val loadedGame = intent.getBooleanExtra(LOAD_SAVED_GAME, false)

        if (loadedGame) initializeGameWithSave() else initializeGame()
        setPlayerFourVisibility()
        initScoreboard()
        if (loadedGame) {
            viewModel.loadScoreFromSave()
            refreshScoreboard()
        }
        setToastMessageObserver()
        setFinishAlertDialogObserver()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            this.menu = menu
            menuInflater.inflate(R.menu.activity_scoreboard_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // The good way to do it is to use itemId property
        // For an unknown reason, item?.itemId doesn't match with R.id.menu_new_game
        // I have done some research and found nothing for this problem
        // So I do the check with the title item and its localized string
        // This is absolutely horrible but I cannot do otherwise
        return when (item.title) {
            getString(R.string.new_game) -> {
                newGameActionMenu()
                return true
            }
            getString(R.string.save_game) -> {
                saveGameActionMenu()
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
        val message = if (viewModel.gameFinished.value!!)
            R.string.back_to_main_screen_game_ended_dialog_message
            else R.string.back_to_main_screen_dialog_message

        AlertDialog.Builder(this)
            .setTitle(R.string.back_to_main_screen_dialog_title)
            .setMessage(message)
            .setPositiveButton(R.string.yes) { _, _ ->
                // Return to main activity
                super.onBackPressed()
            }
            .setNegativeButton(R.string.no, null)
            .show()
    }

    fun onAddScore(view: View) {
        hideSoftKeyboard(view)
        if (viewModel.addScoresRound()) {
            refreshScoreboard()
            hideSaveGameItemMenuIfGameFinished()
        }
    }

    private fun hideSaveGameItemMenuIfGameFinished() {
        if (viewModel.gameFinished.value!!) {
            if (menu != null) {
                val itemMenu: MenuItem = menu!!.findItem(R.id.menu_save_game)
                itemMenu.isVisible = false
            }
        }
    }

    fun onRemovePreviousScore(view: View) {
        hideSoftKeyboard(view)
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
            viewModel.initializeGame(playerOneName!!, playerTwoName!!, playerThreeName!!)
        }
        else {
            val playerFourName = intent.getStringExtra(PLAYER_FOUR_NAME)
            viewModel.initializeGame(playerOneName!!, playerTwoName!!, playerThreeName!!, playerFourName!!)
        }
    }

    private fun initializeGameWithSave() {
        var saveFileContent: String = ""

        try {
            // get save file content
            val text = File(applicationContext.filesDir, SavedData.FILENAME).bufferedReader().use {
                saveFileContent = it.readText()
            }

            viewModel.initializeGameWithSave(saveFileContent)
            intent.putExtra(NB_PLAYERS, if (viewModel.isFourPlayersMode) 4 else 3)
            intent.putExtra(PLAYER_ONE_NAME, viewModel.getPlayerByIndex(0)?.name)
            intent.putExtra(PLAYER_TWO_NAME, viewModel.getPlayerByIndex(1)?.name)
            intent.putExtra(PLAYER_THREE_NAME, viewModel.getPlayerByIndex(2)?.name)
            intent.putExtra(PLAYER_FOUR_NAME, viewModel.getPlayerByIndex(3)?.name)
        }
        catch (ex: Exception) {
            Toast.makeText(this, R.string.game_saving_error_message, Toast.LENGTH_LONG).show()
        }
    }

    private fun setPlayerFourVisibility() {
        val playerFourVisibility = if (viewModel.isFourPlayersMode) View.VISIBLE else View.INVISIBLE
        binding.labelPlayerFourScore.visibility = playerFourVisibility
        binding.editPlayerFourScore.visibility = playerFourVisibility
    }

    @SuppressLint("WrongConstant")
    private fun initScoreboard() {
        binding.scoreboardHeader.setHasFixedSize(true)
        binding.scoreboardHeader.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, viewModel.getScoreboardColumnsCount(),
            androidx.recyclerview.widget.GridLayoutManager.VERTICAL, false
        )

        binding.scoreboard.setHasFixedSize(true)
        binding.scoreboard.layoutManager = androidx.recyclerview.widget.GridLayoutManager(
            this, viewModel.getScoreboardColumnsCount(),
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
                .setNegativeButton(R.string.back_main_screen_button) { _, _ ->
                    activityContext.finish()
                }
                .setPositiveButton(R.string.new_game) { _, _ ->
                    activityContext.launchNewGame()
                }
                .setNeutralButton(R.string.back_to_scoreboard, null)
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

            val pointsLabel = getString(R.string.points_label)

            var playerRank = 0
            ranking.forEach { player ->
                rankingMessage.appendln("${++playerRank}. ${player.name}")
                rankingMessage.appendln("\u27f6 ${player.getTotalScore()} $pointsLabel")
                rankingMessage.appendln()
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

    private fun saveGameActionMenu() {
        val fileContent = viewModel.getContentForSaveFile()

        try {
            applicationContext.openFileOutput(SavedData.FILENAME, Context.MODE_PRIVATE).use {
                it.write(fileContent.toByteArray())
            }

            Toast.makeText(this, R.string.game_saved_message, Toast.LENGTH_LONG).show()
        }
        catch (ex: Exception) {
            Toast.makeText(this, R.string.game_saving_error_message, Toast.LENGTH_LONG).show()
        }
    }

    private fun launchNewGame() {
        finish()
        intent.removeExtra(LOAD_SAVED_GAME)
        startActivity(intent)
    }

    private fun setScoreboardScrollPosition() {
        val scoreboardAdapter = binding.scoreboard.adapter
        if (scoreboardAdapter != null) {
            binding.scoreboard.scrollToPosition(scoreboardAdapter.itemCount - 1)
        }
    }

    private fun hideSoftKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun refreshScoreboard() {
        binding.scoreboardHeader.adapter = viewModel.getScoreboardHeaderAdapter()
        binding.scoreboard.adapter = viewModel.getScoreboardAdapter()
        setScoreboardScrollPosition()
    }
}