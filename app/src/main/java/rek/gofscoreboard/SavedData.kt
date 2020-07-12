package rek.gofscoreboard

class SavedData {

    companion object {
        const val FILENAME = "savefile.txt"
    }

    var isFourPlayersMode: Boolean = false
    lateinit var namePlayerOne: String
    lateinit var namePlayerTwo: String
    lateinit var namePlayerThree: String
    lateinit var namePlayerFour: String
    var scorePlayerOne: List<Int>? = null
    var scorePlayerTwo: List<Int>? = null
    var scorePlayerThree: List<Int>? = null
    var scorePlayerFour: List<Int>? = null

    fun fillSavedData(saveFileContent: String) {
        val keyValuePairs = saveFileContent.split(",")
        keyValuePairs.forEach { keyValue ->
            val splitKeyValue = keyValue.split("|")
            val key = splitKeyValue[0]
            val value = splitKeyValue[1]

            when (key) {
                "isFourPlayersMode" -> isFourPlayersMode = value.toBoolean()
                "name1" -> namePlayerOne = value
                "name2" -> namePlayerTwo = value
                "name3" -> namePlayerThree = value
                "name4" -> namePlayerFour = value
                "score1" -> if (!value.isNullOrBlank()) scorePlayerOne = getScoreFromSave(value)
                "score2" -> if (!value.isNullOrBlank()) scorePlayerTwo = getScoreFromSave(value)
                "score3" -> if (!value.isNullOrBlank()) scorePlayerThree = getScoreFromSave(value)
                "score4" -> if (!value.isNullOrBlank()) scorePlayerFour = getScoreFromSave(value)
            }
        }
    }

    private fun getScoreFromSave(scoreValue: String): List<Int> {
        return scoreValue.split("#").map { it.toInt() }
    }
}