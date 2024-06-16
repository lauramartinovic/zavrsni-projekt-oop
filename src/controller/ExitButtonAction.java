package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Class that implements ActionListener for handling button press events.
 * Acts as a mediator between the component and the action to be performed when the button is pressed.
 */
public class ExitButtonAction implements ActionListener {
    private ExitButtonActionListener listener;

    /**
     * Sets the listener for this action.
     *
     * @param listener the listener to be set
     */
    public void setExitButtonActionListener(ExitButtonActionListener listener) {
        this.listener = listener;
    }

    /**
     * Calls the listener method when the button is pressed.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (listener != null) {
            listener.exitButtonActionPerformed(new ExitButtonActionEvent(this));
        }
    }
}
