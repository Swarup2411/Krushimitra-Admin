package com.mountrich.krushimitraadminapp.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
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
    List<Order> orderList;
    FirebaseFirestore db;

    public OrdersFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = FirebaseFirestore.getInstance();
        orderList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_order, container, false);

        recyclerView = view.findViewById(R.id.recyclerOrders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);

        adapter = new OrderAdapter(orderList, new OrderAdapter.OnOrderUpdatedListener() {
            @Override
            public void onOrderUpdated() {
                loadOrders();
            }
        });
        recyclerView.setAdapter(adapter);

        loadOrders();

        return view;
    }

    private void loadOrders() {

        db.collection("orders")
                .orderBy("timestamp", Query.Direction.DESCENDING) // newest orders first
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {

                    orderList.clear();

                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {

                        try {

                            String orderId = doc.getString("orderId");
                            String deliveryAddress = doc.getString("deliveryAddress");
                            String paymentMethod = doc.getString("paymentMethod");
                            String paymentStatus = doc.getString("paymentStatus");
                            String status = doc.getString("status");

                            // 🔹 Safe timestamp parsing
                            long timestamp = 0;
                            Object tsObj = doc.get("timestamp");

                            if (tsObj instanceof com.google.firebase.Timestamp) {
                                timestamp = ((com.google.firebase.Timestamp) tsObj).getSeconds();
                            }
                            else if (tsObj instanceof Long) {
                                timestamp = (Long) tsObj;
                            }

                            // 🔹 Total amount
                            double totalAmount = 0;
                            Object totalObj = doc.get("totalAmount");

                            if (totalObj instanceof Double) {
                                totalAmount = (Double) totalObj;
                            }
                            else if (totalObj instanceof Long) {
                                totalAmount = ((Long) totalObj).doubleValue();
                            }

                            // 🔹 Items list
                            List<OrderItem> items = new ArrayList<>();

                            List<Map<String, Object>> itemsMap =
                                    (List<Map<String, Object>>) doc.get("items");

                            if (itemsMap != null) {

                                for (Map<String, Object> itemMap : itemsMap) {

                                    OrderItem item = new OrderItem();

                                    item.setProductId((String) itemMap.get("productId"));
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
                                    else if (qtyObj instanceof Double)
                                        qty = ((Double) qtyObj).intValue();

                                    item.setQuantity(qty);

                                    items.add(item);
                                }
                            }

                            Order order = new Order(
                                    orderId,
                                    deliveryAddress,
                                    paymentMethod,
                                    paymentStatus,
                                    status,
                                    timestamp,
                                    items
                            );

                            order.setTotalAmount(totalAmount);

                            orderList.add(order);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    adapter.notifyDataSetChanged();

                })
                .addOnFailureListener(e -> e.printStackTrace());
    }
}