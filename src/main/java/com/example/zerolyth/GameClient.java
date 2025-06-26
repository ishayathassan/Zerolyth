package com.example.zerolyth;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class GameClient {

    private final String host;
    private final int port;
    private final GameMenuController controller;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    public GameClient(String host, int port, GameMenuController controller) {
        this.host = host;
        this.port = port;
        this.controller = controller;
    }

    public void connect() throws IOException {
        socket = new Socket(host, port);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    public void sendRole(String role) {
        sendMessage("ROLE:" + role);
    }

    public void sendMessage(String message) {
        if (out != null) {
            out.println(message);
            out.flush();
        } else {
            System.err.println("Output stream is null. Message not sent.");
        }
    }

    public void listenForMessages() {
        Thread listenThread = new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    controller.onServerMessage(message);
                }
            } catch (IOException e) {
                System.err.println("Error while listening to server: " + e.getMessage());
            } finally {
                closeConnection();
            }
        });
        listenThread.setDaemon(true);
        listenThread.start();
    }

    public void closeConnection() {
        try {
            if (out != null) out.close();
            if (in != null) in.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error while closing client connection: " + e.getMessage());
        }
    }
}
