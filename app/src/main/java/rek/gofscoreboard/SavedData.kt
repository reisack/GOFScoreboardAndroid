package rek.gofscoreboard

class SavedData {
    var isFourPlayersMode: Boolean = false
    lateinit var namePlayerOne: String
    lateinit var namePlayerTwo: String
    lateinit var namePlayerThree: String
    lateinit var namePlayerFour: String
    var scorePlayerOne: List<Int>? = null
    var scorePlayerTwo: List<Int>? = null
    var scorePlayerThree: List<Int>? = null
    var scorePlayerFour: List<Int>? = null
}