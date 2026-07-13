package com.sharif.quizofkings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GameActivity extends AppCompatActivity{

    private ActionBarDrawerToggle actionBarDrawerToggle;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Button startGame;
    private Button logout;
    private Database db;
    private User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        db = Database.getInstance(this);
        LoggedInUser logged = db.LoggedInUserDao().user();
        loggedInUser = db.UserDao().getUser(logged.getEmail());
        setUpDrawer();
        startGame = findViewById(R.id.start_game);
        logout = findViewById(R.id.logout);
        logout.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        configureGame();
    }

    private void configureGame() {
        startGame.setOnClickListener(view -> {
            Call<String> call = RetrofitClient.getInstance().getMyApi().getGame(
                    loggedInUser.getNumberOfQuestions(),
                    loggedInUser.getDifficulty(),
                    loggedInUser.getCategory(),
                    "multiple");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String json = response.body();
                    char resp = (char) json.getBytes(StandardCharsets.UTF_8)[17];
                    if (resp == '1') {
                        onFailure(call, null);
                        TextView textView = findViewById(R.id.textView3);
                        textView.setText(json);
                    } else {
                        int index = json.indexOf(':', json.indexOf(':') + 1);
                        json = json.substring(index + 1);
                        json = json.substring(0, json.length() - 1);
                        TextView textView = findViewById(R.id.textView3);
                        //textView.setText(json);
                        Gson gson = new Gson();
                        ArrayList<Question> questions = gson.fromJson(json, new TypeToken<List<Question>>() {
                        }.getType());
                        StringBuilder builder = new StringBuilder();
                        for (Question question : questions) {
                            builder.append(question.getQuestion());
                        }
                        Game game = new Game(Game.total + 1, questions.size(), loggedInUser.getEmail(), questions);
                        Game.total += 1;
                        db.GameDao().insert(game);
                        Toast.makeText(GameActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GameActivity.this, QuestionsActivity.class);
                        intent.putExtra("game", game);
                        startActivity(intent);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Game game = db.GameDao().getGame(loggedInUser.getEmail());
                    if (game == null) {
                        Toast.makeText(GameActivity.this, "You are offline and have no games in cache!", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(GameActivity.this, "You are offline! Launching a cached game...", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GameActivity.this, QuestionsActivity.class);
                        intent.putExtra("game", game);
                        startActivity(intent);
                    }
                }
            });
        });
    }

    private void setUpDrawer() {
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            Intent intent = null;
            if (id == R.id.profile) {
                intent = new Intent(GameActivity.this, ProfileActivity.class);
            } else if (id == R.id.settings) {
                intent = new Intent(GameActivity.this, SettingsActivity.class);
            } else if (id == R.id.scoreboard) {
                intent = new Intent(GameActivity.this, ScoreboardActivity.class);
            }
            GameActivity.this.startActivity(intent);
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        try {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            Log.d("ERROR", e.getMessage());
        }

    }

    @Override
    public void onBackPressed(){
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
