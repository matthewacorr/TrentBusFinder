package com.example.trentbus;

// Stop object is used to hold data types from each row in the stop table from the database
public class Stop implements Comparable<Stop>{

    // Data Fields
    private int ID;
    private String name;
    private Double latitude;
    private Double longitude;
    private float distancefromUser;

    // Constructor
    public Stop(int ID, String name, Double latitude, Double longitude){
        this.setID(ID);
        this.setName(name);
        this.setLatitude(latitude);
        this.setLongitude(longitude);
    }

    // Getters
    public int getID() { return ID; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getName() { return name; }
    public float getDistanceFromUser() { return distancefromUser; }

    // Setters
    public void setID(int ID) { this.ID = ID; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public void setName(String name) { this.name = name; }
    public void setDistancefromUser(float distancefromUser) {this.distancefromUser = distancefromUser; }

    @Override
    public String toString(){ return this.getName(); }

    // Used to sort distances between the user and the stop
    @Override
    public int compareTo(Stop stop){
        // Comparing this stop object and stop passed as parameter
        if(this.getDistanceFromUser() < stop.getDistanceFromUser()){
            return -1;
        } else if(this.getDistanceFromUser() == stop.getDistanceFromUser()){
            return 0;
        } else {
            return 1;
        }
    }
}