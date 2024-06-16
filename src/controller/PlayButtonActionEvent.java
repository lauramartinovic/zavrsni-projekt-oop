package controller;

import java.util.EventObject;

/**
 * This class represents an event for the play button action.
 * It extends the EventObject class and includes additional information about the player's name and selected category.
 */
public class PlayButtonActionEvent extends EventObject {
    private String playerName;
    private String category;

    /**
     * Constructs a PlayButtonActionEvent.
     *
     * @param source     the object on which the Event initially occurred
     * @param playerName the name of the player
     * @param category   the selected game category
     * @throws IllegalArgumentException if source is null
     */
    public PlayButtonActionEvent(Object source, String playerName, String category) {
        super(source);
        this.playerName = playerName;
        this.category = category;
    }

    /**
     * Gets the player's name.
     *
     * @return the player's name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Gets the selected game category.
     *
     * @return the selected game category
     */
    public String getCategory() {
        return category;
    }
}
