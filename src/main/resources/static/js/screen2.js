let pageContainer = $('#pageContainer');
let userStoryInput = $('#userStoryInput');
let userStoryBoard = $('#userStoryBoard');
let usernames = $('#usernames');
let sendUserStoriesButton = $('#sendUserStoriesButton');
let addButton = $('#addToVoteButton');
let forceSendButton = $('#forceSendButton');

let zuBewertung;
let username;
let roomId;
let topic;
let currentSubscription;
let stompClient;
let admin;
let send;
let userStories;

let bewertung = [];

// connect via websocket with server for bidirectional communication
function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

// subscribe to websocket channel corresponding to roomId to receive messages from server
function registerInRoom() {
    // TODO: popup window or something to set a username in case user didn't come from start window

    currentSubscription = stompClient.subscribe(`/user/queue/${roomId}`, onMessageReceived);
    currentSubscription = stompClient.subscribe(`/queue/${roomId}`, onMessageReceived);

    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({username: username, roomId: roomId})
    );

    // TODO: receive everything and show, meanwhile hide everything
}

// called when websocket connection is set up
function onConnected() {
    registerInRoom()
}

// called when websocket connection failed
function onError() {
    // TODO: something like go back to main page or show error page
    window.location.href = "";
}

// called when server calls
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    switch (message.event) {
        case 'ADDED_USER':
            updateUsernames(message.usernames);
            //admin = message.admin; // TODO: doesn't work? admin still undefined.
            admin = (sessionStorage.getItem("admin"));
            if(admin == "true") {
                forceSendButton.css('visibility', 'visible');
            }
            break;
        case 'SEND':
            updateUsernames(message.usernames);
            //admin = message.admin; // TODO: doesn't work? admin still undefined.
            admin = (sessionStorage.getItem("admin"));
            if(admin == "true") {
                forceSendButton.css('visibility', 'visible');
            }


            fillBoard(message.userStories);
            break;
//        case 'WAIT':
  //          updateHTML(message.html);
    //        break;
        case 'FORCE_SEND':
            if(!send) {
                sendUserStories();
            }
            break;
        case 'MERGE':
            userStories = message.usernames;
            updateHTML(null);
            fillBoard(userStories);
            break;


        // TODO: other cases
    }
}
/*
function addUserStory() {
    let userStory = userStoryInput.val().trim();

    if (userStory) {
        userStoryBoard.append(`<p>${userStory}</p>`);
        userStoryInput.value = '';
        userStoryInput.text = '';
    }


}
*/
function sendUserStories() {
    let allUserStories

        let name = document.getElementById("userStoryInput").value;
        let beschreibung = document.getElementById("beschreibung").value;
        let bewertung1 = document.querySelector("#bewertung1").value;
        let bewertung2 = document.querySelector("#bewertung2").value;
        let zeit = document.querySelector("#zeit").value;

        //allUserStories.push(this.textContent);
        allUserStories =  [name, beschreibung, bewertung1, bewertung2, zeit];

    if (stompClient) {
        let message = {
            username: username,
            userStories: allUserStories,
            roomId: roomId,
            phase: 'SEND'
        }

        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;
}


function addBewertung() {
    let bewertung

    let name = document.getElementById("featureBewertung").value;
    let beschreibung = document.getElementById("beschrebungBewertung").value;

    bewertung =  [name, beschreibung];

    if (stompClient) {
        let message = {
            zuBewertung: bewertung,
            roomId: roomId,
            phase: 'ADDVOTE'
        }

        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;
}

function forceSend() {
    let message = {
        username: username,
        userStories: null,
        roomId: roomId,
        phase: 'FORCE_SEND'
    }

    stompClient.send(`${topic}/forceSend`, {}, JSON.stringify(message));
}

// update usernames when new user has been added
function updateUsernames(users) {
    usernames.empty();
    users.forEach(
        username => usernames.append(`<div>${username}</div>`)
    )
}

// fill board after merge
function fillBoard(userStories) {
    userStoryBoard.empty();
    userStories.forEach(
        userStory => userStoryBoard.append(`<h4>${userStory.name}<\h4>`, `<p>${userStory.beschreibung}<\p>`)
    )
}

function listToObjList(userStoryList){
    let list = [];
    userStoryList.forEach(
        userStory => list.push(new UserStory(userStory.name, userStory.beschreibung, userStory.value1, userStory.value2, userStory.zeit))
    )
    return list;
}

function updateHTML(html) {
    pageContainer.empty();
    pageContainer.append(html);
}

$(document).ready(function () {

    // get session variables
    roomId = parseInt(sessionStorage.getItem("roomId"));
    username = sessionStorage.getItem("username");
    usernames.append(`<div>${username}</div>`);

    topic = `/app/room/${roomId}`; // /app

    sendUserStoriesButton.click(sendUserStories);
    addButton.click(addBewertung);
    forceSendButton.click(forceSend);

    //if user has created the room show force send button
    admin = (sessionStorage.getItem("admin"));
    if(admin == "true") {
        forceSendButton.css('visibility', 'visible');
    }
    connect();
});

function updateTextInput(val) {
    document.getElementById('bewertung1').value=val;
}

function updateTextInput2(val) {
    document.getElementById('bewertung2').value=val;
}
