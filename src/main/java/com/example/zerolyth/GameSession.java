package com.example.zerolyth;

class GameSession {
    private Player player;
    private Level currentLevel;

    public GameSession(Player player, Level level) {
        this.player = player;
        this.currentLevel = level;

        // TODO: Initialize puzzles for this level
        // this.currentLevel.setPuzzles(PuzzleLoader.loadForLevel(level));

        // TODO: Initialize cutscenes for this level
        // CutsceneManager.loadIntroCutscene(level);
    }

    public Player getPlayer() {
        return player;
    }

    public Level getCurrentLevel() {
        return currentLevel;
    }
}