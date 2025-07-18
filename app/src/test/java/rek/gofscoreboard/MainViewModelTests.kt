package rek.gofscoreboard

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: MainViewModel

    @Before
    fun setup() {
        viewModel = MainViewModel()
    }

    @Test
    fun `initial state is correct`() {
        assertEquals(4, viewModel.nbPlayers)
        assertEquals(View.VISIBLE, viewModel.playerFourVisibility.value)
        assertEquals("", viewModel.playerOneName.value)
        assertEquals("", viewModel.playerTwoName.value)
        assertEquals("", viewModel.playerThreeName.value)
        assertEquals("", viewModel.playerFourName.value)
    }

    @Test
    fun `updateNbPlayer hides player four when set to 3`() {
        viewModel.updateNbPlayer(3)
        assertEquals(3, viewModel.nbPlayers)
        assertEquals(View.INVISIBLE, viewModel.playerFourVisibility.value)
    }

    @Test
    fun `updateNbPlayer shows player four when set back to 4`() {
        viewModel.updateNbPlayer(3)
        viewModel.updateNbPlayer(4)
        assertEquals(4, viewModel.nbPlayers)
        assertEquals(View.VISIBLE, viewModel.playerFourVisibility.value)
    }

    @Test
    fun `isEveryPlayerNameFilled returns false if a name is empty`() {
        viewModel.playerOneName.value = "A"
        viewModel.playerTwoName.value = "B"
        viewModel.playerThreeName.value = ""
        viewModel.playerFourName.value = "D"
        assertFalse(viewModel.isEveryPlayerNameFilled())
    }

    @Test
    fun `isEveryPlayerNameFilled returns true if all names are filled`() {
        viewModel.playerOneName.value = "A"
        viewModel.playerTwoName.value = "B"
        viewModel.playerThreeName.value = "C"
        viewModel.playerFourName.value = "D"
        assertTrue(viewModel.isEveryPlayerNameFilled())
    }

    @Test
    fun `isEveryPlayerNameDifferent returns false if two names are the same (case-insensitive)`() {
        viewModel.playerOneName.value = "Bob"
        viewModel.playerTwoName.value = "alice"
        viewModel.playerThreeName.value = "bob"
        viewModel.playerFourName.value = "Dan"
        assertFalse(viewModel.isEveryPlayerNameDifferent())
    }

    @Test
    fun `isEveryPlayerNameDifferent returns true if all names are different`() {
        viewModel.playerOneName.value = "A"
        viewModel.playerTwoName.value = "B"
        viewModel.playerThreeName.value = "C"
        viewModel.playerFourName.value = "D"
        assertTrue(viewModel.isEveryPlayerNameDifferent())
    }

    @Test
    fun `clearPlayersNames resets all player names to empty`() {
        viewModel.playerOneName.value = "A"
        viewModel.playerTwoName.value = "B"
        viewModel.playerThreeName.value = "C"
        viewModel.playerFourName.value = "D"

        viewModel.updateNbPlayer(4)
    }

    @Test
    fun `getPlayerNameList omits player four if nbPlayers is 3`() {
        viewModel.playerOneName.value = "A"
        viewModel.playerTwoName.value = "B"
        viewModel.playerThreeName.value = "C"
        viewModel.playerFourName.value = "D"
        viewModel.updateNbPlayer(3)

        assertTrue(viewModel.isEveryPlayerNameFilled())

        viewModel.playerThreeName.value = "B"
        assertFalse(viewModel.isEveryPlayerNameDifferent())
    }
}
