package ecs.sandbox.scene;

import ecs.engine.base.GameScene;
import ecs.engine.component.CircleCollider;
import ecs.engine.component.PhysicsHandler;
import ecs.engine.component.RenderHandler;
import ecs.sandbox.object.Circle2D;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class TestScene extends GameScene {

  private ArrayList<Circle2D> circles = new ArrayList<>();

  @Override
  public void start() {
    for (int i = 0; i < 15; i++) {
      Circle2D circle = instantiateObject(Circle2D.class);
      RenderHandler renderHandler = circle.getComponent(RenderHandler.class);
      PhysicsHandler physicsHandler = circle.getComponent(PhysicsHandler.class);
      CircleCollider collider = circle.getComponent(CircleCollider.class);

      collider.transform.scale = new Point2D(0.35, 0.35);

      physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
      physicsHandler.applyGravity = true;
      physicsHandler.applyAirResistance = true;

      collider.canSameTagCollide = true;

      Color color = Color.color(Math.random(), Math.random(), Math.random());
      ((Circle) renderHandler.getImage()).setFill(color);

      circles.add(circle);
    }

  }

  @Override
  public void lateUpdate() {
    for (Circle2D circle : circles) {
      CircleCollider circleCollider = circle.getComponent(CircleCollider.class);
      PhysicsHandler physicsHandler = circle.getComponent(PhysicsHandler.class);

      if (circle.transform.position.getY() > height + circleCollider.radiusY) {
        circle.transform.position = new Point2D(width / 2, -circleCollider.radiusY);
        physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
      }
    }
  }
}
