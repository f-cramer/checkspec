package bowling;

/*-
 * #%L
 * CheckSpec Test Files
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

@SuppressWarnings("all")
public abstract class Game implements IGame {

	String name;
	int maxPlayer;
	int pins;
	int rounds;
	Player[] players;
	int currentRound;
	int currentThrow;
	Player currentPlayer;
	int remainingPins;
	boolean finishedRounds;
	boolean finishedPlayer;
	boolean gameStarted;
	int activePlayers;

	public Game(int max) {
		this.maxPlayer = max;
		players = new Player[max];
		currentRound = 1;
		currentThrow = 1;
		activePlayers = 0;
		finishedRounds = false;
		finishedPlayer = false;
		gameStarted = false;
	}

	@Override
	public abstract Player addPlayer(String name);

	public Player[] getActivePlayers() {
		return players;
	}

	@Override
	public Player getActivePlayer() {
		return currentPlayer;
	}

	public void setActivePlayer() {
		if (currentThrow == 1) {
			currentPlayer = currentPlayer;
		} else {
			if (currentPlayer == players[activePlayers - 1]) {
				currentThrow = 1;
				currentPlayer = players[0];
			} else {
				currentThrow = 1;
				int b = currentPlayer.getID();
				currentPlayer = players[b + 1];
			}
		}
	}

	@Override
	public int getActivePlayerCount() {
		return activePlayers;
	}

	@Override
	public int getMaxPlayerCount() {
		return this.maxPlayer;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getPinCount() {
		return pins;
	}

	@Override
	public int getPinsLeft() {
		return remainingPins;
	}

	@Override
	public Player getPlayer(int id) {
		for (int i = 0; i < players.length; i++) {
			int a = players[i].getID();
			if (a == id) {
				return players[i];
			}
		}
		return null;
	}

	@Override
	public int getRound() {
		return currentRound;
	}

	public void setRound() {
		if (players[0] == currentPlayer && currentThrow == 1) {
			currentRound = currentRound++;
			if (currentRound > rounds) {
				finishedRounds = true;
			}
		}
	}

	@Override
	public int getRoundCount() {
		return rounds;
	}

	@Override
	public abstract int[] getScore(Player player);

	@Override
	public int getThrow() {
		return currentThrow;

	}

	@Override
	public abstract Player getWinner();

	@Override
	public abstract boolean hasFinished();

	@Override
	public boolean hasStarted() {
		return gameStarted;
	}

	@Override
	public boolean startGame() {
		if (getActivePlayerCount() < 2) {
			System.err.println("not enough players");
			return false;
		} else if (hasStarted()) {
			System.err.println("Game has already started");
			return false;
		} else {
			gameStarted = true;
		}
		return true;
	}

	@Override
	public abstract boolean throwBall(int count);
}
