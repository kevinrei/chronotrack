package com.kevinrei.chronotrack;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class GameListFragment extends Fragment {

    private static final String TAG = "GameListFragment";
    private static final int HORIZONTAL_MARGIN = 72;
    private static final int VERTICAL_MARGIN = 108;

    protected MySQLiteHelper db;
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
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(HORIZONTAL_MARGIN, VERTICAL_MARGIN));

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

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int horizontalMargin;
        private int verticalMargin;

        public SpacesItemDecoration(int horizontalMargin, int verticalMargin) {
            this.horizontalMargin = horizontalMargin;
            this.verticalMargin = verticalMargin;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = horizontalMargin;
            outRect.right = horizontalMargin;
            outRect.bottom = verticalMargin;

            // Add top margin only for the first item to avoid double space between items
            if(parent.getChildAdapterPosition(view) == 0)
                outRect.top = verticalMargin - 48;
        }
    }
}
