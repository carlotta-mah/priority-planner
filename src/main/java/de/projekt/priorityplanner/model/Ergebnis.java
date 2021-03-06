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

    public Ergebnis(Room room) {
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

    private void createMustHaves() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() <= 20 && feature.getRipMean() >= 0) {
                mustHave.add(feature);
            }
        }
    }

    private void createShouldHave() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() >= 21 && feature.getRipMean() <= 30) {
                shouldHave.add(feature);
            } else if (feature.getRipMean() >= 31 && feature.getRipMean() <= 40 && feature.getBoostMean() < 50) {
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
            if (feature3.getRipMean() > 40 && feature3.getRipMean() <= 60 && feature3.getBoostMean() > 50
                    && feature3.getTimeMean() < aufwandsmarke) {
                shouldHave.add(feature3);
            }
        }

    }

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
            if (feature.getRipMean() >= 41 && feature.getRipMean() <= 60 && feature.getBoostMean() > 50
            && aufwandsmarke <= feature.getTimeMean()) {
                couldHave.add(feature);
            } else if (feature.getRipMean() >= 61 &&  feature.getBoostMean() > 50) {
                couldHave.add(feature);
            }else if (feature.getBoostMean() <= 50 && feature.getRipMean() > 30 && feature.getRipMean() <= 40){
                couldHave.add(feature);
            }
        }
    }

    private void createWontHave() {
        for (Feature feature : allFeature) {
            if (feature.getRipMean() > 40 && feature.getBoostMean() <= 50) {
                wontHave.add(feature);
            }
        }
    }

}
