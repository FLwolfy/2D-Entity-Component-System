package ecs.engine.component;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

/**
 * The collider that represents a circular shape.
 */
public class CircleCollider extends Collider<Circle>{

  // readonly variables
  private double radius;
  private double radiusX;
  private double radiusY;

  @Override
  protected void updateColliderAttributes() {
    radiusX = shape.getBoundsInParent().getWidth() / 2;
    radiusY = shape.getBoundsInParent().getHeight() / 2;
    radius = Math.min(radiusX, radiusY);
  }

  @Override
  protected Point2D getNormalVector(Point2D collisionPoint) {
    Point2D colliderCenter = transform.position;
    Point2D toCollisionPoint = collisionPoint.subtract(colliderCenter);

    double rotation = Math.toRadians(transform.rotation);
    double cosTheta = Math.cos(rotation);
    double sinTheta = Math.sin(rotation);

    // Rotate the collision point back to local space
    double localX = toCollisionPoint.getX() * cosTheta + toCollisionPoint.getY() * sinTheta;
    double localY = -toCollisionPoint.getX() * sinTheta + toCollisionPoint.getY() * cosTheta;

    // Calculate normal in local space
    double normX = localX / (radiusX * radiusX);
    double normY = localY / (radiusY * radiusY);
    double worldNormX = normX * cosTheta - normY * sinTheta;
    double worldNormY = normX * sinTheta + normY * cosTheta;

    return new Point2D(worldNormX, worldNormY).normalize();
  }

  /* API HERE */

  /**
   * Get the radius of the circle collider.
   * If the circle is not a perfect circle, the smallest radius will be returned.
   */
  public double getRadius() {
    return radius;
  }

  /**
   * Get the radius of the circle collider in the X axis
   */
  public double getRadiusX() {
    return radiusX;
  }

  /**
   * Get the radius of the circle collider in the Y axis
   */
  public double getRadiusY() {
    return radiusY;
  }
}
