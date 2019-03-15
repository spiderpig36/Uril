package computerplayer;

import gamelogic.Game;

/**
 * Interface for Classes that can calculate a column to play
 * 
 * @author Nic Dorner
 *
 */
public interface TurnCalculator {
	/**
	 * Calculates the value of the turn
	 * 
	 * @param game
	 *            Current state of the game
	 * @return The column on the gamefield that was calculated by this method
	 */
	public int calculateTurn(Game game);
}
