let pageContainer = $('#pageContainer');
let userStoryInput = $('#userStoryInput');
let userStoryBoard = $('#userStoryBoard');
let usernames = $('#usernames');
let sendUserStoriesButton = $('#sendUserStoriesButton');
let voteButton = $('#voteButton');
let addButton = $('#addToVoteButton');
let addFeatureButton = $('#addFeature');
let voteAgainButton = $('#voteAgain');

let username;
let roomId;
let topic;
let currentSubscription;
let stompClient;
let admin;
let send;
let userStories;
const userStoryBoardDiv = document.getElementById("userStoryBoard");
let idGenerator = 0;

let selectetFeature;

let boostList = [];
let ripList = [];
let timeList = [];


function setTitle() {
    let roomTitle = document.getElementById("rommtitle");
    let title = document.createElement("h1");
    title.innerHTML += "PriorityPlanner: ROOM "
    title.innerHTML += roomId + "<br />";
    title.innerHTML += "Name: ";
    title.innerHTML += username;
    roomTitle.appendChild(title);
}

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

function getId() {
    return idGenerator++;
}

function hideVotes() {
    let voteList = document.getElementsByClassName("test");
    Array.prototype.forEach.call(voteList, function(voteElement) {
        voteElement.style.display = "none";
    });

}

function resetVotingPanel() {
    //TODO:set sliders and Time to default
    let inputs = $(":input[type=range]");
    Array.prototype.forEach.call(inputs, function(input) {
        input.value = "50";
    });
    document.querySelector("#bewertung1").value = 50;
    document.querySelector("#bewertung2").value = 50;
    document.querySelector("#zeit").value = 0;
}

function unselectAllFeatures() {
    let featureList = document.getElementsByClassName("featureList");
    Array.prototype.forEach.call(featureList, function(featureElement) {
        featureElement.classList.remove("selected-feature");
    });
}

function addToBoard(userstory) {
    const newDiv = document.createElement("div");
    let name = userstory.name;

    let beschreibung = userstory.beschreibung;

    let myId = userstory.id;
    newDiv.id = myId;
    newDiv.classList.add('featureList');

    newDiv[0] = name;
    newDiv[1] = beschreibung;

    let nameH4 = document.createElement("h4");
    nameH4.innerHTML = name;
    let bescheibungP = document.createElement("p");
    bescheibungP.innerHTML = beschreibung;

    newDiv.appendChild(nameH4);
    newDiv.appendChild(bescheibungP);

    let bewertung = document.createElement("div");
    bewertung.id = "bewertung-" + myId;
    newDiv.appendChild(bewertung);

    var button = document.createElement("BUTTON");
    button.title = myId;
    button.classList.add('button');
    button.innerHTML = "Vote now";
    button.addEventListener("click", function () {
        let divName = document.getElementById(this.title)[0];
        let divBeschreibung = document.getElementById(this.title)[1];
        hideVotes();
        resetVotingPanel();
        document.getElementById("featureBewertung").value = divName;
        document.getElementById("beschrebungBewertung").value = divBeschreibung;
        unselectAllFeatures();
        newDiv.classList.add("selected-feature");
        addBewertung(userstory);
    });
    newDiv.appendChild(button);


    userStoryBoardDiv.appendChild(newDiv);
    //userStoryBoard.append(`<h4>${userstory.name}<\h4>`, `<p>${userstory.beschreibung}<\p>`)
}

function updateFeatures(userStories) {
    userStoryBoard.empty();
    userStories.forEach(
        story => addToBoard(new UserStory(story.title, story.description, 0, 0, 0))
    )
}


function updateUserVote(user, bewertung1, bewertung2, zeit) {
    document.getElementById(user).classList.add('hasVoted');
}

function updateVote(vote) {
    let userdiv = document.getElementById(vote.user);
    let votediv =userdiv.childNodes[1];
    votediv.setAttribute("class", "test");
    votediv.innerText = "Boost:"+vote.bewertung1 +
                        " RIP:"+vote.bewertung2 + " Zeit:" + vote.zeit;

 //   userdiv.append(bewertungsP)
    userdiv.classList.add("hasVoted")
}

function updateVotes(votes) {

    votes.forEach(
        vote => {
            updateVote(vote);
        }
    )


}

function showVotes() {
    let voteList = document.getElementsByClassName("test");
    Array.prototype.forEach.call(voteList, function(voteElement) {
        voteElement.style.display = "inline-block";
    });



}

// called when server calls
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    switch (message.event) {
        case 'ADDED_USER':
            updateUsernames(message.users);
            updateFeatures(message.features);
            if (message.activeFeature != null) {
                document.getElementById('featureBewertung').value = message.activeFeature.title;
                document.getElementById('beschrebungBewertung').value = message.activeFeature.description;
                updateVotes(message.activeFeature.votes);
                enableButton();
            }


            //updateUsernames(message.usernames);
            //updateFeatures(message.userStories);
            //admin = message.admin; // TODO: doesn't work? admin still undefined.
            admin = (sessionStorage.getItem("admin"));
            // if(admin == "true") {
            //     forceSendButton.css('visibility', 'visible');
            // }
            break;
        case 'FEATURE':
            let userstory = new UserStory(message.title, message.description, message.id);
            addToBoard(userstory);
            break;
        case 'UPDATE':
            updateFeatures(message.userStories);
            break;
        case 'SEND':
            updateUsernames(message.usernames);
            //admin = message.admin; // TODO: doesn't work? admin still undefined.
            admin = (sessionStorage.getItem("admin"));
            /* if(admin == "true") {
                 forceSendButton.css('visibility', 'visible');
             }*/


            fillBoard(message.userStories);
            break;
//        case 'WAIT':
        //          updateHTML(message.html);
        //        break;
        case 'FORCE_SEND':
            if (!send) {
                sendUserStories();
            }
            break;
        case 'MERGE':
            userStories = message.usernames;
            updateHTML(null);
            fillBoard(userStories);
            break;

        case 'ADDVOTE':
            //TODO:maybe check if feature is new
            unselectAllFeatures();
            // document.getElementById('featureBewertung').value = message.userStories[0];
            // document.getElementById('beschrebungBewertung').value = message.userStories[1];
            // document.getElementById(""+message.id).classList.add("selected-feature");
            //TODO rename elements!
            document.getElementById('featureBewertung').value = message.title;
            document.getElementById('beschrebungBewertung').value = message.description;
            document.getElementById(""+message.id).classList.add("selected-feature");
            selectetFeature = message.id;
            hideVotes();
            resetVotingPanel();
            enableButton();
            document.getElementById("result").style.display = "none";
            document.getElementById("voting").style.display = "block";
            break;

        case 'LEAVE':
            updateUsernames(message.usernames);
            break;

        case 'VOTE':
            //updateUserVote(message.user, message.bewertung1, message.bewertung2, message.zeit);
            updateVote(message);
            break;
        case 'ALLVOTED':
            updateVote(message);
            showVotes();
            requestResult();
            break;
        case 'RESULT':
            setResult(message)

        // TODO: other cases
    }
}

function enableButton(){
    document.getElementById('voteButton').disabled = false;
    document.getElementById('voteButton').classList = "button";
}

function requestResult(){
    if (stompClient) {
            message = selectetFeature;

        stompClient.send(`${topic}/result`, {}, JSON.stringify(message));
    }
    send = true;
}

function  setResult(feature){
    document.getElementById("result").style.display = "block";
    document.getElementById("voting").style.display = "none";

    document.getElementById("boostAuswertung").innerText = "Mittelwert: " + feature.boostMean + "\r";
    document.getElementById("boostAuswertung").innerText += "Standartabweichung: " + feature.boostStab;
    if(bigDif(feature.boostStab)){
        document.getElementById("boostAuswertung").classList.add("bigDif");
    }

    document.getElementById("ripAuswertung").innerText = "Mittelwert: " + feature.ripMean + "\r";
    document.getElementById("ripAuswertung").innerText += "Standartabweichung: " + feature.ripStab ;
    if(bigDif(feature.ripStab)){
        document.getElementById("ripAuswertung").classList.add("bigDif");
    }

    document.getElementById("timeAuswertung").innerText = "Mittelwert: " + feature.timeMean + "\r";
    document.getElementById("timeAuswertung").innerText += "Standartabweichung: " + feature.timeStab;
    if(bigDif(feature.timeStab)){
        document.getElementById("timeAuswertung").classList.add("bigDif");
    }

    document.getElementById("bewertung-" + feature.id).innerText
        = "Boost: " + feature.boostMean + ", RIP: " + feature.ripMean + ", Time: " + feature.timeMean;
    document.getElementById(feature.id).classList.add("wurdeBewertet");
}

function bigDif(messeage){
    return 10 < messeage;
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
    allUserStories = [name, beschreibung, bewertung1, bewertung2, zeit];

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


function addBewertung(userstory) {
    let bewertung

    let name = document.getElementById("featureBewertung").value;
    let beschreibung = document.getElementById("beschrebungBewertung").value;
    let featureId = userstory.id;

    bewertung = [name, beschreibung, featureId];

    if (stompClient) {
        let message = {
            userStories: bewertung,
            roomId: roomId,
            featureId: featureId,
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
        username => {
            let userdiv = document.createElement("div");
            let uservotep = document.createElement("p");
            let usernamep = document.createElement("p");
            usernamep.innerText = username;
            uservotep.classList.add("vote");
            uservotep.style.display = "none";
            userdiv.id = username;
            userdiv.classList.add("user");
            //userdiv.innerHTML += username;
            userdiv.append(usernamep);
            userdiv.append(uservotep);
            usernames.append(userdiv);
        }
    )
}

// fill board after merge
function fillBoard(userStories) {
    userStoryBoard.empty();
    userStories.forEach(
        userStory => userStoryBoard.append(`<h4>${userStory.name}<\h4>`, `<p>${userStory.beschreibung}<\p>`)
    )
}

function listToObjList(userStoryList) {
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

function sendBewertung() {
    let bewertung;

    let name = document.getElementById("featureBewertung").value;
    let beschreibung = document.getElementById("beschrebungBewertung").value;
    let bewertung1 = document.querySelector("#bewertung1").value;
    let bewertung2 = document.querySelector("#bewertung2").value;
    let zeit = document.querySelector("#zeit").value;

    //allUserStories.push(this.textContent);
    bewertung = [name, beschreibung, bewertung1, bewertung2, zeit, selectetFeature];

    if (stompClient) {
        let message = {
            username: username,
            content: bewertung,
            featureId: selectetFeature,
            roomId: roomId,
            phase: 'VOTE'
        }

        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;

}

function sendBewertungAgain(){
    console.log("test")
    if(stompClient){
        let message = {
            featureId: selectetFeature,
            roomId: roomId,
            phase: 'VOTEAGAIN'
        }
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;

}

function sendFeature() {


    let name = document.getElementById("userStoryInput").value;
    let beschreibung = document.getElementById("beschreibung").value;
    //allUserStories.push(this.textContent);

    if (stompClient) {
        let message = {
            title: name,
            description: beschreibung,
            event: 'FEATURE'
        }

        stompClient.send(`${topic}/addFeature`, {}, JSON.stringify(message));
    }
    send = true;
}



$(document).ready(function () {

    // get session variables
    roomId = parseInt(sessionStorage.getItem("roomId"));
    username = sessionStorage.getItem("username");
    usernames.append(`<div>${username}</div>`);
    console.log(username);
    topic = `/app/room/${roomId}`; // /app

    sendUserStoriesButton.click(sendUserStories);

    voteAgainButton.click(sendBewertungAgain);
    voteButton.click(sendBewertung);
    addFeatureButton.click(sendFeature);
    //if user has created the room show force send button
    admin = (sessionStorage.getItem("admin"));

    setTitle();
    connect();
});
