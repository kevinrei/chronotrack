package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

/**
 * SavedAlarmAdapter is for the RecyclerView in GameDetailActivity
 * */
public class SavedAlarmAdapter extends RecyclerView.Adapter<SavedAlarmAdapter.ViewHolder> {

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private MySQLiteHelper db;
    private List<Alarm> alarms;

    private CountDownTimer mCountdown;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAlarmToggle;
        private final EditText mAlarmLabel;
        private final TextView mTriggerTime;
        private final TextView mTimeLeft;
        private final TextView mStaminaProgress;
        private final ProgressBar mProgressBar;
        private final ImageView mAlarmDelete;

        public ViewHolder(View v) {
            super(v);

            mAlarmToggle = (ImageView) v.findViewById(R.id.alarm_toggle);
            mAlarmLabel = (EditText) v.findViewById(R.id.label_alarm);
            mTriggerTime = (TextView) v.findViewById(R.id.alarm_trigger_time);
            mTimeLeft = (TextView) v.findViewById(R.id.time_left);
            mStaminaProgress = (TextView) v.findViewById(R.id.stamina_progress);
            mProgressBar = (ProgressBar) v.findViewById(R.id.progressBar);
            mAlarmDelete = (ImageView) v.findViewById(R.id.delete_alarm);
        }
    }

    public SavedAlarmAdapter(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    // Create new views (invoked by LayoutManager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_saved_alarm, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by LayoutManager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Alarm alarm = alarms.get(position);
        int flag = alarm.getFlag();
        int start = alarm.getStart();
        int end = alarm.getEnd();

        db = new MySQLiteHelper(viewHolder.itemView.getContext());
        Game game = db.getGame(alarm.getGameId());

        // If alarm is saved, enable toggle
        viewHolder.mAlarmToggle.setEnabled(alarm.getSave() == 1);

        // Alarm label
        viewHolder.mAlarmLabel.setText(alarm.getLabel());

        // The exact trigger time
        String triggerTime = getAlarmTriggerTime(alarm.getTrigger());
        viewHolder.mTriggerTime.setText(triggerTime);

        // Time remaining until alarm is fired
        mCountdown = new CountDownTimer(alarm.getTrigger(), game.getRecoveryRate()) {
            @Override
            public void onTick(long millisUntilFinished) {
                viewHolder.mTimeLeft.setText(getAlarmTriggerTime(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                String complete = "Alarm triggered!";
                viewHolder.mTimeLeft.setText(complete);
            }
        };
        mCountdown.start();

        // Stamina progress, only displayed if flag == 1
        if (flag == 1) {
            viewHolder.mStaminaProgress.setVisibility(View.VISIBLE);
            String stamina = start + "/" + end;
            viewHolder.mStaminaProgress.setText(stamina);
        } else {
            viewHolder.mStaminaProgress.setVisibility(View.GONE);
        }

        // Progress bar

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Alarm Item", "Alarm at position " + position + " clicked.");
            }
        });

        viewHolder.mAlarmToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Alarm toggle", "Toggle clicked.");
            }
        });

        viewHolder.mAlarmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(v, alarm);
            }
        });
    }

    // Return the size of the data set (invoked by LayoutManager)
    @Override
    public int getItemCount() {
        return alarms.size();
    }


    /** Custom methods */

    private String getAlarmTriggerTime(long triggerTime) {
        StringBuilder trigger = new StringBuilder("");
        long value;
        long time = triggerTime;

        if (time >= DAY) {
            value = time / DAY;
            if (value == 1) {
                trigger.append(time / DAY).append(" day ");
            } else {
                trigger.append(time / DAY).append(" days ");
            }
            time %= DAY;
        }

        if (time >= HOUR) {
            value = time / HOUR;
            if (value == 1) {
                trigger.append(time / HOUR).append(" hour ");
            } else {
                trigger.append(time / HOUR).append(" hours ");
            }
            time %= HOUR;
        }

        if (time >= MINUTE) {
            value = time / MINUTE;
            if (value == 1) {
                trigger.append(time / MINUTE).append(" minute ");
            } else {
                trigger.append(time / MINUTE).append(" minutes ");
            }
        }

        // Only show seconds when less than a minute
        else if (time >= SECOND) {
            value = time / SECOND;
            if (value == 1) {
                trigger.append(time / SECOND).append( "second" );
            } else {
                trigger.append(time / SECOND).append(" seconds ");
            }
        }

        return trigger.toString();
    }

    private void setProgressBar(ProgressBar bar) {

    }

    /** Alert Dialogs */

    private void showDeleteDialog(final View v, final Alarm alarm) {
        final Context context = v.getContext();
        db = new MySQLiteHelper(context);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        mBuilder.setTitle("Deleting alarm")
                .setMessage(R.string.delete_confirm_alarm)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // cancelDeletedAlarm(v.getContext(), alarm.getAlarmId());
                        AddAlarmActivity.cancelAlarm(alarm.getAlarmId());
                        db.deleteAlarm(alarm);
                        Snackbar.make(v,
                                "Successfully deleted the alarm.",
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .create()
                .show();
    }
}
