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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

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
    private TextView gameStatus;
    private TextView gameSummary;
    private Database db;
    private User loggedInUser;
    private boolean gameRequestRunning = false;
    private Call<String> activeGameCall;

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
        gameStatus = findViewById(R.id.game_status);
        gameSummary = findViewById(R.id.game_summary);
        logout.setOnClickListener(view -> {
            Intent intent = new Intent();
            setResult(Activity.RESULT_OK, intent);
            finish();
        });
        configureGame();
        updateGameSummary();
    }

    private void configureGame() {
        startGame.setOnClickListener(view -> {
            if (gameRequestRunning) {
                return;
            }
            setGameRequestRunning(true, R.string.game_status_loading_online);
            activeGameCall = RetrofitClient.getInstance().getMyApi().getGame(
                    loggedInUser.getNumberOfQuestions(),
                    loggedInUser.getDifficulty(),
                    loggedInUser.getCategory(),
                    "multiple");
            activeGameCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (!isCurrentCall(call)) {
                        return;
                    }

                    if (!response.isSuccessful()) {
                        launchCachedGameOrShowRetry(R.string.game_status_online_failed);
                        return;
                    }

                    ArrayList<Question> questions = GameResponseParser.parseQuestions(response.body());
                    if (!GameResponseParser.hasUsableQuestions(questions)) {
                        launchCachedGameOrShowRetry(R.string.game_status_online_empty);
                        return;
                    }

                    Game game = new Game(db.GameDao().getNextGameId(), loggedInUser.getNumberOfQuestions(),
                            loggedInUser.getEmail(), loggedInUser.getDifficulty(), loggedInUser.getCategory(),
                            questions);
                    db.GameDao().insert(game);
                    launchGame(game, R.string.game_status_starting_online, "Starting new game");
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    if (!isCurrentCall(call) || call.isCanceled()) {
                        return;
                    }
                    launchCachedGameOrShowRetry(R.string.game_status_online_failed);
                }
            });
        });
    }

    private void launchCachedGameOrShowRetry(int fallbackStatus) {
        setStatus(fallbackStatus);
        Game game = getUsableCachedGame();
        if (game == null) {
            showRetryState();
            return;
        }
        launchGame(game, R.string.game_status_starting_cached, "Starting cached game");
    }

    private Game getUsableCachedGame() {
        List<Game> games = db.GameDao().getGames(loggedInUser.getEmail(), loggedInUser.getDifficulty(),
                loggedInUser.getCategory(), loggedInUser.getNumberOfQuestions());
        for (Game game : games) {
            if (game != null && GameResponseParser.hasUsableQuestions(game.getQuestions())) {
                return game;
            }
        }
        return null;
    }

    private int getCachedGameCount() {
        return db.GameDao().countGames(
                loggedInUser.getEmail(),
                loggedInUser.getDifficulty(),
                loggedInUser.getCategory(),
                loggedInUser.getNumberOfQuestions()
        );
    }

    private void launchGame(Game game, int statusMessage, String toastMessage) {
        activeGameCall = null;
        setStatus(statusMessage);
        Toast.makeText(GameActivity.this, toastMessage, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(GameActivity.this, QuestionsActivity.class);
        intent.putExtra("game", game);
        startActivity(intent);
    }

    private void showRetryState() {
        activeGameCall = null;
        gameRequestRunning = false;
        startGame.setEnabled(true);
        startGame.setText(R.string.retry_game);
        setStatus(R.string.game_status_retry);
        Toast.makeText(GameActivity.this, "No matching cached game is available", Toast.LENGTH_LONG).show();
    }

    private boolean isCurrentCall(Call<String> call) {
        return call != null && call == activeGameCall && !isFinishing() && !isDestroyed();
    }

    private void setGameRequestRunning(boolean running, int statusMessage) {
        gameRequestRunning = running;
        startGame.setEnabled(!running);
        startGame.setText(running ? R.string.loading_game : R.string.start_a_game);
        setStatus(statusMessage);
    }

    private void setStatus(int statusMessage) {
        gameStatus.setText(statusMessage);
    }

    private void updateGameSummary() {
        gameSummary.setText(getString(
                R.string.game_summary_format,
                QuizLabels.categoryName(loggedInUser.getCategory()),
                QuizLabels.difficultyName(loggedInUser.getDifficulty()),
                loggedInUser.getNumberOfQuestions(),
                getCachedGameCount()
        ));
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
        if (startGame != null && activeGameCall == null) {
            setGameRequestRunning(false, R.string.game_status_ready);
            updateGameSummary();
        }
    }

    @Override
    protected void onDestroy() {
        if (activeGameCall != null) {
            activeGameCall.cancel();
            activeGameCall = null;
        }
        super.onDestroy();
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
