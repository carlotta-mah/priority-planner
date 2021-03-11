package de.projekt.priorityplanner.controller;

import de.projekt.priorityplanner.Database;
import de.projekt.priorityplanner.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.*;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Ein RoomController reguliert die Komunikation von Client und Server.
 * Die Klasse sorgt also dafür das Räume richtige erstellt und User richtig hinzugefügt werden.
 * Änderungen auf Clientseite wird hier entgegen genommen verarbeitet und weitergeleitet.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @date 14.03.2021
 */
@Slf4j
@Controller
public class RoomController {

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    /**
     * Erstellt ein Neuen Raum und gibt die RoomId zurück
     * @param produktName Der Name des zu entwerfenden Produkt oder auch Name des Raums
     * @return Die RaumId
     */
    @ResponseBody
    @RequestMapping("/create-room")
    public int createRoom(@RequestHeader("produktName") String produktName) {
        return Database.addRoom(produktName);
    }

    /**
     * Fügt ein User einem Raum hinzu und updatet alle anderen Clients duch eine update Message
     *
     * @param roomId Die RaumId
     * @param message Die Nachricht die vom Client geschickt worden ist
     * @param headerAccessor Header der Nachricht
     */
    // adds a username to a room and sends a updateMessage to all users of that room
    @MessageMapping("/room/{roomId}/addUser")
    public void addUser(@DestinationVariable int roomId, @Payload MessageToServer message,
                        SimpMessageHeaderAccessor headerAccessor) {
        // TODO: add username to actual Database
        String s = message.getUsername();
        Database.addUser(roomId, s, message.getRoll());


        Objects.requireNonNull(headerAccessor.getSessionAttributes()).put("username", s);
        headerAccessor.getSessionAttributes().put("room_id", message.getRoomId());
        log.info("Received greeting message {}", message);

        Room room = Database.getRoom(roomId);
        room.setEvent(MessagePhase.ADDED_USER);
        messagingTemplate.convertAndSend("/queue/" + roomId, room);

    }

    /**
     * Sendet auf Nachfrage die aktuellen Ergebnisse an die Clients über die ergebnis Queue.
     * Das Ergebnis beinhaltet die einteilung in
     * mustHave, shouldHave, couldHave und wontHave;
     *
     * @param roomId Die RaumId
     * @param headerAccessor Header der Client Nachricht
     */
    @MessageMapping("/room/{roomId}/ergebnis")
    public void sendEgebnis(@DestinationVariable int roomId,
                            SimpMessageHeaderAccessor headerAccessor) {
        Room room = Database.getRoom(roomId);

        Ergebnis ergebnis = null;
        if (Database.containsRoom(roomId)) {
            assert room != null;
            ergebnis = new Ergebnis(room);
        }
        assert ergebnis != null;
        messagingTemplate.convertAndSend("/queue/ergebnis/" + roomId, ergebnis);
    }

    /**
     * Verwaltet das Hinzufügen und Löschen von Features über die Feature Queue.
     * Die Datenbank wird dementsprechend angepasst
     * @param roomId Die RaumId
     * @param feature Das Feature welches hinzugefügt oder gelöscht werden soll
     * @param headerAccessor Header der Nachricht
     */
    @MessageMapping("/room/{roomId}/addFeature")
    public void addFeature(@DestinationVariable int roomId, @Payload Feature feature,
                           SimpMessageHeaderAccessor headerAccessor) {
        switch (feature.getEvent()) {
            case FEATURE:
                feature.setBoostMean(-1);
                feature.setRipMean(-1);
                Database.addFeature(roomId, feature);
                feature.setEvent(MessagePhase.FEATURE);
                messagingTemplate.convertAndSend("/queue/feature/" + roomId, feature);
                break;
            case DELETE:
                Feature deletefeature = Database.getFeature(roomId, feature.getId());
                Database.deleteFeature(roomId, feature.getId());
                deletefeature.setEvent(MessagePhase.DELETE);
                messagingTemplate.convertAndSend("/queue/feature/" + roomId, deletefeature);
        }
    }

    /**
     * Sendet auf Nachfrage das Aktuelle Result an die Clients über die Queue.
     * Das Result beinhaltet sämtliche Mittelwerte und Standdardabweichungen
     *
     * @param roomId Die RaumId
     * @param featureId Header der Client Nachricht
     */
    @MessageMapping("room/{roomId}/result")
    public void getResult(@DestinationVariable int roomId, @Payload int featureId) {
        try {
            Feature feature = Database.getRoom(roomId).getFeatureById(featureId);
            feature.calculateResult();
            feature.setEvent(MessagePhase.RESULT);
            messagingTemplate.convertAndSend("/queue/" + roomId, feature);
        }catch (NullPointerException e){
            log.error("Feature was null", e);
        }

    }

    /**
     * Verwaltet die Votes. Es wird zwichen den Fällen ADDVOTE, VOTE, VOTEAGAIN und NEXT unterschieden.
     * Die Clientseite wird entsprechen der Fälle synchronisiert.
     * @param roomId Die RaumId
     * @param message Die Nachricht vom Clienten
     * @param headerAccessor Header der Nachricht
     * @throws IOException
     */
    @MessageMapping("/room/{roomId}/sendMessage")
    @SendTo("/queue/{roomId}")
    public void sendMessage(@DestinationVariable int roomId, @Payload MessageToServer message,
                            SimpMessageHeaderAccessor headerAccessor) throws IOException {
        int room = message.getRoomId();

        switch (message.getPhase()) {
            case ADDVOTE:
                selectVotingFeature(message);
                break;
            case VOTE:
                Vote vote = message.createVote();
                int raumId = message.getRoomId();
                Database.addVote(vote, raumId, message.getFeatureId());
                if (Database.allVoted(raumId)) {
                    vote.setEvent(MessagePhase.ALLVOTED);
                }
                messagingTemplate.convertAndSend("/queue/" + room, vote);
                break;
            case VOTEAGAIN:
                try{
                    Database.getRoom(message.getRoomId()).getFeatureById(message.getFeatureId()).resetVote();
                    selectVotingFeature(message);
                }catch (NullPointerException e){
                    log.error("feature not found", e );
                }
                break;
            case NEXT:
                Feature f = Database.getNextFeature(roomId);
                if (f != null) {
                    f.setEvent(MessagePhase.ADDVOTE);
                    messagingTemplate.convertAndSend("/queue/" + roomId, f);
                } else {
                    MessageToClient nullMessage = new MessageToClient(MessagePhase.EMPTY);
                    messagingTemplate.convertAndSend("/queue/" + roomId, nullMessage);
                }
                break;
        }
    }

    /**
     * Befasst sich mit dem Fall das ein Feature zum Voten ausgewählt wird.
     * @param message Die Nachricht vom Client
     */
    public void selectVotingFeature(MessageToServer message) {
        int roomId = message.getRoomId();
        try {
            Feature selectedFeature = Database.selectFeature(roomId, message.getFeatureId());
            selectedFeature.setEvent(MessagePhase.ADDVOTE);
            List<String> feature = new ArrayList<>();

            feature.add(selectedFeature.getTitle());
            feature.add(selectedFeature.getDescription());

            messagingTemplate.convertAndSend("/queue/" + roomId, selectedFeature);
        }
        catch (NullPointerException e){
            log.error("Active feature was null", e);
        }
    }

}
