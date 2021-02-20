package de.projekt.priorityplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import de.projekt.priorityplanner.Database;

// Controller for switching HTML Pages
@Controller
public class PageController {

    @RequestMapping
    public String indexPage() {
        return "index";
    }

    @RequestMapping("/room/{roomId}")
    public String roomPage(@PathVariable("roomId") int roomId) {
        // TODO: check in actual Database if roomId exists
        if(Database.containsRoom(roomId)) {
            return "screen2";
        }

        return "index";
    }
}
