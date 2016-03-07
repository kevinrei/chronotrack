package com.kevinrei.chronotrack;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

import java.io.File;

public class NewGameActivity extends AppCompatActivity {

    /** Action code */
    private static final int SELECT_PICTURE = 1;

    /** Database and data values*/
    private MySQLiteHelper db;

    String gameTitle;
    String gameImage;
    String gameCategory;
    String staminaUnit;
    int recoveryRate;
    int maxStamina;

    /** Widgets and Fields */
    View mView;
    LinearLayout mMobileLayout;
    EditText mTitle;
    ImageView mImage;
    Spinner mCategory;
    EditText mUnit;
    Spinner mRecovery;
    EditText mStamina;

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

        mMobileLayout = (LinearLayout) findViewById(R.id.layout_mobile);
        mTitle = (EditText) findViewById(R.id.hint_title);
        mImage = (ImageView) findViewById(R.id.img_game);
        mCategory = (Spinner) findViewById(R.id.spn_category);
        mUnit = (EditText) findViewById(R.id.hint_unit);
        mRecovery = (Spinner) findViewById(R.id.spn_rate);
        mStamina = (EditText) findViewById(R.id.hint_max);

        mImage.setOnClickListener(mImageClickListener);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(categoryAdapter);

        // Do not show Snackbar when activity is created
        mCategory.setSelection(0, false);
        mCategory.setOnItemSelectedListener(mMobileListener);

        ArrayAdapter<CharSequence> rateAdapter = ArrayAdapter.createFromResource(
                this, R.array.recovery_rate_array, android.R.layout.simple_spinner_item);
        rateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mRecovery.setAdapter(rateAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_save) {
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
                setResult(RESULT_OK);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                imgContent = selectedImageUri.toString();
                Picasso.with(getApplicationContext()).load(selectedImageUri).into(mImage);
            }
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
        mImage.setImageURI(Uri.parse(imgContent));
        mCategory.setSelection(savedInstanceState.getInt("mCategory"), false);
        mUnit.setText(savedInstanceState.getString("mUnit"));
        mRecovery.setSelection(savedInstanceState.getInt("mRecovery"));
        mStamina.setText(savedInstanceState.getString("mStamina"));
    }


    /** Custom methods */

    // Update the database with the new game
    private void updateGameLibrary() {
        // Title
        gameTitle = mTitle.getText().toString();

        // Image

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
        // game.setImage(gameImage);
        game.setCategory(gameCategory);
        game.setUnit(staminaUnit);
        game.setRecoveryRate(recoveryRate);
        game.setMaxStamina(maxStamina);

        db.addGame(game);
    }

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
                        Intent i = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, SELECT_PICTURE);
                    }

                    else {
                        Log.d("Image", "Retrieve image from URL");
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

            Snackbar.make(view, mCategory.getItemAtPosition(position) + " selected",
                    Snackbar.LENGTH_SHORT).show();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };
}
