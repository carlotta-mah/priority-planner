package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Ein Raum. Ein Raum besteht aus einer Id, einem Raumnamen, Liste von Features, Liste von Usern und
 * einem ausgewähltem Feature
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
@Data

public class Room {
    private int id;
    private String roomName;
    private List<Feature> features;
    private List<User> users;
    private Feature activeFeature;
    private de.projekt.priorityplanner.model.MessagePhase event;
    static private int idCount;

    /**
     * Inizalisiert den Raum.
     *
     * @param id            Die RaumId
     * @param roomName      Den Raumname
     * @param features      Liste von Features die im Raum eingetragen wurden
     * @param users         Liste von Usern die sich in dem Raum aufhalten.
     * @param activeFeature Das ausgewählte Feature
     * @param event         Das Event
     */
    public Room(int id, String roomName, List<Feature> features, List<User> users, Feature activeFeature, MessagePhase event) {
        this.id = id;
        this.roomName = roomName;
        this.features = features;
        this.users = users;
        this.activeFeature = activeFeature;
        this.event = event;
        idCount = 0;
    }

    /**
     * Gibt eine Liste von Name der User die sich in dem Raum aufhalten
     *
     * @return Liste von Usernamen in vorm einer Stringliste
     */
    public List getOnlyUserNames() {
        List<String> userNames = new LinkedList<>();
        for (User user : users) {
            userNames.add(user.getName());
        }
        return userNames;
    }

    /**
     * Fügt ein User dem Raum hinzu
     *
     * @param newUser User der hinzugefügt werden soll.
     */
    public synchronized void addUser(User newUser) {
        users.add(newUser);
    }

    /**
     * Fügt ein Feature dem Raum hinzu
     *
     * @param newFeature Das neue Feature
     */
    public synchronized void addFeature(Feature newFeature) {
        newFeature.setId(idCount);
        idCount++;
        features.add(newFeature);
    }

    /**
     * Entfernt ein User aus dem Raum
     *
     * @param username Name des User der entfernt werden soll
     */
    public synchronized void removeUser(String username) {
        users.removeIf(user -> user.getName().equals(username));
    }

    /**
     * Entfernt ein User aus dem Raum
     *
     * @param user User der entfernt werden soll
     */
    public synchronized void removeUser(User user) {
        users.remove(user);
    }

    /**
     * Gibt die Anzahl der Votes die bereits für das ausgewählte Feature schon abgestimmt haben
     *
     * @return Anzahl der Votes
     */
    public int getNumberOfVotes() {
        if (activeFeature != null) {
            return activeFeature.getNumberOfVotes();
        } else return 0;
    }

    /**
     * Setzt das activeFeature auf das Feature welches als Parameter übergeben wird
     *
     * @param id Id des neuen aktven Feature
     * @return
     */
    public Feature selectFeature(int id) {
        for (Feature feature : features) {
            if (feature.equals(id)) {
                activeFeature = feature;
                return activeFeature;
            }
        }
        return null;//TODO Fehlerbehandlung
    }

    /**
     * Gibt ein Feature anhand der Id
     *
     * @param id Id des gesuchten Features
     * @return Passendes Feature zur Id
     */
    public synchronized Feature getFeatureById(int id) {
        for (Feature feature : features) {
            if (feature.getId() == id) {
                return feature;
            }
        }
        //TODO: fehlerbehandlung
        return null;
    }

    /**
     * gibt den aktuellen FeatureCount wieder
     *
     * @return
     */
    public int getFeatureCount() {
        return idCount;
    }


    /**
     * Löscht ein Feature aus dem Raum
     *
     * @param id Id des Feature welches gelöscht werden soll
     */
    public synchronized void deleteFeature(int id) {
        features.remove(getFeatureById(id));
    }

    /**
     * Gib das nächste Feature des Raumes zurück und setzt das activeFeature neu.
     *
     * @return das nue activeFeature
     */
    public Feature getNextFeature() {
        for (Feature f : features) {
            if (!(f.getIsVoted())) {
                activeFeature = f;
                return f;
            }
        }
        return null;
    }
}

