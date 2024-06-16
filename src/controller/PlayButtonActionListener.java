package controller;

import java.util.EventListener;

/**
 * This interface should be implemented by any class that wants to handle the play button action events.
 */
public interface PlayButtonActionListener extends EventListener {

    /**
     * Invoked when a play button action event occurs.
     *
     * @param event the PlayButtonActionEvent containing information about the event
     */
    void playButtonActionPerformed(PlayButtonActionEvent event);
}
