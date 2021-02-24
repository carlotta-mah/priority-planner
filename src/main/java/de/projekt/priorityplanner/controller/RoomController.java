package de.projekt.priorityplanner.controller;

import de.projekt.priorityplanner.Database;
import de.projekt.priorityplanner.model.MessagePhase;
import de.projekt.priorityplanner.model.MessageToClient;
import de.projekt.priorityplanner.model.MessageToServer;
import de.projekt.priorityplanner.model.UserStory;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;
import java.io.IOException;

// Controller for creating a room and adding a user to it
@Controller
public class RoomController {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    // creates a new room and returns roomId
    @ResponseBody
    @RequestMapping("/create-room")
    public int createRoom() {
        // TODO: create new room in Database and return newly created ID
        int roomId = Database.addRoom();
        return roomId;
    }

    // adds a username to a room and sends a updateMessage to all users of that room
    @MessageMapping("/room/{roomId}/addUser")
    public void addUser(@DestinationVariable String roomId, @Payload MessageToServer message,
                        SimpMessageHeaderAccessor headerAccessor) {
        int room = message.getRoomId();
        boolean admin = false;

        // TODO: add username to actual Database
        Database.addUsername(room, message.getUsername());

        // add Admin
        if (Database.adminNull(room)) {
            Database.setAdmin(room, headerAccessor.getSessionId());
            admin = true;
        }

        // update all clients in room
        MessageToClient messageC = new MessageToClient(
                MessagePhase.ADDED_USER, Database.getUsernames(room), null, admin, Database.getUserStories(room));
        messagingTemplate.convertAndSend("/queue/" + room, messageC);
    }

    @MessageMapping("/room/{roomId}/sendMessage")
    @SendTo("/queue/{roomId}")
    public void sendMessage(@DestinationVariable String roomId, @Payload MessageToServer message,
                            SimpMessageHeaderAccessor headerAccessor) throws IOException {
        int room = message.getRoomId();
        String sessionId = headerAccessor.getSessionId();

        switch (message.getPhase()) {
            // SEND Phase: add user stories to Database
            case SEND:
                /*message.getUserStories().forEach(
                        userStory -> Database.addUserStory(room, userStory)
                );*/

                UserStory userStory = message.createUserStory();
                Database.addUserStory(room, userStory);


                File input = new ClassPathResource("templates/screen2.html").getFile();
                String html = Jsoup.parse(input, "UTF-8", "").toString();

                MessageToClient messageC = new MessageToClient(
                        MessagePhase.SEND, Database.getUsernames(room), null, true, Database.getUserStories(room));
                messagingTemplate.convertAndSend("/queue/" + room, messageC);


                //MessageToClient messageC = new MessageToClient(MessagePhase.WAIT, null, html, false);

                // update client in room
               // messagingTemplate.convertAndSendToUser(sessionId, "/queue/" + room, messageC, createHeaders(sessionId));

                // reduce counter of amount of user who haven't send
             //   allSend(Database.reduceCounter(room), room);
                break;
            // TODO: other cases
        }
    }

    public void allSend(int c, int room){
        if (c == 0) {
            mergeStories(room);
        }
    }

    public void mergeStories(int room) {
        // update all clients in room
        MessageToClient messageC = new MessageToClient(
                MessagePhase.MERGE, Database.getUserStories(room), null, false, Database.getUserStories(room));
        messagingTemplate.convertAndSend("/queue/" + room, messageC);
    }

    @MessageMapping("/room/{roomId}/forceSend")
    private void forceSend(@DestinationVariable String roomId, @Payload MessageToServer message,
                           SimpMessageHeaderAccessor headerAccessor) {
        int room = message.getRoomId();

        // update all clients in room
        MessageToClient messageC = new MessageToClient(
                MessagePhase.FORCE_SEND, null, null, false, Database.getUserStories(room));
        messagingTemplate.convertAndSend("/queue/" + room, messageC);
    }

    private MessageHeaders createHeaders(String sessionId) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor
                .create(SimpMessageType.MESSAGE);
        headerAccessor.setSessionId(sessionId);
        headerAccessor.setLeaveMutable(true);
        return headerAccessor.getMessageHeaders();

    }
}
