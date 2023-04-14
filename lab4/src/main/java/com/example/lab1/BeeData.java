package com.example.lab1;

import java.util.*;

public class BeeData {
    private static volatile BeeData beeData;

    // data
    private ArrayList<Bee> beeList;
    private HashSet<Integer> idSet;
    private TreeMap<Integer, Integer> bornTree;

    // Hide constructor
    private BeeData() {
        beeList = new ArrayList<Bee>();
        idSet = new HashSet<Integer>();
        bornTree = new TreeMap<Integer, Integer>();
    }

    // Hide copy
    private BeeData(BeeData donor) {};

    // Instance
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
    // Getters
    public ArrayList<Bee> getList() {
        return beeList;
    }
    public HashSet<Integer> getSet() {
        return idSet;
    }
    public TreeMap<Integer,Integer> getTree() {
        return bornTree;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}