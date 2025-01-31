package ecs.sandbox;

import ecs.engine.base.Game;
import ecs.sandbox.scene.ExampleScene;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main class for the application.
 * @author Hsuan-Kai Liao
 */
public class Main extends Application {

    /**
     * Starts the application.
     *
     * @param stage The primary stage.
     */
    @Override
    public void start(Stage stage) {
        // Create the game instance
        Game game = new Game(stage);

        // Add all the required game scenes here
        game.addGameScene(ExampleScene.class);

        // Set the start scene
        game.setStartScene(ExampleScene.class);

        // Start the game
        game.start();
    }

    /**
     * Launches the application.
     *
     * @param args The command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}