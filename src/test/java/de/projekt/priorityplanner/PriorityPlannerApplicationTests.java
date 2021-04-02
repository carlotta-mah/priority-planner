package de.projekt.priorityplanner;

import de.projekt.priorityplanner.controller.RoomController;
import de.projekt.priorityplanner.model.*;
import de.projekt.priorityplanner.model.entity.Feature;
import de.projekt.priorityplanner.model.entity.Outcome;
import de.projekt.priorityplanner.model.entity.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriorityPlannerApplicationTests {

    @BeforeEach
    void resetDB() {
        Database.n = 0;
        Database.rooms1 = new ConcurrentHashMap();
        Database.counters = new ConcurrentHashMap();
    }
    @Autowired
    private RoomController controller;


    @Test
    void contextLoads() {
    }

    @Test
    void parallelDatabaseCreateRoom() {
        Database.n = 0;
        IntStream.range(0, 1000)
                .forEach(count ->
                        Database.addRoom("" + count, "")
                );
        assertEquals(999, Database.n);
    }

    @Test
    void parallelAddUsers() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        IntStream.range(0, 1000)
                .forEach(count -> Database.addUser(0
                        , "xy" + count, "Entwickler"));
        assertEquals(1000, Database.getRoom(0
        ).getUsers().size());
        IntStream.range(0, 1000)
                .forEach(count -> Database.removeUser(0
                        , "xy" + count));
        assertFalse(Database.containsRoom(0));
    }

    @Test
    void addAndRemoveUsers() {
        Database.addRoom("ROOM", "");
        Database.addUser(0, "xy", "Entwickler");
        List<String> names = Database.getUsernames(0);
        assertTrue(names.contains("xy"));
        assertEquals(1, Database.getRoom(0).getUsers().size());
        Database.removeUser(0, "xy");
        assertFalse(Database.containsRoom(0));
    }

    @Test
    void nameGenTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        assertFalse(Database.generateUniqueName("xy", 0
        ).equals("xy"));
    }

    @Test
    void createFeatures() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
        Database.addFeature(0
                , f);
        assertEquals(1
                , Database.getRoom(0
                ).getFeatures().size());
        assertTrue(Database.getRoom(0
        ).getFeatures().contains(f));
        Database.deleteFeature(0
                , f.getId());
        assertEquals(0, Database.getRoom(0
        ).getFeatures().size());
    }

    @Test
    @DisplayName("voteTest adding votes")
    void voteTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0, "xy", "Entwickler");
        Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
        Database.addFeature(0
                , f);
        Database.addVote(new Vote("xy", 0
                        , 0
                        , 0
                        , MessagePhase.VOTE, "Entwickler"), 0
                , f.getId());
        assertEquals(1
                , Database.getRoom(0
                ).getFeatureById(f.getId()).getVotes().size());
    }

    @Test
    void createAndDeleteRoom() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0, "xy", "Entwickler");
        assertTrue(Database.containsRoom(0));
        Database.removeUser(0, "xy");
        assertFalse(Database.containsRoom(0));
        Database.addRoom("ROOM2", "");
        Database.containsRoom(1);
    }

    @Test
    void resultTest() {
        Database.n = 0;
        int roomId = Database.addRoom("ROOM", "");
        Database.addUser(0, "xy", "Entwickler");
        Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
        Database.addFeature(0
                , f);
        Database.addVote(new Vote("xy", 0
                        , 0
                        , 0
                        , MessagePhase.VOTE, "Entwickler"), 0
                , f.getId());
        Outcome ergebnis = new Outcome(Database.getRoom(roomId));
        assertFalse(ergebnis.getWontHave().isEmpty());

    }

    @Test
    void createRoomInController(){
        Database.n = 0;
        controller.createRoom("produkt", "1234");
        assertTrue(Database.containsRoom(0));
    }


    @Test
    void addFeatureInController(){
        Database.n = 0;

        controller.createRoom("produkt", "1234");
        assertTrue(Database.containsRoom(0));
        controller.addFeature(0, new Feature("test", "", MessagePhase.FEATURE), SimpMessageHeaderAccessor.create());
        assertEquals(1
                , Database.getRoom(0
                ).getFeatures().size());
    }


    @Test
    void deleteFeaturesTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
        Database.addFeature(0, f);
        Database.deleteFeature(0, f.getId());

        assertTrue( Database.getRoom(0
        ).getFeatures().isEmpty());
    }

    @Test
    void MustHaveListTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature mustHaveFeature = new Feature("Test", "Test", MessagePhase.FEATURE);

        Vote v = new Vote("xy",100,100,3, MessagePhase.VOTE, "Entwickler");
        mustHaveFeature.addVote(v);
        mustHaveFeature.calculateResult();

        Database.addFeature(0,mustHaveFeature);
        assertEquals(mustHaveFeature, new Outcome(Database.getRoom(0)).getMustHave().get(0));
    }

    @Test
    void shouldHaveListTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature shouldHaveFeature = new Feature("Test", "Test", MessagePhase.FEATURE);

        Vote v = new Vote("xy",60,60,3, MessagePhase.VOTE, "Entwickler");
        shouldHaveFeature.addVote(v);
        shouldHaveFeature.calculateResult();

        Database.addFeature(0,shouldHaveFeature);
        assertEquals(shouldHaveFeature, new Outcome(Database.getRoom(0)).getShouldHave().get(0));
    }

    @Test
    void couldHaveListTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature couldHaveFeature = new Feature("Test", "Test", MessagePhase.FEATURE);

        Vote v = new Vote("xy",100,0,3, MessagePhase.VOTE, "Entwickler");
        couldHaveFeature.addVote(v);
        couldHaveFeature.calculateResult();

        Database.addFeature(0,couldHaveFeature);
        assertEquals(couldHaveFeature, new Outcome(Database.getRoom(0)).getCouldHave().get(0));
    }

    @Test
    void wontHaveListTest() {
        Database.n = 0;
        Database.addRoom("ROOM", "");
        Database.addUser(0
                , "xy", "Entwickler");
        Feature wontHaveFeature = new Feature("Test", "Test", MessagePhase.FEATURE);

        Vote v = new Vote("xy",0,0,3, MessagePhase.VOTE, "Entwickler");
        wontHaveFeature.addVote(v);
        wontHaveFeature.calculateResult();

        Database.addFeature(0,wontHaveFeature);
        assertEquals(wontHaveFeature, new Outcome(Database.getRoom(0)).getWontHave().get(0));
    }

}