package model;

import controller.*;
import view.GamePanel;
import view.GamePanelInterface;
import view.ViewPanel;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game implements GuessButtonActionListener, NewGameButtonActionListener, Serializable {
    private static final long serialVersionUID = 1L;

    private transient GamePanelInterface gameUI;
    private List<Character> triedLettersList = new ArrayList<>();
    private int incorrectGuesses = 0;
    private String playerName;
    private int score = 0;
    private int wordsGuessed = 0;
    private int gamesPlayed = 0;
    private Map<String, List<String>> categoriesMap = new HashMap<>();
    private Map<String, Integer> categoryScores = new HashMap<>();
    private String category;
    private String wordToGuess;

    public Game(String playerName, String category) {
        this.playerName = playerName;
        this.category = category;
        loadWordsFromFile();
        loadGameState();
        gameUI = new GamePanel(this);
        startNewGame();
    }

    public void startNewGame() {
        triedLettersList.clear();
        incorrectGuesses = 0;
        List<String> words = categoriesMap.get(category);
        if (words != null && !words.isEmpty()) {
            int randomIndex = new Random().nextInt(words.size());
            wordToGuess = words.get(randomIndex).toUpperCase();
        } else {
            wordToGuess = "EXAMPLE";
        }
        gameUI.updateWordDisplay(generateWordDisplay());
        updateMissedLetters();
        gameUI.updateGallows();
        gamesPlayed++;
        gameUI.clearLetterInput();
    }

    public void handleGuess() {
        String input = gameUI.getLetterInput().toUpperCase();
        if (input.length() == 1) {
            char letter = input.charAt(0);
            if (triedLettersList.contains(letter) || !Character.isLetter(letter)) {
                JOptionPane.showMessageDialog(gameUI.getFrame(), "Enter a valid, new letter!");
                gameUI.clearLetterInput();
                return;
            }
            triedLettersList.add(letter);
            if (wordToGuess.contains(String.valueOf(letter))) {
                gameUI.updateWordDisplay(generateWordDisplay());
                if (gameUI.getWordDisplay().replace(" ", "").equals(wordToGuess)) {
                    score += 10;
                    wordsGuessed++;
                    categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 10);
                    updateScoreLabel();
                    gameUI.clearLetterInput();
                    continueOrEndGame(true);
                    return;
                }
            } else {
                incorrectGuesses++;
                updateMissedLetters();
                gameUI.updateGallows();
            }
        } else {
            if (input.equals(wordToGuess)) {
                gameUI.updateWordDisplay(wordToGuess);
                score += 10;
                wordsGuessed++;
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 10);
                updateScoreLabel();
                continueOrEndGame(true);
                return;
            } else {
                incorrectGuesses++;
                JOptionPane.showMessageDialog(gameUI.getFrame(), "Incorrect guess!");
                gameUI.updateGallows();
            }
        }
        gameUI.clearLetterInput();
        updateScoreLabel();
        if (incorrectGuesses >= 6) {
            JOptionPane.showMessageDialog(gameUI.getFrame(), "Game over, you lost! The word was: " + wordToGuess);
            saveScore();
            endGame();
        }
    }

    private void updateMissedLetters() {
        StringBuilder missedLetters = new StringBuilder();
        for (char letter : triedLettersList) {
            if (!wordToGuess.contains(String.valueOf(letter))) {
                missedLetters.append(letter).append(", ");
            }
        }
        if (missedLetters.length() > 0) {
            missedLetters.setLength(missedLetters.length() - 2); // Uklanjanje posljednjeg zareza i razmaka
        }
        gameUI.updateMissedLetters(missedLetters.toString());
    }

    private void continueOrEndGame(boolean guessedWord) {
        if (guessedWord) {
            JOptionPane.showMessageDialog(gameUI.getFrame(), "Congratulations! You guessed the word!");
            int choice = JOptionPane.showConfirmDialog(gameUI.getFrame(), "Continue?", "Game over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
                return;
            }
        }
        saveScore();
        endGame();
    }

    private String generateWordDisplay() {
        if (wordToGuess == null) {
            return "";
        }
        StringBuilder display = new StringBuilder();
        for (char c : wordToGuess.toCharArray()) {
            if (c == ' ') {
                display.append("    ");
            } else if (triedLettersList.contains(c)) {
                display.append(c).append(" ");
            } else {
                display.append("_ ");
            }
        }
        return display.toString();
    }

    public void drawGallows(Graphics g) {
        g.setColor(Color.BLACK);
        g.drawLine(0, 0, 0, 200);
        g.drawLine(0, 0, 100, 0);
        g.drawLine(100, 0, 100, 20);

        if (incorrectGuesses >= 1) {
            g.drawOval(80, 20, 40, 40);
        }
        if (incorrectGuesses >= 2) {
            g.drawLine(100, 60, 100, 120);
        }
        if (incorrectGuesses >= 3) {
            g.drawLine(100, 60, 70, 100);
        }
        if (incorrectGuesses >= 4) {
            g.drawLine(100, 60, 130, 100);
        }
        if (incorrectGuesses >= 5) {
            g.drawLine(100, 120, 70, 170);
        }
        if (incorrectGuesses >= 6) {
            g.drawLine(100, 120, 130, 170);
        }
    }

    private void updateScoreLabel() {
        gameUI.updateScoreLabel();
    }

    private void loadWordsFromFile() {
        String currentCategory = "";
        try (BufferedReader br = new BufferedReader(new FileReader("data/words"))) {
            String line;
            List<String> words = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                if (line.startsWith("[")) {
                    if (!words.isEmpty()) {
                        categoriesMap.put(currentCategory, new ArrayList<>(words));
                    }
                    currentCategory = line.substring(1, line.length() - 1);
                    words.clear();
                } else {
                    words.add(line);
                }
            }
            if (!words.isEmpty()) {
                categoriesMap.put(currentCategory, new ArrayList<>(words));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endGame() {
        saveGameState();
        saveScore();
        gameUI.getFrame().dispose();
        new ViewPanel(this, false).setVisible(true);
    }

    public void saveScore() {
        HighscoreManager highscoreManager = new HighscoreManager();
        highscoreManager.savePlayerStats(playerName, score, wordsGuessed, gamesPlayed, categoryScores, category);
    }

    private void saveGameState() {
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getSaveFileName()))) {
            oos.writeObject(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadGameState() {
        String saveFileName = getSaveFileName();
        File file = new File(saveFileName);
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(saveFileName))) {
                Game loadedGame = (Game) ois.readObject();
                this.triedLettersList = loadedGame.triedLettersList;
                this.incorrectGuesses = loadedGame.incorrectGuesses;
                this.wordToGuess = loadedGame.wordToGuess;
                this.score = loadedGame.score;
                this.wordsGuessed = loadedGame.wordsGuessed;
                this.gamesPlayed = loadedGame.gamesPlayed;
                this.categoriesMap = loadedGame.categoriesMap;
                this.categoryScores = loadedGame.categoryScores;
                this.category = loadedGame.category;
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private String getSaveFileName() {
        return "data/" + playerName + "_game.ser";
    }

    public void showAllPlayersStats() {
        new ViewPanel(this, false).setVisible(true);
    }

    public void showPlayerStats() {
        new ViewPanel(this, true).setVisible(true);
    }

    public void setVisible(boolean visible) {
        gameUI.getFrame().setVisible(visible);
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    @Override
    public void guessButtonActionPerformed(GuessButtonActionEvent event) {
        handleGuess();
    }

    @Override
    public void newGameButtonActionPerformed(NewGameButtonActionEvent event) {
        startNewGame();
    }
}
