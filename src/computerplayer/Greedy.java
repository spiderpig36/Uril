package computerplayer;

import gamelogic.Game;
import gamelogic.GameBoard;

/**
 * This class calculates a greedy turn. The turn is choosen by how many seeds
 * can be eaten by this play.
 * 
 * @author Nic Dorner
 *
 */
public class Greedy implements TurnCalculator {

	@Override
	public int calculateTurn(Game game) {
		int greedyTurn = 0;
		int greedLevel = -1;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			int greed;
			if ((greed = game.playTurn(i)) != -1) {
				if (greed > greedLevel) {
					greedLevel = greed;
					greedyTurn = i;
				}
				game.undoTurn();
			}
		}
		return greedyTurn;
	}
}
