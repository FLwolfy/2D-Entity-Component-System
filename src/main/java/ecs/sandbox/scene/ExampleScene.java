package ecs.sandbox.scene;

import ecs.engine.base.GameScene;
import ecs.engine.component.CircleCollider;
import ecs.engine.component.PhysicsHandler;
import ecs.engine.component.RenderHandler;
import ecs.sandbox.object.Bouncer;
import java.util.ArrayList;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class ExampleScene extends GameScene {

  // Store the bouncer game objects
  private final ArrayList<Bouncer> bouncers = new ArrayList<>();

  @Override
  public void start() {
    // Create the bouncers
    for (int i = 0; i < 15; i++) {
      // Instantiate the bouncer
      Bouncer bouncer = instantiateObject(Bouncer.class);

      // Get the components
      RenderHandler renderHandler = bouncer.getComponent(RenderHandler.class);
      PhysicsHandler physicsHandler = bouncer.getComponent(PhysicsHandler.class);
      CircleCollider collider = bouncer.getComponent(CircleCollider.class);

      // configure the components' settings
      bouncer.transform.scale = new Point2D(0.35, 0.35);
      physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
      physicsHandler.applyGravity = true;
      physicsHandler.applyAirResistance = true;
      collider.canCollideSameTag = true;

      // Set the color
      Color color = Color.color(Math.random(), Math.random(), Math.random());
      ((Circle) renderHandler.getImage()).setFill(color);

      // Add the bouncer to the list
      bouncers.add(bouncer);
    }

  }

  @Override
  public void lateUpdate() {
    // Get the screen dimensions
    for (Bouncer circle : bouncers) {
      CircleCollider circleCollider = circle.getComponent(CircleCollider.class);
      PhysicsHandler physicsHandler = circle.getComponent(PhysicsHandler.class);

      // Reset the position if the circle is out of bounds
      if (circle.transform.position.getY() > height + circleCollider.getRadiusY()) {
        circle.transform.position = new Point2D(width / 2, -circleCollider.getRadiusY());
        physicsHandler.velocity = new Point2D(Math.random() * 1000 - 500, Math.random() * 1000 - 500);
      }
    }
  }
}
