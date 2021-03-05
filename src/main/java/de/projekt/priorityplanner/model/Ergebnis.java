package de.projekt.priorityplanner.model;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;

@Data
public class Ergebnis {
    private List<Feature> mustHave;
    private List<Feature> shouldHave;
    private List<Feature> couldHave;
    private List<Feature> wontHave;

    private List<Feature> allFeature;

    public Ergebnis(Room room){
        mustHave = new LinkedList<>();
        shouldHave = new LinkedList<>();
        couldHave = new LinkedList<>();
        wontHave = new LinkedList<>();

        allFeature = room.getFeatures();

        createMustHaves();
        createShouldHave();
        createCouldHave();
        createWontHave();
    }

    private void createMustHaves(){
        mustHave = allFeature;
    }

    private void createShouldHave(){
        shouldHave = allFeature;
    }

    private void createCouldHave(){
        couldHave = allFeature;
    }

    private void createWontHave(){
        wontHave = allFeature;
    }

}
