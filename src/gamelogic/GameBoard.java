package gamelogic;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Observable;

/**
 * 
 * Class that represents the gameboard
 * 
 * @author Nic Dorner
 * 
 */
public class GameBoard extends Observable {

	public static final int HEIGHT = 2;
	public static final int WIDTH = 6;
	public static final int INITIALSEEDS = 4;
	public static final int TOTALSEEDS = HEIGHT * WIDTH * INITIALSEEDS;

	private int[][] board;
	private GameBoardStack history;

	/**
	 * Standard constructor that creates a new game board. The initial seeds will
	 * be planted in each pit
	 */
	public GameBoard() {
		this.board = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
		this.history = new GameBoardStack();
		this.resetBoard();
	}

	/**
	 * Constructor that allows the game board to be created in a specific state
	 * 
	 * @param board
	 *            The board that will be used to create the gameboard
	 */
	public GameBoard(int[][] board) {
		this.board = board;
		this.history = new GameBoardStack();
	}

	/**
	 * Save the current state of the game board
	 */
	public void saveBoard() {
		this.history.push(this.board);
	}

	/**
	 * Retrieve the last saved state of the game baord
	 */
	public void undoBoard() {
		this.board = this.history.pop();
	}

	/**
	 * 
	 * Moves the seeds in the pit clockwise around the game board
	 * 
	 * @param row
	 *            Number of the row
	 * @param column
	 *            Number of the column
	 * @throws InvalidPitException
	 *             if row and column were invalid
	 * @return The number of seeds eaten by this move
	 */
	public int playPit(int row, int column) {
		int seeds = getPitSeeds(row, column);
		int score = 0;

		// Keep track of the changed pits
		List<Point> changedPits = new ArrayList<Point>();

		// Save the parameters to use it later to indicate witch pit was played
		int x = row, y = column;

		// Set the played pit to zero since all seeds are removed
		this.board[x][y] = 0;
		changedPits.add(new Point(x, y));

		// define the direction
		boolean add = false;
		if (x == 0) {
			add = true;
		}
		for (int i = 0; i < seeds; i++) {
			// iterate over column and row
			// make sure boundaries are not exeeded
			if (add) {
				y++;
			} else {
				y--;
			}
			if (y > GameBoard.WIDTH - 1) {
				y = GameBoard.WIDTH - 1;
				x++;
				add = false;
			}
			if (y < 0) {
				y = 0;
				x--;
				add = true;
			}

			// Eat the seeds if there is only one in the pit already and the row
			// is different
			if (this.board[x][y] == 1 && x != row) {
				this.board[x][y] = 0;
				score += 2;
			} else {
				this.board[x][y]++;
			}
			changedPits.add(new Point(x, y));
		}

		this.setChanged();
		this.notifyObservers(new GameBoardDTO(this, changedPits));

		return score;
	}

	/**
	 * 
	 * Returns the number of seeds in the pit
	 * 
	 * @param row
	 *            Number of the row
	 * @param column
	 *            Number of the column
	 * @throws InvalidPitException
	 *             if row and column were invalid
	 * @return The number of seeds in the pit
	 */
	public int getPitSeeds(int row, int column) {
		int seeds = -1;
		try {
			seeds = this.board[row][column];
		} catch (ArrayIndexOutOfBoundsException e) {
			throw new InvalidPitException("The pit at [" + row + "][" + column + "] does not exist.");
		}
		return seeds;
	}

	/**
	 * Returns the total amount of seeds left in the game
	 * 
	 * @return Total amount of seeds on the board
	 */
	public int getTotalSeeds() {
		int total = 0;
		for (int[] row : this.board) {
			for (int pit : row) {
				total += pit;
			}
		}
		return total;
	}

	/**
	 * Checks if all pits in a row are empty
	 * 
	 * @param row
	 *            The row that will be checked
	 * @throws InvalidPitException
	 *             if the row was invalid
	 * @return True if all pits are empty in this row
	 */
	public boolean checkEmptyRow(int row) {
		boolean empty = true;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			if (this.getPitSeeds(row, i) != 0) {
				empty = false;
				break;
			}
		}
		return empty;
	}

	/**
	 * Prints the gameboard
	 */
	public void printBoard() {
		StringBuilder output = new StringBuilder();
		String line = "";
		for (int i = 0; i < (GameBoard.WIDTH * 2 + 1); i++) {
			line = line + "-";
		}
		output.append(line + "\n");
		for (int[] row : this.board) {
			for (int pit : row) {
				output.append("|");
				output.append(pit + "");
			}
			output.append("|");
			output.append("\n");
			output.append(line + "\n");
		}
		System.out.println(output);
	}

	/**
	 * Reset all pits to the inital value
	 */
	public void resetBoard() {
		// Keep track of the changed pits
		List<Point> changedPits = new ArrayList<Point>();

		// Set every pit to its initial value
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				this.board[i][j] = GameBoard.INITIALSEEDS;
				changedPits.add(new Point(i, j));
			}
		}

		this.history.reset();

		this.setChanged();
		this.notifyObservers(new GameBoardDTO(this, changedPits));
	}

	/**
	 * 
	 * Exception that is thrown if the pit doesn't exist
	 * 
	 * @author Nic Dorner
	 *
	 */
	@SuppressWarnings("serial")
	public class InvalidPitException extends RuntimeException {
		public InvalidPitException() {
		}

		public InvalidPitException(String message) {
			super(message);
		}
	}

	/**
	 * Class to save the values of the game board in a stack.
	 * 
	 * @author Nic Dorner
	 *
	 */
	private class GameBoardStack {
		private int pointer;
		private int stack[][][];

		/**
		 * Constructor that creates a new empty stack
		 */
		public GameBoardStack() {
			this.stack = new int[10][GameBoard.HEIGHT][GameBoard.WIDTH];
			this.pointer = 0;
		}

		/**
		 * Push a new value on the stack
		 * 
		 * @param board
		 *            The value of the board to push on the stack
		 */
		public void push(int board[][]) {
			this.copy(this.stack[this.pointer], board);
			if (this.pointer + 1 == this.stack.length) {
				int temp[][][] = new int[this.pointer + 10][GameBoard.HEIGHT][GameBoard.WIDTH];
				for (int i = 0; i < this.stack.length; i++) {
					temp[i] = this.stack[i];
				}
				this.stack = temp;
			}
			this.pointer++;
		}

		/**
		 * Pop the current value from the stack
		 * 
		 * @return The value of the gameboard that was poped from the stack
		 */
		public int[][] pop() {
			if (this.pointer > 0) {
				this.pointer--;
				int[][] temp = new int[GameBoard.HEIGHT][GameBoard.WIDTH];
				this.copy(temp, this.stack[pointer]);
				return temp;
			} else {
				throw new EmptyStackException();
			}
		}

		/**
		 * Helper method to copy a two dimensional array
		 * 
		 * @param destination
		 *            array to copy to
		 * @param source
		 *            array to copy from
		 */
		private void copy(int[][] destination, int[][] source) {
			for (int i = 0; i < source.length; i++) {
				for (int j = 0; j < source[i].length; j++) {
					destination[i][j] = source[i][j];
				}
			}
		}

		/**
		 * Make the stack empty
		 */
		public void reset() {
			this.pointer = 0;
		}
	}
}