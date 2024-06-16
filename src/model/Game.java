package model;

import controller.*;
import view.GameMenuBar;
import view.HighscorePage;
import view.IndexPage;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;

public class Game implements GuessButtonActionListener, NewGameButtonActionListener, Serializable {
    private static final long serialVersionUID = 1L; // Added serialVersionUID

    private transient JFrame frame;
    private transient JTextField letterInput;
    private transient JLabel wordDisplay, playerNameLabel, scoreLabel;
    private transient JTextArea missedLetters;
    private transient JPanel gallowsPanel;

    private String wordToGuess;
    private List<Character> triedLettersList = new ArrayList<>();
    private int incorrectGuesses = 0;
    private String playerName;
    private int score = 0;
    private int wordsGuessed = 0;
    private int gamesPlayed = 0;
    private Map<String, List<String>> categoriesMap = new HashMap<>();
    private Map<String, Integer> categoryScores = new HashMap<>();
    public static final String STATS_FILE = "data/player_stats.ser";
    private String category;

    public Game(String playerName) {
        this.playerName = playerName;
        loadWordsFromFile();
        loadGameState();
        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() {
        frame = new JFrame("Hangman Game");
        frame.setSize(800, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        frame.setJMenuBar(new GameMenuBar(this));

        gallowsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawGallows(g);
            }
        };
        gallowsPanel.setPreferredSize(new Dimension(200, 200));

        missedLetters = new JTextArea(3, 10);
        missedLetters.setEditable(false);
        missedLetters.setText("Missed Letters: \n");

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        JPanel marginPanel = new JPanel();
        marginPanel.setLayout(new BorderLayout());
        marginPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        frame.setContentPane(marginPanel);
        leftPanel.add(gallowsPanel);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        leftPanel.add(missedLetters);
        leftPanel.add(Box.createRigidArea(new Dimension(0, 30)));

        frame.add(leftPanel, BorderLayout.WEST);

        try {
            Font latoFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Lato-Regular.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(latoFont);

            playerNameLabel = new JLabel(playerName.toUpperCase());
            playerNameLabel.setFont(latoFont);
            scoreLabel = new JLabel("Score: " + score);
            scoreLabel.setFont(latoFont);
            wordDisplay = new JLabel("");
            wordDisplay.setFont(latoFont);

            JPanel rightPanel = new JPanel();
            JPanel innerRightPanel = new JPanel();
            innerRightPanel.setLayout(new BoxLayout(innerRightPanel, BoxLayout.Y_AXIS));
            innerRightPanel.setBorder(BorderFactory.createEmptyBorder(70, 150, 50, 100));
            innerRightPanel.add(rightPanel);
            rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
            rightPanel.add(playerNameLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(scoreLabel);
            rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
            rightPanel.add(wordDisplay);

            innerRightPanel.add(rightPanel, BorderLayout.CENTER);
            frame.add(innerRightPanel, BorderLayout.CENTER);

            JPanel inputPanel = new JPanel(new FlowLayout());
            letterInput = new JTextField(10);
            letterInput.setFont(latoFont.deriveFont(Font.PLAIN, 15));
            JButton submitButton = new JButton("Guess");
            submitButton.setFont(latoFont.deriveFont(Font.PLAIN, 15));

            GuessButtonAction guessButtonAction = new GuessButtonAction();
            guessButtonAction.setGuessButtonActionListener(this);
            submitButton.addActionListener(guessButtonAction);

            JLabel inputLabel = new JLabel("Guess a letter or the word:");
            inputLabel.setFont(latoFont.deriveFont(Font.BOLD, 15));
            inputPanel.add(inputLabel);
            inputPanel.add(letterInput);
            inputPanel.add(submitButton);

            frame.add(inputPanel, BorderLayout.SOUTH);

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        frame.setVisible(true);
    }

    @Override
    public void guessButtonActionPerformed(GuessButtonActionEvent event) {
        handleGuess();
    }

    @Override
    public void newGameButtonActionPerformed(NewGameButtonActionEvent event) {
        startNewGame();
    }

    public void startNewGame() {
        triedLettersList.clear();
        incorrectGuesses = 0;
        if (IndexPage.categoryComboBox.getSelectedItem() != null) {
            category = (String) IndexPage.categoryComboBox.getSelectedItem();
        } else {
            category = "DEFAULT";
        }
        List<String> words = categoriesMap.get(category);
        if (words != null && !words.isEmpty()) {
            int randomIndex = new Random().nextInt(words.size());
            wordToGuess = words.get(randomIndex).toUpperCase();
            System.out.println("Selected word for category " + category + ": " + wordToGuess);
        } else {
            wordToGuess = "EXAMPLE";
        }
        wordDisplay.setText(generateWordDisplay());
        missedLetters.setText("Missed Letters: \n");
        updateGallows();
        letterInput.setText("");
        gamesPlayed++;
    }

    public void handleGuess() {
        String input = letterInput.getText().toUpperCase();
        if (input.length() == 1) {
            char letter = input.charAt(0);
            if (triedLettersList.contains(letter) || !Character.isLetter(letter)) {
                JOptionPane.showMessageDialog(frame, "Enter a valid, new letter!");
                letterInput.setText("");
                return;
            }
            triedLettersList.add(letter);
            if (wordToGuess.contains(String.valueOf(letter))) {
                wordDisplay.setText(generateWordDisplay());
                if (wordDisplay.getText().replace(" ", "").equals(wordToGuess)) {
                    score += 10;
                    wordsGuessed++;
                    categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 10);
                    updateScoreLabel();
                    continueOrEndGame(true);
                    return;
                }
            } else {
                incorrectGuesses++;
                missedLetters.append(letter + ", ");
                updateGallows();
            }
        } else {
            if (input.equals(wordToGuess)) {
                wordDisplay.setText(wordToGuess);
                score += 10;
                wordsGuessed++;
                categoryScores.put(category, categoryScores.getOrDefault(category, 0) + 10);
                updateScoreLabel();
                continueOrEndGame(true);
                return;
            } else {
                incorrectGuesses++;
                JOptionPane.showMessageDialog(frame, "Incorrect guess!");
                updateGallows();
            }
        }
        letterInput.setText("");
        updateScoreLabel();
        if (incorrectGuesses >= 6) {
            JOptionPane.showMessageDialog(frame, "Game over, you lost! The word was: " + wordToGuess);
            saveScore(); // Ensure the score is saved even if the player loses
            endGame();
        }
    }

    private void continueOrEndGame(boolean guessedWord) {
        if (guessedWord) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the word!");
            int choice = JOptionPane.showConfirmDialog(frame, "Continue?", "Game over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
                return;
            }
        }
        saveScore(); // Ensure the score is saved if the player chooses not to continue
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
        System.out.println("Generated word display: " + display.toString());
        return display.toString();
    }

    private void drawGallows(Graphics g) {
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

    private void updateGallows() {
        gallowsPanel.repaint();
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
            System.out.println("Categories and words loaded: " + categoriesMap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void endGame() {
        saveGameState();
        saveScore();
        frame.dispose();
        new HighscorePage(this, false).setVisible(true);
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
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
        new HighscorePage(this, false).setVisible(true);
    }

    public void showPlayerStats() {
        new HighscorePage(this, true).setVisible(true);
    }

    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    public String getPlayerName() {
        return playerName;
    }
}
