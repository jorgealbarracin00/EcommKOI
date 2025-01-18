package com.example.ecommkoi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PurchaseHistoryAdapter(private val orderList: List<Order>) :
    RecyclerView.Adapter<PurchaseHistoryAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productName: TextView = view.findViewById(R.id.tvProductName)
        val quantity: TextView = view.findViewById(R.id.tvQuantity)
        val totalPrice: TextView = view.findViewById(R.id.tvTotalPrice)
        val orderDate: TextView = view.findViewById(R.id.tvOrderDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_purchase_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val order = orderList[position]
        holder.productName.text = order.productName
        holder.quantity.text = "Quantity: ${order.quantity}"
        holder.totalPrice.text = "Total: $${order.totalPrice}"
        holder.orderDate.text = "Order Date: ${java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a").format(order.orderDate)}"
    }

    override fun getItemCount() = orderList.size
}