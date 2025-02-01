package ecs.sandbox.object;

import ecs.engine.base.GameObject;
import ecs.engine.component.CircleCollider;
import ecs.engine.component.PhysicsHandler;
import ecs.engine.component.RenderHandler;
import ecs.engine.tag.ObjectTag;
import ecs.sandbox.behavior.BouncerBehavior;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Bouncer extends GameObject {

  // This should be the unique identifier for the object
  @Override
  public ObjectTag OBJECT_TAG() {
    return ObjectTag.BULLET;
  }

  // This is called when the object is created for initialization
  // There should NOT be any constructor in the object class
  @Override
  public void init() {
    // Attach the components
    RenderHandler renderHandler = attachComponent(RenderHandler.class);
    CircleCollider collider = attachComponent(CircleCollider.class);
    PhysicsHandler physicsHandler = attachComponent(PhysicsHandler.class);
    attachComponent(BouncerBehavior.class);

    // Configure the components' settings
    transform.position = new Point2D(50 * getScene().uW, 50 * getScene().uH);
    transform.scale = new Point2D(0.35, 0.35);

    renderHandler.setImage(new Circle(5 * getScene().uW, Color.WHITE));
    Color color = Color.color(Math.random(), Math.random(), Math.random());
    ((Circle) renderHandler.getImage()).setFill(color);

    physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
    physicsHandler.applyGravity = true;
    physicsHandler.applyAirResistance = true;

    collider.setShape(new Circle(5 * getScene().uW));
    collider.canCollideSameTag = true;
  }
}
