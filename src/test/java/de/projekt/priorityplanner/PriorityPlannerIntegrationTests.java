package de.projekt.priorityplanner;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.projekt.priorityplanner.controller.PageController;
import de.projekt.priorityplanner.controller.RoomController;
import de.projekt.priorityplanner.model.MessagePhase;
import de.projekt.priorityplanner.model.entity.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import java.util.logging.Logger;

import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class PriorityPlannerIntegrationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PageController pageController;

    @Autowired
    private RoomController controller;
    private String URL;

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private BlockingQueue<String> blockingQueue;
    private BlockingQueue<Outcome> blockingQueueO;

    private WebSocketStompClient webSocketStompClient;

    @Test
    public void contextLoads() throws Exception {

        assertThat(pageController).isNotNull();
        assertThat(controller).isNotNull();
    }

    @Test
    public void contentLoadIndex() throws Exception {
        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/",
                String.class)).contains("Create a new Room");
    }

    @BeforeEach
    public void setup() {
        URL = "ws://localhost:" + port + "/ws";
        this.blockingQueue = new LinkedBlockingDeque<>();
        this.blockingQueueO = new LinkedBlockingDeque<>();
        this.webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                asList(new WebSocketTransport(new StandardWebSocketClient()))));
        List<Transport> transports = new ArrayList<>(2);
        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
        WebSocketClient client = new SockJsClient(transports);
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        Database.n = 0;
        Database.rooms1 = new ConcurrentHashMap();
        Database.counters = new ConcurrentHashMap();

    }


    @Bean
    public MappingJackson2MessageConverter mappingJackson2MessageConverter(ObjectMapper objectMapper) {
        MappingJackson2MessageConverter jacksonMessageConverter = new MappingJackson2MessageConverter();
        jacksonMessageConverter.setObjectMapper(objectMapper);
        jacksonMessageConverter.setSerializedPayloadClass(String.class);
        jacksonMessageConverter.setStrictContentTypeMatch(true);
        return jacksonMessageConverter;
    }

    @Test
    public void websocketConnection() throws Exception {
        Database.n = 0;
        Database.addRoom("test", "1234");
        int roomId = 0;
        StompSession session = webSocketStompClient
                .connect(URL, new StompSessionHandlerAdapter() {
                })
                .get(1, SECONDS);

        session.subscribe("/user/queue/" + roomId, new DefaultStompFrameHandler());
        session.subscribe("/queue/" + roomId, new DefaultStompFrameHandler());
        session.subscribe("/queue/feature/" + roomId, new DefaultStompFrameHandler());
//        session.subscribe("/queue/ergebnis/" + roomId, new DefaultStompFrameHandler());

    }

    @Test
    public void ergebnisReturnTest() throws Exception {
        int roomId = 0;
        Feature f = new Feature("test", "", MessagePhase.FEATURE);
        Database.addRoom("hi", "");
        Database.addFeature(roomId, f);
        Database.addUser(0, "User", "Entwickler");
        Database.addVote(new Vote("User", 50, 50, 2, MessagePhase.VOTE, "Entwickler"), 0, f.getId());
        StompSession session = webSocketStompClient
                .connect(URL, new StompSessionHandlerAdapter() {
                    @Override
                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
                        session.subscribe("/queue/ergebnis/" + roomId, this);
                        session.send("/app/queue/ergebnis/" + roomId, null);
                        }

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return String.class;

                    }

                    @Override
                    public void handleFrame(StompHeaders stompHeaders, Object o) {
                        System.out.println("Received message: " + o);
                        blockingQueueO.add((Outcome) o);
                    }
                }).get(4, SECONDS);


        Thread.sleep(1000);
        session.send("/app/room/"+roomId +"/ergebnis",null);
        Thread.sleep(1000);

}


class DefaultStompFrameHandler implements StompFrameHandler {
    @Override
    public Type getPayloadType(StompHeaders stompHeaders) {
        return Outcome.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
        logger.info("Received:" + payload.toString());
        blockingQueue.add((String) payload);
        blockingQueueO.add((Outcome) payload);
    }

}


}
