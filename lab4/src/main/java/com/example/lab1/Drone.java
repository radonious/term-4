package com.example.lab1;

import java.io.FileNotFoundException;

public class Drone extends Bee implements IBehaviour {
    public Drone(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime, V);
    }
}