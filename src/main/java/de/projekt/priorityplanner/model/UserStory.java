package de.projekt.priorityplanner.model;

public class UserStory {
    private String name;
    private String beschreibung;
    private int value1;
    private int value2;
    private int zeit;

    public UserStory(String name, String beschreibung, int value1, int value2, int zeit) {
        this.name = name;
        this.beschreibung = beschreibung;
        this.value1 = value1;
        this.value2 = value2;
        this.zeit = zeit;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public int getValue1() {
        return value1;
    }

    public void setValue1(int value1) {
        this.value1 = value1;
    }

    public int getValue2() {
        return value2;
    }

    public void setValue2(int value2) {
        this.value2 = value2;
    }

    public int getZeit() {
        return zeit;
    }

    public void setZeit(int zeit) {
        this.zeit = zeit;
    }
}