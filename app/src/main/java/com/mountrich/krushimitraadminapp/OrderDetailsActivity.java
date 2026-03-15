package com.mountrich.krushimitraadminapp;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.mountrich.krushimitraadminapp.adapter.OrderItemsAdapter;
import com.mountrich.krushimitraadminapp.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class OrderDetailsActivity extends AppCompatActivity {

    TextView tvOrderId, tvStatus, tvPayment, tvAddress, tvTotal;
    RecyclerView recyclerItems;

    FirebaseFirestore db;

    List<OrderItem> itemList;
    OrderItemsAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        tvOrderId = findViewById(R.id.tvOrderId);
        tvStatus = findViewById(R.id.tvStatus);
        tvPayment = findViewById(R.id.tvPayment);
        tvAddress = findViewById(R.id.tvAddress);
        tvTotal = findViewById(R.id.tvTotal);
        recyclerItems = findViewById(R.id.recyclerOrderItems);

        itemList = new ArrayList<>();
        adapter = new OrderItemsAdapter(itemList);

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        String orderId = getIntent().getStringExtra("orderId");

        if (orderId != null) {
            loadOrder(orderId);
        }
    }

    private void loadOrder(String orderId) {

        db.collection("orders")
                .document(orderId)
                .get()
                .addOnSuccessListener(doc -> {

                    if (!doc.exists()) return;

                    tvOrderId.setText("Order ID: " + doc.getString("orderId"));
                    tvStatus.setText("Status: " + doc.getString("status"));
                    tvPayment.setText("Payment: " + doc.getString("paymentMethod"));

                    String address = doc.getString("deliveryAddress");

                    if(address != null){
                        tvAddress.setText("Address: " + doc.getString("deliveryAddress"));
                    }


                    // 🔹 Total amount (safe parsing)
                    double total = 0;
                    Object totalObj = doc.get("totalAmount");

                    if (totalObj instanceof Double) {
                        total = (Double) totalObj;
                    } else if (totalObj instanceof Long) {
                        total = ((Long) totalObj).doubleValue();
                    }

                    tvTotal.setText("Total: ₹" + total);

                    // 🔹 Timestamp (order date)
                    Timestamp timestamp = doc.getTimestamp("timestamp");
                    if (timestamp != null) {
                        Date date = timestamp.toDate();
                        SimpleDateFormat sdf =
                                new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
                    }

                    // 🔹 Load items array
                    List<Map<String, Object>> items =
                            (List<Map<String, Object>>) doc.get("items");

                    itemList.clear();

                    if (items != null) {

                        for (Map<String, Object> map : items) {

                            OrderItem item = new OrderItem();

                            item.setProductId((String) map.get("productId"));
                            item.setName((String) map.get("name"));
                            item.setImageUrl((String) map.get("imageUrl"));

                            // price safe parsing
                            double price = 0;
                            Object priceObj = map.get("price");

                            if (priceObj instanceof Double)
                                price = (Double) priceObj;
                            else if (priceObj instanceof Long)
                                price = ((Long) priceObj).doubleValue();

                            item.setPrice(price);

                            // quantity safe parsing
                            int qty = 0;
                            Object qtyObj = map.get("quantity");

                            if (qtyObj instanceof Long)
                                qty = ((Long) qtyObj).intValue();
                            else if (qtyObj instanceof Double)
                                qty = ((Double) qtyObj).intValue();

                            item.setQuantity(qty);

                            itemList.add(item);
                        }
                    }

                    adapter.notifyDataSetChanged();

                });
    }
}