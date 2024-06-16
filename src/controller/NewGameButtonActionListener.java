package controller;

import java.util.EventListener;

/**
 * This interface should be implemented by any class that wants to handle the new game button action events.
 */
public interface NewGameButtonActionListener extends EventListener {

    /**
     * Invoked when a new game button action event occurs.
     *
     * @param event the NewGameButtonActionEvent containing information about the event
     */
    void newGameButtonActionPerformed(NewGameButtonActionEvent event);
}
