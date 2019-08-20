package rek.gofscoreboard

import android.os.Handler
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

    fun canStartGame(): Boolean {
        return !playerOneName.value.isNullOrEmpty()
                && !playerTwoName.value.isNullOrEmpty()
                && !playerThreeName.value.isNullOrEmpty()
                && (nbPlayers == 3 || !playerFourName.value.isNullOrEmpty())
    }

    fun delayedClearPlayersNames() {
        Handler().postDelayed({
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