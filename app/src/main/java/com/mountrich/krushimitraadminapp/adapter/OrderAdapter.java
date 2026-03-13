package com.mountrich.krushimitraadminapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mountrich.krushimitraadminapp.R;
import com.mountrich.krushimitraadminapp.model.Order;
import com.mountrich.krushimitraadminapp.model.OrderItem;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orderList;

    public OrderAdapter(List<Order> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {

        Order order = orderList.get(position);

        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvStatus.setText(order.getStatus());

        // Set status badge background
        switch(order.getStatus().toLowerCase()){
            case "placed":
                holder.tvStatus.setBackgroundResource(R.drawable.status_placed_bg);
                break;
            case "shipped":
                holder.tvStatus.setBackgroundResource(R.drawable.status_shipped_bg);
                break;
            case "delivered":
                holder.tvStatus.setBackgroundResource(R.drawable.status_delivered_bg);
                break;
            case "cancelled":
                holder.tvStatus.setBackgroundResource(R.drawable.status_cancelled_bg);
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.pending_bg);
        }

        holder.tvPayment.setText(order.getPaymentMethod());
        holder.tvAddress.setText(order.getDeliveryAddress());

        // Total
        holder.tvTotal.setText("₹" + order.getTotalAmount());

        // Order date
        if(order.getTimestamp() > 0){
            Date date = new Date(order.getTimestamp() * 1000); // convert to ms
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault());
            holder.tvOrderDate.setText(sdf.format(date));
        } else {
            holder.tvOrderDate.setText("");
        }

        holder.btnView.setOnClickListener(v -> {
            // TODO: open order details
        });

        holder.btnUpdate.setOnClickListener(v -> {
            // TODO: update order status
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder{

        TextView tvOrderId, tvStatus, tvPayment, tvAddress, tvTotal, tvOrderDate;
        View btnView, btnUpdate;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);

            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvPayment = itemView.findViewById(R.id.tvPayment);
            tvAddress = itemView.findViewById(R.id.tvAddress);
            tvTotal = itemView.findViewById(R.id.tvTotal);
            tvOrderDate = itemView.findViewById(R.id.tvOrderDate);

            btnView = itemView.findViewById(R.id.btnView);
            btnUpdate = itemView.findViewById(R.id.btnUpdate);
        }
    }
}