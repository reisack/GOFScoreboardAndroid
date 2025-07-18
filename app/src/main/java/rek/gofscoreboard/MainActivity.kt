package rek.gofscoreboard

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import rek.gofscoreboard.databinding.ActivityMainBinding
import java.io.File
import androidx.core.net.toUri

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding.mainViewModel = viewModel
        binding.lifecycleOwner = this

        super.onCreate(savedInstanceState)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            menuInflater.inflate(R.menu.activity_main_menu, menu)
        }

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.title) {
            getString(R.string.load_saved_game) -> {
                startSavedGameActionMenu()
                return true
            }
            getString(R.string.privacy) -> {
                val privacyUrl = "https://reisack.github.io/GOF/privacy.html"
                val browserIntent = Intent(Intent.ACTION_VIEW, privacyUrl.toUri())
                startActivity(browserIntent)
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onStartGame(@Suppress("UNUSED_PARAMETER") view: View) {
        if (!viewModel.isEveryPlayerNameFilled()) {
            Toast.makeText(this, R.string.enter_players_names, Toast.LENGTH_LONG).show()
        }
        else if (!viewModel.isEveryPlayerNameDifferent()) {
            Toast.makeText(this, R.string.duplicate_players_names, Toast.LENGTH_LONG).show()
        }
        else {
            val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)

            scoreboardIntent.putExtra(ScoreboardActivity.NB_PLAYERS, viewModel.nbPlayers)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_ONE_NAME, viewModel.playerOneName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_TWO_NAME, viewModel.playerTwoName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_THREE_NAME, viewModel.playerThreeName.value)
            scoreboardIntent.putExtra(ScoreboardActivity.PLAYER_FOUR_NAME, viewModel.playerFourName.value)

            startActivity(scoreboardIntent)

            // Players names are cleared 1 second later in order to avoid
            // empty input fields when the user start a new game
            // (This is only an UI issue)
            viewModel.delayedClearPlayersNames()
        }
    }

    private fun startSavedGameActionMenu() {
        try {
            val isSaveFileExists = File(applicationContext.filesDir, SavedData.FILENAME).exists()

            if (isSaveFileExists) {
                val scoreboardIntent = Intent(this, ScoreboardActivity::class.java)
                scoreboardIntent.putExtra(ScoreboardActivity.LOAD_SAVED_GAME, true)
                startActivity(scoreboardIntent)
                viewModel.delayedClearPlayersNames()
            }
            else {
                Toast.makeText(this, R.string.no_saved_game_message, Toast.LENGTH_LONG).show()
            }
        }
        catch (ex: Exception) {
            Toast.makeText(this, R.string.no_saved_game_message, Toast.LENGTH_LONG).show()
        }
    }
}