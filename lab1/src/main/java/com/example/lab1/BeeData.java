package com.example.lab1;

import java.util.ArrayList;

public class BeeData {
    private static volatile BeeData beeData;
    private ArrayList<Drone> DroneList;
    private ArrayList<Worker> WorkerList;

    // Hide constr
    private BeeData() {
        DroneList = new ArrayList<Drone>();
        WorkerList = new ArrayList<Worker>();
    }
    // Hide copy
    private BeeData(BeeData donor) {};
    // Make instance
    public static BeeData getInstance() {
        if (beeData == null) {
            synchronized (BeeData.class) {
                if (beeData == null) {
                    beeData = new BeeData();
                }
            }
        }
        return beeData;
    }
    // Get Lists
    public ArrayList<Drone> getDroneList() {
        return DroneList;
    }

    public ArrayList<Worker> getWorkerList() {
        return WorkerList;
    }
}
