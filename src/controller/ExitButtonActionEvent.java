package controller;

import java.util.EventObject;

/**
 * Class extending EventObject representing the event passed to the listener when the exit button is pressed.
 */
public class ExitButtonActionEvent extends EventObject {

    /**
     * Calls the constructor of the superclass EventObject.
     *
     * @param source the object on which the Event initially occurred
     */
    public ExitButtonActionEvent(Object source) {
        super(source);
    }
}
