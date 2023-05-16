package com.example.lab1;

import java.util.*;

public class BeeData {
    private static volatile BeeData beeData;

    private ArrayList<Bee> beeList;
    private HashSet<Integer> idSet;
    private TreeMap<Integer, Integer> bornTree;

    private BeeData() {
        beeList = new ArrayList<Bee>();
        idSet = new HashSet<Integer>();
        bornTree = new TreeMap<Integer, Integer>();
    }

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

    public ArrayList<Bee> getList() {
        return beeData.beeList;
    }

    public HashSet<Integer> getSet() {
        return beeData.idSet;
    }

    public TreeMap<Integer, Integer> getTree() {
        return beeData.bornTree;
    }

    public void setList(ArrayList<Bee> tmp) {
        beeData.beeList = tmp;
    }

    public void setSet(HashSet<Integer> tmp) {
        beeData.idSet = tmp;
    }

    public void setTree(TreeMap<Integer, Integer> tmp) {
        beeData.bornTree = tmp;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}