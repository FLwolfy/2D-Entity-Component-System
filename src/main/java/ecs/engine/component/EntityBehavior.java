package ecs.engine.component;

import ecs.engine.base.GameComponent;
import ecs.engine.tag.ComponentUpdateTag;

/**
 * The base class for the components that are used to define the behavior of the entity.
 * This is most often used for as the communication between the components of the gameObject.
 */
public abstract class EntityBehavior extends GameComponent {

  // instance variables
  private boolean isEnable = true;

  @Override
  public final ComponentUpdateTag COMPONENT_UPDATE_TAG() {
    return ComponentUpdateTag.BEHAVIOR;
  }

  @Override
  public final void onAttached() {
    awake();
  }

  @Override
  public final void transformUpdate() {
    // Do nothing
  }

  @Override
  public final void onDetached() {
    // Do nothing
  }

  /* API BELOW */
  /**
   * Set the enable status of the behavior.
   */
  public void setEnable(boolean enable) {
    isEnable = enable;
    if (isEnable) {
      onEnable();
    } else {
      onDisable();
    }
  }

  /**
   * Get the enable status of the behavior.
   */
  public boolean isEnable() {
    return isEnable;
  }

  /* Overridable Methods */
  /**
   * Called when the behavior is attached to the GameObject.
   */
  public abstract void awake();

  /**
   * Called in the first frame after the attaching.
   */
  @Override
  public abstract void start();

  /**
   * Called every frame.
   */
  @Override
  public abstract void update();

  /**
   * Called every frame with a fixed time step.
   */
  @Override
  public void fixedUpdate() {};

  /**
   * Called every frame lastly.
   * This is often used for handling Inputs.
   */
  public void lateUpdate() {};

  /**
   * Called when the behavior is enabled.
   */
  public void onEnable() {};

  /**
   * Called when the behavior is disabled.
   */
  public void onDisable() {};

}
