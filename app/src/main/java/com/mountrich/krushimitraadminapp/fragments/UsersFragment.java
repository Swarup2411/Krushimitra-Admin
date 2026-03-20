package com.mountrich.krushimitraadminapp.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.UserDetailsActivity;
import com.mountrich.krushimitraadminapp.adapter.UserAdapter;
import com.mountrich.krushimitraadminapp.model.User;

import java.util.ArrayList;
import java.util.List;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    UserAdapter adapter;
    List<User> userList = new ArrayList<>();

    FirebaseFirestore db;
    TextView tvTotalUsers;

    TextInputEditText etSearchUser;
    List<User> filteredList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.recyclerUsers);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        etSearchUser = view.findViewById(R.id.etSearchUser);

        adapter = new UserAdapter(getContext(), filteredList, user -> {

            Intent intent = new Intent(getContext(), UserDetailsActivity.class);
            intent.putExtra("userId", user.getUserId());
            startActivity(intent);

        });
        recyclerView.setAdapter(adapter);
        tvTotalUsers = view.findViewById(R.id.tvTotalUsers);

        db = FirebaseFirestore.getInstance();

        loadUsers();

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterUsers(s.toString());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    private void filterUsers(String text) {

        filteredList.clear();

        for (User user : userList) {

            String name = user.getName() != null ? user.getName().toLowerCase() : "";
            String email = user.getEmail() != null ? user.getEmail().toLowerCase() : "";

            if (name.contains(text.toLowerCase()) || email.contains(text.toLowerCase())) {
                filteredList.add(user);
            }
        }

        adapter.notifyDataSetChanged();
    }

    private void loadUsers() {

        db.collection("users")
                .addSnapshotListener((value, error) -> {

                    if (error != null) return;

                    if (value != null) {
                        Log.d("FIREBASE_DEBUG", "Documents: " + value.getDocuments().size());
                    }

                    userList.clear();

                    for (DocumentSnapshot doc : value.getDocuments()) {

                        try {

                            String userId = doc.getId();
                            String name = doc.getString("name");
                            String email = doc.getString("email");
                            String mobile = doc.getString("mobile");
                            String image = doc.getString("profileImage");

                            Log.d("FIREBASE_USER", "Name: " + name + " Email: " + email);
                            User user = new User(userId, name, email, mobile, image);

                            userList.add(user);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    filteredList.clear();
                    filteredList.addAll(userList);
                    adapter.notifyDataSetChanged();
                    tvTotalUsers.setText(String.valueOf(userList.size()));
                });
    }
}