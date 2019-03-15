package gamelogic;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Stack;

import gamelogic.GameBoard.InvalidPitException;

/**
 * 
 * Class that runs the Uril game
 * 
 * @author Nic Dorner
 *
 */
public class Game extends Observable {
	private List<Player> players;
	private GameBoard gameBoard;
	private int turn;
	private Stack<Integer> history;
	private boolean end;

	/**
	 * 
	 * Construcor that creates a new Game but does not reset the gameboard or
	 * the players
	 * 
	 * @param gameBoard
	 *            Gameboard on which the game will be played
	 * @param playerA
	 *            Player of the first row
	 * @param playerB
	 *            Player of the second row
	 */
	public Game(GameBoard gameBoard, Player playerA, Player playerB) {
		this.players = new ArrayList<Player>();
		players.add(playerA);
		players.add(playerB);

		this.gameBoard = gameBoard;

		this.turn = 0;
		this.history = new Stack<Integer>();
		this.end = this.checkEnd();
	}

	/**
	 * Returns the current turn
	 * 
	 * @return The current turn (0 = first row player, 1 = second row player)
	 */
	public int getTurn() {
		return this.turn;
	}

	/**
	 * Tells if the game has ended
	 * 
	 * @return True if the game has ended
	 */
	public boolean hasEnded() {
		return this.end;
	}

	/**
	 * Returns the gameboard on which the game is being played
	 * 
	 * @return The gameboard
	 */
	public GameBoard getGameBoard() {
		return gameBoard;
	}

	/**
	 * Toggles the turn (0 to 1 and 1 to 0)
	 */
	private void toggleTurn() {
		this.turn = 1 - this.turn;

		this.setChanged();
		this.notifyObservers(this);
	}

	/**
	 * 
	 * Used to check on the gameboard if the game has ended sets the end
	 * instancevariable
	 * 
	 * @return True if the Game has ended
	 */
	private boolean checkEnd() {
		if (gameBoard.getTotalSeeds() == 2) {
			return this.end = true;
		}
		return this.end = false;
	}

	/**
	 * Gets the current player
	 * 
	 * @return The player who can play this turn
	 */
	public Player getCurrentPlayer() {
		return players.get(turn);
	}

	/**
	 * Gets the opposing player
	 * 
	 * @return The player who can not play this turn
	 */
	public Player getOpposingPlayer() {
		return players.get(1 - turn);
	}

	/**
	 * Returns the winner of the game
	 * 
	 * @throws RuningGameException
	 *             Thrown if there is no winner yet
	 * @return The player who won the game
	 */
	public Player getWinner() {
		if (end) {
			if (players.get(0).getScore() > players.get(1).getScore()) {
				return players.get(0);
			} else {
				return players.get(1);
			}
		} else {
			throw new RuningGameException("The Game is still running, hence there is no winner.");
		}
	}

	/**
	 * Returns the loser of the game
	 * 
	 * @throws RuningGameException
	 *             Thrown if there is no winner yet
	 * @return The player who lost the game
	 */
	public Player getLoser() {
		if (end) {
			if (players.get(0).getScore() < players.get(1).getScore()) {
				return players.get(0);
			} else {
				return players.get(1);
			}
		} else {
			throw new RuningGameException("The Game is still running, hence there is no loser.");
		}
	}

	/**
	 * Checks if the current player has an option to play
	 * 
	 * @return True if the current player has an option
	 */
	public boolean validatePlayable() {
		if (!this.gameBoard.checkEmptyRow(turn)) {
			return true;
		} else {
			this.toggleTurn();
			return false;
		}
	}

	/**
	 * The selected column is played: sets the score of the current player,
	 * checks for ending and toggles turn. In addition this method saves the
	 * current state of the game before the move is executed but only if the
	 * move is valid.
	 * 
	 * @param column
	 *            The column that will be played
	 * @throws InvalidPitException
	 *             Thrown if the column is invalid
	 * @return The number of seeds eaten by the player or -1 if the column was
	 *         not playable
	 */
	public int playTurn(int column) {
		// return -1 if the column contains no seeds or the game is over already
		if (!end && this.gameBoard.getPitSeeds(this.turn, column) > 0) {
			// Save the board and the player to later undo it properly
			this.saveTurn();
			// Play the turn itself
			int score = this.gameBoard.playPit(this.turn, column);
			// The score equals the number of seeds eaten divided by two
			score /= 2;
			this.players.get(this.turn).increaseScore(score);
			this.checkEnd();
			this.toggleTurn();
			return score;
		}
		return -1;
	}

	/**
	 * Saves the current state of the game
	 */
	private void saveTurn() {
		this.gameBoard.saveBoard();
		this.players.get(0).saveScore();
		this.players.get(1).saveScore();

		this.history.push(this.turn);
	}

	/**
	 * Undoes the last turn executed by playColumn, if playColumn returned -1
	 * then this turn was not saved and should not be undone
	 */
	public void undoTurn() {
		this.gameBoard.undoBoard();
		this.players.get(0).undoScore();
		this.players.get(1).undoScore();
		this.checkEnd();
		this.turn = this.history.pop();
	}

	/**
	 * Resets all objects and variables
	 */
	public void restartGame() {
		this.gameBoard.resetBoard();
		this.players.get(0).resetScore();
		this.players.get(1).resetScore();
		this.turn = 0;
		this.history = new Stack<Integer>();
		this.end = false;

		this.setChanged();
		this.notifyObservers(this);
	}

	/**
	 * 
	 * Exception that is thrown if asked for the winner or loser of the game,
	 * but the game hasn't ended yet
	 * 
	 * @author Nic Dorner
	 *
	 */
	@SuppressWarnings("serial")
	public class RuningGameException extends RuntimeException {
		public RuningGameException() {
		}

		public RuningGameException(String message) {
			super(message);
		}
	}
}
