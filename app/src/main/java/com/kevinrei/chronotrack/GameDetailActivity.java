package com.kevinrei.chronotrack;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class GameDetailActivity extends AppCompatActivity {

    /** Database and data values*/
    private MySQLiteHelper db;

    Game game;
    int gameId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        Intent i = getIntent();
        gameId = i.getIntExtra("game_id", 0);

        db = new MySQLiteHelper(this);
        game = db.getGame(gameId);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(game.getTitle());

        TextView gameId = (TextView) findViewById(R.id.game_id);
        gameId.setText(game.getTitle());
    }
}
