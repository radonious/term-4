package com.example.lab1;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MyApp extends Application {

    private final Habitat mainHabitat = new Habitat();

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(Habitat.class.getResource("MyApp.fxml"));
        Pane pane = loader.load();
        Scene scene = new Scene(pane);
        Controller mainController = loader.getController();
        mainController.setHabitat(mainHabitat);
        mainController.setStage(stage);

        stage.setWidth(700);
        stage.setHeight(630);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.setTitle("Bee simulation");
        stage.show();
    }
}