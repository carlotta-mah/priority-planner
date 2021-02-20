let pageContainer = $('#pageContainer');
let userStoryInput = $('#userStoryInput');
let userStoryBoard = $('#userStoryBoard');
let usernames = $('#usernames');
let addUserStoryButton = $('#addUserStoryButton');
let sendUserStoriesButton = $('#sendUserStoriesButton');
let forceSendButton = $('#forceSendButton');

let username;
let roomId;
let topic;
let currentSubscription;
let stompClient;
let admin;
let send;
let userStories;

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
        case 'WAIT':
            updateHTML(message.html);
            break;
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

function addUserStory() {
    let userStory = userStoryInput.val().trim();

    if (userStory) {
        userStoryBoard.append(`<p>${userStory}</p>`);
        userStoryInput.value = '';
        userStoryInput.text = '';
    }
}

function sendUserStories() {
    let allUserStories = []

    userStoryBoard.children('p').each(function () {
        allUserStories.push(this.textContent);
    });

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
    userStories.forEach(
        userStory => pageContainer.append(`<p>${userStory}<\p>`)
    )
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

    addUserStoryButton.click(() => addUserStory(userStoryInput.val()));
    sendUserStoriesButton.click(sendUserStories);
    forceSendButton.click(forceSend);

    //if user has created the room show force send button
    admin = (sessionStorage.getItem("admin"));
    if(admin == "true") {
        forceSendButton.css('visibility', 'visible');
    }
    connect();
});