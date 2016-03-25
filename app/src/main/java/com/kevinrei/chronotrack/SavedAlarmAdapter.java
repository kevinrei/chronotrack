package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.os.CountDownTimer;
import android.preference.PreferenceActivity;
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

import org.w3c.dom.Text;

import java.util.List;

/**
 * SavedAlarmAdapter is for the RecyclerView in GameDetailActivity
 * */
public class SavedAlarmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Action code */
    private static final int HEADER_VIEW = 1;
    private static final int ALARM_VIEW = 2;

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private MySQLiteHelper db;
    private List<Alarm> alarms;
    private int gameId;

    private CountDownTimer mCountdown;

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        private final TextView mLabelMax;
        private final TextView mMaxValue;
        private final TextView mRecoveryRate;
        private final TextView mFullRecovery;

        public HeaderViewHolder(View v) {
            super(v);

            mLabelMax = (TextView) v.findViewById(R.id.lbl_max);
            mMaxValue = (TextView) v.findViewById(R.id.tv_max);
            mRecoveryRate = (TextView) v.findViewById(R.id.tv_rcv_rate);
            mFullRecovery = (TextView) v.findViewById(R.id.tv_full_rcv);
        }
    }

    public static class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAlarmToggle;
        private final EditText mAlarmLabel;
        private final TextView mTriggerTime;
        private final TextView mTimeLeft;
        private final TextView mStaminaProgress;
        private final ProgressBar mProgressBar;
        private final ImageView mAlarmDelete;

        public AlarmViewHolder(View v) {
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

    public SavedAlarmAdapter(int gameId, List<Alarm> alarms) {
        this.gameId = gameId;
        this.alarms = alarms;
    }

    // Create new views (invoked by LayoutManager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v;

        if (viewType == HEADER_VIEW) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_game_detail, viewGroup, false);
            return new HeaderViewHolder(v);
        }

        else {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.card_saved_alarm, viewGroup, false);
            return new AlarmViewHolder(v);
        }
    }

    // Replace the contents of a view (invoked by LayoutManager)
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder viewHolder, final int position) {
        db = new MySQLiteHelper(viewHolder.itemView.getContext());
        Game game = db.getGame(gameId);

        String gameUnit = game.getUnit();
        int gameRate = game.getRecoveryRate();
        int gameMax = game.getMaxStamina();

        switch(viewHolder.getItemViewType()) {
            case HEADER_VIEW:
                HeaderViewHolder headerViewHolder = (HeaderViewHolder) viewHolder;

                // Max [unit] value:
                String labelMax = "Max " + gameUnit + " value:";
                headerViewHolder.mLabelMax.setText(labelMax);

                // Max stamina value
                if (gameMax == 0) {
                    headerViewHolder.mMaxValue.setText("N/A");
                } else {
                    headerViewHolder.mMaxValue.setText(String.valueOf(gameMax));
                }

                // Recovery rate
                if (gameRate == 0) {
                    headerViewHolder.mRecoveryRate.setText("N/A");
                } else {
                    headerViewHolder.mRecoveryRate.setText(getRateString(gameUnit, gameRate));
                }

                // Full recovery time
                headerViewHolder.mFullRecovery.setText(calculateTime(gameRate * gameMax));

                break;

            case ALARM_VIEW:
                final AlarmViewHolder alarmViewHolder = (AlarmViewHolder) viewHolder;

                final Alarm alarm = alarms.get(position - 1);
                int flag = alarm.getFlag();
                int start = alarm.getStart();
                int end = alarm.getEnd();

                // If alarm is saved, enable toggle
                alarmViewHolder.mAlarmToggle.setEnabled(alarm.getSave() == 1);

                // Alarm label
                alarmViewHolder.mAlarmLabel.setText(alarm.getLabel());

                // The exact trigger time
                String triggerTime = getAlarmTriggerTime(alarm.getTrigger());
                alarmViewHolder.mTriggerTime.setText(triggerTime);

                // Time remaining until alarm is fired
                mCountdown = new CountDownTimer(alarm.getTrigger(), gameRate) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        alarmViewHolder.mTimeLeft.setText(getAlarmTriggerTime(millisUntilFinished));
                    }

                    @Override
                    public void onFinish() {
                        String complete = "Alarm triggered!";
                        alarmViewHolder.mTimeLeft.setText(complete);
                    }
                };
                mCountdown.start();

                // Stamina progress, only displayed if flag == 1
                if (flag == 1) {
                    alarmViewHolder.mStaminaProgress.setVisibility(View.VISIBLE);
                    String stamina = start + "/" + end;
                    alarmViewHolder.mStaminaProgress.setText(stamina);
                } else {
                    alarmViewHolder.mStaminaProgress.setVisibility(View.GONE);
                }

                // Progress bar

                alarmViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Alarm Item", "Alarm at position " + position + " clicked.");
                    }
                });

                alarmViewHolder.mAlarmToggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("Alarm toggle", "Toggle clicked.");
                    }
                });

                alarmViewHolder.mAlarmDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteDialog(v, alarm);
                    }
                });

                break;
        }
    }

    // Return the size of the data set (invoked by LayoutManager)
    @Override
    public int getItemCount() {
        if (alarms == null) {
            return 0;
        }

        if (alarms.size() == 0) {
            return 1;
        }

        return alarms.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        return position == 0 ? HEADER_VIEW : ALARM_VIEW;
    }


    /** Custom methods */

    public String getRateString(String unit, int rate) {
        String result, timeUnit, rateValueString;
        int rateValue;

        rateValue = rate / 60;

        // Get the unit of time measurement
        if (rateValue == 1) {
            timeUnit = "minute";
        } else if (rateValue < 60) {
            timeUnit = " minutes";
        } else if (rateValue == 60) {
            rateValue /= 60;
            timeUnit = "hour";
        } else {
            rateValue /= 60;
            timeUnit = " hours";
        }

        // Remove number if it's 1 minute or 1 hour
        if (rateValue == 1) {
            rateValueString = "";
        } else {
            rateValueString = String.valueOf(rateValue);
        }

        result = "1 " + unit + " every " + rateValueString + timeUnit;
        return result;
    }

    private static String calculateTime(long totalSeconds) {
        if (totalSeconds == 0) {
            return "N/A";
        }

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        long totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        long minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        long hours = totalMinutes / MINUTES_IN_AN_HOUR;

        if (hours == 0) {
            return minutes + " minutes";
        }

        return hours + " hours " + minutes + " minutes";
    }

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
