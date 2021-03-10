package de.projekt.priorityplanner;

import de.projekt.priorityplanner.model.Feature;
import de.projekt.priorityplanner.model.MessagePhase;
import de.projekt.priorityplanner.model.Vote;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PriorityPlannerApplicationTests {

	@Test
	void contextLoads() {
	}
	@Test
	void parallelDatabaseCreateRoom(){
		Database.n = 0;
		IntStream.range(0, 1000)
				.forEach(count ->
						Database.addRoom(""+ count)
				);
		assertEquals(1000, Database.n);
	}
	@Test
	void parallelAddUsers(){
		Database.n = 0;
		Database.addRoom("ROOM");
		IntStream.range(0, 1000)
				.forEach(count -> Database.addUser(1,"xy"+count, "Entwickler"));
		assertEquals(1000, Database.getRoom(1).getUsers().size());
		IntStream.range(0, 1000)
				.forEach(count -> Database.removeUser(1,"xy"+count));
		assertEquals(0, Database.getRoom(1).getUsers().size());
	}
	@Test
	void addAndRemoveUsers(){
		Database.n = 0;
		Database.addRoom("ROOM");
		Database.addUser(1,"xy", "Entwickler");
		List<String> names = Database.getUsernames(1);
		assertTrue(names.contains("xy"));
		assertEquals(1, Database.getRoom(1).getUsers().size());
		Database.removeUser(1, "xy");
		names = Database.getUsernames(1);
		assertFalse(names.contains("xy"));
		assertEquals(0, Database.getRoom(1).getUsers().size());
	}
	@Test
	void nameGenTest(){
		Database.n = 0;
		Database.addRoom("ROOM");
		Database.addUser(1,"xy", "Entwickler");
		assertFalse(Database.generateUniqueName("xy", 1).equals("xy"));
	}
	@Test
	void createFeatures(){
		Database.n = 0;
		Database.addRoom("ROOM");
		Database.addUser(1,"xy", "Entwickler");
		Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
		Database.addFeature(1, f);
		assertEquals(1, Database.getRoom(1).getFeatures().size());
		assertTrue(Database.getRoom(1).getFeatures().contains(f));
		Database.deleteFeature(1, f.getId() );
		assertEquals(0, Database.getRoom(1).getFeatures().size());
	}
	@Test
	@DisplayName("voteTest adding votes")
	void voteTest(){
		Database.n = 0;
		Database.addRoom("ROOM");
		Database.addUser(1,"xy", "Entwickler");
		Feature f = new Feature("Test", "Test", MessagePhase.FEATURE);
		Database.addFeature(1, f);
		Database.addVote(new Vote("xy",1,1,1, MessagePhase.VOTE, "Entwickler"), 1, f.getId());
		assertEquals(1, Database.getRoom(1).getFeatureById(f.getId()).getVotes().size());
	}

	@Test
	public void createRoom() throws Exception {

	}
//	@Test
//	void parallelRemoveUsers(){
//	}
}
