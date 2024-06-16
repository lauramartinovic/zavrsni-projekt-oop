package controller;

import java.util.EventObject;

/**
 * This class represents an event for the new game button action.
 * It extends the EventObject class and passes the source object to the superclass constructor.
 */
public class NewGameButtonActionEvent extends EventObject {

    /**
     * Constructs a NewGameButtonActionEvent.
     *
     * @param source the object on which the Event initially occurred
     * @throws IllegalArgumentException if source is null
     */
    public NewGameButtonActionEvent(Object source) {
        super(source);
    }
}
