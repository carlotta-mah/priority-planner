let pageContainer = $('#pageContainer');
let selectedFeatureDisplay = $('#selected-feature-display')
let usernames = $('#usernames');
let voteButton = $('#voteButton');
let nextButton = $('#next');
let votingpanel = $('#voting-panel')
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

//algemeine Variablen
let username;
let roomId;
let topic;
let currentSubscription;
let stompClient;
let admin;
let send;
const userStoryBoardDiv = document.getElementById("userStoryBoard");

//diagramm
let ctxDognut;
let myDognutChart

let ripList = [];
let timeList = [];


/**
 * Verbindung über Websocket mit Server für die Kommunikation
 */
function connect() {
    let socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, onConnected, onError);
}

/**
 * Bearbeitet den Fall wenn sich die Fatures in der Datenbank ändern
 *
 * @param payload Feature welches hinzugefügt bzw. gelöscht werden soll.
 */
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

/**
 * Abonnieren den Websocket-Kanal, der der roomId entspricht, um Nachrichten vom Server zu empfangen
 */
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

/**
 * Wenn sich das Ergebgnis in der Datenbank ändert sich das durch diese Funktion auch im Client
 * @param payload Das aktuelle Ergebnis
 */
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


    //aktualisiert das Diagramm
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

/**
 * wird aufgerufen, wenn eine Websocket-Verbindung aufgebaut wird
 */
function onConnected() {
    registerInRoom()
}

/**
 * wird aufgerufen, wenn eine Websocket-Verbindung fehlschlägt
 */
function onError() {
    // TODO: something like go back to main page or show error page
    window.location.href = "";
}

/**
 * gibt die aktuelle Id wider und erhöt den idGenerator um 1
 * @returns {number} aktuelle Id
 */
function getId() {
    return idGenerator++;
}

/**
 * versteckt die Votes
 */
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

/**
 * Resettet die VotingPanels auf die default werte
 */
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

/**
 * Makiert die User die schon gevoted hat
 * @param user der zu makierende User
 */
function updateUserVote(user) {
    document.getElementById("user-"+user).classList.add('hasVoted');
}

/**
 * updatet eine einzelne Stimme
 * @param vote Der Vote der geupdatet werden soll
 */
function updateVote(vote) {
    let userdiv = document.getElementById("user-"+vote.user);
    let votediv = userdiv.childNodes[1];
    votediv.innerHTML = "";
    let vote1p = document.createElement("p");
    let vote2p = document.createElement("p");
    let vote3p = document.createElement("p");

    votediv.setAttribute("class", "test");
    vote1p.innerText = vote.bewertung1;
    votediv.appendChild(vote1p);
    vote2p.innerText = vote.bewertung2;
    votediv.appendChild(vote2p);
    vote3p.innerText = vote.zeit;
    votediv.appendChild(vote3p)

    updateUserVote(vote.user);
    userdiv.classList.add("hasVoted");
}

/**
 * udatet alle Votes
 * @param votes Liste von Votes
 */
function updateVotes(votes) {

    votes.forEach(
        vote => {
            updateVote(vote);
        }
    )
}

/**
 * Macht die Votes auf Clientseite sichtbar
 */
function showVotes() {
    let voteList = document.getElementsByClassName("test");
    Array.prototype.forEach.call(voteList, function (voteElement) {
        voteElement.style.display = "inline-block";
    });

}

/**
 * Setzt das Feature welches bewertet werden soll
 * @param title Der Titel
 * @param description die Beschreibung
 * @param id Die Id der Features
 */
function setSelectedFeature(title, description, id) {
    document.getElementById("selected-feature-name").innerText = title;
    document.getElementById("selected-feature-descr").innerText = description;
    featureBar.select(id);
}

/**
 * Resettet das Zwischenergebnis. Die Rot hinterlegten Felder werden entfernt
 */
function resetResult() {
    document.getElementById("result").style.display = "none";
    document.getElementById("voting").style.display = "block";
    document.getElementById("boost-res").classList.remove("bigDif");
    document.getElementById("rip-res").classList.remove("bigDif");
    document.getElementById("time-res").classList.remove("bigDif");
}

/**
 * Reagiert auf die Serverbefehle
 * @param payload Nachricht vom Server
 */
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

/**
 * aktiviert den Button zum Voten
 */
function enableButton() {
    document.getElementById('voteButton').disabled = false;
    document.getElementById('voteButton').classList.add("button");
}

/**
 * Fragt den Server nach dem aktuellem Result
 */
function requestResult() {
    if (stompClient) {
        message = selectetFeature;

        stompClient.send(`${topic}/result`, {}, JSON.stringify(message));
    }
    send = true;
}

/**
 * Setzt das Zwischenergebnis
 * @param feature das Feature welcher bewertet werden soll
 */
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

/**
 * große schwankungen in der Bewertungen wird ture wenn die Standardabweicher größer ist als 10 %
 * @param messeage Standardabweichung
 * @returns {boolean} ist der Unterschied zu groß dann true, sonst false
 */
function bigDif(messeage) {
    return 10 < messeage;
}

/**
 * Löscht ein Feature und benachrichtigt den Server
 * @param featureId Die Feature Id
 */
function deleteFeature(featureId) {
    let message = {
        featureId: featureId,
        phase: DELETE
    }
    stompClient.send(`${topic}/feature`, {}, JSON.stringify(message));
}

/**
 * Updatet die User die sich in diesem Raum befinden wenn ein neuer User beitrit
 * @param users
 */
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

            userdiv.id = "user-"+username;
            userdiv.classList.add("userDiv");
            usernamep.classList.add("user")
            userdiv.append(usernamep);
            userdiv.append(uservotep);
            usernames.append(userdiv);
        }
    )
}

/**
 * Befüllt das Board im dem ausgewählten Feature. Hier als UserStory implementiert
 * @param userStories Das Aktuelle Feature
 */
function fillBoard(userStories) {
    userStoryBoard.empty();
    userStories.forEach(
        userStory => userStoryBoard.append(`<h4>${userStory.name}<\h4>`, `<p>${userStory.beschreibung}<\p>`)
    )
}

/**
 * Sendet die Bewertung vom Server an den Client
 */
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

/**
 * Sendet die Bewertung von einem Feature welches schon Bewertet worden ist. Damit der Server die Bewertung
 * überschreiben kann.
 */
function sendBewertungAgain() {
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

/**
 * Fügt einer Ergebnistabelle ein Feature hinzu
 * @param tableId Id von der Tabelle die erweiter werden soll
 * @param name Name des Features
 * @param beschreibung beschreibung des Features
 * @param boost Boost Wert
 * @param rip Rip wert
 * @param time Aufwand in Tagen
 */
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

/**
 * Aktualisiert das Ergebnis auf Serverseite
 */
function setErgebnis() {
    if (stompClient) {
        let message = {
            roomId: roomId,
        }
        stompClient.send(`${topic}/ergebnis`, {}, JSON.stringify(message));
    }
    send = true;
}

/**
 * wechselt von Bewertungsansicht zur Ergebnisansicht und wieder zurück
 */
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

/**
 * wechselt zur Tabelle auf dem der Knopf gedrückt wurde
 * @param evt von der Tabelle
 * @param tableName Name der Tabelle
 */
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
    // ruft session variables  auf und setzt funktionen für die Buttons
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
