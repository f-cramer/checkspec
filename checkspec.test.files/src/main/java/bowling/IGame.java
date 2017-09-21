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

/**
 * Models a bowling-style game
 * 
 * @author Nicolas Weber
 */
public interface IGame {
    /**
     * Adds a player to the game
     * 
     * @param name the name of the player
     * @return the player object, or null if an error occurred
     */
    Player addPlayer(String name);
    
    /**
     * Returns the current player
     * 
     * @return the current player
     */
    Player getActivePlayer();
    
    /**
     * Returns the actual number of active players
     * 
     * @return the actual number of active players
     */
    int getActivePlayerCount();
    
    /**
     * Returns the maximum number of active players
     * 
     * @return the maximum number of active players
     */
    int getMaxPlayerCount();
    
    /**
     * Returns the name of this game mode
     * 
     * @return the name of the game mode
     */
    String getName();

    /**
     * Returns the number of pins in this game mode
     * 
     * @return the number of pins
     */
    int getPinCount();

    /**
     * Returns the number of pins currently standing 
     * 
     * @return the number of pins still standing
     */
    int getPinsLeft();

    /**
     * Returns the player with the given ID, or null if there is no such player
     *
     * @param id the ID of the player we are looking for
     * @return the player with ID id or null if there is not such player
     */
    Player getPlayer(int id);	 

    /**
     * Returns the current round
     * 
     * @return the round, starting with 1
     */
    int getRound();
    
    /**	
     * Returns the maximum number of rounds
     * 
     * @return the maximum number of rounds
     */
    int getRoundCount();
    
    /**
     * Returns the score for a given player. The format depends on the game mode.
     * 
     * @param player the given player
     * @return the score of the selected player
     */
    int[] getScore(Player player);

    /**
     * Returns the current throw number
     * 
     * @return the throw number, starting with 1
     */
    int getThrow();

    /**
     * Returns the player who has won the game.
     * 
     * @return player who won, or null if an error occurred
     */
    Player getWinner();

    /**
     * Indicates if the game has been finished
     * 
     * @return true if the game is complete
     */
    boolean hasFinished();

    /**
     * Indicates if the game has been started
     * 
     * @return true if the game is running
     */
    boolean hasStarted();

    /**
     * Starts the game
     * 
     * @return true if the game was started, false if any error occurred, e.g., not
     *         enough players
     */
    boolean startGame();
    
    /**
     * Throws a ball
     * 
     * @param count
     * @return true if the throw was valid, false if any error occurred, e.g., game
     *         not started, invalid count, ...
     */
    boolean throwBall(int count);
}
