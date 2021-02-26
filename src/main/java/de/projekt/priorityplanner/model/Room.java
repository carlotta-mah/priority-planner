package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Room {
    private int id;
    private List<Feature> features;
    private List<String> users;

    public void addUser (String newUser){
        users.add(newUser);
    }
    public void addFeature(Feature newFeature){
        features.add(newFeature);
    }

}

