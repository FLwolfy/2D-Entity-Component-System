package ecs.engine.component;

import ecs.engine.base.GameComponent;
import javafx.geometry.Point2D;

public class Transform extends GameComponent {
  public Point2D position;
  public Point2D scale;
  public double rotation;

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
