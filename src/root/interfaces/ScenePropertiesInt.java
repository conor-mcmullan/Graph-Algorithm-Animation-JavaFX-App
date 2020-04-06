package root.interfaces;

import root.scenes.SceneName;

import java.util.HashMap;

/**
 * The interface Scene properties int.
 */
public interface ScenePropertiesInt {
    /**
     * Gets props map.
     *
     * @param scene the scene
     * @return the props map
     */
    HashMap<String, String> getPropsMap(SceneName scene);
}
