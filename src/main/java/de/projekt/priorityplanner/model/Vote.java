package de.projekt.priorityplanner.model;

import lombok.Data;

/**
 * Ein Vote. Ein Vote besteht aus einem Username als String, drei bewertungs Faktoren (bewertung 1, bewertung2, zeit)
 * und der Rolle des Users
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
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

    /**
     * gibt den Usernamen zurück der gevotet hat
     * @return name des Users
     */
    public String getUser() {
        return user;
    }

    /**
     * gibt den Boostfaktor zurück (erster Bewertungspunkt)
     * @return Den Boostfaktor
     */
    public int getBewertung1() {
        return bewertung1;
    }

    /**
     * gibt den Survivalfactor zurück (zweiter Bewertungspunkt)
     * @return Den Survivalfactor
     */
    public int getBewertung2() {
        return bewertung2;
    }

    /**
     * Gibt die geschätzt zeit wieder
     * @return Die Zeit
     */
    public int getZeit() {
        return zeit;
    }
}
