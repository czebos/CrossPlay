package CrossPlay;

import java.util.ArrayList;

import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/*
 * This is the SmartAI class. This class uses most of the programs processing. It runs MonteCarlo simulations which
 * is explained in the README. Essentially, it runs simulations to determine the best move, and then makes that move,
 * when the class is asked to make a move. The intelligence is determined by the number of iterations.
 */
public class SmartAI implements Player {

	private LogicBoard _logicBoard, playedNode;
	private Game _game;
	private ArrayList<LogicBoard> _possibleNodes;
	private Boolean _best;
	private int _iterations;

	// This is the constructor for the SmartAI class. The iterations is the strength
	// of the computer.
	public SmartAI(Boolean best) {
		_best = best;
		_possibleNodes = new ArrayList<LogicBoard>();
		_iterations = 0;
	}

	// This is the constructor for the computer that solves for hints.
	public SmartAI(Boolean best, int iterations) {
		_best = best;
		_possibleNodes = new ArrayList<LogicBoard>();
		_iterations = iterations;
	}

	@Override
	public void setLogicBoard(LogicBoard logicBoard) {
		_logicBoard = logicBoard;
	}

	public void setGame(Game game) {
		_game = game;
	}

	// This method is called when the computer makes a move. It calls the method
	// that finds the best move and moves the piece visually to that space.
	@Override
	public void makeMove(Game game) {
		_game = game;
		if(_iterations != 0);
		_iterations = _game.getIterations();
		_game.stopCLick();
		Move choice = this.getChoice(_logicBoard);
		Timeline timeline = choice.getPiece().getTimeline();
		choice.getPiece().moveSpaces(-choice.getFromX() + choice.getToX(), -choice.getFromY() + choice.getToY());
		timeline.setOnFinished(new MovePieceHandler(choice));

	}

	// This method returns the best move that the computer decided. It also finds
	// the tree to add additional data to the original number of iterations.
	public Move getChoice(LogicBoard node) {
		this.setMatchingNode(node);
		int bestChoice = 0;
		double bestValue = 0;
		LogicBoard nodeToChoose = new LogicBoard(playedNode);
		playedNode = null;

		nodeToChoose.createTempBoardState();
		for (int i = 0; i < _iterations; i++) { // This is the 4 steps of the MonteCarlo Search Tree simulation.
			LogicBoard bestNode = this.chooseNode(nodeToChoose); // Choose best UCT node
			bestNode = this.expandNode(bestNode); // Expand that Node
			int win = this.simulation(bestNode); // Simulate one of those child nodes at random
			this.backPropagate(bestNode, win); // Update all parents and the node itself on wins/playouts
			nodeToChoose.restoreBoardState();
		}

		if (!_best)
			bestValue = Constants.HIGH_NUMBER;
		for (int i = 0; i < nodeToChoose.getChildren().size(); i++) { // Finds the move with the highest proportion of
																		// wining and chooses that move.
			if (_best) {
				if (nodeToChoose.getChildren().get(i).getValueOfMove() > bestValue) {
					bestValue = nodeToChoose.getChildren().get(i).getValueOfMove();
					bestChoice = i;
				}
			} else {
				if (nodeToChoose.getChildren().get(i).getValueOfMove() < bestValue) {
					bestValue = nodeToChoose.getChildren().get(i).getValueOfMove();
					bestChoice = i;

				}
			}
		}

		// Finds all possible moves the opponent makes, and adds those to the possible
		// nodes
		for (int k = 0; k < nodeToChoose.getChildren().get(bestChoice).getChildren().size(); k++)
			_possibleNodes.add(nodeToChoose.getChildren().get(bestChoice).getChildren().get(k));
		nodeToChoose.createTempBoardState();
		// Finds the move that leads the chosen, best board state
		for (int i = 0; i < nodeToChoose.checkForMoves().size(); i++) {
			Boolean moveMatches = true;
			LogicBoard moveChoices = new LogicBoard(nodeToChoose);
			moveChoices.evaluatePosition(nodeToChoose.checkForMoves().get(i));
			nodeToChoose.restoreBoardState();
			for (int k = 0; k < Constants.COLUMNS; k++)
				for (int j = 0; j < Constants.ROWS; j++) {
					if (nodeToChoose.getChildren().get(bestChoice).getBoard()[k][j] != moveChoices.getBoard()[k][j]) // If
																														// every
																														// square
																														// does
																														// not
																														// match
						moveMatches = false;
				}
			if (moveMatches) {
				return nodeToChoose.checkForMoves().get(i);
			}
		}
		return null;
	}

	// Finds the possible board state that matches current board state, and uses the
	// data from that node.
	private void setMatchingNode(LogicBoard node) {
		Boolean boardMatches = true;
		for (int k = 0; k < _possibleNodes.size(); k++) { // This for loop looks for the played out position and starts
															// with that tree.
			boardMatches = true;
			for (int i = 0; i < Constants.COLUMNS; i++) {
				for (int j = 0; j < Constants.ROWS; j++) {
					if ((node.getBoard()[i][j] == null && _possibleNodes.get(k).getBoard()[i][j] != null)
							|| (node.getBoard()[i][j] != null && _possibleNodes.get(k).getBoard()[i][j] == null))
						boardMatches = false; // Between the current board and the different possible boards, if one
												// specific space has a piece and the other doesn't, the boards dont
												// match.
					if (node.getBoard()[i][j] != null) { // If there is a piece in the current board
						if (_possibleNodes.get(k).getBoard()[i][j] != null) // If there is a piece in the possible board
							if (!node.getBoard()[i][j].equals(_possibleNodes.get(k).getBoard()[i][j])) // If the piece
																										// is not the
																										// same
								boardMatches = false;
					}

				}
			}
			if (boardMatches) {
				playedNode = _possibleNodes.get(k);
			}
		}
		if (playedNode == null)
			playedNode = new LogicBoard(node);
		_possibleNodes.clear();
	}

	// Step 1: Choose best UCT node
	// Finds the UCT value of each of the children, and chooses the best value.
	// This depends on who's turn it is. Repeats until there are no children
	public LogicBoard chooseNode(LogicBoard node) {
		while (node.getChildren().size() != 0) { // While there are children
			double maxUCT = 0;
			int best = 0;
			double nodeUCT;
			for (int i = 0; i < node.getChildren().size(); i++) { // For all children
				if (((node.getTurn()) == _logicBoard.getTurn() && _best)
						|| (!((node.getTurn()) == _logicBoard.getTurn()) && !_best)) { // If its the Computers turn
					nodeUCT = node.getChildren().get(i).getUCT();
					if (nodeUCT > maxUCT) {
						maxUCT = node.getChildren().get(i).getUCT();
						best = i;
					}
				} else { // If its the Enemy's turn
					nodeUCT = node.getChildren().get(i).getEnemyUCT();
					if (nodeUCT > maxUCT) {
						maxUCT = node.getChildren().get(i).getEnemyUCT();
						best = i;
					}
				}
			}
			node = node.getChildren().get(best);
		}
		return node;
	}

	// Step 2: Expansion
	// After the chosen node is found all the way down the tree. All of the children
	// of that node are found and made children. Then one is chosen arbitrarily and
	// returned.
	public LogicBoard expandNode(LogicBoard node) {
		LogicBoard child = new LogicBoard(node);
		ArrayList<Move> options = node.checkForMoves();
		int nodeChoice = (int) (Math.random() * options.size());
		if (node.checkWhiteWin() || node.checkBlackWin()) { // If game is already over. prevents the game from being
															// played out after it is over
			return node;
		}
		node.createTempBoardState();
		for (int i = 0; i < options.size(); i++) { // For each of the possible options
			child = new LogicBoard(node);
			child.evaluatePosition(options.get(i)); // Evaluate each option
			node.add(child);
			child.setParent(node); // Set parent and child
			node.restoreBoardState();
		}
		return node.getChildren().get(nodeChoice);
	}

	// Step 3: Simulation
	// This plays out a node completely randomly. Each move is randomly chosen, and
	// once the game is over,
	// if the winner was the computer, returns a win.
	public int simulation(LogicBoard board) {
		board.checkEndGame();
		if ((board.checkBlackWin() && !_logicBoard.getTurn()) || (board.checkWhiteWin() && _logicBoard.getTurn())) { // If
																														// the
																														// color
																														// matches
																														// the
																														// board
																														// win
			return 0;
		}
		if ((board.checkBlackWin() && _logicBoard.getTurn()) || (board.checkWhiteWin() && !_logicBoard.getTurn())) { // If
																														// the
																														// color
																														// doesn't
																														// match
																														// the
																														// board
																														// win
			return 1;
		}
		LogicBoard logicBoard = new LogicBoard(board);
		int selection;
		ArrayList<Move> movesAvailable = logicBoard.checkForMoves();
		selection = (int) (Math.random() * movesAvailable.size());
		logicBoard.evaluatePosition(movesAvailable.get(selection));
		return this.simulation(logicBoard);
	}

	// Step 4: Back Propagation
	// This updates the node and all subsequent parents of the node up until the
	// root node.
	// Sets if it was win or loss and adds a playout.
	public void backPropagate(LogicBoard node, int win) {
		if (win == 1) // 1 represents a win
			node.win();
		else
			node.lose();
		node.playout();
		LogicBoard parentNode = node.getParent();
		while (parentNode != null) { // While the node has a parent
			if (win == 1)
				parentNode.win();
			else
				parentNode.lose();
			parentNode.playout();
			parentNode = parentNode.getParent(); // Parent node is its parent
		}
	}

	// This class is responsible for moving the piece logically and taking care of
	// handlers and turns.
	// This is after the player has decided on a move. It evaluates it and prepares
	// the handlers.
	private class MovePieceHandler implements EventHandler<ActionEvent> {

		private Move _move;

		public MovePieceHandler(Move move) {
			_move = move;
		}

		// This checks for a winner. If there is no winner, then the next turn is
		// called.
		@Override
		public void handle(ActionEvent event) {
			_game.allowCLick();
			_logicBoard.evaluateTurn(_move);
			if (!_logicBoard.checkEndGame())
				_game.nextTurn();
			else
				_game.checkEndGame();
			event.consume();
		}
	}

}