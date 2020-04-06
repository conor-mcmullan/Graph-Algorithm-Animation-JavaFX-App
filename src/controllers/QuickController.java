package controllers;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import root.scenes.SceneData;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * The type Quick controller.
 */
public class QuickController extends AbstractController implements Initializable{

    /**
     * The Lbl edit me.
     */
    @FXML private Label lblEditMe;
    /**
     * The Main panel.
     */
    @FXML private Pane MainPanel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        System.out.println("init called");
        MainPanel.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                System.out.println(mouseEvent.getX());
                System.out.println(mouseEvent.getY());

                Circle c = new Circle(mouseEvent.getX(), mouseEvent.getY(), 20);
                c.setStroke(Color.RED);
                c.setRadius(20);
                c.setStrokeWidth(2);

                // do i have tp get the parent object and add to it ?
                MainPanel.getChildren().add(c);


            }
        });

    }

    /**
     * My function.
     *
     * @param s the s
     */
    public void myFunction(String s){
        lblEditMe.setText(s);
    }

    @Override
    public void init_data(SceneData x) {

    }

}
