package view;

import model.Game;
import model.HighscoreManager;
import model.PlayerStats;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class HighscorePage extends JFrame {
    private JTable scoreTable;

    public HighscorePage(Game game, boolean showCurrentPlayerOnly) {
        setTitle("Highscores");
        setSize(800, 480);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        scoreTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(scoreTable);
        add(scrollPane, BorderLayout.CENTER);

        Font latoFont;
        try {
            latoFont = Font.createFont(Font.TRUETYPE_FONT, new File("resources/Lato-Regular.ttf")).deriveFont(20f);
            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(latoFont);
        } catch (Exception e) {
            e.printStackTrace();
            latoFont = new Font("SansSerif", Font.PLAIN, 20); // Fallback font
        }

        if (showCurrentPlayerOnly) {
            loadPlayerStats(game.getPlayerName());
        } else {
            loadScores(null);
        }

        JPanel buttonPanel = new JPanel();
        JButton homeButton = new JButton("Home Screen");
        JButton backButton = new JButton("Back to the Game");
        JButton exitButton = new JButton("Exit");

        homeButton.setFont(latoFont.deriveFont(Font.PLAIN, 15));
        backButton.setFont(latoFont.deriveFont(Font.PLAIN, 15));
        exitButton.setFont(latoFont.deriveFont(Font.PLAIN, 15));

        homeButton.addActionListener(e -> {
            dispose();
            new IndexPage().setVisible(true);
        });

        backButton.addActionListener(e -> {
            dispose();
            game.setVisible(true);
        });

        exitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(homeButton);
        buttonPanel.add(backButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void loadScores(String filterPlayer) {
        HighscoreManager highscoreManager = new HighscoreManager();
        List<PlayerStats> playerStatsList = highscoreManager.getPlayerStatsList();

        String[] columnNames = {"Player Name", "Score", "Words Guessed", "Games Played", "Category Scores"};
        DefaultTableModel model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        for (PlayerStats stats : playerStatsList) {
            if (filterPlayer != null && !filterPlayer.equals(stats.getPlayerName())) {
                continue;
            }
            StringBuilder categoryScores = new StringBuilder();
            for (String category : stats.getCategoryScores().keySet()) {
                categoryScores.append(category).append(": ").append(stats.getCategoryScores().get(category)).append(" ");
            }
            Object[] rowData = {
                    stats.getPlayerName(),
                    stats.getScore(),
                    stats.getWordsGuessed(),
                    stats.getGamesPlayed(),
                    categoryScores.toString()
            };
            model.addRow(rowData);
        }
        scoreTable.setModel(model);
    }

    private void loadPlayerStats(String playerName) {
        loadScores(playerName);
    }
}
