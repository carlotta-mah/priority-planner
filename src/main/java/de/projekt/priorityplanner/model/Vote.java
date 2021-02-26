package de.projekt.priorityplanner.model;

public class Vote {
    private String user;
    private int bewertung1;
    private int bewertung2;
    private int zeit;

    public Vote(String user, int bewertung1, int bewertung2, int zeit) {
        this.user = user;
        this.bewertung1 = bewertung1;
        this.bewertung2 = bewertung2;
        this.zeit = zeit;
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
