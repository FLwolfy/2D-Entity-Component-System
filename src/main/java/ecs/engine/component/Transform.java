package ecs.engine.component;

import ecs.engine.base.GameComponent;
import javafx.geometry.Point2D;

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
  public ComponentUpdateOrder COMPONENT_UPDATE_ORDER() {
    return ComponentUpdateOrder.TRANSFORM;
  }

  @Override
  public void onAttached() {
    position = new Point2D(0, 0);
    scale = new Point2D(1, 1);
    rotation = 0;
  }
}
