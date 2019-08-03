package rek.gofscoreboard

import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val nbPlayer = MutableLiveData<Int>()
    val playerFourVisibility = MutableLiveData<Int>()

    val playerOneName = MutableLiveData<String>()
    val playerTwoName = MutableLiveData<String>()
    val playerThreeName = MutableLiveData<String>()
    val playerFourName = MutableLiveData<String>()

    init {
        nbPlayer.value = 4
        playerFourVisibility.value = View.VISIBLE

        playerOneName.value = ""
        playerTwoName.value = ""
        playerThreeName.value = ""
        playerFourName.value = ""
    }

    fun updateNbPlayer(newNbPlayer : Int) {
        nbPlayer.value = newNbPlayer
        playerFourVisibility.value = if (nbPlayer.value == 3) View.INVISIBLE else View.VISIBLE
    }
}