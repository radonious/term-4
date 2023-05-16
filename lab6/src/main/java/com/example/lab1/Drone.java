package com.example.lab1;

import java.io.FileNotFoundException;
import java.io.Serializable;

public class Drone extends Bee implements Serializable {
    public Drone(double x, double y, String imgPath, int id, long bornTime, double deathTime, int V) throws FileNotFoundException {
        super(x, y, imgPath, id, bornTime, deathTime, V);
    }
}