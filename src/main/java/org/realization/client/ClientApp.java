package org.realization.client;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class ClientApp {

    private static final String SERVER_HOST = "localhost";
    private static final int SERVER_PORT = 8080;

    public static void main(String[] args) {
        System.out.println("[CLIENT] Підключення до сервера...");

        try (
                Socket socket = new Socket(SERVER_HOST, SERVER_PORT);
                BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                Scanner scanner = new Scanner(System.in)
        ) {

            System.out.println(reader.readLine());

            String input;
            while (true) {
                System.out.print("Введіть команду (exit для виходу): ");
                input = scanner.nextLine();

                writer.write(input + "\n");
                writer.flush();

                String response = reader.readLine();
                System.out.println(response);

                if ("exit".equalsIgnoreCase(input.trim())) {
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("[CLIENT] Помилка з'єднання: " + e.getMessage());
        }
    }
}
