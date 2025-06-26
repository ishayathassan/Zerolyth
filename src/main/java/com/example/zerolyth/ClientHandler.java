package com.example.zerolyth;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private GameServer server;
    private BufferedReader in;
    private PrintWriter out;
    private String playerRole; // PROTAGONIST or ANTAGONIST

    public ClientHandler(Socket socket, GameServer server) {
        this.socket = socket;
        this.server = server;
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getPlayerRole() {
        return playerRole;
    }

    public void sendMessage(String msg) {
        out.println(msg);
    }

    @Override
    public void run() {
        try {
            String message;
            while ((message = in.readLine()) != null) {
                System.out.println("Received from client: " + message);

                // Handle role selection
                if (message.startsWith("ROLE:")) {
                    playerRole = message.substring(5);
                    server.playerRoleSelected(this, playerRole);
                }
                else if (message.startsWith("PROGRESS:")) {
                    server.broadcastProgress(this, message);
                }
                else {
                    System.out.println("Unknown message from client: " + message);
                }
            }
        } catch (IOException e) {
            System.out.println("Client disconnected: " + playerRole);
        } finally {
            server.removeClient(this);
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

