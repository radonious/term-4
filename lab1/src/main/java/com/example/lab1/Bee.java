package com.example.lab1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Bee implements IBehaviour {

    Bee(double x, double y, String imgPath) throws FileNotFoundException {
        Image img = new Image(new FileInputStream(imgPath));
        view = new ImageView(img);
        view.setX(x);
        view.setY(y);
        view.setFitHeight(100);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
    }

    public ImageView getView() { return this.view; }

    private ImageView view;
}
