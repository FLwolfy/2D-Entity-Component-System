package ecs.engine.base;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

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
  public static final double MAX_FRAME_RATE = 144.0;
  public static final double FIXED_TIME_STEP = 0.02;

  ///////////////////////////////////////////

  // Stage for the game
  private final Stage stage;

  // Time tracking for the game loop
  private static final double TIME_PER_FRAME = 1.0 / MAX_FRAME_RATE;
  private long lastLogicUpdateTime = System.nanoTime();
  private long lastFixedUpdateTime = System.nanoTime();

  /**
   * Create a new game
   * @param stage The stage (application window) to use for the game
   */
  public Game(Stage stage) {
    this.stage = stage;

    // Create the scene
    Scene scene = new Scene(new StackPane(), WIDTH, HEIGHT);
    scene.setFill(DEFAULT_BACKGROUND);
    setupCanvases(scene);
    GameScene.setInnerScene(scene);

    // Set the stage
    stage.setTitle(TITLE);
    stage.setScene(scene);
    stage.show();
  }

  private void setupCanvases(Scene scene) {
    // Create the canvas groups
    Pane uiCanvas = new Pane();
    Pane backgroundCanvas = new Pane();
    Pane gameCanvas = new Pane();

    // Add the canvases to the scene
    StackPane root = (StackPane) scene.getRoot();
    root.getChildren().addAll(backgroundCanvas, gameCanvas, uiCanvas);
  }

  private void startGameLoop() {
    // Start the Logic loop
    Thread gameLoopThread = new Thread(() -> {
      while (true) {
        long beginTime = System.nanoTime();
        double elapsedTime = (beginTime - lastLogicUpdateTime) / 1_000_000_000.0;
        double fixedElapsedTime = (beginTime - lastFixedUpdateTime) / 1_000_000_000.0;

        if (beginTime - lastLogicUpdateTime >= 1_000_000_000 * TIME_PER_FRAME) {
          // Step the game logic
          GameScene.step(elapsedTime);

          // Calculate the last time
          lastLogicUpdateTime = beginTime;
        }

        if (beginTime - lastFixedUpdateTime >= 1_000_000_000 * FIXED_TIME_STEP) {
          // Step the fixed game logic
          GameScene.fixedStep(FIXED_TIME_STEP);

          // Calculate the last time
          lastFixedUpdateTime = beginTime;
        }
      }
    });
    gameLoopThread.setDaemon(true);
    gameLoopThread.start();

    // Start the render loop
    Timeline renderLoop = new Timeline();
    renderLoop.setCycleCount(Timeline.INDEFINITE);
    renderLoop.getKeyFrames().add(new KeyFrame(
        Duration.seconds(TIME_PER_FRAME),
        event -> GameScene.renderStep()
    ));
    renderLoop.play();
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
