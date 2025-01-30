package ecs.engine.base;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javafx.scene.image.ImageView;

public class GameSourceLoader {
  private static final GameSourceLoader instance = new GameSourceLoader();

  private final Map<String, ImageView> IMAGES = new HashMap<>();
  private final Map<String, char[][]> LEVELS = new HashMap<>();
  private final Map<String, Object> PROPERTIES = new HashMap<>();

  /////////////////////////////////////////////////
  ///////////// PUBLIC GETTER METHODS /////////////
  /////////////////////////////////////////////////

  /**
   * Load all images and levels from the given directories
   */
  public static void loadAll(String imageDirectory, String levelDirectory) {
    loadAllImages(imageDirectory);
    loadAllLevels(levelDirectory);
    loadAllSerializableProperties();
  }

  /**
   * Get image from the image pool by name
   */
  public static ImageView getImage(String imageName) {
    return new ImageView(instance.IMAGES.get(imageName).getImage());
  }

  /**
   * Get level data from the level pool by name
   */
  public static char[][] getLevel(String levelName) {
    return instance.LEVELS.get(levelName);
  }

  /**
   * Get a deserialized object from PROPERTIES by name
   */
  public static Object getSerializableProperty(String name) {
    return instance.PROPERTIES.get(name);
  }

  ////////////////////////////////////////////////////
  ///////////// LOAD ALL THE IMAGES HERE /////////////
  ////////////////////////////////////////////////////

  /**
   * Load all images from the specified directory.
   */
  public static void loadAllImages(String imageDirectory) {
    try {
      // Get the directory as a URL
      URL resourceUrl = GameSourceLoader.class.getClassLoader().getResource(imageDirectory);
      if (resourceUrl == null) {
        throw new IOException("Directory not found: " + imageDirectory);
      }

      if (resourceUrl.toString().startsWith("jar")) {
        // Handle resources inside a JAR
        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
        System.out.println("Loading images from JAR: " + jarPath);
        try (JarFile jarFile = new JarFile(new File(jarPath))) {
          jarFile.stream()
              .filter(entry -> isValidImageEntry(entry, imageDirectory))
              .forEach(entry -> loadImageFromJar(entry));
        }
      } else {
        // Handle resources on the filesystem
        Path directoryPath = Paths.get(resourceUrl.toURI());
        Files.list(directoryPath)
            .filter(Files::isRegularFile)
            .filter(file -> isValidImageFile(file.getFileName().toString()))
            .forEach(file -> loadImageFromFile(file));
      }
    } catch (Exception e) {
      System.err.println("Failed to load images from directory: " + imageDirectory);
      e.printStackTrace();
    }
  }

  /**
   * Checks if the JarEntry is a valid image file in the specified directory.
   */
  private static boolean isValidImageEntry(JarEntry entry, String directory) {
    return !entry.isDirectory()
        && entry.getName().startsWith(directory)
        && isValidImageFile(entry.getName());
  }

  /**
   * Checks if a file name corresponds to a valid image file.
   */
  private static boolean isValidImageFile(String fileName) {
    String lowerCaseName = fileName.toLowerCase();
    return lowerCaseName.endsWith(".png") || lowerCaseName.endsWith(".jpg") || lowerCaseName.endsWith(".jpeg");
  }

  /**
   * Loads an image from a JAR entry.
   */
  private static void loadImageFromJar(JarEntry entry) {
    String imagePath = entry.getName();
    try (InputStream is = GameSourceLoader.class.getClassLoader().getResourceAsStream(imagePath)) {
      if (is != null) {
        loadImage(imagePath, is);
      } else {
        System.err.println("Failed to load image from JAR: " + imagePath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads an image from a file on the filesystem.
   */
  private static void loadImageFromFile(Path file) {
    try (InputStream is = Files.newInputStream(file)) {
      loadImage(file.toString(), is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads an image from the InputStream and stores it in the IMAGES map.
   */
  private static void loadImage(String imagePath, InputStream inputStream) {
    try {
      // Load the image from the input stream
      javafx.scene.image.Image image = new javafx.scene.image.Image(inputStream);

      // Create an ImageView and associate it with the image
      ImageView imageView = new ImageView(image);

      // Store the imageView in the IMAGES map, using the part of the imagePath after the last '/' and before the first '.'
      String imageName = imagePath.substring(imagePath.lastIndexOf('/') + 1, imagePath.indexOf('.'));
      instance.IMAGES.put(imageName, imageView);

      System.out.println("Successfully loaded image: " + image);
    } catch (Exception e) {
      System.err.println("Failed to load image from path: " + imagePath);
      e.printStackTrace();
    }
  }


  //////////////////////////////////////////////////
  ///////////// LOAD ALL THE LEVELS HERE ///////////
  //////////////////////////////////////////////////

  public static void loadAllLevels(String levelDirectory) {
    try {
      // Get the directory as a URL
      URL resourceUrl = GameSourceLoader.class.getClassLoader().getResource(levelDirectory);
      if (resourceUrl == null) {
        throw new IOException("Directory not found: " + levelDirectory);
      }

      if (resourceUrl.toString().startsWith("jar")) {
        // Handle resources inside a JAR
        String jarPath = resourceUrl.getPath().substring(5, resourceUrl.getPath().indexOf("!"));
        System.out.println("Loading levels from JAR: " + jarPath);
        try (JarFile jarFile = new JarFile(new File(jarPath))) {
          jarFile.stream()
              .filter(entry -> isValidLevelEntry(entry, levelDirectory))
              .forEach(entry -> loadLevelFromJar(entry));
        }
      } else {
        // Handle resources on the filesystem
        Path directoryPath = Paths.get(resourceUrl.toURI());
        Files.list(directoryPath)
            .filter(Files::isRegularFile)
            .filter(file -> file.getFileName().toString().endsWith(".level"))
            .forEach(file -> loadLevelFromFile(file));
      }
    } catch (Exception e) {
      System.err.println("Failed to load levels from directory: " + levelDirectory);
      e.printStackTrace();
    }
  }

  /**
   * Checks if the JarEntry is a valid level file in the specified directory.
   */
  private static boolean isValidLevelEntry(JarEntry entry, String directory) {
    return !entry.isDirectory()
        && entry.getName().startsWith(directory)
        && entry.getName().endsWith(".level");
  }

  /**
   * Loads a level from a JAR entry.
   */
  private static void loadLevelFromJar(JarEntry entry) {
    String levelPath = entry.getName();
    try (InputStream is = GameSourceLoader.class.getClassLoader().getResourceAsStream(levelPath)) {
      if (is != null) {
        loadLevelFromStream(levelPath, is);
      } else {
        System.err.println("Failed to load level from JAR: " + levelPath);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads a level from a file on the filesystem.
   */
  private static void loadLevelFromFile(Path file) {
    try (InputStream is = Files.newInputStream(file)) {
      loadLevelFromStream(file.toString(), is);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Loads level data from an InputStream.
   */
  private static void loadLevelFromStream(String levelPath, InputStream inputStream) {
    try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
      String line;
      StringBuilder levelData = new StringBuilder();
      while ((line = reader.readLine()) != null) {
        levelData.append(line).append("\n");
      }

      String levelName = Paths.get(levelPath).getFileName().toString().split("\\.")[0];
      instance.LEVELS.put(levelName, parseLevelData(levelData.toString()));
      System.out.println("Loaded level: " + levelName);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Parses level data from a string, extracting only the portion between @BEGIN and @END.
   */
  private static char[][] parseLevelData(String levelData) {
    // Split the input data into lines
    String[] lines = levelData.split("\n");
    ArrayList<ArrayList<Character>> levelDataList = new ArrayList<>();
    boolean inLevelData = false;

    // Iterate through each line of the data
    for (String line : lines) {
      String trimmedLine = line.trim();

      // Skip empty lines or comment lines
      if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
        continue;
      }

      // Start reading level data after @BEGIN
      if (trimmedLine.equals("@BEGIN")) {
        inLevelData = true;
        continue;
      }

      // Stop reading level data at @END
      if (trimmedLine.equals("@END")) {
        break;
      }

      // Only parse the level data between @BEGIN and @END
      if (inLevelData) {
        // Split the line by commas and convert it to a character array
        String[] values = trimmedLine.split(",");
        ArrayList<Character> rowData = new ArrayList<>();
        for (String value : values) {
          value = value.trim();
          if (value.length() == 1) {
            rowData.add(value.charAt(0));
          } else {
            throw new IllegalArgumentException("Invalid value found: " + value);
          }
        }
        levelDataList.add(rowData);
      }
    }

    // Find the maximum number of columns in any row
    int maxCols = 0;
    for (ArrayList<Character> rowData : levelDataList) {
      maxCols = Math.max(maxCols, rowData.size());
    }

    // Convert the ArrayList to a 2D char array
    char[][] levelArray = new char[levelDataList.size()][maxCols];
    for (int i = 0; i < levelDataList.size(); i++) {
      ArrayList<Character> rowData = levelDataList.get(i);
      for (int j = 0; j < rowData.size(); j++) {
        levelArray[i][j] = rowData.get(j);
      }
    }
    return levelArray;
  }


  ////////////////////////////////////////////////////
  ///////////// LOAD ALL THE PROPERTIES HERE //////////
  ////////////////////////////////////////////////////

  /**
   * Save a serializable object to the configuration file
   */
  public static void saveSerializableProperty(String name, Serializable obj) {
    String configFile = "config.properties";
    Properties properties = new Properties();
    File file = new File(configFile);
    if (!file.exists()) {
      try {
        if (!file.createNewFile()) {
          throw new RuntimeException("Cannot create configuration file: " + configFile);
        }
      } catch (IOException e) {
        throw new RuntimeException("Cannot create configuration file: " + configFile, e);
      }
    }

    try (FileInputStream input = new FileInputStream(configFile)) {
      properties.load(input);
    } catch (IOException e) {
      System.out.println("Failed to load the configuration file. Proceeding with an empty configuration.");
    }

    try (FileOutputStream output = new FileOutputStream(configFile)) {
      ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
      ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
      objectStream.writeObject(obj);
      objectStream.close();
      String serializedObject = Base64.getEncoder().encodeToString(byteStream.toByteArray());

      properties.setProperty(name, serializedObject);
      properties.store(output, "Serialized Properties");
      instance.PROPERTIES.put(name, obj);
    } catch (IOException e) {
      throw new RuntimeException("Cannot store the serialized properties: " + name, e);
    }
  }


  /**
   * Read all serializable objects from the configuration file and store them in PROPERTIES
   */
  public static void loadAllSerializableProperties() {
    String configFile = "config.properties";
    Properties properties = new Properties();
    File file = new File(configFile);
    if (!file.exists()) {
      System.out.println("Configuration file does not exist: " + configFile);
      return;
    }

    try (FileInputStream input = new FileInputStream(configFile)) {
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Cannot load the configuration file: " + configFile, e);
    }

    for (String name : properties.stringPropertyNames()) {
      String serializedObject = properties.getProperty(name);
      try {
        byte[] data = Base64.getDecoder().decode(serializedObject);
        ObjectInputStream objectStream = new ObjectInputStream(new ByteArrayInputStream(data));
        Object obj = objectStream.readObject();
        instance.PROPERTIES.put(name, obj);
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException("Cannot deserialize the object: " + name, e);
      }
    }
  }
}
