package com.example.zerolyth;

public class GameSession {
    private Player player;
    private Level currentLevel;
    private GameClient gameClient;

    public GameSession(Player player, Level level, GameClient gameClient) {
        this.player = player;
        this.currentLevel = level;
        this.gameClient = gameClient;

        // Future support for initializing puzzles/cutscenes can be added here
        // this.currentLevel.setPuzzles(PuzzleLoader.loadForLevel(level));
        // CutsceneManager.loadIntroCutscene(level);
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
