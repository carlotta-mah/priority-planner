package de.projekt.priorityplanner.controller;

import org.springframework.stereotype.Component;

@Component
public class WebSocketEventListener {

//    private static final Logger logger = LoggerFactory.getLogger(WebSocketEventListener.class);
//
//    @Autowired
//    private SimpMessageSendingOperations messagingTemplate;
//
//    @EventListener
//    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
//        logger.info("Received a new web socket connection.");
//    }
//
//    @EventListener
//    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
//        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
//
//        String username = (String) headerAccessor.getSessionAttributes().get("username");
//        String roomId = (String) headerAccessor.getSessionAttributes().get("room_id");
//        if (username != null) {
//            logger.info("User Disconnected: " + username);
//
//            ChatMessage chatMessage = new ChatMessage();
//            chatMessage.setType(MessageType.LEAVE);
//            chatMessage.setSender(username);
//
//            messagingTemplate.convertAndSend(format("/channel/%s", roomId), chatMessage);
//        }
//    }
}