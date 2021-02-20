'use strict';

let nameInput = $('#name');
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

    joinRoom(roomId);
}

// joins a room (switches page with new url)
function joinRoom(roomId) {
    if (roomId) {
        username = nameInput.val();
        admin = true;

        // store variables for next page (there is other ways to store variables, eg cookies)
        sessionStorage.setItem("roomId", roomId);
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("admin", admin)

        // switch page
        window.location.href = `/room/${roomId}`;
    }
}

$(document).ready(function () {
    createRoomButton.click(createRoom);
    joinRoomButton.click(() => {
        joinRoom(roomInput.val());
    });
});