package com.kevinrei.chronotrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    /** Action code */
    private static final int SELECT_INSTALLED_APP = 0;
    private static final int ADD_NEW_GAME = 1;
    private static final int ADD_NEW_ALARM = 2;

    /** ViewPager */
    private View view;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /** Floating Action Button */
    private int fabPosition = 0;

    /** Game Dialog */
    public static class Action {
        public final int icon;
        public final String action;
        public Action(Integer icon, String action) {
            this.icon = icon;
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

    /** Alarm Dialog */
    MySQLiteHelper db;
    List<Game> games;
    List<Boolean> isStamina;

    ExpandableListAdapter mExpandAdapter;
    ExpandableListView mExpandView;
    List<String> gameTitles;
    HashMap<String, List<String>> alarmOptions;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.main_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        db = new MySQLiteHelper(this);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Fab speed dial with options to add from installed apps or add a new game
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fabPosition == 0) {
                    showGameTypeDialog();
                }

                else if (fabPosition == 1) {
                    games = db.getAllGames();

                    if (!games.isEmpty()) {
                        showGameListDialog();
                    } else {
                        Snackbar.make(view, "Please add a game first.", Snackbar.LENGTH_LONG);
                    }
                }

                else if (fabPosition == 2) {

                }
            }
        });

        // Set up the TabLayout.  Only show the FAB on Games tab.
        TabLayout mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.setOnTabSelectedListener(
                new TabLayout.ViewPagerOnTabSelectedListener(mViewPager) {
                    @Override
                    public void onTabSelected(TabLayout.Tab tab) {
                        super.onTabSelected(tab);

                        if (tab.getPosition() == 0) {
                            fabPosition = 0;
                            fab.setImageDrawable(getResources()
                                    .getDrawable(R.drawable.ic_add));
                        } else if (tab.getPosition() == 1) {
                            fabPosition = 1;
                            fab.setImageDrawable(getResources()
                                    .getDrawable(R.drawable.ic_add_alarm));
                        } else if (tab.getPosition() == 2) {
                            fabPosition = 2;
                            fab.setImageDrawable(getResources()
                                    .getDrawable(R.drawable.ic_play_arrow));
                        }
                    }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_about) {
            Intent i = new Intent(this, AboutActivity.class);
            startActivity(i);
            return true;
        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_INSTALLED_APP) {
                Intent i = new Intent(MainActivity.this, NewGameActivity.class);
                i.putExtras(data);
                startActivityForResult(i, ADD_NEW_GAME);
            }

            else if (requestCode == ADD_NEW_GAME) {
                String confirmAdd = "Successfully added " + data.getStringExtra("game_title") + ".";
                Snackbar.make(view, confirmAdd, Snackbar.LENGTH_LONG).show();
            }

            else if (requestCode == ADD_NEW_ALARM) {
                String confirmAdd = "Added alarm for " + data.getStringExtra("game_title") + ".";
                Snackbar.make(view, confirmAdd, Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
        }
    }


    /** Custom methods */

    private void prepareTitles() {
        gameTitles = new ArrayList<>();
        alarmOptions = new HashMap<>();
        isStamina = new ArrayList<>();

        for (Game game : games) {
            List<String> options = new ArrayList<>();
            gameTitles.add(game.getTitle());

            if (game.getCategory().equals("Mobile game") && game.getRecoveryRate() != 0) {
                options.add("Stamina");
                options.add("Date & Time");
                options.add("Condition");
                isStamina.add(true);
            } else {
                options.add("Date & Time");
                options.add("Condition");
                isStamina.add(false);
            }

            alarmOptions.put(game.getTitle(), options);
        }
    }


    /** Alert Dialogs */

    private void showGameTypeDialog() {
        final Action[] actions = {
                new Action(R.drawable.ic_phone_android, "Installed App"),
                new Action(R.drawable.ic_gamepad, "Other Games")
        };

        TypedArray typedArray = this.obtainStyledAttributes(null,
                R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);

        ListAdapter adapter = new ArrayAdapter<Action>(this,
                typedArray.getResourceId(R.styleable.AlertDialog_listItemLayout, 0),
                android.R.id.text1, actions){
            public View getView(int position, View convertView, ViewGroup parent) {
                //Use super class to create the View
                View v = super.getView(position, convertView, parent);
                TextView tv = (TextView) v.findViewById(android.R.id.text1);

                //Put the image on the TextView
                tv.setCompoundDrawablesWithIntrinsicBounds(actions[position].icon, 0, 0, 0);

                //Add margin between image and text (support various screen densities)
                int dp8 = (int) (8 * v.getContext().getResources().getDisplayMetrics().density);
                tv.setCompoundDrawablePadding(dp8);

                return v;
            }
        };

        typedArray.recycle();

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        mBuilder.setTitle("Select Game Type");
        mBuilder.setCancelable(false);

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which == 0) {
                    Intent selectInstalledIntent = new Intent(MainActivity.this, InstalledAppActivity.class);
                    startActivityForResult(selectInstalledIntent, SELECT_INSTALLED_APP);
                }

                else {
                    Intent newGameIntent = new Intent(MainActivity.this, NewGameActivity.class);
                    startActivityForResult(newGameIntent, ADD_NEW_GAME);
                }
            }
        });

        mBuilder.show();
    }

    private void showGameListDialog() {
        prepareTitles();

        mExpandAdapter = new ExpandableListAdapter(this, gameTitles, alarmOptions);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        View view = this.getLayoutInflater().inflate(R.layout.dialog_alarm_options, null);
        mBuilder.setView(view);

        mExpandView = (ExpandableListView) view.findViewById(R.id.expandable_alarm_options);
        mExpandView.setAdapter(mExpandAdapter);

        mBuilder.setTitle("Select Game");
        mBuilder.setCancelable(false);

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog mAlert = mBuilder.create();

        // List view on child click listener
        mExpandView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                int flag;
                if (isStamina.get(groupPosition)) {
                    flag = childPosition;
                } else {
                    flag = childPosition + 1;
                }

                Intent i = new Intent(MainActivity.this, AddAlarmActivity.class);
                i.putExtra("flag", flag);
                i.putExtra("game", games.get(groupPosition));
                startActivityForResult(i, ADD_NEW_ALARM);

                mAlert.dismiss();
                return true;
            }
        });

        mAlert.show();
    }


    /** Adapters */

    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            if (position == 0) {
                return new GameListFragment();
            }

            else if (position == 1) {
                return new AlarmListFragment();
            }

            else if (position == 2) {
                return new TimerFragment();
            }

            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    // List of games
                    return "Games";
                case 1:
                    // Active alarms
                    return "Active";
                case 2:
                    // Quick timer
                    return "Quick";
            }
            return null;
        }

        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
