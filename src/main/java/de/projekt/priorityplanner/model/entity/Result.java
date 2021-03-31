package de.projekt.priorityplanner.model.entity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Die Klasse Result bestimmt die Mittelwerte und Standardabweichungen der Voteliste.
 *
 * @author Mia Mahncke, Nedim Seroka
 * @data 14.03.2021
 */
public class Result {
    public float boostMean;
    public float boostStab;
    public float ripMean;
    public float ripStab;
    public float timeMean;
    public float timeStab;
    private List<Vote> votes;

    /**
     * Bestimmt die Mittelwerte und Standardabweichungen anhade der Voteliste die als Parameter übergeben wird.
     * @param votes Eine Voteliste
     */
    public Result(List votes){
        this.votes = votes;
        setBoostMittel();
        setRipMittel();
        setTimeMittel();

        setBoostStabb();
        setRipStabb();
        setTimeStabb();
    }

    /**
     * Setzt den Mittelwerte der Boostfaktoren anhand der Voteliste.
     */
    private void setBoostMittel() {
        List<Integer> boostUX = new LinkedList<Integer>();
        List<Integer> boostEntwickler = new LinkedList<Integer>();
        List<Integer> boostManager = new LinkedList<Integer>();
        int zaelerUx = 0;
        int zaelerEntwickler = 0;
        int zaelerManager = 0;

        for (Vote vote : votes) {
            if(vote.getRoll().equalsIgnoreCase("Gründer")
                    ||vote.getRoll().equalsIgnoreCase("Product Owner")
                    ||vote.getRoll().equalsIgnoreCase("Manager")){
                boostManager.add(vote.getBewertung1());
                zaelerManager++;
            }else if(vote.getRoll().equalsIgnoreCase("Entwickler")) {
                boostEntwickler.add(vote.getBewertung1());
                zaelerEntwickler++;
            }else if(vote.getRoll().equalsIgnoreCase("User Experience")) {
                boostUX.add(vote.getBewertung1());
                zaelerUx++;
            }
        }

        int mittelUx = -1;
        if(zaelerUx != 0){
            mittelUx = sum(boostUX)/zaelerUx;
        }
        int mittelEntw = -1;
        if(zaelerEntwickler != 0){
            mittelEntw = sum(boostEntwickler)/zaelerEntwickler;
        }
        int mittelManager = -1;
        if(zaelerManager != 0){
            mittelManager = sum(boostManager)/zaelerManager;
        }

        if(mittelUx == -1 ){
            boostMean = 0.33F * mittelEntw + 0.67F * mittelManager;
            if(mittelEntw == -1){
                boostMean = mittelManager;
            }
            if (mittelManager == -1 ){
                boostMean = mittelEntw;
            }

        } else if (mittelEntw == -1 ){
            boostMean = 0.33F * mittelUx + 0.67F * mittelManager;

            if (mittelManager == -1 ){
                boostMean = mittelUx;
            }

        }else if (mittelManager == -1 ){
            boostMean = 0.5F * mittelUx + 0.5F * mittelEntw;
        }else {
            boostMean = 0.25F * mittelUx + 0.25F * mittelEntw + 0.5F * mittelManager;
        }
        boostMean = round(boostMean,2);
    }

    /**
     * Setzt den Mittelwerte der Survivalfactoren anhand der Voteliste.
     */
    private void setRipMittel() {
        List<Integer> ripUx = new LinkedList<Integer>();
        List<Integer> ripEntwickler = new LinkedList<Integer>();
        List<Integer> ripManager = new LinkedList<Integer>();
        int zaelerUx = 0;
        int zaelerEntwickler = 0;
        int zaelerManager = 0;

        for (Vote vote : votes) {
            if(vote.getRoll().equalsIgnoreCase("Gründer")
                    ||vote.getRoll().equalsIgnoreCase("Product Owner")
                    ||vote.getRoll().equalsIgnoreCase("User Experience")){
                ripUx.add(vote.getBewertung2());
                zaelerUx++;
            }else if(vote.getRoll().equalsIgnoreCase("Entwickler")) {
                ripEntwickler.add(vote.getBewertung2());
                zaelerEntwickler++;
            }else if(vote.getRoll().equalsIgnoreCase("Manager")) {
                ripManager.add(vote.getBewertung2());
                zaelerManager++;
            }
        }

        int mittelUx = -1;
        if(zaelerUx != 0){
            mittelUx = sum(ripUx)/zaelerUx;
        }
        int mittelEntw = -1;
        if(zaelerEntwickler != 0){
            mittelEntw = sum(ripEntwickler)/zaelerEntwickler;
        }
        int mittelManager = -1;
        if(zaelerManager != 0){
            mittelManager = sum(ripManager)/zaelerManager;
        }




        if(mittelUx == -1 ){
            ripMean = 0.5F * mittelEntw + 0.5F * mittelManager;
            if(mittelEntw == -1){
                ripMean = mittelManager;
            }
            if (mittelManager == -1 ){
                ripMean = mittelEntw;
            }

        } else if (mittelEntw == -1 ){
            ripMean = 0.67F * mittelUx + 0.33F * mittelManager;

            if (mittelManager == -1 ){
                ripMean = mittelUx;
            }

        }else if (mittelManager == -1 ){
            ripMean = 0.67F * mittelUx + 0.33F * mittelEntw;
        }else {
            ripMean = 0.5F * mittelUx + 0.25F * mittelEntw + 0.25F * mittelManager;
        }
        ripMean = round(ripMean,2);
    }

    /**
     * Setzt den Mittelwerte der Zeitangaben anhand der Voteliste.
     */
    private void setTimeMittel() {
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

    /**
     * Setzt die Standardabweichung von den Boostfacktoren anhand der Voteliste.
     */
    private void setBoostStabb() {
        int[] boostList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            boostList[i] = votes.get(i).getBewertung1();
        }
        float mittelwert = Arrays.stream(boostList).sum() / boostList.length;
        float devSum= 0;
        for(int i= 0; i< boostList.length; i++) {
            devSum+= Math.pow(boostList[i] -mittelwert, 2);
        }
        boostStab =  round((float) Math.sqrt(devSum/ (boostList.length )),2);
    }
    /**
     * Setzt die Standardabweichung von den Survivalfactoren anhand der Voteliste.
     */
    private void setRipStabb() {
        int[] ripList = new int[votes.size()];
        for (int i = 0; i < votes.size(); i++) {
            ripList[i] = votes.get(i).getBewertung2();
        }
        float mittelwert = Arrays.stream(ripList).sum() / ripList.length;
        float devSum= 0;
        for(int i= 0; i< ripList.length; i++) {
            devSum+= Math.pow(ripList[i] -mittelwert, 2);
        }
        ripStab = round((float) Math.sqrt(devSum/ (ripList.length )),2);
    }

    /**
     * Setzt die Standardabweichung von den Zeiten anhand der Voteliste.
     */
    private void setTimeStabb() {
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

    /**
     * Summiert eine Liste.
     * @param list Eine Liste die Summiert werden soll.
     * @return Die Summe als Integer
     */
    public int sum(List<Integer> list) {
        int sum = 0;

        for (int i : list)
            sum = sum + i;

        return sum;
    }

    /**
     * Rundet ein Float auf eine bestimmte Nachkommerstelle.
     *
     * @param number Die Floatzahl die gerundet werden soll.
     * @param decimalPlace Anzahl der Nachkommerstellen
     * @return gerundeter Float
     */
    public static float round(float number, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(number));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }

}
