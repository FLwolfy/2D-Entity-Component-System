package ecs.engine.component;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;

public class CircleCollider extends Collider<Circle>{

  public double radius;
  public double radiusX;
  public double radiusY;

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
}
