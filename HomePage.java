package com.example.task91p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomePage extends AppCompatActivity {
    // Declare variables
    int currentUserId;
    String currentUser;
    TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);

        // Get username passed from login
        currentUserId = getIntent().getIntExtra("userId", -1);
        currentUser = getIntent().getStringExtra("username");

        // Link variables to UI elements
        welcomeText = findViewById(R.id.welcomeText);

        // Set welcome message
        welcomeText.setText("Welcome " + currentUser + "!");


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void createNewAdvert(View view){
        // Launch create new advert process
        Intent newAdvert = new Intent(this, AdvertDetailView.class);
        newAdvert.putExtra("userId", currentUserId);
        newAdvert.putExtra("username", currentUser);
        newAdvert.putExtra("mode", "new");
        startActivity(newAdvert);
    }
    public void loadAllAdverts(View view){
        // Load list of all current adverts
        Intent openAdvertList = new Intent(this, AdvertList.class);
        openAdvertList.putExtra("userId", currentUserId);
        openAdvertList.putExtra("username", currentUser);
        startActivity(openAdvertList);
    }

    public void showOnMap(View view){
        // Display lost and found items on map
        Intent showOnMap = new Intent(this, MapActivity.class);
        showOnMap.putExtra("userId", currentUserId);
        showOnMap.putExtra("username", currentUser);
        startActivity(showOnMap);
    }

    public void logout(View view){
        //Return to login screen
        finish();
    }
}