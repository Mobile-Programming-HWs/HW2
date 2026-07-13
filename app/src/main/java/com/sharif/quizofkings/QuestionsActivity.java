package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class QuestionsActivity extends AppCompatActivity {

    private TextView title;
    private RadioGroup rg;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    private Button clear;
    private Button submit;
    private Game game;
    private int current = 0;
    private int score = 0;
    private int zarib = 0;
    private Database db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        db = Database.getInstance(this);
        LoggedInUser logged = db.LoggedInUserDao().user();
        user = db.UserDao().getUser(logged.getEmail());
        findViews();
        clear.setOnClickListener(view -> rg.clearCheck());
        Intent intent = getIntent();
        db = Database.getInstance(this);
        game = (Game) intent.getSerializableExtra("game");
        if (game == null || game.getQuestions() == null || game.getQuestions().isEmpty()) {
            Toast.makeText(this, "No questions found for this game", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadQuestion();
        configureSubmit();
    }

    private void configureSubmit() {
        submit.setOnClickListener(view -> {
            int selected = rg.getCheckedRadioButtonId();
            String difficulty = user.getDifficulty();
            if (difficulty.equals("easy"))
                zarib = 1;
            if (difficulty.equals("medium"))
                zarib = 2;
            if (difficulty.equals("hard"))
                zarib = 3;
            if (selected == -1) {
            } else if (isCorrect(selected)) {
                score += 3 * zarib;
            } else {
                score -= zarib;
            }
            current += 1;
            if (current < game.getQuestions().size()) {
                loadQuestion();
                rg.clearCheck();
            } else {
                Toast.makeText(this, "Your score is " + score, Toast.LENGTH_SHORT).show();
                Score highest = db.ScoreDao().getTopScore();
                if (highest == null || highest.getScore() <= score) {
                    Toast.makeText(this, "Your broke the record!", Toast.LENGTH_SHORT).show();
                }
                db.ScoreDao().insert(new Score(user.getEmail(), score));
                finish();
            }
        });
    }

    private boolean isCorrect(int selected) {
        View radioSelected = rg.findViewById(selected);
        int idx = rg.indexOfChild(radioSelected);
        String answer = game.getQuestions().get(current).getCorrect_answer();
        RadioButton selectedButton = (RadioButton) rg.getChildAt(idx);
        if (selectedButton != null && selectedButton.getText().toString().equals(answer)) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(this, "Wrong! The answer is " + answer, Toast.LENGTH_SHORT).show();
        return false;
    }

    private void loadQuestion() {
        Question question = game.getQuestions().get(current);
        title.setText(question.getQuestion());
        ArrayList<String> inc = question.getIncorrect_answers();
        String[] answers = {question.getCorrect_answer(), inc.get(0), inc.get(1), inc.get(2)};
        Collections.shuffle(Arrays.asList(answers));
        rb1.setText(answers[0]);
        rb2.setText(answers[1]);
        rb3.setText(answers[2]);
        rb4.setText(answers[3]);
    }

    private void findViews() {
        title = findViewById(R.id.q_title);
        rg = findViewById(R.id.rd_group);
        rb1 = findViewById(R.id.rd1);
        rb2 = findViewById(R.id.rd2);
        rb3 = findViewById(R.id.rd3);
        rb4 = findViewById(R.id.rd4);
        clear = findViewById(R.id.clear_text);
        submit = findViewById(R.id.submit);
    }
}
