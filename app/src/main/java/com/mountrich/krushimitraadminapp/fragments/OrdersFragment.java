package com.mountrich.krushimitraadminapp.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.adapter.OrderAdapter;
import com.mountrich.krushimitraadminapp.model.Order;
import com.mountrich.krushimitraadminapp.model.OrderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersFragment extends Fragment {

    RecyclerView recyclerView;
    OrderAdapter adapter;

    List<Order> orderList = new ArrayList<>();
    List<Order> filteredList = new ArrayList<>();

    FirebaseFirestore db;

    TextView tvTotalOrders, tvRevenue, tvPending;
    TextInputEditText etSearch;
    ChipGroup chipGroup;

    private String currentFilter = "All";

    public OrdersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tvTotalOrders = view.findViewById(R.id.tvTotalOrders);
        tvRevenue = view.findViewById(R.id.tvRevenue);
        tvPending = view.findViewById(R.id.tvPending);

        etSearch = view.findViewById(R.id.etSearch);
        chipGroup = view.findViewById(R.id.chipGroupStatus);

        adapter = new OrderAdapter(filteredList, () -> loadOrders());
        recyclerView.setAdapter(adapter);

        setupFilters();
        setupSearch();

        loadOrders();

        return view;
    }

    // 🔍 SEARCH
    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                applyFilterAndSearch();
            }
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    // 🎯 FILTER (Chips)
    private void setupFilters() {

        chipGroup.setOnCheckedChangeListener((group, checkedId) -> {

            if (checkedId == R.id.chipAll) currentFilter = "All";
            else if (checkedId == R.id.chipPlaced) currentFilter = "Placed";
            else if (checkedId == R.id.chipShipped) currentFilter = "Shipped";
            else if (checkedId == R.id.chipDelivered) currentFilter = "Delivered";
            else if (checkedId == R.id.chipCancelled) currentFilter = "Cancelled";

            applyFilterAndSearch();
        });
    }

    // 🔥 APPLY BOTH FILTER + SEARCH
    private void applyFilterAndSearch() {

        String searchText = etSearch.getText().toString().toLowerCase();

        filteredList.clear();

        for (Order order : orderList) {

            boolean matchesFilter = currentFilter.equals("All") ||
                    order.getStatus().equalsIgnoreCase(currentFilter);

            boolean matchesSearch = order.getOrderId()
                    .toLowerCase()
                    .contains(searchText);

            if (matchesFilter && matchesSearch) {
                filteredList.add(order);
            }
        }

        adapter.notifyDataSetChanged();
    }

    // 📊 LOAD ORDERS + ANALYTICS
    private void loadOrders() {

        db.collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    orderList.clear();

                    int totalOrders = 0;
                    double totalRevenue = 0;
                    int pendingOrders = 0;

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        try {

                            String orderId = doc.getString("orderId");
                            String deliveryAddress = doc.getString("deliveryAddress");
                            String paymentMethod = doc.getString("paymentMethod");
                            String paymentStatus = doc.getString("paymentStatus");
                            String status = doc.getString("status");

                            // 🔹 Timestamp
                            long timestamp = 0;
                            Object tsObj = doc.get("timestamp");

                            if (tsObj instanceof com.google.firebase.Timestamp) {
                                timestamp = ((com.google.firebase.Timestamp) tsObj).getSeconds();
                            } else if (tsObj instanceof Long) {
                                timestamp = (Long) tsObj;
                            }

                            // 🔹 Total Amount
                            double totalAmount = 0;
                            Object totalObj = doc.get("totalAmount");

                            if (totalObj instanceof Double) {
                                totalAmount = (Double) totalObj;
                            } else if (totalObj instanceof Long) {
                                totalAmount = ((Long) totalObj).doubleValue();
                            }

                            // 🔹 Analytics
                            totalOrders++;
                            totalRevenue += totalAmount;

                            if ("Placed".equalsIgnoreCase(status)) {
                                pendingOrders++;
                            }

                            // 🔹 Items
                            List<OrderItem> items = new ArrayList<>();
                            List<Map<String, Object>> itemsMap =
                                    (List<Map<String, Object>>) doc.get("items");

                            if (itemsMap != null) {
                                for (Map<String, Object> itemMap : itemsMap) {

                                    OrderItem item = new OrderItem();

                                    item.setName((String) itemMap.get("name"));
                                    item.setImageUrl((String) itemMap.get("imageUrl"));

                                    Object priceObj = itemMap.get("price");
                                    double price = 0;
                                    if (priceObj instanceof Double)
                                        price = (Double) priceObj;
                                    else if (priceObj instanceof Long)
                                        price = ((Long) priceObj).doubleValue();

                                    item.setPrice(price);

                                    Object qtyObj = itemMap.get("quantity");
                                    int qty = 0;
                                    if (qtyObj instanceof Long)
                                        qty = ((Long) qtyObj).intValue();

                                    item.setQuantity(qty);

                                    items.add(item);
                                }
                            }

                            Order order = new Order(orderId, deliveryAddress,
                                    paymentMethod, paymentStatus,
                                    status, timestamp, items);

                            order.setTotalAmount(totalAmount);

                            orderList.add(order);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    // 🔥 Apply filter + search after loading
                    applyFilterAndSearch();

                    // 📊 Update Analytics UI
                    tvTotalOrders.setText(String.valueOf(totalOrders));
                    tvRevenue.setText("₹" + totalRevenue);
                    tvPending.setText(String.valueOf(pendingOrders));

                });
    }
}