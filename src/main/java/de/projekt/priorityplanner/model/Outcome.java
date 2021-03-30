package de.projekt.priorityplanner.model;

import lombok.Data;
import java.util.LinkedList;
import java.util.List;
/**
 * Ein Ergebnis teilt alle Features eines Raumes auf die Listen mustHave, shouldHave, couldHave und WontHave auf.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */

@Data
public class Outcome {
    private List<Feature> mustHave;
    private List<Feature> shouldHave;
    private List<Feature> couldHave;
    private List<Feature> wontHave;

    private List<Feature> allFeature;

    /**
     * Initialisiert ein Ergebnis, indem die Features auf die Listen mustHave, shouldHave, coulHave und WontHave
     * aufgeteilt werten.
     *
     * @param room Der Raum von dem das Ergebnis ertellt werden soll.
     */
    public Outcome(Room room) {
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

    /**
     * Fügt die passenden Features der MustHave Liste hinzu.
     */
    private void createMustHaves() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() > 80 && feature.getRipMean() <= 100) {
                mustHave.add(feature);
            } else if (feature.getRipMean() > 70 && feature.getRipMean() <= 80 && feature.getBoostMean() > 80) {
                mustHave.add(feature);
            }
        }
    }

    /**
     * Fügt die passenden Features der ShouldHave Liste hinzu.
     */
    private void createShouldHave() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() <= 80 && feature.getRipMean() > 70 && feature.getBoostMean() <= 80) {
                shouldHave.add(feature);
            } else if (feature.getRipMean() <= 70 && feature.getRipMean() > 50 && feature.getBoostMean() > 50) {
                shouldHave.add(feature);
            }
        }
        float gesamtAufwand = 0;
        for (Feature feature1 : mustHave) {
            gesamtAufwand = feature1.getTimeMean() + gesamtAufwand;
        }
        for (Feature feature2 : shouldHave) {
            gesamtAufwand = feature2.getTimeMean() + gesamtAufwand;
        }
        int aufwandsmarke = Math.round((gesamtAufwand / 100) * 10);

        for (Feature feature3 : allFeature) {
            if (feature3.getRipMean() <= 50 && feature3.getRipMean() > 30 && feature3.getBoostMean() > 50
                    && feature3.getTimeMean() < aufwandsmarke) {
                shouldHave.add(feature3);
            }
        }

    }

    /**
     * Fügt die passenden Features der CouldHave Liste hinzu.
     */
    private void createCouldHave() {
        float gesamtAufwand = 0;
        for (Feature feature1 : mustHave) {
            gesamtAufwand = feature1.getTimeMean() + gesamtAufwand;
        }
        for (Feature feature2 : shouldHave) {
            gesamtAufwand = feature2.getTimeMean() + gesamtAufwand;
        }
        int aufwandsmarke = Math.round((gesamtAufwand / 100) * 10);


        for (Feature feature : allFeature) {
            if (feature.getRipMean() <= 70 && feature.getRipMean() > 50 && feature.getBoostMean() <= 50) {
                couldHave.add(feature);
            } else if (feature.getRipMean() <= 30 &&  feature.getBoostMean() > 50) {
                couldHave.add(feature);
            }else if (feature.getRipMean() <= 50 && feature.getRipMean() > 30 && feature.getBoostMean() > 50
                    && feature.getTimeMean() >= aufwandsmarke){
                couldHave.add(feature);
            }
        }
    }

    /**
     * Fügt die passenden Features der WontHave Liste hinzu.
     */
    private void createWontHave() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() <= 50 && feature.getBoostMean() <= 50 && feature.getRipMean() >=0) {
                wontHave.add(feature);
            }
        }
    }

}
