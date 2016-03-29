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
import android.widget.TextView;

import java.util.List;

/**
 * AlarmListFragment is the ViewPager for the list of alarms set by the user.
 */
public class AlarmListFragment extends Fragment {

    private static final String TAG = "AlarmListFragment";
    private static final int HORIZONTAL_MARGIN = 32;
    private static final int VERTICAL_MARGIN = 32;

    protected MySQLiteHelper db;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected List<Alarm> alarms;

    public static  AlarmAdapter mAlarmAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_alarms_list, container, false);
        rootView.setTag(TAG);

        db = new MySQLiteHelper(getActivity());
        alarms = db.getAllAlarms();

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerView);

        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new SpacesItemDecoration(HORIZONTAL_MARGIN, VERTICAL_MARGIN));

        mAlarmAdapter = new AlarmAdapter(alarms);
        mRecyclerView.setAdapter(mAlarmAdapter);

        return rootView;
    }
}
