package userinterface;

import java.awt.Point;
import java.math.BigInteger;
import java.net.URL;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.ResourceBundle;

import gamelogic.Game;
import gamelogic.GameBoard;
import gamelogic.GameBoardDTO;
import gamelogic.Player;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import statistics.Statistics;
import statistics.StatisticsHelper;

/**
 * Class that is the controller of the gameview. Observes the game, the
 * gameboard and the players. Updates the view if changes happen. Starts and
 * stops the gameloop that controlls the flow of the game. Updates the
 * statistics after a game ends.
 * 
 * @author Nic Dorner
 *
 */
public class GameController implements Initializable, Observer {
	@FXML
	private MenuBar menuBar;
	@FXML
	private CheckMenuItem cmbAnimation;
	@FXML
	private CheckMenuItem cmbDelay;
	@FXML
	private ToggleGroup tglDifficulty;
	@FXML
	private GridPane grid;
	@FXML
	private Label lblPlayerA;
	@FXML
	private Label lblPlayerB;
	@FXML
	private Label lblWinner;
	@FXML
	private Label lblTurns;
	@FXML
	private ComboBox<PlayerMode> cbxPlayerA;
	@FXML
	private ComboBox<PlayerMode> cbxPlayerB;
	// Statistics
	@FXML
	private Label lblGamesPlayed;
	@FXML
	private Label lblAverageTurns;
	@FXML
	private Label lblHumanWins;
	@FXML
	private Label lblHumanLosses;
	@FXML
	private Label lblOptimalWins;
	@FXML
	private Label lblOptimalLosses;
	@FXML
	private Label lblRandomWins;
	@FXML
	private Label lblRandomLosses;
	@FXML
	private Label lblGreedyWins;
	@FXML
	private Label lblGreedyLosses;
	@FXML
	private Label lblDefensiveWins;
	@FXML
	private Label lblDefensiveLosses;

	private GameBoard gameBoard;
	private Player playerA;
	private Player playerB;
	private Game game;
	private int turnsPlayed;
	private boolean playerTurn;

	private Statistics statistics;

	private GameLoop gameLoop;
	// if true the restarting is not safe and the game is waiting to be
	// restarted
	private boolean restarting;

	public boolean isPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(boolean playerTurn) {
		this.playerTurn = playerTurn;
	}

	public Game getGame() {
		return game;
	}

	public Statistics getStatistics() {
		return statistics;
	}

	public GameController(Statistics statistics) {
		this.statistics = statistics;
	}

	public boolean isDelayed() {
		return cmbDelay.isSelected() || cmbAnimation.isSelected();
	}

	public int getDepth() {
		RadioMenuItem tgl = (RadioMenuItem) tglDifficulty.getSelectedToggle();
		int depth = 1;
		switch (Integer.parseInt(tgl.getId())) {
			case 4:
				depth = 2;
				break;
			case 5:
				depth = 8;
				break;
			case 6:
				depth = 12;
				break;
		}
		return depth;
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.gameBoard = new GameBoard();

		this.playerA = new Player("A");
		this.playerA.setPlayerMode(PlayerMode.HUMAN);
		this.playerB = new Player("B");
		this.playerB.setPlayerMode(PlayerMode.HUMAN);

		this.game = new Game(gameBoard, playerA, playerB);

		this.setObservers();

		this.cbxPlayerA.setItems(FXCollections.observableArrayList(PlayerMode.values()));
		this.cbxPlayerA.getSelectionModel().select(0);
		this.cbxPlayerB.setItems(FXCollections.observableArrayList(PlayerMode.values()));
		this.cbxPlayerB.getSelectionModel().select(0);
		playerTurn = false;

		this.fillInStatistics();

		gameLoop = new GameLoop(this);
		restarting = false;
	}

	/**
	 * Stops the gameloop. This method should be called when this class is no
	 * longer in use.
	 */
	public void close() {
		if (gameLoop.isAlive()) {
			gameLoop.cancel();
		}
	}

	/**
	 * Event is called when the menu is clicked
	 * 
	 * @param event
	 *            Contains information about this event
	 */
	@FXML
	private void handleMenuAction(final ActionEvent event) {
		MenuItem item = (MenuItem) event.getSource();
		if (item.getId().equals("1")) {
			if (gameLoop.isAlive()) {
				gameLoop.cancel();
			}

			this.restartGame(false);
		}
	}

	/**
	 * Event is called when a pit is clicked
	 * 
	 * @param event
	 *            Contains information about this event
	 */
	@FXML
	private void handlePitSelected(final MouseEvent event) {
		int column = GridPane.getColumnIndex((Label) event.getSource()).intValue();
		int row = GridPane.getRowIndex((Label) event.getSource()).intValue();

		int turn = game.getTurn();
		if (turn == row && playerTurn) {
			gameLoop = new GameLoop(this);
			gameLoop.setPlayerTurn(column);
			playerTurn = false;
			gameLoop.start();
		}
	}

	/**
	 * Event is called when a new value is selected in the comboboxes
	 * 
	 * @param event
	 *            Contains information about this event
	 */
	@FXML
	private void handleModeChange(final ActionEvent event) {
		if (event.getSource().equals(this.cbxPlayerA)) {
			this.playerA.setPlayerMode(this.cbxPlayerA.getSelectionModel().getSelectedItem());
		}
		if (event.getSource().equals(this.cbxPlayerB)) {
			this.playerB.setPlayerMode(this.cbxPlayerB.getSelectionModel().getSelectedItem());
		}
		if (!gameLoop.isAlive() && playerTurn == true) {
			gameLoop = new GameLoop(this);
			gameLoop.start();
		}
	}

	private void fillInStatistics() {
		if (statistics.getGamesPlayed() == null) {
			statistics.setGamesPlayed(BigInteger.ZERO);
		}
		Platform.runLater(() -> lblGamesPlayed.setText("Games played: " + statistics.getGamesPlayed()));
		if (statistics.getAverageTurns() == null) {
			statistics.setAverageTurns(BigInteger.ZERO);
		}
		Platform.runLater(() -> lblAverageTurns.setText("Average turns: " + statistics.getAverageTurns()));

		List<Statistics.PlayerMode> list = statistics.getPlayerMode();
		for (Statistics.PlayerMode playerMode : list) {
			if (playerMode.getWins() == null) {
				playerMode.setWins(BigInteger.ZERO);
			}
			if (playerMode.getLosses() == null) {
				playerMode.setLosses(BigInteger.ZERO);
			}
			if (playerMode.getName().equals("Human")) {
				Platform.runLater(() -> lblHumanWins.setText("Wins: " + playerMode.getWins()));
				Platform.runLater(() -> lblHumanLosses.setText("Losses: " + playerMode.getLosses()));
			}
			if (playerMode.getName().equals("Optimal")) {
				Platform.runLater(() -> lblOptimalWins.setText("Wins: " + playerMode.getWins()));
				Platform.runLater(() -> lblOptimalLosses.setText("Losses: " + playerMode.getLosses()));
			}
			if (playerMode.getName().equals("Random")) {
				Platform.runLater(() -> lblRandomWins.setText("Wins: " + playerMode.getWins()));
				Platform.runLater(() -> lblRandomLosses.setText("Losses: " + playerMode.getLosses()));
			}
			if (playerMode.getName().equals("Greedy")) {
				Platform.runLater(() -> lblGreedyWins.setText("Wins: " + playerMode.getWins()));
				Platform.runLater(() -> lblGreedyLosses.setText("Losses: " + playerMode.getLosses()));
			}
			if (playerMode.getName().equals("Defensive")) {
				Platform.runLater(() -> lblDefensiveWins.setText("Wins: " + playerMode.getWins()));
				Platform.runLater(() -> lblDefensiveLosses.setText("Losses: " + playerMode.getLosses()));
			}
		}
	}

	/**
	 * Should be called when the a game ends or the gameloop gets stoped.
	 * Restarts the game and updates the statistics.
	 */
	public void endGame() {
		if (restarting) {
			this.restartGame(true);
		} else {
			if (game.hasEnded()) {
				final String name = game.getWinner().getName();
				Platform.runLater(() -> lblWinner.setText("The winner is player " + name));

				StatisticsHelper.fillStatistics(statistics, turnsPlayed, game.getWinner(), game.getLoser());
				this.fillInStatistics();
			}
		}
	}

	/**
	 * Reset all game variables
	 */
	private void restartGame(boolean finished) {
		if (!gameLoop.isAlive() || finished) {
			playerTurn = false;
			game.restartGame();
			turnsPlayed = 0;
			toggleRow(game.getTurn());

			final int scoreA = playerA.getScore();
			Platform.runLater(() -> lblPlayerA.setText("Player A: " + scoreA));
			final int scoreB = playerB.getScore();
			Platform.runLater(() -> lblPlayerB.setText("Player B: " + scoreB));
			final int turns = turnsPlayed;
			Platform.runLater(() -> lblTurns.setText("Turns: " + turns));
			Platform.runLater(() -> lblWinner.setText(""));

			gameLoop = new GameLoop(this);
			gameLoop.start();

			restarting = false;
		} else {
			restarting = true;
		}
	}

	/**
	 * This method is called when updates from the models are received. It
	 * updates all userinterface controlls.
	 */
	@Override
	public void update(Observable o, Object arg) {
		// Update from the GameBoard
		if (arg instanceof gamelogic.GameBoardDTO) {
			// Variables to control the timer
			float delay = 0.01f;
			GameBoardDTO dto = (GameBoardDTO) arg;
			for (Point coordinates : dto.getChangedPits()) {
				Label pit = (Label) grid.getChildren().get((coordinates.x * gamelogic.GameBoard.WIDTH) + coordinates.y);
				// Final varable to acess in scope
				final int seeds = gameBoard.getPitSeeds(coordinates.x, coordinates.y);
				// Delay the update of the pit
				if (cmbAnimation.isSelected()) {
					delay += .075;
					Timeline animation = new Timeline(new KeyFrame(Duration.seconds(delay), event -> {
						pit.setText(seeds + "");
					}));
					animation.play();
				} else {
					Platform.runLater(() -> pit.setText(seeds + ""));
				}
			}
		}
		// Update from a Player
		if (arg instanceof gamelogic.Player) {
			final int score;
			if (arg.equals(playerA)) {
				score = playerA.getScore();
				Platform.runLater(() -> lblPlayerA.setText("Player A: " + score));
			} else {
				score = playerB.getScore();
				Platform.runLater(() -> lblPlayerB.setText("Player B: " + score));
			}
		}
		// Update from the Game
		if (arg instanceof gamelogic.Game) {
			turnsPlayed++;
			final int turns = turnsPlayed;
			Platform.runLater(() -> lblTurns.setText("Turns: " + turns));

			game.validatePlayable();
			toggleRow(game.getTurn());
		}
	}

	/**
	 * Used to observe the models
	 */
	public void setObservers() {
		gameBoard.addObserver(this);
		playerA.addObserver(this);
		playerB.addObserver(this);
		game.addObserver(this);
	}

	/**
	 * Used to stop observing the models
	 */
	public void removeObservers() {
		gameBoard.deleteObserver(this);
		playerA.deleteObserver(this);
		playerB.deleteObserver(this);
		game.deleteObserver(this);
	}

	/**
	 * Calls the setRowOpacity method for both rows.
	 * 
	 * @param turn
	 *            The current turn of the game
	 */
	private void toggleRow(int turn) {
		setRowOpacity(turn, 1);
		setRowOpacity(1 - turn, 0.5);
	}

	/**
	 * Set the opacity of the specified row to the specified opacity
	 * 
	 * @param row
	 *            The row that is changed
	 * @param opacity
	 *            The opacity that is set to the specified row
	 */
	private void setRowOpacity(int row, double opacity) {
		Color color = Color.rgb(0, 0, 0, opacity);
		for (int j = 0; j < gamelogic.GameBoard.WIDTH; j++) {
			Label pit = (Label) grid.getChildren().get((row * gamelogic.GameBoard.WIDTH) + j);
			pit.setTextFill(color);
		}
	}
}