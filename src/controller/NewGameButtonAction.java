package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class handles the event of pressing the new game button. It sets a listener and forwards the event to the appropriate listener.
 */
public class NewGameButtonAction implements ActionListener {
    private NewGameButtonActionListener listener;

    /**
     * Sets the NewGameButtonActionListener.
     *
     * @param listener the listener to be set for the new game button action
     */
    public void setNewGameButtonActionListener(NewGameButtonActionListener listener) {
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
            listener.newGameButtonActionPerformed(new NewGameButtonActionEvent(this));
        }
    }
}
