package bowling;

/*-
 * #%L
 * CheckSpec Test Files Results
 * %%
 * Copyright (C) 2017 Florian Cramer
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
public class Bowling extends Game {

	private int scores[][];

	public Bowling(int maxPlayerCount) {
		super(maxPlayerCount);

		scores = new int[maxPlayerCount][(getRoundCount() - 1) * 2 + 3];
	}

	@Override
	public String getName() {
		return "Bowling";
	}

	@Override
	public int getPinCount() {
		return 10;
	}

	@Override
	public int getRoundCount() {
		return 10;
	}

	@Override
	public int[] getScore(Player player) {
		if (!isValidPlayer(player)) {
			return null;
		}

		int score = 0;
		int[] output = new int[getRoundCount()];

		for (int round = 1; round < getRoundCount(); round++) {
			int roundScore = 0;

			for (int thro = 1; thro <= 2; thro++) {
				int index = getThrowIndex(round, thro);
				int pins = scores[player.getID()][index];

				score += pins;
				roundScore += pins;

				if (thro == 1 && pins == getPinCount()) {
					int nextPins = scores[player.getID()][index + 2];

					score += nextPins;

					if (nextPins == getPinCount() && round != 9) {
						score += scores[player.getID()][index + 4];
					} else {
						score += scores[player.getID()][index + 3];
					}

					break;
				}

				if (thro == 2 && roundScore == getPinCount()) {
					score += scores[player.getID()][index + 1];
				}
			}

			output[round - 1] = score;
		}

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

	@Override
	public boolean throwBall(int count) {
		if (!super.throwBall(count)) {
			return false;
		}

		int thro = getThrow() - 1;
		int index = getThrowIndex(getRound(), thro);
		int playerID = getActivePlayer().getID();

		scores[playerID][index] = count;

		if (getRound() == getRoundCount()) {
			if (getPinsLeft() == 0) {
				resetPins();
			} else if (thro == 2 && scores[playerID][getThrowIndex(getRound(), 1)] != 10) {
				nextPlayer();
			}

			if (thro == 3) {
				nextPlayer();
			}
		} else {
			boolean isStrike = thro == 1 && getPinsLeft() == 0;
			boolean isEnd = thro == 2;

			if (isStrike || isEnd) {
				nextPlayer();
			}
		}

		return true;
	}

	private int getThrowIndex(int round, int thro) {
		return (round - 1) * 2 + (thro - 1);
	}

}
