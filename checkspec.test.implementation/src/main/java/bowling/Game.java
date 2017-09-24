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

	public Game(int max) {
	}

	@Override
	public abstract Player addPlayer(String name);

	public Player[] getActivePlayers() {
		return new Player[0];
		// implementation
	}

	@Override
	public Player getActivePlayer() {
		return null;
		// implementation
	}

	public void setActivePlayer() {
		// implementation
	}

	@Override
	public int getActivePlayerCount() {
		return 0;
		// implementation
	}

	@Override
	public int getMaxPlayerCount() {
		return 0;
		// implementation
	}

	@Override
	public String getName() {
		return "";
		// implementation
	}

	@Override
	public int getPinCount() {
		return 0;
		// implementation
	}

	@Override
	public int getPinsLeft() {
		return 0;
		// implementation
	}

	@Override
	public Player getPlayer(int id) {
		return null;
		// implementation
	}

	@Override
	public int getRound() {
		return 0;
		// implementation
	}

	public void setRound() {
		// implementation
	}

	@Override
	public int getRoundCount() {
		return 0;
		// implementation
	}

	@Override
	public abstract int[] getScore(Player player);

	@Override
	public int getThrow() {
		return 0;
		// implementation

	}

	@Override
	public abstract Player getWinner();

	@Override
	public abstract boolean hasFinished();

	@Override
	public boolean hasStarted() {
		return false;
		// implementation
	}

	@Override
	public boolean startGame() {
		return false;
		// implementation
	}

	@Override
	public abstract boolean throwBall(int count);
}
