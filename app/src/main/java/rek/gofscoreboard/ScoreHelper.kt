package rek.gofscoreboard

class ScoreHelper {
    companion object {
        fun calculateScore(nbCardsLeft: Int) : Int =
            when (nbCardsLeft) {
                in 1..7 -> nbCardsLeft
                in 8..10 -> nbCardsLeft * 2
                in 11..13 -> nbCardsLeft * 3
                14, 15 -> nbCardsLeft * 4
                16 -> 80
                else -> 0
            }

        fun getFinalRanking(playersList: List<Player>) : List<Player>? {
            val finishScore = 100
            var finalRanking: List<Player>? = null

            val playerWithMaxScore = playersList.maxBy { player -> player.getTotalScore() }

            if (playerWithMaxScore != null && playerWithMaxScore.getTotalScore() >= finishScore) {
                finalRanking = playersList.sortedBy { player -> player.getTotalScore() }
            }

            return finalRanking
        }

        fun areScoresValid(playersList: List<Player>): Boolean {
            val nbValidScores = playersList.count { player ->
                player.nbCardsLeft.value?.toInt() in 0..16
            }

            return nbValidScores == playersList.count()
        }

        fun onlyOneWinnerExistsForThisRound(playersList: List<Player>): Boolean {
            val nbPlayersWithNoCardsLeft = playersList.count { player ->
                player.nbCardsLeft.value?.toInt() == 0
            }

            return nbPlayersWithNoCardsLeft == 1
        }

        fun canAddScoresRound(playersList: List<Player>): Boolean {
            val nbInvalidScores = playersList.count { player ->
                player.nbCardsLeft.value.isNullOrEmpty()
            }

            return nbInvalidScores == 0
        }
    }
}