package model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

// Class that stores player statistics
public class PlayerStats implements PlayerStatsInterface, Serializable, Comparable<PlayerStats> {
    private static final long serialVersionUID = 1L;

    private String playerName;
    private int score;
    private int wordsGuessed;
    private int gamesPlayed;
    private Map<String, Integer> categoryScores;

    public PlayerStats(String playerName, int score, int wordsGuessed, int gamesPlayed,
                       Map<String, Integer> categoryScores) {
        this.playerName = playerName;
        this.score = score;
        this.wordsGuessed = wordsGuessed;
        this.gamesPlayed = gamesPlayed;
        this.categoryScores = categoryScores;
    }

    @Override
    public String getPlayerName() {
        return playerName;
    }

    @Override
    public int getScore() {
        return score;
    }

    @Override
    public int getWordsGuessed() {
        return wordsGuessed;
    }

    @Override
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    @Override
    public Map<String, Integer> getCategoryScores() {
        return categoryScores;
    }

    @Override
    public void addCategoryScore(String category, int score) {
        categoryScores.put(category, score);
    }

    // New method to update player statistics
    public void updateStats(int newScore, int newWordsGuessed, int newGamesPlayed, Map<String, Integer> newCategoryScores) {
        this.score = newScore;
        this.wordsGuessed = newWordsGuessed;
        this.gamesPlayed = newGamesPlayed;
        this.categoryScores = newCategoryScores;
    }

    @Override
    public int compareTo(PlayerStats other) {
        return Integer.compare(other.score, this.score);
    }
}
