package com.example.zerolyth;

public class GameSession {
    private Player player;
    private Level currentLevel;
    private GameClient gameClient;
    private Player winner;


    private boolean playerWon;


    public GameSession(Player player, Level level, GameClient gameClient) {
        this.player = player;
        this.currentLevel = level;
        this.gameClient = gameClient;

    }


    public void setPlayerWon(boolean won) {
        this.playerWon = won;
    }

    public boolean didPlayerWin() {
        return playerWon;
    }


    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }
    public Player getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }

    public GameClient getGameClient() {
        return gameClient;
    }

    /**
     * Sends a progress update to the server (called from LevelViewController or puzzle controllers).
     */
//    public void sendProgressUpdate(String progressMessage) {
//        if (gameClient != null) {
//            gameClient.sendMessage("PROGRESS:" + progressMessage);
//        }
//    }

    //Changed
    public void sendProgressUpdate(String progressMessage) {
        if (gameClient != null) {
            gameClient.sendMessage(progressMessage);
        }
    }

}
