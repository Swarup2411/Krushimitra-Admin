package com.mountrich.krushimitraadminapp;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mountrich.krushimitraadminapp.databinding.ActivityUsersDetailsBinding;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.widget.Button;
import android.widget.Toast;

public class UserDetailsActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvAddress;
    ImageView imgUser;

    FirebaseFirestore db;
    String userId;
    Button btnCall, btnDeleteUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        imgUser = findViewById(R.id.imgUser);
        btnCall = findViewById(R.id.btnCall);
        btnDeleteUser = findViewById(R.id.btnDeleteUser);

        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("userId");

        loadUserDetails();
        loadUserAddress(); // 🔥 IMPORTANT


        btnCall.setOnClickListener(v -> {

            String phone = tvPhone.getText().toString();

            if (phone.equals("N/A") || phone.isEmpty()) {
                Toast.makeText(this, "Phone not available", Toast.LENGTH_SHORT).show();
                return;
            }

            new AlertDialog.Builder(this)
                    .setTitle("Call User")
                    .setMessage("Do you want to call this user?")
                    .setPositiveButton("Call", (dialog, which) -> {

                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.setData(Uri.parse("tel:" + phone));
                        startActivity(intent);

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        btnDeleteUser.setOnClickListener(v -> {

            new AlertDialog.Builder(this)
                    .setTitle("Delete User")
                    .setMessage("Are you sure you want to delete this user?")
                    .setPositiveButton("Delete", (dialog, which) -> deleteUser())
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    // ✅ 1. Load basic user info
    private void loadUserDetails() {

        db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (doc.exists()) {

                        String name = doc.getString("name");
                        String email = doc.getString("email");
                        String mobile = doc.getString("mobile");
                        String image = doc.getString("profileImage");

                        tvName.setText(name != null ? name : "N/A");
                        tvEmail.setText(email != null ? email : "N/A");
                        tvPhone.setText(mobile != null ? mobile : "N/A");

                        Glide.with(this)
                                .load(image)
                                .placeholder(R.drawable.farmer_icon)
                                .into(imgUser);
                    }
                });
    }

    // ✅ 2. Load address from subcollection
    private void loadUserAddress() {

        db.collection("users")
                .document(userId)
                .collection("addresses")
                .whereEqualTo("isDefault", true)
                .get()
                .addOnSuccessListener(query -> {

                    if (!query.isEmpty()) {

                        DocumentSnapshot doc = query.getDocuments().get(0);

                        String addressLine = doc.getString("addressLine");
                        String city = doc.getString("city");
                        String state = doc.getString("state");
                        String pincode = doc.getString("pincode");

                        String fullAddress =
                                (addressLine != null ? addressLine : "") + ", " +
                                        (city != null ? city : "") + ", " +
                                        (state != null ? state : "") + " - " +
                                        (pincode != null ? pincode : "");

                        tvAddress.setText(fullAddress);

                    } else {
                        tvAddress.setText("No default address");
                    }
                });
    }


    private void deleteUser() {

        db.collection("users")
                .document(userId)
                .delete()
                .addOnSuccessListener(unused -> {

                    Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    finish(); // go back

                })
                .addOnFailureListener(e -> {

                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                });
    }
}