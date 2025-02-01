package ecs.engine.base;

import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

/**
 * The main class for the game.
 * This class is responsible for setting up the game and starting the game loop.
 */
public class Game {
  ////////////// Game Constants //////////////
  public static final int WIDTH = 800;
  public static final int HEIGHT = 600;
  public static final String TITLE = "ECS Example";
  public static final Paint DEFAULT_BACKGROUND = Paint.valueOf("#202020");
  public static final double MAX_FRAME_RATE = 60.0;

  ///////////////////////////////////////////

  // Stage for the game
  private Stage stage;

  // Time tracking for the game loop
  private static final double TIME_PER_FRAME = 1.0 / MAX_FRAME_RATE;
  private long lastTime = System.nanoTime();

  /**
   * Create a new game
   * @param stage The stage (application window) to use for the game
   */
  public Game(Stage stage) {
    this.stage = stage;

    // Create the scene
    Scene scene = new Scene(new StackPane(), WIDTH, HEIGHT);
    setupCanvases(scene);
    GameScene.setInnerScene(scene);

    // Set the stage
    stage.setTitle(TITLE);
    stage.setScene(scene);
    stage.show();
  }

  private void setupCanvases(Scene scene) {
    // Create the canvas
    Canvas backgroundCanvas = new Canvas(WIDTH, HEIGHT);
    Canvas gameCanvas = new Canvas(WIDTH, HEIGHT);

    // Set the background
    GraphicsContext bggc = backgroundCanvas.getGraphicsContext2D();
    bggc.setFill(DEFAULT_BACKGROUND);
    bggc.fillRect(0, 0, WIDTH, HEIGHT);

    // Add the canvases to the scene
    StackPane root = (StackPane) scene.getRoot();
    root.getChildren().addAll(backgroundCanvas, gameCanvas);
  }

  private void startGameLoop() {
    Thread gameLoopThread = new Thread(() -> {
      while (true) {
        long beginTime = System.nanoTime();
        if (beginTime - lastTime < 1_000_000_000 / MAX_FRAME_RATE) {
          continue;
        }

        // Step the game logic
        double deltaTime = (beginTime - lastTime) / 1_000_000_000.0;
        GameScene.step(deltaTime);

        // Render the game scene by calling Platform.runLater() to execute in the main thread
        Platform.runLater(GameScene::render);

        // Calculate the last time
        lastTime = beginTime;
      }
    });
    gameLoopThread.setDaemon(true);
    gameLoopThread.start();
  }

  /* API HERE */

  /**
   * Start the game
   */
  public void start() {
    // start the stage
    stage.show();

    // Start the game loop
    startGameLoop();
  }

  /**
   * Add a game scene to the game
   * @param sceneClass The class of the scene to add
   * @param <T> The type of the scene to add
   */
  public <T extends GameScene> void addGameScene(Class<T> sceneClass) {
    GameScene.addScene(sceneClass);
  }

  /**
   * Set the start scene of the game
   * @param sceneClass The class of the scene to set as the start scene
   * @param <T> The type of the scene to set as the start scene
   */
  public <T extends GameScene> void setStartScene(Class<T> sceneClass) {
    GameScene.setActiveScene(sceneClass);
  }
}
