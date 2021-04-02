package de.projekt.priorityplanner;

import de.projekt.priorityplanner.controller.PageController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.concurrent.ConcurrentHashMap;

@SpringBootTest
@AutoConfigureMockMvc
public class TestPageController {

    @Autowired
    private MockMvc mvc;


    @Test
    public void indexLoadTest() throws Exception {
        Database.n = 0;
        Database.rooms1 = new ConcurrentHashMap();
        Database.counters = new ConcurrentHashMap();
        this.mvc.perform(get("/")).andDo(print()).andExpect(status().isOk())
                .andExpect(content().string(containsString("Create a new Room")));

    }

    @Test
    public void createRoomTest() throws Exception {
        Database.n = 0;
        Database.rooms1 = new ConcurrentHashMap();
        Database.counters = new ConcurrentHashMap();
        this.mvc.perform(get("/create-room/").header("produktName" , "SOMETOKEN").header("passwort", "1234")).andDo(print())
                .andExpect(content().string(containsString("0")));
    }
}
