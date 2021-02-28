package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Feature {
    private String title;
    private String description;
    private de.projekt.priorityplanner.model.MessagePhase event;
    private List<Vote> votes;
    private int id;

    public Feature(String title, String description, MessagePhase phase){
        this.event = phase;
        this.title = title;
        this.description = description;
        this.votes = new LinkedList<>();
    }

    public void addVote(Vote vote){
        String voteName = vote.getUser();
        for (Vote voteVonList:votes) {
            if(voteVonList.getUser().equalsIgnoreCase(voteName)){
                votes.remove(voteVonList);
            }
        }
        votes.add(vote);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public int getNumberOfVotes(){
        return votes.size();
    }
    public boolean equals(String name, String beschreibung){
        if(this.title.equalsIgnoreCase(name) && this.description.equalsIgnoreCase(beschreibung)){
            return true;
        }else {
            return false;
        }
    }

}
