package root.scenes;

import javafx.stage.Modality;
import root.interfaces.ScenePropertiesInt;

import java.util.EnumMap;
import java.util.HashMap;


/**
 * The type Scene properties.
 */
public class SceneProperties implements ScenePropertiesInt {

    /**
     * The State map.
     */
    protected final EnumMap<SceneName, HashMap<String, String>> stateMap;

    /**
     * Instantiates a new Scene properties.
     */
    public SceneProperties() {
        this.stateMap = new EnumMap<SceneName, HashMap<String, String>>(SceneName.class);
        this.setStateMaps();
    }

    /**
     * Set state maps.
     */
    private void setStateMaps(){
        this.stateMap.put(SceneName.MAIN, this.mainWindow());
        this.stateMap.put(SceneName.NOTES, this.notesWindow());
        this.stateMap.put(SceneName.DRAW, this.drawWindow());
        this.stateMap.put(SceneName.LOAD, this.loadWindow());
        this.stateMap.put(SceneName.EXAMPLES, this.examplesWindow());
        this.stateMap.put(SceneName.SETTINGS, this.settingsWindow());
        this.stateMap.put(SceneName.HELP, this.helpWindow());
        this.stateMap.put(SceneName.EXIT, this.exitWindow());
    }

    /**
     * Main window hash map.
     * @return the hash map
     */
    private HashMap<String, String> mainWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Main");
        window.put("resizable", String.valueOf(false));
        window.put("maximized", String.valueOf(false));
        window.put("modality", "");
        return window;
    }

    /**
     * Notes window hash map.
     * @return the hash map
     */
    private HashMap<String, String> notesWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Notes");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(false));
        window.put("modality", "");
        return window;
    }

    /**
     * Draw window hash map.
     * @return the hash map
     */
    private HashMap<String, String> drawWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Draw");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(true));
        window.put("modality", "");
        return window;
    }

    /**
     * Load window hash map.
     * @return the hash map
     */
    private HashMap<String, String> loadWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Load Graph File");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(false));
        window.put("modality", "");
        return window;
    }

    /**
     * Examples window hash map.
     * @return the hash map
     */
    private HashMap<String, String> examplesWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Example Graphs");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(true));
        window.put("modality", "");
        return window;
    }

    /**
     * Help window hash map.
     *
     * @return the hash map
     */
    private HashMap<String, String> helpWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Help");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(false));
        window.put("modality", "");
        return window;
    }

    /**
     * Settings window hash map.
     *
     * @return the hash map
     */
    private HashMap<String, String> settingsWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Settings");
        window.put("resizable", String.valueOf(true));
        window.put("maximized", String.valueOf(false));
        window.put("modality", "");
        return window;
    }

    /**
     * Exit window hash map.
     * @return the hash map
     */
    private HashMap<String, String> exitWindow(){
        HashMap<String, String> window = new HashMap<>();
        window.put("title", "Graph Algorithm Animation App | Exit Application");
        window.put("resizable", String.valueOf(false));
        window.put("maximized", String.valueOf(false));
        window.put("modality", String.valueOf(Modality.APPLICATION_MODAL));
        return window;
    }

    public HashMap<String, String> getPropsMap(SceneName scene){
        return this.stateMap.get(scene);
    }

}