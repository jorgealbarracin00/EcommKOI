package com.example.ecommkoi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.Locale
import java.text.SimpleDateFormat
import java.util.Date

class PurchaseHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<PurchaseHistoryAdapter.PurchaseHistoryViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PurchaseHistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase_history, parent, false)
        return PurchaseHistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: PurchaseHistoryViewHolder, position: Int) {
        val order = orders[position]

        val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        holder.orderDate.text = dateFormat.format(Date(order.orderDate))

        holder.productName.text = "Order #${order.id} - ${order.productName}"  // ✅ Now works correctly
        holder.orderTotal.text = "Total: $${order.totalPrice}"
        holder.orderQuantity.text = "Quantity: ${order.quantity}"
    }

    override fun getItemCount(): Int = orders.size

    class PurchaseHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderDate: TextView = itemView.findViewById(R.id.tvOrderDate)
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val orderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
        val orderQuantity: TextView = itemView.findViewById(R.id.tvOrderQuantity)
    }
}