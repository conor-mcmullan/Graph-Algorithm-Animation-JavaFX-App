package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import root.FileHandler;
import root.scenes.SceneData;
import root.scenes.SceneSwitcher;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * The interface Interface controller.
 */
public interface InterfaceController {

    /** {@code SceneSwitcher} {@code Object} allowing transition to a new {@code Scene}. */
    SceneSwitcher wm = new SceneSwitcher();

    /** A {@code FileHandler} that allows ability to save and load new files. */
    FileHandler fh = new FileHandler();

    /** {@code T} generify type {@code Class} data holder Object to move data between {@code Controllers}. */
    SceneData sd = new SceneData();

    /** The {@code Scene} data map. */
    HashMap<String, ArrayList<String>> sceneDataMap = new HashMap<>();

    /**
     * Initialize.
     * Implements the function, but abstracts the functionality. Must include the use of Platform.runLater().
     * This is needed to hold the data 'that is alive' within the running Thread and transfer it into memory,
     * into a new object within the next running Thread so that the data can be accessed by the requested Scene.
     */
    void initialize();

    /**
     * Run later.
     * Utilises the ability to hold off the garbage collector with late code execution on a sleeper Thread.
     */
    void runLater();

    /**
     * Init data.
     * The function that is used to unpack transferred generfied type T
     * data from scene to scene into a specified HashMap.
     * @param sd the sd
     */
    void init_data(SceneData sd);

    /**
     * Move window.
     * EventHandler implemented and abstracted out is now accessible to all JavaFX Objects allowing,
     * multiple Scenes to be loaded and handled using a standard function.
     * @param actionEvent the action event
     * @throws IOException the io exception
     */
    void move_window(ActionEvent actionEvent) throws IOException;

}
