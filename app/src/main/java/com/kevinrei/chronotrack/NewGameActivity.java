package com.kevinrei.chronotrack;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Arrays;
import java.util.UUID;

public class NewGameActivity extends AppCompatActivity {

    /** Action code */
    private static final int REQUEST_EXTERNAL_READ_PERMISSION = 0;

    /** Database and data values*/
    private MySQLiteHelper db;
    private int flag;

    int appId;
    String gameTitle;
    String gameImage;
    String gameCategory;
    String staminaUnit;
    int recoveryRate;
    int maxStamina;

    /** Widgets and Fields */
    protected View mView;
    protected LinearLayout mMobileLayout;
    protected EditText mTitle;
    protected ImageView mImage;
    protected Spinner mCategory;
    protected EditText mUnit;
    protected Spinner mRecovery;
    protected EditText mStamina;

    /** Image */
    private String imgContent = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        mView = findViewById(R.id.main_content);

        db = new MySQLiteHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set back (home) navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_home);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mMobileLayout = (LinearLayout) findViewById(R.id.layout_mobile);
        mTitle = (EditText) findViewById(R.id.hint_title);
        mImage = (ImageView) findViewById(R.id.img_game);
        mCategory = (Spinner) findViewById(R.id.spn_category);
        mUnit = (EditText) findViewById(R.id.hint_unit);
        mRecovery = (Spinner) findViewById(R.id.spn_rate);
        mStamina = (EditText) findViewById(R.id.hint_max);

        // Get the intent and its flag, which is 1 or 2.
        // 1 means an installed activity is being added.
        // 2 means a game detail is being edited.
        Intent i = getIntent();
        flag = i.getIntExtra("flag", 0);
        Log.d("flag", String.valueOf(flag));

        mImage.setOnClickListener(mImageClickListener);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(categoryAdapter);

        ArrayAdapter<CharSequence> rateAdapter = ArrayAdapter.createFromResource(
                this, R.array.recovery_rate_array, android.R.layout.simple_spinner_item);
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecovery.setAdapter(rateAdapter);

        if (flag == 1) {
            // An installed app was selected
            String appTitle = i.getStringExtra("app_title");
            String appIcon = i.getStringExtra("app_icon");

            mTitle.setText(appTitle);

            imgContent = appIcon;
            Picasso.with(getApplicationContext()).load(imgContent).into(mImage);

            mCategory.setEnabled(false);
        }

        else if (flag == 2) {
            // A game is being edited
            toolbar.setTitle("Edit Game");
            getExistingGameData(i);
        }

        // Do not show Snackbar when activity is created
        else {
            mCategory.setSelection(0, false);
            mCategory.setOnItemSelectedListener(mMobileListener);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("mTitle", mTitle.getText().toString());
        savedInstanceState.putString("mImage", imgContent);
        savedInstanceState.putInt("mCategory", mCategory.getSelectedItemPosition());
        savedInstanceState.putString("mUnit", mUnit.getText().toString());
        savedInstanceState.putInt("mRecovery", mRecovery.getSelectedItemPosition());
        savedInstanceState.putString("mStamina", mStamina.getText().toString());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mTitle.setText(savedInstanceState.getString("mTitle"));
        imgContent = savedInstanceState.getString("mImage");
        Picasso.with(getApplicationContext()).load(imgContent).into(mImage);
        mCategory.setSelection(savedInstanceState.getInt("mCategory"), false);
        mUnit.setText(savedInstanceState.getString("mUnit"));
        mRecovery.setSelection(savedInstanceState.getInt("mRecovery"));
        mStamina.setText(savedInstanceState.getString("mStamina"));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }

        else if (id == R.id.action_save) {
            if (isEmpty(mTitle)) {
                Snackbar.make(mView, getString(R.string.unfilled_title), Snackbar.LENGTH_SHORT).show();
                return false;
            }

            if (!isEmpty(mStamina) && !isValidMax(mStamina)) {
                Snackbar.make(mView, getString(R.string.invalid_max), Snackbar.LENGTH_SHORT).show();
                return false;
            }

            else {
                updateGameLibrary();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("game_title", gameTitle);
                setResult(RESULT_OK, resultIntent);
                finish();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_EXTERNAL_READ_PERMISSION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay!
                    Crop.pickImage(this);
                } else {
                    // permission denied, boo!
                    Picasso.with(getApplicationContext()).load(R.mipmap.ic_launcher).into(mImage);
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }


    /** Listeners */

    // Create an AlertDialog when the image is clicked
    private View.OnClickListener mImageClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CharSequence[] options = new CharSequence[] {
                    "Select image from external storage",
                    "Retrieve image from URL"
            };

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(v.getContext());
            mBuilder.setTitle("Icon options");
            mBuilder.setCancelable(false);

            mBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            mBuilder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (which == 0) {
                        Log.d("Image", "Select image from external storage");
                        checkExternalPermissionThenStart();
                    }

                    else {
                        Log.d("Image", "Retrieve image from URL");
                        showImageURLDialog();
                    }
                }
            });

            mBuilder.show();
        }
    };

    private AdapterView.OnItemSelectedListener mMobileListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            // It is a mobile game
            if (position == 0) {
                mMobileLayout.setVisibility(View.VISIBLE);
            }

            // Not a mobile game
            else {
                mMobileLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };


    /** Custom methods */

    // Check if the EditText field is empty
    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    // Check if max stamina is within range
    private boolean isValidMax(EditText editText) {
        int val = Integer.parseInt(editText.getText().toString());
        return (val >= 0 && val <= 999);
    }

    // Get the recovery rate integer value (in seconds)
    private int getRateValue(Spinner rate) {
        int[] mRateValueArray = getResources().getIntArray(R.array.recovery_rate_value_array);
        return mRateValueArray[rate.getSelectedItemPosition()];
    }

    private void getExistingGameData(Intent i) {
        Game game = (Game) i.getSerializableExtra("game");

        appId = game.getId();
        String appTitle = game.getTitle();
        String appIcon = game.getImage();
        String appCategory = game.getCategory();

        Log.d("Category", appCategory);

        if (appCategory.equals("Mobile game")) {
            String appUnit = game.getUnit();
            int appRecovery = game.getRecoveryRate();
            int appMax = game.getMaxStamina();

            mUnit.setHint(appUnit);
            mRecovery.setSelection(getArrayPosition(appRecovery));
            mStamina.setHint(String.valueOf(appMax));
        }

        mTitle.setHint(appTitle);

        imgContent = appIcon;
        Picasso.with(getApplicationContext()).load(imgContent).into(mImage);

        String[] categoryArray = getResources().getStringArray(R.array.category_array);
        mCategory.setSelection(Arrays.asList(categoryArray).indexOf(appCategory), false);
    }

    private int getArrayPosition(int value) {
        int[] recoveryArray = getResources().getIntArray(R.array.recovery_rate_value_array);

        Integer[] retrievableArray = new Integer[recoveryArray.length];
        for (int i = 0; i < recoveryArray.length; i++) {
            retrievableArray[i] = recoveryArray[i];
        }

        return Arrays.asList(retrievableArray).indexOf(value);
    }

    // Update the database with the new game
    private void updateGameLibrary() {
        // Title
        gameTitle = mTitle.getText().toString();

        // Image
        if (imgContent == null) {
            gameImage = "";
        } else {
            gameImage = imgContent;
        }

        // Category
        gameCategory = mCategory.getSelectedItem().toString();

        // Unit
        if (isEmpty(mUnit)) {
            staminaUnit = "Stamina";
        } else {
            staminaUnit = mUnit.getText().toString();
        }

        // Recovery rate
        recoveryRate = getRateValue(mRecovery);

        // Maximum stamina
        if (isEmpty(mStamina)) {
            maxStamina = 0;
        } else {
            maxStamina = Integer.parseInt(mStamina.getText().toString());
        }

        Game game = new Game();

        game.setTitle(gameTitle);
        game.setImage(gameImage);
        game.setCategory(gameCategory);
        game.setUnit(staminaUnit);
        game.setRecoveryRate(recoveryRate);
        game.setMaxStamina(maxStamina);

        if (flag == 2) {
            db.updateGame(game, appId);
        } else {
            db.addGame(game);
        }
    }

    private void checkExternalPermissionThenStart() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_EXTERNAL_READ_PERMISSION);
            }
        } else {
            Crop.pickImage(this);
        }
    }

    private void showImageURLDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
        LayoutInflater mInflater = this.getLayoutInflater();
        final View mDialogView = mInflater.inflate(R.layout.dialog_image_url, null);
        mBuilder.setView(mDialogView);

        final EditText editURL = (EditText) mDialogView.findViewById(R.id.img_url);

        mBuilder.setTitle("Enter Image URL")
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Save", null);

        final AlertDialog mDialog = mBuilder.create();

        mDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button cancel = mDialog.getButton(AlertDialog.BUTTON_NEGATIVE);
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });

                Button save = mDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isEmpty(editURL)) {
                            Toast.makeText(getApplicationContext(),
                                    getString(R.string.unfilled_url),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            String url = editURL.getText().toString();
                            if (Patterns.WEB_URL.matcher(url).matches()) {
                                imgContent = url;
                                Picasso.with(getApplicationContext()).load(url).into(mImage);
                                mDialog.dismiss();
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        getString(R.string.invalid_url),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
            }
        });

        mDialog.show();
    }

    /** android-crop */

    private void beginCrop(Uri source) {
        // Unique identifier for each cached uri
        String cacheID = UUID.randomUUID().toString().replaceAll("-", "");
        Uri destination = Uri.fromFile(new File(getCacheDir(), cacheID));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Uri output = Crop.getOutput(result);
            Log.d("icon", output.toString());
            Picasso.with(getApplicationContext()).load(output).into(mImage);
            imgContent = output.toString();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Snackbar.make(mView, Crop.getError(result).getMessage(), Snackbar.LENGTH_SHORT).show();
        }
    }
}
