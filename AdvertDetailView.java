package com.example.task91p;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AdvertDetailView extends AppCompatActivity {
    // Declare variables
    String mode;
    int id;
    int userId;
    TextView enterDate;
    RadioGroup radioGroup;
    RadioButton lostRadioButton;
    RadioButton foundRadioButton;
    EditText enterTitle;
    EditText enterDescription;
    TextView enterLocation;
    EditText enterPhone;
    Button selectDateButton;
    Button saveButton;
    Button deleteButton;
    Button setCurrentLocation;
    TextView textViewAdverts;
    Advert selectedAdvert;
    private static final int PERMISSION_REQUEST_LOCATION = 1;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    double latitude;
    double longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_advert_detail);

        // Link variables to UI elements
        enterTitle = findViewById(R.id.enterTitle);
        enterDescription = findViewById(R.id.enterDescription);
        enterLocation = findViewById(R.id.enterLocation);
        enterPhone = findViewById(R.id.enterPhone);
        enterDate = findViewById(R.id.advertDateView);
        saveButton = findViewById(R.id.saveButton);
        selectDateButton = findViewById(R.id.selectDateButton);
        deleteButton = findViewById(R.id.deleteButton);
        setCurrentLocation = findViewById(R.id.setCurrentLocation);
        textViewAdverts = findViewById(R.id.textViewAdverts);
        radioGroup = findViewById(R.id.radioGroup);
        lostRadioButton = findViewById(R.id.lostRadioButton);
        foundRadioButton = findViewById(R.id.foundRadioButton);

        // Initialise API elements
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        if (!Places.isInitialized()) {
            Places.initialize(getApplicationContext(), getString(R.string.GOOGLE_API_KEY));
        }

        // Load task
        loadTask();
    }

    public void loadTask() {

        // Retrieve selected task and display mode
        id = getIntent().getIntExtra("id", -1);
        mode = getIntent().getStringExtra("mode");
        userId = getIntent().getIntExtra("userId", -1);

        selectedAdvert = Advert.getTaskById(id);

        // Initialise values of variables if existing task loaded
        if (selectedAdvert != null) {
            enterTitle.setText(selectedAdvert.getTitle());
            enterDescription.setText(selectedAdvert.getDescription());
            enterDate.setText(selectedAdvert.getDate());
            enterLocation.setText(selectedAdvert.getLocation());
            enterPhone.setText(selectedAdvert.getPhone());
            latitude = Double.parseDouble(selectedAdvert.getLat());
            longitude = Double.parseDouble(selectedAdvert.getLong());

            // Set radio button for type
            String type = selectedAdvert.getType();
            if (type == "Lost") {
                lostRadioButton.setChecked(true);
                foundRadioButton.setChecked(false);
            } else {
                lostRadioButton.setChecked(false);
                foundRadioButton.setChecked(true);
            }
        }

        // Set display mode
        modeSetup();
    }

    public void modeSetup() {
        // Set display elements depending on mode

        switch (mode) {
            case "new":
                enterTitle.setFocusableInTouchMode(true);
                enterDescription.setFocusableInTouchMode(true);
                enterLocation.setFocusable(true);
                enterPhone.setFocusableInTouchMode(true);
                lostRadioButton.setEnabled(true);
                foundRadioButton.setEnabled(true);
                saveButton.setText("CREATE");
                textViewAdverts.setText("Create New Advert");
                deleteButton.setVisibility(View.INVISIBLE);
                selectDateButton.setVisibility(View.VISIBLE);
                setCurrentLocation.setVisibility(View.VISIBLE);
                break;
            case "edit":
                enterTitle.setFocusableInTouchMode(true);
                enterDescription.setFocusableInTouchMode(true);
                enterLocation.setFocusable(true);
                enterPhone.setFocusableInTouchMode(true);
                lostRadioButton.setEnabled(true);
                foundRadioButton.setEnabled(true);
                saveButton.setText("SAVE");
                textViewAdverts.setText("Edit Advert");
                deleteButton.setVisibility(View.INVISIBLE);
                setCurrentLocation.setVisibility(View.VISIBLE);
                break;
            case "view":
            default:
                enterTitle.setFocusable(false);
                enterDescription.setFocusable(false);
                enterLocation.setFocusable(false);
                enterPhone.setFocusableInTouchMode(false);
                lostRadioButton.setEnabled(false);
                foundRadioButton.setEnabled(false);

                // If the logged in user created the advert, allow option for editing and deletion
                if (userId == selectedAdvert.getUserId()) {
                    saveButton.setText("EDIT");
                    deleteButton.setVisibility(View.VISIBLE);
                } else {
                    saveButton.setVisibility(View.INVISIBLE);
                    deleteButton.setVisibility(View.INVISIBLE);
                }

                textViewAdverts.setText("Advert Details");
                selectDateButton.setVisibility(View.INVISIBLE);
                setCurrentLocation.setVisibility(View.INVISIBLE);
                break;
        }
    }

    public void saveButtonClick(View view) {
        // Perform action based on edit/save button click

        // Retrieve button click action
        String action = saveButton.getText().toString();

        switch (action) {
            case "CREATE":
            case "SAVE":
                // Check if all entries have been completed
                if (validateEntries()) {
                    // Save/update task
                    saveTask();

                    // Switch to view mode
                    mode = "view";
                    modeSetup();
                }
                break;

            case "EDIT":
                // Switch to edit mode
                mode = "edit";
                modeSetup();
                break;
        }
    }

    private boolean validateEntries() {
        // Check if all advert entries have been completed before saving

        // Retrieve current values of advert elements
        String title = enterTitle.getText().toString();
        String description = enterDescription.getText().toString();
        String date = enterDate.getText().toString();
        String location = enterLocation.getText().toString();
        String phone = enterPhone.getText().toString();

        // Check if type has been selected
        if (radioGroup.getCheckedRadioButtonId() == -1) {
            Toast.makeText(getApplicationContext(), "Please select advert type", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check if entries contain a value
        if (title.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter advert title", Toast.LENGTH_LONG).show();
            return false;
        }

        if (description.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter advert description", Toast.LENGTH_LONG).show();
            return false;
        }

        if (date.isEmpty()) {
            // Note: no format checking for date as is set by date picker process
            Toast.makeText(getApplicationContext(), "Please enter advert date", Toast.LENGTH_LONG).show();
            return false;
        }

        if (location.isEmpty()) {
            // Note: no format checking for date as is set by date picker process
            Toast.makeText(getApplicationContext(), "Please enter advert location", Toast.LENGTH_LONG).show();
            return false;
        }

        if (phone.isEmpty()) {
            // Note: no format checking for date as is set by date picker process
            Toast.makeText(getApplicationContext(), "Please enter advert phone", Toast.LENGTH_LONG).show();
            return false;
        }

        // All entries have been entered
        return true;
    }

    public void closeAdvert(View view) {
        // Close task and return to previous screen
        finish();
    }

    public void deleteTaskButtonClick(View view) {
        // Create popup alert to confirm deletion of advert

        // Create AlertDialog Builder
        AlertDialog.Builder builder = new AlertDialog.Builder(AdvertDetailView.this);

        // Set alert text
        builder.setMessage("Are you sure you wish to delete this advert?");

        // Set Alert Title
        builder.setTitle("Warning");

        // Set value and action of Yes button
        builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
            // Continue to delete task
            deleteTask();
        });

        // Set value and action of No button
        builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            // Close popup
            dialog.cancel();
        });

        // Create the Alert dialog
        AlertDialog alertDialog = builder.create();
        // Display alert
        alertDialog.show();
    }

    public void saveTask() {
        // Set db instance
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);

        // Retrieve value of radio buttons
        View radioButtonView = radioGroup.findViewById(radioGroup.getCheckedRadioButtonId());
        int index = radioGroup.indexOfChild(radioButtonView);
        RadioButton radioButton = (RadioButton) radioGroup.getChildAt(index);
        String type = radioButton.getText().toString();

        // Retrieve value of fields
        String title = enterTitle.getText().toString();
        String description = enterDescription.getText().toString();
        String date = enterDate.getText().toString();
        String location = enterLocation.getText().toString();
        String phone = enterPhone.getText().toString();

        if (selectedAdvert == null) {
            // If new - add to task list and DB
            int id = db.getNextAdvertId();
            Advert newAdvert = new Advert(id, type, title, description, date, location, Double.toString(latitude), Double.toString(longitude), userId, phone);

            Advert.advertList.add(newAdvert);
            db.addAdvertToDatabase(newAdvert);

            // Set as selected task
            selectedAdvert = Advert.getTaskById(id);

        } else {
            // If existing task, update values
            selectedAdvert.setType(type);
            selectedAdvert.setTitle(title);
            selectedAdvert.setDescription(description);
            selectedAdvert.setDate(date);
            selectedAdvert.setLocation(location);
            selectedAdvert.setLat(Double.toString(latitude));
            selectedAdvert.setLong(Double.toString(longitude));
            selectedAdvert.setPhone(phone);

            db.updateAdvertInDB(selectedAdvert);
        }
    }

    public void deleteTask() {
        // Set db instance
        SQLiteManager db = SQLiteManager.instanceOfDatabase(this);

        // Update deleted flag in task list and database
        selectedAdvert.setDeleteFlag("Y");
        db.updateAdvertInDB(selectedAdvert);

        // Close task detail display and return to previous view
        finish();

    }

    public void showDatePicker(View view) {
        // Set current date for default date view
        Calendar calendar = Calendar.getInstance();
        int todayYear = calendar.get(Calendar.YEAR);
        int todayMonth = calendar.get(Calendar.MONTH);
        int todayDay = calendar.get(Calendar.DAY_OF_MONTH);

        // Create date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                // Update display with selected date
                enterDate.setText(String.valueOf(year) + "-" + String.format("%02d", month + 1) + "-" + String.format("%02d", day));
            }
        }, todayYear, todayMonth, todayDay);

        // Display date picker
        datePickerDialog.show();
    }

    public void setCurrentLocation(View view){
        // On set current location button click
        getLastLocation();
    }

    private void getLastLocation() {
        // Check and request permission if not yet granted
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        // Request current location
        CurrentLocationRequest request = new CurrentLocationRequest.Builder().build();
        Task<Location> task = fusedLocationProviderClient.getCurrentLocation(request, null);

        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location){
                if (location != null) {
                    // Store current location
                    currentLocation = location;
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();

                    // Display current location in form
                    enterLocation.setText(getAddressFromLatLong(latitude,longitude));

                } else {
                    Toast.makeText(getApplicationContext(), "Unable to retrieve current location", Toast.LENGTH_LONG).show();
                }

            }
        })
        .addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to get location
                Toast.makeText(getApplicationContext(), "Unable to retrieve current location", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // Request permission from user to access current location
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSION_REQUEST_LOCATION){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            } else {
                Toast.makeText(getApplicationContext(), "Permission to access current location not granted", Toast.LENGTH_LONG).show();
            }
        }
    }

    public String getAddressFromLatLong(Double latitude, Double longitude){
        // Take lat/long and retrieve street address
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1);
            return addresses.get(0).getAddressLine(0);
        } catch (Exception error) {
            return "";
        }
    }

    public void launchAutocomplete(View view){
        // If in new or edit mode, launch autocomplete intent
        if (Objects.equals(mode, "new") || Objects.equals(mode, "edit")) {
            // Set fields to retrieve from autocomplete
            List<Place.Field> fields = Arrays.asList(Place.Field.ADDRESS, Place.Field.LAT_LNG);

            // Start the autocomplete intent
            Intent intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields).build(this);
            startAutocomplete.launch(intent);
        }
    }

    private final ActivityResultLauncher<Intent> startAutocomplete = registerForActivityResult(
            // Set action for return of autocomplete intent
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent intent = result.getData();
                    if (intent != null) {
                        // Retrieve and store data from autocomplete intent
                        Place place = Autocomplete.getPlaceFromIntent(intent);
                        enterLocation.setText(place.getAddress());

                        LatLng latlng = place.getLatLng();
                        latitude = latlng.latitude;
                        longitude = latlng.longitude;
                    }
                }
            });
}