package de.projekt.priorityplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import de.projekt.priorityplanner.Database;

import java.util.LinkedList;
import java.util.List;

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

        return "roomDoesNotExist";
    }

    @RequestMapping("/test/room/{roomId}" )
    @ResponseBody
    public List userName(@RequestHeader("username") String username, @RequestHeader("roomId") int roomId){
        //int i = Integer.parseInt(roomId);
        List<String> list = new LinkedList<>();
        String s = "";
        if(Database.containsRoom(roomId)) {
            s = Database.generateUniqueName(username, roomId);
        }
        list.add(s);
        String raumName = Database.getRoom(roomId).getRoomName();
        list.add(raumName);

        return list;
    }

}
