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
    private int featureId;
    private String roll;
    private LinkedList<String> content;
    private de.projekt.priorityplanner.model.MessagePhase phase;

    public UserStory createUserStory() {

        int bewertung1;
        String bew1 = content.get(2);
        bewertung1 = Integer.parseInt(bew1);

        int bewertung2;
        String bew2 = content.get(3);
        bewertung2 = Integer.parseInt(bew2);

        int zeit;
        String z = content.get(4);
        zeit = Integer.parseInt(z);

        return new UserStory(content.get(0), content.get(1),bewertung1,bewertung2, zeit );
    }

    public Vote createVote(){

        int bewertung1;
        String bew1 = content.get(2);
        bewertung1 = Integer.parseInt(bew1);

        int bewertung2;
        String bew2 = content.get(3);
        bewertung2 = Integer.parseInt(bew2);

        int zeit;
        String z = content.get(4);
        zeit = Integer.parseInt(z);

        return new Vote(username,bewertung1,bewertung2,zeit, MessagePhase.VOTE, roll);
    }

    public String getUserStoryName(){
        return content.get(0);
    }

    public String getUserStoryBeschreibung(){
        return content.get(1);
    }

    public UserStory createUserStoryHead() {
        return new UserStory(content.get(0), content.get(1));
    }
}
