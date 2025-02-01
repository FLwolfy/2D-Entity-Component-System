# 2D Entity-Component-System (ECS) in JavaFX

## 1. Introduction
This document provides an overview of the 2D Entity-Component-System (ECS) framework using JavaFX. The system is designed to manage game objects efficiently, separating concerns between objects, behaviors, and scenes.

## 2. Dependencies
This project requires:
- **JavaFX** for UI rendering and interaction.
- **Java 21 or later** to leverage modern language features and improvements.

## 2. Quick Start

### 2.1 Project Structure
All files and scripts should be placed in the `sandbox` package. The framework consists of three main components:

1. **GameScene**: Represents a scene where game objects interact.
2. **GameObject**: Represents an entity in the game world.
3. **EntityBehavior**: Defines the behavior of a game object.

### 2.2 Example Implementation

#### 2.2.1 Defining a Scene
Each scene extends `GameScene` and defines its setup and interaction logic.

```java
package sandbox;

public class ExampleScene extends GameScene {

    @Override
    public void setUp() {
        // Initialize game objects, behaviors, and environment
    }

    @Override
    public void interact() {
        // Define interactions between game objects
    }
}
```

#### 2.2.2 Creating a Game Object
Each game object extends `GameObject` and defines its unique identifier and initialization logic.

```java
package sandbox;

public class ExampleObject extends GameObject {

    @Override
    public ObjectTag OBJECT_TAG() {
        return ObjectTag.PLAYER; // Unique identifier
    }

    @Override
    public void init() {
        // Initialize components, attach behaviors, set properties
    }
}
```

#### 2.2.3 Implementing Behavior
Behaviors define logic that can be attached to game objects. They extend `EntityBehavior` and implement lifecycle methods.

```java
package sandbox;

public class ExampleBehavior extends EntityBehavior {

    @Override
    public void awake() {
        // Acquire references to necessary components or objects
    }

    @Override
    public void start() {
        // Initialize values, configure behavior
    }

    @Override
    public void update() {
        // Define frame-by-frame behavior if enabled
    }
}
```

## 3. Core Concepts

### 3.1 GameScene
A scene represents a game level or state where objects exist and interact.

- `setUp()`: Called at the beginning to initialize objects.
- `interact()`: Called every frame to manage object interactions.

### 3.2 GameObject
Represents an entity in the game world.

- `OBJECT_TAG()`: Returns a unique identifier for the object.
- `init()`: Called when the object is created (instead of a constructor).

### 3.3 EntityBehavior
Defines behavior that can be attached to a game object.

- `awake()`: Called when the object is created to obtain references.
- `start()`: Called when the object is initialized.
- `update()`: Called every frame if the behavior is enabled.

## 4. Notes
This framework is designed to be simple and flexible, allowing for easy extension and customization. It separates concerns between game objects, behaviors, and scenes, enabling efficient management of game entities.

