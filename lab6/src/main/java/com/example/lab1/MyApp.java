package com.example.lab1;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.junit.Test;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;

import static org.junit.Assert.assertEquals;

public class MyApp extends Application {

    private final Habitat mainHabitat = new Habitat();

    public static void main(String[] args) throws IOException {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException, ClassNotFoundException {
        FXMLLoader loader = new FXMLLoader(Habitat.class.getResource("MyApp.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        Controller mainController = loader.getController();
        mainController.setHabitat(mainHabitat);
        mainController.setStage(stage);
        mainController.loadConfigOnStart();

        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent e) {
                try {
                    mainController.closeRequest();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                Platform.exit();
                System.exit(0);
            }
        });

        stage.setWidth(900);
        stage.setHeight(630);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Bee simulation");
        stage.show();
    }
}