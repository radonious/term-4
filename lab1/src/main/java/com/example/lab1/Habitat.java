package com.example.lab1;

import java.io.FileNotFoundException;
import java.util.*;


public class Habitat {

    private static final String imgPath, imgPath2;
    private int N1, K, N2;
    private float P;
    private static ArrayList<Worker> workers;
    private static ArrayList<Drone> drons;

    static {
        imgPath = "src/main/resources/com/example/lab1/img/zhzh.png";
        imgPath2 = "src/main/resources/com/example/lab1/img/pchel.png";
        drons = BeeData.getInstance().getDroneList();
        workers = BeeData.getInstance().getWorkerList();
    }

    public Habitat() {
        N1 = 1;
        N2 = 1;
        P = 0.80f;
        K = 30;
    }

    public int getN1()  { return N1;}
    public int getN2()  { return N2;}
    public int getK()   { return K; }
    public float getP() { return P; }

    public void setN1(int tmp) { N1 = tmp; }
    public void setN2(int tmp) { N2 = tmp; }
    public void setK(int tmp) { K = tmp; }
    public void setP(float tmp) { P = tmp; }

    public ArrayList<Drone> getDroneList() {
        return drons;
    }

    public void addDrone(Drone drone) {
        drons.add(drone);
    }

    public ArrayList<Worker> getWorkerList() {
        return workers;
    }

    public void addWorker(Worker worker) {
        workers.add(worker);
    }

    public void clearLists() {
        drons.clear();
        workers.clear();
    }

    public int getDronsSize() {
        return drons.size();
    }

    public int getWorkersSize() {
        return workers.size();
    }

    public Bee create(double xmax, double ymax, Class type) throws FileNotFoundException {
        Random r = new Random();
        int x,y;
        Bee res = null;

        if (type == Drone.class) {
            x = r.nextInt((int)xmax);
            y = r.nextInt((int)ymax - 110);
            res = new Drone(x,y, imgPath2);
            drons.add((Drone) res);
        }

        if (type == Worker.class) {
            x = r.nextInt((int)xmax);
            y = r.nextInt((int)ymax - 110);
            res = new Worker(x,y, imgPath);
            workers.add((Worker) res);
        }

        return res;
    }

}
