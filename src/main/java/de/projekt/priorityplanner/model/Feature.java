package de.projekt.priorityplanner.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;

/**
 * Ein Feature. Ein Feature besteht aus einem Titel, einer Beschreibung, einer
 * Voteliste und aus den Mittelwertuen und Standardabweichung.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @date 14.03.2021
 */
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

    /**
     * Initalisiert das Feature. Titel und Beschreibung werden festgesetzt. Die Mittelwerte und Standardabweichung
     * werten zu beginn auf 0 gesetzt.
     *
     * @param title Der Titel oder Name des Features
     * @param description Eine Beschreibung des Features
     * @param phase Die Phase in der man sich gerade bei der Komunikation befindet.
     */
    public Feature(String title, String description, MessagePhase phase) {
        this.event = phase;
        this.title = title;
        this.description = description;
        this.votes = Collections.synchronizedList(new ArrayList<>());
        this.boostMean = 0;
        this.boostStab = 0;
        this.ripMean = 0;
        this.ripStab = 0;
        this.timeMean = 0;
        this.timeStab = 0;
    }

    /**
     * Fügt der Voteliste ein Vote hinzu.
     *
     * @param vote Eine Bewertung des Features
     */
    public synchronized void addVote(Vote vote) {
        String voteName = vote.getUser();
        /*
        for (Vote voteVonList : votes) {
            if (voteVonList.getUser().equalsIgnoreCase(voteName)) {
                votes.remove(voteVonList);
            }
        }
        */

        votes.removeIf(vote1 -> vote1.getUser().equalsIgnoreCase(voteName));

        votes.add(vote);
    }

    /**
     * Gibt die Liste der Votes zurück.
     * @return Voteliste. Behinhaltet alle Votes des Features
     */
    public List<Vote> getVotes() {
        return votes;
    }

    /**
     * Gibt die Anzahl der Votes zurück
     * @return Anzahl der Votes
     */
    public int getNumberOfVotes() {
        return votes.size();
    }

    /**
     * Vertleicht zwei Features anhand der id.
     * @param id Die Feature Id.
     * @return true wenn die Id gleich ist, sonst false
     */
    public boolean equals(int id) {
        if (this.id == id) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Leert die Voteliste.
     */
    public synchronized void resetVote(){
        votes.clear();
        this.isVoted = false;
    }

    /**
     * Berechnet alle Mittelwerte und Standardabweichungen mit hilfe der Result Klasse.
     */
    public synchronized void calculateResult(){
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
