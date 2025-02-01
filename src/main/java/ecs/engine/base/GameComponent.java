package ecs.engine.base;

import ecs.engine.component.Transform;
import ecs.engine.tag.ComponentUpdateTag;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all the components in the game.
 * The component is the smallest unit of the game object.
 * The component is responsible for the behavior of the game object.
 */
public abstract class GameComponent {

  /**
   * The list of all game components in the game.
   */
  public final static Map<GameScene, Map<ComponentUpdateTag, ArrayList<GameComponent>>> allComponents = new HashMap<>();

  /**
   * The GameObject that this game component is attached to.
   */
  public final GameObject gameObject; // This is initialized using reflection in GameObject

  /**
   * The Transform component of the GameObject that this game component is attached to.
   */
  public final Transform transform; // This is initialized using reflection in GameObject

  /* OVERRIDABLE METHODS BELOW */

  protected GameComponent() {
    gameObject = null;
    transform = null;
  }

  /**
   * Retrieve a component of the specified type from the list of attached components.
   * If no matching component is found, it returns null.
   */
  protected <T extends GameComponent> T getComponent(Class<T> componentClass) {
    if (gameObject == null) {
      return null;
    }
    return gameObject.getComponent(componentClass);
  }

  /**
   * Retrieve all Components.
   */
  public Map<Class<? extends GameComponent>, GameComponent> getAllComponents() {
    if (gameObject == null) {
      return null;
    }
    return gameObject.getAllComponents();
  }

  /**
   * The order of the update of this component.
   * This method MUST be declared for frame update use.
   */
  public abstract ComponentUpdateTag COMPONENT_UPDATE_TAG();

  /**
   * Called in the very next frame to do the start-ups.
   * This method is called right before its first update method.
   * This method should be overridden by subclasses as needed.
   */
  public void start() {}

  /**
   * Called every frame to update the component behavior of the object.
   * This method should be overridden by subclasses as needed.
   */
  public void update() {}

  /**
   * Called every frame to update the impact of the transform changes on other components.
   * This method is called before all the update() methods of all the components.
   * This method should be overridden by subclasses as needed.
   */
  public void transformUpdate() {}

  /**
   * Called at the moment right after the game component is attached on a game object.
   * Often times you should do the initializations here.
   * This method should be overridden by subclasses as needed.
   */
  public void onAttached() {}

  /**
   * Called at the moment right after the gamecomponent is detached on a gameobject.
   * This method should be overridden by subclasses as needed.
   */
  public void onDetached() {}
}
