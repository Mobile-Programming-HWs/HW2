package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.util.List;

public class ScoreboardActivity extends AppCompatActivity{


    private Database db;
    private TextView[] email = new TextView[5];
    private TextView[] score = new TextView[5];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scoreboard);
        db = Database.getInstance(this);
        List<ScoreboardRows.Row> rows = ScoreboardRows.topRows(db.ScoreDao().getOrderedScores(), email.length);
        findViews();
        for (int i = 0; i < email.length; i++) {
            if (i < rows.size()) {
                ScoreboardRows.Row row = rows.get(i);
                score[i].setText(String.valueOf(row.getScore()));
                email[i].setText(row.getUserEmail());
            } else {
                score[i].setText(R.string.empty_score_value);
                email[i].setText(R.string.empty_scoreboard_row);
            }
        }
    }

    private void findViews() {
        email[0] = findViewById(R.id.email1);
        email[1] = findViewById(R.id.email2);
        email[2] = findViewById(R.id.email3);
        email[3] = findViewById(R.id.email4);
        email[4] = findViewById(R.id.email5);
        score[0] = findViewById(R.id.score1);
        score[1] = findViewById(R.id.score2);
        score[2] = findViewById(R.id.score3);
        score[3] = findViewById(R.id.score4);
        score[4] = findViewById(R.id.score5);
    }

}
