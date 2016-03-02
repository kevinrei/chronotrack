package com.kevinrei.chronotrack;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class GameListFragment extends Fragment {

    private static final String TAG = "GameListFragment";
    private static final int DATASET_COUNT = 60;

    protected RecyclerView mRecyclerView;
    protected GameAdapter mGameAdapter;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected String[] mDataSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDataSet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_games_list, container, false);
        rootView.setTag(TAG);

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);

        mLayoutManager = new LinearLayoutManager(getActivity());

        mGameAdapter = new GameAdapter(mDataSet);
        mRecyclerView.setAdapter(mGameAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {

        // Refresh recycler view here

        super.onSaveInstanceState(savedInstanceState);
    }

    private void initDataSet() {
        mDataSet = new String[DATASET_COUNT];
        for (int i = 0; i < DATASET_COUNT; i++) {
            mDataSet[i] = "This is element #" + i;
        }
    }
}
