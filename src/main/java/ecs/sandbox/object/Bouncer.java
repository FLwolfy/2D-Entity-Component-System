package ecs.sandbox.object;

import ecs.engine.base.GameObject;
import ecs.engine.component.CircleCollider;
import ecs.engine.component.PhysicsHandler;
import ecs.engine.component.RenderHandler;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bouncer extends GameObject {

  // The components normally will be stored as private fields
  private RenderHandler renderHandler;
  private PhysicsHandler physicsHandler;
  private CircleCollider collider;

  // This should be the unique identifier for the object
  @Override
  public int TAG() {
    return 0;
  }

  // This is called when the object is created for initialization
  // There should NOT be any constructor in the object class
  @Override
  public void awake() {
    // Attach the components
    renderHandler = attachComponent(RenderHandler.class);
    physicsHandler = attachComponent(PhysicsHandler.class);
    collider = attachComponent(CircleCollider.class);

    // Set the properties
    transform.position = new Point2D(50 * getScene().uW, 50 * getScene().uH);
    renderHandler.setImage(new Circle(5 * getScene().uW, Color.WHITE));
    collider.setShape(new Circle(5 * getScene().uW));
  }

  // This is called every frame
  @Override
  public void update() {
    // Check the walls
    checkWalls();
  }

  private void checkWalls() {
    // horizontal walls
    if (transform.position.getX() > getScene().width - collider.getRadiusX() && physicsHandler.velocity.getX() > 0 ||
        transform.position.getX() < collider.getRadiusX() && physicsHandler.velocity.getX() < 0) {
      physicsHandler.velocity = new Point2D(-physicsHandler.velocity.getX(), physicsHandler.velocity.getY());
    }

    // vertical walls
    if (transform.position.getY() > getScene().height - collider.getRadiusY() && physicsHandler.velocity.getY() > 0 ||
        transform.position.getY() < collider.getRadiusY() && physicsHandler.velocity.getY() < 0) {
      physicsHandler.velocity = new Point2D(physicsHandler.velocity.getX(), -physicsHandler.velocity.getY());
    }
  }

}
