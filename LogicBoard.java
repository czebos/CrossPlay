package CrossPlay;

import java.util.ArrayList;

/*
 * This is the LogicBoard class. It keeps track of all of the logic, and it acts as a node. Its main responsibilities are to check
 * available moves for both the human players and the computer players, theoretically evaluate positions, actually evaluate the board, check
 * the end of the game, evaluate the UCT(MonteCarlo value), keep track of all of the node's information and keep track of which pieces have moved
 * in specific nodes. This class takes care of all logic functions of the board and stores a lot information about the board itself.
 */
public class LogicBoard {

	private Piece[][] _board;
	private Boolean _isBlacksTurn, _blackWon, _whiteWon;
	private Piece _pieceToMove, _deletedPiece;
	private int _totalWins, _totalPlayouts, _totalLosses;
	private ArrayList<LogicBoard> _childNodes;
	private LogicBoard _parent;
	private ArrayList<Piece> _pieces;
	private ArrayList<Boolean> _movedOnce;
	private UndoStack _undoStack;

	// LogicBoard normal constructor. Instantiates all instance variables
	public LogicBoard(Piece[][] board) {
		_undoStack = new UndoStack();
		_blackWon = false;
		_whiteWon = false;
		_board = board;
		_childNodes = new ArrayList<LogicBoard>();
		_pieces = new ArrayList<Piece>();
		_movedOnce = new ArrayList<Boolean>();
	}

	// LogicBoard copy constructor. Transfers primitive data types. With the array
	// and the array list,
	// it manually copies over the data.
	public LogicBoard(LogicBoard board) {
		this.transferVariables(board);
		for (int i = 0; i < Constants.COLUMNS; i++)
			for (int j = 0; j < Constants.ROWS; j++)
				_board[i][j] = board._board[i][j];
		for (int i = 0; i < board.getChildren().size(); i++) {
			_childNodes.add(board.getChildren().get(i));
		}
	}

	// Transfers primitive data types and instantiates non-primitive data types.
	private void transferVariables(LogicBoard board) {
		_childNodes = new ArrayList<LogicBoard>();
		_pieces = new ArrayList<Piece>();
		_movedOnce = new ArrayList<Boolean>();
		_board = new Piece[Constants.COLUMNS][Constants.ROWS];
		_whiteWon = board._whiteWon;
		_blackWon = board._blackWon;
		_isBlacksTurn = board._isBlacksTurn;
		_totalWins = board._totalWins;
		_totalPlayouts = board._totalPlayouts;
		_totalLosses = board._totalLosses;

	}

	// Checks for all black moves that involve taking another piece.
	private void checkForBlackTakes(ArrayList<Move> movesAvailable) {
		for (int i = 0; i < Constants.COLUMNS; i++)
			for (int j = 0; j < Constants.ROWS; j++) // For all spots on the board
				if (_board[i][j] != null) { // If a piece is in the space...
					if (!_board[i][j].isTopPiece()) { // If the piece is black
						if (this.getTurn()) { // If its blacks turn
							if (!(i == 3) && (i < 3 && j > 0)) { // If its not on the right edge
								if (!(_board[i + 1][j - 1] == null)) // If a piece is to the top right
									if (_board[i + 1][j - 1].isTopPiece()) { // If the piece is white
										movesAvailable.add(new Move(_board[i][j], i + 1, j - 1, i, j)); // Add this move
									}
							}
							if (!(i == 0) && (i > 0 && j > 0)) // If its not on the left edge
								if (!(_board[i - 1][j - 1] == null)) // If a piece is to the top left
									if (_board[i - 1][j - 1].isTopPiece()) { // If its a white piece
										movesAvailable.add(new Move(_board[i][j], i - 1, j - 1, i, j)); // Add this move

									}
						}
					}

				}

	}

	// Identical to checkForBlack takes, but reversed color.
	private void checkForWhiteTakes(ArrayList<Move> movesAvailable) {
		for (int i = 0; i < Constants.COLUMNS; i++) {
			for (int j = 0; j < Constants.ROWS; j++) {
				if (!(_board[i][j] == null)) {
					if (_board[i][j].isTopPiece()) {
						if (!this.getTurn()) {
							if (!(i == 3) && j < 7 && i < 3) {
								if (!(_board[i + 1][j + 1] == null)) {
									if (!_board[i + 1][j + 1].isTopPiece()) {
										movesAvailable.add(new Move(_board[i][j], i + 1, j + 1, i, j));
									}
								}
							}
						}

						if (!this.getTurn()) {
							if (!(i == 0) && j < 7 && i > 0) {
								if (!(_board[i - 1][j + 1] == null)) {
									if (!_board[i - 1][j + 1].isTopPiece()) {
										movesAvailable.add(new Move(_board[i][j], i - 1, j + 1, i, j));
									}
								}
							}
						}

					}

				}
			}
		}
	}

	// Creates an array list of all moves. When the computer evaluates moves,
	// whether or not the piece
	// has moved twice is kept track by the Piece class, so restoring the board
	// state changes the pieces
	// back to whether or not they could move twice.
	public ArrayList<Move> checkForMoves() {
		this.restoreBoardState();
		ArrayList<Move> movesAvailable = new ArrayList<Move>();
		for (int i = 0; i < Constants.COLUMNS; i++) {
			for (int j = 0; j < Constants.ROWS; j++) { // For all pieces on the board
				if (_board[i][j] != null) { // If its not null
					if (_board[i][j].isTopPiece()) { // If the piece is white
						if (!this.getTurn()) { // If its whites turn
							if (!_board[i][j].hasMovedOnce()) { // If the piece hasn't moved twice
								if (_board[i][j + 2] == null && _board[i][j + 1] == null) { // If both spaces in front
																							// are empty
									movesAvailable.add(new Move(_board[i][j], i, j + 2, i, j));
								}
								if (_board[i][j + 1] == null) { // If one square in front is empty
									movesAvailable.add(new Move(_board[i][j], i, j + 1, i, j));
								}
							} else if (_board[i][j + 1] == null) { // If one square in front is empty
								movesAvailable.add(new Move(_board[i][j], i, j + 1, i, j));
							}
							if (j == 0) { // If piece is on backmost row
								if (i > 0) // If its not the leftmost square
									if (_board[i - 1][j] == null)
										movesAvailable.add(new Move(_board[i][j], i - 1, j, i, j));
								if (i < 3) // If its not the rightmost square
									if (_board[i + 1][j] == null)
										movesAvailable.add(new Move(_board[i][j], i + 1, j, i, j));
							}
						}
					} else {
						if (this.getTurn()) { // If its blacks turn
							if (!_board[i][j].hasMovedOnce()) { // If the piece hasn't moved once
								if (_board[i][j - 2] == null && _board[i][j - 1] == null) { // If both spaces in front
																							// are empty
									movesAvailable.add(new Move(_board[i][j], i, j - 2, i, j));
								}
								if (_board[i][j - 1] == null) { // If one space in front is empty
									movesAvailable.add(new Move(_board[i][j], i, j - 1, i, j));
								}
							} else if (_board[i][j - 1] == null) { // If one space in front is empty
								movesAvailable.add(new Move(_board[i][j], i, j - 1, i, j));
							}
							if (j == 7) { // If its the backmost row
								if (i > 0) // If its not the leftmost square
									if (_board[i - 1][j] == null)
										movesAvailable.add(new Move(_board[i][j], i - 1, j, i, j));
								if (i < 3) // If its not the rightmost square
									if (_board[i + 1][j] == null)
										movesAvailable.add(new Move(_board[i][j], i + 1, j, i, j));
							}
						}
					}
				}
			}
		}
		this.checkForWhiteTakes(movesAvailable);
		this.checkForBlackTakes(movesAvailable);
		return movesAvailable;
	}

	// This is identical to the check for moves, except it is for the one piece that
	// the human has clicked.
	public ArrayList<Move> clickCheck() {
		ArrayList<Move> movesAvailable = new ArrayList<Move>();
		movesAvailable = this.checkForMoves();
		this.checkForWhiteTakes(movesAvailable);
		this.checkForBlackTakes(movesAvailable); // Got all moves
		for (int i = 0; i < movesAvailable.size(); i++) { // Deletes all moves that doesn't have to do with the piece
															// clicked
			if (_pieceToMove != movesAvailable.get(i).getPiece()) {
				movesAvailable.remove(i);
				i--;
			}
		}
		return movesAvailable;
	}

	// Evaluates the board based on the move passed. This method is called when the
	// final
	// decision is made.
	public void evaluateTurn(Move move) {
		LogicBoard board = new LogicBoard(this);
		_undoStack.stack(board);
		board.createTempBoardState();

		this.nextTurn();
		_board[move.getFromX()][move.getFromY()] = null;
		if (_board[move.getToX()][move.getToY()] != null) { // If spot piece is moving to is not null
			if (_board[move.getToX()][move.getToY()].isTopPiece()) { // If that piece is white
				if (_board[move.getToX()][0] == null) { // If the spot that the piece should get moved back to is null
					_board[move.getToX()][0] = _board[move.getToX()][move.getToY()]; // Set that spot to the piece taken
					_board[move.getToX()][0].setLocation(
							(move.getToX() + 1) * Constants.SQUARE_SIZE + Constants.X_OFFSET, Constants.TOP_Y_OFFSET); // Visually
																														// set
																														// it
																														// there
					_board[move.getToX()][0].resetFirstMove(); // It can now move again

				} else {
					_board[move.getToX()][move.getToY()].removeItself(move.getToX(), move.getToY());// otherwise remove
																									// it visually and
																									// keep track of
																									// where it was
					_undoStack.setDeletedPiece(_board[move.getToX()][move.getToY()]);
				}
			}

			else { // Same as above but with black
				if (_board[move.getToX()][7] == null) {
					_board[move.getToX()][7] = _board[move.getToX()][move.getToY()];
					_board[move.getToX()][7].setLocation(
							(move.getToX() + 1) * Constants.SQUARE_SIZE + Constants.X_OFFSET,
							Constants.BOTTOM_Y_OFFSET);
					_board[move.getToX()][7].resetFirstMove();

				} else {
					_board[move.getToX()][move.getToY()].removeItself(move.getToX(), move.getToY());
					_undoStack.setDeletedPiece(_board[move.getToX()][move.getToY()]);
				}
			}
		}
		_board[move.getToX()][move.getToY()] = move.getPiece();
		_childNodes.clear(); // Turn is evaluated, so it is now one of its children.
		move.getPiece().moved();
		this.checkEndGame();
	}

	// Temporary evaluation of the board. This is used for the SmartAI's to play out
	// different situations
	public void evaluatePosition(Move move) {
		this.restoreBoardState();
		this.nextTurn();
		_board[move.getFromX()][move.getFromY()] = null;
		if (_board[move.getToX()][move.getToY()] != null) {
			if (_board[move.getToX()][move.getToY()].isTopPiece()) {
				if (_board[move.getToX()][0] == null) {
					_board[move.getToX()][0] = _board[move.getToX()][move.getToY()];
					_board[move.getToX()][0].resetFirstMove();
				}
			}

			else {
				if (_board[move.getToX()][7] == null) {
					_board[move.getToX()][7] = _board[move.getToX()][move.getToY()];
					_board[move.getToX()][7].resetFirstMove();
				}
			}
		}
		_board[move.getToX()][move.getToY()] = move.getPiece();
		move.getPiece().moved();
		this.checkEndGame();
		_childNodes.clear();
		this.createTempBoardState(); // Creates the board state so that it can come back to the arrangement of piece
										// movement.
	}

	// Checks for the end of the game and sets the appropriate variables to the
	// value. Two ways of winning are
	// the opponent having no moves, or one of your pieces get to the other side.
	public Boolean checkEndGame() {
		for (int i = 0; i < Constants.COLUMNS; i++) // black gets to other side
			if (!(_board[i][0] == null))
				if (!_board[i][0].isTopPiece()) {
					_blackWon = true;
				}
		for (int i = 0; i < Constants.COLUMNS; i++) // white gets to other side
			if (!(_board[i][7] == null))
				if (_board[i][7].isTopPiece()) {
					_whiteWon = true;
				}
		Boolean hasMove = false;
		ArrayList<Move> movesAvailable = this.checkForMoves();
		if (movesAvailable.size() != 0)
			hasMove = true;
		if (!(_blackWon || _whiteWon) && !hasMove) { // If there are no moves and no one won yet
			if (_isBlacksTurn)
				_whiteWon = true;
			else
				_blackWon = true;
		}
		return (_whiteWon || _blackWon);
	}

	// Calculates the Upper Confidence interval for the AI. This formula was derived
	// by a famous mathematician
	// which balances promising nodes to exploring nodes. (Choosing between the best
	// moves or exploring new moves).
	public double getUCT() {
		double exploitation = ((double) _totalWins / ((double) _totalPlayouts + Constants.EPSILON));
		double exploration = ((double) (Math.sqrt(.1)
				* Math.sqrt((Math.log((double) _parent.getTotalPlayouts()) / (double) _totalPlayouts))));
		return exploitation + exploration + (double) (Constants.EPSILON * Math.random());
	}

	// Does the same but for the enemy
	public double getEnemyUCT() {
		double exploitation = ((double) _totalLosses / ((double) _totalPlayouts + Constants.EPSILON));
		double exploration = ((double) (Math.sqrt(.1)
				* Math.sqrt((Math.log((double) _parent.getTotalPlayouts()) / (double) _totalPlayouts))));
		return exploitation + exploration + (double) (Constants.EPSILON * Math.random());
	}

	public double getValueOfMove() {
		return ((double) _totalWins / (double) _totalPlayouts);
	}

	// Creates the arrangement of which pieces have moved once or not for this
	// particular board.
	public void createTempBoardState() {
		_pieces.clear();
		_movedOnce.clear();
		for (int i = 0; i < Constants.COLUMNS; i++)
			for (int j = 0; j < Constants.ROWS; j++)
				if (_board[i][j] != null) {
					_pieces.add(_board[i][j]);
					_movedOnce.add(_board[i][j].hasMovedOnce());
				}
	}

	// Restores the arrangement of which pieces have moved once or not for this
	// particular board.
	public void restoreBoardState() {
		for (int i = 0; i < _pieces.size(); i++) {
			_pieces.get(i).movedOnceSet(_movedOnce.get(i));
		}
	}

	public Piece[][] getBoard() {
		return _board;
	}

	public void setBoard(Piece[][] board) {
		_board = board;
	}

	public void nextTurn() {
		_isBlacksTurn = !_isBlacksTurn;
	}

	public void blackGoesFirst() {
		_isBlacksTurn = true;
	}

	public Boolean getTurn() {
		return _isBlacksTurn;
	}

	public void setClick(Piece piece) {
		_pieceToMove = piece;
	}

	public Piece getClick() {
		return _pieceToMove;
	}

	public Boolean checkWhiteWin() {
		return _whiteWon;
	}

	public Boolean checkBlackWin() {
		return _blackWon;
	}

	public void setParent(LogicBoard parent) {
		_parent = parent;
	}

	public LogicBoard getParent() {
		return _parent;
	}

	public void add(LogicBoard node) {
		_childNodes.add(node);
	}

	public ArrayList<LogicBoard> getChildren() {
		return _childNodes;
	}

	public int getTotalPlayouts() {
		return _totalPlayouts;
	}

	public void playout() {
		_totalPlayouts++;
	}

	public void win() {
		_totalWins++;
	}

	public void lose() {
		_totalLosses++;
	}

	public UndoStack getUndoStack() {
		return _undoStack;
	}

	public Piece getDeletedPiece() {
		return _deletedPiece;
	}

	public void setDeletedPiece(Piece piece) {
		_deletedPiece = piece;
	}
}
