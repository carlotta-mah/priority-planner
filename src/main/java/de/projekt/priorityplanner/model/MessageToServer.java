package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;

// Message send by client
@Data
@AllArgsConstructor
public class MessageToServer {
    private String username;
    private int roomId;
    private LinkedList<String> userStories;
    private de.projekt.priorityplanner.model.MessagePhase phase;

    public UserStory createUserStory() {

        int bewertung1;
        String bew1 = userStories.get(2);
        bewertung1 = Integer.parseInt(bew1);

        int bewertung2;
        String bew2 = userStories.get(3);
        bewertung2 = Integer.parseInt(bew2);

        int zeit;
        String z = userStories.get(4);
        zeit = Integer.parseInt(z);

        return new UserStory(userStories.get(0),userStories.get(1),bewertung1,bewertung2, zeit );
    }

    public Vote createVote(){

        int bewertung1;
        String bew1 = userStories.get(2);
        bewertung1 = Integer.parseInt(bew1);

        int bewertung2;
        String bew2 = userStories.get(3);
        bewertung2 = Integer.parseInt(bew2);

        int zeit;
        String z = userStories.get(4);
        zeit = Integer.parseInt(z);

        return new Vote(username,bewertung1,bewertung2,zeit, MessagePhase.VOTE);
    }

    public String getUserStoryName(){
        return userStories.get(0);
    }

    public String getUserStoryBeschreibung(){
        return userStories.get(1);
    }

    public UserStory createUserStoryHead() {
        return new UserStory(userStories.get(0),userStories.get(1));
    }
}
