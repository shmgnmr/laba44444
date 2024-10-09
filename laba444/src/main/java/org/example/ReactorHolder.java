package org.example;

import java.util.LinkedHashMap;


public class ReactorHolder {


    private final LinkedHashMap<String, LinkedHashMap<String, Double>> reactorsWithConsumption;


    public ReactorHolder() {
        this.reactorsWithConsumption = new LinkedHashMap<>();
    }

    public void addConsumption(String reactorname, String year, double consumption) {

        reactorsWithConsumption.putIfAbsent(reactorname, new LinkedHashMap<>());


        reactorsWithConsumption.get(reactorname).put(year, consumption);
    }


    public LinkedHashMap<String, Double> getReactorData(String reactorName) {
        return (LinkedHashMap<String, Double>) reactorsWithConsumption.get(reactorName);
    }


}