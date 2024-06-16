package model;

import java.io.Serializable;
import java.util.Map;

/**
 * This class represents the statistics of a player. It implements Serializable for object serialization,
 * PlayerStatsInterface for defining player statistics operations, and Comparable for comparing player statistics.
 */
public class PlayerStats implements PlayerStatsInterface, Serializable, Comparable<PlayerStats> {
    private static final long serialVersionUID = 1L;
    private String playerName;
    private int score;
    private int wordsGuessed;
    private int gamesPlayed;
    private Map<String, Integer> categoryScores;

    /**
     * Constructs a new PlayerStats instance with the specified details.
     *
     * @param playerName     the name of the player
     * @param score          the player's score
     * @param wordsGuessed   the number of words guessed by the player
     * @param gamesPlayed    the number of games played by the player
     * @param categoryScores the scores of the player in different categories
     */
    public PlayerStats(String playerName, int score, int wordsGuessed, int gamesPlayed,
                       Map<String, Integer> categoryScores) {
        this.playerName = playerName;
        this.score = score;
        this.wordsGuessed = wordsGuessed;
        this.gamesPlayed = gamesPlayed;
        this.categoryScores = categoryScores;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    @Override
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the player's score.
     *
     * @return the player's score
     */
    @Override
    public int getScore() {
        return score;
    }

    /**
     * Gets the number of words guessed by the player.
     *
     * @return the number of words guessed by the player
     */
    @Override
    public int getWordsGuessed() {
        return wordsGuessed;
    }

    /**
     * Gets the number of games played by the player.
     *
     * @return the number of games played by the player
     */
    @Override
    public int getGamesPlayed() {
        return gamesPlayed;
    }

    /**
     * Gets the scores of the player in different categories.
     *
     * @return the scores of the player in different categories
     */
    @Override
    public Map<String, Integer> getCategoryScores() {
        return categoryScores;
    }

    /**
     * Adds a score for a specific category.
     *
     * @param category the category name
     * @param score    the score to be added
     */
    @Override
    public void addCategoryScore(String category, int score) {
        categoryScores.put(category, score);
    }

    /**
     * Updates the player's statistics with new values.
     *
     * @param newScore         the new score
     * @param newWordsGuessed  the new number of words guessed
     * @param newGamesPlayed   the new number of games played
     * @param newCategoryScores the new category scores
     */
    public void updateStats(int newScore, int newWordsGuessed, int newGamesPlayed, Map<String, Integer> newCategoryScores) {
        this.score = newScore;
        this.wordsGuessed = newWordsGuessed;
        this.gamesPlayed = newGamesPlayed;
        this.categoryScores = newCategoryScores;
    }

    /**
     * Compares this player stats to another player stats based on the score.
     *
     * @param other the other PlayerStats to compare with
     * @return a negative integer, zero, or a positive integer as this score is less than, equal to,
     * or greater than the other score
     */
    @Override
    public int compareTo(PlayerStats other) {
        return Integer.compare(other.score, this.score);
    }
}
