package ecs.engine.base;

import ecs.engine.base.GameComponent.ComponentUpdateOrder;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

public abstract class GameScene {
  ////////////// Component Constants //////////////

  /// The width of the scene.
  public final double width;
  /// The height of the scene.
  public final double height;
  /// The unit width of the scene (width / 100).
  public final double uW;
  /// The unit height of the scene (height / 100).
  public final double uH;

  /////////////////////////////////////////////////

  // static variables
  private static final Map<Class<? extends GameScene>, GameScene> allScenes = new HashMap<>();
  private static final ArrayList<EventHandler<ActionEvent>> subscribedActions = new ArrayList<>();
  private static EventHandler<ActionEvent> sceneChangeAction;
  private static Scene FXscene;
  private static GameScene previousScene;
  private static GameScene currentScene;

  // readonly variables
  private static Double deltaTime;
  private boolean isActive;

  // instance variables
  private final ArrayList<GameObject> allObjects;
  
  // Inputs
  private KeyCode keyInput;
  private MouseButton mouseInput;
  private Point2D mouseCursor;

  public GameScene() {
    // Initialize the scene attributes
    width = FXscene.getWidth();
    height = FXscene.getHeight();
    uW = width / 100;
    uH = height / 100;

    // Initialize the instance variables
    allObjects = new ArrayList<>();

    // Initialize the component list
    GameComponent.allComponents.put(this, new HashMap<>());
    for (ComponentUpdateOrder order : ComponentUpdateOrder.values()) {
      GameComponent.allComponents.get(this).put(order, new ArrayList<>());
    }

    // Initialize the input handler
    keyInput = null;
    mouseCursor = new Point2D(0, 0);
  }

  private void updateInputHandler() {
    FXscene.setOnKeyPressed(e -> keyInput = e.getCode());
    FXscene.setOnKeyReleased(e -> {
      if (e.getCode() == keyInput) {
        keyInput = null;
      }
    });
    FXscene.setOnMousePressed(e -> mouseInput = e.getButton());
    FXscene.setOnMouseReleased(e -> {
      if (e.getButton() == mouseInput) {
        mouseInput = null;
      }
    });
    FXscene.setOnMouseMoved(e -> mouseCursor = new Point2D(e.getX(), e.getY()));
  }

  /* API BELOW */

  /**
   * Set the inner JAVAFX scene of the game.
   */
  public static void setInnerScene(Scene scene) {
    GameScene.FXscene = scene;
  }

  /**
   * Add a new scene to the game. The first scene added will be the current scene.
   * Once the scene is added, there is NO WAY you can remove it.
   */
  public static <T extends GameScene> void addScene(Class<T> sceneClass) {
    if (allScenes.containsKey(sceneClass)) {
      return;
    }
    try {
      T scene = sceneClass.getDeclaredConstructor().newInstance();

      allScenes.put(sceneClass, scene);
      if (currentScene == null) {
        currentScene = scene;
      }

      subscribedActions.add(e -> scene.start());
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Get the delta time between the current frame and the previous frame.
   */
  public static double getDeltaTime() {
    return deltaTime;
  }

  /**
   * Get the scene of the specified class type.
   */
  public static <T extends GameScene> T getGameScene(Class<T> sceneClass) {
    return (T) allScenes.get(sceneClass);
  }

  /**
   * Set the active scene to the specified scene.
   * This active state will be updated in the very next frame.
   */
  public static <T extends GameScene> void setActiveScene(Class<T> sceneClass) {
    if (allScenes.containsKey(sceneClass)) {
      GameScene newScene = allScenes.get(sceneClass);

      // change the scene
      if (currentScene != null) {
        previousScene = currentScene;
        subscribedActions.add(e -> { previousScene.isActive = false; previousScene.onSleep(); });
      }
      currentScene = newScene;
      subscribedActions.add(event -> { currentScene.isActive = true; currentScene.onActive(); });

      // passing inputs
      currentScene.updateInputHandler();
      currentScene.mouseCursor = previousScene.mouseCursor;
      currentScene.keyInput = previousScene.keyInput;
      currentScene.mouseInput = previousScene.mouseInput;

      return;
    }
    throw new RuntimeException("The scene is not found.");
  }

  /**
   * Get the render graphics context of the game.
   * The render GC is automatically cleared in every frame.
   */
  public static GraphicsContext getRenderGC() {
    return ((Canvas)((StackPane) FXscene.getRoot()).getChildren().get(1)).getGraphicsContext2D();
  }

  /**
   * The actions that will be performed for current scene in every frame.
   */
  public static void step(double elapsedTime) {
    if (currentScene == null) {
      throw new RuntimeException("No scene is currently active.");
    }

    // Update the delta time
    deltaTime = elapsedTime;

    // Update with the following sequence
    // 1. Update the input handler
    // TODO: Implement the input handler (UI)

    // 2. Update the events and actions、
    int size = subscribedActions.size();
    for (int i = 0; i < size; i++) {
      subscribedActions.get(i).handle(new ActionEvent());
    }
    subscribedActions.subList(0, size).clear();

    // 3. Update transform
    for (ComponentUpdateOrder order : ComponentUpdateOrder.values()) {
      for (GameComponent component : GameComponent.allComponents.get(currentScene).get(order)) {
        component.transformUpdate();
      }
    }

    // 4. Update the components based on the order
    for (ComponentUpdateOrder order : ComponentUpdateOrder.values()) {
      // Skip the render handler
      if (order == ComponentUpdateOrder.RENDER) {
        continue;
      }

      // Update the components
      for (GameComponent component : GameComponent.allComponents.get(currentScene).get(order)) {
        component.update();
      }
    }

    // 5. Update the gameobjects
    for (GameObject object : currentScene.allObjects) {
      object.update();
    }

    // 6. Update the late update
    currentScene.lateUpdate();
  }

  /**
   * Render the current scene.
   * This method will be called in the main thread to render the scene.
   */
  public static void render() {
    if (currentScene == null) {
      throw new RuntimeException("No scene is currently active.");
    }

    // Clear the canvas
    GraphicsContext gc = ((Canvas)((StackPane) FXscene.getRoot()).getChildren().get(1)).getGraphicsContext2D();
    gc.clearRect(0, 0, FXscene.getWidth(), FXscene.getHeight());

    // Render the gameComponents
    for (GameComponent component : GameComponent.allComponents.get(currentScene).get(ComponentUpdateOrder.RENDER)) {
      component.update();
    }
  }

  /**
   * Change the current scene to the specified scene.
   * This method will be called very lastly in the current frame.
   */
  public <T extends GameScene> void changeScene(Class<T> sceneClass) {
    if (sceneChangeAction == null) {
      sceneChangeAction = e -> setActiveScene(sceneClass);
    } else {
      throw new RuntimeException("Scene change is already in progress.");
    }
  }

  /**
   * Register the object onto this scene for synchronous frame updates.
   */
  protected void registerObject(GameObject object) {
    // Add object to the list
    allObjects.add(object);

    // register all the components associated with the object
    for (GameComponent component : object.getAllComponents().values()) {
      GameComponent.allComponents.get(this).get(component.COMPONENT_UPDATE_ORDER()).add(component);
    }

    // give the gamescene reference to the object
    try {
      Field sceneField = GameObject.class.getDeclaredField("attachedScene");
      sceneField.setAccessible(true);
      sceneField.set(object, this);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }
    
    // call the awake() method and subscribe the start() method
    object.awake();
    subscribedActions.add(e -> object.start());
  }

  /**
   * Unregister the object from this scene.
   * This will call the onDestroy() method immediately and the object will be removed from the scene.
   */
  protected void unregisterObject(GameObject object) {
    // called the onDestroy() method
    object.onDestroy();

    // unregister all the components associated with the object
    for (GameComponent component : object.getAllComponents().values()) {
      GameComponent.allComponents.get(this).get(component.COMPONENT_UPDATE_ORDER()).remove(component);
    }
    object.detachAllComponents();

    // Reset the gamescene reference of the object back to null
    try {
      Field awakeField = GameObject.class.getDeclaredField("attachedScene");
      awakeField.setAccessible(true);
      awakeField.set(object, null);
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new RuntimeException(e);
    }

    // Subscribe the removal of the object
    allObjects.remove(object);
  }

  /**
   * Instantiate a new object of the specified class type and register it onto this scene.
   * Return the reference of the instantiated object.
   */
  protected <T extends GameObject> T instantiateObject(Class<T> objectClass) {
    try {
      T object = objectClass.getDeclaredConstructor().newInstance();
      registerObject(object);
      return object;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Subscribe a given action to be executed in the very next upcoming frame.
   * The subscribed actions will only be executed once.
   */
  public void subscribeAction(EventHandler<ActionEvent> action) {
    subscribedActions.add(action);
  }

  /**
   * Whether the scene is currently active.
   */
  public boolean isActive() {
    return isActive;
  }

  /**
   * Get the input key keycode.
   * After the keycode have been used, it will be no longer active until the next input.
   */
  public KeyCode getKeyInput() {
    KeyCode temp = keyInput;
    keyInput = null;
    return temp;
  }

  /**
   * Get the input mouse button.
   */
  public MouseButton getMouseInput() {
    MouseButton temp = mouseInput;
    mouseInput = null;
    return temp;
  }

  /**
   * Get the mouse cursor position vector.
   */
  public Point2D getMouseCursor() {
    return mouseCursor;
  }

  /* OVERRIDABLE METHODS BELOW */

  /**
   * Called in the very first frame when the gamescene is added on the game (before the first onActive()).
   * This method should be overridden by subclasses as needed.
   */
  public void start() {}

  /**
   * Called in the very next frame when the scene is active.
   * This method should be overridden by subclasses as needed.
   */
  public void onActive() {}

  /**
   * Called in the very next frame when the scene is no longer active.
   * This method should be overridden by subclasses as needed.
   */
  public void onSleep() {}

  /**
   * Called very lastly in every frame to do the final calculation.
   * This is often used for handling inputs and scene changes.
   * This method should be overridden by subclasses as needed.
   */
  public void lateUpdate() {}
}
