package CrossPlay;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/*
 * This is the PaneOrganizer class. This class deals with move of the button interaction and visuals. This class
 * creates and sets the location of different panes, visuals , and interactive things. It creates the title, the letters
 * and numbers around the board, the buttons, the instructions, and the slider and its words. This class takes care of most of
 * the front-end and GUI.
 */
public class PaneOrganizer {

	private Boolean _canPushButton, _firstGame;
	private BorderPane _root;
	private Player _playerOne, _playerTwo;
	private Pane _centerPane, _rightPane;
	private Game _game;
	private Slider _slider;
	private Label _title;
	private Piece _piece1, _piece2;
	private ToggleGroup _player1, _player2;

	/*
	 * This is the PaneOrganizer Constructor. It calls the methods that sets up the
	 * panes, the variables, and image of instructions.
	 */
	public PaneOrganizer() {
		_firstGame = true;
		this.instantiateVariables();
		_root.setStyle("-fx-background-color: white;");
		_root.setRight(this.setUpRightPane());
		_root.setCenter(_centerPane);
		this.setUpCenterPane();
		this.setUpPieces();
		this.setUpInstructions();
	}

	// Instantiates all the needed variables
	private void instantiateVariables() {
		_player1 = new ToggleGroup();
		_player2 = new ToggleGroup();
		_canPushButton = true;
		_centerPane = new Pane();
		_root = new BorderPane();
		_playerOne = new HumanPlayer();
		_playerTwo = new HumanPlayer();
	}

	// Sets up the instructions image in the right pane
	private void setUpInstructions() {
		Image instruction = new Image(
				"https://image.ibb.co/mo3ivR/instructions.png");
		ImageView imageView = new ImageView(instruction);
		imageView.setFitWidth(200);
		imageView.setFitHeight(450);
		_rightPane.getChildren().add(imageView);
		imageView.setY(Constants.IMAGE_Y_OFFSET);
	}

	// Creates the pieces in the CrossPlay title
	private void setUpPieces() {
		_piece1 = new Piece(Color.SADDLEBROWN, _centerPane, true);
		_piece2 = new Piece(Color.BLACK, _centerPane, false);
		_piece1.setLocation(Constants.PIECE_X_OFFSET, Constants.SQUARE_SIZE);
		_piece2.setLocation(Constants.PIECE_Y_OFFSET, Constants.SQUARE_SIZE);
	}

	// Sets up all the letters around the board
	public void setUpLetters() {
		Label a = new Label("A");
		Label b = new Label("B");
		Label c = new Label("C");
		Label d = new Label("D");
		HBox letters = new HBox(Constants.SQUARE_SIZE -5);
		letters.getChildren().addAll(a,b,c,d);
		letters.setTranslateX(Constants.PIECE_X_OFFSET);
		letters.setTranslateY(Constants.Y_OFFSET_2);

		a.setScaleX(2.0);
		b.setScaleX(2.0);
		c.setScaleX(2.0);
		d.setScaleX(2.0);
		a.setScaleY(2.0);
		b.setScaleY(2.0);
		c.setScaleY(2.0);
		d.setScaleY(2.0);

		Label one = new Label("1");
		Label two = new Label("2");
		Label three = new Label("3");
		Label four = new Label("4");
		Label five = new Label("5");
		Label six = new Label("6");
		Label seven = new Label("7");
		Label eight = new Label("8");
		
		VBox numbers = new VBox(Constants.SQUARE_SIZE - 17);
		numbers.getChildren().addAll(one, two, three, four, five, six, seven, eight);
		numbers.setTranslateX(Constants.NUMBER_X_OFFSET);
		numbers.setTranslateY(Constants.ONE_Y_OFFSET);

		one.setScaleX(2.0);
		two.setScaleX(2.0);
		three.setScaleX(2.0);
		four.setScaleX(2.0);
		five.setScaleX(2.0);
		six.setScaleX(2.0);
		seven.setScaleX(2.0);
		eight.setScaleX(2.0);
		one.setScaleY(2.0);
		two.setScaleY(2.0);
		three.setScaleY(2.0);
		four.setScaleY(2.0);
		five.setScaleY(2.0);
		six.setScaleY(2.0);
		seven.setScaleY(2.0);
		eight.setScaleY(2.0);

		_centerPane.getChildren().addAll(letters, numbers);
	}

	// Sets up all of the buttons on the right pane. These buttons create which
	// player is which and starts/restarts the game.
	private Pane setUpRightPane() {
		VBox player1 = new VBox(20);
		VBox player2 = new VBox(20);
		
		Label player1Label = new Label("Player 1");
		player1Label.setScaleX(1.5);
		player1Label.setScaleY(1.5);
		player1Label.setUnderline(true);

		Label player2Label = new Label("Player 2");
		player2Label.setScaleX(1.5);
		player2Label.setScaleY(1.5);
		player2Label.setUnderline(true);

		RadioButton computerButton1 = new RadioButton("Win AI");
		computerButton1.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		computerButton1.setOnAction(new PlayerHandler(1, new SmartAI(true)));
		computerButton1.setToggleGroup(_player1);

		RadioButton computerButton2 = new RadioButton("Win AI");
		computerButton2.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		computerButton2.setOnAction(new PlayerHandler(2, new SmartAI(true)));
		computerButton2.setToggleGroup(_player2);

		RadioButton playerButton1 = new RadioButton("Player");
		playerButton1.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		playerButton1.setOnAction(new PlayerHandler(1, new HumanPlayer()));
		playerButton1.setSelected(true);
		playerButton1.setToggleGroup(_player1);

		RadioButton playerButton2 = new RadioButton("Player");
		playerButton2.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		playerButton2.setOnAction(new PlayerHandler(2, new HumanPlayer()));
		playerButton2.setSelected(true);
		playerButton2.setToggleGroup(_player2);

		RadioButton worstComputer = new RadioButton("Losing AI");
		worstComputer.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		worstComputer.setOnAction(new PlayerHandler(1, new SmartAI(false)));
		worstComputer.setToggleGroup(_player1);

		RadioButton worstComputer1 = new RadioButton("Losing AI");
		worstComputer1.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		worstComputer1.setOnAction(new PlayerHandler(2, new SmartAI(false)));
		worstComputer1.setToggleGroup(_player2);

		Button gameStart = new Button("Start");
		gameStart.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		gameStart.setOnAction(new GameHandler());

		Button exitButton = new Button("Quit");
		exitButton.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		exitButton.setOnAction(new QuitHandler());

		Button undoButton = new Button("Undo");
		undoButton.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		undoButton.setOnAction(new UndoHandler());

		Button helpButton = new Button("Give Hint");
		helpButton.setPrefSize(Constants.SQUARE_SIZE, Constants.Y_BUTTON_SIZE);
		helpButton.setOnAction(new HelpHandler());
		
		player1.getChildren().addAll(player1Label, playerButton1, computerButton1, worstComputer, gameStart, helpButton);
		player2.getChildren().addAll(player2Label, playerButton2, computerButton2, worstComputer1, exitButton, undoButton);
		
		player1.setTranslateX(Constants.LEFT_X_BUTTON);
		player2.setTranslateX(Constants.RIGHT_X_BUTTON);
		player1.setTranslateY(Constants.PLAYER_Y);
		player2.setTranslateY(Constants.PLAYER_Y);

		_rightPane = new Pane();
		_rightPane.setPrefSize(Constants.RIGHTPANE_X, Constants.RIGHTPANE_Y);
		_rightPane.setStyle("-fx-background-color: goldenrod;");
		_rightPane.getChildren().addAll(player1, player2);
		return _rightPane;
	}

	// Sets up the centerPane. Makes the CrossPlay title, the slider and the sliders
	// titles.
	private void setUpCenterPane() {
		_title = new Label("CrossPlay");
		_title.setScaleX(3.0);
		_title.setScaleY(3.0);
		_title.setTranslateX(Constants.TITLE_X);
		_title.setTranslateY(Constants.TITLE_Y);
		_slider = new Slider(.15, 1, .55);
		_slider.setShowTickMarks(true);
		_slider.setMajorTickUnit(0.25f);
		_slider.setBlockIncrement(0.1f);
		_slider.setTranslateX(Constants.SLIDER_X);
		_slider.setTranslateY(Constants.SLIDER_Y);
		Label difficulty = new Label("Simulation Depth");
		difficulty.setTranslateX(Constants.DIFFICULTY_X);
		difficulty.setTranslateY(Constants.DIFFICULTY_Y);
		difficulty.setScaleX(2.0);
		difficulty.setScaleY(2.0);
		Label easy = new Label("Easy");
		Label hard = new Label("Hard");
		easy.setTranslateX(Constants.DIFFICULTY_LABEL_X);
		easy.setTranslateY(Constants.DIFFICULTY_LABEL_Y);
		hard.setTranslateX(Constants.DIFFICULTY_Y);
		hard.setTranslateY(Constants.DIFFICULTY_LABEL_Y);
		_centerPane.getChildren().addAll(_slider, _title, easy, hard, difficulty);
		_slider.setScaleX(2.0);
		_slider.setScaleY(2.0);
	}

	public Pane getRoot() {
		return _root;
	}

	// This is the PlayerHandler class. It is responsible for making the players set
	// to the appropriate class.
	private class PlayerHandler implements EventHandler<ActionEvent> {

		private Player _playerType;
		private int _playerNumber;

		public PlayerHandler(int playerNumber, Player playerType) {
			_playerNumber = playerNumber;
			_playerType = playerType;
		}

		// Sets the appropriate player to the appropriate type
		@Override
		public void handle(ActionEvent event) {
			if (_playerNumber == 1)
				_playerOne = _playerType;
			else
				_playerTwo = _playerType;
			event.consume();
		}
	}

	// This is the GameHandler class. It is responsible for starting and restarting
	// the game.
	private class GameHandler implements EventHandler<ActionEvent> {

		// Clears everything visually from the pane. Creates a new Game and sets it
		// center.
		@Override
		public void handle(ActionEvent event) {
			if(_firstGame || _game.getAllowance()) {
			_centerPane.getChildren().clear();
			PaneOrganizer.this.setUpLetters();
			_piece1 = new Piece(Color.SADDLEBROWN, _centerPane, true);
			_piece2 = new Piece(Color.BLACK, _centerPane, true);
			_piece1.setLocation(Constants.PIECE_X_OFFSET, Constants.SQUARE_SIZE);
			_piece2.setLocation(Constants.PIECE_Y_OFFSET, Constants.SQUARE_SIZE);
			_centerPane.getChildren().addAll(_title);
			_firstGame = false;
			_game = new Game(_centerPane, _playerOne, _playerTwo, _slider.getValue());
			_root.setCenter(_centerPane);
			}
			event.consume();
		}

	}

	/*
	 * This class is responsible for making the undo moves. If a piece is not moving
	 * and there are enough moves on the stack, the moves will undo.
	 */
	private class UndoHandler implements EventHandler<ActionEvent> {

		// If no piece is moving and there are enough moves on the stack, undo the move
		@Override
		public void handle(ActionEvent event) {
			if (_game != null)
				if (_game.getAllowance()) {
					if (_game.getLogicBoard().getUndoStack().getStack().size() > 1)
						_game.setPieces(2);
				}
			event.consume();
		}
	}

	/*
	 * This is the class that creates the hint. It calculates a very good move, then
	 * displays the move in the right pane based on its coordinates
	 */
	private class HelpHandler implements EventHandler<ActionEvent> {

		private Label _hint;

		// If no piece is moving, calculates a very good move. Prints this move based on
		// the coordinates returned.
		@Override
		public void handle(ActionEvent event) {
			if (_game != null)
				if (_game.getAllowance()) {
					_rightPane.getChildren().remove(_hint);
					SmartAI helper = new SmartAI(true, Constants.HINT_LEVEL);
					helper.setGame(_game);
					helper.setLogicBoard(_game.getLogicBoard());
					Move move = helper.getChoice(_game.getLogicBoard());
					_hint = new Label("Hint: " + this.getText(move.getToX(), move.getToY()));
					_hint.setTranslateX(Constants.HINT_X);
					_hint.setTranslateY(Constants.HINT_Y);
					_hint.setScaleX(2.0);
					_hint.setScaleY(2.0);
					_rightPane.getChildren().add(_hint);
				}
			event.consume();
		}

		// Creates the correct string that represents the move based on the coordinates
		// passed
		private String getText(int letter, int number) {
			String chosenLetter = null;
			switch (letter) {
			case 0:
				chosenLetter = "A";
				break;
			case 1:
				chosenLetter = "B";
				break;
			case 2:
				chosenLetter = "C";
				break;
			case 3:
				chosenLetter = "D";
				break;
			}

			return (chosenLetter + (number + 1));

		}
	}

	// This class is responsible for quitting the programs
	private class QuitHandler implements EventHandler<ActionEvent> {
		@Override
		public void handle(ActionEvent event) {
			System.exit(0);
			event.consume();
		}
	}

}
