package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import root.scenes.SceneData;
import root.scenes.SceneSwitcher;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Main controller.
 */
public class MainController extends AbstractController implements Initializable{

    /**
     * The Btn notes.
     */
    @FXML private Button btn_notes;
    /**
     * The Btn draw.
     */
    @FXML private Button btn_draw;
    /**
     * The Btn load.
     */
    @FXML private Button btn_load;
    /**
     * The Btn examples.
     */
    @FXML private Button btn_examples;
    /**
     * The Btn help.
     */
    @FXML private Button btn_help;
    /**
     * The Btn settings.
     */
    @FXML private Button btn_settings;
    /**
     * The Btn exit.
     */
    @FXML private Button btn_exit;

    /**
     * The Sd.
     */
// data container
    private SceneData sd = new SceneData();
    /**
     * The Wm.
     */
    private SceneSwitcher wm = new SceneSwitcher();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) { }

    /**
     * Example.
     *
     * @throws IOException the io exception
     */
    public void example() throws IOException {
        String path ="../resources/quick.fxml";
        System.out.println("TRYING TO LOAD QUICK.FXML");
        System.out.println(path);

        FXMLLoader loader =  new FXMLLoader();
        loader.setLocation(getClass().getResource(path));
        try{
            loader.load();
        }catch (IOException e){
            e.printStackTrace();
        }

        QuickController qc = loader.getController();
        qc.myFunction("TEST DATA");

        Parent p = loader.getRoot();
        Stage s  = new Stage();
        s.setScene(new Scene(p));
        s.show();

    }

    @Override
    public void init_data(SceneData x) { }
}
