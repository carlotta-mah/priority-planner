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
    private Feature activeFeature;
    private de.projekt.priorityplanner.model.MessagePhase event;

    public void addUser(String newUser) {
        users.add(newUser);
    }

    public void addFeature(Feature newFeature) {
        features.add(newFeature);
    }

    public void removeUser(String username) {
        users.remove(username);
    }

    public Feature getFeature(String name, String beschreibung) {
        for (Feature feature : features) {
            if (feature.equals(name, beschreibung)) {
                return feature;
            }
        }
        //TODO: Fehlerbehandlung
        return null;
    }

    public void selectFeature(String name, String beschreibung) {
        for (Feature feature : features) {
            if (feature.equals(name, beschreibung)) {
                activeFeature = feature;
            }
        }//TODO Fehlerbehandlung
    }

}

