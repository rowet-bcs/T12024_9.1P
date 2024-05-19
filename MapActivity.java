package com.example.task91p;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Objects;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {
    // Declare variables
    int currentUserId;
    String currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map);

        // Get userId and name passed from login
        currentUserId = getIntent().getIntExtra("userId", -1);
        currentUser = getIntent().getStringExtra("username");

        // Populate advert list
        loadFromDB();

        // Initialise map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    private void loadFromDB(){
        // Refresh data from database
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);
        db.populateAdvertArray();
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        // When map is ready
        Boolean setCamera = true;
        BitmapDescriptor colour;

        // Iterate through advert list and add marker to map
        for (Advert advert : Advert.advertList){
            Double latitude = Double.parseDouble(advert.getLat());
            Double longitude = Double.parseDouble(advert.getLong());

            LatLng location = new LatLng(latitude, longitude);

            // Set colour of marker depending on Lost and Found type
            if (Objects.equals(advert.getType(), "Lost")){
               colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET);
            } else {
                colour = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            }

            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(location)
                    .title(advert.getType() + " Item")
                    .snippet(advert.getTitle())
                    .icon(colour));
            marker.setTag(advert);

            if(setCamera){
                // Set zoom focused on first advert loaded
                setCamera = false;
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location,12));
            }

        }

        // Turn on ability to zoom in and out
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        // Set a listener for info window events.
        googleMap.setOnInfoWindowClickListener(this);
    }

    public void closeMap(View view){
        // Close map
        finish();
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        // When marker info window clicked, load avert details

        // Retrieve advert and id from marker tag
        Advert advert = (Advert) marker.getTag();
        int id = advert.getId();

        // Launch advert detail activity in view mode
        Intent intent = new Intent(MapActivity.this, AdvertDetailView.class);

        intent.putExtra("userId", currentUserId);
        intent.putExtra("mode","view");
        intent.putExtra("id", id);
        startActivity(intent);
    }
}