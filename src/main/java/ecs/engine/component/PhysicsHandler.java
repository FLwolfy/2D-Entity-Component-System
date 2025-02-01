package ecs.engine.component;

import ecs.engine.base.GameComponent;
import ecs.engine.base.GameScene;
import ecs.engine.tag.ComponentUpdateTag;
import javafx.geometry.Point2D;

/**
 * The component that handles the physics of the entity.
 */
public class PhysicsHandler extends GameComponent {

  ////////////// Component Constants //////////////

  /// The maximum speed of the object.
  public static final double MAX_SPEED = 1000;

  /// The maximum angular speed of the object.
  public static final double MAX_ANGULAR_SPEED = 1000;

  /// The maximum acceleration of the object.
  public static final double MAX_ACCELERATION = 100;

  /// The maximum angular acceleration of the object.
  public static final double MAX_ANGULAR_ACCELERATION = 100;

  ////////////// Component Settings //////////////

  /// Whether the object is static.
  public boolean isStatic = false;
  /// Whether the object is affected by gravity.
  public boolean applyGravity = false;
  /// Whether the object is affected by air resistance.
  public boolean applyAirResistance = false;

  ////////////// Modifiable Fields //////////////

  /// The mass of the object.
  public int mass;
  /// The velocity of the object.
  public Point2D velocity;
  /// The acceleration of the object.
  public Point2D acceleration;
  /// The angular velocity of the object.
  public double angularVelocity;
  /// The angular acceleration of the object.
  public double angularAcceleration;
  /// The gravitation of the object.
  public double gravitation;
  /// The air resistance per second of the object.
  public double airResistancePercentage; // per second

  // instance variables
  private double oldAirResistancePercentage;
  private double realAirResistancePercentage; // per rate

  @Override
  public ComponentUpdateTag COMPONENT_UPDATE_TAG() {
    return ComponentUpdateTag.PHYSICS;
  }

  @Override
  public void onAttached() {
    mass = 1;
    velocity = new Point2D(0, 0);
    acceleration = new Point2D(0, 0);
    angularVelocity = 0;
    angularAcceleration = 0;
    gravitation = 1000;
    airResistancePercentage = 0.25;
  }

  @Override
  public void update() {
    // Apply gravitation
    applyGravity();

    // Apply air resistance
    applyAirResistance();

    // Apply acceleration
    applyAcceleration();
    applyAngularAcceleration();

    // Apply velocity
    applyVelocity();
    applyAngularVelocity();
  }

  private void applyGravity() {
    if (applyGravity) {
      velocity = velocity.add(new Point2D(0, gravitation * mass * GameScene.getDeltaTime()));
    }
  }

  private void applyAirResistance() {
    if (applyAirResistance) {
      if (oldAirResistancePercentage != airResistancePercentage) {
        realAirResistancePercentage = 1 - Math.pow(1 - airResistancePercentage, GameScene.getDeltaTime());
        oldAirResistancePercentage = airResistancePercentage;
      }
      velocity = velocity.multiply(1 - realAirResistancePercentage);
    }
  }

  private void applyAcceleration() {
    if (acceleration.magnitude() > MAX_ACCELERATION) {
      acceleration = acceleration.normalize().multiply(MAX_ACCELERATION);
    }
    velocity = velocity.add(acceleration.multiply(GameScene.getDeltaTime()));
    if (velocity.magnitude() > MAX_SPEED) {
      velocity = velocity.normalize().multiply(MAX_SPEED);
    }
    angularAcceleration = Math.min(Math.max(angularAcceleration, -MAX_ANGULAR_ACCELERATION), MAX_ANGULAR_ACCELERATION);
  }

  private void applyAngularAcceleration() {
    angularVelocity += angularAcceleration * GameScene.getDeltaTime();
    angularVelocity = Math.min(Math.max(angularVelocity, -MAX_ANGULAR_SPEED), MAX_ANGULAR_SPEED);
  }

  private void applyVelocity() {
    transform.position = transform.position.add(velocity.multiply(GameScene.getDeltaTime()));
  }

  private void applyAngularVelocity() {
    transform.rotation += angularVelocity * GameScene.getDeltaTime();
  }

  /* API BELOW */

  /**
   * Applies a force to the object.
   * @param force The force to apply.
   */
  public void applyForce(Point2D force) {
    acceleration = force.multiply(1.0 / mass);
  }

  /**
   * Applies a torque to the object.
   * @param torque The torque to apply.
   */
  public void applyTorque(double torque) {
    angularAcceleration = torque / mass;
  }

  /**
   * Applies an impulse to the object.
   * @param impulse The impulse to apply.
   */
  public void applyImpulse(Point2D impulse) {
    velocity = velocity.add(impulse.multiply(1.0 / mass));
  }

  /**
   * Applies an angular impulse to the object.
   * @param angularImpulse The angular impulse to apply.
   */
  public void applyAngularImpulse(double angularImpulse) {
    angularVelocity += angularImpulse / mass;
  }
}
