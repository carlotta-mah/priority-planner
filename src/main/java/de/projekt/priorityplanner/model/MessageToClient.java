package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class MessageToClient {
    private de.projekt.priorityplanner.model.MessagePhase event;
    private List usernames;
    private String HTML;
    private boolean admin;
    private List userStories;

    public MessageToClient(MessagePhase event, List userStories) {
        this.event = event;
        this.userStories = userStories;
    }

    public MessageToClient(de.projekt.priorityplanner.model.MessagePhase event) {
        this.event = event;
    }
}
