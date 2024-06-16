package controller;

import java.util.EventListener;

/**
 * This interface should be implemented by any class that wants to handle the guess button action events.
 */
public interface GuessButtonActionListener extends EventListener {

    /**
     * Invoked when a guess button action event occurs.
     *
     * @param event the GuessButtonActionEvent containing information about the event
     */
    void guessButtonActionPerformed(GuessButtonActionEvent event);
}
