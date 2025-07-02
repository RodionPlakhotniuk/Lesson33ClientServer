package org.realization.server;

import java.io.*;
import java.net.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

public class ServerApp {

    private static final int PORT = 8080;
    private static int clientCounter = 1;
    private static final Map<String, ClientInfo> activeClients = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        System.out.println("[SERVER] Сервер запущено... Очікування клієнтів...");

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                String clientName = "client-" + clientCounter++;
                ClientInfo clientInfo = new ClientInfo(clientName, LocalDateTime.now(), clientSocket);
                activeClients.put(clientName, clientInfo);

                System.out.printf("[SERVER] %s успішно підключився%n", clientName);


                new Thread(() -> handleClient(clientInfo)).start();
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Помилка сервера: " + e.getMessage());
        }
    }

    private static void handleClient(ClientInfo clientInfo) {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientInfo.getSocket().getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientInfo.getSocket().getOutputStream()))
        ) {
            writer.write("[SERVER] Ви підключені як " + clientInfo.getName() + "\n");
            writer.flush();

            String inputLine;
            while ((inputLine = reader.readLine()) != null) {
                if ("exit".equalsIgnoreCase(inputLine.trim())) {
                    writer.write("[SERVER] Ви відключаєтесь. До побачення!\n");
                    writer.flush();
                    break;
                } else {
                    writer.write("[SERVER] Невідома команда: " + inputLine + "\n");
                    writer.flush();
                }
            }
        } catch (IOException e) {
            System.err.println("[SERVER] Помилка з клієнтом " + clientInfo.getName());
        } finally {
            try {
                clientInfo.getSocket().close();
            } catch (IOException ignored) {
            }
            activeClients.remove(clientInfo.getName());
            System.out.printf("[SERVER] %s відключився%n", clientInfo.getName());
        }
    }


    static class ClientInfo {
        private final String name;
        private final LocalDateTime connectedAt;
        private final Socket socket;

        public ClientInfo(String name, LocalDateTime connectedAt, Socket socket) {
            this.name = name;
            this.connectedAt = connectedAt;
            this.socket = socket;
        }

        public String getName() {
            return name;
        }

        public LocalDateTime getConnectedAt() {
            return connectedAt;
        }

        public Socket getSocket() {
            return socket;
        }
    }
}
