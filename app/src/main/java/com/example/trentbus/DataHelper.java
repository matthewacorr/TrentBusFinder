package com.example.trentbus;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DataHelper extends SQLiteOpenHelper {

    // https://stackoverflow.com/questions/9109438/how-to-use-an-existing-database-with-an-android-application/9109728#9109728
    // Written by Manoj Fegde

    //The Android's default system path of your application database.
    private static String DB_PATH = "/data/data/com.example.trentbus/databases/";
    // Database Name.
    private static final String DATABASE_NAME = "TrentBus";
    // Database Version.
    private static final int DATABASE_VERSION = 1;


    public Context context;
    static SQLiteDatabase sqliteDataBase;

    /**
     Constructor
     Takes and keeps a reference of the passed context in order to access to the application assets and resources.
     @param context Parameters of super() are    1. Context
                                                 2. Data Base Name.
                                                 3. Cursor Factory.
                                                 4. Data Base Version.
     */
    public DataHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    /**
     Creates a empty database on the system and rewrites it with your own database.
     By calling this method and empty database will be created into the default system path
     of your application so we are gonna be able to overwrite that database with our database.
     */
    public void createDataBase() throws IOException {
        //check if the database exists
        boolean databaseExist = checkDataBase();

        if (databaseExist) {
            // Do Nothing.
        } else {
            this.getWritableDatabase();
            copyDataBase();
        }// end if else dbExist
    } // end createDataBase().

    /**
     Check if the database already exist to avoid re-copying the file each time you open the application.
     @return true if it exists, false if it doesn't
     */
    public boolean checkDataBase() {
        File databaseFile = new File(DB_PATH + DATABASE_NAME);
        return databaseFile.exists();
    }

    /**
     Copies your database from your local assets-folder to the just created empty database in the
     system folder, from where it can be accessed and handled.
     This is done by transferring byte stream.
     */
    private void copyDataBase() throws IOException {
        //Open your local db as the input stream
        InputStream myInput = context.getAssets().open(DATABASE_NAME);
        // Path to the just created empty db
        String outFileName = DB_PATH + DATABASE_NAME;
        //Open the empty db as the output stream
        OutputStream myOutput = new FileOutputStream(outFileName);
        //transfer bytes from the input file to the output file
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        //Close the streams (Don't cross them)
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     This method opens the data base connection.
     First it create the path up till data base of the device.
     Then create connection with data base.
     */
    public void openDataBase() throws SQLException {
        //Open the database
        String myPath = DB_PATH + DATABASE_NAME;
        sqliteDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    /**
      This Method is used to close the data base connection.
     */
    @Override
    public synchronized void close() {
        if (sqliteDataBase != null)
            sqliteDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // No need to write the create table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // No need to write the update table query.
        // As we are using Pre built data base.
        // Which is ReadOnly.
        // We should not update it as requirements of application.
    }
    // End of code from Manoj Fegde
    // ------------------------------------------------------------------------------------------- //


    // ~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~
    // SQL Queries
    // ~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~*~

    // SQL Query to select ALL routes from our "route" table
    public static ArrayList<Route> selectAllFromRoutesTable() {
        String query = "SELECT * FROM route";
        Cursor cursor = sqliteDataBase.rawQuery(query, null);
        // Create a new ArrayList of type route to contain all of the routes and their fields
        ArrayList<Route> routes = new ArrayList<>();
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    // Create the new route with the data fields from each column
                    Route route = new Route(cursor.getInt(0), cursor.getString(1), cursor.getInt(2));
                    routes.add(route);
                } while (cursor.moveToNext());
            }
        }
        return routes;
    }

    // Returns the time the bus left the originating stop
    public static String timeFromStart(int routeID, int stopID) {
        String timeFromStart = "";
        String[] args = {String.valueOf(routeID), String.valueOf(stopID)};
        String query = "SELECT timeFromStart FROM route_stops WHERE routeid =? AND stopid =?";
        Cursor cursor = sqliteDataBase.rawQuery(query, args);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    timeFromStart = cursor.getString(0);
                } while (cursor.moveToNext());
            }
        }
        return timeFromStart;
    }

    // Returns an ArrayList<String> containing the next three scheduled bus times
    public static ArrayList<String> nextThreeBuses(int routeID, String scheduleType, String intermediateTime) {
        ArrayList<String> nextBusList = new ArrayList<>();
        String[] args = {String.valueOf(routeID), scheduleType, intermediateTime};
        String query = "SELECT startTime FROM schedule WHERE routeid =? AND type =? AND startTime >=? LIMIT 3";
        Cursor cursor = sqliteDataBase.rawQuery(query, args);

        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    String bus = cursor.getString(0);
                    nextBusList.add(bus);
                } while (cursor.moveToNext());
            }
        }

        return nextBusList;
    }

    // This method adds the time it takes the bus to reach a given stop,
    // without this method the app will only display the time that the bus leaves the terminal
    public static ArrayList<String> calculateArrivalTime(ArrayList<String> busList, String timeFromStart){
        // ArrayList to hold the calculated values
        ArrayList<String> calculatedArrivalTime = new ArrayList<>();

        // Split the time from start into hh,mm,ss
        String[] timeFromStartArray = timeFromStart.split(":");
        // Used to hold timeFromStart as an integer so we can perform time math
        Integer[] timeFromStartArrayAsInt = new Integer[3];

        // Parse all string values into integers
        for(int index = 0; index < timeFromStartArray.length; index++){
            timeFromStartArrayAsInt[index] = Integer.parseInt(timeFromStartArray[index]);
        }

        // For each time in the busList
        for(int index = 0; index < busList.size(); index++) {
            // Split the time
            String[] time = busList.get(index).split(":");
            int hour = Integer.parseInt(time[0]);
            int minute = Integer.parseInt(time[1]);

            // If the sum of the two minutes is greater than or equal to 60
            if (minute + timeFromStartArrayAsInt[1] > 59){
                // Add one to the hour
                hour+=1;
                // Get the remainder for the minute
                minute = minute + timeFromStartArrayAsInt[1] - 60;
            }
            else {
                // If the sums are less than 60, add the time
                minute += timeFromStartArrayAsInt[1];
            }

            // Since int's under 10 will be one digit, we need to prefix with a zero
            if (minute < 10){
                calculatedArrivalTime.add(hour + ":0" + minute + ":00");
            }
            else {
                calculatedArrivalTime.add(hour + ":" + minute + ":00");
            }
        }
        return calculatedArrivalTime;
    }

    // Associates stopID's to routeID's using a "LEFT JOIN". Results are arranged by order of stops on routes.
    public static ArrayList<Stop> selectFromStopLeftJoinRouteStops (int routeID){
        ArrayList<Stop> stopList = new ArrayList<>();
        String[] args = {String.valueOf(routeID)};
        String query = "SELECT stop.stopID, stop.stopName, stop.latitude, stop.longitude FROM stop LEFT JOIN route_stops ON stop.stopID = route_stops.stopID WHERE routeID = ? ORDER BY route_stops.'order' ASC";
        Cursor cursor = sqliteDataBase.rawQuery(query, args);
        if (cursor.getCount() > 0) {
            if (cursor.moveToFirst()) {
                do {
                    Stop stop = new Stop(cursor.getInt(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3));
                    stopList.add(stop);
                } while (cursor.moveToNext());
            }
        }
        return stopList;
    }
}



