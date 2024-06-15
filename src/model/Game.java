package model;

import controller.*;
import view.GameMenuBar;
import view.HighscorePage;
import view.IndexPage;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.List;


//glavna klasa koja predstavlja igru, implementira sučelja za rukovanje dogadajima pritiska na gumbe
public class Game implements GuessButtonActionListener, NewGameButtonActionListener {

    private JFrame frame;
    private JTextField letterInput;
    private JLabel wordDisplay, playerNameLabel, scoreLabel;
    private JTextArea missedLetters;
    private JPanel gallowsPanel;

    private String wordToGuess;
    private List<Character> triedLettersList = new ArrayList<>();
    private int incorrectGuesses = 0;
    private String playerName;
    private int score = 0;
    private int wordsGuessed = 0;
    private int gamesPlayed = 0;
    private Map<String, List<String>> categoriesMap = new HashMap<>();
    private Map<String, Integer> categoryScores = new HashMap<>();
    public static final String STATS_FILE = "data/player_stats.txt";
    private String category;

    public Game(String playerName) {
        this.playerName = playerName;
        loadWordsFromFile();
        initializeGUI();
        startNewGame();
    }

    private void initializeGUI() { //metoda koja postavlja izgled guija
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

        playerNameLabel = new JLabel(playerName.toUpperCase());
        Font font = new Font("serif", Font.BOLD, 20);
        scoreLabel = new JLabel("Score: " + score);

        wordDisplay = new JLabel("");

        JPanel rightPanel = new JPanel();
        JPanel innerRightPanel = new JPanel();
        innerRightPanel.setLayout(new BoxLayout(innerRightPanel, BoxLayout.Y_AXIS));
        innerRightPanel.setBorder(BorderFactory.createEmptyBorder(70, 150, 50, 100));
        innerRightPanel.add(rightPanel);
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        playerNameLabel.setFont(font);
        scoreLabel.setFont(font);
        wordDisplay.setFont(font);
        rightPanel.add(playerNameLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(scoreLabel);
        rightPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        rightPanel.add(wordDisplay);

        innerRightPanel.add(rightPanel, BorderLayout.CENTER);
        frame.add(innerRightPanel, BorderLayout.CENTER);

        JPanel inputPanel = new JPanel(new FlowLayout());
        letterInput = new JTextField(10);
        letterInput.setFont(new Font("serif", Font.PLAIN, 15));
        JButton submitButton = new JButton("Guess");

        GuessButtonAction guessButtonAction = new GuessButtonAction();
        guessButtonAction.setGuessButtonActionListener(this);
        submitButton.addActionListener(guessButtonAction);

        JLabel inputLabel = new JLabel("Guess a letter or the word:");
        inputLabel.setFont(new Font("serif", Font.BOLD, 15));
        inputPanel.add(inputLabel);
        inputPanel.add(letterInput);
        inputPanel.add(submitButton);

        frame.add(inputPanel, BorderLayout.SOUTH);

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

    public void startNewGame() { //za zapocinjanje nove igre
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
        } else {
            wordToGuess = "EXAMPLE";
        }
        wordDisplay.setText(generateWordDisplay());
        missedLetters.setText("Missed Letters: \n");
        updateGallows();
        letterInput.setText("");
        gamesPlayed++;
    }

    public void handleGuess() { //vodi tekstualnii unos korisnika
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
            endGame();
        }
    }

    private void continueOrEndGame(boolean guessedWord) { //provjerava je li igra gotova i postavlja upit za nastavak
        if (guessedWord) {
            JOptionPane.showMessageDialog(frame, "Congratulations! You guessed the word!");
            int choice = JOptionPane.showConfirmDialog(frame, "Continue?", "Game over", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                startNewGame();
                return;
            }
        }
        endGame();
    }

    private String generateWordDisplay() { //generira crtice za rijec
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

    private void drawGallows(Graphics g) { //crta vjesala
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
    } //ponovo crta vjesala

    private void loadWordsFromFile() { //ucitava rijeci iz filea
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

    private void endGame() { //zavrsava igru i otvara highscore page
        saveScore();
        frame.dispose();
        new HighscorePage(this, false).setVisible(true);
    }

    private void updateScoreLabel() {
        scoreLabel.setText("Score: " + score);
    } //azurira prikaz bodova

    public void saveScore() { //sprema bodove u datoteku
        HighscoreManager highscoreManager = new HighscoreManager();
        highscoreManager.savePlayerStats(playerName, score, wordsGuessed, gamesPlayed, categoryScores);
    }


    //prikazuje stats svih igraca
    public void showAllPlayersStats() {
        new HighscorePage(this, false).setVisible(true);
    }


    //prikazuje stats trenutnog igraca
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