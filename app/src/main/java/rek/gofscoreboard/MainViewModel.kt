package rek.gofscoreboard

import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    var nbPlayers: Int

    val playerOneName = MutableLiveData<String>()
    val playerTwoName = MutableLiveData<String>()
    val playerThreeName = MutableLiveData<String>()
    val playerFourName = MutableLiveData<String>()

    val playerFourVisibility = MutableLiveData<Int>()

    init {
        nbPlayers = 4
        playerFourVisibility.value = View.VISIBLE
        clearPlayersNames()
    }

    fun updateNbPlayer(newNbPlayer : Int) {
        nbPlayers = newNbPlayer
        playerFourVisibility.value = if (nbPlayers == 3) View.INVISIBLE else View.VISIBLE
    }

    fun isEveryPlayerNameFilled(): Boolean = getPlayerNameList().all { it.isNotEmpty() }

    fun isEveryPlayerNameDifferent(): Boolean {
        val names = getPlayerNameList()

        // Check is case-insensitive
        val lowered = names.map { it.lowercase() }

        // Putting them in a Set, which removes duplicates.
        // if size is the same in the set and in original list, every player name is different
        return lowered.size == lowered.toSet().size
    }

    private fun getPlayerNameList() = listOfNotNull(
        playerOneName.value,
        playerTwoName.value,
        playerThreeName.value,
        if (nbPlayers == 4) playerFourName.value else null
    )

    fun delayedClearPlayersNames() {
        Handler(Looper.myLooper()!!).postDelayed({
            clearPlayersNames()
        }, 1000)
    }

    private fun clearPlayersNames() {
        playerOneName.value = ""
        playerTwoName.value = ""
        playerThreeName.value = ""
        playerFourName.value = ""
    }
}