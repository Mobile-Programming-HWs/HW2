package com.sharif.quizofkings;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText email;
    private EditText password;
    private Button login;
    private Button register;
    private Database db;
    private User loggedInUser;
    public String dbSample;

    public User getLoggedInUser() {
        return loggedInUser;
    }

    public void logout() {
        loggedInUser = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //readSample();
        super.onCreate(savedInstanceState);
        db = Database.getInstance(this);
        setContentView(R.layout.activity_main);
        findViews();
        LoggedInUser logged = db.LoggedInUserDao().user();
        if (logged != null) {
            loggedInUser = db.UserDao().getUser(logged.getEmail());
            if (loggedInUser == null) {
                db.LoggedInUserDao().deleteAll();
            } else {
                login();
            }
        }
        login.setOnClickListener(view -> {
            boolean isValid = true;
            if (email.getText().toString().isEmpty()){
                email.setError("Please enter email");
                isValid = false;
            }
            if (password.getText().toString().isEmpty()){
                password.setError("Please enter password");
                isValid = false;
            }
            if (!isValid)
                return;
            User user = db.UserDao().getUser(email.getText().toString());
            if (user != null) {
                if (user.getPassword().equals(password.getText().toString())) {
                    loggedInUser = user;
                    db.LoggedInUserDao().deleteAll();
                    db.LoggedInUserDao().insert(new LoggedInUser(email.getText().toString()));
                    Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                    login();
                } else {
                    Toast.makeText(MainActivity.this, "Incorrect password!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Entered email does not exist!", Toast.LENGTH_SHORT).show();
            }
        });
        register.setOnClickListener(view -> {
            boolean isValid = true;
            if (email.getText().toString().isEmpty()){
                email.setError("Please enter email");
                isValid = false;
            }
            if (password.getText().toString().isEmpty()){
                password.setError("Please enter password");
                isValid = false;
            }
            if (!isValid)
                return;
            User user = db.UserDao().getUser(email.getText().toString());
            if (user != null) {
                Toast.makeText(MainActivity.this, "This email is already registered!", Toast.LENGTH_SHORT).show();
            } else {
                db.UserDao().insert(new User(email.getText().toString(), password.getText().toString()));
                Toast.makeText(MainActivity.this, "Success!", Toast.LENGTH_SHORT).show();
            }
        });
    }

//    private void readSample() {
//        InputStream inputStream = this.getResources().openRawResource(R.raw.opentdb);
//        JsonParser jsonParser = new JsonParser();
//        try {
//            JSONObject object = (JSONObject) jsonParser.parse(new InputStreamReader(inputStream, "UTF-8"));
//        } catch (Exception e) {
//
//        }
//    }

    private void login() {
        Intent intent = new Intent(this, GameActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                loggedInUser = null;
                db.LoggedInUserDao().deleteAll();
            }
        }
    }

    private void findViews() {
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        register = findViewById(R.id.register);
    }
}
