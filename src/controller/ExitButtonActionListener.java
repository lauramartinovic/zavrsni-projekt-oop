package controller;

import java.util.EventListener;

/**
 * Interface extending EventListener.
 */
public interface ExitButtonActionListener extends EventListener {

    /**
     * Invoked when the exit button action occurs.
     *
     * @param event the event to be processed
     */
    void exitButtonActionPerformed(ExitButtonActionEvent event);
}
