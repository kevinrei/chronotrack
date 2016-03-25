package com.kevinrei.chronotrack;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

import java.util.Locale;

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
    protected TextView mCalcUnit;
    protected NumberPicker mCurrentPicker;
    protected NumberPicker mGoalPicker;

    /** Condition Layout */
    protected Button mSetReminderButton;
    protected TextView mCurrentReminderText;

    /** Date & Time */
    String goalReminder;
    long reminderValue;

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
                getSupportActionBar().setTitle("Create New Stamina Alarm");
            } else if (layoutFlag == LAYOUT_ADD_CONDITION_ALARM) {
                getSupportActionBar().setTitle("Create New Condition Alarm");
            }
        }

        mCheckSave = (CheckBox) findViewById(R.id.cb_save);

        // Mobile game with stamina system
        if (game.getCategory().equals("Mobile game") && game.getRecoveryRate() != 0) {
            layoutFlag = LAYOUT_ADD_STAMINA_ALARM;
        } else {
            layoutFlag = LAYOUT_ADD_CONDITION_ALARM;
        }

        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
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
                alarmTriggerTime = reminderValue;
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
        mSetReminderButton = (Button) findViewById(R.id.btn_set_reminder);
        mCurrentReminderText = (TextView) findViewById(R.id.current_reminder);

        mSetReminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSetCountdownDialog();
            }
        });
    }

    private void showSetCountdownDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater mInflater = this.getLayoutInflater();
        final View mDialogView = mInflater.inflate(R.layout.dialog_reminder_countdown, null);
        mBuilder.setView(mDialogView);

        final NumberPicker pickHour = (NumberPicker) mDialogView.findViewById(R.id.picker_hr);
        final NumberPicker pickMinute = (NumberPicker) mDialogView.findViewById(R.id.picker_min);
        final NumberPicker pickSecond = (NumberPicker) mDialogView.findViewById(R.id.picker_sec);

        // Max value for hours is 7 days
        String[] valuesHour = new String[169];
        for (int i = 0; i < valuesHour.length; i++) {
            valuesHour[i] = Integer.toString(i);
        }

        String[] valuesMS = new String[60];
        for (int i = 0; i < valuesMS.length; i++) {
            valuesMS[i] = Integer.toString(i);
        }

        pickHour.setMinValue(0);
        pickHour.setMaxValue(168);
        pickHour.setDisplayedValues(valuesHour);
        pickHour.setWrapSelectorWheel(false);

        pickMinute.setMinValue(0);
        pickMinute.setMaxValue(59);
        pickMinute.setDisplayedValues(valuesMS);

        pickSecond.setMinValue(0);
        pickSecond.setMaxValue(59);
        pickSecond.setDisplayedValues(valuesMS);

        mBuilder.setTitle("Set Countdown Time")
                .setCancelable(false)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int seconds = pickSecond.getValue();
                        int minutes = pickMinute.getValue();
                        int hours = pickHour.getValue();

                        reminderValue = (hours * 60 * 60 * 1000)
                                + (minutes * 60 * 1000)
                                + (seconds * 1000);

                        goalReminder = String.format(Locale.getDefault(),
                                "%02dh %02dm %02ds", hours, minutes, seconds);

                        mCurrentReminderText.setText(goalReminder);
                    }
                }).show();
    }

    // Check if the EditText field is empty
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    public static void cancelAlarm(int alarmId) {
        if (mAlarmManager != null) {
            PendingIntent cancelIntent = PendingIntent.getBroadcast(context, alarmId, notifyIntent, 0);
            mAlarmManager.cancel(cancelIntent);
        }
    }
}
