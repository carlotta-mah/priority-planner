package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Data
public class Feature {
    private String title;
    private String description;
    private de.projekt.priorityplanner.model.MessagePhase event;
    private List<Vote> votes;
    private int id;
    private float boostMean;
    private float boostStab;
    private float ripMean;
    private float ripStab;
    private float timeMean;
    private float timeStab;
    private Boolean isVoted = false;

    public Feature(String title, String description, MessagePhase phase) {
        this.event = phase;
        this.title = title;
        this.description = description;
        this.votes = new LinkedList<>();
        this.boostMean = 0;
        this.boostStab = 0;
        this.ripMean = 0;
        this.ripStab = 0;
        this.timeMean = 0;
        this.timeStab = 0;
    }

    public void addVote(Vote vote) {
        String voteName = vote.getUser();
        for (Vote voteVonList : votes) {
            if (voteVonList.getUser().equalsIgnoreCase(voteName)) {
                votes.remove(voteVonList);
            }
        }
        votes.add(vote);
    }

    public List<Vote> getVotes() {
        return votes;
    }

    public int getNumberOfVotes() {
        return votes.size();
    }

    public boolean equals(int id) {
        if (this.id == id) {
            return true;
        } else {
            return false;
        }
    }

    public void resetVote(){
        votes.clear();
        this.isVoted = false;
    }


    public void calculateResult(){
        Result res = new Result(votes);
        this.boostMean = res.boostMean;
        this.boostStab = res.boostStab;
        this.ripMean = res.ripMean;
        this.ripStab = res.ripStab;
        this.timeMean = res.timeMean;
        this.timeStab = res.timeStab;
        this.isVoted = true;
    }


}
