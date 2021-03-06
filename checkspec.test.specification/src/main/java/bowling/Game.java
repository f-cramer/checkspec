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

public abstract class Game implements IGame {

	public Game(int maxPlayerCount) {
		// implementation
	}

	protected void overwriteRound(int newRound) {
		// implementation
	}

	@Override
	public Player addPlayer(String name) {
		return null;
		// implementation
	}

	@Override
	public Player getActivePlayer() {
		return null;
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

	@Override
	public int getThrow() {
		return 0;
		// implementation
	}

	@Override
	public boolean hasFinished() {
		return false;
		// implementation
	}

	@Override
	public boolean hasStarted() {
		return false;
		// implementation
	}

	protected boolean isValidPlayer(Player player) {
		return false;
	}

	protected void nextPlayer() {
		// implementation
	}

	protected void nextRound() {
		// implementation
	}

	protected void resetPins() {
		// implementation
	}

	@Override
	public boolean startGame() {
		return false;
		// implementation
	}

	@Override
	public boolean throwBall(int count) {
		return false;
		// implementation
	}
}
