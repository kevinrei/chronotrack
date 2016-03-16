package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
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

import java.util.List;
import java.util.concurrent.TimeUnit;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder> {

    /** Day & Time */
    private static final int SECOND = 1000;
    private static final int MINUTE = 60 * SECOND;
    private static final int HOUR = 60 * MINUTE;
    private static final int DAY = 24 * HOUR;

    private MySQLiteHelper db;
    private List<Alarm> alarms;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView mAlarmToggle;
        private final EditText mAlarmLabel;
        private final TextView mAlarmTrigger;
        private final TextView mAlarmCategory;
        private final ImageView mAlarmDelete;

        public ViewHolder(View v) {
            super(v);

            mAlarmToggle = (ImageView) v.findViewById(R.id.alarm_toggle);
            mAlarmLabel = (EditText) v.findViewById(R.id.label_alarm);
            mAlarmTrigger = (TextView) v.findViewById(R.id.alarm_trigger_time);
            mAlarmCategory = (TextView) v.findViewById(R.id.category_alarm);
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
                .inflate(R.layout.item_alarm, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by LayoutManager)
    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Alarm alarm = alarms.get(position);

        Log.d("Save", String.valueOf(alarm.getSave()));

        // If alarm is saved, enable toggle
        viewHolder.mAlarmToggle.setEnabled(alarm.getSave() == 1);

        viewHolder.mAlarmLabel.setText(alarm.getLabel());

        String triggerTime = getAlarmTriggerTime(alarm);
        viewHolder.mAlarmTrigger.setText(triggerTime);

        String category = getAlarmCategory(viewHolder, alarm.getFlag());
        viewHolder.mAlarmCategory.setText(category);

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

    private String getAlarmTriggerTime(Alarm alarm) {
        StringBuilder trigger = new StringBuilder("");
        long value = 0;
        long time = alarm.getTrigger();

        if (time > DAY) {
            value = time / DAY;
            if (value == 1) {
                trigger.append(time / DAY).append(" day ");
            } else {
                trigger.append(time / DAY).append(" days ");
            }
            time %= DAY;
        }

        if (time > HOUR) {
            value = time / HOUR;
            if (value == 1) {
                trigger.append(time / HOUR).append(" hour ");
            } else {
                trigger.append(time / HOUR).append(" hours ");
            }
            time %= HOUR;
        }

        if (time > MINUTE) {
            value = time / MINUTE;
            if (value == 1) {
                trigger.append(time / MINUTE).append(" minute ");
            } else {
                trigger.append(time / MINUTE).append(" minutes ");
            }
        }

        return trigger.toString();
    }

    private String getAlarmCategory(ViewHolder viewHolder, int flag) {
        String category = "";
        Resources item = viewHolder.itemView.getResources();

        if (flag == 1) {
            category = item.getString(R.string.stamina_alarm);
        } else if (flag == 2) {
            category = item.getString(R.string.condition_datetime_alarm);
        } else if (flag == 3) {
            category = item.getString(R.string.condition_countdown_alarm);
        }

        return category;
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
