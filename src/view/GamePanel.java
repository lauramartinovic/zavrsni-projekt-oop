package view;

import model.Game;
import controller.GuessButtonAction;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GamePanel implements GamePanelInterface {
    private JFrame frame;
    private JTextField letterInput;
    private JLabel wordDisplay, playerNameLabel, scoreLabel;
    private JTextArea missedLetters;
    private JPanel gallowsPanel;
    private Font latoFont;
    private Game game;

    public GamePanel(Game game) {
        this.game = game;
        initializeGUI();
    }

    private void initializeGUI() {
        frame = new JFrame("Hangman Game");
        frame.setSize(800, 480);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setLocationRelativeTo(null);

        frame.setJMenuBar(new GameMenuBar(game));

        gallowsPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                game.drawGallows(g);
            }
        };
        gallowsPanel.setPreferredSize(new Dimension(200, 200));

        missedLetters = new JTextArea(3, 10);
        missedLetters.setEditable(false);
        missedLetters.setText("Missed Letters: ");

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
            latoFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Lato-Regular.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(latoFont);

            playerNameLabel = new JLabel(game.getPlayerName().toUpperCase());
            playerNameLabel.setFont(latoFont);
            scoreLabel = new JLabel("Score: " + game.getScore());
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
            guessButtonAction.setGuessButtonActionListener(game);
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
    public void updateGallows() {
        gallowsPanel.repaint();
    }

    @Override
    public void updateScoreLabel() {
        scoreLabel.setText("Score: " + game.getScore());
    }

    @Override
    public void updateWordDisplay(String text) {
        wordDisplay.setText(text);
    }

    @Override
    public void updateMissedLetters(String text) {
        missedLetters.setText("Missed Letters: " + text);
    }

    @Override
    public void clearLetterInput() {
        letterInput.setText("");
    }

    @Override
    public JFrame getFrame() {
        return frame;
    }

    @Override
    public String getLetterInput() {
        return letterInput.getText();
    }

    @Override
    public String getWordDisplay() {
        return wordDisplay.getText();
    }
}
