package view;

import javax.swing.*;

public interface GamePanelInterface {
    void updateGallows();
    void updateScoreLabel();
    void updateWordDisplay(String text);
    void updateMissedLetters(String text);
    void clearLetterInput();
    JFrame getFrame();
    String getLetterInput();
    String getWordDisplay();
}
