package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Data

public class Room {
    private int id;
    private String roomName;
    private List<Feature> features;
    private List<User> users;
    private Feature activeFeature;
    private de.projekt.priorityplanner.model.MessagePhase event;
    static private int idCount;

    public Room(int id, String roomName,List<Feature> features, List<User> users, Feature activeFeature, MessagePhase event) {
        this.id = id;
        this.roomName = roomName;
        this.features = features;
        this.users = users;
        this.activeFeature = activeFeature;
        this.event = event;
        idCount = 0;
    }

    public List getOnlyUserNames(){
        List<String> userNames = new LinkedList<>();
        for (User user: users) {
                userNames.add(user.getName());
        }
        return userNames;
    }

    public void addUser(User newUser) {
        users.add(newUser);
    }

    public void addFeature(Feature newFeature) {
        newFeature.setId(idCount);
        idCount++;
        features.add(newFeature);
    }

    public void removeUser(User username) {
        users.remove(username);
    }


    public int getNumberOfVotes(){
        if(activeFeature != null){
            return activeFeature.getNumberOfVotes();
        }
        else return 0;
    }
    public Feature selectFeature(int id) {
        for (Feature feature : features) {
            if (feature.equals(id)) {
                activeFeature = feature;
                return activeFeature;
            }
        }
        return null;//TODO Fehlerbehandlung
    }

    public Feature getFeatureById(int id){
        for (Feature feature:features) {
            if (feature.getId() == id){
                return  feature;
            }
        }
        //TODO: fehlerbehandlung
        return null;
    }

    public int getFeatureId(Feature feature) {
        return idCount;
    }

    public void deleteFeature(int id) {
        features.remove(getFeatureById(id));
    }

    public Feature getNextFeature() {
        for(Feature f : features){
            if(!(f.getIsVoted()))
            {
                return f;
            }
        }
        return null;
    }
}

