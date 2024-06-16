package model;

import java.io.*;
import java.util.*;

/**
 * This class manages the high scores and player statistics. It handles loading, saving,
 * and updating player statistics.
 */
public class HighscoreManager {

    private static final String STATS_FILE = "data/player_stats.ser";
    private List<PlayerStats> playerStatsList;

    /**
     * Constructs a new HighscoreManager and loads player statistics from the file.
     */
    public HighscoreManager() {
        playerStatsList = new ArrayList<>();
        loadPlayerStats();
    }

    /**
     * Loads player statistics from the serialized file.
     * If the file does not exist, it initializes an empty player stats list.
     */
    private void loadPlayerStats() {
        File file = new File(STATS_FILE);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(STATS_FILE))) {
                playerStatsList = (List<PlayerStats>) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Serialized file not found. Initializing empty player stats list.");
            playerStatsList = new ArrayList<>();
        }
    }

    /**
     * Saves or updates the player statistics. If the player already exists in the list,
     * it updates their statistics. Otherwise, it adds a new player to the list.
     *
     * @param playerName     the name of the player
     * @param score          the player's score
     * @param wordsGuessed   the number of words guessed by the player
     * @param gamesPlayed    the number of games played by the player
     * @param categoryScores the scores of the player in different categories
     * @param currentCategory the current category being played
     */
    public void savePlayerStats(String playerName, int score, int wordsGuessed, int gamesPlayed,
                                Map<String, Integer> categoryScores, String currentCategory) {
        boolean found = false;
        for (PlayerStats stats : playerStatsList) {
            if (stats.getPlayerName().equals(playerName)) {
                stats.updateStats(score, wordsGuessed, gamesPlayed, categoryScores);
                found = true;
                break;
            }
        }
        if (!found) {
            playerStatsList.add(new PlayerStats(playerName, score, wordsGuessed, gamesPlayed, categoryScores));
        }
        categoryScores.putIfAbsent(currentCategory, 0);
        savePlayerStatsToFile();
    }

    /**
     * Saves the player statistics to the serialized file.
     */
    private void savePlayerStatsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            oos.writeObject(playerStatsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the list of player statistics.
     *
     * @return the list of player statistics
     */
    public List<PlayerStats> getPlayerStatsList() {
        return playerStatsList;
    }
}
