package com.kevinrei.chronotrack;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * GameListFragment is the ViewPager for the list of games entered by the user.
 */
public class GameListFragment extends Fragment implements GameAdapter.OnStartDragListener {

    private static final String TAG = "GameListFragment";
    private static final int HORIZONTAL_MARGIN = 32;
    private static final int VERTICAL_MARGIN = 32;

    protected MySQLiteHelper db;
    protected RecyclerView mRecyclerView;
    protected List<Game> games;

    protected RecyclerView.LayoutManager mLayoutManager;
    protected ItemTouchHelper mItemTouchHelper;

    public static  GameAdapter mGameAdapter;
    Parcelable mListState;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games_list, container, false);
        rootView.setTag(TAG);

        db = new MySQLiteHelper(getActivity());
        games = db.getAllGames();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(HORIZONTAL_MARGIN, VERTICAL_MARGIN));

        mGameAdapter = new GameAdapter(games, this);
        mRecyclerView.setAdapter(mGameAdapter);

        // Attach ItemTouchHelper
        ItemTouchHelper.Callback mCallback = new SimpleItemTouchHelperCallback(mGameAdapter);
        mItemTouchHelper = new ItemTouchHelper(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        return rootView;
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        mListState = mLayoutManager.onSaveInstanceState();
        savedInstanceState.putParcelable("state_key", mListState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (mListState != null) {
            mListState = savedInstanceState.getParcelable("state_key");
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mListState != null) {
            mLayoutManager.onRestoreInstanceState(mListState);
        }
    }
}
