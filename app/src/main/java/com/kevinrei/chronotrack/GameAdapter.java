package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;

public class GameAdapter extends RecyclerView.Adapter<GameAdapter.ViewHolder>
        implements SimpleItemTouchHelperCallback.ItemTouchHelperAdapter {

    /** Action code */
    private static final int ADD_NEW_ALARM = 2;

    public interface OnStartDragListener {
        void onStartDrag(RecyclerView.ViewHolder viewHolder);
    }

    private List<Game> games;
    private OnStartDragListener mStartDragListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {

        private final RelativeLayout mGameDetail;
        private final ImageView mGameImage;
        private final TextView mGameTitle;
        private final TextView mGameCategory;
        private final TextView mGameCreate;
        private final TextView mGameEdit;
        private final TextView mGameDelete;

        public ViewHolder(View v) {
            super(v);

            mGameDetail = (RelativeLayout) v.findViewById(R.id.card_detail);
            mGameImage = (ImageView) v.findViewById(R.id.game_img);
            mGameTitle = (TextView) v.findViewById(R.id.card_title);
            mGameCategory = (TextView) v.findViewById(R.id.card_category);
            mGameCreate  = (TextView) v.findViewById(R.id.card_create);
            mGameEdit = (TextView) v.findViewById(R.id.card_edit);
            mGameDelete = (TextView) v.findViewById(R.id.card_delete);
        }

        @Override
        public void onItemSelected() {
            // itemView.setBackgroundResource(R.color.colorPrimaryDark);
        }

        @Override
        public void onItemClear() {
            // itemView.setBackgroundColor(0);
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
                .inflate(R.layout.card_game, viewGroup, false);

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

        // Set listeners to card items
        viewHolder.mGameDetail.setOnClickListener(new CardClickListener(game));
        viewHolder.mGameCreate.setOnClickListener(new CardClickListener(game));
        viewHolder.mGameEdit.setOnClickListener(new CardClickListener(game));
        viewHolder.mGameDelete.setOnClickListener(new CardClickListener(game));

        viewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                v.setSelected(false);
                if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                    mStartDragListener.onStartDrag(viewHolder);
                }

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
                .setMessage(R.string.delete_confirm_game)
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

    /** Listeners */

    public class CardClickListener implements View.OnClickListener {
        Game game;

        public CardClickListener(Game game) {
            this.game = game;
        }

        @Override
        public void onClick(View v) {
            Context context = v.getContext();

            switch(v.getId()) {

                case R.id.card_detail:
                    Intent i = new Intent(context, GameDetailActivity.class);
                    i.putExtra("game_id", game.getId());
                    context.startActivity(i);
                    break;

                case R.id.card_create:
                    Intent alarmIntent = new Intent(context, AddAlarmActivity.class);
                    alarmIntent.putExtra("game", game);
                    ((MainActivity) context).startActivityForResult(alarmIntent, ADD_NEW_ALARM);
                    break;

                case R.id.card_edit:
                    Intent gameIntent = new Intent(context, NewGameActivity.class);
                    gameIntent.putExtra("flag", 2);
                    gameIntent.putExtra("game", game);
                    context.startActivity(gameIntent);
                    break;

                case R.id.card_delete:
                    showDeleteDialog(v, game);
                    break;

                default:
                    break;
            }
        }
    }
}