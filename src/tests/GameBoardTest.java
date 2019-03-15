package tests;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gamelogic.GameBoard;

public class GameBoardTest {

	private GameBoard gameBoard;

	private int[][] emptyRowBoard = { { 0, 0, 0, 0, 0, 0 }, { 1, 2, 3, 4, 5, 6 } };

	@Before
	public void setup() {
		gameBoard = new GameBoard();
	}

	@Test
	public void testPlayColumn() {
		gameBoard.playPit(0, 2);
		assertTrue(gameBoard.getPitSeeds(1, 5) == GameBoard.INITIALSEEDS + 1);
	}

	@Test
	public void testUndoBoard() {
		gameBoard.saveBoard();
		gameBoard.playPit(0, 1);
		gameBoard.undoBoard();
		assertTrue(gameBoard.getPitSeeds(0, 1) == GameBoard.INITIALSEEDS);
	}

	@Test
	public void testTotalSeeds() {
		assertTrue(gameBoard.getTotalSeeds() == GameBoard.TOTALSEEDS);
	}

	@Test
	public void testEmptyRow() {
		gameBoard = new GameBoard(emptyRowBoard);
		assertTrue(gameBoard.checkEmptyRow(0));
	}
}
