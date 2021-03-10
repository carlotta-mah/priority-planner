package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * Die Klasse MessageToClient representiert die Nachricht die vom Server zum Clienten geschickt wird.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
@AllArgsConstructor
@Data
public class MessageToClient {
    private de.projekt.priorityplanner.model.MessagePhase event;
    private List usernames;
    private String HTML;
    private boolean admin;
    private List userStories;

    /**
     * Initialisiert MessageToClient.
     *
     * @param event Das Event
     * @param userStories Das zur verschickende Feature / UserStory
     */
    public MessageToClient(MessagePhase event, List userStories) {
        this.event = event;
        this.userStories = userStories;
    }
    public MessageToClient(de.projekt.priorityplanner.model.MessagePhase event) {
        this.event = event;
    }
}
