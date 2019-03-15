package computerplayer;

import gamelogic.Game;
import gamelogic.GameBoard;

/**
 * This class calculates a defensive turn. The turn is choosen by how few singel
 * pits remain in the game.
 * 
 * @author Nic Dorner
 *
 */
public class Defensive implements TurnCalculator {

	@Override
	public int calculateTurn(Game game) {
		int defensiveTurn = 0;
		int defenseLevel = 12;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			if (game.playTurn(i) != -1) {
				GameBoard gameBoard = game.getGameBoard();
				int defense = 0;
				for (int y = 0; y < GameBoard.WIDTH; y++) {
					if (gameBoard.getPitSeeds(1 - game.getTurn(), y) == 1) {
						defense++;
					}
				}
				if (defense < defenseLevel) {
					defenseLevel = defense;
					defensiveTurn = i;
				}
				game.undoTurn();
			}
		}
		return defensiveTurn;
	}
}
