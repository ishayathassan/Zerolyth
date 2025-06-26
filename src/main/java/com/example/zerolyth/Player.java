package com.example.zerolyth;

public class Player {
    private String name;
    private PlayerType type;
    private int health;
    private int score;
    private boolean completedHanoiPuzzle;
    private boolean completedSimonSaysPuzzle;
    private boolean completedSlidingTilePuzzle;
    private boolean completedCipherPuzzle;
    private int collectiblesCollected;

    public int getCollectiblesCollected() {
        return collectiblesCollected;
    }

    public void addCollectible() {
        collectiblesCollected++;
    }

    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
        this.health = 100;
        this.score = 0;
        this.completedCipherPuzzle = false;
        this.completedHanoiPuzzle = false;
        this.completedSimonSaysPuzzle = false;
        this.completedSlidingTilePuzzle = false;
    }

    // Changed
    public int getProgressPercentage() {
        int completed = 0;
        if (completedHanoiPuzzle) completed++;
        if (completedSimonSaysPuzzle) completed++;
        if (completedSlidingTilePuzzle) completed++;
        if (completedCipherPuzzle) completed++;
        return completed * 25; // 25% per puzzle
    }

    public boolean isCompletedHanoiPuzzle() {
        return completedHanoiPuzzle;
    }

    public boolean isCompletedSimonSaysPuzzle() {
        return completedSimonSaysPuzzle;
    }

    public boolean isCompletedSlidingTilePuzzle() {
        return completedSlidingTilePuzzle;
    }

    public boolean isCompletedCipherPuzzle() {
        return completedCipherPuzzle;
    }

    public void setCompletedHanoiPuzzle(boolean completedHanoiPuzzle) {
        this.completedHanoiPuzzle = completedHanoiPuzzle;
    }

    public void setCompletedSimonSaysPuzzle(boolean completedSimonSaysPuzzle) {
        this.completedSimonSaysPuzzle = completedSimonSaysPuzzle;
    }

    public void setCompletedSlidingTilePuzzle(boolean completedSlidingTilePuzzle) {
        this.completedSlidingTilePuzzle = completedSlidingTilePuzzle;
    }

    public void setCompletedCipherPuzzle(boolean completedCipherPuzzle) {
        this.completedCipherPuzzle = completedCipherPuzzle;
    }

    public String getName() {
        return name;
    }

    public PlayerType getType() {
        return type;
    }

    public int getHealth() {
        return health;
    }

    public void reduceHealth(int amount) {
        this.health = Math.max(0, this.health - amount);
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

}
