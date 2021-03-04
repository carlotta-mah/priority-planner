package de.projekt.priorityplanner.controller;

import de.projekt.priorityplanner.Database;
import de.projekt.priorityplanner.model.MessagePhase;
import de.projekt.priorityplanner.model.MessageToClient;
import org.slf4j.Logger;
import org.springframework.stereotype.Component;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.List;
/**
 * This Event Listener registers when a user leaves the room
*/
@Component
public class WebSocketEventListener {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);

    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        logger.info("Received a new web socket connection.");
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());

        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Integer roomId = (Integer) headerAccessor.getSessionAttributes().get("room_id");
        if ((username != null) && (roomId != null)) {
            logger.info("User Disconnected: " + username);

            List<String> users= Database.removeUser(roomId, username);
            MessageToClient message = new MessageToClient(MessagePhase.LEAVE, users, null, false, null);
            messagingTemplate.convertAndSend("/queue/" + roomId, message);

        }
    }
}