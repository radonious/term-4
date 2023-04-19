package com.example.lab1;

import java.io.FileNotFoundException;
import java.util.*;

public class Habitat {

    private static final String imgPath, imgPath2;
    private int N1, K, N2, V, N;
    private float P, angleN;
    private static ArrayList<Bee> beeList;
    private static HashSet<Integer> idSet;
    private static TreeMap<Integer, Integer> bornTree;

    static {
        imgPath = "src/main/resources/com/example/lab1/img/zhzh.png";
        imgPath2 = "src/main/resources/com/example/lab1/img/pchel.png";
        beeList = BeeData.getInstance().getList();
        idSet = BeeData.getInstance().getSet();
        bornTree = BeeData.getInstance().getTree();
    }

    public Habitat() {
        N1 = 1;
        N2 = 1;
        K = 30;
        V = 3;
        N = 3;
        angleN = (float) (0f * Math.PI / 180);
        P = 0.80f;
    }

    public int getN1() {
        return N1;
    }

    public int getN2() {
        return N2;
    }

    public int getK() {
        return K;
    }

    public int getV() {
        return V;
    }

    public int getN() {
        return N;
    }

    public float getP() {
        return P;
    }

    public float getAngleN() {
        return angleN;
    }

    public void setN1(int tmp) {
        N1 = tmp;
    }

    public void setN2(int tmp) {
        N2 = tmp;
    }

    public void setK(int tmp) {
        K = tmp;
    }

    public void setV(int tmp) {
        V = tmp;
    }

    public void setN(int tmp) {
        N = tmp;
    }

    public void setP(float tmp) {
        P = tmp;
    }

    public void setAngleN(float tmp) {
        angleN = tmp;
    }

    public ArrayList<Bee> getList() {
        return beeList;
    }

    public HashSet<Integer> getSet() {
        return idSet;
    }

    public TreeMap<Integer, Integer> getTree() {
        return bornTree;
    }

    public void clearData() {
        beeList.clear();
        idSet.clear();
        bornTree.clear();
    }

    public int getDronsSize() {
        return (int) beeList.stream().filter(Object -> Object.getClass() == Drone.class).count();
    }

    public int getWorkersSize() {
        return (int) beeList.stream().filter(Object -> Object.getClass() == Worker.class).count();
    }

    public Bee create(double xmax, double ymax, Class type, long time, double deathTime) throws FileNotFoundException {
        Random r = new Random();
        Bee res = null;

        int id = r.nextInt((int) 10000);
        while (idSet.contains(id)) {
            id = r.nextInt((int) 10000);
        }
        idSet.add(id);

        int x = r.nextInt((int) xmax - 140);
        int y = r.nextInt((int) ymax - 10);

        if (type == Drone.class) {
            res = new Drone(x, y, imgPath2, id, time, deathTime, V);
        }

        if (type == Worker.class) {
            res = new Worker(x, y, imgPath, id, time, deathTime, V);
        }

        beeList.add(res);
        bornTree.put(id, (int) time);

        return res;
    }
}