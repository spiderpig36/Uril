package computerplayer;

import gamelogic.Game;
import gamelogic.GameBoard;

/**
 * This class calculates a random turn.
 * 
 * @author Nic Dorner
 *
 */
public class Random implements TurnCalculator {

	@Override
	public int calculateTurn(Game game) {
		int result;
		int random;
		do {
			random = (int) Math.round((Math.random() * (GameBoard.WIDTH - 1)));
			result = game.playTurn(random);
		} while (result == -1);
		game.undoTurn();
		return random;
	}

}
