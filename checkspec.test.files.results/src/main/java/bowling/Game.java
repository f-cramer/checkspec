package bowling;

public abstract class Game implements IGame {

	private int activePlayerCount;
	private int activePlayerID;
	private int currentRound;
	private int currentThrow;
	private boolean hasStarted;
	private Player players[];
	private int pinsLeft;

	public Game(int maxPlayerCount) {
		players = new Player[maxPlayerCount];
		activePlayerCount = 0;
		hasStarted = false;
		activePlayerID = 0;
		currentRound = 1;
		currentThrow = 1;
		resetPins();
	}

	protected void overwriteRound(int newRound) {
		currentRound = newRound;
	}

	@Override
	public Player addPlayer(String name) {
		if (activePlayerCount == players.length) {
			System.err.println("max number of players reached");
			return null;
		}

		if (hasStarted()) {
			System.err.println("unable to add more players once game is started");
			return null;
		}

		Player player = new Player(name, activePlayerCount++);
		players[player.getID()] = player;
		return player;
	}

	@Override
	public Player getActivePlayer() {
		return getPlayer(activePlayerID);
	}

	@Override
	public int getActivePlayerCount() {
		return activePlayerCount;
	}

	@Override
	public int getMaxPlayerCount() {
		return players.length;
	}

	@Override
	public int getPinsLeft() {
		return pinsLeft;
	}

	@Override
	public Player getPlayer(int id) {
		if (id >= getActivePlayerCount()) {
			return null;
		}
		return players[id];
	}

	@Override
	public int getRound() {
		return currentRound;
	}

	@Override
	public int getThrow() {
		return currentThrow;
	}

	@Override
	public boolean hasFinished() {
		return getRound() > getRoundCount();
	}

	@Override
	public boolean hasStarted() {
		return hasStarted;
	}

	protected boolean isValidPlayer(Player player) {
		if (player == null) {
			return false;
		}

		if (player.getID() >= players.length) {
			return false;
		}

		return players[player.getID()] == player;
	}

	protected void nextPlayer() {
		activePlayerID = (activePlayerID + 1) % getActivePlayerCount();
		currentThrow = 1;
		resetPins();

		if (activePlayerID == 0) {
			nextRound();
		}
	}

	protected void nextRound() {
		currentRound++;
	}

	protected void resetPins() {
		pinsLeft = getPinCount();
	}

	@Override
	public boolean startGame() {
		if (hasStarted()) {
			System.err.println("cannot start game, as it is already started.");
			return false;
		}

		if (getActivePlayerCount() < 2) {
			System.err.println("there have to be at least 2 active players");
			return false;
		}

		hasStarted = true;
		return true;
	}

	@Override
	public boolean throwBall(int count) {
		if (count < 0) {
			System.err.println("negative counts not possible");
			return false;
		}

		if (count > getPinsLeft()) {
			System.err.println("more pins hit than possible");
			return false;
		}

		if (!hasStarted()) {
			System.err.println("cannot throw ball if game is not started");
			return false;
		}

		if (hasFinished()) {
			System.err.println("cannot throw ball if game is already finished");
			return false;
		}

		pinsLeft -= count;
		currentThrow++;
		return true;
	}

}
