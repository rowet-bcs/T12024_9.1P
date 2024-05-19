package com.example.task91p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AdvertList extends AppCompatActivity {
    // Declare variables
    ListView advertListView;
    ListAdapter listAdapter;
    int currentUserId;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advert_list);

        // Get userId and name passed from login
        currentUserId = getIntent().getIntExtra("userId", -1);
        currentUser = getIntent().getStringExtra("username");

        // Link variables to UI elements
        advertListView = findViewById(R.id.listView);

        // Load adverts, set list adapter and on click listener
        loadFromDB();
        setAdapter();
        setOnClickListener();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setAdapter() {
        // Set list adapter to display elements of advertList
        listAdapter = new ListAdapter(AdvertList.this, Advert.advertList);
        advertListView.setAdapter(listAdapter);
    }

    private void setOnClickListener() {
        // Define on click action for advert list
        advertListView.setClickable(true);

        advertListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Launch advert detail activity in view mode
                Intent intent = new Intent(AdvertList.this, AdvertDetailView.class);
                Advert advert = Advert.advertList.get(position);
                int advertId = advert.getId();

                intent.putExtra("userId", currentUserId);
                intent.putExtra("mode","view");
                intent.putExtra("id", advertId);
                startActivity(intent);
            }
        });
    }

    public void closeList(View view){
        // Close activity and return to home screen
        finish();
    }

    private void loadFromDB(){
        // Refresh data from database
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);
        db.populateAdvertArray();
    }

    @Override
    protected void onResume(){
        super.onResume();
        // Refresh from database and update advert list
        loadFromDB();
        setAdapter();
    }
}