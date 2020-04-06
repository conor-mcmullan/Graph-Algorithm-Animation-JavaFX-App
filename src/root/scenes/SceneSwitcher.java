package root.scenes;

import com.sun.tools.javac.Main;
import controllers.AbstractController;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import redis.clients.jedis.Jedis;
import root.OSDetails;

import java.awt.*;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.HashMap;


/**
 * The type Scene switcher.
 */
public class SceneSwitcher {

    /**
     * The Btn id scene map.
     */
    private HashMap<String, SceneName> btnIdSceneMap;
    /**
     * The Scene properties.
     */
    private SceneProperties sceneProperties;

    /**
     * Instantiates a new Scene switcher.
     */
    public SceneSwitcher(){
        this.btnIdSceneMap = new HashMap<String, SceneName>();
        this.sceneProperties = new SceneProperties();
        this.setBtnIdSceneMap();
    }

    /**
     * Set btn id scene map.
     */
    private void setBtnIdSceneMap(){
        btnIdSceneMap.put("main", SceneName.MAIN);
        btnIdSceneMap.put("notes", SceneName.NOTES);
        btnIdSceneMap.put("draw", SceneName.DRAW);
        btnIdSceneMap.put("load", SceneName.LOAD);
        btnIdSceneMap.put("examples", SceneName.EXAMPLES);
        btnIdSceneMap.put("help", SceneName.HELP);
        btnIdSceneMap.put("settings", SceneName.SETTINGS);
        btnIdSceneMap.put("exit", SceneName.EXIT);
    }

    /**
     * Get scene scene name.
     *
     * @param btnId the btn id
     * @return the scene name
     */
    private SceneName getScene(String btnId){
        return this.btnIdSceneMap.getOrDefault(btnId, SceneName.MAIN);
    }

    /**
     * Get scene properties hash map.
     *
     * @param scene the scene
     * @return the hash map
     */
    private HashMap<String, String> getSceneProperties(SceneName scene){
        return this.sceneProperties.getPropsMap(scene);
    }

    /**
     * Swith scene.
     *
     * @param actionEvent the action event
     * @param sd          the sd
     * @throws IOException the io exception
     */
    public void swithScene(ActionEvent actionEvent, SceneData sd) throws IOException {
        Button btn = (Button) actionEvent.getSource();
        String id = btn.toString().contains("btn_") ? btn.getId().replace("btn_","").toLowerCase() : "draw";

        // there is no real load screen to it must go to draw (this will allow data to get deleted easy !)
        id = id.equalsIgnoreCase("load")||id.toLowerCase().contains("clear")?"draw":id;
        String path = "../../resources/~.fxml".replace("~", id);

        SceneName sceneType = this.getScene(id);
        HashMap<String, String> windowProperties = this.getSceneProperties(sceneType);
        String title = windowProperties.get("title");
        Boolean resizable = Boolean.valueOf(windowProperties.get("resizable"));
        Boolean maximized = Boolean.valueOf(windowProperties.get("maximized"));
        String modalityStr = windowProperties.get("modality");
        Modality modality = null;
        if (!modalityStr.isEmpty()){
            modality = Modality.valueOf(modalityStr);
        }
        try {
            this.loadSelectedWindow(sceneType, sd, actionEvent, title, resizable, maximized, modality, path);
        }catch (Exception e){
            System.out.println("\n*****\nERROR\n");
            System.out.println(e.getMessage());
            System.out.println("\n*****\n");
        }
    }

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
    public void loadSelectedWindow(SceneName sceneType, SceneData sd, ActionEvent actionEvent,
                                   String title, Boolean resizable, Boolean maximized,
                                   Modality modality, String path) throws IOException {
        if (!path.contains("exit")) ((Stage) ((Node) actionEvent.getSource()).getScene().getWindow()).close();
        String jarExecution = System.getenv().getOrDefault("JAR_EXECUTION", "FALSE");
        if (Boolean.parseBoolean(jarExecution.toLowerCase())){
            // Execution: JAR
            String[] directory = path.split("/");
            if (directory.length-1>-1){
                String filename = directory[directory.length-1];
                if (filename.contains(".fxml")){
                    path = String.format("/resources/%s", filename);
                }
            }
        }
        else{
            // Execution: IDE
        }
        FXMLLoader loader = new FXMLLoader();
        // TODO: perhaps this should be changed based on the operating system
        System.out.println(String.format("\nLOADING SCENE:\t%s\n", path));
        System.out.println(String.format("\nOS:\t%s", OSDetails.getOperatingSystemName()));
        loader.setLocation(getClass().getResource(path));
        try{
            // fade for n seconds
            FadeTransition fadeIn = new FadeTransition(Duration.seconds(1), loader.load());
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.setCycleCount(1);
            fadeIn.play();
        }catch (IOException e){
            e.printStackTrace();
        }
        // because all controller classes implements this method from Controller interface
        AbstractController e = loader.getController();
        if (sd.getAll().size() != 0) {
            e.init_data(sd);
        }
        Stage modalWindow = new Stage();
        String iconURL = "images/appIcon.png";
        // set the app icon
        modalWindow.getIcons().add(new Image(iconURL));
        Parent parent = loader.getRoot();
        Scene scene = new Scene(parent);
        if (scene != null) {
            modalWindow.setTitle(title);
            modalWindow.setScene(scene);
            modalWindow.setMaximized(maximized);
        }
        if (modality != null) {
            modalWindow.initModality(modality);
        }
        if (!modalWindow.isShowing()) {
            modalWindow.show();
            modalWindow.setResizable(resizable);
        }
    }
}
