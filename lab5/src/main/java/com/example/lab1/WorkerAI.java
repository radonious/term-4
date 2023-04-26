package com.example.lab1;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class WorkerAI extends BaseAI {
    public WorkerAI(int FPS, int x, int y) {
        super(FPS, x, y);
    }

    public WorkerAI(int x, int y) {
        super(60, x, y);
    }

    @Override
    public void initFilter() {
        this.filter = object -> object instanceof Worker;
    }

    @Override
    public void initMover() {
        this.mover = object -> {
            Worker bee = (Worker) object;
            ImageView view = bee.getView();
            double x = view.getX(), y = view.getY();
            if ((x < 0 && y < 0) || (x > bee.getX_start() && y > bee.getY_start())) bee.rotateVelocity();
            x += bee.getVx();
            y += bee.getVy();
            double finalX = x;
            double finalY = y;
            bee.setX_curr(finalX);
            bee.setY_curr(finalY);
            Platform.runLater(() -> view.setX(finalX));
            Platform.runLater(() -> view.setY(finalY));
        };
    }
}
