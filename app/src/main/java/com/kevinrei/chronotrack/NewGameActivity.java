package com.kevinrei.chronotrack;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class NewGameActivity extends AppCompatActivity {

    private MySQLiteHelper db;

    /** Data Values */
    String gameTitle;
    // String gameImage;
    String gameCategory;
    String staminaUnit;
    int recoveryRate;
    int maxStamina;

    /** Widgets and Fields */
    View parent;
    EditText mTitle;
    // ImageView mImage;
    Spinner mCategory;
    EditText mUnit;
    Spinner mRecovery;
    EditText mStamina;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_game);
        parent = findViewById(R.id.main_content);

        db = new MySQLiteHelper(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mTitle = (EditText) findViewById(R.id.hint_title);
        // mImage = (ImageView) findViewById(R.id.img_game);
        mCategory = (Spinner) findViewById(R.id.spn_category);
        mUnit = (EditText) findViewById(R.id.hint_unit);
        mRecovery = (Spinner) findViewById(R.id.spn_rate);
        mStamina = (EditText) findViewById(R.id.hint_max);

        ArrayAdapter<CharSequence> categoryAdapter = ArrayAdapter.createFromResource(
                this, R.array.category_array, android.R.layout.simple_spinner_item);
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(categoryAdapter);

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
                Snackbar.make(parent, getString(R.string.unfilled_title), Snackbar.LENGTH_SHORT).show();
                return false;
            }

            // if spinner is not selected

            if (isEmpty(mStamina)) {
                Snackbar.make(parent, getString(R.string.unfilled_max), Snackbar.LENGTH_SHORT).show();
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
        maxStamina = Integer.parseInt(mStamina.getText().toString());

        Game game = new Game();

        // game.setImage(gameImage);
        game.setTitle(gameTitle);
        // game.setImage(gameImage);
        game.setCategory(gameCategory);
        game.setUnit(staminaUnit);
        game.setRecoveryRate(recoveryRate);
        game.setMaxStamina(maxStamina);

        db.addGame(game);
    }

    private boolean isEmpty(EditText editText) {
        return editText.getText().toString().trim().length() == 0;
    }

    private int getRateValue(Spinner rate) {
        int[] mRateValueArray = getResources().getIntArray(R.array.recovery_rate_value_array);
        return mRateValueArray[rate.getSelectedItemPosition()];
    }
}
