package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import root.FileHandler;
import root.scenes.SceneData;
import root.scenes.SceneSwitcher;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * {@code AbstractController}
 * Implementing {@code abstract} functions that must be included using {@code interface},
 * but do not ned to be the definition provided.
 */
public abstract class AbstractController implements InterfaceController{

    /** {@code SceneSwitcher} {@code Object} allowing transition to a new {@code Scene}. */
    SceneSwitcher wm = new SceneSwitcher();

    /** A {@code FileHandler} that allows ability to save and load new files. */
    FileHandler fh = new FileHandler();

    /** {@code T} generify type {@code Class} data holder Object to move data between {@code Controllers}. */
    SceneData sd = new SceneData();

    /** The {@code Scene} data map. */
    HashMap<String, ArrayList<String>> sceneDataMap;

    /** {@code FileChooser}. */
    FileChooser fileChooser = new FileChooser();

    public void initialize(){
        this.runLater();
    }

    public void runLater(){ Platform.runLater(() -> {}); }

    @SuppressWarnings("unchecked")
    public void init_data(SceneData sd){
        this.sd = sd;
        if (sd.getAll().size() > 0){
            this.sceneDataMap = new HashMap<String, ArrayList<String>> (sd.getAll());
        }
    }

    public void move_window(ActionEvent actionEvent) throws IOException {
        String btnId = ((Node) actionEvent.getSource()).getId();
        if (btnId.toLowerCase().contains("load")){
            this.loadAction(actionEvent);
        }
        else {
            if (this.movingWindowWithExistingData()){
                this.wm.swithScene(actionEvent, this.sd);
            }
        }
    }

    /**
     * Moving window with existing data boolean.
     *
     * @return the boolean
     */
    private boolean movingWindowWithExistingData() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.NO, ButtonType.YES);
        alert.setTitle("Leaving Drawing Scene");
        alert.setHeaderText("Scene Has Existing Data:\tAction To Move Window Selected");
        alert.setContentText("The scene currently has existing data consider saving before exiting this Scene."+
                             "\nOptions:" +
                             "\n\tYES = Continue to new window" +
                             "\n\tNO = Remain on this Scene with the same data");
        if (this.sd.getAll().size()>0) {
            alert.showAndWait();
        }
        else if(this.sd.getAll().size()==0){
            // there is no data at all yet, continued move is allowed
            return true;
        }

        if (alert.getResult()==ButtonType.YES) {
            this.sd.getAll().clear();
            return true;
        }
        else if (alert.getResult()==ButtonType.NO) {
            return false;
        }
        return false;
    }

    /**
     * Load action.
     * Loading a Graph file into the in memory SceneData Type T and parsing
     * this back onto the Scene using the SceneSwitcher Object.
     * @param actionEvent the action event
     */
    @SuppressWarnings("unchecked")
    public void loadAction(ActionEvent actionEvent) {
        System.out.println("[LOAD] --- Load Graph & Animation Steps --- ");
        this.fileChooser.setTitle("Load Graph File");
        try {
            File selectedFile = this.fileChooser.showOpenDialog(null);
            if (selectedFile.exists()){
                String loadFilePath = selectedFile.getAbsolutePath();

                // load graph file into hashmap
                HashMap<String, ArrayList<String>> loadedFile = this.fh.readExampleFile(loadFilePath);

                // clear the existing scenedata
                this.sd.getAll().clear();

                // add the loaded data into the cleared out scene data
                for (String k: loadedFile.keySet()) {
                    this.sd.add(k, loadedFile.get(k));
                }

                this.wm.swithScene(actionEvent, this.sd);
            }
        }
        catch (Exception ex){
            System.out.println(ex.toString());
        }
    }

    /**
     * Log string.
     * UNUSED but debug appropriate STDOUT printer
     * @param s the s
     * @return the string
     */
    private String log(String s){
        System.out.println(s);
        return s;
    }

}
