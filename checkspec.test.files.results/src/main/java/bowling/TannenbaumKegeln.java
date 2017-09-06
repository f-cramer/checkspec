package bowling;

/**
 * Models the "Tannenbaum Kegeln" bowling-style game.
 *
 * @author Nicolas Weber
 */
public class TannenbaumKegeln extends Game {
	// internal attributes
	private int[][] counts;
	private int state;

	/**
	 * Creates a new instance of "Tannenbaum Kegeln" for up to maxPlayerCount
	 * players.
	 *
	 * @param maxPlayerCount the maximum number of players for this game
	 */
	public TannenbaumKegeln(int maxPlayerCount) {
		super(maxPlayerCount);
		counts = new int[maxPlayerCount][getPinCount()];
		state = 0;

		for (int i = 0; i < maxPlayerCount; i++) {
			counts[i][0] = 1;
			counts[i][1] = 2;
			counts[i][2] = 7;
			counts[i][3] = 6;
			counts[i][4] = 5;
			counts[i][5] = 4;
			counts[i][6] = 3;
			counts[i][7] = 2;
			counts[i][8] = 1;
		}
	}

	// implementation of the abstract methods from class bowling.Game
	
	@Override
	public String getName() {
		return "Tannenbaum Kegeln";
	}

	@Override
	public int getPinCount() {
		return 9;
	}

	@Override
	public int getRoundCount() {
		return 100;
	}


	@Override
	public int[] getScore(Player player) {
		if (!isValidPlayer(player))
			return null;

		return counts[player.getID()];
	}

	@Override
	public Player getWinner() {
		if (hasFinished()) {
			Player bestPlayer = null;
			int bestScore = Integer.MAX_VALUE;

			for (int i = 0; i < getActivePlayerCount(); i++) {
				Player p = getPlayer(i);
				int score = 0;
				int[] scores = getScore(p);

				for (int value : scores)
					score += value;

				if (score < bestScore) {
					bestPlayer = p;
					bestScore = score;
				}
			}

			return bestPlayer;
		}

		return null;
	}
	
	// overridden or additional methods below
	
	/**
	 * the current player throws a new ball and hits 'count' pins
	 * 
	 * @param count number of pins hit
	 * @return false if the throw is invalid (e.g. too many pins are hit), else
	 *         true
	 */
	@Override
	public boolean throwBall(int count) {
		// is this a valid throw?
		if (!super.throwBall(count))
			return false;

		// get player id
		int playerID = getActivePlayer().getID();

		// increment state
		state += count;

		// is this either the second throw or
		// getThrow() == 3 is necessary as throwBall already incremented the value
		if (getThrow() == 3 || count == getPinCount()) {
			// decrement count
			if (state > 0 && counts[playerID][state - 1] != 0)
				counts[playerID][state - 1]--;

			// reset state
			state = 0;

			// finished game if player has won
			if (hasPlayerWon()) {
				overwriteRound(Integer.MAX_VALUE);
			}
			// switch player
			else {
				nextPlayer();
			}
		}

		// return
		return true;
	}

	// internal helper method
	// returns true if the player has struck all pin values from his "tree"
	private boolean hasPlayerWon() {
		for (int i : counts[getActivePlayer().getID()]) {
			if (i != 0)
				return false;
		}

		return true;
	}

}
