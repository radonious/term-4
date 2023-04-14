package com.example.lab1;

import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;

public class Drone extends Bee {
    public Drone(double x, double y, String imgPath, int id, long bornTime, double deathTime) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime);
    }

    public synchronized void move(int V, float ang, double max_x, double max_y) {
        new Thread(new BaseAI() {
            @Override
            public void run() {
                ImageView view = Drone.super.getView();
                double x = view.getX();
                double y = view.getY();
                x += Math.cos(ang) * V;
                y += Math.sin(ang) * V;
                x = Math.max(0, Math.min(max_x, x));
                y = Math.max(0, Math.min(max_y, y));
                view.setX(x);
                view.setY(y);
            }
        }).start();
    }
}