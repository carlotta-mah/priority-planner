package de.projekt.priorityplanner.model;

public class User {
    private String name;
    private UserRoll roll;

    public User(String name, String rolle){
        this.name = name;
        setUserRoll(rolle);
    }

    private void setUserRoll(String rolle){
        switch(rolle){
            case "Verantwortlicher":
                this.roll = UserRoll.Verantwortlicher;
                break;
            case "Entwickler":
                this.roll = UserRoll.Entwickler;
                break;
            case "Nutzer":
                this.roll = UserRoll.Nutzer;
                break;
            case "Marketing":
                this.roll = UserRoll.Marketing;
                break;
            default:
                this.roll = UserRoll.KeineRolle;
                break;
        }
    }

    public String getName() {
        return name;
    }

    public UserRoll getRoll() {
        return roll;
    }
}
