package com.example.lab1;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ModalWindow {
    public static void newWindow() {
        Stage window = new Stage();
        window.initModality(Modality.APPLICATION_MODAL);
        Pane pane = new Pane();

        Button btnClose = new Button("Close");
        btnClose.setOnAction(actionEvent -> window.close());

        Button btnContinue = new Button("Continue");
        btnContinue.setOnAction(actionEvent -> window.close());

        pane.getChildren().add(btnClose);
        pane.getChildren().add(btnContinue);

        Scene scene = new Scene(pane, 200, 100);
        window.setScene(scene);
        //window.setTitle(title);
        window.showAndWait();
    }
}
