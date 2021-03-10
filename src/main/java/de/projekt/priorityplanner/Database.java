package de.projekt.priorityplanner;


import de.projekt.priorityplanner.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessageSendingOperations;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// TODO: replace with actual Database
// entity Room (or Session):  id, [usernames], adminSessionId
// entity UserStories: id, roomId, String, (maybe username too)
public class Database {

    static Map<Integer, Room> rooms1 = new ConcurrentHashMap();

    static Map<Integer, String> admins = new HashMap();

    static Map<Integer, Integer> counters = new ConcurrentHashMap();

    static int n = 0;

    static public synchronized int addRoom(String roomName) {
        n++;
        //rooms.put(n, new LinkedList<String>());
        Room room = new Room(n, roomName,  Collections.synchronizedList(new ArrayList<Feature>()), Collections.synchronizedList(new ArrayList<User>()), null, null);
        rooms1.put(n, room);
        //userStories.put(n, new LinkedList<UserStory>());
        admins.put(n, null);
        counters.put(n, 0);
        return n;
    }

    static public synchronized void addVote(Vote vote, int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        room.getFeatureById(featureId).addVote(vote);
    }

    static public Boolean allVoted(int roomId) {
        Room room = rooms1.get(roomId);
        if (room.getNumberOfVotes() == counters.get(roomId)) {
            return true;
        } else return false;

    }

    static public synchronized void setAdmin(int i, String sessionId) {
        admins.replace(i, sessionId);
    }

    static public synchronized boolean adminNull(int i) {
        return admins.get(i) == null;
    }

    static public boolean adminEquals(int i, String sessionId) {
        return admins.get(i).equals(sessionId);
    }

    static public synchronized void addUsername(int i, String username, String rolle) {
        //rooms.get(i).add(username);
        rooms1.get(i).addUser(new User(username, rolle));
        int v = counters.get(i);
        counters.replace(i, ++v);
    }

    static public synchronized int reduceCounter(int i) {
        int v = counters.get(i);
        counters.replace(i, --v);
        return v;
    }

    static public synchronized List getUsernames(int i) {
        //return rooms.get(i);
        Room room = rooms1.get(i);
        return room.getOnlyUserNames();
    }

    static public synchronized boolean containsRoom(int i) {
        //return rooms.get(i) != null;
        return rooms1.get(i) != null;
    }

    static public List<Feature> getFeatures(int roomId) {
        Room room = rooms1.get(roomId);
        if (room != null) {
            return room.getFeatures();
        } else {
            return null;
        }
    }

    static public Room getRoom(int i) {
        Room room = rooms1.get(i);
        if (room != null) {
            return room;
        } else {
            //TODO Exception
        }
        return null; //TODO remove this
    }

    static public synchronized void addUserStory(int roomId, Feature feature) {
        //userStories.get(roomId).add(UserStory);
        Room room = rooms1.get(roomId);
        room.addFeature(feature);

    }

    static public List<Feature> getUserStories(int roomId) {
        //return userStories.get(roomId);
        Room room = rooms1.get(roomId);
        return room.getFeatures();
    }

    public static synchronized List<String> removeUser(int roomId, String username) {
        Room room = rooms1.get(roomId);
        room.removeUser(username);
//        for (User user : room.getUsers()) {
//            if (user.getName().equalsIgnoreCase(username)) {
//                room.removeUser(user);
//            }
//        }
        reduceCounter(roomId);
        return room.getOnlyUserNames();
    }

    public static Feature selectFeature(int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        return room.selectFeature(featureId);
    }

    public static synchronized String generateUniqueName(String name, int roomId) {
        String s = name;
        while (getUsernames(roomId).contains(s)) {
            s = s + "1";
        }
        return s;
    }

    public static synchronized void deleteFeature(int roomId, int id) {
        Room room = rooms1.get(roomId);
        room.deleteFeature(id);
    }

    public static Feature getFeature(int roomId, int featureId) {
        Room room = rooms1.get(roomId);
        return room.getFeatureById(featureId);
    }

    public static Feature getNextFeature(int roomId) {
        Room room = rooms1.get(roomId);
        return room.getNextFeature();
    }
}
