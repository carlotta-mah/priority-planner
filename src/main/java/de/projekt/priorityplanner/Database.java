package de.projekt.priorityplanner;


import de.projekt.priorityplanner.model.Feature;
import de.projekt.priorityplanner.model.Room;
import de.projekt.priorityplanner.model.UserStory;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: replace with actual Database
// entity Room (or Session):  id, [usernames], adminSessionId
// entity UserStories: id, roomId, String, (maybe username too)
public class Database {

    static Map<Integer, List> rooms = new HashMap();
    static Map<Integer, Room> rooms1 = new HashMap();
    static Map<Integer, List> userStories = new HashMap();
    static Map<Integer, String> admins = new HashMap();

    static HashMap<Integer, Integer> counters = new HashMap<>();

    static int n = 0;

    static public int addRoom() {
        n++;
        //rooms.put(n, new LinkedList<String>());
        Room room = new Room(n, new LinkedList<Feature>(), new LinkedList<String>());
        rooms1.put(n,room);
        //userStories.put(n, new LinkedList<UserStory>());
        admins.put(n, null);
        counters.put(n, 0);
        return n;
    }



    static public void setAdmin(int i, String sessionId) {
        admins.replace(i, sessionId);
    }

    static public boolean adminNull(int i) {
        return admins.get(i) == null;
    }

    static public boolean adminEquals(int i, String sessionId) {
        return admins.get(i).equals(sessionId);
    }

    static public void addUsername(int i, String username) {
        //rooms.get(i).add(username);
        rooms1.get(i).addUser(username);
        int v = counters.get(i);
        counters.replace(i, ++v);
    }

    static public int reduceCounter(int i) {
        int v = counters.get(i);
        counters.replace(i, --v);
        return v;
    }

    static public List getUsernames(int i) {
        //return rooms.get(i);
        Room room = rooms1.get(i);
        return room.getUsers();
    }

    static public boolean containsRoom(int i) {
        //return rooms.get(i) != null;
        return rooms1.get(i) != null;
    }
    static public List<Feature> getFeatures(int i){
        Room room =  rooms1.get(i);
        if (room != null) {
            return room.getFeatures();
        }
        else{return null;}
    }
    static  public Room getRoom(int i){
        Room room =  rooms1.get(i);
        if (room != null) {
            return room;
        }
        else{
            //TODO Exception
        }
        return null; //TODO remove this
    }

    static public void addUserStory(int roomId, Feature feature) {
        //userStories.get(roomId).add(UserStory);
        Room room = rooms1.get(roomId);
        room.addFeature(feature);

    }
    static public void addUserStory(int roomId, UserStory userStory) {
        userStories.get(roomId).add(userStory);
    }

    static public List<Feature> getUserStories(int roomId) {
        //return userStories.get(roomId);
        Room room = rooms1.get(roomId);
        return room.getFeatures();
    }

    public static List<String> removeUser(int roomId, String username) {
        Room room = rooms1.get(roomId);
        room.removeUser(username);
        return room.getUsers();
    }
}
