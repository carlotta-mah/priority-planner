package de.projekt.priorityplanner;


import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

// TODO: replace with actual Database
// entity Room (or Session):  id, [usernames], adminSessionId
// entity UserStories: id, roomId, String, (maybe username too)
public class Database {

    static Map<Integer, List> rooms = new HashMap();
    static Map<Integer, List> userStories = new HashMap();
    static Map<Integer, String> admins = new HashMap();

    static HashMap<Integer, Integer> counters = new HashMap<>();

    static int n = 0;

    static public int addRoom() {
        n++;
        rooms.put(n, new LinkedList<String>());
        userStories.put(n, new LinkedList<String>());
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
        rooms.get(i).add(username);
        int v = counters.get(i);
        counters.replace(i, ++v);
    }

    static public int reduceCounter(int i) {
        int v = counters.get(i);
        counters.replace(i, --v);
        return v;
    }

    static public List getUsernames(int i) {
        return rooms.get(i);
    }

    static public boolean containsRoom(int i) {
        return rooms.get(i) != null;
    }

    static public void addUserStory(int roomId, String UserStory) {
        userStories.get(roomId).add(UserStory);
    }

    static public List<String> getUserStories(int roomId) {
        return userStories.get(roomId);
    }
}
