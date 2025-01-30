package ecs.engine.component;

import ecs.engine.base.GameComponent;
import ecs.engine.base.GameScene;
import javafx.geometry.Point2D;

public class PhysicsHandler extends GameComponent {
  public static final double MAX_SPEED = 1000;
  public static final double MAX_ANGULAR_SPEED = 1000;
  public static final double MAX_ACCELERATION = 100;
  public static final double MAX_ANGULAR_ACCELERATION = 100;

  // Settings
  public boolean isStatic = false;
  public boolean applyGravity = false;
  public boolean applyAirResistance = false;

  // Accessible variables
  public int mass;
  public Point2D velocity;
  public Point2D acceleration;
  public double angularVelocity;
  public double angularAcceleration;
  public double gravitation;
  public double airResistancePercentage; // per second

  // instance variables
  private double oldAirResistancePercentage;
  private double realAirResistancePercentage; // per rate

  @Override
  public ComponentUpdateOrder COMPONENT_UPDATE_ORDER() {
    return ComponentUpdateOrder.PHYSICS;
  }

  @Override
  public void onAttached() {
    mass = 1;
    velocity = new Point2D(0, 0);
    acceleration = new Point2D(0, 0);
    angularVelocity = 0;
    angularAcceleration = 0;
    gravitation = 981;
    airResistancePercentage = 0.05;
  }

  @Override
  public void update() {
    // Apply gravitation
    if (applyGravity) {
      velocity = velocity.add(new Point2D(0, gravitation * mass * GameScene.deltaTime));
    }

    // Apply air resistance
    if (applyAirResistance) {
      if (oldAirResistancePercentage != airResistancePercentage) {
        realAirResistancePercentage = 1 - Math.pow(1 - airResistancePercentage, GameScene.deltaTime);
        oldAirResistancePercentage = airResistancePercentage;
      }
      velocity = velocity.multiply(1 - realAirResistancePercentage);
    }

    // Apply acceleration
    if (acceleration.magnitude() > MAX_ACCELERATION) {
      acceleration = acceleration.normalize().multiply(MAX_ACCELERATION);
    }
    velocity = velocity.add(acceleration.multiply(GameScene.deltaTime));
    if (velocity.magnitude() > MAX_SPEED) {
      velocity = velocity.normalize().multiply(MAX_SPEED);
    }
    angularAcceleration = Math.min(Math.max(angularAcceleration, -MAX_ANGULAR_ACCELERATION), MAX_ANGULAR_ACCELERATION);
    angularVelocity += angularAcceleration * GameScene.deltaTime;
    angularVelocity = Math.min(Math.max(angularVelocity, -MAX_ANGULAR_SPEED), MAX_ANGULAR_SPEED);

    // Apply velocity
    transform.position = transform.position.add(velocity.multiply(GameScene.deltaTime));
    transform.rotation += angularVelocity * GameScene.deltaTime;
  }

  public void applyForce(Point2D force) {
    acceleration = force.multiply(1.0 / mass);
  }

  public void applyTorque(double torque) {
    angularAcceleration = torque / mass;
  }

  public void applyImpulse(Point2D impulse) {
    velocity = velocity.add(impulse.multiply(1.0 / mass));
  }

  public void applyAngularImpulse(double angularImpulse) {
    angularVelocity += angularImpulse / mass;
  }
}
