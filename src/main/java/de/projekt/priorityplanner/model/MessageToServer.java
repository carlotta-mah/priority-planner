package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;

// Message send by client
@Data
@AllArgsConstructor
public class MessageToServer {
    private String username;
    private int roomId;
    private LinkedList<String> userStories;
    private de.projekt.priorityplanner.model.MessagePhase phase;
}
