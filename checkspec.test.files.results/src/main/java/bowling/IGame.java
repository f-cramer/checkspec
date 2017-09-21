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



public interface IGame {

	Player addPlayer(String name);

	Player getActivePlayer();

	int getActivePlayerCount();

	int getMaxPlayerCount();

	String getName();

	int getPinCount();

	int getPinsLeft();

	Player getPlayer(int id);

	int getRound();

	int getRoundCount();

	int[] getScore(Player player);

	int getThrow();

	Player getWinner();

	boolean hasFinished();

	boolean hasStarted();

	boolean startGame();

	boolean throwBall(int count);
}
