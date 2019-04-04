package com.example.trentbus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    @Override
    // On creation of MainActivity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Request permission for location data
        requestPermission();

        // Copy an instance of the database and then open it
        setUpDatabase();

        // Call method to dynamically set the spinners
        setSpinners();
    }

    // Requests location permission data
    private void requestPermission(){
        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, 1);
    }

    // Copies the included database to one that the app can manipulate
    private void setUpDatabase() {
        // Create the instance DataHelper
        DataHelper dbHelper = new DataHelper(MainActivity.this);
        try{
            // This copies the database we include in assets to a usable version within the app
            dbHelper.createDataBase();
            // Next we open the database to utilize it
            dbHelper.openDataBase();
        }catch(IOException ioe){
            // If database cannot be cloned or opened, display a toast
            Toast.makeText(getApplicationContext(),"Database Failed to Load",Toast.LENGTH_SHORT).show();
        }
    }

    // setSpinners uses methods that call SQL queries to populate the spinners with items from the database
    // This method also dynamically updates the stop spinner based on the route selection
    public void setSpinners() {
        // Bring the route spinner into scope from the app interface
        Spinner routeSpinner = findViewById(R.id.route_spinner);

        // Create an string array list containing all routes from the database
        ArrayList<String> allRoutes = ListTypeConverter.routeToString(DataHelper.selectAllFromRoutesTable());

        // An array adapter is used to convert an array list to something a spinner can interpret
        ArrayAdapter<String> routeAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, allRoutes);

        // Populate route spinner to use the allRoute adapter
        routeSpinner.setAdapter(routeAdapter);

        // Create the listener for the routeSpinner so we can dynamically update the stop spinner
        routeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            // Called after something is selected in the route spinner
            // Updates stop spinner with stops on the selected route
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Bring the route and stop spinners into scope from the app's interface
                Spinner routeSpinner = findViewById(R.id.route_spinner);
                Spinner stopSpinner = findViewById(R.id.stop_spinner);

                // Create an ArrayList of type route which contains all of the routes in the table
                ArrayList<Route> routeArrayList = DataHelper.selectAllFromRoutesTable();

                // Iterate through the routeArrayList
                // If the selected item in the routeSpinner matches the routeArrayList item at the current index
                for (int index = 0; index < routeArrayList.size(); index++) {
                    if (routeSpinner.getSelectedItem().toString().equals(routeArrayList.get(index).toString())) {

                        // Set the route ID to the corresponding route
                        int routeID = routeArrayList.get(index).getID();

                        // Use the routeID to retrieve all stops belonging to that route using a SQL "Left Join"
                        // The associates the routeID from the route table to all stops containing that routeID
                        ArrayList<Stop> availableStopsOnRoute = DataHelper.selectFromStopLeftJoinRouteStops(routeID);

                        // Call method that returns the closest stops to the user if they allow location access
                        availableStopsOnRoute = showClosestStops(availableStopsOnRoute);

                        // Convert the stops along route to a string to be used in ArrayAdapter
                        ArrayList<String> stopsAlongRouteAsString = ListTypeConverter.stopToString(availableStopsOnRoute);

                        // Create an ArrayAdapter with all the stop names so that the spinner can interpret it
                        ArrayAdapter<String> availableStopAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_dropdown_item, stopsAlongRouteAsString);

                        // Populate stopSpinner with availableStopAdapter
                        stopSpinner.setAdapter(availableStopAdapter);
                    }
                }
            }

            // Log if listener failed (For debugging purposes)
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.i("TrentBus Log", "Listener returned nothing selected");
            }
        });
    }

    // Called when the Submit Button is pressed
    public void submitData(View view) {

        // Create the intent for the second activity - This acts as a container to pass values to the second activity
        Intent secondActivityIntent = new Intent(this, SecondActivity.class);

        // Bring the route and stop spinners into scope from the app's interface
        Spinner routeSpinner = findViewById(R.id.route_spinner);
        Spinner stopSpinner = findViewById(R.id.stop_spinner);

        // Verify both spinners are populated before proceeding
        if (routeSpinner.getSelectedItem() != null && stopSpinner.getSelectedItem() != null) {

            // Create variables to store selected stop and route ID
            int selectedRouteID = 0;
            int selectedStopID = 0;

            // Retrieve the selected route from the spinner
            String selectedRoute = routeSpinner.getSelectedItem().toString();

            // Create an ArrayList of all routes
            ArrayList<Route> routeArrayList = DataHelper.selectAllFromRoutesTable();

            // Iterate through the list of all stops
            for (int index = 0; index < routeArrayList.size(); index++) {
                // If the selected item in the route spinner matches the name of the route at the current index
                if (selectedRoute.equals(routeArrayList.get(index).getName())) {
                    // If they match, set the selectedRouteID to the the ID of the route at the current index
                    selectedRouteID = routeArrayList.get(index).getID();
                }
            }

            // Using a left join, create a list of stops along the selected route
            ArrayList<Stop> stopsAlongRoute = DataHelper.selectFromStopLeftJoinRouteStops(selectedRouteID);

            // Goes through all of the stops along the selected route
            // If the selected stop in the spinner is equal to the current stop name, retrieve the ID
            for (int index = 0; index < stopsAlongRoute.size(); index++) {
                if (stopSpinner.getSelectedItem().equals(stopsAlongRoute.get(index).getName())){
                    selectedStopID = stopsAlongRoute.get(index).getID();
                }
            }

            // LBYL - Check that the route ID and stop ID have been set to a valid ID (1-n)
            if (selectedRouteID != 0 && selectedStopID != 0) {
                // Load values into Intent and start second activity
                secondActivityIntent.putExtra("routeID", selectedRouteID);
                secondActivityIntent.putExtra("stopID", selectedStopID);
                startActivity(secondActivityIntent);
            }
        }
        // If one spinner has not been filled notify the user
        else{
            Toast.makeText(getApplicationContext(),"Select a route AND stop!",Toast.LENGTH_SHORT).show();
        }
    }

    // Calculates the distance between the user and returns a sorted list of closest stops if granted location access
    public ArrayList<Stop> showClosestStops(ArrayList<Stop> availableStopsOnRoute){
        // Check if we have access to location information
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == 0){

            // Create a location manager to be used.
            LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

            // Create a location object to get last known location
            Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            // If user has given permission, but they have no lastKnownLocation (ie. it's null), exit the method prematurely.
            if (location == null){ return availableStopsOnRoute; }

            //Set variables to last known latitude and longitude
            double longitude = location.getLongitude();
            double latitude = location.getLatitude();

            // Create location objects
            Location stopLocation = new Location("");
            Location userLocation = new Location("");

            // Set the users location using their coordinates
            userLocation.setLatitude(latitude);
            userLocation.setLongitude(longitude);

            // For each stop in the ArrayList...
            for(int index = 0; index < availableStopsOnRoute.size(); index++) {
                // Set the stop location to the latitude and longitude of the current stop object
                stopLocation.setLatitude(availableStopsOnRoute.get(index).getLatitude());
                stopLocation.setLongitude(availableStopsOnRoute.get(index).getLongitude());

                // calculate distance from user
                float distanceFromUser = userLocation.distanceTo(stopLocation);

                // Set distance from user at the current stop
                availableStopsOnRoute.get(index).setDistancefromUser(distanceFromUser);
            }

            // Sort the availableStopsOnRoute by distance from user.
            Collections.sort(availableStopsOnRoute);

            // Return the now-sorted ArrayList
            return availableStopsOnRoute;
        // If we don't have access to user's location then return the ArrayList unchanged.
        } else {
            return availableStopsOnRoute;
        }
    }
}

