package com.kevinrei.chronotrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class AddAlarmActivity extends AppCompatActivity {

    /** Layout flags */
    private static final int LAYOUT_ADD_STAMINA_ALARM = 0;
    private static final int LAYOUT_ADD_CONDITION_ALARM = 1;
    int layoutFlag = 2;

    /** Database and data values */
    MySQLiteHelper db;
    Game game;

    /** General */
    protected View mView;
    protected EditText mAlarmLabel;
    protected CheckBox mCheckSave;

    /** Stamina Layout */
    protected NumberPicker mCurrentPicker;
    protected NumberPicker mGoalPicker;

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

    int position = 7;
    String day;
    String hour;
    String minute;
    String second;

    /** Alarm variables */
    public static AlarmManager mAlarmManager;
    public static PendingIntent mPendingIntent;
    public static Context context;
    public static Intent notifyIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = getIntent();
        layoutFlag = i.getIntExtra("flag", 1);
        game = (Game) i.getSerializableExtra("game");

        if (layoutFlag == LAYOUT_ADD_STAMINA_ALARM) {
            setContentView(R.layout.activity_add_stamina_alarm);
            initStaminaLayout();
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

        mCheckSave = (CheckBox) findViewById(R.id.cb_save);
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Log.d("layout_flag", String.valueOf(layoutFlag));
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
            int uniqueAlarmID = (int) (System.currentTimeMillis() / 100000);
            int gameId = game.getId();
            int current = 0;
            int goal = 0;
            long alarmTriggerTime = -1;      // When the alarm should be triggered
            String alarmLabel;               // The alarm's label
            boolean saveAfter = false;      // Delete the alarm after it's been triggered

            Log.d("layout", String.valueOf(layoutFlag));

            if (layoutFlag == LAYOUT_ADD_STAMINA_ALARM) {
                current = mCurrentPicker.getValue();
                goal = mGoalPicker.getValue();

                alarmTriggerTime = game.getRecoveryRate() * (goal - current) * 1000;
                saveAfter = mCheckSave.isChecked();
            }

            else if (layoutFlag == LAYOUT_ADD_CONDITION_ALARM) {
                alarmTriggerTime = getTriggerTime();
                saveAfter = mCheckSave.isChecked();
            }

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

            // Trigger a new PendingIntent
            context = AddAlarmActivity.this;
            notifyIntent = new Intent(this, AlarmReceiver.class);
            mPendingIntent = PendingIntent.getBroadcast(context, alarm.getAlarmId(), notifyIntent, 0);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP,
                    startCountdownValue,
                    mPendingIntent);

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

        mOne.setOnClickListener(mButtonClickListener);
        mTwo.setOnClickListener(mButtonClickListener);
        mThree.setOnClickListener(mButtonClickListener);
        mFour.setOnClickListener(mButtonClickListener);
        mFive.setOnClickListener(mButtonClickListener);
        mSix.setOnClickListener(mButtonClickListener);
        mSeven.setOnClickListener(mButtonClickListener);
        mEight.setOnClickListener(mButtonClickListener);
        mNine.setOnClickListener(mButtonClickListener);
        mZero.setOnClickListener(mButtonClickListener);
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

    private long getTriggerTime() {
        return  (Integer.parseInt(removeLeadingZero(day)) * 24 * 60 * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(hour)) * 60 * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(minute)) * 60 * 1000)
                + (Integer.parseInt(removeLeadingZero(second)) * 1000);
    }

    public static void cancelAlarm(int alarmId) {
        if (mAlarmManager != null) {
            PendingIntent cancelIntent = PendingIntent.getBroadcast(context, alarmId, notifyIntent, 0);
            mAlarmManager.cancel(cancelIntent);
        }
    }

    /** Listeners */

    private View.OnClickListener mButtonClickListener = new View.OnClickListener() {
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
                position--;
            }

            String t = time.toString();

            day = t.substring(0, 2);
            hour = t.substring(2, 4);
            minute = t.substring(4, 6);
            second = t.substring(6, 8);

            mDay.setText(day);
            mHour.setText(hour);
            mMinute.setText(minute);
            mSecond.setText(second);
        }
    };
}
