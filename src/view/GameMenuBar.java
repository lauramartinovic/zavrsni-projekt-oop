package view;

import controller.ExitButtonAction;
import controller.NewGameButtonAction;
import model.Game;

import javax.swing.*;

/**
 * This class represents the menu bar with options for new game, exit, and statistics.
 * It adds action listeners for each menu item.
 */
public class GameMenuBar extends JMenuBar {

    /**
     * Constructs a GameMenuBar and sets up the menu items with their action listeners.
     *
     * @param game the Game instance to associate with the menu bar
     */
    public GameMenuBar(Game game) {
        JMenu gameMenu = new JMenu("Game");
        JMenuItem newGameItem = new JMenuItem("New Game");
        JMenuItem exitItem = new JMenuItem("Exit");

        NewGameButtonAction newGameButtonAction = new NewGameButtonAction();
        newGameButtonAction.setNewGameButtonActionListener(game);
        newGameItem.addActionListener(newGameButtonAction);

        ExitButtonAction exitButtonAction = new ExitButtonAction();
        exitButtonAction.setExitButtonActionListener(event -> System.exit(0));
        exitItem.addActionListener(exitButtonAction);

        gameMenu.add(newGameItem);
        gameMenu.add(exitItem);

        JMenu statsMenu = new JMenu("Statistics");
        JMenuItem allPlayersStatsItem = new JMenuItem("All Players Stats");
        JMenuItem playerStatsItem = new JMenuItem("My Stats");

        allPlayersStatsItem.addActionListener(e -> game.showAllPlayersStats());
        playerStatsItem.addActionListener(e -> game.showPlayerStats());

        statsMenu.add(allPlayersStatsItem);
        statsMenu.add(playerStatsItem);

        add(gameMenu);
        add(statsMenu);
    }
}
