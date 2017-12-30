package CrossPlay;

// This is the move class. This keeps track of distinct moves that the players
// and the computer can make. This is simply a class for keeping track of data.
public class Move {

	private int _toX, _toY, _fromX, _fromY;
	private Piece _piece;

	// Move class constructor. Sets the values of the move
	public Move(Piece piece, int toX, int toY, int fromX, int fromY) {
		_piece = piece;
		_toX = toX;
		_toY = toY;
		_fromX = fromX;
		_fromY = fromY;
	}

	public int getToX() {
		return _toX;
	}

	public int getToY() {
		return _toY;
	}

	public int getFromX() {
		return _fromX;
	}

	public int getFromY() {
		return _fromY;
	}

	public Piece getPiece() {
		return _piece;
	}

}
