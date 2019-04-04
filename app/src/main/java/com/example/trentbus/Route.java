package com.example.trentbus;

public class Route {

    // Data Fields
    private int ID;
    private String name;
    private int startLocation;

    // Constructor
    public Route(int ID, String name, int startLocation){
        this.setID(ID);
        this.setName(name);
        this.setStartLocation(startLocation);
    }
    
    // Getters
    public int getID() { return ID; }
    public String getName() { return name; }
    public int getStartLocation() { return startLocation; }
    
    // Setters
    public void setName(String name) { this.name = name; }
    public void setID(int ID) { this.ID = ID; }
    public void setStartLocation(int startLocation) { this.startLocation = startLocation; }

    @Override
    public String toString(){ return getName(); }
}
