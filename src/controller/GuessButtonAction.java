package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class handles the event of pressing the guess button. It sets a listener and forwards the event to the game class.
 */
public class GuessButtonAction implements ActionListener {
    private GuessButtonActionListener listener;

    /**
     * Sets the GuessButtonActionListener.
     *
     * @param listener the listener to be set for the guess button action
     */
    public void setGuessButtonActionListener(GuessButtonActionListener listener) {
        this.listener = listener;
    }

    /**
     * Invoked when an action occurs. If a listener is set, it forwards the action event to the listener.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            listener.guessButtonActionPerformed(new GuessButtonActionEvent(this));
        }
    }
}
