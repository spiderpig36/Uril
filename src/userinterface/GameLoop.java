package userinterface;

import java.util.Date;

import computerplayer.Defensive;
import computerplayer.Greedy;
import computerplayer.MinMax;
import computerplayer.Random;
import computerplayer.TurnCalculator;
import gamelogic.Game;

/**
 * Theard that controlls the flow of the game.
 * 
 * @author Nic Dorner
 *
 */
public class GameLoop extends Thread {

	private final int DELAY = 1000;

	private GameController gameController;
	private boolean stopped;
	private int playerTurn;

	public GameLoop(GameController gameController) {
		super("Uril");

		this.gameController = gameController;
		stopped = false;
		playerTurn = -1;
	}

	public void cancel() {
		stopped = true;
	}

	public boolean isCanceled() {
		return stopped;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	@Override
	public void run() {
		Game game = gameController.getGame();
		while (!game.hasEnded() && !stopped && !this.isInterrupted()) {
			TurnCalculator turnCalculator = null;
			try {
				switch (game.getCurrentPlayer().getPlayerMode()) {
					case DEFENSIVE:
						turnCalculator = new Defensive();
						gameController.setPlayerTurn(false);
						break;
					case GREEDY:
						turnCalculator = new Greedy();
						gameController.setPlayerTurn(false);
						break;
					case MINMAX:
						MinMax minMax = new MinMax();
						minMax.setDepth(gameController.getDepth());
						turnCalculator = minMax;
						gameController.setPlayerTurn(false);
						break;
					case RANDOM:
						turnCalculator = new Random();
						gameController.setPlayerTurn(false);
						break;
					case HUMAN:
						gameController.setPlayerTurn(true);
						if (playerTurn != -1) {
							game.playTurn(playerTurn);
							playerTurn = -1;
						} else {
							// Loop is interrupted while the human player
							// chooses its turn.
							this.interrupt();
						}
						break;
					default:
						break;
				}
				if (turnCalculator != null && !game.hasEnded() && !stopped) {
					gameController.removeObservers();
					Date before = new Date();
					int column = turnCalculator.calculateTurn(game);
					Date after = new Date();
					long diff = after.getTime() - before.getTime();
					gameController.setObservers();
					if (gameController.isDelayed() && diff < DELAY) {
						Thread.sleep(DELAY - diff);
					}
					game.playTurn(column);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		gameController.endGame();
	}
}