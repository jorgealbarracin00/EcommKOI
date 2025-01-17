package com.example.ecommkoi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.*

class PurchaseHistoryAdapter(private val orders: List<Order>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_HEADER = 0
        private const val TYPE_ITEM = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0 || orders[position].orderSessionId != orders[position - 1].orderSessionId) {
            TYPE_HEADER // First item of each session group is a header
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_purchase_history_header, parent, false)
            PurchaseHistoryHeaderViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_purchase_history, parent, false)
            PurchaseHistoryViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val order = orders[position]
        val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

        if (holder is PurchaseHistoryHeaderViewHolder) {
            holder.orderGroupHeader.text =
                "Order Placed: ${dateFormatter.format(Date(order.orderDate))}" // Group header
        } else if (holder is PurchaseHistoryViewHolder) {
            holder.productName.text = "${order.productName} (x${order.quantity})"
            holder.orderTotal.text = "Total: $${order.totalPrice}"
        }
    }

    override fun getItemCount(): Int = orders.size

    class PurchaseHistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val orderTotal: TextView = itemView.findViewById(R.id.tvOrderTotal)
    }

    class PurchaseHistoryHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderGroupHeader: TextView = itemView.findViewById(R.id.tvOrderGroupHeader)
    }
}