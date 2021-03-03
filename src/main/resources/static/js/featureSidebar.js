let addFeatureButton = $('#addFeature');
let userStoryBoard = $('#userStoryBoard');
let addButton = $('#addToVoteButton');
let userStoryInput =$('#userStoryInput');
let beschreibungInput =$('#beschreibungInput')
let selectetFeature;

class featureSidebar {

    constructor(sidebar) {
        sidebar.style.backgroundColor = "red";
        addFeatureButton.click(this.sendFeature);
        //let test = document.createElement("p");
        //test.innerText = "TEST";
        //sidebar.appendChild(test);

    }

    unselectAllFeatures() {
        let featureList = document.getElementsByClassName("featureList");
        Array.prototype.forEach.call(featureList, function(featureElement) {
            featureElement.classList.remove("selected-feature");
        });
    }

    select(featureId){
        selectetFeature = featureId;
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
            document.getElementById("featureBewertung").value = divName;
            document.getElementById("beschrebungBewertung").value = divBeschreibung;
            //unselectAllFeatures();
            newDiv.classList.add("selected-feature");
        });
        newDiv.appendChild(button);

        userStoryBoardDiv.appendChild(newDiv);
        userStoryInput.val("");
        beschreibungInput.val("");
    }



    addBewertung(userstory) {
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
            // stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
            stompClient.send(`${topic}/sendMessage`, {}, JSON.stringify(message));
        }
        send = true;
    }

    updateFeatures(userStories) {
        userStoryBoard.empty();
        userStories.forEach(
            story => this.addToBoard(new UserStory(story.title, story.description, story.id, 0, 0))
        )
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



}



// let addFeatureButton = $('#addFeature');
