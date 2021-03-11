package de.projekt.priorityplanner.model;

/**
 * Ein User. Ein User besteht aus einem Namen und einer UserRolle.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
public class User {
    private String name;
    private UserRoll roll;

    /**
     * Inizalisiert den User, indem der Name und die Rolle gesetzt wird.
     * @param name Name des Users
     * @param rolle Rolle des Users
     */
    public User(String name, String rolle){
        this.name = name;
        setUserRoll(rolle);
    }

    /**
     * Setzt die Rolle des Users indem ein passender String übergeben wird
     * @param rolle Rolle des Users als String
     */
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
            case "User Experience":
                this.roll = UserRoll.UserExperience;
                break;
            default:
                this.roll = UserRoll.KeineRolle;
                break;
        }
    }

    /**
     * gibt den Username zurück
     * @return Der Username als String
     */
    public String getName() {
        return name;
    }

    /**
     * gibt die Rolle des Users zurück
     * @return Rolle des Users als UserRoll
     */
    public UserRoll getRoll() {
        return roll;
    }
}
