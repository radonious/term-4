package com.example.lab1;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.Serializable;

public abstract class Bee implements Serializable {

    Bee(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        this.y_start = y;
        this.x_start = x;
        this.y_curr = y;
        this.x_curr = x;
        Image img = new Image(new FileInputStream(imgPath));
        view = new ImageView(img);
        view.setX(x_curr);
        view.setY(y_curr);
        view.setFitHeight(100);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
        this.id = id;
        this.bornTime = (int) bornTime;
        this.deathTime = (int) deathTime;
        this.V = V;
    }

    public ImageView getView() {
        return this.view;
    }

    public void setView(ImageView tmp) {
        view = tmp;
    }

    public void syncPos() {
        view.setX(x_curr);
        view.setY(y_curr);
        view.setFitHeight(100);
        view.setFitWidth(100);
        view.setPreserveRatio(true);
    }

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

    public double getX_start() {
        return x_start;
    }

    public double getY_start() {
        return y_start;
    }

    public void setX_curr(double tmp) {
        this.x_curr = tmp;
    }

    public void setY_curr(double tmp) {
        this.y_curr = tmp;
    }

    public double getX_curr() {
        return x_curr;
    }

    public double getY_curr() {
        return y_curr;
    }

    private final double x_start;
    private final double y_start;
    private double x_curr;
    private double y_curr;
    private transient ImageView view;
    private final int id;
    private final int bornTime;
    private final int deathTime;
    int V;
}
