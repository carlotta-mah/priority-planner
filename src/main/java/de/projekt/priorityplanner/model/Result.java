package de.projekt.priorityplanner.model;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Result {
    public float boostMean;
    public float boostStab;
    public float ripMean;
    public float ripStab;
    public float timeMean;
    public float timeStab;
    private List<Vote> votes;

    public Result(List votes){
        this.votes = votes;
        setBoostMittel();
        setRipMittel();
        setTimeMittel();

        setBoostStabb();
        setRipStabb();
        setTimeStabb();
    }

    public void setBoostMittel() {
        int[] boostList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            boostList[i] = votes.get(i).getBewertung1();
        }
        boostMean = Arrays.stream(boostList).sum() / boostList.length;
    }

    public void setRipMittel() {
        int[] ripList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            ripList[i] = votes.get(i).getBewertung2();
        }
        ripMean = Arrays.stream(ripList).sum() /ripList.length;
    }

    public void setTimeMittel() {
        int gesamt = 0;
        int z = 0;
        for (int i = 0; i < votes.size(); i++) {
            if(votes.get(i).getZeit() != 0) {
                gesamt = gesamt + votes.get(i).getZeit();
                z++;
            }
        }
        if(z==0){
            timeMean = gesamt;
        }else {
            timeMean = gesamt / z;
        }
    }

    public void setBoostStabb() {
        int[] boostList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            boostList[i] = votes.get(i).getBewertung1();
        }
        float mittelwert = Arrays.stream(boostList).sum() / boostList.length;
        float devSum= 0;
        for(int i= 0; i< boostList.length; i++) {
            devSum+= Math.pow(boostList[i] -mittelwert, 2);
        }
        boostStab =  (float) Math.sqrt(devSum/ (boostList.length ));
    }

    public void setRipStabb() {
        int[] ripList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            ripList[i] = votes.get(i).getBewertung2();
        }
        float mittelwert = Arrays.stream(ripList).sum() / ripList.length;
        float devSum= 0;
        for(int i= 0; i< ripList.length; i++) {
            devSum+= Math.pow(ripList[i] -mittelwert, 2);
        }
        ripStab = (float) Math.sqrt(devSum/ (ripList.length ));
    }

    public void setTimeStabb() {
        List<Integer> timeList = new LinkedList<Integer>();
        int gesamt = 0;
        int z = 0;
        for (int i = 0; i < votes.size(); i++) {
            if(votes.get(i).getZeit() != 0) {
                gesamt = gesamt + votes.get(i).getZeit();
                timeList.add(votes.get(i).getZeit());
                z++;
            }
        }
        float mittelwert;
        if(z == 0) {
             mittelwert = gesamt;
        }else {
             mittelwert = gesamt / z;
        }
        float devSum= 0;
        for(int i= 0; i< z; i++) {
            devSum+= Math.pow(timeList.get(i) -mittelwert, 2);
        }

        if(z == 0){
           timeStab = (float) Math.sqrt(devSum);
        }else {
            timeStab =  (float) Math.sqrt(devSum/ (z ));
        }

    }

}
