package bowling;

/**
 * Represents a player in a bowling-like game
 * 
 * @author Nicolas Weber
 */
public class Player {
	/**
	 * Player name
	 */
	private String name;

	/**
	 * Player ID
	 */
	private int id;

	/**
	 * Initializes a player
	 * 
	 * @param playerName the player's name
	 * @param playerID the player's ID
	 */
	public Player(String playerName, int playerID) {
		name = playerName;
		id = playerID;
	}

	/**
	 * returns the player name
	 * 
	 * @return the player's name
	 */
	public String getName() {
		return name;
	}

	/**
	 * returns the player ID
	 * 
	 * @return the player's ID
	 */
	public int getID() {
		return id;
	}
}
