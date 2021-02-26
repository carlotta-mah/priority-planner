package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
public class Feature {
    private String title;
    private String description;
    private de.projekt.priorityplanner.model.MessagePhase event;

    public Feature(String title, String description, MessagePhase phase){
        this.event = phase;
        this.title = title;
        this.description = description;
    }
}
