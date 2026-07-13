package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class QuestionsActivity extends AppCompatActivity {

    private TextView title;
    private TextView questionProgress;
    private TextView currentScore;
    private TextView questionDifficulty;
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
        if (logged == null) {
            Toast.makeText(this, "Please sign in again", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        user = db.UserDao().getUser(logged.getEmail());
        if (user == null) {
            Toast.makeText(this, "Profile could not be loaded", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        findViews();
        clear.setOnClickListener(view -> rg.clearCheck());
        Intent intent = getIntent();
        game = (Game) intent.getSerializableExtra("game");
        if (game == null || game.getQuestions() == null || game.getQuestions().isEmpty()) {
            Toast.makeText(this, "No quiz questions are available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (!loadQuestion()) {
            finish();
            return;
        }
        configureSubmit();
    }

    private void configureSubmit() {
        submit.setOnClickListener(view -> {
            int selected = rg.getCheckedRadioButtonId();
            String difficulty = game.getDifficulty();
            if (difficulty.equals("easy")) {
                zarib = 1;
            } else if (difficulty.equals("medium")) {
                zarib = 2;
            } else if (difficulty.equals("hard")) {
                zarib = 3;
            }
            if (selected == -1) {
                Toast.makeText(this, "Select an answer first", Toast.LENGTH_SHORT).show();
                return;
            } else if (isCorrect(selected)) {
                score += 3 * zarib;
            } else {
                score -= zarib;
            }
            updateQuizMeta();
            current += 1;
            if (current < game.getQuestions().size()) {
                if (!loadQuestion()) {
                    finish();
                    return;
                }
                rg.clearCheck();
            } else {
                Toast.makeText(this, "Score: " + score, Toast.LENGTH_SHORT).show();
                Score highest = db.ScoreDao().getTopScore();
                if (highest == null || highest.getScore() <= score) {
                    Toast.makeText(this, "New high score", Toast.LENGTH_SHORT).show();
                }
                db.ScoreDao().insert(new Score(user.getEmail(), score));
                finish();
            }
        });
    }

    private boolean isCorrect(int selected) {
        View radioSelected = rg.findViewById(selected);
        int idx = rg.indexOfChild(radioSelected);
        String answer = decodeHtml(game.getQuestions().get(current).getCorrect_answer());
        RadioButton selectedButton = (RadioButton) rg.getChildAt(idx);
        if (selectedButton != null && selectedButton.getText().toString().equals(answer)) {
            Toast.makeText(this, "Correct", Toast.LENGTH_SHORT).show();
            return true;
        }
        Toast.makeText(this, "Incorrect. Answer: " + answer, Toast.LENGTH_SHORT).show();
        return false;
    }

    private boolean loadQuestion() {
        Question question = game.getQuestions().get(current);
        updateQuizMeta();
        title.setText(decodeHtml(question.getQuestion()));
        ArrayList<String> inc = question.getIncorrect_answers();
        if (inc == null || inc.size() < 3) {
            Toast.makeText(this, "This question is missing answers", Toast.LENGTH_SHORT).show();
            return false;
        }
        ArrayList<String> answers = new ArrayList<>();
        answers.add(decodeHtml(question.getCorrect_answer()));
        answers.add(decodeHtml(inc.get(0)));
        answers.add(decodeHtml(inc.get(1)));
        answers.add(decodeHtml(inc.get(2)));
        Collections.shuffle(answers);
        rb1.setText(answers.get(0));
        rb2.setText(answers.get(1));
        rb3.setText(answers.get(2));
        rb4.setText(answers.get(3));
        return true;
    }

    private void findViews() {
        title = findViewById(R.id.q_title);
        questionProgress = findViewById(R.id.question_progress);
        currentScore = findViewById(R.id.current_score);
        questionDifficulty = findViewById(R.id.question_difficulty);
        rg = findViewById(R.id.rd_group);
        rb1 = findViewById(R.id.rd1);
        rb2 = findViewById(R.id.rd2);
        rb3 = findViewById(R.id.rd3);
        rb4 = findViewById(R.id.rd4);
        clear = findViewById(R.id.clear_text);
        submit = findViewById(R.id.submit);
    }

    private void updateQuizMeta() {
        questionProgress.setText(getString(
                R.string.question_progress_format,
                current + 1,
                game.getQuestions().size()
        ));
        currentScore.setText(getString(R.string.score_format, score));
        questionDifficulty.setText(getString(
                R.string.question_context_format,
                QuizLabels.categoryName(game.getCategory()),
                QuizLabels.difficultyName(game.getDifficulty())
        ));
    }

    private String decodeHtml(String value) {
        if (value == null) {
            return "";
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(value, Html.FROM_HTML_MODE_LEGACY).toString();
        }
        return Html.fromHtml(value).toString();
    }
}
