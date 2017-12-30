package CrossPlay;

// This is the Player interface. This must be implemented by all classes that actually play the game.
public interface Player {
	public void makeMove(Game game);
	public void setLogicBoard(LogicBoard logicBoard);
}
