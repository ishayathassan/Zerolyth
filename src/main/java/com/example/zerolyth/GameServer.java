package com.example.zerolyth;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private final int port;
    private ServerSocket serverSocket;
    private final List<ClientHandler> clients = new ArrayList<>();

    private ClientHandler protagonistClient = null;
    private ClientHandler antagonistClient = null;

    public GameServer(int port) {
        this.port = port;
    }

    public void start() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("GameServer started on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket);

                ClientHandler clientHandler = new ClientHandler(clientSocket, this);
                clients.add(clientHandler);

                Thread thread = new Thread(clientHandler);
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void playerRoleSelected(ClientHandler client, String role) {
        System.out.println("Player selected role: " + role);

        if ("PROTAGONIST".equalsIgnoreCase(role)) {
            if (protagonistClient == null) {
                protagonistClient = client;
                client.sendMessage("ROLE_ACCEPTED:PROTAGONIST");
            } else {
                client.sendMessage("ROLE_DENIED:PROTAGONIST already taken");
            }
        } else if ("ANTAGONIST".equalsIgnoreCase(role)) {
            if (antagonistClient == null) {
                antagonistClient = client;
                client.sendMessage("ROLE_ACCEPTED:ANTAGONIST");
            } else {
                client.sendMessage("ROLE_DENIED:ANTAGONIST already taken");
            }
        } else {
            client.sendMessage("ROLE_DENIED:Unknown role");
            return;
        }

        // Check if both players connected with roles assigned
        if (protagonistClient != null && antagonistClient != null) {
            System.out.println("Both players ready. Starting game...");

            // Notify both clients to start the game
            protagonistClient.sendMessage("START_GAME");
            antagonistClient.sendMessage("START_GAME");
        }
    }

    public synchronized void broadcastProgress(ClientHandler sender, String progressMessage) {
        System.out.println("Broadcasting progress: " + progressMessage);
        for (ClientHandler client : clients) {
            if (client != sender) {
                client.sendMessage(progressMessage);
            }
        }
    }

    public synchronized void removeClient(ClientHandler client) {
        System.out.println("Removing client: " + client.getPlayerRole());
        clients.remove(client);

        if (client == protagonistClient) {
            protagonistClient = null;
            notifyOtherClient("PROTAGONIST_DISCONNECTED");
        }
        if (client == antagonistClient) {
            antagonistClient = null;
            notifyOtherClient("ANTAGONIST_DISCONNECTED");
        }
    }

    private void notifyOtherClient(String message) {
        for (ClientHandler client : clients) {
            client.sendMessage(message);
        }
    }

    public static void main(String[] args) {
        GameServer server = new GameServer(55555);
        server.start();
    }
}
