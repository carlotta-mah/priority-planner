'use strict';

//inizalisiert die Elemente der index.html
let produktInput = $('#produkt-name');
let nameInput = $('#joiner-name');
let roomInput = $('#room-id');
let createRoomButton = $('#createRoomButton');
let joinRoomButton = $('#joinRoomButton');


let passwortInput;
let passwortEnter;
let admin = "false";
let roomName
let username;
let roomId;
let roll;

/**
 * Erstellt ein Neuen Raum
 */
// creates a new room and joins it
async function createRoom() {

    roll = document.getElementById("create-rollen").value;
    if(roll == ""){
        alert("Please choose your Role");
        return false;
    }
    passwortInput = document.getElementById("setRoomPasswort").value;
    roomName = produktInput.val();
    const Http = new XMLHttpRequest();
    const url = window.location.href + '/create-room/';

    Http.open("GET", url);
    Http.setRequestHeader("produktName", roomName);
    Http.setRequestHeader("passwort", passwortInput);
    Http.send();

    Http.onreadystatechange = (e) => {
        if (Http.readyState == XMLHttpRequest.DONE) {
            roomId = Http.responseText;
            admin = true;
            nameInput = $('#creator-name');
            joinRoom(roomId);
        }
    }
}

/**
 * Tritt einem neuen Raum bei. Die Id wird übergeben.
 * Außerdem wird die Seiter zum screen2.html gewechselt
 *
 * @param roomId Die RaumID
 */
function joinRoom(roomId) {
    if (roomId) {
        username = nameInput.val();
        roomName = produktInput.val();
        roll = document.getElementById("create-rollen").value;

        if(username.isEmpty()){
            username = "User";
        }

        // Variablen werden im Storage gespeichert damit sie auf der nächsten seite zur verfügung stehen
        sessionStorage.setItem("passwort", passwortInput);
        sessionStorage.setItem("roomName", roomName);
        sessionStorage.setItem("roomId", roomId);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("roll", roll);
        sessionStorage.setItem("admin", admin);

        // switch page
        window.location.href = `/room/${roomId}`;
    }
}

/**
 * Tritt eine alten Raum bei
 *
 * @param roomId  Die RaumId
 */
function joinExistingRoom2(roomId){
    if(roomId) {
        username = nameInput.val();
        passwortEnter = document.getElementById("enterRoomPasswort").value;
        roll = document.getElementById("joiner-rollen").value;
        if(roll == ""){
            alert("Please choose your Role");
            return false;
        }
        if(username.isEmpty()){
            username = "User";
        }

        // store variables for next page (there is other ways to store variables, eg cookies)
        sessionStorage.setItem("roomId", roomId);
        sessionStorage.setItem("roll", roll);
        sessionStorage.setItem("admin", admin);

        const Http = new XMLHttpRequest();
        const url = '/test/room/' + roomId;

        Http.open("GET", url);
        Http.setRequestHeader("username", username);
        Http.setRequestHeader("roomId", roomId);
        Http.setRequestHeader("passwort", passwortEnter)
        Http.send();


        Http.onreadystatechange = (e) => {
            if (Http.readyState == XMLHttpRequest.DONE) {
                console.log(Http.responseText)
                let request =JSON.parse( Http.responseText);
                username = request[0];
                sessionStorage.setItem("username", username);
                roomName = request[1];
                sessionStorage.setItem("roomName",roomName);
                if(request[2] === "true"){
                    sessionStorage.setItem("passwort", passwortEnter);
                    window.location.href = `/room/${roomId}`;
                }else{
                    alert("The Password is wrong");
                }

            }
        }
    }
}

/**
 * Prüft ob das Eingabefeld für den Username leer ist.
 * @returns {boolean} Wenn leer dann true, sonst false
 */
String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

/**
 * aktiviert die Enter taste zum betreten des Raumes
 */
function creatRoomByEnter(){
    if(event.key === 'Enter') {
        createRoom();
    }
}
/**
 * aktiviert die Enter taste zum betreten des Raumes
 */
function joinRoomByEnter(idInTest){
    if(event.key === 'Enter') {
        joinExistingRoom2(idInTest.value);
    }
}

function joinRoomByEnterPw(){
    if(event.key === 'Enter') {
        joinExistingRoom2(document.getElementById("room-id").value);
    }
}
$(document).ready(function () {
    createRoomButton.click(createRoom);
    joinRoomButton.click(() => {
        joinExistingRoom2(roomInput.val());
    });
});