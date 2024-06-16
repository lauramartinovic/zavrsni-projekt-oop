package controller;

import java.util.EventObject;

/**
 * This class represents an event for the guess button action.
 * It extends the EventObject class and passes the source object to the superclass constructor.
 */
public class GuessButtonActionEvent extends EventObject {

    /**
     * Constructs a GuessButtonActionEvent.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public GuessButtonActionEvent(Object source) {
        super(source);
    }
}
