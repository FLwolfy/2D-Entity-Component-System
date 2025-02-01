package ecs.engine.component;

import ecs.engine.base.GameComponent;
import ecs.engine.tag.ComponentUpdateTag;
import javafx.geometry.Point2D;

/**
 * The component that stores the position, scale, and rotation of the entity.
 */
public class Transform extends GameComponent {
  ////////////// Modifiable Fields //////////////

  /// The position of the entity in the world.
  public Point2D position;
  /// The scale of the entity in the world.
  public Point2D scale;
  /// The rotation of the entity in the world.
  public double rotation;

  ///////////////////////////////////////////////

  @Override
  public ComponentUpdateTag COMPONENT_UPDATE_TAG() {
    return ComponentUpdateTag.TRANSFORM;
  }

  @Override
  public void onAttached() {
    position = new Point2D(0, 0);
    scale = new Point2D(1, 1);
    rotation = 0;
  }
}
