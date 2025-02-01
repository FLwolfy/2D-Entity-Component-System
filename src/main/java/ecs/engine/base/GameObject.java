package ecs.engine.base;

import ecs.engine.component.Transform;
import ecs.engine.tag.ObjectTag;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * The base class for all objects in the game.
 */
public abstract class GameObject {
  /**
   * The Transform component of the GameObject.
   */
  public final Transform transform;

  // instance variables
  private GameScene attachedScene; // This is initialized using reflection in GameScene
  private final Map<Class<? extends GameComponent>, GameComponent> attachedComponents;

  protected GameObject() {
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
   * The Transform component is a default component such that it should not be attached.
   * You should NOT attach the component while the game is running.
   */
  protected  <T extends GameComponent> T attachComponent(Class<T> componentClass) {
    T checkedComponent = getComponent(componentClass);
    if (checkedComponent != null) {
        return checkedComponent;
    }
    try {
      T component = componentClass.getDeclaredConstructor().newInstance();
      attachedComponents.put(componentClass, component);

      // attach the component to the scene
      if (attachedScene != null) {
        GameComponent.allComponents.get(attachedScene).get(component.COMPONENT_UPDATE_TAG()).add(component);
      }

      // give the reference of the gameobject to the gamecomponent using reflection
      Field gameObjectField = GameComponent.class.getDeclaredField("gameObject");
      gameObjectField.setAccessible(true);
      gameObjectField.set(component, this);

      // give the reference of the transform to the gamecomponent using reflection
      Field transformField = GameComponent.class.getDeclaredField("transform");
      transformField.setAccessible(true);

      if (component.getClass() == Transform.class) {
        transformField.set(component, component);
      } else {
        transformField.set(component, transform);
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
  protected <T extends GameComponent> void detachComponent(Class<T> componentClass) {
    T component = getComponent(componentClass);
    if (component == null || component == transform) {
      return;
    }
    try {
      // call the onDetached()
      component.onDetached();

      // remove the component from the list
      attachedComponents.remove(componentClass);

      // remove the component from the game
      if (attachedScene != null) {
        GameComponent.allComponents.get(attachedScene).get(component.COMPONENT_UPDATE_TAG()).remove(component);
      }

      // reset the reference of the gameobject of the gamecomponent back to null using reflection
      Field gameObjectField = GameComponent.class.getDeclaredField("gameObject");
      gameObjectField.setAccessible(true);
      gameObjectField.set(component, null);

      // reset the reference of the transform of the gamecomponent back to null using reflection
      Field transformField = GameComponent.class.getDeclaredField("transform");
      transformField.setAccessible(true);
      transformField.set(component, null);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Detach all components from the GameObject.
   */
  protected void detachAllComponents() { // This method is called in GameScene using reflection
    for (GameComponent component : attachedComponents.values()) {
      detachComponent(component.getClass());
    }
  }

  /* OVERRIDABLE METHODS BELOW */
  /**
   * The Tag of the gameObject.
   * This method MUST be declared for frame update use.
   */
  public abstract ObjectTag OBJECT_TAG();

  /**
   * Called at the moment right after the object is registered on a scene.
   * Often times you should add GameComponents here.
   * This method should be overridden by subclasses as needed.
   */
  public abstract void init();

  /**
   * Called at the moment right after the object is unregistered from a scene.
   * This is called before the object's components are detached.
   * This method should be overridden by subclasses as needed.
   */
  public void onDestroy() {}
}
