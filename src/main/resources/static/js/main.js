'use strict';

let nameInput = $('#joiner-name');
let roomInput = $('#room-id');
let createRoomButton = $('#createRoomButton');
let joinRoomButton = $('#joinRoomButton');

let admin = "false";
let username;
let roomId;

// creates a new room and joins it
async function createRoom() {
    roomId = await fetch('/create-room')
        .then(r => r.json())
        .catch()
    admin = true;
    nameInput = $('#creator-name');
    joinRoom(roomId);
}

// joins a room (switches page with new url)
function joinRoom(roomId) {
    if (roomId) {
        username = nameInput.val();

        // store variables for next page (there is other ways to store variables, eg cookies)
        sessionStorage.setItem("roomId", roomId);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("admin", admin)

        // switch page
        window.location.href = `/room/${roomId}`;
    }
}

function joinExistingRoom2(roomId){
    username = nameInput.val();

    // store variables for next page (there is other ways to store variables, eg cookies)
    sessionStorage.setItem("roomId", roomId);
    sessionStorage.setItem("admin", admin)

    const Http = new XMLHttpRequest();
    const url= '/test/room/'+ roomId;

    Http.open("GET", url);
    Http.setRequestHeader("username", username);
    Http.setRequestHeader("roomId", roomId);
    Http.send();


    Http.onreadystatechange = (e) => {
        if (Http.readyState == XMLHttpRequest.DONE) {
            username = Http.responseText;
            sessionStorage.setItem("username", username);
            window.location.href = `/room/${roomId}`;
        }
    }

}

$(document).ready(function () {
    createRoomButton.click(createRoom);
    joinRoomButton.click(() => {
        joinExistingRoom2(roomInput.val());
    });
});