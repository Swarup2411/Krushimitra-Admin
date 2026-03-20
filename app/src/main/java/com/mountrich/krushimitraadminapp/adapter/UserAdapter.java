package com.mountrich.krushimitraadminapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.UserDetailsActivity;
import com.mountrich.krushimitraadminapp.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    List<User> userList;
    Context context;

    OnUserClick listener;

    public UserAdapter(Context context, List<User> userList, OnUserClick listener) {
        this.context = context;
        this.userList = userList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {

        User user = userList.get(position);

        holder.tvName.setText(user.getName());
        holder.tvEmail.setText(user.getEmail());
        holder.tvPhone.setText(user.getMobile());

        // Load Image (Glide)
        Glide.with(context)
                .load(user.getProfileImage())
                .placeholder(R.drawable.farmer_icon)
                .into(holder.imgUser);

//        holder.itemView.setOnClickListener(v -> {
//
//            Intent intent = new Intent(context, UserDetailsActivity.class);
//            intent.putExtra("userId", user.getUserId());
//            context.startActivity(intent);
//
//        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onClick(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        ImageView imgUser;
        TextView tvName, tvEmail, tvPhone;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);

            imgUser = itemView.findViewById(R.id.imgUser);
            tvName = itemView.findViewById(R.id.tvName);
            tvEmail = itemView.findViewById(R.id.tvEmail);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }
    }

    public interface OnUserClick {
        void onClick(User user);
    }
}