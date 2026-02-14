package com.mountrich.krushimitraadminapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class SplashActivityAdmin extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_admin);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        new Handler(getMainLooper())
                .postDelayed(this::checkLoginStatus, 1500);


        ImageView logo = findViewById(R.id.imgLogo);

        Animation animation = AnimationUtils
                .loadAnimation(this, R.anim.logo_animation);

        logo.startAnimation(animation);

    }

    private void checkLoginStatus() {

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {

            db.collection("admins")
                    .document(currentUser.getUid())
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {

                        if (documentSnapshot.exists() &&
                                "admin".equals(documentSnapshot.getString("role"))) {

                            startActivity(new Intent(this,
                                    MainActivity.class));
                            finish();

                        } else {
                            mAuth.signOut();
                            goToLogin();
                        }
                    })
                    .addOnFailureListener(e -> goToLogin());

        } else {
            goToLogin();
        }
    }

    private void goToLogin() {
        startActivity(new Intent(this,
                LoginActivityAdmin.class));
        finish();
    }
}
