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
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
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

    public static List<Game> games;
    private OnStartDragListener mStartDragListener;

    public static class ViewHolder extends RecyclerView.ViewHolder
            implements SimpleItemTouchHelperCallback.ItemTouchHelperViewHolder {

        private final RelativeLayout mGameDetail;
        private final ImageView mGameImage;
        private final TextView mGameTitle;
        private final TextView mGameCategory;
        private final ImageView mGameReorder;
        private final TextView mGameCreate;
        private final TextView mGameEdit;
        private final TextView mGameDelete;

        public ViewHolder(View v) {
            super(v);

            mGameDetail = (RelativeLayout) v.findViewById(R.id.card_detail);
            mGameImage = (ImageView) v.findViewById(R.id.game_img);
            mGameTitle = (TextView) v.findViewById(R.id.card_title);
            mGameCategory = (TextView) v.findViewById(R.id.card_category);
            mGameReorder = (ImageView) v.findViewById(R.id.reorder_handle);
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
        viewHolder.mGameDetail.setOnClickListener(new CardClickListener(game, position));
        viewHolder.mGameCreate.setOnClickListener(new CardClickListener(game, position));
        viewHolder.mGameEdit.setOnClickListener(new CardClickListener(game, position));
        viewHolder.mGameDelete.setOnClickListener(new CardClickListener(game, position));

        viewHolder.mGameReorder.setOnTouchListener(new View.OnTouchListener() {
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

    /** Listeners */

    public class CardClickListener implements View.OnClickListener {
        Game game;
        int position;

        public CardClickListener(Game game, int position) {
            this.game = game;
            this.position = position;
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
                    if (game.getCategory().equals("Mobile game") && game.getRecoveryRate() != 0) {
                        showAlarmDialogWithStamina(context, game);
                    } else {
                        showAlarmDialogWithoutStamina(context, game);
                    }
                    break;

                case R.id.card_edit:
                    Intent gameIntent = new Intent(context, NewGameActivity.class);
                    gameIntent.putExtra("flag", 2);
                    gameIntent.putExtra("game", game);
                    context.startActivity(gameIntent);
                    break;

                case R.id.card_delete:
                    showDeleteDialog(v, game, position);
                    break;

                default:
                    break;
            }
        }
    }

    /** Alert Dialogs */

    private void showAlarmDialogWithStamina(final Context context, final Game game) {
        CharSequence[] options = new CharSequence[] { "Stamina", "Date & Time", "Countdown" };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Alarm Type");
        mBuilder.setCancelable(false);

        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, AddAlarmActivity.class);
                i.putExtra("flag", which);
                i.putExtra("game", game);
                ((MainActivity) context).startActivityForResult(i, ADD_NEW_ALARM);
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mBuilder.show();
    }

    private void showAlarmDialogWithoutStamina(final Context context, final Game game) {
        CharSequence[] options = new CharSequence[] { "Date & Time", "Countdown" };

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setTitle("Select Alarm Type");
        mBuilder.setCancelable(false);

        mBuilder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent i = new Intent(context, AddAlarmActivity.class);
                i.putExtra("flag", which + 1);
                i.putExtra("game", game);
                ((MainActivity) context).startActivityForResult(i, ADD_NEW_ALARM);
            }
        });

        mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        mBuilder.show();
    }

    private void showDeleteDialog(final View v, final Game game, final int position) {
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

                        games.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, games.size());

                        Snackbar.make(v, confirmDelete, Snackbar.LENGTH_LONG).show();
                    }
                })
                .create()
                .show();
    }
}