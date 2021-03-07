package de.projekt.priorityplanner.model;

import lombok.Data;

@Data
public class Vote {
    private String user;
    private int bewertung1;
    private int bewertung2;
    private int zeit;
    private de.projekt.priorityplanner.model.MessagePhase event;
    private String roll;

    public Vote(String user, int bewertung1, int bewertung2, int zeit, MessagePhase event, String rolle) {
        this.user = user;
        this.bewertung1 = bewertung1;
        this.bewertung2 = bewertung2;
        this.zeit = zeit;
        this.event = event;
        this.roll = rolle;
    }

    public String getUser() {
        return user;
    }

    public int getBewertung1() {
        return bewertung1;
    }

    public int getBewertung2() {
        return bewertung2;
    }

    public int getZeit() {
        return zeit;
    }
}
