package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private SwitchMaterial switchMaterial;
    private Spinner category;
    private Spinner difficulty;
    private Spinner num;
    private Button apply;
    private final String[] categories = {"Art", "Animals", "Vehicles", "Celebrities", "Sports"};
    private final int[] categoryValues = {25, 27, 28, 26, 21};
    private final String[] difficulties = {"Easy", "Medium", "Hard"};
    private final String[] difficultyValues = {"easy", "medium", "hard"};
    private final String[] numQuestions = {"10", "7", "5"};
    private final int[] numQuestionValues = {10, 7, 5};
    private int selectedCat = 25;
    private String selectedDif = "easy";
    private int selectedNum = 10;
    private boolean selectedDark = false;
    private Database db;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = Database.getInstance(this);
        findViews();
        LoggedInUser logged = db.LoggedInUserDao().user();
        if (logged == null) {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        user = db.UserDao().getUser(logged.getEmail());
        if (user == null) {
            Toast.makeText(this, "User not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        loadCurrentSettings();
        switchMaterial.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            selectedDark = isChecked;
            applyNightMode(isChecked);
        });
        configureCategory();
        configureDifficulty();
        configureNum();
        configureApply();
    }

    private void configureApply() {
        apply.setOnClickListener(view -> {
            user.setCategory(selectedCat);
            user.setDifficulty(selectedDif);
            user.setNumberOfQuestions(selectedNum);
            user.setDarkMode(selectedDark);
            db.UserDao().update(user);
            Toast.makeText(this, "Settings Applied!", Toast.LENGTH_LONG).show();
        });
    }

    private void configureNum() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, numQuestions);
        num.setAdapter(adapter);
        num.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedNum = numQuestionValues[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        num.setSelection(indexOf(numQuestionValues, selectedNum));
    }

    private void configureDifficulty() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, difficulties);
        difficulty.setAdapter(adapter);
        difficulty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedDif = difficultyValues[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        difficulty.setSelection(indexOf(difficultyValues, selectedDif));
    }

    private void configureCategory() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, categories);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCat = categoryValues[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        category.setSelection(indexOf(categoryValues, selectedCat));
    }

    private void findViews() {
        switchMaterial = findViewById(R.id.dark_mode);
        category = findViewById(R.id.category);
        difficulty = findViewById(R.id.difficulty);
        num = findViewById(R.id.number_of_questions);
        apply = findViewById(R.id.apply_settings);
    }

    private void loadCurrentSettings() {
        selectedCat = user.getCategory();
        selectedDif = user.getDifficulty();
        selectedNum = user.getNumberOfQuestions();
        selectedDark = user.getDarkMode();
        switchMaterial.setChecked(selectedDark);
        applyNightMode(selectedDark);
    }

    private void applyNightMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

    private int indexOf(int[] values, int selectedValue) {
        for (int i = 0; i < values.length; i++) {
            if (values[i] == selectedValue) {
                return i;
            }
        }
        return 0;
    }

    private int indexOf(String[] values, String selectedValue) {
        for (int i = 0; i < values.length; i++) {
            if (values[i].equals(selectedValue)) {
                return i;
            }
        }
        return 0;
    }
}
