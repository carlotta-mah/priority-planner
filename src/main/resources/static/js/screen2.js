let pageContainer = $('#pageContainer');
let selectedFeatureDisplay = $('#selected-feature-display')
let usernames = $('#usernames');
let voteButton = $('#voteButton');
let nextButton = $('#next');
let votingpanel = $('#voting-panel')
//let addButton = $('#addToVoteButton');
let ergebnisButton = $('#ergebnis');
let backButton = $('#back');

let voteAgainButton = $('#voteAgain');
let myname = $('#my-name');
let projectname = $('#project-name');
let invitehinttext = $('#invite-hint-text')
let featureBar;

let mustHaveTime;
let mustHaveAnzahl;
let featureAnzahl;

let projektName;
let username;
let roomId;
let topic;
let currentSubscription;
let stompClient;
let admin;
let userStories;
let send;
const userStoryBoardDiv = document.getElementById("userStoryBoard");

let ctxDognut;
let myDognutChart

let boostList = [];
let ripList = [];
let timeList = [];


// connect via websocket with server for bidirectional communication
function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

function onFeatureReceived(payload) {
    let message = JSON.parse(payload.body);
    switch (message.event) {
        case 'FEATURE':
            let userstory = new UserStory(message.title, message.description, message.id);
            featureBar.addToBoard(userstory);
            featureBar.toggleFeatureInput();
            break;
        case 'DELETE':
            featureBar.deleteFeature(message.id);
            break;

    }
}

// subscribe to websocket channel corresponding to roomId to receive messages from server
function registerInRoom() {
    // TODO: popup window or something to set a username in case user didn't come from start window

    currentSubscription = stompClient.subscribe(`/user/queue/${roomId}`, onMessageReceived);
    currentSubscription = stompClient.subscribe(`/queue/${roomId}`, onMessageReceived);
    currentSubscription = stompClient.subscribe(`/queue/feature/${roomId}`, onFeatureReceived);
    currentSubscription = stompClient.subscribe(`/queue/ergebnis/${roomId}`, onErgebnisReceived);


    stompClient.send(`${topic}/addUser`,
        {},
        JSON.stringify({username: username, roomId: roomId, roll: roll})
    );

    // TODO: receive everything and show, meanwhile hide everything
}

function onErgebnisReceived(payload) {
    let message = JSON.parse(payload.body);
    mustHaveAnzahl = 0;
    mustHaveTime = 0;
    featureAnzahl = 0;

    $("#mustHaveTable tr:not(:first)").remove();
    message.mustHave.forEach(feature => {
        addToTable("mustHaveTable",feature.title, feature.description,
            feature.boostMean,feature.ripMean,feature.timeMean);
        mustHaveAnzahl++;
        mustHaveTime = mustHaveTime + feature.timeMean;
    });
    $("#shouldHaveTable tr:not(:first)").remove();
    message.shouldHave.forEach(feature => {
        addToTable("shouldHaveTable",feature.title, feature.description,
            feature.boostMean,feature.ripMean,feature.timeMean);
    });
    $("#couldHaveTable tr:not(:first)").remove();
    message.couldHave.forEach(feature => {
        addToTable("couldHaveTable",feature.title, feature.description,
            feature.boostMean,feature.ripMean,feature.timeMean);
    });
    $("#wontHaveTable tr:not(:first)").remove();
    message.wontHave.forEach(feature => {
        addToTable("wontHaveTable",feature.title, feature.description,
            feature.boostMean,feature.ripMean,feature.timeMean);
    });
    message.allFeature.forEach(feature => {featureAnzahl++;});

    //Ausgaben beschreibung
    document.getElementById("diagramBeschreibung").innerHTML = "";
    let timeMustHave = document.createElement("h3");
    timeMustHave.classList.add("diagramSchrift");
    timeMustHave.innerHTML += "Anzahl Features: " + featureAnzahl+ "<br />";
    timeMustHave.innerHTML += "Anzahl Must haves: " + mustHaveAnzahl+ "<br />";
    timeMustHave.innerHTML +="Benötigte Zeit für Must haves: " + mustHaveTime;

    document.getElementById("diagramBeschreibung").appendChild(timeMustHave);


     myDognutChart.data = {
        datasets: [{
            data: [document.getElementById("mustHaveTable").rows.length-1,
                document.getElementById("shouldHaveTable").rows.length-1,
                document.getElementById("couldHaveTable").rows.length-1,
                document.getElementById("wontHaveTable").rows.length -1],
            backgroundColor: [
                'rgb(23,212,205)',
                'rgb(3,31,51)',
                'rgb(170,170,170)',
                'rgb(127,127,127)',
            ],
        }],

        labels: ['Must have', 'Should have', 'Could have', 'wont Have'],
    };

    myDognutChart.update();
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
    Array.prototype.forEach.call(voteList, function (voteElement) {
        voteElement.style.display = "none";
    });
    let userList = document.getElementsByClassName("user");
    Array.prototype.forEach.call(userList, function (user) {
        user.classList.remove("hasVoted");
    });


}


function resetVotingPanel() {
    //TODO:set sliders and Time to default
    let inputs = $(":input[type=range]");
    Array.prototype.forEach.call(inputs, function (input) {
        input.value = "50";
    });
    document.querySelector("#bewertung1").value = 50;
    document.querySelector("#bewertung2").value = 50;
    document.querySelector("#zeit").value = 0;
}

function updateUserVote(user) {
    document.getElementById(user).classList.add('hasVoted');
}

function updateVote(vote) {
    let userdiv = document.getElementById(vote.user);
    let votediv = userdiv.childNodes[1];
    votediv.setAttribute("class", "test");
    votediv.innerText = "Boost:" + vote.bewertung1 +
        " RIP:" + vote.bewertung2 + " Zeit:" + vote.zeit;

    updateUserVote(vote.user);
    //   userdiv.append(bewertungsP)
    userdiv.classList.add("hasVoted");
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
    Array.prototype.forEach.call(voteList, function (voteElement) {
        voteElement.style.display = "inline-block";
    });


}

function setSelectedFeature(title, description, id) {
    document.getElementById("selected-feature-name").innerText = title;
    document.getElementById("selected-feature-descr").innerText = description;
    //document.getElementById(""+message.id).classList.add("selected-feature");
    featureBar.select(id);
}

function resetResult() {
    document.getElementById("result").style.display = "none";
    document.getElementById("voting").style.display = "block";
    document.getElementById("ripAuswertung").classList.remove("bigDif");
    document.getElementById("boostAuswertung").classList.remove("bigDif");
    document.getElementById("timeAuswertung").classList.remove("bigDif");
}

// called when server calls
function onMessageReceived(payload) {
    let message = JSON.parse(payload.body);

    switch (message.event) {
        case 'ADDED_USER':
            updateUsernames(message.onlyUserNames);
            featureBar.updateFeatures(message.features);
            if (message.activeFeature != null) {
                document.getElementById('selected-feature-name').value = message.activeFeature.title;
                document.getElementById('selected-feature-descr').value = message.activeFeature.description;
                setSelectedFeature(message.activeFeature.title, message.activeFeature.description, message.activeFeature.id);
                updateVotes(message.activeFeature.votes);
                enableButton();
            }
            admin = (sessionStorage.getItem("admin"));
            break;
        case 'UPDATE':
            featureBar.updateFeatures(message.userStories);
            break;
        case 'ADDVOTE':
            //TODO:maybe check if feature is new
            featureBar.unselectAllFeatures();
            $('.user')
            //TODO rename elements!
            document.getElementById('selected-feature-name').value = message.title;
            document.getElementById('selected-feature-descr').value = message.description;
            document.getElementById("" + message.id).classList.add("selected-feature");
            setSelectedFeature(message.title, message.description, message.id);
            featureBar.select(message.id);
            resetVotingPanel();
            enableButton();
            resetResult();
            hideVotes();
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
            //Todo: change votebutton of selected feature
            featureBar.setButtonToRevote();
            requestResult();
            break;
        case 'RESULT':
            setResult(message)
            break;
        case'EMPTY':
            toggleElement(document.getElementById("greeting"));
            toggleElement(document.getElementById("voting-panel"));
            resetResult();
            resetVotingPanel();
            hideVotes();
    }
}

function enableButton() {//welcher button???
    document.getElementById('voteButton').disabled = false;
    document.getElementById('voteButton').classList = "button";
}

function requestResult() {
    if (stompClient) {
        message = selectetFeature;

        stompClient.send(`${topic}/result`, {}, JSON.stringify(message));
    }
    send = true;
}

function setResult(feature) {
    document.getElementById("result").style.display = "block";
    document.getElementById("voting").style.display = "none";

    document.getElementById("boostAuswertung").innerText = "Mittelwert: " + feature.boostMean + "\r";
    document.getElementById("boostAuswertung").innerText += "Standartabweichung: " + feature.boostStab;
    if (bigDif(feature.boostStab)) {
        document.getElementById("boostAuswertung").classList.add("bigDif");
    }

    document.getElementById("ripAuswertung").innerText = "Mittelwert: " + feature.ripMean + "\r";
    document.getElementById("ripAuswertung").innerText += "Standartabweichung: " + feature.ripStab;
    if (bigDif(feature.ripStab)) {
        document.getElementById("ripAuswertung").classList.add("bigDif");
    }

    document.getElementById("timeAuswertung").innerText = "Mittelwert: " + feature.timeMean + "\r";
    document.getElementById("timeAuswertung").innerText += "Standartabweichung: " + feature.timeStab;
    if (bigDif(feature.timeStab)) {
        document.getElementById("timeAuswertung").classList.add("bigDif");
    }

    document.getElementById("bewertung-" + feature.id).innerText
        = "Boost: " + feature.boostMean + ", RIP: " + feature.ripMean + ", Time: " + feature.timeMean;
    document.getElementById(feature.id).classList.add("wurdeBewertet");
}

function bigDif(messeage) {
    return 10 < messeage;
}

// function forceSend() {
//     let message = {
//         username: username,
//         userStories: null,
//         roomId: roomId,
//         phase: 'FORCE_SEND'
//     }
//
//     stompClient.send(`${topic}/forceSend`, {}, JSON.stringify(message));
// }


function deleteFeature(featureId) {
    let message = {
        featureId: featureId,
        phase: DELETE
    }
    stompClient.send(`${topic}/feature`, {}, JSON.stringify(message));
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

function sendBewertung() {
    let bewertung;

    let name = document.getElementById("selected-feature-name").value;
    let beschreibung = document.getElementById("selected-feature-descr").value;
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
            roll: roll,
            phase: 'VOTE'
        }

        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;

}

function sendBewertungAgain() {
    console.log("test")
    if (stompClient) {
        let message = {
            featureId: selectetFeature,
            roomId: roomId,
            phase: 'VOTEAGAIN'
        }
        stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
    }
    send = true;

}
function addToTable(tableId,name, beschreibung, boost, rip, time) {
    var rowLength = document.getElementById(tableId).rows.length;
    var table = document.getElementById(tableId);
    var row = table.insertRow(rowLength);
    var cell1 = row.insertCell(0);
    var cell2 = row.insertCell(1);
    var cell3 = row.insertCell(2);
    var cell4 = row.insertCell(3);
    var cell5 = row.insertCell(4);
    cell1.innerHTML = name;
    cell2.innerHTML = beschreibung;
    cell3.innerHTML = boost;
    cell4.innerHTML = rip;
    cell5.innerHTML = time;
}
function setErgebnis() {
    if (stompClient) {
        let message = {
            roomId: roomId,
        }
        stompClient.send(`${topic}/ergebnis`, {}, JSON.stringify(message));
    }
    send = true;
}

function zeigErgebnis() {
    setErgebnis();

    document.getElementById("defaultOpen").click();
    $('#ergebnisDiv').toggle();
    $('#pageContainer').toggle();

    if(document.body.classList.contains("ergebnisBody")){
        document.body.classList.remove("ergebnisBody")

    }else{
        document.body.classList.add("ergebnisBody");
    }
}

function openTable(evt, tableName) {
    var i, tabcontent, tablinks;
    tabcontent = document.getElementsByClassName("tabcontent");
    for (i = 0; i < tabcontent.length; i++) {
        tabcontent[i].style.display = "none";
    }
    tablinks = document.getElementsByClassName("tablinks");
    for (i = 0; i < tablinks.length; i++) {
        tablinks[i].className = tablinks[i].className.replace(" active", "");
    }
    document.getElementById(tableName).style.display = "block";
    evt.currentTarget.className += " active";
}

$(document).ready(function () {
    // get session variables
    roomId = parseInt(sessionStorage.getItem("roomId"));
    username = sessionStorage.getItem("username");
    roomName = sessionStorage.getItem("roomName");
    roll = sessionStorage.getItem("roll");
    usernames.append(`<div>${username}</div>`);
    console.log(username);
    console.log(roll);
    console.log(roomName);
    votingpanel.hide();
    myname.text(username);
    projectname.text(roomName);
    invitehinttext.text("Your Room ID is" + roomId);

    let featureElement = document.getElementById("featurePanel");
    featureBar = new featureSidebar(featureElement);
    topic = `/app/room/${roomId}`; // /app

    nextButton.click(featureBar.addNextVote);
    voteAgainButton.click(sendBewertungAgain);
    voteButton.click(sendBewertung);
    ergebnisButton.click(zeigErgebnis);
    backButton.click(zeigErgebnis);

    //if user has created the room show force send button
    admin = (sessionStorage.getItem("admin"));

    if(roll == "Entwickler"){
        $('#zeitLabel').toggle();
        $('#zeit').toggle();
    }else{
        document.getElementById("zeit").value = 0;
    }

    //diagramm
    let data = {
        datasets: [{
            data: [document.getElementById("mustHaveTable").rows.length-1,
                document.getElementById("shouldHaveTable").rows.length-1,
                document.getElementById("couldHaveTable").rows.length-1,
                document.getElementById("wontHaveTable").rows.length -1],
            backgroundColor: [
                'rgb(23,212,205)',
                'rgb(1,54,105)',
                'rgb(170,170,170)',
                'rgb(127,127,127)',
            ],
        }],

        labels: ['Must have', 'Should have', 'Could have', 'wont Have'],
    };
    ctxDognut = document.getElementById('myDognutChart').getContext('2d')
    let configDognut = {
        type: 'doughnut',
        data: data,

        options: {

            legend: {
                position: 'right',
                display: true,
                labels: {
                    fontColor: 'rgb(255, 255, 255)'
                }
            },
            responsive: true // resizes chart when ist container does
        }
    }

    myDognutChart = new Chart(ctxDognut, configDognut);
    $('#offCanvas').on('closed.zf.offCanvas', function(event) {
        featureBar.showOpenInputButton();
    });

    connect();
});
