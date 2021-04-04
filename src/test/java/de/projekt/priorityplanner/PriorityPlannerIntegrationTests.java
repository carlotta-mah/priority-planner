package de.projekt.priorityplanner;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.projekt.priorityplanner.controller.PageController;
import de.projekt.priorityplanner.controller.RoomController;
import de.projekt.priorityplanner.model.MessagePhase;
import de.projekt.priorityplanner.model.MessageToServer;
import de.projekt.priorityplanner.model.entity.*;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;


//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.SimpleMessageConverter;
import org.springframework.messaging.converter.StringMessageConverter;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.socket.client.WebSocketClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.RestTemplateXhrTransport;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;

import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;

import java.util.logging.Logger;

import static java.net.Proxy.Type.HTTP;
import static java.util.Arrays.asList;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.setMaxLengthForSingleLineDescription;
import static org.assertj.core.api.Fail.fail;
import static org.junit.Assert.assertEquals;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.get;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
    static final String WEBSOCKET_TOPIC = "/topic";
    static final String WEBSOCKET_QUEUE = "/queue";
    static final String WEBSOCKET_USER = "/user";
    static final String CREATE = "/create-room/";

    private Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    private BlockingQueue<String> blockingQueue;
    private BlockingQueue<Outcome> blockingQueueO;

    private WebSocketStompClient webSocketStompClient;
    private WebSocketStompClient stompClient;

    private MockMvc mvc;
    CountDownLatch latch = new CountDownLatch(1);
    @Autowired
    private SimpMessageSendingOperations messagingTemplate;

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
//        transports.add(new RestTemplateXhrTransport());
        WebSocketClient client = new SockJsClient(transports);
//        stompClient = new WebSocketStompClient(client);
//        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
//        webSocketStompClient.setMessageConverter(new StringMessageConverter());
//        webSocketStompClient.setMessageConverter(new SimpleMessageConverter());
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
                        logger.info("New session established inside: " + session.getSessionId());
                        session.subscribe("/queue/ergebnis/" + roomId, this);
                        logger.info("now sending request");
                        session.send("/app/queue/ergebnis/" + roomId, null);
                        }

                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        logger.info("Type: String");
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
//            return byte[].class;
//            return String.class;
//            return JSONObject.class;

        logger.info("Payload type:" + Outcome.class.toString());
        return Outcome.class;
    }

    @Override
    public void handleFrame(StompHeaders stompHeaders, Object payload) {
//            blockingQueue.offer(new String((byte[]) o));
//            System.out.println("Received message: " + o);
        logger.info("Received:" + payload.toString());
        blockingQueue.add((String) payload);
        blockingQueueO.add((Outcome) payload);
    }

}


}
