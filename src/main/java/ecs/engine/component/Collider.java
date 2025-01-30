package ecs.engine.component;

import ecs.engine.base.GameComponent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public abstract class Collider<T extends Shape> extends GameComponent {
  private static final Set<Collider<?>> allColliders = new HashSet<>();

  // Settings
  public boolean isTrigger = false;
  public boolean canSameTagCollide = false;

  // Accessible variables
  public Point2D triggerStartPoint = null;
  public Point2D triggerEndPoint = null;
  public Point2D collisionPoint = null;

  // instance variables
  private final ArrayList<Collider<?>> onCollideColliders = new ArrayList<>();

  protected double rawWidth;
  protected double rawHeight;
  protected Shape shape;
  private Point2D collisionVelocityToBeSet;
  private Point2D triggerIntersectCenter;
  private Consumer<Collider<?>> onTriggerEnter;
  private Consumer<Collider<?>> onTriggerStay;
  private Consumer<Collider<?>> onTriggerExit;
  private boolean isTriggering;

  @Override
  public final ComponentUpdateOrder COMPONENT_UPDATE_ORDER() {
    return ComponentUpdateOrder.COLLISION;
  }

  @Override
  public final void onAttached() {
    allColliders.add(this);
  }

  @Override
  public final void transformUpdate() {
    handleColliderShape();
  }

  @Override
  public final void update() {
    if (shape == null) {
      return;
    }

    // Update the collider physics and events
    for (Collider<?> collider : allColliders) {
      if (collider.gameObject.getScene() == null || !collider.gameObject.getScene().isActive) {
        continue;
      }
      collider.handleColliderPhysics();
      collider.handleCollisionEvents();
    }
  }

  @Override
  public final void onDetached() {
    allColliders.remove(this);
  }

  private void handleColliderShape() {
    if (shape == null) {
      return;
    }

    Bounds bounds = shape.getBoundsInLocal();
    double centerX = bounds.getMinX() + bounds.getWidth() / 2;
    double centerY = bounds.getMinY() + bounds.getHeight() / 2;

    shape.getTransforms().clear();
    shape.getTransforms().addAll(
        new Translate(
            transform.position.getX() - centerX,
            transform.position.getY() - centerY
        ),
        new Rotate(
            transform.rotation,
            centerX,
            centerY
        ),
        new Scale(
            transform.scale.getX(),
            transform.scale.getY(),
            centerX,
            centerY
        )
    );

    updateColliderAttributes();
  }

  private void handleColliderPhysics() {
    if (shape == null || !this.isTriggering) {
      return;
    }

    // Handle the collision velocity
    if (collisionVelocityToBeSet != null) {
      PhysicsHandler physicsHandler = gameObject.getComponent(PhysicsHandler.class);
      if (physicsHandler != null) {
        physicsHandler.velocity = collisionVelocityToBeSet;
        collisionVelocityToBeSet = null;
      }
    }
  }

  private void handleCollisionEvents() {
    boolean onCollision = false;

    for (Collider<?> other : allColliders) {
      if ((!canSameTagCollide && other.gameObject.TAG() == gameObject.TAG()) || other.gameObject.getScene() == null || !other.gameObject.getScene().isActive) {
        continue;
      }

      if (other != this) {
        Shape intersection = Shape.intersect(this.shape, other.shape);

        if (intersection.getBoundsInLocal().getWidth() != -1) {
          onCollision = true;

          // Calculate the center of the intersection
          Bounds bounds = intersection.getBoundsInLocal();
          double intersectX = bounds.getMinX() + bounds.getWidth() / 2;
          double intersectY = bounds.getMinY() + bounds.getHeight() / 2;
          triggerIntersectCenter = new Point2D(intersectX, intersectY);

          // The first entry
          if (!this.isTriggering) {
            this.isTriggering = true;
            triggerStartPoint = new Point2D(intersectX, intersectY);
            triggerEndPoint = null;
          }

          // The first time the collider enters a new collision
          if (!onCollideColliders.contains(other)) {
            onCollideColliders.add(other);

            if (!other.isTrigger) {
              collisionPoint = new Point2D(intersectX, intersectY);
            }

            // Physics collision check
            if (!isTrigger && !other.isTrigger) {
              ///////////////// HERE TO SET THE COLLISION PHYSICS /////////////////
              // Update the velocity for the collision
              collisionVelocityToBeSet = updateColliderVelocity(other);

              /////////////////////////////////////////////////////////////////////
            }

            // Trigger onCollisionEnter() only when the collider first enters a new collision
            if (onTriggerEnter != null) {
              onTriggerEnter.accept(other);
            }
          }

          // Trigger onCollision() every frame while colliding
          if (this.onTriggerStay != null) {
            this.onTriggerStay.accept(other);
          }

        } else {
          if (onCollideColliders.contains(other)) {
            onCollideColliders.remove(other);

            // Trigger onCollisionExit() when the collider exits a collision
            if (collisionPoint != null && onTriggerExit != null) {
              onTriggerExit.accept(other);
            }

            if (!other.isTrigger) {
              collisionPoint = null;
            }
          }
        }
      }
    }

    // The final exit
    if (!onCollision && this.isTriggering) {
      this.isTriggering = false;

      triggerEndPoint = triggerIntersectCenter;
      triggerIntersectCenter = null;
      triggerStartPoint = null;
    }
  }

  private Point2D updateColliderVelocity(Collider<?> other) {
    if (shape == null || other.shape == null) {
      return null;
    }

    PhysicsHandler physicsHandler = gameObject.getComponent(PhysicsHandler.class);
    if (physicsHandler == null || physicsHandler.isStatic) {
      return null;
    }

    PhysicsHandler otherPhysicsHandler = other.gameObject.getComponent(PhysicsHandler.class);

    // get the center of the normal vector
    Point2D normal = getNormalVector(collisionPoint);
    if(normal == null) {
      normal = other.getNormalVector(collisionPoint);
    }
    if(normal == null) {
      return null;
    }

    // get the relative velocity
    Point2D velocity = physicsHandler.velocity;
    Point2D otherVelocity;
    if (otherPhysicsHandler == null) {
      otherVelocity = Point2D.ZERO;
    } else {
      otherVelocity = otherPhysicsHandler.velocity;
    }
    Point2D relativeVelocity = velocity.subtract(otherVelocity);

    // calculate the dot product
    Point2D parallel = normal.multiply(relativeVelocity.dotProduct(normal));
    Point2D perpendicular = relativeVelocity.subtract(parallel);

    // calculate the new velocity
    if (otherPhysicsHandler == null || otherPhysicsHandler.isStatic) {
      return parallel.multiply(-1).add(perpendicular).add(otherVelocity);
    }

    double mass = physicsHandler.mass;
    double otherMass = otherPhysicsHandler.mass;
    return parallel.multiply((mass - otherMass) / (mass + otherMass)).add(perpendicular).add(otherVelocity);
  }

  /**
   * Update the attributes of the collider.
   * This method should be implemented by the subclass.
   */
  protected abstract void updateColliderAttributes();

  /**
   * Get the normalized normal vector of the collider.
   * Since the collision calculation is based on the normal vector, this is really important.
   * This method should be implemented by the subclass.
   */
  protected abstract Point2D getNormalVector(Point2D collisionPoint);

  /* API BELOW */

  /**
   * Check if a point is within the collider.
   */
  public boolean isWithin(Point2D point) {
    if (shape == null) {
      return false;
    }

    // Transform the point back to the local coordinate system manually
    Point2D localPoint = point;
    for (var transform : shape.getTransforms()) {
      if (transform instanceof Translate) {
        Translate translate = (Translate) transform;
        localPoint = new Point2D(
            localPoint.getX() - translate.getX(),
            localPoint.getY() - translate.getY()
        );
      } else if (transform instanceof Scale) {
        Scale scale = (Scale) transform;
        localPoint = new Point2D(
            localPoint.getX() / scale.getX(),
            localPoint.getY() / scale.getY()
        );
      } else if (transform instanceof Rotate) {
        Rotate rotate = (Rotate) transform;
        double angle = -Math.toRadians(rotate.getAngle());
        double pivotX = rotate.getPivotX();
        double pivotY = rotate.getPivotY();

        double dx = localPoint.getX() - pivotX;
        double dy = localPoint.getY() - pivotY;

        double rotatedX = dx * Math.cos(angle) - dy * Math.sin(angle);
        double rotatedY = dx * Math.sin(angle) + dy * Math.cos(angle);

        localPoint = new Point2D(rotatedX + pivotX, rotatedY + pivotY);
      }
    }

    // Check if the transformed point is within the shape
    return shape.contains(localPoint);
  }

  /**
   * Set the shape of the collider.
   * This needs to be called after the collider is attached to a GameObject.
   */
  public void setShape(T shape) {
    this.shape = shape;
    this.rawWidth = shape.getBoundsInLocal().getWidth();
    this.rawHeight = shape.getBoundsInLocal().getHeight();
    handleColliderShape();
  }

  public void setOnTriggerEnter(Consumer<Collider<?>> onTriggerEnter) {
    this.onTriggerEnter = onTriggerEnter;
  }

  public void setOnTriggerStay(Consumer<Collider<?>> onTriggerStay) {
    this.onTriggerStay = onTriggerStay;
  }

  public void setOnTriggerExit(Consumer<Collider<?>> onTriggerExit) {
    this.onTriggerExit = onTriggerExit;
  }
}
