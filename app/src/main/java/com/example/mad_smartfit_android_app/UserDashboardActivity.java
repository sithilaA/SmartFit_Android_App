package com.example.mad_smartfit_android_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class UserDashboardActivity extends AppCompatActivity {

    private TextView userGreetingText;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Reference to TextView
        userGreetingText = findViewById(R.id.userGreetingText);

        // Fetch and display username
        getUserName();

    }

    public void onSettingsClick(View view) {
        startActivity(new Intent(getApplicationContext(),SettingsActivity.class));
        finish();
    }
    public void onBookAppointmentClick(View view) {
        startActivity(new Intent(getApplicationContext(),AppointmentActivity.class));

    }
    public void onNutritionPlanClick(View view) {
        startActivity(new Intent(getApplicationContext(),NutritionPlanActivity.class));

    }
    public void onWorkoutClick(View view) {
        startActivity(new Intent(getApplicationContext(),WorkoutsActivity.class));
        finish();
    }
    //Set userName to TextView
    private void getUserName() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("users").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String username = document.getString("userName");
                                if (username == null) {
                                    username = "";
                                }
                                userGreetingText.setText("Welcome, " + username );

                            } else {
                                Toast.makeText(UserDashboardActivity.this, "User data not found!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(UserDashboardActivity.this, "Failed to fetch data!", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show();
        }
    }


}