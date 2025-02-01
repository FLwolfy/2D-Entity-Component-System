package ecs.engine.component;

import ecs.engine.base.GameComponent;
import ecs.engine.base.GameScene;
import ecs.engine.tag.ComponentUpdateTag;
import java.util.ArrayList;
import java.util.Comparator;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

/**
 * The component that handles the rendering of the entity.
 */
public class RenderHandler extends GameComponent {

  ////////////// Component Settings //////////////

  /// The render order of the object. The smaller the render order, the earlier it gets rendered.
  public int renderOrder = 0;

  ///////////////////////////////////////////////

  // readonly variables
  private double rawWidth;
  private double rawHeight;
  private double width;
  private double height;

  // instance variables
  private Node image;
  private GraphicsContext graphicsContext;
  private int oldRenderOrder;

  @Override
  public ComponentUpdateTag COMPONENT_UPDATE_TAG() {
    return ComponentUpdateTag.RENDER;
  }

  @Override
  public void onAttached() {
    rawWidth = 0;
    rawHeight = 0;
    width = 0;
    height = 0;
    oldRenderOrder = renderOrder;

    updateRenderOrder();
  }

  @Override
  public void start() {
    graphicsContext = GameScene.getRenderGC();
  }

  @Override
  public void transformUpdate() {
    if (image == null) {
      return;
    }

    // Update the render order
    if (oldRenderOrder != renderOrder) {
      updateRenderOrder();
      oldRenderOrder = renderOrder;
    }

    handleRenderShape();
  }
  
  @Override
  public void update() {
    synchronized (this) {
      if (image == null) {
        return;
      }

      // Capture the transformed image
      SnapshotParameters params = new SnapshotParameters();
      params.setFill(Color.TRANSPARENT);
      WritableImage snapshot = image.snapshot(params, null);

      // Draw the image at the correct transformed position
      graphicsContext.drawImage(snapshot, transform.position.getX() - width / 2, transform.position.getY() - height / 2);
    }
  }

  private void handleRenderShape() {
    // update the image position
    Bounds bounds = image.getBoundsInLocal();
    double centerX = bounds.getMinX() + bounds.getWidth() / 2;
    double centerY = bounds.getMinY() + bounds.getHeight() / 2;

    image.getTransforms().clear();
    image.getTransforms().addAll(
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
    width = image.getBoundsInParent().getWidth();
    height = image.getBoundsInParent().getHeight();
  }

  private void updateRenderOrder() {
    ArrayList<GameComponent> renderComponents = GameComponent.allComponents.get(gameObject.getScene()).get(
        ComponentUpdateTag.RENDER);

    // The smaller the renderOrder, the earlier it gets rendered, the back it is
    renderComponents.sort(Comparator.comparingInt(c -> ((RenderHandler) c).renderOrder));
  }

  /* API BELOW */

  /**
   * Set the image to be rendered
   * @param image The image to be rendered
   */
  public void setImage(Node image) {
    this.image = image;
    this.rawWidth = image.getBoundsInParent().getWidth();
    this.rawHeight = image.getBoundsInParent().getHeight();
  }

  /**
   * Get the image to be rendered
   * @return The image to be rendered
   */
  public Node getImage() {
    return image;
  }

  /**
   * Get the width of the rendered image
   * @return The width of the rendered image
   */
  public double getWidth() {
    return width;
  }

  /**
   * Get the height of the rendered image
   * @return The height of the rendered image
   */
  public double getHeight() {
    return height;
  }

  /**
   * Get the raw width of the rendered image
   * @return The raw width of the rendered image
   */
  public double getRawWidth() {
    return rawWidth;
  }

  /**
   * Get the raw height of the rendered image
   * @return The raw height of the rendered image
   */
  public double getRawHeight() {
    return rawHeight;
  }
}
