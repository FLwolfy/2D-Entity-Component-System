package ecs.sandbox.object;

import ecs.engine.base.GameObject;
import ecs.engine.base.GameScene;
import ecs.engine.component.CircleCollider;
import ecs.engine.component.PhysicsHandler;
import ecs.engine.component.RenderHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Circle2D extends GameObject {

  private RenderHandler renderHandler;
  private PhysicsHandler physicsHandler;
  private CircleCollider collider;

  @Override
  public int TAG() {
    return 0;
  }

  @Override
  public void awake() {
    renderHandler = attachComponent(RenderHandler.class);
    physicsHandler = attachComponent(PhysicsHandler.class);
    collider = attachComponent(CircleCollider.class);

    transform.position = new Point2D(50 * getScene().uW, 50 * getScene().uH);
    renderHandler.setImage(new Circle(5 * getScene().uW, Color.WHITE));
    collider.setShape(new Circle(5 * getScene().uW));
  }

  @Override
  public void update() {
    if (transform.position.getX() > getScene().width - collider.radiusX && physicsHandler.velocity.getX() > 0 ||
        transform.position.getX() < collider.radiusX && physicsHandler.velocity.getX() < 0) {
      physicsHandler.velocity = new Point2D(-physicsHandler.velocity.getX(), physicsHandler.velocity.getY());
    }
    if (transform.position.getY() > getScene().height - collider.radiusY && physicsHandler.velocity.getY() > 0 ||
        transform.position.getY() < collider.radiusY && physicsHandler.velocity.getY() < 0) {
      physicsHandler.velocity = new Point2D(physicsHandler.velocity.getX(), -physicsHandler.velocity.getY());
    }
  }

}
