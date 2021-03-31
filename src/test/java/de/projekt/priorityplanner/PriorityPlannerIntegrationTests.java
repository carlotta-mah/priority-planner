package de.projekt.priorityplanner;

import de.projekt.priorityplanner.controller.PageController;
import de.projekt.priorityplanner.controller.RoomController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;

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
//    @Test
//    public void createRoom() throws Exception {
//        assertThat(this.restTemplate.getForObject("http://localhost:" + port + "/" + "create-room/",
//                String.class)).contains("Create a new Room");
//    }

}
