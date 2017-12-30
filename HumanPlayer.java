package CrossPlay;

import java.util.ArrayList;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
 * This is the HumanPlayer class. It implements the Player interface, meaning it can play the game. It is responsible for playing the game
 * with a human as the controller. It allows the player to click on pieces, and then move the pieces to the next move if the move is legal.
 * It also highlights the squares, and moves the piece after a legal click. It deals with all human interaction with the game.
 */
public class HumanPlayer implements Player {

	private Rectangle[][] _boardOfRectangles;
	private ClickMoveHandler[][] _handlers;
	private LogicBoard _logicBoard;
	private Game _game;
	private ArrayList<Move> _movesAvailable;

	// Constructor for HumanPlayer.
	public HumanPlayer() {
		_boardOfRectangles = new Rectangle[Constants.COLUMNS][Constants.ROWS];
		_handlers = new ClickMoveHandler[Constants.COLUMNS][Constants.ROWS];
		_movesAvailable = new ArrayList<Move>();
	}

	// Implemented method from player that makes the actual move
	@Override
	public void makeMove(Game game) {
		_game = game;
		this.setUpSquares();
		this.allowPieceMovement();
	}

	// Makes transparent squares that allow the person to click on, correlating to
	// the postion of the pieces on the board
	private void setUpSquares() {
		for (int i = 1; i <= Constants.COLUMNS; i++)
			for (int j = 1; j <= Constants.ROWS; j++) {
				Rectangle square = new Rectangle(i * Constants.SQUARE_SIZE,
						Constants.Y_OFFSET + j * Constants.SQUARE_SIZE, Constants.SQUARE_SIZE, Constants.SQUARE_SIZE);
				_boardOfRectangles[i - 1][j - 1] = square;
				_game.getPane().getChildren().add(square);
				square.setFill(Color.TRANSPARENT);
			}
	}

	public void setLogicBoard(LogicBoard logicBoard) {
		_logicBoard = logicBoard;
	}

	// Creates individual handlers for every square
	public void allowPieceMovement() {
		for (int i = 0; i < Constants.COLUMNS; i++) {
			for (int j = 0; j < Constants.ROWS; j++) {
				_handlers[i][j] = new ClickMoveHandler(i, j);
				_boardOfRectangles[i][j].setOnMouseClicked(_handlers[i][j]);
			}
		}
	}

	// This class is responsible for handling all of the clicks, and their actions.
	// Some examples of this are making squares black and green, setting the
	// pieceToMove, getting the available moves for that specific piece, and allowing clicks
	private class ClickMoveHandler implements EventHandler<MouseEvent> {

		private Timeline _timeline;
		private int _x, _y;

		// Constructor for the private class. _x and _y are the location of the square.
		public ClickMoveHandler(int x, int y) {
			_x = x;
			_y = y;
		}

		// Handles the clicks. Checks if the click is on a piece, and selects it if so.
		// If the next click is an available move for the selected piece, evaluates that
		// move.
		@Override
		public void handle(MouseEvent event) {
			if (_game.getClick()) { // If the player may click
				if (_logicBoard.getBoard()[_x][_y] != null) { // If there is a piece
					if (_logicBoard.getClick() == null) { // If no Piece is selected
						_logicBoard.setClick(_logicBoard.getBoard()[_x][_y]);
						_movesAvailable = _logicBoard.clickCheck();
					} else {
						// The next 2 statements allow the human player to interact with enemy pieces.
						// Otherwise, the piece would just select the opponents piece and not make the move.
						if (!(_logicBoard.getClick().isTopPiece() != _logicBoard.getBoard()[_x][_y].isTopPiece()
								&& _logicBoard.getClick().isTopPiece() != _logicBoard.getTurn())) {
							_logicBoard.setClick(_logicBoard.getBoard()[_x][_y]);
							_movesAvailable = _logicBoard.clickCheck();
						}
						if (!(_logicBoard.getClick().isTopPiece() != _logicBoard.getBoard()[_x][_y].isTopPiece()
								|| _logicBoard.getClick().isTopPiece() != _logicBoard.getTurn())) {
							_logicBoard.setClick(_logicBoard.getBoard()[_x][_y]);
							_movesAvailable = _logicBoard.clickCheck();
						}

					}
				}

			}
			if (_game.getClick()) { // If the player can click
				for (int a = 0; a < Constants.COLUMNS; a++) {
					for (int b = 0; b < Constants.ROWS; b++)
						if (_logicBoard.getBoard()[a][b] != null) // If the spot is not null
							if (_movesAvailable.size() != 0) // If there is a move
								if (_movesAvailable.get(0).getPiece().equals(_logicBoard.getBoard()[a][b])) // Makes
																											// sure the
																											// piece
																											// being
																											// moved is
																											// the piece
																											// selected
									for (int k = 0; k < _movesAvailable.size(); k++)
										if (_movesAvailable.get(k).getToX() == _x
												&& _movesAvailable.get(k).getToY() == _y) { // If the piece coordinates
																							// match the move
											_timeline = _movesAvailable.get(k).getPiece().getTimeline();
											_movesAvailable.get(k).getPiece().moveSpaces(_x - a, _y - b);
											_game.cantClick();
											_game.stopCLick();
											_timeline.setOnFinished(new MovePieceHandle(_movesAvailable.get(k))); // Move
																													// this
																													// piece

										}

				}

				if (_game.getClick()) {
					for (int i = 0; i < Constants.COLUMNS; i++) {
						for (int j = 0; j < Constants.ROWS; j++) {
							_boardOfRectangles[i][j].setStroke(Color.BLACK);
						}
					}

					if (_logicBoard.getBoard()[_x][_y] != null) {
						_boardOfRectangles[_x][_y].toFront();
						_boardOfRectangles[_x][_y].setStroke(Color.RED);
					}
				}
				event.consume();

			}
		}
	}

	// This class is responsible for moving the piece logically and taking care of
	// handlers and turns.
	// This is after the player has decided on a move. It evaluates it and prepares
	// the handlers.
	private class MovePieceHandle implements EventHandler<ActionEvent> {

		private Move _move;

		public MovePieceHandle(Move move) {
			_move = move;
		}

		// This removes the handlers so that the human may not make a move during the
		// opponents turn. Then it checks for
		// a winner. If there is no winner, then the next turn is called.
		@Override
		public void handle(ActionEvent event) {
			_game.allowCLick();
			_logicBoard.evaluateTurn(_move);
			for (int i = 0; i < Constants.COLUMNS; i++)
				for (int j = 0; j < Constants.ROWS; j++)
					if (!(_handlers[i][j] == null)) {
						_boardOfRectangles[i][j].removeEventHandler(MouseEvent.MOUSE_CLICKED, _handlers[i][j]);
						_handlers[i][j] = null;
					}
			if (!(_logicBoard.checkWhiteWin() || _logicBoard.checkBlackWin())) {
				_game.nextTurn();
				_game.canClick();
			} else {
				_game.cantClick();
				_game.checkEndGame();
			}
			event.consume();
		}
	}
}
