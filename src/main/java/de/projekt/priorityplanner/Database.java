package de.projekt.priorityplanner;


import de.projekt.priorityplanner.model.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Die statische Datanbank-Klasse ersetzt die Datenbank-Anbindung/Datenbank und verwaltet die Räume.
 * Die Klasse soll in Zukunft durch eine Datenbank ersetzt werden die aber die zugrundeliegende Logik beibehält
 *
 * @author Mia Mahncke, Nedim Seroka
 * @date 14.03.2021
 */
public class Database {

    static volatile Map<Integer, Room> rooms1 = new ConcurrentHashMap();
    static volatile Map<Integer, Integer> counters = new ConcurrentHashMap();
    static volatile int n = 0;

    /**
     * erstellt einen Raum und fügt ihn der Liste hinzu
     *
     * @param roomName Name des Raums
     * @return RaumID
     */
    static public synchronized int addRoom(String roomName) {
        n = findUniqueId();
        //rooms.put(n, new LinkedList<String>());
        Room room = new Room(n, roomName, Collections.synchronizedList(new ArrayList<Feature>()), Collections.synchronizedList(new ArrayList<User>()), null, null);
        rooms1.put(n, room);
        //userStories.put(n, new LinkedList<UserStory>());
        counters.put(n, 0);
        return n;
    }

    private synchronized static int findUniqueId() {
        while (rooms1.containsKey(n)) {
            n++;
        }
        return n;
    }

    /**
     * Fügt einem feature in einem best. Raum eine Bewertung hinzu
     *
     * @param vote      bewetung
     * @param roomId    raum in dem bewertet wurde
     * @param featureId feature, das bewertet wurde
     */
    static public synchronized void addVote(Vote vote, int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        room.getFeatureById(featureId).addVote(vote);
    }

    /**
     * testet ob alle user das aktive Feature bewertet haben
     *
     * @param roomId RaumID in dem das Feature exsistiert
     * @return ob alle user eine Bewertung abgegeben haben
     */
    static public Boolean allVoted(int roomId) {
        Room room = rooms1.get(roomId);
        if (room.getNumberOfVotes() == counters.get(roomId)) {
            return true;
        } else return false;

    }

    /**
     * fügt einen user hinzu
     *
     * @param roomId   RaumID des Raums in den der user kommt
     * @param username Name des neuen Users
     * @param rolle    Rolle des neuen Users
     */
    static public synchronized void addUser(int roomId, String username, String rolle) {
        //rooms.get(i).add(username);
        rooms1.get(roomId).addUser(new User(username, rolle));
        int v = counters.get(roomId);
        counters.replace(roomId, ++v);
    }

    /**
     * Reduziert den Counter, wenn ein User den Raum, verlässt
     *
     * @param roomId Raum aus dem der User ausgetreten ist
     * @return Anzahl der verbleibenden User
     */
    static public synchronized int reduceCounter(int roomId) {
        int v = counters.get(roomId);
        v--;
        if (v != 0) {
            counters.replace(roomId, v);
        } else {
            removeRoom(roomId);
        }
        return v;
    }

    public static void removeRoom(int roomId) {
            rooms1.remove(roomId);
            counters.remove(roomId);
    }

    /**
     * Gibt die Liste der Usernames aus für einen Raum
     *
     * @param roomId raumID aus der die Usernamen kommen
     * @return Liste der Usernamen
     */
    static public synchronized List getUsernames(int roomId) {
        Room room = rooms1.get(roomId);
        return room.getOnlyUserNames();
    }

    /**
     * Gibt an, ob ein Raum exsistiert
     *
     * @param i raumId
     * @return ob der Raum mit raumId i exsistiert
     */
    static public synchronized boolean containsRoom(int i) {
        return rooms1.get(i) != null;
    }

    /**
     * gibt den Raum mit der RaumID roomId zurück
     *
     * @param roomId
     * @return Raum mit RaumID roomId
     */
    static public Room getRoom(int roomId) {
        Room room = rooms1.get(roomId);
        if (room != null) {
            return room;
        } else {
            //TODO Exception
        }
        return null; //TODO remove this
    }

    /**
     * Fügt ein Feature in einem Raum hinzu
     *
     * @param roomId  RaumID von dem Raum in dem das Feature hinzugefügt wird
     * @param feature Feature das hinzugefügt wird
     */
    static public synchronized void addFeature(int roomId, Feature feature) {
        //userStories.get(roomId).add(UserStory);
        Room room = rooms1.get(roomId);
        room.addFeature(feature);
    }

    /**
     * gibt die Features aus einem Raum wieder
     *
     * @param roomId RaumId von Raum aus dem die Features wiedergegeben werden
     * @return Features
     */
    static public List<Feature> getFeatures(int roomId) {
        Room room = rooms1.get(roomId);
        return room.getFeatures();
    }

    /**
     * entfertn einen User
     *
     * @param roomId   raumID von Raum aus dem User entfernt wird
     * @param username Name des User der entfernt wird
     * @return Liste der verbleibenden User
     */
    public static synchronized List<String> removeUser(int roomId, String username) {
        Room room = rooms1.get(roomId);
        room.removeUser(username);
        reduceCounter(roomId);
        return room.getOnlyUserNames();
    }

    /**
     * Setzt das feature mit FeatureID featureid aktiv
     *
     * @param roomId    ID von dem Raum in dem das Feature aktiv gesetzt wird
     * @param featureId ID des Features das aktiv gesetzt wird
     * @return das aktive Feature
     */
    public static Feature selectFeature(int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        return room.selectFeature(featureId);
    }

    /**
     * Erzeugt einen Namen der noch nicht im Raum vorkommt
     *
     * @param name   Name der schon im Raum vorkommt
     * @param roomId ID von dem Raum
     * @return neuer Name
     */
    public static synchronized String generateUniqueName(String name, int roomId) {
        String s = name;
        int i = 1;
        while (getUsernames(roomId).contains(s + i)) {
            i++;
        }
        return s + i;
    }

    /**
     * Löscht ein Feature
     *
     * @param roomId ID des Raums in dem das Feature gelöscht wird
     * @param id     ID des Features das gelöscht wird
     */
    public static synchronized void deleteFeature(int roomId, int id) {
        Room room = rooms1.get(roomId);
        room.deleteFeature(id);
    }

    /**
     * gibt ein Feature aus einem Raum wieder
     *
     * @param roomId    ID des Raums
     * @param featureId ID des Features
     * @return passendes Feature
     */
    public static Feature getFeature(int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        return room.getFeatureById(featureId);
    }

    /**
     * gibt das nächste, noch nicht bewertete Feature aus einem Raum wieder
     *
     * @param roomId ID des Raums
     * @return nächstes unbewertetes Feature
     */
    public static Feature getNextFeature(int roomId) {
        Room room = rooms1.get(roomId);
        return room.getNextFeature();
    }
}
