package com.example.trentbus;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        Information info = new Information();

        // Pull all the extra data from the first activity
        Bundle extras = getIntent().getExtras();
        int route = extras.getInt("routeID");
        int stop = extras.getInt("stopID");

        // Find the time from start using the route and stop selected on the previous activity
        String timeFromStart = DataHelper.timeFromStart(route, stop);

        // Calculate the intermediate time using the timeFromStart
        String intermediateTime = info.getIntermediateTime(timeFromStart);

        // Will return either "weekday" or "weekend" depending on the current day
        String day = info.getDayOfWeek();

        // Return an ArrayList of the next three buses
        ArrayList<String> nextBusList = DataHelper.nextThreeBuses(route,day,intermediateTime);

        // Adding time from start to the bus start times
        nextBusList = DataHelper.calculateArrivalTime(nextBusList, timeFromStart);

        // TextView countdown = findViewById(R.id.countdownTextView);
        TextView bus1 = findViewById(R.id.firstBusTextView);
        TextView bus2 = findViewById(R.id.secondBusTextView);
        TextView bus3 = findViewById(R.id.thirdBusTextView);

        // Fill TextViews according to how many buses are left
        if(nextBusList.size() >= 3 ) {
            bus1.setText(info.getFormattedTime(nextBusList.get(0)));
            bus2.setText(info.getFormattedTime(nextBusList.get(1)));
            bus3.setText(info.getFormattedTime(nextBusList.get(2)));
        } else if(nextBusList.size() == 2) {
            bus1.setText(info.getFormattedTime(nextBusList.get(0)));
            bus2.setText(info.getFormattedTime(nextBusList.get(1)));
        } else if (nextBusList.size() == 1) {
            bus1.setText(info.getFormattedTime(nextBusList.get(0)));
        } else{
            bus1.setText("Check");
            bus2.setText("Again");
            bus3.setText("Tomorrow");
        }
    }

    // Returns to the previous activity (Called when button_back is pressed)
    public void finishedActivity(View view) { finish(); }
}
