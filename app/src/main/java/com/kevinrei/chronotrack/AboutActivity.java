package com.kevinrei.chronotrack;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AboutActivity extends AppCompatActivity {

    /** Views */
    protected ExpandableListView mExpandableListView;

    /** Dataset */
    private List<String> headers;
    private HashMap<String, List<String>> descriptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back (home) navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mExpandableListView = (ExpandableListView) findViewById(R.id.expandable_about);

        prepareData();
        ExpandableListAdapter ela = new ExpandableListAdapter(this, headers, descriptions);
        mExpandableListView.setAdapter(ela);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Custom methods */

    private void prepareData() {
        headers = new ArrayList<>();
        descriptions = new HashMap<>();

        // Headers
        headers.add("About ChronoTrack");
        headers.add("What is a Stamina Alarm?");
        headers.add("What is a Date & Time Alarm?");
        headers.add("What is a Condition Alarm?");
        headers.add("Special Thanks");

        // Descriptions
        List<String> aboutChronoTrack = new ArrayList<>();
        aboutChronoTrack.add(
                "ChronoTrack is a native Android alarm application dedicated to keep track of " +
                        "all the different types of timers within a broad range of games.  Keep " +
                        "track of stamina recovery times, set reminders to take a break and " +
                        "continue playing your game, save a timer for your favorite board games, " +
                        "and more.");

        List<String> staminaDescription = new ArrayList<>();
        staminaDescription.add(
                "A stamina alarm is a specific alarm for your mobile games with an energy system. " +
                        "Set reminders to play when you have enough energy for that descend " +
                        "dungeon, when your stamina is fully restored, etc.");

        List<String> dateTimeDescription = new ArrayList<>();
        dateTimeDescription.add(
                "A date & time alarm sends a notification to your phone at a specific date and " +
                        "time you set.  Get reminders of when that long-awaited event starts, " +
                        "what time that time-specific dungeon appears, etc.");

        List<String> conditionDescription = new ArrayList<>();
        conditionDescription.add(
                "A condition alarm fires a notification in x amount of time that you set.  It\'s " +
                        "essentially a countdown alarm.  Remind yourself of when your building " +
                        "construction\'s done, when it\'s time to take a break from work and " +
                        "play your games, etc.");

        List<String> specialThanks = new ArrayList<>();
        specialThanks.add(
                "");

        // Populate data
        descriptions.put(headers.get(0), aboutChronoTrack);
        descriptions.put(headers.get(1), staminaDescription);
        descriptions.put(headers.get(2), dateTimeDescription);
        descriptions.put(headers.get(3), conditionDescription);
        descriptions.put(headers.get(4), specialThanks);
    }
}
