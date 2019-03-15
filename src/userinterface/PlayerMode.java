package userinterface;

/**
 * Enum that represents the diffrent modes of the game
 * 
 * @author Nic Dorner
 *
 */
public enum PlayerMode {
	HUMAN("Human"), MINMAX("Optimal"), RANDOM("Random"), GREEDY("Greedy"), DEFENSIVE("Defensive");
	private String text;

	private PlayerMode(String text) {
		this.text = text;
	}

	@Override
	public String toString() {
		return text;
	}
}
