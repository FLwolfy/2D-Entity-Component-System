package ecs.sandbox.behavior;

import ecs.engine.component.CircleCollider;
import ecs.engine.component.EntityBehavior;
import ecs.engine.component.PhysicsHandler;
import javafx.geometry.Point2D;

public class BouncerBehavior extends EntityBehavior {

  // The components references
  private PhysicsHandler physicsHandler;
  private CircleCollider collider;

  private double sceneWidth;
  private double sceneHeight;

  // This is called when the object is created for getting the references for the references
  // There should NOT be any constructor in the behavior class
  @Override
  public void awake() {
    physicsHandler = getComponent(PhysicsHandler.class);
    collider = getComponent(CircleCollider.class);
  }

  // This is called when the object is created for setting up the initial values
  @Override
  public void start() {
    sceneWidth = gameObject.getScene().width;
    sceneHeight = gameObject.getScene().height;
  }

  // This is called every frame if the behavior is enabled
  @Override
  public void update() {
    checkWalls();
    checkGround();
  }

  /* private methods below */

  private void checkWalls() {
    // horizontal walls
    if (transform.position.getX() > sceneWidth - collider.getRadiusX() && physicsHandler.velocity.getX() > 0 ||
        transform.position.getX() < collider.getRadiusX() && physicsHandler.velocity.getX() < 0) {
      physicsHandler.velocity = new Point2D(-physicsHandler.velocity.getX(), physicsHandler.velocity.getY());
    }

    // vertical walls
    if (transform.position.getY() > sceneHeight - collider.getRadiusY() && physicsHandler.velocity.getY() > 0 ||
        transform.position.getY() < collider.getRadiusY() && physicsHandler.velocity.getY() < 0) {
      physicsHandler.velocity = new Point2D(physicsHandler.velocity.getX(), -physicsHandler.velocity.getY());
    }
  }

  private void checkGround() {
    // Reset the position if the circle is out of bounds
    if (transform.position.getY() > sceneHeight + collider.getRadiusY()) {
      transform.position = new Point2D(sceneWidth / 2, -collider.getRadiusY());
      physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
    }
  }
}
