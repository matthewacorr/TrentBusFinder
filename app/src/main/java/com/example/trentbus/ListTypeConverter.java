package com.example.trentbus;

import java.util.ArrayList;

public class ListTypeConverter {

    // Convert's ArrayLists of type Stop to an ArrayList of type String
    public static ArrayList<String> stopToString(ArrayList<Stop> stopArrayList) {
        // Initialize the ArrayList of type string to hold the stopIDs
        ArrayList<String> stringArrayList = new ArrayList<String>();
        // Loop through the stop ArrayList
        for (int index = 0; index < stopArrayList.size(); index++) {
            // Add to the new ArrayList
            stringArrayList.add(stopArrayList.get(index).toString());
        }
        return stringArrayList;
    }

    // Convert's ArrayLists of type Route to an ArrayList of type String
    public static ArrayList<String> routeToString(ArrayList<Route> routeArrayList) {
        // Initialize ArrayList of type String to hold the Routes
        ArrayList<String> stringArrayList = new ArrayList<String>();
        // Loop through the routeArrayList
        for (int index = 0; index < routeArrayList.size(); index++) {
            // Add to the new ArrayList
            stringArrayList.add(routeArrayList.get(index).toString());
        }
        return stringArrayList;
    }
}
