package com.kevinrei.chronotrack;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.wefika.horizontalpicker.HorizontalPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class AddAlarmActivity extends AppCompatActivity {

    /** Layout flags */
    private static final int LAYOUT_ADD_STAMINA_ALARM = 0;
    private static final int LAYOUT_ADD_DATETIME_ALARM = 1;
    private static final int LAYOUT_ADD_CONDITION_ALARM = 2;
    int layoutFlag;

    /** Database and data values */
    MySQLiteHelper db;
    Game game;

    /** General */
    protected View mView;
    protected EditText mAlarmLabel;
    protected CheckBox mCheckSave;

    /** Stamina Layout */
    protected HorizontalPicker mCurrentPicker;
    protected HorizontalPicker mGoalPicker;

    /** DateTime Layout */
    protected Button mSetDateButton;
    protected Button mSetTimeButton;
    protected TextView mCurrentDate;
    protected TextView mCurrentTime;

    final Calendar c = Calendar.getInstance();
    SimpleDateFormat sdfDate = new SimpleDateFormat("EEEE, MMMM dd, yyyy", Locale.getDefault());
    SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    String triggerDate;
    String triggerTime;

    int dtYear;
    int dtMonth;
    int dtDay;

    int dtHour;
    int dtMinute;

    /** Condition Layout */
    protected TextView mDay;
    protected TextView mHour;
    protected TextView mMinute;
    protected TextView mSecond;
    protected Button mOne;
    protected Button mTwo;
    protected Button mThree;
    protected Button mFour;
    protected Button mFive;
    protected Button mSix;
    protected Button mSeven;
    protected Button mEight;
    protected Button mNine;
    protected Button mZero;
    protected ImageButton mClear;
    protected ImageButton mBack;

    int position = 7;
    String day = "00";
    String hour = "00";
    String minute = "00";
    String second = "00";

    /** Alarm variables */
    public static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        layoutFlag = i.getIntExtra("flag", 2);
        game = i.getParcelableExtra("game");

        if (layoutFlag == LAYOUT_ADD_STAMINA_ALARM) {
            setContentView(R.layout.activity_add_stamina_alarm);
            initStaminaLayout();
        } else if (layoutFlag == LAYOUT_ADD_DATETIME_ALARM) {
            setContentView(R.layout.activity_add_datetime_alarm);
            initDateTimeLayout();
        } else if (layoutFlag == LAYOUT_ADD_CONDITION_ALARM) {
            setContentView(R.layout.activity_add_countdown_alarm);
            initConditionLayout();
        }

        mView = findViewById(R.id.main_content);
        db = new MySQLiteHelper(this);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back (home) navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            if (layoutFlag == LAYOUT_ADD_STAMINA_ALARM) {
                getSupportActionBar().setTitle("New Stamina Alarm");
            } else if (layoutFlag == LAYOUT_ADD_CONDITION_ALARM) {
                getSupportActionBar().setTitle("New Countdown Alarm");
            }
        }

        mAlarmLabel = (EditText) findViewById(R.id.alarm_label);
        mCheckSave = (CheckBox) findViewById(R.id.cb_save);
        Log.d("layout_flag", String.valueOf(layoutFlag));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_alarm, menu);
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
        } else if (id == R.id.action_start) {
            int uniqueAlarmID = (int) (System.currentTimeMillis() % Integer.MAX_VALUE);
            int gameId = game.getId();
            int current = 0;
            int goal = 0;
            long alarmTriggerTime = -1;     // When the alarm should be triggered
            String alarmLabel;              // The alarm's label
            boolean saveAfter = false;      // Delete the alarm after it's been triggered

            Log.d("layout", String.valueOf(layoutFlag));

            if (layoutFlag == LAYOUT_ADD_STAMINA_ALARM) {
                current = mCurrentPicker.getSelectedItem();
                goal = mGoalPicker.getSelectedItem();

                if ((goal - current) <= 0) {
                    Snackbar.make(mView, "Invalid entry.  Please try different values.",
                            Snackbar.LENGTH_LONG).show();
                    return false;
                }

                alarmTriggerTime = game.getRecoveryRate() * (goal - current) * 1000;
                saveAfter = mCheckSave.isChecked();
            }

            else if (layoutFlag == LAYOUT_ADD_DATETIME_ALARM) {
                Date dtGoal = getDateTimeTriggerTime(triggerDate, triggerTime);
                alarmTriggerTime = dtGoal.getTime() - c.getTimeInMillis();

                if (alarmTriggerTime < 0) {
                    Snackbar.make(mView, "Invalid time.  Please try a different time.",
                            Snackbar.LENGTH_LONG).show();
                    return false;
                }

                saveAfter = false;
            }

            else if (layoutFlag == LAYOUT_ADD_CONDITION_ALARM) {
                alarmTriggerTime = getConditionTriggerTime();

                if (alarmTriggerTime == 0) {
                    Snackbar.make(mView, "Please enter time.",
                            Snackbar.LENGTH_LONG).show();
                    return false;
                }

                saveAfter = mCheckSave.isChecked();
            }

            Log.d("editText", mAlarmLabel.getText().toString());
            Log.d("isEmpty", String.valueOf(isEmpty(mAlarmLabel)));

            if (isEmpty(mAlarmLabel)) {
                alarmLabel = "Label";
            } else {
                alarmLabel = mAlarmLabel.getText().toString();
            }

            long startCountdownValue = System.currentTimeMillis() + alarmTriggerTime;
            int saveAfterFlag = (saveAfter) ? 1 : 0;

            // Create the new Alarm
            Alarm alarm = new Alarm();

            alarm.setAlarmId(uniqueAlarmID);
            alarm.setGameId(gameId);
            alarm.setFlag(layoutFlag);
            alarm.setStart(current);
            alarm.setEnd(goal);
            alarm.setTrigger(alarmTriggerTime);
            alarm.setCountdown(startCountdownValue);
            alarm.setLabel(alarmLabel);
            alarm.setSave(saveAfterFlag);

            // Add the alarm to the database
            db.addAlarm(alarm);

            // Notify the alarm adapter
            notifyAlarmAdapter(alarm);

            // Create the alarm
            AlarmController controller = new AlarmController();
            controller.setAlarm(this, alarm);

            // Pass the result to MainActivity
            Intent i = new Intent();
            i.putExtra("game_title", game.getTitle());
            setResult(RESULT_OK, i);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    /** Custom methods */

    private void initStaminaLayout() {
        mCurrentPicker = (HorizontalPicker) findViewById(R.id.picker_current);
        mGoalPicker = (HorizontalPicker) findViewById(R.id.picker_goal);

        int min = 0;
        int max = game.getMaxStamina();

        String[] values = new String[max+1];
        for (int i = 0; i < values.length; i++) {
            values[i] = Integer.toString(i);
        }

        mCurrentPicker.setValues(values);
        mCurrentPicker.setSelectedItem(min);

        mGoalPicker.setValues(values);
        mGoalPicker.setSelectedItem(max);
    }

    private void initDateTimeLayout() {
        mSetDateButton = (Button) findViewById(R.id.btn_set_date);
        mSetTimeButton = (Button) findViewById(R.id.btn_set_time);

        mCurrentDate = (TextView) findViewById(R.id.txt_current_date);
        mCurrentTime = (TextView) findViewById(R.id.txt_current_time);

        mSetDateButton.setOnClickListener(mDateTimeClickListener);
        mSetTimeButton.setOnClickListener(mDateTimeClickListener);

        Date today = c.getTime();
        triggerDate = sdfDate.format(today);
        triggerTime = sdfTime.format(today);

        mCurrentDate.setText(triggerDate);
        mCurrentTime.setText(triggerTime);
    }

    private void initConditionLayout() {
        mDay = (TextView) findViewById(R.id.input_day);
        mHour = (TextView) findViewById(R.id.input_hour);
        mMinute = (TextView) findViewById(R.id.input_minute);
        mSecond = (TextView) findViewById(R.id.input_second);

        mOne = (Button) findViewById(R.id.one);
        mTwo = (Button) findViewById(R.id.two);
        mThree = (Button) findViewById(R.id.three);
        mFour = (Button) findViewById(R.id.four);
        mFive = (Button) findViewById(R.id.five);
        mSix = (Button) findViewById(R.id.six);
        mSeven = (Button) findViewById(R.id.seven);
        mEight = (Button) findViewById(R.id.eight);
        mNine = (Button) findViewById(R.id.nine);
        mZero = (Button) findViewById(R.id.zero);

        mClear = (ImageButton) findViewById(R.id.img_btn_clear);
        mBack = (ImageButton) findViewById(R.id.img_btn_back);

        mOne.setOnClickListener(mConditionClickListener);
        mTwo.setOnClickListener(mConditionClickListener);
        mThree.setOnClickListener(mConditionClickListener);
        mFour.setOnClickListener(mConditionClickListener);
        mFive.setOnClickListener(mConditionClickListener);
        mSix.setOnClickListener(mConditionClickListener);
        mSeven.setOnClickListener(mConditionClickListener);
        mEight.setOnClickListener(mConditionClickListener);
        mNine.setOnClickListener(mConditionClickListener);
        mZero.setOnClickListener(mConditionClickListener);

        mClear.setOnClickListener(mDeleteClickListener);
        mBack.setOnClickListener(mDeleteClickListener);
    }

    // Check if the EditText field is empty
    private boolean isEmpty(EditText editText) {
        if (editText == null) {
            return true;
        } else {
            return editText.getText().toString().trim().length() == 0;
        }
    }

    private String removeLeadingZero(String str) {
        if (str.charAt(0) == '0') {
            return str.substring(1, str.length());
        }

        return str;
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

    private Date getDateTimeTriggerTime(String date, String time) {
        Date goal = c.getTime();

        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yy HH:mm", Locale.getDefault());
        String dateTime = date + " " + time;

        try {
            goal = format.parse(dateTime);
        } catch (ParseException e) {
            e.getMessage();
        }

        return goal;
    }

    private void setCondition(String t) {
        day = t.substring(0, 2);
        hour = t.substring(2, 4);
        minute = t.substring(4, 6);
        second = t.substring(6, 8);

        mDay.setText(day);
        mHour.setText(hour);
        mMinute.setText(minute);
        mSecond.setText(second);
    }

    private long getConditionTriggerTime() {
        return  (long) (Integer.parseInt(removeLeadingZero(day)) * 24 * 60 * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(hour)) * 60 * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(minute)) * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(second)) * 1000);
    }

    public void notifyAlarmAdapter(Alarm alarm) {
        int position = AlarmAdapter.alarms.size();

        AlarmAdapter.alarms.add(position, alarm);
        AlarmListFragment.mAlarmAdapter.notifyItemInserted(position);
        AlarmListFragment.mAlarmAdapter.notifyItemRangeChanged(0, position);
    }

    /** Listeners */

    private View.OnClickListener mDateTimeClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Context context = v.getContext();

            switch(v.getId()) {
                case R.id.btn_set_date:
                    dtYear = c.get(Calendar.YEAR);
                    dtMonth = c.get(Calendar.MONTH);
                    dtDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dp = new DatePickerDialog(context,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view,
                                                      int year, int monthOfYear, int dayOfMonth) {
                                    triggerDate = (monthOfYear + 1) + "/" + dayOfMonth + "/" + year;

                                    SimpleDateFormat parseFormat = new SimpleDateFormat(
                                            "MM/dd/yyyy", Locale.getDefault());

                                    Date d = c.getTime();

                                    try {
                                        d = parseFormat.parse(triggerDate);
                                    } catch (ParseException e) {
                                        e.getMessage();
                                    }

                                    mCurrentDate.setText(sdfDate.format(d));
                                }
                            }, dtYear, dtMonth, dtDay);

                    dp.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    dp.setTitle("");
                    dp.setCancelable(false);

                    dp.show();
                    break;

                case R.id.btn_set_time:
                    dtHour = c.get(Calendar.HOUR_OF_DAY);
                    dtMinute = c.get(Calendar.MINUTE);

                    TimePickerDialog tp = new TimePickerDialog(context,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    triggerTime = hourOfDay + ":" + minute;

                                    if (hourOfDay < 12) {
                                        triggerTime += " AM";
                                    } else {
                                        triggerTime += " PM";
                                    }

                                    String formatted = formatTime(hourOfDay, minute);
                                    mCurrentTime.setText(formatted);
                                }
                            }, dtHour, dtMinute, false);

                    tp.setCancelable(false);

                    tp.show();
                    break;
            }
        }
    };

    private View.OnClickListener mConditionClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            char input = '0';

            switch(v.getId()) {
                case R.id.one:
                    input = mOne.getText().toString().charAt(0);
                    break;

                case R.id.two:
                    input = mTwo.getText().toString().charAt(0);
                    break;

                case R.id.three:
                    input = mThree.getText().toString().charAt(0);
                    break;

                case R.id.four:
                    input = mFour.getText().toString().charAt(0);
                    break;

                case R.id.five:
                    input = mFive.getText().toString().charAt(0);
                    break;

                case R.id.six:
                    input = mSix.getText().toString().charAt(0);
                    break;

                case R.id.seven:
                    input = mSeven.getText().toString().charAt(0);
                    break;

                case R.id.eight:
                    input = mEight.getText().toString().charAt(0);
                    break;

                case R.id.nine:
                    input = mNine.getText().toString().charAt(0);
                    break;

                case R.id.zero:
                    input = mZero.getText().toString().charAt(0);
                    break;
            }

            String secondText = mSecond.getText().toString();
            String minuteText = mMinute.getText().toString();
            String hourText = mHour.getText().toString();
            String dayText = mDay.getText().toString();

            String join = dayText + hourText + minuteText + secondText;
            StringBuilder time = new StringBuilder(join);
            if (position == 7) {
                time.setCharAt(7, input);
                position--;
            } else if (position >= 0) {
                for (int p = position; p < 7; p++) {
                    time.setCharAt(p, join.charAt(p + 1));
                }

                time.setCharAt(7, input);
                if (position != 0) {
                    position--;
                }
            }

            setCondition(time.toString());
        }
    };

    private View.OnClickListener mDeleteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch(v.getId()) {
                case R.id.img_btn_clear:
                    String reset = "00";

                    mDay.setText(reset);
                    mHour.setText(reset);
                    mMinute.setText(reset);
                    mSecond.setText(reset);

                    position = 7;
                    break;

                case R.id.img_btn_back:
                    char del = '0';

                    String secondText = mSecond.getText().toString();
                    String minuteText = mMinute.getText().toString();
                    String hourText = mHour.getText().toString();
                    String dayText = mDay.getText().toString();

                    String join = dayText + hourText + minuteText + secondText;
                    StringBuilder time = new StringBuilder(join);
                    if (position == 7) {
                        time.setCharAt(7, del);
                    } else if (position >= 0) {
                        for (int p = position; p < 7; p++) {
                            time.setCharAt(p + 1, join.charAt(p));
                        }

                        time.setCharAt(position, del);
                        position++;
                    }

                    setCondition(time.toString());
                    break;
            }
        }
    };
}
