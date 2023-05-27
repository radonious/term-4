package com.example.lab1;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class Worker extends Bee implements Serializable {
    public Worker(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime, V);
        double z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double k = z / V;
        Vx = -x / k;
        Vy = -y / k;
    }

    public void rotateVelocity() {
        this.Vx = -Vx;
        this.Vy = -Vy;
    }

    public double getVx() {
        return Vx;
    }

    public double getVy() {
        return Vy;
    }

    private double Vx, Vy;
}