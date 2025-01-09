package com.example.ecommkoi


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(private val cartItems: List<CartItem>,
                  private val onRemoveClick: (CartItem) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tvProductName)
        val productPrice: TextView = itemView.findViewById(R.id.tvProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.tvProductQuantity)
        val productImage: ImageView = itemView.findViewById(R.id.ivProductImage)
        val removeButton: Button = itemView.findViewById(R.id.btnRemoveItem) // Add removeButton

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartItem = cartItems[position]
        Log.d("CartAdapter", "Binding cart item: $cartItem")
        holder.productName.text = cartItem.productName
        holder.productPrice.text = "$${cartItem.productPrice}"
        holder.productQuantity.text = "Quantity: ${cartItem.quantity}"
        holder.productImage.setImageResource(cartItem.productImage)

        // Handle remove button click
        holder.removeButton.setOnClickListener {
            onRemoveClick(cartItem)
        }
    }

    override fun getItemCount(): Int = cartItems.size
}