package com.kevinrei.chronotrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

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
    String gameUnit;
    int gameRate;
    int gameMax;

    /** Widgets and Fields */
    protected View mView;
    protected TextView mSavedLabel;

    protected RecyclerView mRecyclerView;
    protected SavedAlarmAdapter mSavedAlarmAdapter;
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
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_alarms);

        // Alarm list
        alarms = db.getAlarmsForGame(gameId);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(HORIZONTAL_MARGIN, VERTICAL_MARGIN));

        mSavedAlarmAdapter = new SavedAlarmAdapter(gameId, alarms);
        mRecyclerView.setAdapter(mSavedAlarmAdapter);
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
}
