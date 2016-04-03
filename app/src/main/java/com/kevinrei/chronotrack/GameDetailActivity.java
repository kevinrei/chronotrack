package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

public class GameDetailActivity extends AppCompatActivity {

    private static final int HORIZONTAL_MARGIN = 32;
    private static final int VERTICAL_MARGIN = 32;

    /** Action code */
    private static final int ADD_NEW_ALARM = 2;

    /** Database and data values*/
    private MySQLiteHelper db;

    Game game;
    int gameId;
    String gameTitle;
    String gameCategory;
    String gameUnit;
    int gameRate;
    int gameMax;

    /** Views */
    protected View mView;
    protected LinearLayout mStaminaView;
    protected TextView mSavedAlarmsText;
    protected RecyclerView mRecyclerView;

    /** Alarm List */
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
        gameCategory = game.getCategory();
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

        // Set up stamina view if conditions are met
        mStaminaView = (LinearLayout) findViewById(R.id.stamina_view);

        if (gameCategory.equals("Mobile game") && gameRate != 0) {
            mStaminaView.setVisibility(View.VISIBLE);
            setStaminaView();
        } else {
            mStaminaView.setVisibility(View.GONE);
        }

        // Initialize the views
        mView = findViewById(R.id.main_content);
        mSavedAlarmsText = (TextView) findViewById(R.id.lbl_saved_alarms);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_alarms);

        // Alarm list
        alarms = db.getSavedAlarmsForGame(gameId);

        String header = "Saved Alarms for " + gameTitle;
        mSavedAlarmsText.setText(header);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(HORIZONTAL_MARGIN, VERTICAL_MARGIN));

        mAlarmAdapter = new AlarmAdapter(1, alarms);
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
            if (gameCategory.equals("Mobile game") && gameRate != 0) {
                showAlarmDialogWithStamina(this, game);
            } else {
                showAlarmDialogWithoutStamina(this, game);
            }

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

    private void setStaminaView() {
        TextView mLabelMax = (TextView) findViewById(R.id.lbl_max);
        TextView mMaxValue = (TextView) findViewById(R.id.tv_max);
        TextView mRecoveryRate = (TextView) findViewById(R.id.tv_rcv_rate);
        TextView mFullRecovery = (TextView) findViewById(R.id.tv_full_rcv);

        // Max [unit] value:
        String labelMax = "Max " + gameUnit + " value:";
        mLabelMax.setText(labelMax);

        // Max stamina value
        mMaxValue.setText(String.valueOf(gameMax));

        // Recovery rate
        mRecoveryRate.setText(getRateString(gameUnit, gameRate));

        // Full recovery time
        mFullRecovery.setText(calculateTime(gameRate * gameMax));
    }

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


    /** Alert Dialogs */

    private void showAlarmDialogWithStamina(final Context context, final Game game) {
        CharSequence[] options = new CharSequence[] { "Stamina", "Date & Time", "Countdown" };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Alarm Type");
        mBuilder.setCancelable(false);

        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, AddAlarmActivity.class);
                i.putExtra("flag", which);
                i.putExtra("game", game);
                startActivityForResult(i, ADD_NEW_ALARM);
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mBuilder.create().show();
    }

    private void showAlarmDialogWithoutStamina(final Context context, final Game game) {
        CharSequence[] options = new CharSequence[] { "Date & Time", "Countdown" };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Alarm Type");
        mBuilder.setCancelable(false);

        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, AddAlarmActivity.class);
                i.putExtra("flag", which + 1);
                i.putExtra("game", game);
                startActivityForResult(i, ADD_NEW_ALARM);
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mBuilder.create().show();
    }
}
