package controller;

import model.Game;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PlayButtonAction implements ActionListener {
    private JTextField enterName;
    private JComboBox<String> categoryComboBox;
    private PlayButtonActionListener listener;

    public PlayButtonAction(JTextField enterName, JComboBox<String> categoryComboBox) {
        this.enterName = enterName;
        this.categoryComboBox = categoryComboBox;
    }

    public void setPlayButtonActionListener(PlayButtonActionListener listener) {
        this.listener = listener;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String playerName = enterName.getText();
        String selectedCategory = (String) categoryComboBox.getSelectedItem();
        if (listener != null) {
            listener.playButtonActionPerformed(new PlayButtonActionEvent(this, playerName, selectedCategory));
        } else {
            if (playerName.isEmpty() || selectedCategory == null) {
                JOptionPane.showMessageDialog(null, "Please enter your name and select a category.");
                return;
            }
            new Game(playerName, selectedCategory); // Prosljeđivanje odabrane kategorije
        }
    }
}
