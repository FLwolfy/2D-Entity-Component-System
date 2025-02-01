package ecs.sandbox.scene;

import ecs.engine.base.GameScene;
import ecs.sandbox.object.Bouncer;
import java.util.ArrayList;


public class ExampleScene extends GameScene {

  // Store the bouncer game objects
  private final ArrayList<Bouncer> bouncers = new ArrayList<>();

  @Override
  public void setUp() {
    // Create the bouncers
    for (int i = 0; i < 15; i++) {
      // Instantiate the bouncer
      Bouncer bouncer = instantiateObject(Bouncer.class);

      // Add the bouncer to the list
      bouncers.add(bouncer);
    }
  }

  // This is called every frame to update the interactions between the objects
  @Override
  public void interact() {
    // Do nothing
  }
}
