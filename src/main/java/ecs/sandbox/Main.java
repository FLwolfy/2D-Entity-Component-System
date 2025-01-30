package ecs.sandbox;

import ecs.engine.base.Game;
import ecs.sandbox.scene.TestScene;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Hello world!
 */
public class Main extends Application {

    /**
     * Starts the application.
     *
     * @param stage The primary stage.
     */
    @Override
    public void start(Stage stage) {
        // Create the game
        Game game = new Game(stage);

        // Set the initial scene
        game.addGameScene(TestScene.class);
        game.setStartScene(TestScene.class);

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