package ecs.engine.base;

import ecs.engine.base.GameComponent.ComponentUpdateOrder;
import ecs.engine.component.Transform;
import java.util.HashMap;
import java.util.Map;

public abstract class GameObject {
  /**
   * The Transform component of the GameObject.
   */
  public final Transform transform;

  // instance variables
  private GameScene attachedScene; // This is initialized using reflection in GameScene
  private final Map<Class<? extends GameComponent>, GameComponent> attachedComponents;

  public GameObject() {
    // Initialize gamecomponents
    this.attachedComponents = new HashMap<>();

    // Transform component is a default component for every GameObject
    transform = attachComponent(Transform.class);
  }

  /* API BELOW */
  /**
   * Get the Scene that this gameobject is attached to.
   * If the object hasn't been registered to any scene, then it returns null.
   */
  public GameScene getScene() {
    return attachedScene;
  }

  /**
   * Retrieve a component of the specified type from the list of attached components.
   * If no matching component is found, it returns null.
   */
  public <T extends GameComponent> T getComponent(Class<T> componentClass) {
    if (attachedComponents.containsKey(componentClass)) {
      return componentClass.cast(attachedComponents.get(componentClass));
    }
    return null;
  }

  /**
   * Retrieve all Components.
   */
  public Map<Class<? extends GameComponent>, GameComponent> getAllComponents() {
    return attachedComponents;
  }

  /**
   * Attach a component of the specified class type to the GameObject, and return the added component instance.
   * If a component of the same class already exists, the method just returns the component instance.
   */
  public <T extends GameComponent> T attachComponent(Class<T> componentClass) {
    T checkedComponent = getComponent(componentClass);
    if (checkedComponent != null) {
        return checkedComponent;
    }
    try {
      T component = componentClass.getDeclaredConstructor().newInstance();
      attachedComponents.put(componentClass, component);

      // attach the component to the scene
      if (attachedScene != null) {
        attachedScene.attachComponent(component);
      }

      // give the reference of the gameobject to the gamecomponent
      component.gameObject = this;

      // give the reference of the transform to the gamecomponent
      if (transform == null) {
        component.transform = (Transform) component;
      } else {
        component.transform = transform;
      }

      // called the onAttached
      component.onAttached();

      // subscribe the start() in the next frame's actions
      GameScene scene = getScene();
      if (scene != null) {
        scene.subscribeAction(e -> component.start());
      }

      return component;
    } catch (Exception e) {
        throw new RuntimeException(e);
    }
  }

  /**
   * Detach a component of the specified class type from the GameObject.
   */
  public <T extends GameComponent> void detachComponent(Class<T> componentClass) {
    T component = getComponent(componentClass);
    if (component == null || component == transform) {
      return;
    }
    try {
      // call the onDetached()
      component.onDetached();

      // remove the component from the list
      attachedComponents.remove(componentClass);

      // reset the reference of the gameobject of the gamecomponent back to null
      component.gameObject = null;
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Detach all components from the GameObject.
   */
  public void detachAllComponents() {
    for (GameComponent component : attachedComponents.values()) {
      detachComponent(component.getClass());
    }
  }

  /* OVERRIDABLE METHODS BELOW */
  /**
   * The Tag of the gameObject.
   * This method MUST be declared for frame update use.
   */
  public abstract int TAG();

  /**
   * Called every frame to update the state or behavior of the object.
   * This method is called before all of its components' updates.
   * This method should be overridden by subclasses as needed.
   */
  public void update() {}

  /**
   * Called at the moment right after the object is registered on a scene.
   * Often times you should add GameComponents here.
   * This method should be overridden by subclasses as needed.
   */
  public void awake() {}

  /**
   * Called in the very next frame to do the start-ups.
   * It serves as an entry point for initializing the object and its attached components or defining
   * any initial behavior specific to the object.
   * This method should be overridden by subclasses as needed.
   */
  public void start() {}

  /**
   * Called at the moment right after the object is unregistered from a scene.
   * This method should be overridden by subclasses as needed.
   */
  public void onDestroy() {}
}
