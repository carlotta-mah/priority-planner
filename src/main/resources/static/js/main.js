'use strict';

let produktInput = $('#produkt-name');
let nameInput = $('#joiner-name');
let roomInput = $('#room-id');
let createRoomButton = $('#createRoomButton');
let joinRoomButton = $('#joinRoomButton');


let admin = "false";
let roomName
let username;
let roomId;
let roll;

// creates a new room and joins it
async function createRoom() {
    /*
    roomId = await fetch('/create-room')
        .then(r => r.json())
        .catch()
    */
    roomName = produktInput.val();
    const Http = new XMLHttpRequest();
    const url = '/create-room/';

    Http.open("GET", url);
    Http.setRequestHeader("produktName", roomName);
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

// joins a room (switches page with new url)
function joinRoom(roomId) {
    if (roomId) {
        username = nameInput.val();
        roomName = produktInput.val();
        roll = document.getElementById("create-rollen").value;

        if(username.isEmpty()){
            username = "User";
        }

        // store variables for next page (there is other ways to store variables, eg cookies)
        sessionStorage.setItem("roomName", roomName);
        sessionStorage.setItem("roomId", roomId);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("roll", roll);
        sessionStorage.setItem("admin", admin);

        // switch page
        window.location.href = `/room/${roomId}`;
    }
}

function joinExistingRoom2(roomId){
    if(roomId) {
        username = nameInput.val();
        roll = document.getElementById("joiner-rollen").value;

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
        Http.send();


        Http.onreadystatechange = (e) => {
            if (Http.readyState == XMLHttpRequest.DONE) {
                console.log(Http.responseText)
                let request =JSON.parse( Http.responseText);
                username = request[0];
                sessionStorage.setItem("username", username);
                roomName = request[1];
                sessionStorage.setItem("roomName",roomName);
                window.location.href = `/room/${roomId}`;
            }
        }
    }
}
String.prototype.isEmpty = function() {
    return (this.length === 0 || !this.trim());
};

//aktiviert die Enter taste zum betreten des Raumes
function creatRoomByEnter(){
    if(event.key === 'Enter') {
        createRoom();
    }
}
function joinRoomByEnter(idInTest){
    if(event.key === 'Enter') {
        joinExistingRoom2(idInTest.value);
    }
}
$(document).ready(function () {
    createRoomButton.click(createRoom);
    joinRoomButton.click(() => {
        joinExistingRoom2(roomInput.val());
    });
});