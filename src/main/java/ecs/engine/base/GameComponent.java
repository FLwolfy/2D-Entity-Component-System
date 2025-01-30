package ecs.engine.base;

import ecs.engine.component.Transform;

public abstract class GameComponent {

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
   * The GameObject that this gamecomponent is attached to.
   */
  public GameObject gameObject;

  /**
   * The Transform component of the GameObject that this gamecomponent is attached to.
   */
  public Transform transform;

  /**
   * The order of the update of this component.
   * This method MUST be declared for frame update use.
   */
  public abstract ComponentUpdateOrder COMPONENT_UPDATE_ORDER();

  /* OVERRIDABLE METHODS BELOW */

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
