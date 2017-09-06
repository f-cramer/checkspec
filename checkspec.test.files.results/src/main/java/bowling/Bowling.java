package bowling;

/**
 * This class implements a Bowling game controller
 * 
 * @author Nicolas Weber
 */
public class Bowling extends Game {
	// internal attributes
	/**
	 * Array that stores all throws
	 */
	private int scores[][];

	/**
	 * Initializes the game
	 * 
	 * @param maxPlayerCount the maximum number of players able to join
	 */
	public Bowling(int maxPlayerCount) {
		super(maxPlayerCount);

		// we can have maximal maxPlayerCount players and max have 9*2+3 throws
		// per player
		scores = new int[maxPlayerCount][(getRoundCount() - 1) * 2 + 3];
	}

	// implementation of the abstract methods from class bowling.Game

	/**
	 * returns the name of this game type
	 *
	 * @return the name of this game type
	 */
	@Override
	public String getName() {
		return "Bowling";
	}

	/**
	 * returns the number of pins in this game type
	 */
	@Override
	public int getPinCount() {
		return 10;
	}

	/**
	 * returns the number of rounds in this game type
	 *
	 * @return the number of rounds (10)
	 */
	@Override
	public int getRoundCount() {
		return 10;
	}

	/**
	 * calculates the score for a given player
	 * 
	 * @param player player for which the score is calculated
	 * @return returns the current score of the given player and null if the
	 *         player is not valid or any other problem occurred
	 */
	@Override
	public int[] getScore(Player player) {
		if (!isValidPlayer(player))
			return null;

		int score = 0;
		int[] output = new int[getRoundCount()];

		// round 1 to 9
		for (int round = 1; round < getRoundCount(); round++) {
			int roundScore = 0;

			// note: "thro" because "throw" is a reserved keyword...
			for (int thro = 1; thro <= 2; thro++) {
				int index = getThrowIndex(round, thro);
				int pins = scores[player.getID()][index];

				score += pins;
				roundScore += pins;

				// is strike
				if (thro == 1 && pins == getPinCount()) {
					int nextPins = scores[player.getID()][index + 2];

					score += nextPins;

					if (nextPins == getPinCount() && round != 9)
						score += scores[player.getID()][index + 4];
					else
						score += scores[player.getID()][index + 3];

					break;
				}

				// is spare
				if (thro == 2 && roundScore == getPinCount()) {
					score += scores[player.getID()][index + 1];
				}
			}

			output[round - 1] = score;
		}

		// 10th and final round
		score += scores[player.getID()][(getRoundCount() - 1) * 2 + 0];
		score += scores[player.getID()][(getRoundCount() - 1) * 2 + 1];
		score += scores[player.getID()][(getRoundCount() - 1) * 2 + 2];

		output[9] = score;

		return output;
	}

	@Override
	public Player getWinner() {
		if (hasFinished()) {
			Player bestPlayer = null;
			int bestScore = 0;

			for (int i = 0; i < getActivePlayerCount(); i++) {
				Player p = getPlayer(i);
				int score = getScore(p)[getRoundCount() - 1];

				if (score > bestScore) {
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
	 * @return false if throw is invalid (e.g. too many pins are hit), else
	 *         true
	 */
	@Override
	public boolean throwBall(int count) {
		if (!super.throwBall(count))
			return false;

		// is already incremented
		int thro = getThrow() - 1;
		int index = getThrowIndex(getRound(), thro);
		int playerID = getActivePlayer().getID();

		scores[playerID][index] = count;

		// is last round?
		if (getRound() == getRoundCount()) {
			// reset pins if this is was a strike or spare
			if (getPinsLeft() == 0)
				resetPins();
			// if this is round 2 and the first was no strike, then hand to next
			// player
			else if (thro == 2 && scores[playerID][getThrowIndex(getRound(), 1)] != 10)
				nextPlayer();

			// change after round three
			if (thro == 3)
				nextPlayer();
		} else {
			boolean isStrike = thro == 1 && getPinsLeft() == 0;
			boolean isEnd = thro == 2;

			if (isStrike || isEnd)
				nextPlayer();
		}

		return true;
	}

	// internal helper method
	/**
	 * returns the index of round and throw
	 */
	private int getThrowIndex(int round, int thro) {
		return (round - 1) * 2 + (thro - 1);
	}

}
