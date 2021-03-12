let addFeatureButton = $('#addFeature');
let cancelFeature = $('#cancelFeature');
let userStoryBoard = $('#userStoryBoard');
let addButton = $('#addToVoteButton');
let userStoryInput = $('#userStoryInput');
let beschreibungInput = $('#beschreibungInput')
let openInputButton = $("#open-feature-input")
let selectetFeature;

/**
 * Die Klasse featureSidebar erstellt die Sidebar von der screen2.html.
 * Sie beinhaltet sämtliche Features. Über die Sidebar ist es möglich das nächste Feature auszuwählen welches
 * gevotet werden soll.
 */
class featureSidebar {

    /**
     * Inizalisiert die Sidebar
     */
    constructor(sidebar) {
        addFeatureButton.click(this.sendFeature);
        openInputButton.click(this.toggleFeatureInput);
        cancelFeature.click(this.cancelInput);
        document.getElementById("feature-input-row").style.display = "none"
    }

    /**
     * Wechselt die Ansicht von open-Feature zur feature ansicht bzw. andersrum.
     */
    toggleFeatureInput() {
        let open = document.getElementById("open-feature-input-row");
        let input = document.getElementById("feature-input-row");
        toggleElement(open);
        toggleElement(input);
    }

    /**
     * Entfernt das selected-feature token von der classList bei allen Features
     */
    unselectAllFeatures() {
        $(".selected-feature button.primary").text("Vote now");
        $(".selected-feature button.primary").removeClass("voting");
        let featureList = document.getElementsByClassName("featureList");
        Array.prototype.forEach.call(featureList, function (featureElement) {
            featureElement.classList.remove("selected-feature");
        });

    }

    /**
     * wählt ein Feature aus das bewertet werden soll
     *
     * @param featureId Id des Feature welches ausgewehlt werden soll
     */
    select(featureId) {
        if (selectetFeature == null) {
            toggleElement(document.getElementById("greeting"))
            toggleElement(document.getElementById("voting-panel"))
        }
        selectetFeature = featureId;
        document.getElementById("" + featureId).classList.add("selected-feature");
        $(".selected-feature button.primary").text("voting");
        $(".selected-feature button.primary").addClass("voting");
        document.getElementById("" + featureId).classList.remove("wurdeBewertet");
    }

    /**
     * fügt eine UserStory bzw. Feature zum Board hinzu
     * @param userstory das Feature welches dem Board hinzugefügt werden soll
     */
    addToBoard(userstory) {
        const newDiv = document.createElement("div");
        let name = userstory.name;
        let beschreibung = userstory.beschreibung;
        let myId = userstory.id;

        newDiv.id = myId;
        newDiv[0] = name;
        newDiv[1] = beschreibung;

        newDiv.classList.add('featureList');

        let nameH4 = document.createElement("h4");
        nameH4.innerHTML = name;
        let bescheibungP = document.createElement("p");
        bescheibungP.innerHTML = beschreibung;
        let bewertung = document.createElement("div");
        bewertung.id = "bewertung-" + myId;

        newDiv.appendChild(nameH4);
        newDiv.appendChild(bescheibungP);
        newDiv.appendChild(bewertung);

        var button = document.createElement("BUTTON");
        button.title = myId;
        button.id = "select-" + myId;
        button.classList.add('button');
        button.classList.add('primary')
        button.innerHTML = "Vote now";
        var that = this;

        button.addEventListener("click", function () {
            let divName = document.getElementById(this.title)[0];
            let divBeschreibung = document.getElementById(this.title)[1];
            hideVotes();
            resetVotingPanel();
            that.addBewertung(userstory);
            that.unselectAllFeatures();
            document.getElementById("selected-feature-name").value = divName;
            document.getElementById("selected-feature-descr").value = divBeschreibung;
            //unselectAllFeatures();
            newDiv.classList.add("selected-feature");
        });
        newDiv.appendChild(button);
        var deleteButton = document.createElement("button");
        deleteButton.innerHTML = "<i class=\"fas fa-trash-alt\"></i>";
        deleteButton.classList.add("delete-button");
        deleteButton.setAttribute("aria-labelledby", myId);
        deleteButton.addEventListener("click", function () {
            that.sendDeleteFeature(this.getAttribute("aria-labelledby"));
        })
        newDiv.appendChild(deleteButton);
        userStoryBoardDiv.appendChild(newDiv);
        userStoryInput.val("");
        beschreibungInput.val("");
    }

    /**
     * lehrt das Board
     */
    clearBoard() {
        userStoryBoardDiv.empty();
    }

    /**
     * setzt den RemoveButton
     */
    setButtonToRevote() {
        let featureButton = $("#select-" + selectetFeature);
        let featureID = selectetFeature;
        $(".selected-feature button.primary").removeClass("voting");
        featureButton.text("Vote again")
        featureButton.click(function () {
            selectetFeature = featureID;
            sendBewertungAgain();

        });
    }

    /**
     * fügt das nächste Feature
     */
    addNextVote(){
        if (stompClient) {
            let message = {
                phase: 'NEXT'
            }
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
        }
        send = true;
    }

    /**
     * Fügt die bewertung der Usersotry hinzu damit die Bewertung auch in der Sidebar zu sehen ist.
     * @param userstory
     */
    addBewertung(userstory) {
        let bewertung

        let name = document.getElementById("selected-feature-name").value;
        let beschreibung = document.getElementById("selected-feature-descr").value;
        let featureId = userstory.id;

        bewertung = [name, beschreibung, featureId];

        if (stompClient) {
            let message = {
                userStories: bewertung,
                roomId: roomId,
                featureId: featureId,
                phase: 'ADDVOTE'
            }
            // stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
        }
        send = true;
    }

    /**
     * Updatet ein bestimmtes Featuer welches als Parameter übergeben wird.
     * @param userStories Diese Usersotry soll geupdatet werden
     */
    updateFeatures(userStories) {
        userStoryBoard.empty();
        userStories.forEach(
            story => {
                this.addToBoard(new UserStory(story.title, story.description, story.id))
            }
        )
    }

    /**
     * entfernt ein Feature aus der Sidebar
     * @param id id des Features
     */
    deleteFeature(id){
        if(selectetFeature === id){
            toggleElement(document.getElementById("greeting"));
            toggleElement(document.getElementById("voting-panel"));
            resetResult();
            resetVotingPanel();
            hideVotes();
            selectetFeature = null;
        }
        document.getElementById(id).remove();
    }

    /**
     * Sendet das aktuelle Feature an den Server unter dem event addFeature, damit der server das Feature speichern
     * kann.
     */
    sendFeature() {
        let name = document.getElementById("userStoryInput").value;
        let beschreibung = document.getElementById("beschreibungInput").value;
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

    /**
     * benachrichtigt denn Server das ein Feature gelöscht wurde
     */
    sendDeleteFeature(featureId){
        let message = {
            id: featureId,
            phase: 'DELETE'
        }
        stompClient.send(`${topic}/addFeature`, {}, JSON.stringify(message));
    }

    /**
     * Wechselt die Ansicht von open-Feature zur feature ansicht bzw. andersrum.
     */
    showOpenInputButton(){
        let open = document.getElementById("open-feature-input-row");
        let close = document.getElementById("feature-input-row");
        open.style.display = "block";
        close.style.display = "none";

    }

    /**
     * Setzt denn Input des Namens und der Beschr4eibung auf ""
     */
    cancelInput() {
        document.getElementById("userStoryInput").value = "";
        document.getElementById("beschreibungInput").value = "";
        let open = document.getElementById("open-feature-input-row");
        let input = document.getElementById("feature-input-row");
        toggleElement(open);
        toggleElement(input);
    }
}


// let addFeatureButton = $('#addFeature');

