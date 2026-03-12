package com.mountrich.krushimitraadminapp.adapter;


import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.mountrich.krushimitraadminapp.EditProductActivity;
import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.model.Product;


import java.util.List;
import android.app.AlertDialog;
import android.content.Context;
import android.widget.Toast;
import com.google.firebase.firestore.FirebaseFirestore;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private Context context;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<Product> productList;

    public ProductAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Product product = productList.get(position);

        holder.tvName.setText(product.getName());
        holder.tvPrice.setText("Price: ₹ " + product.getPrice());
        holder.tvStock.setText("Stock: " + product.getStock());

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .into(holder.imgProduct);

        holder.btnDelete.setOnClickListener(v -> {

            new AlertDialog.Builder(context)
                    .setTitle("Delete Product")
                    .setMessage("Are you sure you want to delete this product?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        db.collection("products")
                                .document(product.getId())
                                .delete()
                                .addOnSuccessListener(unused -> {

                                    productList.remove(position);
                                    notifyItemRemoved(position);

                                    Toast.makeText(context,
                                            "Product deleted",
                                            Toast.LENGTH_SHORT).show();
                                });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.btnEdit.setOnClickListener(v -> {

            Intent intent = new Intent(v.getContext(), EditProductActivity.class);

            intent.putExtra("id", product.getId());
            intent.putExtra("name", product.getName());
            intent.putExtra("category", product.getCategory());
            intent.putExtra("price", product.getPrice()+"");
            intent.putExtra("description", product.getDescription());
            intent.putExtra("imageUrl", product.getImageUrl());
            intent.putExtra("stock", product.getStock());

            v.getContext().startActivity(intent);

        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgProduct, btnEdit, btnDelete;
        TextView tvName, tvPrice, tvStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProduct);
            tvName = itemView.findViewById(R.id.tvName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvStock = itemView.findViewById(R.id.tvStock);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
