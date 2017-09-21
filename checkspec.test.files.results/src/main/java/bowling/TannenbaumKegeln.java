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


public class TannenbaumKegeln extends Game {

	private int[][] counts;
	private int state;

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
		if (!isValidPlayer(player)) {
			return null;
		}

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

				for (int value : scores) {
					score += value;
				}

				if (score < bestScore) {
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

		int playerID = getActivePlayer().getID();

		state += count;

		if (getThrow() == 3 || count == getPinCount()) {
			if (state > 0 && counts[playerID][state - 1] != 0) {
				counts[playerID][state - 1]--;
			}

			state = 0;

			if (hasPlayerWon()) {
				overwriteRound(Integer.MAX_VALUE);
			} else {
				nextPlayer();
			}
		}

		return true;
	}

	private boolean hasPlayerWon() {
		for (int i : counts[getActivePlayer().getID()]) {
			if (i != 0) {
				return false;
			}
		}

		return true;
	}

}
