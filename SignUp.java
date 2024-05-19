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

public class SignUp extends AppCompatActivity {
    // Declare variables
    EditText username;
    EditText password1;
    EditText password2;
    SQLiteManager db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sign_up);

        // Link variables to UI elements
        username = findViewById(R.id.enterUsernameSU);
        password1 = findViewById(R.id.enterPasswordSU1);
        password2 = findViewById(R.id.enterPasswordSU2);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void createAccount(View view){
        // Initialise database
        db = SQLiteManager.instanceOfDatabase(this);

        // Retrieve text entries
        String user = username.getText().toString();
        String pw1 = password1.getText().toString();
        String pw2 = password2.getText().toString();

        if (db.checkUsername(user) != -1){
            // Check if username already taken
            Toast.makeText(getApplicationContext(), "Username is already taken, please choose something else", Toast.LENGTH_LONG).show();
        } else if (!pw1.equals(pw2)){
            // Check if passwords match
            Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
        } else {
            //Checks passesd, add user to database
            db.addUserToDatabase(user, pw1);

            // Retrieve newly created userId
            int id = db.checkUsername(user);

            // Launch home page
            Intent homePage = new Intent(this, HomePage.class);
            homePage.putExtra("userId", id);
            homePage.putExtra("username", user);
            startActivity(homePage);
            finish();
        }
    }

    public void cancel(View view){
        // Return to login screen
        finish();
    }
}