package com.kevinrei.chronotrack;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.util.List;

import io.github.yavski.fabspeeddial.FabSpeedDial;
import io.github.yavski.fabspeeddial.SimpleMenuListenerAdapter;

public class MainActivity extends AppCompatActivity {

    /** Action code */
    private static final int SELECT_INSTALLED_APP = 0;

    /** ViewPager */
    private ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private View parent;

    /** App info */
    String selectedTitle;
    String selectedIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parent = findViewById(R.id.main_content);

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
                    startActivity(newGameIntent);
                    return true;
                }

                return true;
            }
        });

        // Set up the TabLayout.  Only show the FAB on Games tab.
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setOnTabSelectedListener(
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
                selectedTitle = data.getStringExtra("app_title");
                selectedIcon = data.getStringExtra("app_icon");
                Intent addGameIntent = new Intent(MainActivity.this, NewGameActivity.class);
                addGameIntent.putExtra("app_title", selectedTitle);
                addGameIntent.putExtra("app_icon", selectedIcon);
                startActivity(addGameIntent);
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

            else {
                return new AlarmListFragment();
            }
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
                    return "Countdown";
                case 2:
                    // Condition: remind me when x time reached
                    return "Condition";
            }
            return null;
        }
    }
}
