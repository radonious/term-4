package com.example.lab1;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class Client extends Thread {
    Controller clientController;
    int port;
    String ip;
    Socket socket;
    ArrayList<String> clients;
    ObjectInputStream in_obj;
    ObjectOutputStream out_obj;

    Client(Controller ctrl) {
        clientController = ctrl;
    }

    // При нажатии кнопки connect
    public boolean connect(String ip, int port) {
        boolean result = false;
        this.ip = ip;
        this.port = port;
        System.out.println("Connecting to " + ip + ":" + port);
        try {
            socket = new Socket(ip, port);
            out_obj = new ObjectOutputStream(socket.getOutputStream());
            in_obj = new ObjectInputStream(socket.getInputStream());
            System.out.println("Socket created. Connected to " + ip + ":" + port);
            result = true;
        } catch (IOException e) {
            System.out.println("Cannot connect: " + e);
        }
        return result;
    }

    // При успешном подключении к серверу
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                ClientDTO clientDTO = (ClientDTO) in_obj.readObject();
                handleDTO(clientDTO);
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("\nClient stop listening: " + e);
                disconnect();
            }
        }
    }

    // При нажатии кнопки disconnect
    public boolean disconnect() {
        boolean result = true;
        System.out.println("Disconnecting...");
        try {
            in_obj.close();
            out_obj.close();
            socket.close();
            System.out.println("Socket " + socket.getRemoteSocketAddress() + " closed. Disconnected");
            result = false;
        } catch (IOException e) {
            System.out.println("Cannot disconnect: " + e);
        }
        return result;
    }

    // При нажатию кнопки send
    public void sendDTO(ClientDTO clientDTO) {
        System.out.println("Trying to send DTO...");
        try {
            out_obj.writeObject(clientDTO);
            out_obj.flush();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("DTO was sent. Success");
    }


    // При принятии любого DTO от сервера
    public void handleDTO(ClientDTO clientDTO) throws FileNotFoundException {
        System.out.println("\nGot a new DTO. Handling...");
        System.out.println(clientDTO);
        if (clientDTO.getDtoType().equals(ClientDTO.dtoType.SERVER_REPLY) &&
                clientDTO.getDtoObject().equals(ClientDTO.dtoObject.CLIENT_LIST)) {
            clients = clientDTO.getClients();
            clientController.setConnections(clientDTO.getClients(), clientDTO.getToName());
        } else if (clientDTO.getDtoType().equals(ClientDTO.dtoType.SERVER_REPLY) &&
                clientDTO.getDtoObject().equals(ClientDTO.dtoObject.OBJECTS)) {
            synchronized (BeeData.getInstance().getList()) {
                for (Bee bee : clientDTO.getBeeList()) {
                    Image img = null;
                    if (bee.getClass().equals(Drone.class)) {
                        img = new Image(new FileInputStream(clientController.getDronePath()));
                    } else {
                        img = new Image(new FileInputStream(clientController.getWorkerPath()));
                    }
                    ImageView view = new ImageView(img);
                    view.setX(bee.getX_curr());
                    view.setY(bee.getY_curr());
                    view.setFitHeight(100);
                    view.setFitWidth(100);
                    view.setPreserveRatio(true);
                    bee.setView(view);
                    BeeData.getInstance().getList().add(bee);
                    clientController.showBeeOnViewPane(bee);
                }
            }
        }
        System.out.println("DTO handled successfully");
    }
}
