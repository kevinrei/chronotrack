package com.kevinrei.chronotrack;

import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ViewSwitcher;

public class AddAlarmActivity extends AppCompatActivity {

    /** Database and data values */
    private MySQLiteHelper db;

    /** General */
    protected ViewSwitcher mViewSwitcher;
    protected Switch mSwitch;
    protected EditText mAlarmLabel;
    protected CheckBox mCheckDelete;

    /** Stamina Layout */
    protected NumberPicker mCurrentPicker;
    protected NumberPicker mGoalPicker;

    /** Condition Layout */
    protected RelativeLayout mDateTimeLayout;
    protected LinearLayout mCountdownLayout;
    protected RadioGroup mRadioGroup;
    protected RadioButton mRadioButton;

    protected Button mSetDateButton;
    protected TextView mCurrentDateText;

    protected Button mSetTimeButton;
    protected TextView mCurrentTimeText;

    protected Button mSetReminderButton;
    protected TextView mCurrentReminderText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_alarm);

        db = new MySQLiteHelper(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back (home) navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mSwitch = (Switch) findViewById(R.id.switch_layout);

        mAlarmLabel = (EditText) findViewById(R.id.alarm_label);
        mCheckDelete = (CheckBox) findViewById(R.id.cb_delete);

        initStaminaLayout();
        initConditionLayout();

        mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewSwitcher.showNext();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
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
        } else if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    /** Custom methods */

    private void initStaminaLayout() {
        mCurrentPicker = (NumberPicker) findViewById(R.id.picker_current);
        mGoalPicker = (NumberPicker) findViewById(R.id.picker_goal);
    }

    private void initConditionLayout() {
        mDateTimeLayout = (RelativeLayout) findViewById(R.id.layout_datetime);
        mCountdownLayout = (LinearLayout) findViewById(R.id.layout_countdown);

        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);

        mSetDateButton = (Button) findViewById(R.id.btn_set_date);
        mCurrentDateText = (TextView) findViewById(R.id.current_date);

        mSetTimeButton = (Button) findViewById(R.id.btn_set_time);
        mCurrentTimeText = (TextView) findViewById(R.id.current_time);

        mSetReminderButton = (Button) findViewById(R.id.btn_set_reminder);
        mCurrentReminderText = (TextView) findViewById(R.id.current_reminder);

        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                mRadioButton = (RadioButton) group.findViewById(checkedId);

                if (mRadioButton.getText().equals(getString(R.string.set_datetime))) {
                    mDateTimeLayout.setVisibility(View.VISIBLE);
                    mCountdownLayout.setVisibility(View.GONE);
                } else {
                    mDateTimeLayout.setVisibility(View.GONE);
                    mCountdownLayout.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
