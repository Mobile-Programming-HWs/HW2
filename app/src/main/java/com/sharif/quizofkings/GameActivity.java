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
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private boolean gameRequestRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        db = Database.getInstance(this);
        LoggedInUser logged = db.LoggedInUserDao().user();
        if (logged == null) {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loggedInUser = db.UserDao().getUser(logged.getEmail());
        if (loggedInUser == null) {
            db.LoggedInUserDao().deleteAll();
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
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
            if (gameRequestRunning) {
                return;
            }
            setGameRequestRunning(true);
            Call<String> call = RetrofitClient.getInstance().getMyApi().getGame(
                    loggedInUser.getNumberOfQuestions(),
                    loggedInUser.getDifficulty(),
                    loggedInUser.getCategory(),
                    "multiple");
            call.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    String json = response.body();
                    if (!response.isSuccessful() || json == null) {
                        onFailure(call, null);
                        return;
                    }

                    try {
                        JSONObject object = new JSONObject(json);
                        if (object.optInt("response_code", 1) != 0) {
                            onFailure(call, null);
                            return;
                        }

                        JSONArray results = object.getJSONArray("results");
                        Gson gson = new Gson();
                        ArrayList<Question> questions = gson.fromJson(results.toString(), new TypeToken<ArrayList<Question>>() {
                        }.getType());
                        if (questions == null || questions.isEmpty()) {
                            onFailure(call, null);
                            return;
                        }

                        Game game = new Game(db.GameDao().getNextGameId(), loggedInUser.getNumberOfQuestions(),
                                loggedInUser.getEmail(), loggedInUser.getDifficulty(), loggedInUser.getCategory(),
                                questions);
                        db.GameDao().insert(game);
                        Toast.makeText(GameActivity.this, "Success!", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GameActivity.this, QuestionsActivity.class);
                        intent.putExtra("game", game);
                        startActivity(intent);
                    } catch (Exception e) {
                        onFailure(call, e);
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Game game = db.GameDao().getGame(loggedInUser.getEmail(), loggedInUser.getDifficulty(),
                            loggedInUser.getCategory(), loggedInUser.getNumberOfQuestions());
                    if (game == null) {
                        Toast.makeText(GameActivity.this, "No matching cached game is available", Toast.LENGTH_LONG).show();
                        setGameRequestRunning(false);
                    } else {
                        Toast.makeText(GameActivity.this, "Launching a cached game", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(GameActivity.this, QuestionsActivity.class);
                        intent.putExtra("game", game);
                        startActivity(intent);
                    }
                }
            });
        });
    }

    private void setGameRequestRunning(boolean running) {
        gameRequestRunning = running;
        startGame.setEnabled(!running);
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
            if (intent == null) {
                drawerLayout.closeDrawer(GravityCompat.START);
                return false;
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
    protected void onResume() {
        super.onResume();
        if (startGame != null) {
            setGameRequestRunning(false);
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout != null && drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
