package com.example.trentbus;

import android.util.Log;

import java.util.Calendar;
import java.util.Date;

public class Information {
    // Returns whether the current day is a weekday or weekend
    private String checkDayOfWeek(){
        Calendar dateCal = Calendar.getInstance();
        int day = dateCal.get(Calendar.DAY_OF_WEEK);
        // Check if today is either Saturday(7) or Sunday(1)
        if (day == 1 || day == 7) {
            return "weekend";
        }
        else return "weekday";
    }

    // Converts formatted 24 hour time into 12 hour time with either a PM or AM prefix
    private String makeTimeHumanReadable(String militaryTime) {
        String[] newTimeArray = {"00","00","00"};
        String[] milTimeAsArray = militaryTime.split(":");
        if(Integer.parseInt(milTimeAsArray[0]) > 12){

            String formattedHour = String.valueOf(Integer.parseInt(milTimeAsArray[0]) - 12);

            newTimeArray[0] = formattedHour;
            newTimeArray[1] = milTimeAsArray[1];
            newTimeArray[2] = milTimeAsArray[2];
            return newTimeArray[0]+ ":" + newTimeArray[1] + " PM";
        }
        else{
            // Remove the seconds from the time
            return milTimeAsArray[0] + ":" + milTimeAsArray[1] + " AM";
        }
    }

    // Returns the amount of time since the bus left the station
    // This is used to see if the bus has passed a given stop
    // Ex: If a bus leaves at 1:20 and a user is 10 min away,
    // if the intermediate time is bigger than 10 min, then they miss the bus
    private String calculateIntermediateTime(String timeFromStart){
        // Create and instance of the phones calendar
        Calendar cal = Calendar.getInstance();

        // Get the current time as a Date object
        Date currentTime = cal.getTime();
        Log.i("TrentBus Log", "currentTime: " + currentTime);

        // Convert timeFromStart to array with split
        // Ex: 11:22:33 to {"11","22","33"}
        String[] timeFromStartArray = timeFromStart.split(":");

        // Use today's date as a base for the current day's bus schedule
        // (Need to use today's date otherwise it assumes date is epoch (Jan 1 1970)
        // Overwrite the hour, minute and second to the current time
        cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeFromStartArray[0]));
        cal.set(Calendar.MINUTE, Integer.parseInt(timeFromStartArray[1]));
        cal.set(Calendar.SECOND, Integer.parseInt(timeFromStartArray[2]));

        // Retrieve the time from our now overwritten calendar
        Date timeFromStartDate = cal.getTime();
        Log.i("TrentBus Log", "timeFromStartDate: " + timeFromStartDate);

        // Subtract the current time from the time that the bus left the station
        long intermediateTimeLong = currentTime.getTime() - timeFromStartDate.getTime();
        long seconds = intermediateTimeLong / 1000 % 60;
        long minutes = intermediateTimeLong / (60 * 1000) % 60;
        long hours = intermediateTimeLong / (60 * 60 * 1000) % 24;
        // This assumes the difference is not negative...

        // Return a formatted time code as a String
        // Prefix a 0 if the minute or hour is less than 10
        if (minutes < 10 && hours < 10){
            return "0" + hours + ":0" + minutes + ":" + seconds;
        }
        if (minutes < 10){
           return hours + ":0" + minutes + ":" + seconds;
        }
        if (hours < 10) {
            return "0" + hours + ":" + minutes + ":" + seconds;
        }
        return hours + ":" + minutes + ":" + seconds;
    }

    public String getDayOfWeek() { return checkDayOfWeek(); }

    public String getIntermediateTime(String timeFromStart) {
        try { return calculateIntermediateTime(timeFromStart); }
        catch (Exception e){
            Log.i("TrentBus Log", "Exception on intermediate time calculation");
            return "00:00:00";
        }
    }

    public String getFormattedTime(String militaryTime){ return makeTimeHumanReadable(militaryTime); }
}
