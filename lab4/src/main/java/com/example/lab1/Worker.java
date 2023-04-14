package com.example.lab1;

import javafx.scene.image.ImageView;

import java.io.FileNotFoundException;

public class Worker extends Bee {
    public Worker(double x, double y, String imgPath, int id, long bornTime, double deathTime) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime);
        x_start = x;
        y_start = y;
        moveDirection = -1;
    }

    public synchronized void move(int V) {
        new Thread(new BaseAI() {
            @Override
            public void run() {
                ImageView view = Worker.super.getView();
                double x = view.getX();
                double y = view.getY();
                double z = Math.sqrt(Math.pow(x,2) + Math.pow(y,2));
                double k = z / V;
                x = Math.max(1/y_start,x + x / k * moveDirection);
                y = Math.max(1/x_start,y + y / k * moveDirection);
                if (x < 1 && y < 1) moveDirection = 1;
                else if (x >= x_start && y >= y_start) moveDirection = -1;
                view.setX(x);
                view.setY(y);
            }
        }).start();
    };
    double x_start;
    double y_start;
    int moveDirection;
}