package model;

import java.io.*;
import java.util.*;

public class HighscoreManager {

    private static final String STATS_FILE = "data/player_stats.ser";
    private List<PlayerStats> playerStatsList;

    public HighscoreManager() {
        playerStatsList = new ArrayList<>();
        loadPlayerStats();
    }

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

    private void savePlayerStatsToFile() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(STATS_FILE))) {
            oos.writeObject(playerStatsList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<PlayerStats> getPlayerStatsList() {
        return playerStatsList;
    }
}
