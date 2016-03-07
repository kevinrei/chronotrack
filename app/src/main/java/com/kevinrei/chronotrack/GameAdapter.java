package com.kevinrei.chronotrack;

import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder> {

    private static final String TAG = "GameAdapter";
    private List<Game> games;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mGameImage;
        private final TextView mGameTitle;
        private final TextView mGameCategory;

        public ViewHolder(View v) {
            super(v);

            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Element " + getPosition() + " clicked.");
                }
            });

            mGameImage = (ImageView) v.findViewById(R.id.game_img);
            mGameTitle = (TextView) v.findViewById(R.id.game_title);
            mGameCategory = (TextView) v.findViewById(R.id.game_category);
        }
    }

    public GameAdapter(List<Game> games) {
        this.games = games;
    }

    // Create new views (invoked by LayoutManager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_game, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by LayoutManager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        Log.d(TAG, "Element " + position + " set.");
        Game game = games.get(position);

        Log.d("Bitmap", "Image string: " + game.getImage());
        // viewHolder.mGameImage.setImageBitmap(convertToBitmap(game.getImage()));
        viewHolder.mGameImage.setImageURI(Uri.parse(game.getImage()));
        viewHolder.mGameTitle.setText(game.getTitle());
        viewHolder.mGameCategory.setText(game.getCategory());
    }

    // Return the size of the data set (invoked by LayoutManager)
    @Override
    public int getItemCount() {
        return games.size();
    }

/*    public Bitmap convertToBitmap(String encoded) {
        try {
            byte[] encodeByte = Base64.decode(encoded, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }*/

/*    public String getRateString(String unit, int rate) {
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
    }*/
}