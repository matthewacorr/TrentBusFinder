# Trent Bus Application

##### Created by *Matthew Corr*, *Nicolas Barnes* and *Nathan McBride* for COIS 2240



#### Installation Guide

- Our application is not on the Play Store.... yet.

- There are two ways you can install our project

  1. - Enable Developer Options by going to `Settings > About Phone > Software Information` and then tap on your `Build Number` five times. `Developer Options` should now be visible from your Settings

     - Go into `Developer Options` and enable `USB Debugging`

     - Plug your phone into the computer, accept the `Allow USB Debugging` prompt, now when you click run in android studio it should build the application and then install the `.apk` file onto your phone

       

  2. - Plug phone into computer

     - If you receive a prompt asking for data permission on your device hit `Allow`

     - Go to the File Explorer on your computer and drag the `TrentBusFinder.apk` into your phones local storage

     - Back on your phone, open your Android file manager and navigate to the folder containing the `TrentBusFinder.apk` and click it

     - This will prompt you to `Allow unknown apps from this application`, press OK

     - This should bring you to the Android package manager, where you can click install

     - If you receive a "Play Protect" warning, you can dismiss it and install anyway

       

####  Classes and Methods 

- `MainActivity.java`
  The first activity of the application, this is where the user selects a stop and route

  - `onCreate()`

    - Runs when the main activity is created (When the application is opened)
    - Calls `requestPermission()`, `setUpDataBase()` and `setSpinners()`

  - `requestPermission()`

    - This method displays a window which prompts the user if they would like to allow permission to the phones location information

  - `setUpDatabase()`

    - This method calls the functions `createDatabase()` and `openDatabase()` from `DataHelper.java` which create and open a copy of our database for use within the application

  - `setSpinners()`

    - Responsible for populating the drop down spinners for selecting route and stop from the user
    - Database is queried for the selected route and then populates all of the stops along that route into the second spinner using a `onItemSelected` listener

  - `submitData(View view)`

    - Method called when the submit button is pressed on the main activity
    - It creates an intent for the second activity and passes the selected route ID and selected stop ID to the second activity using `putExtra()`

  - `showClosestStops(ArrayList<Stop> availableStopsOnRoute)`

    - Uses a `LocationManager` to retrieve the latitude and the longitude of the device.
    - It then loops through all of the available stops and uses the `distanceTo()` function to calculate the distance in meters to each stop from your current position.
    - Those distances are then sorted in order of smallest to largest

    

- `SecondActivity.java`
  This is the second activity of the application this activity uses a lot of methods to calculate and display the next bus times with the `routeID` and `stopID` retrieved from `MainActivity.java`

  - `onCreate()`

    - Created when the user presses the "Submit" button on the first activity
    - Retrieves the `stopID` and `routeID` that was sent in the previous activity
    - Uses `timeFromStart(routeID, stopID)` to find the time the bus left the initial bus stop
    - Then uses `intermediateTime(timeFromStart)` to find the amount of time since the bus left the station
    - Calls `getDayOfWeek` to find out what schedule to use
    - Once we have all that information, `nextThreeBuses(route,day,intermediateTime)` is called to retrieve the next three buses that will arrive at your selected stop
    - `calculateArrivalTime` is used to increase the time of the buses based on how far away you are from the initial stop
    - Finally `getFormattedTime` is used to change military time into a 12 hour format with an AM or PM suffix
    - The Text Views are then displayed with the correct times

  - `finishedActivity(View view)`

    - Used to return the user to the previous activity when they press the back button

    

- `DataHelper.java`
  Data Helper is a class which interfaces with the DB and allows us to perform SQL queries

  - `createDatabase()`

    - Checks if the database already exists with `checkDatabase()`, if it does do nothing
    - If it doesn't exist call `copyDatabase()`

  - `checkDatabase()`

    - Checks for the existence of a database

  - `copyDatabase()`

    - Uses a buffer to make an identical copy of our database that the application can interact with

  - `openDatabase()`

    - Uses the `SQLiteDatabase.openDatabase()` to open the database for use

  - `selectAllFromRoutesTable`

    - Uses an SQL query to retrieve all routes in the route table

  - `timeFromStart(int routeID, int stopID)`

    - Finds the `timeFromStart` in the database given a `routeID` and `stopID` from the `route_stops` table

  - `nextThreeBuses(int routeID, String scheduleType, String intermediateTime)`

    - Returns an Array List containing the next three buses to arrive at the users selected stop

  - `calculateArrivalTime(ArrayList<String> busList, String timeFromStart)`

    - Calculates the arrival time of the bus given the time from start and the list of next buses

  - `selectFromStopLeftJoinRouteStops(int routeID)`

    - Associates `stopID`'s  to `routeID`'s using a `LEFT JOIN` query. Results are arranged by order of stops on routes.

    

- `Information.java`

  - `checkDayOfWeek()`

    - Returns either `"weekend"` or `"weekday"`, used to determine which bus schedule to use

  - `makeTimeHumanReadable(String militaryTime)`

    - Converts time from 24-hour time code to 12-hour with a AM or PM prefix
    - *i.e. 23:23:00 to 11:23 PM*

  - `calculateIntermediateTime(String timeFromStart)`

    - Intermediate Time is the current time subtracted from the time from start and it gives the 

    

- `ListTypeConverter.java`

  - `stopToString(ArrayList<Stop> stopArrayList)`
    - Converts an Array List of type `Stop` to an Array List of type `String`
  - `routeToString(ArrayList<Route> routeArrayList)`
    - Converts an Array List of type `Route` to an Array List of type `String

- `Route.java`

  - Class which contains route information, it's data members are
    - `int ID` : ID associated with the route
    - `String name`: Name of the route
    - `int startLocation`: The ID of the stop which the route starts on

- `Stop.java`

  - Class which contains stop information, it's data members are
    - `int ID`: ID associated with the stop
    - `String name`: Name associated with the stop
    - `double latitude`: Double which contains the latitude of the stop
    - `double longitude`: Double which contains the longitude of the stop