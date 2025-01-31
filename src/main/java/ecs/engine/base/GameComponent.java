package ecs.engine.base;

import ecs.engine.component.Transform;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class GameComponent {

  /**
   * The list of all game components in the game.
   */
  public final static Map<GameScene, Map<ComponentUpdateOrder, ArrayList<GameComponent>>> allComponents = new HashMap<>();

  /**
   * The order of the update of the component.
   * The more the Component is in the front, the earlier it gets updated.
   */
  public enum ComponentUpdateOrder {
    TRANSFORM,
    PHYSICS,
    COLLISION,
    RENDER,
    BEHAVIOR
  }

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
   * The order of the update of this component.
   * This method MUST be declared for frame update use.
   */
  public abstract ComponentUpdateOrder COMPONENT_UPDATE_ORDER();

  /**
   * Called in the very next frame to do the start-ups.
   * This method is called right before its update method.
   * This method should be overridden by subclasses as needed.
   */
  public void start() {}

  /**
   * Called every frame to update the component behavior of the object.
   * This method is called before its attached gameobject's update method.
   * This method should be overridden by subclasses as needed.
   */
  public void update() {}

  /**
   * Called every frame to update the transform of the object.
   * This method is called before its own update() method to update the transformation effect.
   * This method should be overridden by subclasses as needed.
   */
  public void transformUpdate() {}

  /**
   * Called at the moment right after the gamecomponent is attached on a gameobject.
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
