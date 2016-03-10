package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder>
        implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    private List<Game> games;
    private OnStartDragListener mStartDragListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {

        private final ImageView mGameImage;
        private final TextView mGameTitle;
        private final TextView mGameCategory;
        private final ImageView mReorderHandle;

        public ViewHolder(View v) {
            super(v);

            mGameImage = (ImageView) v.findViewById(R.id.game_img);
            mGameTitle = (TextView) v.findViewById(R.id.game_title);
            mGameCategory = (TextView) v.findViewById(R.id.game_category);
            mReorderHandle = (ImageView) v.findViewById(R.id.reorder_handle);
        }

        @Override
        public void onItemSelected() {
            // itemView.setBackgroundResource(R.color.colorPrimaryDark);
            itemView.setBackgroundColor(0);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }

    public GameAdapter(List<Game> games, OnStartDragListener mStartDragListener) {
        this.games = games;
        this.mStartDragListener = mStartDragListener;
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
    public void onBindViewHolder(final ViewHolder viewHolder, final int position) {
        final Game game = games.get(position);

        Picasso.with(viewHolder.mGameImage.getContext())
                .load(Uri.parse(game.getImage()))
                .into(viewHolder.mGameImage);
        viewHolder.mGameTitle.setText(game.getTitle());
        viewHolder.mGameCategory.setText(game.getCategory());

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent i = new Intent(context, GameDetailActivity.class);
                i.putExtra("game_id", game.getId());
                context.startActivity(i);
            }
        });

        viewHolder.mReorderHandle.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setSelected(false);
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.onStartDrag(viewHolder);
                }

                return false;
            }
        });

        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View view = v;

                CharSequence[] options = new CharSequence[] {
                        "Create an alarm",
                        "Edit entry",
                        "Delete the game"
                };

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                mBuilder.setTitle("Select an action");
                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                mBuilder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Log.d("Option 1", "Create an alarm");
                        }

                        else if (which == 1) {
                            Log.d("Option 2", "Edit entry");
                        }

                        else {
                            Log.d("Option 3", "Delete the game");
                            showDeleteDialog(view, game);
                        }
                    }
                });
                mBuilder.show();
                return false;
            }
        });
    }

    // Return the size of the data set (invoked by LayoutManager)
    @Override
    public int getItemCount() {
        return games.size();
    }

    @Override
    public void onItemDismiss(int position) {
        games.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, games.size());
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(games, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(games, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    /** Alert Dialogs */

    private void showDeleteDialog(final View v, final Game game) {
        final Context context = v.getContext();
        final MySQLiteHelper db = new MySQLiteHelper(context);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        String deleteTitle = "Deleting " + game.getTitle() + "...";
        final String confirmDelete = "Successfully deleted " + game.getTitle() + ".";
        mBuilder.setTitle(deleteTitle)
                .setMessage(R.string.delete_confirm)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        db.deleteGame(game);
                        Snackbar.make(v, confirmDelete, Snackbar.LENGTH_LONG).show();
                    }
                })
                .create()
                .show();
    }

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