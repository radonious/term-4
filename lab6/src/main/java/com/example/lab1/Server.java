package com.example.lab1;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Optional;

public class Server {
    private static final int port = 8888;
    static ArrayList<ServerThread> threads;
    static ServerSocket server_socket;
    static ArrayList<String> clients;

    // Ручной запуск сервера
    public static void main(String[] args) throws Exception {
        server_socket = new ServerSocket(port);
        threads = new ArrayList<>();
        clients = new ArrayList<>();
        int user_id = 0;

        while (!server_socket.isClosed()) {
            Socket client_socket = server_socket.accept();
            ++user_id;
            System.out.println("\nA new client #" + user_id + " is connected to " + client_socket.getLocalSocketAddress());
            ServerThread thread = new ServerThread(client_socket, user_id);
            clients.add("User #" + user_id);
            threads.add(thread);
            thread.start();
            shareClients();
        }
    }

    // Рассылка нового списка, при обновлении списка клиентов
    public static synchronized void shareClients() throws IOException {
        System.out.println("Sharing updated clients list..");
        ArrayList<String> names = new ArrayList<>(clients);
        for (ServerThread th : threads) {
            String name = new String(th.user_name);
            ClientDTO DTO = ClientDTO.builder()
                    .dtoType(ClientDTO.dtoType.SERVER_REPLY)
                    .dtoObject(ClientDTO.dtoObject.CLIENT_LIST)
                    .toName(name)
                    .clients(names)
                    .build();
            th.sendDtoToClient(DTO);
        }
        System.out.println("New clients list was sent for all clients");
    }

    // Удаление потока при его закрытии
    public static synchronized void removeThread(ServerThread thread) {
        threads.remove(thread);
        clients.remove(thread.user_name);
    }

    public static synchronized ServerThread getObjectOut(String name) {
        return threads.stream().filter(e -> e.user_name.equals(name)).findFirst().get();
    }
}
