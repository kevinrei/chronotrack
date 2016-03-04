package com.kevinrei.chronotrack;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private static final String TAG = "GameAdapter";
    private List<Game> games;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView mGameTitle;
        private final TextView mRateTitle;
        private final TextView mStaminaTitle;

        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            mGameTitle = (TextView) v.findViewById(R.id.game_title);
            mRateTitle = (TextView) v.findViewById(R.id.game_rate);
            mStaminaTitle = (TextView) v.findViewById(R.id.game_max);
        }
    }

    public GameAdapter(List<Game> games) {
        this.games = games;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.game_item, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Game game = games.get(position);

        viewHolder.mGameTitle.setText(game.getTitle());
        viewHolder.mRateTitle.setText(getRateString(game.getUnit(), game.getRecoveryRate()));
        viewHolder.mStaminaTitle.setText(getMaxString(game.getMaxStamina()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return games.size();
    }

    public String getRateString(String unit, int rate) {
        String result, timeUnit, rateValueString;
        int rateValue;

        rateValue = rate / 60;

        // Get the unit of time measurement
        if (rateValue == 1) {
            timeUnit = "minute";
        } else if (rateValue < 60) {
            timeUnit = "minutes";
        } else if (rateValue == 60) {
            rateValue /= 60;
            timeUnit = "hour";
        } else {
            rateValue /= 60;
            timeUnit = "hours";
        }

        // Remove number if it's 1 minute or 1 hour
        if (rateValue == 1) {
            rateValueString = "";
        } else {
            rateValueString = String.valueOf(rateValue);
        }

        result = "Recovery rate: 1 " + unit + " every " + rateValueString + " " + timeUnit;
        return result;
    }

    public String getMaxString(int max) {
        return "Maximum stamina value: " + max;
    }
}