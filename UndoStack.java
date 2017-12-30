package CrossPlay;

import java.util.ArrayList;

/*
 * This is the UndoStack class. Its sole purpose is to be the stack. This is used
 * to undo multiple times.
 */
public class UndoStack {

	ArrayList<LogicBoard> _boards;

	public UndoStack() {
		_boards = new ArrayList<LogicBoard>();
	}

	// Gets the most recent node put on the stack and removes it and returns it.
	public LogicBoard pop() {
		LogicBoard topOfStack = _boards.get(_boards.size() - 1);
		_boards.remove(_boards.get(_boards.size() - 1));
		return topOfStack;
	}

	// Puts the node on the stack
	public void stack(LogicBoard logicBoard) {
		_boards.add(logicBoard);
	}

	// If a piece was deleted, then set the piece deleted
	public void setDeletedPiece(Piece piece) {
		_boards.get(_boards.size() - 1).setDeletedPiece(piece);
	}

	// Gets the stack
	public ArrayList<LogicBoard> getStack() {
		return _boards;
	}

}
