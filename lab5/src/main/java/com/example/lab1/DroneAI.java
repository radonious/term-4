package com.example.lab1;

import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class DroneAI extends BaseAI {
    public DroneAI(int FPS, int x, int y) {
        super(FPS, x, y);
    }

    public DroneAI(int x, int y) {
        super(x, y);
    }

    @Override
    public void initFilter() {
        this.filter = object -> object instanceof Drone;
    }

    @Override
    public void initMover() {
        this.mover = object -> {
            Drone bee = (Drone) object;
            ImageView view = bee.getView();
            int V = bee.getV();
            double x = view.getX();
            double y = view.getY();
            x += Math.cos(ang) * V;
            y += Math.sin(ang) * V;
            double finalX = Math.max(0, Math.min(max_x - 80, x));
            double finalY = Math.max(0, Math.min(max_y - 55, y));
            bee.setX_curr(finalX);
            bee.setY_curr(finalY);
            Platform.runLater(() -> view.setX(finalX));
            Platform.runLater(() -> view.setY(finalY));
        };
    }

    public void setAng(int tmp) {
        ang = tmp;
    }

    private int ang;
}
