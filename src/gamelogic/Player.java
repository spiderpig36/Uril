package gamelogic;

import java.util.Observable;
import java.util.Stack;

import userinterface.PlayerMode;

/**
 * Class that represents a Player
 * 
 * @author Nic Dorner
 *
 */
public class Player extends Observable {
	private int score;
	private String name;
	private PlayerMode playerMode;
	private Stack<Integer> history;

	/**
	 * Constructor that creates a new Player and sets the score to zero
	 * 
	 * @param name
	 *            Name of the player
	 */
	public Player(String name) {
		this.resetScore();
		this.name = name;
	}

	/**
	 * Returns the name of the player
	 * 
	 * @return Name of the player
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Gets the current playermode
	 * 
	 * @return Current playermode
	 */
	public PlayerMode getPlayerMode() {
		return playerMode;
	}

	/**
	 * Sets the playermode
	 * 
	 * @param mode
	 *            The playermode to be set
	 */
	public void setPlayerMode(PlayerMode mode) {
		this.playerMode = mode;
	}

	/**
	 * Save the current score of the player
	 */
	public void saveScore() {
		history.push(this.score);
	}

	/**
	 * Set the score to the last saved value
	 */
	public void undoScore() {
		this.score = history.pop();
	}

	/**
	 * Returns the current score of the player
	 * 
	 * @return Current score
	 */
	public int getScore() {
		return this.score;
	}

	/**
	 * Increases the score
	 * 
	 * @param amount
	 *            Number that will be added to the score
	 */
	public void increaseScore(int amount) {
		this.score += amount;

		this.setChanged();
		this.notifyObservers(this);
	}

	/**
	 * Sets the score to zero
	 * 
	 */
	public void resetScore() {
		this.score = 0;
		this.history = new Stack<Integer>();

		this.setChanged();
		this.notifyObservers(this);
	}
}
