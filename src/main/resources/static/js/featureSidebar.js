let addFeatureButton = $('#addFeature');
let userStoryBoard = $('#userStoryBoard');
let addButton = $('#addToVoteButton');
let userStoryInput = $('#userStoryInput');
let beschreibungInput = $('#beschreibungInput')
let openInputButton = $("#open-feature-input")
let selectetFeature;

class featureSidebar {

    constructor(sidebar) {
        addFeatureButton.click(this.sendFeature);
        openInputButton.click(this.toggleFeatureInput);
        document.getElementById("feature-input-row").style.display = "none"

    }

    toggleFeatureInput() {
        let open = document.getElementById("open-feature-input-row");
        let input = document.getElementById("feature-input-row");
        toggleElement(open);
        toggleElement(input);
    }



    unselectAllFeatures() {
        let featureList = document.getElementsByClassName("featureList");
        Array.prototype.forEach.call(featureList, function (featureElement) {
            featureElement.classList.remove("selected-feature");
        });

    }

    select(featureId) {
        if(selectetFeature==null){
            toggleElement(document.getElementById("greeting"))
            toggleElement(document.getElementById("voting-panel"))
        }
        selectetFeature = featureId;
        document.getElementById("" + featureId).classList.add("selected-feature");
        document.getElementById("" + featureId).classList.remove("wurdeBewertet");
    }

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
        deleteButton.innerText ="delete";
        deleteButton.setAttribute("aria-labelledby", myId);
        deleteButton.addEventListener("click", function (){
            that.sendDeleteFeature(this.getAttribute("aria-labelledby"));
        })
        newDiv.appendChild(deleteButton);
        userStoryBoardDiv.appendChild(newDiv);
        userStoryInput.val("");
        beschreibungInput.val("");
    }
    clearBoard() {
        userStoryBoardDiv.empty();
    }

    setButtonToRevote() {
        let identifier = "#"+selectetFeature;
        let featureButton = $("#select-" + selectetFeature );
        featureButton.html("Vote again")
        featureButton.click(sendBewertungAgain);
    }


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

    updateFeatures(userStories) {
        userStoryBoard.empty();
        userStories.forEach(
            story => {
                this.addToBoard(new UserStory(story.title, story.description, story.id))
            }
        )
    }
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
    sendDeleteFeature(featureId){
        let message = {
            id : featureId,
            phase: 'DELETE'
        }
        stompClient.send(`${topic}/addFeature`, {}, JSON.stringify(message));
    }


}


// let addFeatureButton = $('#addFeature');

