package com.kevinrei.chronotrack;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstalledAppActivity extends AppCompatActivity {

    /** Application */
    private PackageManager pm;
    private List<ApplicationInfo> installedApps;
    private InstalledAdapter mInstalledAdapter;

    /** Views */
    ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_app);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.lv_installed_apps);
        pm = getPackageManager();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ApplicationInfo app = installedApps.get(position);
                Intent i = new Intent();

                i.putExtra("flag", 1);

                i.putExtra("app_title", app.loadLabel(pm).toString());

                String icon = "android.resource://" + app.packageName + "/" + app.icon;
                i.putExtra("app_icon", icon);

                setResult(RESULT_OK, i);
                finish();
            }
        });

        new LoadApplications().execute();
    }

    private List<ApplicationInfo> checkForLaunchIntent(List<ApplicationInfo> list) {
        ArrayList<ApplicationInfo> installedList = new ArrayList<>();
        for (ApplicationInfo info : list) {
            try {
                if (null != pm.getLaunchIntentForPackage(info.packageName)) {
                    installedList.add(info);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(installedList, new ApplicationInfo.DisplayNameComparator(pm));
        return installedList;
    }

    private class LoadApplications extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {
            installedApps = checkForLaunchIntent(pm.getInstalledApplications(PackageManager.GET_META_DATA));
            mInstalledAdapter = new InstalledAdapter(InstalledAppActivity.this,
                    R.layout.item_installed_app, installedApps);
            return null;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPostExecute(Void result) {
            mListView.setAdapter(mInstalledAdapter);
            progress.dismiss();
            super.onPostExecute(result);
        }

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(InstalledAppActivity.this, null,
                    "Loading application info...");
            super.onPreExecute();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }
}
