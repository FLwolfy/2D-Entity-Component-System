# 2D Entity-Component-System (ECS) in JavaFX

## 1. Introduction

### 1.1 About This Project
This document provides an overview of the 2D Entity-Component-System (ECS) framework using JavaFX. The system is designed to manage game objects efficiently, separating concerns between objects, behaviors, and scenes. If you think the structure helpful or fun, please ⭐️ star this project, thank you!

### 1.2 Dependencies
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
A scene represents a game level or state where objects exist and interact.

- `setUp()`: Called at the beginning to initialize objects.
- `interact()`: Called every frame to manage object interactions.

```java
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
Represents an entity in the game world.

- `OBJECT_TAG()`: Returns a unique identifier for the object.
- `init()`: Called when the object is created (instead of a constructor).

```java
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
Defines behavior that can be attached to a game object.

- `awake()`: Called when the object is created to obtain references.
- `start()`: Called when the object is initialized.
- `update()`: Called every frame if the behavior is enabled.

```java
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

### 3.1 Components
Components define different functionalities that can be attached to a `GameObject`. The ECS framework provides four core components:

1. **Transform**: Stores position, rotation, and scale of an object.
2. **RenderHandler**: Handles rendering logic for the object.
3. **PhysicsHandler**: Manages physics-related properties such as velocity and acceleration.
4. **Collider**: Defines collision normals and detection logic.

A `GameObject` must attach components in its `init()` method using `attachComponent(Class)`. Example:

```java
@Override
public void init() {
    attachComponent(PhysicsHandler.class);
    attachComponent(RenderHandler.class);
    attachComponent(ExampleBehavior.class);
}
```

### 3.2 EntityBehavior
A `EntityBehavior` defines game logic and must be attached to a `GameObject`. To interact with components, it uses `getComponent(Class)`. Example:

```java
private PhysicsHandler physicsHandler;

@Override
public void awake() {
    physicsHandler = getComponent(PhysicsHandler.class);
}
```

## 4. Notes
This framework is designed to be simple and flexible, allowing for easy extension and customization. It separates concerns between game objects, behaviors, and scenes, enabling efficient management of game entities.

