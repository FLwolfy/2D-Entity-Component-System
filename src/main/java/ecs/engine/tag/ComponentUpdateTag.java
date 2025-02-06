package ecs.engine.tag;

/**
 * The type that specifies the order of the update of the component.
 * The more the Component is in the front, the earlier it gets updated.
 */
public enum ComponentUpdateTag {
  // DO NOT MODIFY THE COMPONENT TAGS BELOW
  TRANSFORM,
  PHYSICS,
  COLLISION,
  BEHAVIOR,
  RENDER
}
