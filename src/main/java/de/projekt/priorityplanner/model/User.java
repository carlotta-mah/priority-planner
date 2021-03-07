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
            case "Gründer":
                this.roll = UserRoll.Gründer;
                break;
            case "Product Owner":
                this.roll = UserRoll.ProductOwner;
            case "Entwickler":
                this.roll = UserRoll.Entwickler;
                break;
            case "Nutzer":
                this.roll = UserRoll.UserExperience;
                break;
            case "Manager":
                this.roll = UserRoll.Manager;
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
