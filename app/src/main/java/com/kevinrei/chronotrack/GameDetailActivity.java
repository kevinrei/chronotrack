package com.kevinrei.chronotrack;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class GameDetailActivity extends AppCompatActivity {

    /** Action code */
    private static final int ADD_NEW_ALARM = 2;

    /** Database and data values*/
    private MySQLiteHelper db;

    Game game;
    int gameId;
    String gameTitle;
    String gameUnit;
    int gameRate;
    int gameMax;

    /** Widgets and Fields */
    protected View mView;
    protected RelativeLayout mDetailLayout;
    protected TextView mLabelMax;
    protected TextView mMaxValue;
    protected TextView mRecoveryRate;
    protected TextView mFullRecovery;

    protected LinearLayoutManager mLayoutManager;
    protected RecyclerView mRecyclerView;
    protected AlarmAdapter mAlarmAdapter;
    protected List<Alarm> alarms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Intent i = getIntent();
        gameId = i.getIntExtra("game_id", 0);

        db = new MySQLiteHelper(this);
        game = db.getGame(gameId);

        gameTitle = game.getTitle();
        gameUnit = game.getUnit();
        gameRate = game.getRecoveryRate();
        gameMax = game.getMaxStamina();

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back (home) navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Set title as app name
        toolbar.setTitle(gameTitle);

        // Initialize the views
        mView = findViewById(R.id.main_content);
        mDetailLayout = (RelativeLayout) findViewById(R.id.layout_mobile_details);
        mLabelMax = (TextView) findViewById(R.id.lbl_max);
        mMaxValue = (TextView) findViewById(R.id.tv_max);
        mRecoveryRate = (TextView) findViewById(R.id.tv_rcv_rate);
        mFullRecovery = (TextView) findViewById(R.id.tv_full_rcv);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_alarms);

        // Hide the mobile detail layout if the game is not a mobile game
        if (!game.getCategory().equals("Mobile game")) {
            mDetailLayout.setVisibility(View.GONE);
        } else {
            mDetailLayout.setVisibility(View.VISIBLE);
        }

        // Max [unit] value:
        String labelMax = "Max " + gameUnit + " value:";
        mLabelMax.setText(labelMax);

        // Max stamina value
        if (gameMax == 0) {
            mMaxValue.setText("N/A");
        } else {
            mMaxValue.setText(String.valueOf(gameMax));
        }

        // Recovery rate
        if (gameRate == 0) {
            mRecoveryRate.setText("N/A");
        } else {
            mRecoveryRate.setText(getRateString(gameUnit, gameRate));
        }

        // Full recovery time
        mFullRecovery.setText(calculateTime(gameRate * gameMax));

        // Alarm list
        alarms = db.getAlarmsForGame(gameId);

        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAlarmAdapter = new AlarmAdapter(alarms);
        mRecyclerView.setAdapter(mAlarmAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_detail, menu);
        return true;
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

        else if (id == R.id.action_add_alarm) {
            Intent i = new Intent(this, AddAlarmActivity.class);
            i.putExtra("game", game);
            startActivityForResult(i, ADD_NEW_ALARM);
            return true;
        }

        else if (id == R.id.action_edit) {
            Intent i = new Intent(this, NewGameActivity.class);
            i.putExtra("flag", 2);
            i.putExtra("game", game);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == ADD_NEW_ALARM) {
                Snackbar.make(mView,
                        "Successfully added the alarm.",
                        Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /** Custom methods */

    public String getRateString(String unit, int rate) {
        String result, timeUnit, rateValueString;
        int rateValue;

        rateValue = rate / 60;

        // Get the unit of time measurement
        if (rateValue == 1) {
            timeUnit = "minute";
        } else if (rateValue < 60) {
            timeUnit = " minutes";
        } else if (rateValue == 60) {
            rateValue /= 60;
            timeUnit = "hour";
        } else {
            rateValue /= 60;
            timeUnit = " hours";
        }

        // Remove number if it's 1 minute or 1 hour
        if (rateValue == 1) {
            rateValueString = "";
        } else {
            rateValueString = String.valueOf(rateValue);
        }

        result = "1 " + unit + " every " + rateValueString + timeUnit;
        return result;
    }

    private static String calculateTime(long totalSeconds) {
        if (totalSeconds == 0) {
            return "N/A";
        }

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

        if (hours == 0) {
            return minutes + " minutes";
        }

        return hours + " hours " + minutes + " minutes";
    }
}
