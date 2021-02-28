package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data

public class Room {
    private int id;
    private List<Feature> features;
    private List<String> users;
    private Feature activeFeature;
    private de.projekt.priorityplanner.model.MessagePhase event;
    static private int idCount;

    public Room(int id, List<Feature> features, List<String> users, Feature activeFeature, MessagePhase event) {
        this.id = id;
        this.features = features;
        this.users = users;
        this.activeFeature = activeFeature;
        this.event = event;
        idCount = 0;
    }

    public void addUser(String newUser) {
        users.add(newUser);
    }

    public void addFeature(Feature newFeature) {
        newFeature.setId(idCount);
        idCount++;
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

    public int getNumberOfVotes(){
        if(activeFeature != null){
            return activeFeature.getNumberOfVotes();
        }
        else return 0;
    }
    public Feature selectFeature(String name, String beschreibung) {
        for (Feature feature : features) {
            if (feature.equals(name, beschreibung)) {
                activeFeature = feature;
                return activeFeature;
            }
        }
        return null;//TODO Fehlerbehandlung
    }

    public int getFeatureId(Feature feature) {
        return idCount;
    }
}

