package com.example.mad_smartfit_android_app;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleAppointmentActivity extends AppCompatActivity {

    private RadioGroup radioGroup;
    private RadioButton radioTrainer, radioNutrition;
    private TextInputEditText dateInput, timeInput, reasonInput;
    private Spinner trainerSpinner , nutritionSpinner;
    private Button submitButton;
    private FirebaseFirestore fStore;
    private FirebaseAuth fAuth;
    private List<String> trainerList ,nutritionList;
    private List<String> trainerUserIdList,nutritionUserIdList;
    private String selectedTrainer = "",selectedNutrition = "";
    private String selectedTrainerUserID = "",selectedNutritionUserID = "";

    private  String username="";
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_appointment);

        // Initialize Firebase
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();

        // Initialize views
        radioGroup = findViewById(R.id.radioGroup);
        radioTrainer = findViewById(R.id.radioTrainer);
        radioNutrition = findViewById(R.id.radioNutrition);
        dateInput = findViewById(R.id.dateInput);
        timeInput = findViewById(R.id.timeInput);
        trainerSpinner = findViewById(R.id.trainerSpinner);
        nutritionSpinner = findViewById(R.id.nutritionSpinner);
        submitButton = findViewById(R.id.submitButton);
        reasonInput = findViewById(R.id.tietReason);

        // Initialize  list
        trainerList = new ArrayList<>();
        nutritionList = new ArrayList<>();
        trainerUserIdList = new ArrayList<>();
        nutritionUserIdList = new ArrayList<>();

        // Set up date picker
        dateInput.setOnClickListener(v -> showDatePicker());

        // Set up time picker
        timeInput.setOnClickListener(v -> showTimePicker());

        // Handle radio button selection
        radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTrainer) {
                // Show the trainer Spinner
                findViewById(R.id.nutritionInputLayout).setVisibility(View.GONE);
                findViewById(R.id.trainerInputLayout).setVisibility(View.VISIBLE);
                fetchTrainersFromFirestore();
            } else {
                // Hide the trainer Spinner
                findViewById(R.id.trainerInputLayout).setVisibility(View.GONE);
                findViewById(R.id.nutritionInputLayout).setVisibility(View.VISIBLE);
                fetchNutritionistsFromFirestore();
            }
        });

        // Handle Spinner item selection
        trainerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedTrainer = trainerList.get(position);
                selectedTrainerUserID = trainerUserIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedTrainer = "";
            }
        });
        nutritionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedNutrition = nutritionList.get(position);
                selectedNutritionUserID = nutritionUserIdList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedNutrition ="";
            }
        });

        // Handle submit button click
        submitButton.setOnClickListener(v -> onSubmitClick());

    }

    private void fetchTrainersFromFirestore() {
        Log.d("FetchTrainers", "Fetching trainers Start ");
        int selectedId = radioGroup.getCheckedRadioButtonId();
        fStore.collection("users")
                .whereEqualTo("userType", "Trainer") // Assuming trainers have a "role" field set to "trainer"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        trainerUserIdList.clear();
                        trainerList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String trainerName = document.getString("fullName"); // Assuming trainers have a "name" field
                            String trainerID = document.getId();
                            Log.d("Trainer ID", trainerID);
                            Log.d("Trainer Name", trainerName);
                            trainerUserIdList.add(trainerID);
                            trainerList.add(trainerName);
                        }
                        // Populate the Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, trainerList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        trainerSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to fetch trainers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    public void fetchNutritionistsFromFirestore() {
        Log.d("FetchTrainers", "Fetching trainers Start ");
        int selectedId = radioGroup.getCheckedRadioButtonId();
        fStore.collection("users")
                .whereEqualTo("userType", "Nutrition") // Assuming trainers have a "role" field set to "trainer"
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        nutritionUserIdList.clear();
                        nutritionList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String nutritionName = document.getString("fullName"); // Assuming trainers have a "name" field
                            String nutritionID = document.getId();
                            Log.d("Trainer Name", nutritionName);
                            nutritionList.add(nutritionName);
                            nutritionUserIdList.add(nutritionID);
                        }
                        // Populate the Spinner
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, nutritionList);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        nutritionSpinner.setAdapter(adapter);
                    } else {
                        Toast.makeText(this, "Failed to fetch trainers.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String selectedDate = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    dateInput.setText(selectedDate);
                },
                year, month, day
        );
        datePickerDialog.show();
    }

    private void showTimePicker() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    String selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeInput.setText(selectedTime);
                },
                hour, minute, true
        );
        timePickerDialog.show();
    }
    private void onSubmitClick() {
        // Get selected radio button
        int selectedId = radioGroup.getCheckedRadioButtonId();
        String selectedOption = (selectedId == R.id.radioTrainer) ? "Trainer" : "Nutrition";
        // Get date and time
        String date = dateInput.getText().toString();
        String time = timeInput.getText().toString();
        String reason = reasonInput.getText().toString();
        // Validate inputs
        if (date.isEmpty() || time.isEmpty()) {
            Toast.makeText(this, "Please select a date and time.", Toast.LENGTH_SHORT).show();
            return;
        }
        // If Trainer is selected, ensure a trainer is chosen
        if (selectedId == R.id.radioTrainer && selectedOption.isEmpty()) {
            Toast.makeText(this, "Please select a trainer.", Toast.LENGTH_SHORT).show();
            return;
        }

            fStore.collection("users").document(fAuth.getCurrentUser().getUid()).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                username = document.getString("userName");
                                if (username == null) {
                                    username = "";
                                }

                            } else {
                                Toast.makeText(ScheduleAppointmentActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(ScheduleAppointmentActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });

        // Prepare data to save to Firestore
        Map<String, Object> appointment = new HashMap<>();
        appointment.put("userID", fAuth.getCurrentUser().getUid());
        appointment.put("userName", username);
        appointment.put("type", selectedOption);
        appointment.put("date", date);
        appointment.put("time", time);
        appointment.put("reason", reason);
        appointment.put("status", "Pending");
        if (selectedId == R.id.radioTrainer) {
            appointment.put("trainer", selectedTrainer);
            appointment.put("appointmentConductorUserID", selectedTrainerUserID);
        }
        if(selectedId == R.id.radioNutrition){
            appointment.put("nutrition",selectedNutrition);
            appointment.put("appointmentConductorUserID",selectedNutritionUserID);
        }
        // Save to Firestore
        fStore.collection("appointments")
                .add(appointment)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(this, "Appointment scheduled successfully!", Toast.LENGTH_SHORT).show();
                    finish(); // Close the activity
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to schedule appointment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    public void onBackClick(View view) {
        super.onBackPressed();
        finish();
    }
}