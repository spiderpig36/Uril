package gamelogic;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Test class for the gamelogic
 * 
 * @author Nic Dorner
 *
 */
public class GLTest {

	/**
	 * Runs the game in the console without any graphical userinterface
	 * 
	 * @param args
	 *            input parameters for the application. Not used in this case
	 */
	public static void main(String[] args) {
		GameBoard gameBoard = new GameBoard();

		Player a = new Player("A");
		Player b = new Player("B");

		Game game = new Game(gameBoard, a, b);

		while (!game.hasEnded()) {
			System.out.println("Player " + a.getName() + ": " + a.getScore());
			System.out.println("Player " + b.getName() + ": " + b.getScore());
			System.out.println("Turn for Player: " + game.getCurrentPlayer().getName());
			gameBoard.printBoard();

			try {
				if (!game.validatePlayable()) {
					continue;
				}

				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String s = br.readLine();
				int column = Integer.parseInt(s);

				if (game.playTurn(column - 1) == -1) {
					System.err.println("Invalid selection");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Winner is player: " + game.getWinner().getName());
	}
}
