package com.kevinrei.chronotrack;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class InstalledAdapter extends ArrayAdapter<ApplicationInfo> {
    private List<ApplicationInfo> installedApps = null;
    private Context context;
    private PackageManager pm;

    public InstalledAdapter(Context context, int textViewResourceId,
                              List<ApplicationInfo> installedApps) {
        super(context, textViewResourceId, installedApps);
        this.context = context;
        this.installedApps = installedApps;
        pm = context.getPackageManager();
    }

    @Override
    public int getCount() {
        return ((null != installedApps) ? installedApps.size() : 0);
    }

    @Override
    public ApplicationInfo getItem(int position) {
        return ((null != installedApps) ? installedApps.get(position) : null);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (null == view) {
            LayoutInflater layoutInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.item_installed_app, null);
        }

        ApplicationInfo applicationInfo = installedApps.get(position);
        if (null != applicationInfo) {
            ImageView appIcon = (ImageView) view.findViewById(R.id.app_img);
            TextView appTitle = (TextView) view.findViewById(R.id.app_title);

            appIcon.setImageDrawable(applicationInfo.loadIcon(pm));
            appTitle.setText(applicationInfo.loadLabel(pm));
        }

        return view;
    }
}
