package CrossPlay;

import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/*
 * This is the Game class. This creates the board visually and sets up start of the pieces logically. It is also responsible for keeping track of who's
 * turn it is. It also resets the pieces visually when the undo button is clicked. At the end of the game it creates the label to notify the user. Its main
 * goal is to create visuals and have a class for the players to interact through.
 */
public class Game {

	private Boolean _canClick;
	private LogicBoard _logicBoard;
	private Player _playerOne, _playerTwo;
	private Pane _centerPane;
	private double _iterations;
	private Boolean _canPushButton;
	private Label _endGameLabel;

	// Game constructor. Sets up the game by creating the board visually, making the
	// players and starting the turns.
	public Game(Pane centerPane, Player playerOne, Player playerTwo, double iterations) {
		_canPushButton = true;
		_iterations = iterations;
		_centerPane = centerPane;
		this.setUpBoard();
		_playerOne = playerOne;
		_playerTwo = playerTwo;
		this.setUpPlayers();
		this.resetLabel();
		this.nextTurn();
	}

	private void setUpPlayers() {
		_canClick = true;
		_playerOne.setLogicBoard(_logicBoard);
		_playerTwo.setLogicBoard(_logicBoard);
	}

	// Makes the pieces visually, logically positions them, and makes black go first
	private void setUpBoard() {
		_logicBoard = new LogicBoard(new Piece[Constants.COLUMNS][Constants.ROWS]);
		this.setUpBoard(_centerPane, this.createStartPosition());
		this.createBoard();
		this.createPieces();
		_logicBoard.blackGoesFirst();
	}

	// Creates all of the squares visually
	private void createBoard() {
		for (int i = 1; i <= Constants.COLUMNS; i++)
			for (int j = 1; j <= Constants.ROWS; j++) {
				Rectangle square = new Rectangle(i * Constants.SQUARE_SIZE,
						Constants.NUMBER_X_OFFSET + (j * Constants.SQUARE_SIZE), Constants.SQUARE_SIZE,
						Constants.SQUARE_SIZE);
				square.setStroke(Color.BLACK);
				_centerPane.getChildren().add(square);
				square.toBack();
				if (i % 2 == 0) {
					if (j % 2 == 0)
						square.setFill(Color.WHITE);
					else
						square.setFill(Color.BEIGE);
				} else {
					if (j % 2 == 1)
						square.setFill(Color.WHITE);
					else
						square.setFill(Color.BEIGE);
				}
			}
	}

	// Visually positions the pieces
	private void createPieces() {
		_logicBoard.getBoard()[0][0].setLocation(Constants.X_OFFSET + (0 + 1) * Constants.SQUARE_SIZE,
				Constants.TOP_START);
		_logicBoard.getBoard()[1][0].setLocation(Constants.X_OFFSET + (1 + 1) * Constants.SQUARE_SIZE,
				Constants.TOP_START);
		_logicBoard.getBoard()[2][0].setLocation(Constants.X_OFFSET + (2 + 1) * Constants.SQUARE_SIZE,
				Constants.TOP_START);
		_logicBoard.getBoard()[3][0].setLocation(Constants.X_OFFSET + (3 + 1) * Constants.SQUARE_SIZE,
				Constants.TOP_START);

		_logicBoard.getBoard()[0][6].setLocation(Constants.X_OFFSET + (0 + 1) * Constants.SQUARE_SIZE,
				Constants.BOTTOM_START);
		_logicBoard.getBoard()[1][6].setLocation(Constants.X_OFFSET + (1 + 1) * Constants.SQUARE_SIZE,
				Constants.BOTTOM_START);
		_logicBoard.getBoard()[2][6].setLocation(Constants.X_OFFSET + (2 + 1) * Constants.SQUARE_SIZE,
				Constants.BOTTOM_START);
		_logicBoard.getBoard()[3][6].setLocation(Constants.X_OFFSET + (3 + 1) * Constants.SQUARE_SIZE,
				Constants.BOTTOM_START);
	}

	// check for accountability later
	public void setPieces(int times) {
		if (times != 0) {
			LogicBoard undoBoard = _logicBoard.getUndoStack().pop();
			for (int i = 0; i < Constants.COLUMNS; i++) {
				for (int j = 0; j < Constants.ROWS; j++) {
					if (_logicBoard.getBoard()[i][j] != null)
						for (int k = 0; k < Constants.COLUMNS; k++)
							for (int l = 0; l < Constants.ROWS; l++)
								if (undoBoard.getBoard()[k][l] != null)
									if (_logicBoard.getBoard()[i][j] == undoBoard.getBoard()[k][l])
										_logicBoard.getBoard()[i][j].setLocation(113 + (k * Constants.SQUARE_SIZE),
												Constants.TOP_START + (l * Constants.SQUARE_SIZE));
				}
			}
			if (undoBoard.getDeletedPiece() != null) {
				undoBoard.getDeletedPiece().setLocation(
						Constants.X_OFFSET + (0 + 1) * Constants.SQUARE_SIZE
								+ (undoBoard.getDeletedPiece().getRemovedX() * Constants.SQUARE_SIZE),
						Constants.SQUARE_SIZE * 2
								+ (undoBoard.getDeletedPiece().getRemovedY() * Constants.SQUARE_SIZE));

			}
			_logicBoard.setBoard(undoBoard.getBoard());
			undoBoard.restoreBoardState();
			this.setPieces(times - 1);
		}

	}

	// Starts the game, and sets the next turn
	public void nextTurn() {
		this.checkEndGame();
		if (_logicBoard.getTurn())
			_playerOne.makeMove(this);
		else
			_playerTwo.makeMove(this);
		
	}

	// Creates the starting pieces for the game.
	private void setUpBoard(Pane centerPane, int[][] _coordinates) {
		for (int j = 0; j < Constants.COLUMNS; j++) {
			_logicBoard.getBoard()[_coordinates[j][0]][_coordinates[j][1]] = new Piece(Color.SADDLEBROWN, centerPane,
					true);
			_logicBoard.getBoard()[_coordinates[j + Constants.COLUMNS][0]][_coordinates[j
					+ Constants.COLUMNS][1]] = new Piece(Color.BLACK, centerPane, false);
		}

	}

	// Creates the coordinates for the starting pieces
	private int[][] createStartPosition() {
		int[][] pieceCoordinates = new int[Constants.ROWS][2];
		for (int j = 0; j < Constants.COLUMNS; j++) {
			pieceCoordinates[j][0] = j;
			pieceCoordinates[j + Constants.COLUMNS][0] = j;
		}
		for (int j = 0; j < Constants.COLUMNS; j++) {
			pieceCoordinates[j][1] = 0;
			pieceCoordinates[j + Constants.COLUMNS][1] = 6;
		}
		return pieceCoordinates;
	}
	
	// This checks the end of the game. If the game is over, it returns the
	// appropriate
	// label that prints "X Wins!"
	public void checkEndGame() {
		if (_logicBoard.checkBlackWin() || _logicBoard.checkWhiteWin()) {
			if (_logicBoard.checkBlackWin())
				_endGameLabel = new Label("Black Wins!");
			if (_logicBoard.checkWhiteWin())
				_endGameLabel = new Label("White Wins!");
			_endGameLabel.setTranslateX(Constants.ENDGAME_X);
			_endGameLabel.setTranslateY(Constants.ENDGAME_Y);
			_endGameLabel.setScaleX(2.5);
			_endGameLabel.setScaleY(3.0);
			_centerPane.getChildren().add(_endGameLabel);
		}
	}

	// Gets the strength of the computer
	public int getIterations() {
		return (int) (Math.pow(_iterations, Constants.POWER) * Constants.MAX_ITERATIONS);
	}

	public Boolean getClick() {
		return _canClick;
	}

	public void canClick() {
		_canClick = true;
	}

	public void cantClick() {
		_canClick = false;
	}

	public Pane getPane() {
		return _centerPane;
	}

	public LogicBoard getLogicBoard() {
		return _logicBoard;
	}

	public void stopCLick() {
		_canPushButton = false;
	}

	public void allowCLick() {
		_canPushButton = true;
	}

	public Boolean getAllowance() {
		return _canPushButton;
	}

	public void resetLabel() {
		_endGameLabel = new Label();
	}

}
