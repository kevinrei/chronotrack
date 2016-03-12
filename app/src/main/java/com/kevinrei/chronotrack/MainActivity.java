package com.kevinrei.chronotrack;

import android.content.Intent;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity {

    /** Action code */
    private static final int SELECT_INSTALLED_APP = 0;
    private static final int ADD_NEW_GAME = 1;

    /** ViewPager */
    private View view;
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        view = findViewById(R.id.main_content);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // Fab speed dial with options to add from installed apps or add a new game
        final FabSpeedDial fabSpeedDial = (FabSpeedDial) findViewById(R.id.fab_speed_dial);
        fabSpeedDial.setMenuListener(new SimpleMenuListenerAdapter() {
            @Override
            public boolean onPrepareMenu(NavigationMenu navigationMenu) {
                return true;
            }

            @Override
            public boolean onMenuItemSelected(MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_add_installed) {
                    Intent selectInstalledIntent = new Intent(MainActivity.this, InstalledAppActivity.class);
                    startActivityForResult(selectInstalledIntent, SELECT_INSTALLED_APP);
                    return true;
                }

                else if (id == R.id.action_add_uninstalled) {
                    Intent newGameIntent = new Intent(MainActivity.this, NewGameActivity.class);
                    startActivityForResult(newGameIntent, ADD_NEW_GAME);
                    return true;
                }

                return false;
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
                            fabSpeedDial.setVisibility(View.VISIBLE);
                        }

                        else {
                            fabSpeedDial.setVisibility(View.GONE);
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


    /**
     * FragmentStatePagerAdapter
     * */
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
                    return "Games";
                case 1:
                    // Countdown: remind me in x time
                    return "Alarms";
                case 2:
                    // Condition: remind me when x time reached
                    return "Timer";
            }
            return null;
        }
    }
}
