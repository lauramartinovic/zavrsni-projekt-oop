package controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class handles the event of pressing the play button. It sets a listener, retrieves player name and category from UI components,
 * and forwards the event to the appropriate listener.
 */
public class PlayButtonAction implements ActionListener {
    private PlayButtonActionListener listener;
    private JTextField enterName;
    private JComboBox<String> categoryComboBox;

    /**
     * Constructs a PlayButtonAction with the specified text field and combo box.
     *
     * @param enterName        the text field for entering the player's name
     * @param categoryComboBox the combo box for selecting the game category
     */
    public PlayButtonAction(JTextField enterName, JComboBox<String> categoryComboBox) {
        this.enterName = enterName;
        this.categoryComboBox = categoryComboBox;
    }

    /**
     * Sets the PlayButtonActionListener.
     *
     * @param listener the listener to be set for the play button action
     */
    public void setPlayButtonActionListener(PlayButtonActionListener listener) {
        this.listener = listener;
    }

    /**
     * Invoked when an action occurs. It retrieves the player's name and selected category from the UI components.
     * If the player's name is empty, it shows a message dialog. Otherwise, it forwards the action event to the listener.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String playerName = enterName.getText();
        String category = (String) categoryComboBox.getSelectedItem();

        if (playerName.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please enter your name");
        } else {
            if (listener != null) {
                listener.playButtonActionPerformed(new PlayButtonActionEvent(this, playerName, category));
            }
        }
    }
}
