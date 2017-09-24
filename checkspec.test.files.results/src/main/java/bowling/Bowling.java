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

	public Bowling(int maxPlayerCount) {
		super(maxPlayerCount);
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
	public int getRoundCount() {
		return 0;
		// implementation
	}

	@Override
	public int[] getScore(Player player) {
		return new int[0];
		// implementation
	}

	@Override
	public Player getWinner() {
		return null;
		// implementation
	}

	@Override
	public boolean throwBall(int count) {
		return false;
		// implementation
	}
}
