package com.mountrich.krushimitraadminapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.adapter.ProductAdapter;
import com.mountrich.krushimitraadminapp.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProductAdapter adapter;
    private List<Product> productList;
    private FirebaseFirestore db;
    private ShimmerFrameLayout shimmerLayout;
    private TextView tvEmpty;
    private SwipeRefreshLayout swipe;

    public ProductFragment() {}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_product, container, false);

        // Initialize Views
        recyclerView = view.findViewById(R.id.recyclerView);
        shimmerLayout = view.findViewById(R.id.shimmerLayout);
        tvEmpty = view.findViewById(R.id.tvEmpty);
        swipe = view.findViewById(R.id.swipeRefresh);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productList = new ArrayList<>();
        adapter = new ProductAdapter(productList);
        recyclerView.setAdapter(adapter);

        // Firestore Instance
        db = FirebaseFirestore.getInstance();

        // Swipe Refresh
        swipe.setOnRefreshListener(() -> loadProducts());

        // Load Data First Time
        loadProducts();

        return view;
    }

    private void loadProducts() {

        // Show Shimmer
        shimmerLayout.setVisibility(View.VISIBLE);
        shimmerLayout.startShimmer();
        recyclerView.setVisibility(View.GONE);
        tvEmpty.setVisibility(View.GONE);

        db.collection("products")
                .get()
                .addOnCompleteListener(task -> {

                    shimmerLayout.stopShimmer();
                    shimmerLayout.setVisibility(View.GONE);
                    swipe.setRefreshing(false);

                    if (task.isSuccessful() && task.getResult() != null) {

                        productList.clear();

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Product product = document.toObject(Product.class);
                            productList.add(product);
                        }

                        adapter.notifyDataSetChanged();

                        if (productList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        } else {
                            tvEmpty.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        }

                    } else {
                        Toast.makeText(getContext(),
                                "Failed to load products",
                                Toast.LENGTH_SHORT).show();
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                });
    }

    @Override
    public void onPause() {
        shimmerLayout.stopShimmer();
        super.onPause();
    }
}
