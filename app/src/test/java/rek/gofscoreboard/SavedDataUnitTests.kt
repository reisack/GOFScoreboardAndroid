package rek.gofscoreboard

import org.junit.Assert.*
import org.junit.Test

class SavedDataUnitTests {
    @Test
    fun fillSavedData_4_players_with_scores() {
        // Arrange
        val saveFileContent = "isFourPlayersMode|true,name1|Stan,score1|4#0#6,name2|Kyle,score2|6#2#5,name3|Kenny,score3|16#3#0,name4|Clyde,score4|0#7#1"

        // Act
        val savedData = SavedData()
        savedData.fillSavedData(saveFileContent)

        // Assert
        assertTrue(savedData.isFourPlayersMode)
        assertEquals("Stan", savedData.playerOneName)
        assertEquals("Kyle", savedData.playerTwoName)
        assertEquals("Kenny", savedData.playerThreeName)
        assertEquals("Clyde", savedData.playerFourName)

        assertEquals(4, savedData.playerOneScore!![0])
        assertEquals(0, savedData.playerOneScore!![1])
        assertEquals(6, savedData.playerOneScore!![2])

        assertEquals(6, savedData.playerTwoScore!![0])
        assertEquals(2, savedData.playerTwoScore!![1])
        assertEquals(5, savedData.playerTwoScore!![2])

        assertEquals(16, savedData.playerThreeScore!![0])
        assertEquals(3, savedData.playerThreeScore!![1])
        assertEquals(0, savedData.playerThreeScore!![2])

        assertEquals(0, savedData.playerFourScore!![0])
        assertEquals(7, savedData.playerFourScore!![1])
        assertEquals(1, savedData.playerFourScore!![2])
    }

    @Test
    fun fillSavedData_4_players_without_scores() {
        // Arrange
        val saveFileContent = "isFourPlayersMode|true,name1|Stan,score1|,name2|Kyle,score2|,name3|Kenny,score3|,name4|Clyde,score4|"

        // Act
        val savedData = SavedData()
        savedData.fillSavedData(saveFileContent)

        // Assert
        assertTrue(savedData.isFourPlayersMode)
        assertEquals("Stan", savedData.playerOneName)
        assertEquals("Kyle", savedData.playerTwoName)
        assertEquals("Kenny", savedData.playerThreeName)
        assertEquals("Clyde", savedData.playerFourName)

        assertNull(savedData.playerOneScore)
        assertNull(savedData.playerTwoScore)
        assertNull(savedData.playerThreeScore)
        assertNull(savedData.playerFourScore)
    }

    @Test
    fun fillSavedData_3_players_with_scores() {
        // Arrange
        val saveFileContent = "isFourPlayersMode|false,name1|Stan,score1|1#20#4,name2|Kyle,score2|3#5#0,name3|Kenny,score3|0#0#18"

        // Act
        val savedData = SavedData()
        savedData.fillSavedData(saveFileContent)

        // Assert
        assertFalse(savedData.isFourPlayersMode)
        assertEquals("Stan", savedData.playerOneName)
        assertEquals("Kyle", savedData.playerTwoName)
        assertEquals("Kenny", savedData.playerThreeName)

        assertEquals(1, savedData.playerOneScore!![0])
        assertEquals(20, savedData.playerOneScore!![1])
        assertEquals(4, savedData.playerOneScore!![2])

        assertEquals(3, savedData.playerTwoScore!![0])
        assertEquals(5, savedData.playerTwoScore!![1])
        assertEquals(0, savedData.playerTwoScore!![2])

        assertEquals(0, savedData.playerThreeScore!![0])
        assertEquals(0, savedData.playerThreeScore!![1])
        assertEquals(18, savedData.playerThreeScore!![2])
    }

    @Test
    fun fillSavedData_3_players_without_scores() {
        // Arrange
        val saveFileContent = "isFourPlayersMode|false,name1|Stan,score1|,name2|Kyle,score2|,name3|Kenny,score3|"

        // Act
        val savedData = SavedData()
        savedData.fillSavedData(saveFileContent)

        // Assert
        assertFalse(savedData.isFourPlayersMode)
        assertEquals("Stan", savedData.playerOneName)
        assertEquals("Kyle", savedData.playerTwoName)
        assertEquals("Kenny", savedData.playerThreeName)

        assertNull(savedData.playerOneScore)
        assertNull(savedData.playerTwoScore)
        assertNull(savedData.playerThreeScore)
    }
}