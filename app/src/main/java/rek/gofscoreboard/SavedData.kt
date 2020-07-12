package rek.gofscoreboard

class SavedData {

    companion object {
        const val FILENAME = "savefile.txt"
    }

    var isFourPlayersMode: Boolean = false
    lateinit var playerOneName: String
    lateinit var playerTwoName: String
    lateinit var playerThreeName: String
    lateinit var playerFourName: String
    var playerOneScore: List<Int>? = null
    var playerTwoScore: List<Int>? = null
    var playerThreeScore: List<Int>? = null
    var playerFourScore: List<Int>? = null

    fun fillSavedData(saveFileContent: String) {
        val keyValuePairs = saveFileContent.split(",")
        keyValuePairs.forEach { keyValue ->
            val splitKeyValue = keyValue.split("|")
            val key = splitKeyValue[0]
            val value = splitKeyValue[1]

            when (key) {
                "isFourPlayersMode" -> isFourPlayersMode = value.toBoolean()
                "name1" -> playerOneName = value
                "name2" -> playerTwoName = value
                "name3" -> playerThreeName = value
                "name4" -> playerFourName = value
                "score1" -> if (!value.isNullOrBlank()) playerOneScore = getScoreFromSave(value)
                "score2" -> if (!value.isNullOrBlank()) playerTwoScore = getScoreFromSave(value)
                "score3" -> if (!value.isNullOrBlank()) playerThreeScore = getScoreFromSave(value)
                "score4" -> if (!value.isNullOrBlank()) playerFourScore = getScoreFromSave(value)
            }
        }
    }

    private fun getScoreFromSave(scoreValue: String): List<Int> {
        return scoreValue.split("#").map { it.toInt() }
    }
}