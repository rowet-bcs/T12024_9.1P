
// This activity was completed from code created for task 7.1P and with assistance from
// https://www.youtube.com/watch?v=_gpreGNtNCM
// https://www.youtube.com/watch?v=XimcwP-OzFg
// https://developer.android.com/develop/sensors-and-location/location/retrieve-current
// https://developers.google.com/maps/documentation/places/android-sdk/autocomplete
// https://developers.google.com/maps/documentation/android-sdk/marker
// https://stackoverflow.com/questions/9409195/how-to-get-complete-address-from-latitude-and-longitude
// https://www.youtube.com/watch?v=9Va14Q6edD8
// https://developers.google.com/maps/documentation/android-sdk/infowindows

package com.example.task91p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    // Declare variables
    EditText enterUsername;
    EditText enterPassword;
    Intent homePage;
    Intent signUp;
    SQLiteManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Link variables to UI elements
        enterUsername = findViewById(R.id.enterUsername);
        enterPassword = findViewById(R.id.enterPassword);

        // Initialise database
        db = SQLiteManager.instanceOfDatabase(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void login(View view){
        // Retrieve values of entry fields
        String username = enterUsername.getText().toString();
        String password = enterPassword.getText().toString();

        // Check if username and password has been entered
        if (username.isEmpty()){
            Toast.makeText(getApplicationContext(), "Enter Username", Toast.LENGTH_LONG).show();
        } else if (password.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Enter Password", Toast.LENGTH_LONG).show();
        } else {
            // Validate values against database to login
            int loginResult = db.validateUser(username, password);

            if (loginResult == -1){
                Toast.makeText(getApplicationContext(), "Incorrect login details, please try again", Toast.LENGTH_LONG).show();
            } else {
                // Logged in, launch home page
                enterPassword.setText("");
                homePage = new Intent(this, HomePage.class);
                homePage.putExtra("userId", loginResult);
                homePage.putExtra("username", username);
                startActivity(homePage);
            }
        }
    }

    public void signup(View view){
        // Launch sign up process
        signUp = new Intent(this, SignUp.class);
        startActivity(signUp);
    }

    public void closeApp(View view){
        // Close app
        finish();
    }
}