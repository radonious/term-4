package com.example.lab1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public abstract class Bee {

    Bee(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        Image img = new Image(new FileInputStream(imgPath));
        view = new ImageView(img);
        view.setX(x);
        view.setY(y);
        view.setFitHeight(100);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
        this.id = id;
        this.bornTime = (int) bornTime;
        this.deathTime = (int) deathTime;
        this.V = V;
        moveDirection = -1;
    }

    public ImageView getView() {
        return this.view;
    }

    public void setView(ImageView tmp) {
        view = tmp;
    }

    private ImageView view;

    public int getId() {
        return id;
    }

    public int getBornTime() {
        return bornTime;
    }

    public int getDeathTime() {
        return deathTime;
    }

    public int getV() {
        return V;
    }

    private int id;
    private int bornTime;
    private int deathTime;
    int V;
    int moveDirection;
}
