package com.example.zerolyth;

public class Player {
    private String name;
    private PlayerType type;
    private int health;
    private int score;

    public Player(String name, PlayerType type) {
        this.name = name;
        this.type = type;
        this.health = 100;
        this.score = 0;
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
