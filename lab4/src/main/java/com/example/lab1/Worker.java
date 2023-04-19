package com.example.lab1;

import java.io.FileNotFoundException;

public class Worker extends Bee implements IBehaviour {
    public Worker(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime, V);
        double z = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        double k = z / V;
        Vx = -x / k;
        Vy = -y / k;
        x_start = x;
        y_start = y;
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

    public double getX_start() {
        return x_start;
    }

    public double getY_start() {
        return y_start;
    }

    private double x_start, y_start;
    private double Vx, Vy;
}