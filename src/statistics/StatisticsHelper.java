package statistics;

import java.math.BigInteger;
import java.util.List;

import gamelogic.Player;

/**
 * Helper class to update the statistics
 * 
 * @author Nic Dorner
 *
 */
public class StatisticsHelper {
	/**
	 * Static method that updates the statistics. The values for wins and losses
	 * are only updated if the players did not use the same modes.
	 * 
	 * @param statistics
	 *            Statistics object that needs to be updated
	 * @param turns
	 *            Amount of turns the game lasted
	 * @param winner
	 *            The winner of the game
	 * @param loser
	 *            The loser of the game
	 */
	public static void fillStatistics(Statistics statistics, int turns, Player winner, Player loser) {
		if (statistics.getGamesPlayed() == null) {
			statistics.setGamesPlayed(BigInteger.ONE);
		} else {
			statistics.setGamesPlayed(statistics.getGamesPlayed().add(BigInteger.ONE));
		}

		if (statistics.getAverageTurns() == null) {
			statistics.setAverageTurns(BigInteger.valueOf(turns));
		} else {
			statistics.setAverageTurns(BigInteger.valueOf(((statistics.getGamesPlayed().intValue() - 1) * statistics.getAverageTurns().intValue() + turns) / statistics.getGamesPlayed().intValue()));
		}

		// Only set win loose statistics if the modes of the players were not
		// the same
		if (!winner.getPlayerMode().toString().equals(loser.getPlayerMode().toString())) {
			List<Statistics.PlayerMode> list = statistics.getPlayerMode();
			boolean exists = false;
			for (Statistics.PlayerMode mode : list) {
				if (mode.getName().equals(winner.getPlayerMode().toString())) {
					if (mode.getWins() == null) {
						mode.setWins(BigInteger.ONE);
					} else {
						mode.setWins(mode.getWins().add(BigInteger.ONE));
					}
					exists = true;
				}
			}
			if (!exists) {
				Statistics.PlayerMode mode = new Statistics.PlayerMode();
				mode.name = winner.getPlayerMode().toString();
				mode.wins = BigInteger.ONE;
				list.add(mode);
			}
			exists = false;
			for (Statistics.PlayerMode mode : list) {
				if (mode.getName().equals(loser.getPlayerMode().toString())) {
					if (mode.getLosses() == null) {
						mode.setLosses(BigInteger.ONE);
					} else {
						mode.setLosses(mode.getLosses().add(BigInteger.ONE));
					}
					exists = true;
				}
			}
			if (!exists) {
				Statistics.PlayerMode mode = new Statistics.PlayerMode();
				mode.name = loser.getPlayerMode().toString();
				mode.losses = BigInteger.ONE;
				list.add(mode);
			}
		}
	}
}