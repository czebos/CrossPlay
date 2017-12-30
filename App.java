package CrossPlay;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/*
 * This is the App class. This class starts the Application and is responsible for setting up the stage.
 */

public class App extends Application {

	/*
	 * This is the start method. It instantiates an instance of the top-level class
	 * and sets up the scene for the App. Its set at 675 x 750.
	 */
	@Override
	public void start(Stage stage) {
		PaneOrganizer organizer = new PaneOrganizer();
		Scene scene = new Scene(organizer.getRoot(), 675, 750);
		stage.setScene(scene);
		stage.setTitle("CrossPlay");
		stage.setResizable(false);
		stage.show();
	}

	public static void main(String[] argv) {
		launch(argv);
	}
}
