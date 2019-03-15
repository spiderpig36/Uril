package computerplayer;

import gamelogic.Game;
import gamelogic.GameBoard;
import gamelogic.Player;

/**
 * This class calculates the optimal turn by using the minmax value of each
 * column. The minmax value is determined by the amount of points that can be
 * achieved by this play.
 * 
 * @author Nic Dorner
 *
 */
public class MinMax implements TurnCalculator {

	private int depth = 1;

	public void setDepth(int depth) {
		this.depth = depth;
	}

	@Override
	public int calculateTurn(Game game) {
		// if the other player has won the game by points the minmax player
		// gives up
		if (game.getOpposingPlayer().getScore() >= (GameBoard.TOTALSEEDS - 2) / 4) {
			return new Random().calculateTurn(game);
		}

		int maxValue = Integer.MIN_VALUE;
		int maxTurn = 0;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			if (game.playTurn(i) != -1) {
				int value;
				if (game.validatePlayable()) {
					value = min(game, depth, maxValue, Integer.MAX_VALUE);
				} else {
					value = max(game, depth, maxValue, Integer.MAX_VALUE);
				}
				if (value > maxValue) {
					maxValue = value;
					maxTurn = i;
				}
				game.undoTurn();
			}
		}
		return maxTurn;
	}

	/**
	 * Maximizing part of the recursive minmax algorithm
	 * 
	 * @param game
	 *            The current state of the game
	 * @param depth
	 *            The remaining depth
	 * @param alpha
	 *            Aplha cut-off value
	 * @param beta
	 *            Beta cut-off value
	 * @return Minmax value calculated by the evaluate method
	 */
	private static int max(Game game, int depth, int alpha, int beta) {
		int maxValue = alpha;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			if (game.playTurn(i) != -1) {
				int value = 0;
				if (game.hasEnded() || depth - 1 <= 0) {
					value = evaluate(game, game.getOpposingPlayer(), game.getCurrentPlayer());
				} else {
					if (game.validatePlayable()) {
						value = min(game, depth - 1, maxValue, beta);
					} else {
						value = max(game, depth - 1, maxValue, beta);
					}
				}
				game.undoTurn();
				if (value > maxValue) {
					maxValue = value;
					if (maxValue >= beta) {
						break;
					}
				}
			}
		}
		return maxValue;
	}

	/**
	 * Mimimizing part of the recursive minmax algorithm
	 * 
	 * @param game
	 *            The current state of the game
	 * @param depth
	 *            The remaining depth
	 * @param alpha
	 *            Aplha cut-off value
	 * @param beta
	 *            Beta cut-off value
	 * @return Minmax value calculated by the evaluate method
	 */
	private static int min(Game game, int depth, int alpha, int beta) {
		int minValue = beta;
		for (int i = 0; i < GameBoard.WIDTH; i++) {
			if (game.playTurn(i) != -1) {
				int value = 0;
				if (game.hasEnded() || depth - 1 <= 0) {
					value = evaluate(game, game.getCurrentPlayer(), game.getOpposingPlayer());
				} else {
					if (game.validatePlayable()) {
						value = max(game, depth - 1, alpha, minValue);
					} else {
						value = min(game, depth - 1, alpha, minValue);
					}
				}
				game.undoTurn();
				if (value < minValue) {
					minValue = value;
					if (minValue <= alpha) {
						break;
					}
				}
			}
		}
		return minValue;
	}

	/**
	 * Evaluete the minmax value to return by the recursive funtion
	 * 
	 * @param game
	 *            The current state of the game
	 * @param max
	 *            The player who maximizes
	 * @param min
	 *            The player who minimizes
	 * @return The current value of the game from the perspective of the
	 *         maximizing player
	 */
	private static int evaluate(Game game, Player max, Player min) {
		if (game.hasEnded()) {
			if (game.getWinner().equals(max)) {
				return 24;
			}
			if (game.getWinner().equals(min)) {
				return -24;
			}
		}

		return max.getScore() - min.getScore();
	}
}
