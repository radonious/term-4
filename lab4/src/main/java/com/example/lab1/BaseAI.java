package com.example.lab1;

import java.util.ArrayList;
import java.util.function.Consumer;
import java.util.function.Predicate;

public abstract class BaseAI {
    private Thread moveTh;
    private final ArrayList objList;
    private boolean toggleFlag;
    private final Object locker;

    protected int FPS, max_x, max_y;
    protected Predicate filter;
    protected Consumer mover;

    public BaseAI(int FPS, int x, int y) {
        locker = new Object();
        this.FPS = FPS;
        max_x = x;
        max_y = y;
        objList = BeeData.getInstance().getList();
        initMover();
        initFilter();
        initMoveThread();
        moveTh.setDaemon(true);
    }

    public BaseAI(int x, int y) {
        this(60, x, y);
    }

    public abstract void initFilter();

    public abstract void initMover();

    private void initMoveThread() {
        moveTh = new Thread(() -> {
            double freq = 1000 / FPS;
            double delay = (freq - (int) freq) * 1000000;
            while (true) {
                synchronized (locker) {
                    while (!toggleFlag) {
                        try {
                            System.out.println("Thread stopped: " + Thread.currentThread());
                            locker.wait();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
                synchronized (objList) {
                    if (!objList.isEmpty()) {
                        objList.stream().filter(filter).forEach(mover);
                    }
                }
                try {
                    Thread.sleep((int) freq, (int) delay);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    public void startAI() {
        if (moveTh.getState() == Thread.State.NEW) {
            moveTh.start();
        } else {
            resumeAI();
        }
    }

    public void pauseAI() {
        toggleFlag = false;
    }

    public void resumeAI() {
        synchronized (locker) {
            toggleFlag = true;
            locker.notifyAll();
        }
    }
}