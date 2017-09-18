package bowling;

public class Player {

	private String name;

	private int id;

	public Player(String playerName, int playerID) {
		name = playerName;
		id = playerID;
	}

	public String getName() {
		return name;
	}

	public int getID() {
		return id;
	}
}
