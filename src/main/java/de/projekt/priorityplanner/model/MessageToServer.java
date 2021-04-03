package de.projekt.priorityplanner.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import de.projekt.priorityplanner.model.entity.Vote;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedList;

/**
 * Die Klasse MessageToServer representiert die Nachricht die vom Client zum Server geschickt wird.
 * Beihaltet Username, RaumId, FeatureId, Rolle des Users, und den Content als Liste.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageToServer {
    private String username;
    private int roomId;
    private int featureId;
    private String roll;
    private LinkedList<String> content;
    private de.projekt.priorityplanner.model.MessagePhase phase;

    @JsonCreator
    public MessageToServer(@JsonProperty("username")String username, @JsonProperty("roomId")int roomId, @JsonProperty("roll")String roll){
        this.username = username;
        this.roomId = roomId;
        this.roll = roll;
    }
    /**
     * Erstellt ein Vote aus der Message.
     * @return Eine Bewerung in Form der Klasse Vote
     */
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

    /**
     * Gibt den Name des Features, welches in der Nachricht verschickt wurde.
     * @return Name des Features
     */
    public String getUserStoryName(){
        return content.get(0);
    }

    /**
     * Gibt die Beschreibung des Features zurück, welches in der Nachricht verschickt wurde.
     * @return Beschreibung des Features
     */
    public String getUserStoryBeschreibung(){
        return content.get(1);
    }

}
