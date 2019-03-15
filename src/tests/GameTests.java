package tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import gamelogic.Game;
import gamelogic.GameBoard;
import gamelogic.Player;

public class GameTests {

	private Game game;

	private int[][] emptyRowBoard = { { 0, 0, 0, 0, 0, 0 }, { 1, 2, 3, 4, 5, 6 } };
	private int[][] endBoard = { { 0, 0, 0, 0, 0, 1 }, { 1, 0, 0, 0, 0, 0 } };

	@Before
	public void setup() {
		game = new Game(new GameBoard(), new Player("A"), new Player("B"));
	}

	@Test
	public void testTurnChange() {
		int turn = game.getTurn();
		game.playTurn(0);
		assertTrue("The turn did not change!", game.getTurn() != turn);
	}

	@Test
	public void testUndo() {
		int turn = game.getTurn();
		game.playTurn(0);
		game.undoTurn();
		assertTrue("Turn was undone!", game.getTurn() == turn);
	}

	@Test
	public void testPlayerChange() {
		assertTrue(game.getCurrentPlayer().getName().equals("A"));
		game.playTurn(0);
		assertTrue(game.getOpposingPlayer().getName().equals("A"));
	}

	@Test
	public void testValidatePlayable() {
		game.playTurn(0);
		assertTrue(game.validatePlayable());

		game = new Game(new GameBoard(emptyRowBoard), new Player("A"), new Player("B"));
		assertFalse(game.validatePlayable());
	}

	@Test
	public void testEnd() {
		game = new Game(new GameBoard(endBoard), new Player("A"), new Player("B"));
		assertTrue(game.hasEnded());
	}

	@Test(expected = Game.RuningGameException.class)
	public void testGameHasNotEndedWinner() {
		game.getWinner();
	}

	@Test(expected = Game.RuningGameException.class)
	public void testGameHasNotEndedLooser() {
		game.getLoser();
	}

	@Test(expected = GameBoard.InvalidPitException.class)
	public void testInvalidPit() {
		game.playTurn(GameBoard.WIDTH);
	}
}
