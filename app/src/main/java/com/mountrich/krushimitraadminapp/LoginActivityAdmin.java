package com.mountrich.krushimitraadminapp;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivityAdmin extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword;
    private MaterialButton btnLogin;
    private TextView tvRegister;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_admin);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvRegister = findViewById(R.id.tvRegister);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnLogin.setOnClickListener(v -> loginAdmin());

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivityAdmin.this,RegisterActivityAdmin.class));
            }
        });
    }

    private void loginAdmin() {

        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        btnLogin.setEnabled(false);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {

                    if (task.isSuccessful()) {

                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            checkAdminRole(user.getUid());
                        }

                    } else {
                        btnLogin.setEnabled(true);
                        Toast.makeText(this,
                                "Authentication Failed",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAdminRole(String uid) {

        db.collection("admins")
                .document(uid)
                .get()
                .addOnSuccessListener(documentSnapshot -> {

                    btnLogin.setEnabled(true);

                    if (documentSnapshot.exists()) {

                        String role = documentSnapshot.getString("role");

                        if ("admin".equals(role)) {

                            Toast.makeText(this,
                                    "Login Successful",
                                    Toast.LENGTH_SHORT).show();

                            startActivity(new Intent(LoginActivityAdmin.this,
                                    MainActivity.class));
                            finish();

                        } else {
                            logoutUser("Access Denied");
                        }

                    } else {
                        logoutUser("Not an Admin Account");
                    }
                })
                .addOnFailureListener(e -> {
                    btnLogin.setEnabled(true);
                    Toast.makeText(this,
                            "Error checking role",
                            Toast.LENGTH_SHORT).show();
                });
    }

    private void logoutUser(String message) {
        mAuth.signOut();
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            checkAdminRole(currentUser.getUid());
        }
    }

}
