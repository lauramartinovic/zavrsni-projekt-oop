package model;

import java.util.Map;

/**
 * This interface defines the operations for accessing and modifying player statistics.
 */
public interface PlayerStatsInterface {

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    String getPlayerName();

    /**
     * Gets the player's score.
     *
     * @return the player's score
     */
    int getScore();

    /**
     * Gets the number of words guessed by the player.
     *
     * @return the number of words guessed by the player
     */
    int getWordsGuessed();

    /**
     * Gets the number of games played by the player.
     *
     * @return the number of games played by the player
     */
    int getGamesPlayed();

    /**
     * Gets the scores of the player in different categories.
     *
     * @return a map containing the scores of the player in different categories
     */
    Map<String, Integer> getCategoryScores();

    /**
     * Adds a score for a specific category.
     *
     * @param category the category name
     * @param score    the score to be added
     */
    void addCategoryScore(String category, int score);
}
