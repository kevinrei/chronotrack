package com.kevinrei.chronotrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ViewSwitcher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAlarmActivity extends AppCompatActivity {

    /** Layout flags */
    private static final int LAYOUT_STAMINA = 1;
    private static final int LAYOUT_CONDITION = 2;

    int layoutFlag;

    /** Database and data values */
    MySQLiteHelper db;
    Game game;

    /** General */
    protected ViewSwitcher mViewSwitcher;
    protected Switch mSwitch;
    protected TextView mStaminaLabel;
    protected EditText mAlarmLabel;
    protected CheckBox mCheckDelete;

    /** Stamina Layout */
    protected TextView mCalcUnit;
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

    /** Date & Time */
    final Calendar c = Calendar.getInstance();
    SimpleDateFormat sdfDate = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    String goalDate;
    String goalTime;
    String conditionDateTime;

    int mYear;
    int mMonth;
    int mDay;
    int mHour;
    int mMinute;

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

        Intent i = getIntent();
        game = (Game) i.getSerializableExtra("game");

        mViewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
        mSwitch = (Switch) findViewById(R.id.switch_layout);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    layoutFlag = LAYOUT_CONDITION;
                } else {
                    layoutFlag = LAYOUT_STAMINA;
                }

                Log.d("layoutFlag", String.valueOf(layoutFlag));
            }
        });

        mStaminaLabel = (TextView) findViewById(R.id.lbl_stamina_layout);

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

        // Mobile game with stamina system
        if (game.getCategory().equals("Mobile game") && game.getRecoveryRate() != 0) {
            mSwitch.setEnabled(true);
            mStaminaLabel.setTextColor(getResources().getColor(R.color.textIcons));
        } else {
            layoutFlag = LAYOUT_CONDITION;
            mViewSwitcher.showNext();
            mSwitch.setChecked(true);
            mSwitch.setEnabled(false);
            mStaminaLabel.setPaintFlags(mStaminaLabel.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
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
            Date example = getGoal(goalDate, goalTime);
            Log.d("Example", example.toString());
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
        mCalcUnit = (TextView) findViewById(R.id.lbl_unit);

        String calc = "Calculate " + game.getUnit() + " recovery time";
        mCalcUnit.setText(calc);

        mCurrentPicker = (NumberPicker) findViewById(R.id.picker_current);
        mGoalPicker = (NumberPicker) findViewById(R.id.picker_goal);

        int min = 0;
        int max = game.getMaxStamina();

        String[] values = new String[max+1];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.toString(i);
        }

        mCurrentPicker.setMinValue(min);
        mCurrentPicker.setMaxValue(max);
        mCurrentPicker.setValue(min);
        mCurrentPicker.setDisplayedValues(values);
        mCurrentPicker.setWrapSelectorWheel(false);

        mGoalPicker.setMinValue(min);
        mGoalPicker.setMaxValue(max);
        mGoalPicker.setValue(max);
        mGoalPicker.setDisplayedValues(values);
        mGoalPicker.setWrapSelectorWheel(false);
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

        mSetDateButton.setOnClickListener(mButtonClickListener);
        mSetTimeButton.setOnClickListener(mButtonClickListener);
        mSetReminderButton.setOnClickListener(mButtonClickListener);

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

        Date today = c.getTime();

        goalDate = sdfDate.format(today);
        goalTime = sdfTime.format(today);

        mCurrentDateText.setText(goalDate);
        mCurrentTimeText.setText(goalTime);
    }

    private String formatTime(int hourOfDay, int minute) {
        String formatted;
        String min;
        String am = " AM";
        String pm = " PM";

        if (minute < 10) {
            min = "0" + minute;
        } else {
            min = String.valueOf(minute);
        }

        if (hourOfDay == 0) {
            formatted = 12 + ":" + min + am;
        } else if (hourOfDay < 10) {
            formatted = "0" + hourOfDay + ":" + min + am;
        } else if (hourOfDay < 12) {
            formatted = hourOfDay + ":" + min + am;
        } else if (hourOfDay == 12) {
            formatted = hourOfDay + ":" + min + pm;
        } else {
            formatted = "0" + (hourOfDay - 12) + ":" + min + pm;
        }

        return formatted;
    }

    private Date getGoal(String date, String time) {
        Date goal = c.getTime();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault());
        String dateTime = date + " " + time;

        try {
            goal = format.parse(dateTime);
        } catch (ParseException e) {
            e.getMessage();
        }

        Log.d("Date time", dateTime);

        return goal;
    }

    /** Listeners */

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();

            switch(v.getId()) {
                case R.id.btn_set_date:

                    DatePickerDialog dp = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view,
                                              int year, int monthOfYear, int dayOfMonth) {
                            goalDate = monthOfYear + "/" + dayOfMonth + "/" + year;
                            mCurrentDateText.setText(goalDate);
                        }
                    }, mYear, mMonth, mDay);

                    dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    dp.setTitle("");
                    dp.setCancelable(false);

                    dp.show();

                    break;

                case R.id.btn_set_time:

                    TimePickerDialog tp = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    goalTime = hourOfDay + ":" + minute;
                                    String formatted = formatTime(hourOfDay, minute);
                                    mCurrentTimeText.setText(formatted);
                                }
                            }, mHour, mMinute, false);

                    tp.setCancelable(false);
                    tp.show();

                    break;

                case R.id.btn_set_reminder:
                    break;
            }
        }
    };
}
