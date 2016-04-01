package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
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
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private MySQLiteHelper db;

    public static  List<Alarm> alarms;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mGameIcon;
        private final EditText mAlarmLabel;
        private final TextView mTriggerTime;
        private final TextView mTimeLeft;
        private final ImageView mAlarmDelete;

        public ViewHolder(View v) {
            super(v);

            mGameIcon = (ImageView) v.findViewById(R.id.game_icon);
            mAlarmLabel = (EditText) v.findViewById(R.id.label_alarm);
            mTriggerTime = (TextView) v.findViewById(R.id.alarm_trigger_time);
            mTimeLeft = (TextView) v.findViewById(R.id.time_left);
            mAlarmDelete = (ImageView) v.findViewById(R.id.delete_alarm);
        }
    }

    public AlarmAdapter(List<Alarm> alarms) {
        this.alarms = alarms;
    }

    // Create new views (invoked by LayoutManager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.card_active_alarm, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by LayoutManager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Alarm alarm = alarms.get(position);

        db = new MySQLiteHelper(viewHolder.itemView.getContext());
        Game game = db.getGame(alarm.getGameId());

        // Set game icon
        Picasso.with(viewHolder.mGameIcon.getContext())
                .load(Uri.parse(game.getImage()))
                .into(viewHolder.mGameIcon);

        // Alarm label
        viewHolder.mAlarmLabel.setText(alarm.getLabel());

        // The exact trigger time
        String triggerTime = getAlarmTriggerTime(alarm.getTrigger());
        viewHolder.mTriggerTime.setText(triggerTime);

        // Time remaining until alarm is fired
        Countdown mCountdown = new Countdown(viewHolder.mTimeLeft);
        mCountdown.updateTextAndProgress(alarm);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Alarm Item", "Alarm at position " + position + " clicked.");
            }
        });

        viewHolder.mAlarmDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteDialog(v, alarm, position);
            }
        });
    }

    // Return the size of the data set (invoked by LayoutManager)
    @Override
    public int getItemCount() {
        return alarms.size();
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


    /** Alert Dialogs */

    private void showDeleteDialog(final View v, final Alarm alarm, final int position) {
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
                        AlarmController controller = new AlarmController();
                        controller.cancelAlarm(context, alarm);
                        db.deleteAlarm(alarm);

                        alarms.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, alarms.size());

                        Snackbar.make(v,
                                "Successfully deleted the alarm.",
                                Snackbar.LENGTH_LONG).show();
                    }
                })
                .create()
                .show();
    }
}
