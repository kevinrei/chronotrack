package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder>
        implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    public static class Action {
        public final int icon;
        public final String action;
        public Action(Integer icon, String action) {
            this.icon = icon;
            this.action = action;
        }

        @Override
        public String toString() {
            return action;
        }
    }

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

                final Action[] actions = {
                        new Action(R.drawable.ic_add_alarm, "Create Alarm"),
                        new Action(R.drawable.ic_mode_edit, "Edit"),
                        new Action(R.drawable.ic_delete, "Delete")
                };

                TypedArray typedArray = v.getContext().obtainStyledAttributes(null,
                        R.styleable.AlertDialog, R.attr.alertDialogStyle, 0);

                ListAdapter adapter = new ArrayAdapter<Action>(v.getContext(),
                        typedArray.getResourceId(R.styleable.AlertDialog_listItemLayout, 0),
                        android.R.id.text1, actions){
                    public View getView(int position, View convertView, ViewGroup parent) {
                        //Use super class to create the View
                        View v = super.getView(position, convertView, parent);
                        TextView tv = (TextView) v.findViewById(android.R.id.text1);

                        //Put the image on the TextView
                        tv.setCompoundDrawablesWithIntrinsicBounds(actions[position].icon, 0, 0, 0);

                        //Add margin between image and text (support various screen densities)
                        int dp8 = (int) (8 * v.getContext().getResources().getDisplayMetrics().density);
                        tv.setCompoundDrawablePadding(dp8);

                        return v;
                    }
                };

                typedArray.recycle();

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
                mBuilder.setTitle(game.getTitle());
                mBuilder.setCancelable(false);

                mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                mBuilder.setAdapter(adapter, new DialogInterface.OnClickListener() {
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
}