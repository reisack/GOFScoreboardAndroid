package rek.gofscoreboard

import androidx.lifecycle.MutableLiveData
import java.util.Stack

class Player(val name: String) {
    val stackedScore = Stack<Int>()
    val nbCardsLeft = MutableLiveData<String>()

    fun getTotalScore(): Int = stackedScore.sum()
}