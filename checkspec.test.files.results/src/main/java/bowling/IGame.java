package bowling;

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
