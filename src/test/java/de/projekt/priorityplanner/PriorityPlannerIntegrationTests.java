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
        session.subscribe("/queue/ergebnis/" + roomId, new DefaultStompFrameHandler());

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
                }).get(4, SECONDS);
        session.subscribe("/queue/ergebnis/" + roomId, new StompFrameHandler() {
            @Override
            public Type getPayloadType(StompHeaders headers) {
                return Outcome.class;
            }

            @Override
            public void handleFrame(StompHeaders stompHeaders, Object o) {
                System.out.println("Received message: " + o);
                blockingQueueO.add((Outcome) o);
            }
        });

        Thread.sleep(1000);
        session.send("/app/room/"+roomId +"/ergebnis",null);
        Thread.sleep(1000);
    Outcome o = new Outcome(Database.getRoom(0));

//    assertEquals(o, blockingQueueO.poll(5, SECONDS));
}

//    @Test
//    public void addUserIntegrationTest() throws Exception {
//        Database.n = 0;
//        Database.addRoom("test", "1234");
//        int roomId = 0;
//        JSONObject jo = new JSONObject();
//        jo.put("username", "User");
//        jo.put("roomId", 0);
//        jo.put("roll", "Entwickler");
//        String usermsg = jo.toString();
//        StompSession session = webSocketStompClient
//                .connect(URL, new StompSessionHandlerAdapter() {
//                    @Override
//                    public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
//                        logger.info("New session established : " + session.getSessionId());
//                        session.subscribe("/queue/" + roomId, new StompFrameHandler() {
//                            @Override
//                            public Type getPayloadType(StompHeaders headers) {
//                                logger.info("payload is asked"+headers.toString());
//                                return MessageToServer.class;
//                            }
//                            @Override
//                            public void handleFrame(StompHeaders stompHeaders, Object o) {
////            blockingQueue.offer(new String((byte[]) o));
//                                System.out.println("Received message: " + o);
//                                blockingQueue.add((String) o);
//                            }
//                        });
//                    }
//                })
//                .get(4, SECONDS);
//        logger.info("New session established : " + session.getSessionId());
//        StompHeaders stmph = new StompHeaders();
//        session.subscribe("/queue/" + roomId, new StompFrameHandler() {
//            @Override
//            public Type getPayloadType(StompHeaders headers) {
//                logger.info("payload is asked"+headers.toString());
//                return MessageToServer.class;
//            }
//            @Override
//            public void handleFrame(StompHeaders stompHeaders, Object o) {
////            blockingQueue.offer(new String((byte[]) o));
//                System.out.println("Received message: " + o);
//                blockingQueue.add((String) o);
//            }
//        });
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//        String jsonString = mapper.writeValueAsString(jo);
//        logger.info("Warum passiert dsa?");
//        stmph.setDestination("/app/room/" + roomId + "/addUser");
//        logger.info("Warum passiert dsa2?");
//        session.send("/app/room/" + roomId + "/ergebnis", null);
////        session.send("/room/" + roomId + "/addUser", jsonString);
////        session.send("/app/room/" + roomId + "/addUser", usermsg);
//        logger.info("Warum passiert dsa3?");
////        if (!latch.await(1, TimeUnit.SECONDS)) {
////            fail("Message not received");
////        }
////        Thread.sleep(6000);
//////
//////        session.subscribe("/user/queue/"+roomId, new DefaultStompFrameHandler());
////        session.subscribe("/queue/" + roomId, new DefaultStompFrameHandler());
////        StompHeaders stmph = new StompHeaders();
////        stmph.setDestination("/app/room/" + roomId + "/ergebnis");
////        session.send(stmph, usermsg);
////        session.subscribe("/queue/feature/"+roomId, new DefaultStompFrameHandler());
//////        session.subscribe("/queue/ergebnis/"+roomId, new DefaultStompFrameHandler());
//        MessageToServer joMsg = new MessageToServer("User", 0, "Entwickler");
//////        jo.put("featureId", "0");
//////        jo.put("content", "[]");
//////        jo.put("phase", "VOTE");
////        Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
////        logger.info(jo.toString());
//////        ObjectMapper mapper = new ObjectMapper();
//////        mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
//////        mapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
//////        String jsonString = mapper.writeValueAsString(jo);
//////        logger.info(jsonString);
////
////
////        //        MessageToServer msgToServer = new MessageToServer("username", 0, 0, "Entwickler", new LinkedList<>(), MessagePhase.ADDED_USER);
//////        JSONObject obj = new JSONObject(msgToServer);
////        stmph.setContentType(MediaType.APPLICATION_JSON);
////        stmph.setContentLength(usermsg.length());
//////        session.send(URL+"/addUser", usermsg.getBytes()); usermsg.getBytes()
//////        session.send("/app/room/" +roomId +"/addUser", jo);
////////        "{'username':'username','roomId':0,'roll':'Entwickler'}"
//////        Message<byte[]> builmessage = MessageBuilder.withPayload(jsonString.getBytes("UTF-8")).build();
//////        Message<String> buildUsermsg = MessageBuilder.withPayload(usermsg).build();
//////        logger.info(buildUsermsg.toString());
//////        messagingTemplate.send("/app/room/" + roomId + "/addUser", buildUsermsg);
//////        MvcResult result = mvc.perform(get("/addUserr")
//////                .contentType(MediaType.APPLICATION_JSON)
//////                .content(jsonString)
//////                .characterEncoding("utf-8"))
//////                .andExpect(status().isOk())
//////                .andReturn();
//////        String message = "MESSAGE TEST";
//////        session.send(URL+"/addUser", message.getBytes());
//////        Assert.assertEquals("User", blockingQueue.poll(1, SECONDS));
////
////        List<Transport> transports = new ArrayList<>(2);
////        transports.add(new WebSocketTransport(new StandardWebSocketClient()));
////        transports.add(new RestTemplateXhrTransport());
////        WebSocketClient client = new SockJsClient(transports);
//////        WebSocketClient client = new StandardWebSocketClient();
////        WebSocketStompClient stompClient = new WebSocketStompClient(client);
////        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
////
////
////        StompSession session = stompClient.connect(URL, new StompSessionHandlerAdapter(){
////            @Override
////            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
////                session.subscribe("/queue/", this);
////                session.send(stmph, usermsg);
////                session.subscribe("/queue/feature/"+roomId, this);
////                session.subscribe("/queue/ergebnis/"+roomId, this);
////                session.send("/app/room/" + roomId + "/addUser", usermsg);
////            }
////            @Override
////            public Type getPayloadType(StompHeaders headers) {
//////                return String.class;
//////                return byte[].class;
//////                return MessageToServer.class;
////                return JSONObject.class;
////            }
////
////            @Override
////            public void handleFrame(StompHeaders headers, Object payload) {
////                Message msg = (Message) payload;
////                logger.info("Received : " + msg.getPayload() + " from : " + msg.getHeaders());
////            }
////            }).get(1, SECONDS);
////        session.send(stmph, usermsg);
////                new StompSessionHandler() {
////            @Override
////            public void afterConnected(StompSession session, StompHeaders connectedHeaders) {
////                session.subscribe("/queue/", this);
////                session.send("/app/room/" + roomId + "/addUser", usermsg.getBytes());
////            }
////
////            @Override
////            public void handleException(StompSession session, StompCommand command, StompHeaders headers, byte[] payload, Throwable exception) {
////            }
////
////            @Override
////            public void handleTransportError(StompSession session, Throwable exception) {
////
////            }
////
////            @Override
////            public Type getPayloadType(StompHeaders headers) {
//////                return String.class;
////                return byte[].class;
////            }
//
////            @Override
////            public void handleFrame(StompHeaders headers, Object payload) {
////                Message msg = (Message) payload;
////                logger.info("Received : " + msg.getPayload() + " from : " + msg.getHeaders());
////            }
////        }).get(1, SECONDS);;
//
////        new Scanner(System.in).nextLine();
////
////        List<String> namelist = Database.getRoom(0).getOnlyUserNames();
////        Assert.assertTrue(Database.containsRoom(0));
////        Assert.assertTrue(Database.getRoom(0).
////                getOnlyUserNames().
////                contains("User"));
//    }
//
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
