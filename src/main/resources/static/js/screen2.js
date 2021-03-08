let pageContainer = $('#pageContainer');
let selectedFeatureDisplay = $('#selected-feature-display')
let usernames = $('#usernames');
let voteButton = $('#voteButton');
let nextButton = $('#next');
let votingpanel = $('#voting-panel')
//let addButton = $('#addToVoteButton');
let ergebnisButton = $('#ergebnis');
let bewertungButton = $('#ergebnis2');
let backButton = $('#back');
let votingShown;
let voteAgainButton = $('#voteAgain');
let myname = $('#my-name');
let myname2 = $('#my-name2');
let projectname = $('#project-name');
let projectname2 = $('#project-name2');
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
        addToTable("mustHaveTable", feature.title, feature.description,
            feature.boostMean, feature.ripMean, feature.timeMean);
        mustHaveAnzahl++;
        mustHaveTime = mustHaveTime + feature.timeMean;
    });
    $("#shouldHaveTable tr:not(:first)").remove();
    message.shouldHave.forEach(feature => {
        addToTable("shouldHaveTable", feature.title, feature.description,
            feature.boostMean, feature.ripMean, feature.timeMean);
    });
    $("#couldHaveTable tr:not(:first)").remove();
    message.couldHave.forEach(feature => {
        addToTable("couldHaveTable", feature.title, feature.description,
            feature.boostMean, feature.ripMean, feature.timeMean);
    });
    $("#wontHaveTable tr:not(:first)").remove();
    message.wontHave.forEach(feature => {
        addToTable("wontHaveTable", feature.title, feature.description,
            feature.boostMean, feature.ripMean, feature.timeMean);
    });
    message.allFeature.forEach(feature => {
        featureAnzahl++;
    });

    //Ausgaben beschreibung
    document.getElementById("diagramBeschreibung").innerHTML = "";
    let diagramBeschreibung = document.createElement("h3");
    let anzahlMustHave = document.createElement("h1");
    anzahlMustHave.classList.add("zahl1");
    anzahlMustHave.innerHTML = mustHaveAnzahl;
    let anzahlFeatures = document.createElement("h3");
    anzahlFeatures.classList.add("zahl2");
    anzahlFeatures.innerHTML = featureAnzahl;
    let timeFuerMustHave = document.createElement("h3");
    timeFuerMustHave.classList.add("zahl2");
    timeFuerMustHave.innerHTML = mustHaveTime;


    diagramBeschreibung.classList.add("diagramSchrift");
    diagramBeschreibung.appendChild(anzahlFeatures);
    diagramBeschreibung.innerHTML += " Features in total" + "<br />";
    diagramBeschreibung.appendChild(anzahlMustHave);
    diagramBeschreibung.innerHTML += " of them are must haves" + "<br />";
    diagramBeschreibung.appendChild(timeFuerMustHave);
    diagramBeschreibung.innerHTML += " days for the must haves" + "<br />";


    document.getElementById("diagramBeschreibung").appendChild(diagramBeschreibung);


    myDognutChart.data = {
        datasets: [{
            data: [document.getElementById("mustHaveTable").rows.length - 1,
                document.getElementById("shouldHaveTable").rows.length - 1,
                document.getElementById("couldHaveTable").rows.length - 1,
                document.getElementById("wontHaveTable").rows.length - 1],
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
    let userList = document.getElementsByClassName("userDiv");
    Array.prototype.forEach.call(userList, function (user) {
        user.classList.remove("hasVoted");
    });


}


function resetVotingPanel() {
    let inputs = $(".slider");
    inputs.each(function () {
        var elem = new Foundation.Slider($(this));
        elem.options.start = 0;
        elem.options.end = 100;
    })
    document.querySelector("#bewertung1").value = 50;
    document.querySelector("#bewertung2").value = 50;
    document.querySelector("#zeit").value = 0;
}

function updateUserVote(user) {
    document.getElementById(user).classList.add('hasVoted');
}

function updateVote(vote) {
    // var boostIcon = new Image(30, 30);
    // var ripIcon = new Image(30, 30);
    // var timeIcon = new Image(30, 30);
    // boostIcon.src = "../img/astronaut.svg";
    // ripIcon.src = "../img/tombstone.svg";
    // timeIcon.src = "../img/uhr-2.svg";
    let userdiv = document.getElementById(vote.user);
    let votediv = userdiv.childNodes[1];
    votediv.innerHTML = "";
    let vote1p = document.createElement("p");
    let vote2p = document.createElement("p");
    let vote3p = document.createElement("p");

    votediv.setAttribute("class", "test");
    vote1p.innerText = vote.bewertung1;
    // vote1p.appendChild(boostIcon);
    // votediv.appendChild(boostIcon);
    votediv.appendChild(vote1p);
    vote2p.innerText = vote.bewertung2;
    // votediv.appendChild(ripIcon);
    votediv.appendChild(vote2p);
    vote3p.innerText = vote.zeit;
    votediv.appendChild(vote3p)

    // votediv.innerHTML = boostIcon+ vote.bewertung1 +
    //     " RIP:" + vote.bewertung2 + " Zeit:" + vote.zeit;

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
    document.getElementById("boost-res").classList.remove("bigDif");
    document.getElementById("rip-res").classList.remove("bigDif");
    document.getElementById("time-res").classList.remove("bigDif");
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
            if (!votingShown) {
                toggleElement(document.getElementById("greeting"));
                toggleElement(document.getElementById("voting-panel"));
            }
            votingShown = true;
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
            votingShown = false;
            resetResult();
            resetVotingPanel();
            hideVotes();
    }
}

function enableButton() {//welcher button???
    document.getElementById('voteButton').disabled = false;
    document.getElementById('voteButton').classList.add("button");
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

    document.getElementById("boostAuswertung").innerText = "Average: " + feature.boostMean + "\r";
    document.getElementById("boostAuswertung").innerText += "Avg. Diff.: " + feature.boostStab;
    if (bigDif(feature.boostStab)) {
        document.getElementById("boost-res").classList.add("bigDif");
    }

    document.getElementById("ripAuswertung").innerText = "Average: " + feature.ripMean + "\r";
    document.getElementById("ripAuswertung").innerText += "Avg. Diff.: " + feature.ripStab;
    if (bigDif(feature.ripStab)) {
        document.getElementById("rip-res").classList.add("bigDif");
    }

    document.getElementById("timeAuswertung").innerText = "Average: " + feature.timeMean + "\r";
    document.getElementById("timeAuswertung").innerText += "Avg. Diff.: " + feature.timeStab;
    if (bigDif(feature.timeStab)) {
        document.getElementById("time-res").classList.add("bigDif");
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
            let uservotep = document.createElement("div");
            let usernamep = document.createElement("p");
            usernamep.innerText = username;
            uservotep.classList.add("vote");
            uservotep.style.display = "none";

            userdiv.id = username;
            userdiv.classList.add("userDiv");
            usernamep.classList.add("user")
            // userdiv.classList.add("user");
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

function addToTable(tableId, name, beschreibung, boost, rip, time) {
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

    if (document.body.classList.contains("ergebnisBody")) {
        document.body.classList.remove("ergebnisBody")

    } else {
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

$(function () {
    $("#userStoryBoard").sortable();
    $("#userStoryBoard").disableSelection();
});

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
    myname2.text(username);
    projectname.text(roomName);
    projectname2.text(roomName);
    invitehinttext.html("<b> &nbsp; Your Room ID is &nbsp;" + roomId + "." + "</b>");
    votingShown = true;

    let featureElement = document.getElementById("featurePanel");
    featureBar = new featureSidebar(featureElement);
    topic = `/app/room/${roomId}`; // /app

    nextButton.click(featureBar.addNextVote);
    voteAgainButton.click(sendBewertungAgain);
    voteButton.click(sendBewertung);
    ergebnisButton.click(zeigErgebnis);
    bewertungButton.click(zeigErgebnis)
    backButton.click(zeigErgebnis);

    //if user has created the room show force send button
    admin = (sessionStorage.getItem("admin"));

    if (roll == "Entwickler") {
        $('#zeitLabel').toggle();
        $('#zeit').toggle();
    } else {
        document.getElementById("zeit").value = 0;
    }
    // let ripbar = document.getElementById("rip-bar")
    // var myBarChart = new Chart(ripbar, {
    //     type: 'bar',
    //     data: [12],
    //     options: {}
    // });
    //diagramm
    let data = {
        datasets: [{
            data: [document.getElementById("mustHaveTable").rows.length - 1,
                document.getElementById("shouldHaveTable").rows.length - 1,
                document.getElementById("couldHaveTable").rows.length - 1,
                document.getElementById("wontHaveTable").rows.length - 1],
            backgroundColor: [
                'rgb(23,212,205)',
                'rgb(3,31,51)',
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

            elements: {
                arc: {
                    borderWidth: 0
                }
            },

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
    $('#offCanvas').on('closed.zf.offCanvas', function (event) {
        featureBar.showOpenInputButton();
    });

    connect();
});
