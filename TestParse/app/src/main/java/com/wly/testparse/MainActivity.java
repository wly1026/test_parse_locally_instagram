package com.wly.testparse;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    EditText userName;
    EditText password;
    Button signButton;
    Boolean signInMode;
    TextView switchText;
    RelativeLayout background;

    @Override
    public void onClick(View view) {
        if (view.getId() == switchText.getId()) {
            if (signInMode) {
                signInMode = false;
                signButton.setText("Register");
                switchText.setText("Already Have an account?");
            } else {
                signInMode = true;
                signButton.setText("Sign In");
                switchText.setText("Create an account?");
            }
        } else if (view.getId() == background.getId()) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Home Recipe");

//        ParseObject firstObject = new  ParseObject("FirstClass");
//        firstObject.put("message","Hey ! First message from android. Parse is now connected");
//        firstObject.saveInBackground(e -> {
//            if (e != null){
//                Log.e("MainActivity", e.getLocalizedMessage());
//            }else{
//                Log.d("MainActivity","Object saved.");
//            }
//        });

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.passWord);
        signButton = findViewById(R.id.signButton);
        signInMode = true;
        switchText = findViewById(R.id.switchText);
        background = findViewById(R.id.background);

        switchText.setOnClickListener(this);
        background.setOnClickListener(this);

        if (ParseUser.getCurrentUser() != null) {
            showUserList();
        }
        ParseAnalytics.trackAppOpenedInBackground(getIntent());


    }

    public void sign(View view) {
        if (userName.getText().toString().matches("") || password.getText().toString().matches((""))) {
            Toast.makeText(this, "A username and password are required", Toast.LENGTH_SHORT).show();
        } else if (signInMode) {
            signIn();
        } else {
            signUp();
        }
    }


    private void signUp() {

        ParseUser user = new ParseUser();

        user.setUsername(userName.getText().toString());
        user.setPassword(password.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Success", "Sign up successfully");
                    Toast.makeText(MainActivity.this, "sign up successfully", Toast.LENGTH_SHORT).show();
                    showUserList();
                } else {
                    //e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {

        ParseUser.logInInBackground(userName.getText().toString(), password.getText().toString(), new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (user != null) {
                    Log.i("Success", "Log in successfully");
                    Toast.makeText(MainActivity.this, "log in successfully", Toast.LENGTH_SHORT).show();
                    showUserList();
                } else {
                    //e.printStackTrace();
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void showUserList() {
        Intent intent = new Intent(getApplicationContext(), UserListActivity.class);
        startActivity(intent);
    }
}