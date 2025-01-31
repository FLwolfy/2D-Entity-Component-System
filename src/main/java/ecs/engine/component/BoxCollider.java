package ecs.engine.component;

import java.util.Arrays;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;

public class BoxCollider extends Collider<Rectangle> {

  ////////////// Component Constants //////////////

  /// The maximum distance between a corner and a collision point to consider the collision as a corner collision
  public static final double MAX_CORNER_DETECTION_DISTANCE = 6;
  /////////////////////////////////////////////////

  // readonly variables
  public double width;
  public double height;
  public Point2D topLeft;
  public Point2D topRight;
  public Point2D bottomLeft;
  public Point2D bottomRight;

  @Override
  public void updateColliderAttributes() {
    // calculate the width and height of the shape
    width = shape.getBoundsInParent().getWidth();
    height = shape.getBoundsInParent().getHeight();

    // Get the bounds of the shape before transformation
    double minX = transform.position.getX() - rawWidth / 2;
    double minY = transform.position.getY() - rawHeight / 2;
    double maxX = transform.position.getX() + rawWidth / 2;
    double maxY = transform.position.getY() + rawHeight / 2;

    // Calculate the center of the shape
    double centerX = (minX + maxX) / 2;
    double centerY = (minY + maxY) / 2;

    // Define the initial corner points before transformation
    Point2D initialTopLeft = new Point2D(minX, minY);
    Point2D initialTopRight = new Point2D(maxX, minY);
    Point2D initialBottomLeft = new Point2D(minX, maxY);
    Point2D initialBottomRight = new Point2D(maxX, maxY);

    // Apply the transformation (Translation, Rotation, Scale)
    Point2D transformedTopLeft = pointToTransformedPoint(initialTopLeft, centerX, centerY);
    Point2D transformedTopRight = pointToTransformedPoint(initialTopRight, centerX, centerY);
    Point2D transformedBottomLeft = pointToTransformedPoint(initialBottomLeft, centerX, centerY);
    Point2D transformedBottomRight = pointToTransformedPoint(initialBottomRight, centerX, centerY);

    // Collect all transformed points
    List<Point2D> points = Arrays.asList(
        transformedTopLeft,
        transformedTopRight,
        transformedBottomLeft,
        transformedBottomRight
    );

    // Reorder the points based on their Y (and X if needed) values to ensure correct assignment
    points.sort((p1, p2) -> {
      if (p1.getY() != p2.getY()) {
        return Double.compare(p1.getY(), p2.getY());
      } else {
        return Double.compare(p1.getX(), p2.getX());
      }
    });

    // Assign transformed points to the collider attributes
    topLeft = points.get(0);
    topRight = points.get(1);
    bottomLeft = points.get(2);
    bottomRight = points.get(3);
  }


  @Override
  protected Point2D getNormalVector(Point2D collisionPoint) {
    // get the edge vectors of the rectangle
    Point2D[] edges = {
        topRight.subtract(topLeft),
        bottomRight.subtract(topRight),
        bottomLeft.subtract(bottomRight),
        topLeft.subtract(bottomLeft)
    };

    // get the center of the rectangle
    Point2D center = new Point2D(
        shape.getBoundsInParent().getMinX() + width / 2,
        shape.getBoundsInParent().getMinY() + height / 2
    );

    // check whether the collision is on the 1. edge or 2. corner
    // 1. if the corner is close to the collision point, return the normalized vector from the center to the corner
    Point2D[] corners = { topLeft, topRight, bottomRight, bottomLeft };
    for (Point2D corner : corners) {
      if (collisionPoint.distance(corner) < MAX_CORNER_DETECTION_DISTANCE) {
        return collisionPoint.subtract(center).normalize();
      }
    }

    // 2. otherwise, return the normalized vector from the center to the closest edge
    double minDistance = Double.MAX_VALUE;
    int closestEdgeIndex = -1;

    for (int i = 0; i < edges.length; i++) {
      Point2D edgeStart;
      switch (i) {
        case 0:
          edgeStart = topLeft;
          break;
        case 1:
          edgeStart = topRight;
          break;
        case 2:
          edgeStart = bottomRight;
          break;
        default:
          edgeStart = bottomLeft;
          break;
      }

      Point2D edgeEnd = edgeStart.add(edges[i]);

      // calculate the distance between the collision point and the edge
      double distance = pointToSegmentDistance(collisionPoint, edgeStart, edgeEnd);

      if (distance < minDistance) {
        minDistance = distance;
        closestEdgeIndex = i;
      }
    }

    Point2D edge = edges[closestEdgeIndex];

    System.out.println("normal: " + new Point2D(-edge.getY(), edge.getX()).normalize());
    return new Point2D(-edge.getY(), edge.getX()).normalize();

  }

  private Point2D pointToTransformedPoint(Point2D point, double centerX, double centerY) {
    // Apply Translation
    double translatedX = point.getX() + (transform.position.getX() - centerX);
    double translatedY = point.getY() + (transform.position.getY() - centerY);

    // Apply Rotation (around the center)
    double rotatedX = centerX + (translatedX - centerX) * Math.cos(Math.toRadians(transform.rotation))
        - (translatedY - centerY) * Math.sin(Math.toRadians(transform.rotation));
    double rotatedY = centerY + (translatedX - centerX) * Math.sin(Math.toRadians(transform.rotation))
        + (translatedY - centerY) * Math.cos(Math.toRadians(transform.rotation));

    // Apply Scale (around the center)
    double scaledX = centerX + (rotatedX - centerX) * transform.scale.getX();
    double scaledY = centerY + (rotatedY - centerY) * transform.scale.getY();

    return new Point2D(scaledX, scaledY);
  }

  private double pointToSegmentDistance(Point2D point, Point2D segmentStart, Point2D segmentEnd) {
    Point2D segment = segmentEnd.subtract(segmentStart);
    double segmentLengthSquared = segment.getX() * segment.getX() + segment.getY() * segment.getY();

    // If the segment is a point, return the distance between the point and the point
    if (segmentLengthSquared == 0) {
      return point.distance(segmentStart);
    }

    // calculate t (0 <= t <= 1)
    double t = point.subtract(segmentStart).dotProduct(segment) / segmentLengthSquared;
    t = Math.max(0, Math.min(1, t));

    Point2D projection = segmentStart.add(segment.multiply(t));
    return point.distance(projection);
  }

  /* API HERE */

  /**
   * Get the width of the box collider.
   */
  public double getWidth() {
    return width;
  }

  /**
   * Get the height of the box collider.
   */
  public double getHeight() {
    return height;
  }

  /**
   * Get the top left corner of the box collider.
   */
  public Point2D getTopLeft() {
    return topLeft;
  }

  /**
   * Get the top right corner of the box collider.
   */
  public Point2D getTopRight() {
    return topRight;
  }

  /**
   * Get the bottom left corner of the box collider.
   */
  public Point2D getBottomLeft() {
    return bottomLeft;
  }

  /**
   * Get the bottom right corner of the box collider.
   */
  public Point2D getBottomRight() {
    return bottomRight;
  }


}
