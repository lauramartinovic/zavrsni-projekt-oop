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

/**
 * This class represents the game logic and user interface for the Hangman game.
 * It handles actions related to guessing letters and starting new games,
 * and it manages the game's state, including saving and loading the state.
 */
public class Game implements GuessButtonActionListener, NewGameButtonActionListener, Serializable {
    private static final long serialVersionUID = 1L;

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
    private String category;

    /**
     * Constructs a new Game instance for the specified player.
     *
     * @param playerName the name of the player
     */
    public Game(String playerName) {
        this.playerName = playerName;
        loadWordsFromFile();
        loadGameState();
        initializeGUI();
        startNewGame();
    }

    /**
     * Initializes the graphical user interface for the game.
     */
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

    /**
     * Handles the guess button action event.
     *
     * @param event the GuessButtonActionEvent containing information about the event
     */
    @Override
    public void guessButtonActionPerformed(GuessButtonActionEvent event) {
        handleGuess();
    }

    /**
     * Handles the new game button action event.
     *
     * @param event the NewGameButtonActionEvent containing information about the event
     */
    @Override
    public void newGameButtonActionPerformed(NewGameButtonActionEvent event) {
        startNewGame();
    }

    /**
     * Starts a new game by selecting a new word to guess and resetting the game state.
     */
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

    /**
     * Handles the logic of guessing a letter or word in the game.
     */
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

    /**
     * Continues or ends the game based on whether the player guessed the word.
     *
     * @param guessedWord true if the player guessed the word, false otherwise
     */
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

    /**
     * Generates the display string for the word to guess, showing correctly guessed letters and underscores for missing letters.
     *
     * @return the display string for the word to guess
     */
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

    /**
     * Draws the gallows and hangman figure based on the number of incorrect guesses.
     *
     * @param g the Graphics object used to draw the gallows and hangman figure
     */
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

    /**
     * Updates the gallows panel to reflect the current number of incorrect guesses.
     */
    private void updateGallows() {
        gallowsPanel.repaint();
    }

    /**
     * Loads words from a file and populates the categories map.
     */
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

    /**
     * Ends the game by saving the game state and score, and showing the highscore page.
     */
    private void endGame() {
        saveGameState();
        saveScore();
        frame.dispose();
        new HighscorePage(this, false).setVisible(true);
    }

    /**
     * Updates the score label to reflect the current score.
     */
    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    }

    /**
     * Saves the player's score and other statistics.
     */
    public void saveScore() {
        HighscoreManager highscoreManager = new HighscoreManager();
        highscoreManager.savePlayerStats(playerName, score, wordsGuessed, gamesPlayed, categoryScores, category);
    }

    /**
     * Saves the current game state to a file.
     */
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

    /**
     * Loads the game state from a file if it exists.
     */
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

    /**
     * Returns the filename used for saving the game state.
     *
     * @return the filename used for saving the game state
     */
    private String getSaveFileName() {
        return "data/" + playerName + "_game.ser";
    }

    /**
     * Shows the highscore page with all players' statistics.
     */
    public void showAllPlayersStats() {
        new HighscorePage(this, false).setVisible(true);
    }

    /**
     * Shows the highscore page with the current player's statistics.
     */
    public void showPlayerStats() {
        new HighscorePage(this, true).setVisible(true);
    }

    /**
     * Sets the visibility of the game window.
     *
     * @param visible true to make the window visible, false to hide it
     */
    public void setVisible(boolean visible) {
        frame.setVisible(visible);
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }
}
