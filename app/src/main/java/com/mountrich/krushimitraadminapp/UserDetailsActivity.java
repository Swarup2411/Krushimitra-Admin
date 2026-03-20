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

public class UserDetailsActivity extends AppCompatActivity {

    TextView tvName, tvEmail, tvPhone, tvAddress;
    ImageView imgUser;

    FirebaseFirestore db;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_details);

        tvName = findViewById(R.id.tvName);
        tvEmail = findViewById(R.id.tvEmail);
        tvPhone = findViewById(R.id.tvPhone);
        tvAddress = findViewById(R.id.tvAddress);
        imgUser = findViewById(R.id.imgUser);

        db = FirebaseFirestore.getInstance();

        userId = getIntent().getStringExtra("userId");

        loadUserDetails();
        loadUserAddress(); // 🔥 IMPORTANT
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
}