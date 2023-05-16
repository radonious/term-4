package com.example.lab1;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ServerThread extends Thread {
    public Socket socket;
    private ObjectInputStream in_obj;
    private ObjectOutputStream out_obj;
    int id;
    String user_name;

    // Инициализация серверного потока сервером
    ServerThread(Socket socket, int id) throws IOException {
        this.setDaemon(true);
        this.socket = socket;
        try {
            out_obj = new ObjectOutputStream(socket.getOutputStream());
            in_obj = new ObjectInputStream(socket.getInputStream());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        this.id = id;
        this.user_name = "User #" + id;
    }

    // Запуск после успешной инициализации
    public void run() {
        try {
            System.out.println("Thread started");
            while (!socket.isClosed()) {
                System.out.println("Client #" + id + " thread listening");
                ClientDTO clientDTO = (ClientDTO) in_obj.readObject();
                System.out.println("Received DTO from Client #" + id);
                if (clientDTO.getDtoType().equals(ClientDTO.dtoType.CLIENT_REQUEST) &&
                        clientDTO.getDtoObject().equals(ClientDTO.dtoObject.OBJECTS)) {
                    ServerThread th = Server.getObjectOut(clientDTO.getToName());
                    clientDTO.setDtoType(ClientDTO.dtoType.SERVER_REPLY);
                    th.out_obj.writeObject(clientDTO);
                    th.out_obj.flush();
                }
            }
        } catch (Exception e) {
            System.out.println("\nClient #" + id + " has been disconnected via: " + e);
        } finally {
            stopConnection();
            System.out.println("Client #" + id + " handle end. Thread closed");
        }

    }

    // Отключение после окончания отслеживания
    private void stopConnection() {
        try {
            Server.removeThread(this);
            Server.shareClients();
            in_obj.close();
            out_obj.close();
            socket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendDtoToClient(ClientDTO clientDTO) {
        try {
            out_obj.writeObject(clientDTO);
            out_obj.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}