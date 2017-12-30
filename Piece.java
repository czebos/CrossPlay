package CrossPlay;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

/*
 * This is the piece class. It is responsible for storing information about the piece, actually creating the piece visually,
 * and moving the piece. This class is responsible for each individual piece.
 */
public class Piece {

	private Boolean _movedOnce, _isTopPiece;
	private Pane _centerPane;
	private Ellipse _ellipse;
	private Rectangle _rectangle;
	private Arc _arc;
	private Timeline _animationTimeline;
	private int _xSpaces, _ySpaces;
	private int _removedX, _removedY;

	// This is the constructor for the Piece class. This creates the shape and sets
	// up the timeline.
	public Piece(Color color, Pane centerPane, Boolean topPiece) {
		_centerPane = centerPane;
		_movedOnce = false;
		_isTopPiece = topPiece;
		this.setUpAnimationTimeline();
		this.createShapes(color, centerPane);
	}

	// Creates all of the shapes that makes a gull and sets the ratio of sizes.
	private void createShapes(Color color, Pane centerPane) {
		_ellipse = new Ellipse(Constants.ELLIPSE_SIZE, Constants.ELLIPSE_SIZE);
		_ellipse.setFill(color);
		_rectangle = new Rectangle(Constants.ELLIPSE_SIZE + 1, Constants.RECTANGLE_SIZE);
		_rectangle.setFill(color);
		_arc = new Arc();
		_arc.setFill(color);
		_arc.setRadiusX(Constants.ELLIPSE_RADIUS);
		_arc.setRadiusY(Constants.ELLIPSE_RADIUS);
		_arc.setStartAngle(0.0f);
		_arc.setLength(Constants.ARC_LENGTH);
		_arc.setType(ArcType.CHORD);
		_centerPane.getChildren().addAll(_arc, _rectangle, _ellipse);
	}

	// Sets up the timeline that dictates where and how quickly a piece moves
	private void setUpAnimationTimeline() {
		KeyFrame fallAnimation = new KeyFrame(Duration.millis(5), new AnimationHandler());
		_animationTimeline = new Timeline(fallAnimation);
		_animationTimeline.setCycleCount(Constants.SQUARE_SIZE);
		_animationTimeline.setOnFinished(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				_xSpaces = 0;
				_ySpaces = 0;
				event.consume();
			}
		});
	}

	// Sets the pieces to the x and y location relative to the arc.
	public void setLocation(double x, double y) {
		_ellipse.setCenterX(x);
		_ellipse.setCenterY(y - Constants.ELLIPSE_OFFSET);
		_rectangle.setX(x - Constants.RECTANGLE_X_OFFSET);
		_rectangle.setY(y - Constants.RECTANGLE_Y_OFFSET);
		_arc.setCenterX(x);
		_arc.setCenterY(y);
	}

	// Activates the timeline and moves the number of spaces passed.
	public void moveSpaces(int xSpaces, int ySpaces) {
		_xSpaces = xSpaces;
		_ySpaces = ySpaces;
		_animationTimeline.play();
	}

	// Removes the piece visually and keeps track of the x and y of the piece
	public void removeItself(int x, int y) {
		this.setLocation(0, Constants.LEAVE_PANE);
		_removedX = x;
		_removedY = y;
	}

	public Boolean hasMovedOnce() {
		return _movedOnce;
	}

	public void moved() {
		_movedOnce = true;
	}

	public Boolean isTopPiece() {
		return _isTopPiece;
	}

	public void resetFirstMove() {
		_movedOnce = false;
	}

	public void movedOnceSet(Boolean hasMovedOnce) {
		_movedOnce = hasMovedOnce;
	}

	public int getRemovedX() {
		return _removedX;
	}

	public int getRemovedY() {
		return _removedY;
	}

	private double getX() {
		return _arc.getCenterX();
	}

	private double getY() {
		return _arc.getCenterY();
	}

	public Timeline getTimeline() {
		return _animationTimeline;
	}

	// This is the AnimationHandler Class. It is responsible for moving the piece
	// from one square to another.
	private class AnimationHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent event) {
			Piece.this.setLocation(Piece.this.getX() + (_xSpaces), Piece.this.getY() + (_ySpaces));
			event.consume();
		}
	}
}
