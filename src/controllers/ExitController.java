package controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import root.scenes.SceneData;

/**
 * The type Exit controller.
 */
public class ExitController extends AbstractController {

    /**
     * The Btn confirm exit.
     */
    @FXML private Button btnConfirmExit;
    /**
     * The Btn cancel exit.
     */
    @FXML private Button btnCancelExit;

    /**
     * Confirm exit.
     *
     * @param actionEvent the action event
     */
    public void confirmExit(ActionEvent actionEvent){
        Platform.exit();
    }

    /**
     * Cancel exit.
     *
     * @param actionEvent the action event
     */
    public void cancelExit(ActionEvent actionEvent){
        Stage window = (Stage)((Node)actionEvent.getSource()).getScene().getWindow();
        window.close();
    }

    @Override
    public void init_data(SceneData x) {

    }
}
