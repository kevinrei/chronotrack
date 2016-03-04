package com.kevinrei.chronotrack;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GameListFragment extends Fragment {

    private static final String TAG = "GameListFragment";
    private MySQLiteHelper db;

    protected RecyclerView mRecyclerView;
    protected GameAdapter mGameAdapter;
    protected List<Game> games;

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
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mGameAdapter = new GameAdapter(games);
        mRecyclerView.setAdapter(mGameAdapter);
        checkGameList();

        return rootView;
    }

    private void checkGameList() {
        if (mGameAdapter.getItemCount() == 0) {
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}
