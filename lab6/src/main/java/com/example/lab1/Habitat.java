package com.example.lab1;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class Habitat implements Serializable {

    private static final String imgWorker, imgDrone;
    private static final String gifWorker, gifDrone;
    private int N1, N2, K, V, N;
    private double deathDrone, deathWorker;
    private float P;
    private ArrayList<Bee> beeList;
    private HashSet<Integer> idSet;
    private TreeMap<Integer, Integer> bornTree;
    private long simulationTime;

    static {
        imgWorker = "src/main/resources/com/example/lab1/img/Worker.png";
        imgDrone = "src/main/resources/com/example/lab1/img/Drone.png";
        gifWorker = "src/main/resources/com/example/lab1/gif/GWorker.gif";
        gifDrone = "src/main/resources/com/example/lab1/gif/GDrone.gif";
    }

    public Habitat() {
        N1 = 1;
        N2 = 1;
        K = 30;
        V = 3;
        N = 3;
        P = 0.80f;
        initData();
    }

    public void initData() {
        beeList = BeeData.getInstance().getList();
        idSet = BeeData.getInstance().getSet();
        bornTree = BeeData.getInstance().getTree();
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

    public void setDeathDrone(double tmp) {
        deathDrone = tmp;
    }

    public void setDeathWorker(double tmp) {
        deathWorker = tmp;
    }

    public void setBeeList(ArrayList<Bee> beeList) {
        this.beeList = beeList;
    }

    public void setIdSet(HashSet<Integer> idSet) {
        this.idSet = idSet;
    }

    public void setBornTree(TreeMap<Integer, Integer> bornTree) {
        this.bornTree = bornTree;
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

    public double getDeathDrone() {
        return deathDrone;
    }

    public double getDeathWorker() {
        return deathWorker;
    }

    public ArrayList<Bee> getList() {
        return beeList;
    }

    public void setList(ArrayList<Bee> tmp) {
        this.beeList = tmp;
    }

    public HashSet<Integer> getSet() {
        return idSet;
    }

    public TreeMap<Integer, Integer> getTree() {
        return bornTree;
    }

    public String getImgWorker() {
        return imgWorker;
    }

    public String getImgDrone() {
        return imgDrone;
    }

    public String getGifWorker() {
        return gifWorker;
    }

    public String getGifDrone() {
        return gifDrone;
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

    public Bee create(double xmax, double ymax, Class type, long time) throws FileNotFoundException {
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
            res = new Drone(x, y, gifDrone, id, time, deathDrone, V);
        }

        if (type == Worker.class) {
            res = new Worker(x, y, gifWorker, id, time, deathWorker, V);
        }

        beeList.add(res);
        bornTree.put(id, (int) time);

        return res;
    }

    public long getSimulationTime() {
        return simulationTime;
    }

    public void setSimulationTime(long simulationTime) {
        this.simulationTime = simulationTime;
    }
}