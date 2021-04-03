package de.projekt.priorityplanner.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import de.projekt.priorityplanner.Database;
import java.util.LinkedList;
import java.util.List;

/**
 * Ein PageController reguliert die verschiedenen Html Seiten.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */

@Controller
public class PageController {

    /**
     * Erstellt ein Neuen Raum und gibt die RoomId zurück
     *
     * @param produktName Der Name des zu entwerfenden Produkt oder auch Name des Raums
     * @return Die RaumId
     */
    @ResponseBody
    @RequestMapping("/create-room")
    public int createRoom(@RequestHeader("produktName") String produktName, @RequestHeader("passwort") String pw) {
        return Database.addRoom(produktName, pw);
    }
    /**
     * Gib die Index Seite bzw. Startseite als Html String zurück.
     *
     * @return index.html als String
     */
    @RequestMapping
    public String indexPage() {
        return "index";
    }

    /**
     * Gib die doc als Html String zurück. Bei anfragen an /docs.
     *
     * @return doc.html als String
     */
    @RequestMapping("/docs")
    public String docs() {
        return "doc";
    }

    /**
     * Gib die screen2 als Html String zurück. Bei anfragen an /room/roomId.
     * Screen2 representiert die Bewertrungs und Auswertungs Funktion
     *
     * @param roomId Die RaumId. Für die Raum zuordung.
     * @return screen2.html als String
     */
    @RequestMapping("/room/{roomId}")
    public String roomPage(@PathVariable("roomId") int roomId) {
        if(Database.containsRoom(roomId)) {
            return "screen2";
        }

        return "roomDoesNotExist";
    }

    /**
     * Generiert einen Eizigartigen Name für den angegebenen Raum.
     *
     * @param username Ist der von User eingegebene Name.
     * @param roomId Die RaumId. Für die Raum zuordung.
     * @return
     */
    @RequestMapping("/test/room/{roomId}" )
    @ResponseBody
    public List userName(@RequestHeader("username") String username, @RequestHeader("roomId") int roomId,
    @RequestHeader("passwort") String pw){
        //int i = Integer.parseInt(roomId);
        List<String> list = new LinkedList<>();
        String s = "";
        if(Database.containsRoom(roomId)) {
            s = Database.generateUniqueName(username, roomId);
        }
        list.add(s);
        String raumName = Database.getRoom(roomId).getRoomName();
        list.add(raumName);

        String passwort = Database.getRoom(roomId).getPasswort();
        String passwortTrue;
        if (passwort.equals(pw)||passwort.equals("")){
            passwortTrue = "true";
        } else{
            passwortTrue = "false";
        }
        list.add(passwortTrue);

        return list;
    }

}
