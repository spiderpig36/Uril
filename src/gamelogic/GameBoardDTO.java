package gamelogic;

import java.awt.Point;
import java.util.List;

/**
 * Data Transfer Object that is send by the GameBoard class when it changes
 * 
 * @author Nic
 *
 */
public class GameBoardDTO {
	private GameBoard gameBoard;
	private List<Point> changedPits;

	/**
	 * Constructor that sets the DTO
	 * 
	 * @param gameBoard
	 *            The GameBoard Object that changed
	 * @param changedPits
	 *            A List of Points that contains each pit that changed
	 */
	public GameBoardDTO(GameBoard gameBoard, List<Point> changedPits) {
		this.setGameBoard(gameBoard);
		this.setChangedPits(changedPits);
	}

	public GameBoard getGameBoard() {
		return gameBoard;
	}

	public void setGameBoard(GameBoard gameBoard) {
		this.gameBoard = gameBoard;
	}

	public List<Point> getChangedPits() {
		return changedPits;
	}

	public void setChangedPits(List<Point> changedPits) {
		this.changedPits = changedPits;
	}
}
