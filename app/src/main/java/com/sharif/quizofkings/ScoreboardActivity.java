package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
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
        ArrayList<Score> scores = (ArrayList<Score>) db.ScoreDao().getOrderedScores();
        findViews();
        for (int i = 0; i < email.length && i < scores.size(); i++) {
            Score userScore = scores.get(i);
            if (userScore == null)
                continue;
            score[i].setText(String.valueOf((int) userScore.getScore()));
            email[i].setText(userScore.getUserEmail());
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
