package root.interfaces;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import root.scenes.SceneData;
import root.scenes.SceneName;

import java.io.IOException;

/**
 * The interface Scene switcher int.
 */
public interface SceneSwitcherInt {
    /**
     * Swith scene.
     *
     * @param actionEvent the action event
     * @param sd          the sd
     * @throws IOException the io exception
     */
    void swithScene(ActionEvent actionEvent, SceneData sd) throws IOException;

    /**
     * Load selected window.
     *
     * @param sceneType   the scene type
     * @param sd          the sd
     * @param actionEvent the action event
     * @param title       the title
     * @param resizable   the resizable
     * @param maximized   the maximized
     * @param modality    the modality
     * @param path        the path
     * @throws IOException the io exception
     */
    void loadSelectedWindow(SceneName sceneType, SceneData sd, ActionEvent actionEvent,
                            String title, Boolean resizable, Boolean maximized,
                            Modality modality, String path) throws IOException;
}
