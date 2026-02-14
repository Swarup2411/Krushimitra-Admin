package com.mountrich.krushimitraadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mountrich.krushimitraadminapp.R;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivityAdmin extends AppCompatActivity {

    private TextInputEditText etName, etEmail, etPassword, etConfirmPassword, etSecretCode;
    private MaterialButton btnRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_admin);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etName = findViewById(R.id.etName);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etSecretCode = findViewById(R.id.etSecretCode);
        btnRegister = findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> registerAdmin());
    }

    private void registerAdmin() {

        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String enteredCode = etSecretCode.getText().toString().trim();

        // Reset errors
        etName.setError(null);
        etEmail.setError(null);
        etPassword.setError(null);
        etConfirmPassword.setError(null);
        etSecretCode.setError(null);

        // Name validation
        if (name.isEmpty()) {
            etName.setError("Name is required");
            etName.requestFocus();
            return;
        }

        // Email validation
        if (email.isEmpty()) {
            etEmail.setError("Email is required");
            etEmail.requestFocus();
            return;
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Enter valid email");
            etEmail.requestFocus();
            return;
        }

        // Password validation
        if (password.isEmpty()) {
            etPassword.setError("Password is required");
            etPassword.requestFocus();
            return;
        }

        if (password.length() < 6) {
            etPassword.setError("Password must be at least 6 characters");
            etPassword.requestFocus();
            return;
        }

        // Confirm password
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Passwords do not match");
            etConfirmPassword.requestFocus();
            return;
        }

        // Secret code validation
        if (enteredCode.isEmpty()) {
            etSecretCode.setError("Secret Code required");
            etSecretCode.requestFocus();
            return;
        }

        btnRegister.setEnabled(false);

        // Fetch secret code from Firestore
        db.collection("app_config")
                .document("admin_config")
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    if (!documentSnapshot.exists()) {
                        btnRegister.setEnabled(true);
                        Toast.makeText(this,
                                "Config not found",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String firestoreCode =
                            documentSnapshot.getString("secret_code");

                    if (firestoreCode == null ||
                            !enteredCode.equals(firestoreCode)) {

                        btnRegister.setEnabled(true);
                        etSecretCode.setError("Invalid Secret Code");
                        etSecretCode.requestFocus();
                        return;
                    }

                    createAdminAccount(name, email, password);
                })
                .addOnFailureListener(e -> {
                    btnRegister.setEnabled(true);
                    Toast.makeText(this,
                            "Error fetching secret code",
                            Toast.LENGTH_SHORT).show();
                });
    }


    private void createAdminAccount(String name,
                                    String email,
                                    String password) {

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        String uid = mAuth.getCurrentUser().getUid();

                        Map<String, Object> admin = new HashMap<>();
                        admin.put("name", name);
                        admin.put("email", email);
                        admin.put("role", "admin");
                        admin.put("createdAt", FieldValue.serverTimestamp());

                        db.collection("admins")
                                .document(uid)
                                .set(admin)
                                .addOnSuccessListener(unused -> {

                                    Toast.makeText(this,
                                            "Admin Registered Successfully",
                                            Toast.LENGTH_SHORT).show();

                                    startActivity(new Intent(this,
                                            MainActivity.class));
                                    finish();
                                });

                    } else {
                        btnRegister.setEnabled(true);
                        Toast.makeText(this,
                                "Registration Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

}
