package com.example.list7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class CartAdapter(
    private val context: Context,
    private var cartItems: MutableList<Item>, // Updated to MutableList for dynamic updates
    private val onDeleteClick: (Item) -> Unit,
    private val onQuantityChange: (Item, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.itemName.text = item.itemName
        holder.itemPrice.text = "Price: $${item.price}"
        holder.itemQuantity.setText(item.quantity.toString()) // Display initial quantity

        // Add button logic
        holder.addButton.setOnClickListener {
            val currentQuantity = holder.itemQuantity.text.toString().toIntOrNull() ?: 1
            val newQuantity = currentQuantity + 1
            holder.itemQuantity.setText(newQuantity.toString())
            onQuantityChange(item, newQuantity)
        }

        // Subtract button logic
        holder.subtractButton.setOnClickListener {
            val currentQuantity = holder.itemQuantity.text.toString().toIntOrNull() ?: 1
            if (currentQuantity > 1) {
                val newQuantity = currentQuantity - 1
                holder.itemQuantity.setText(newQuantity.toString())
                onQuantityChange(item, newQuantity)
            } else {
                Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
            }
        }

        // Delete button logic
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    override fun getItemCount(): Int = cartItems.size

    /**
     * Update the cart items dynamically and refresh the list.
     */
    fun updateCartItems(newCartItems: MutableList<Item>) {
        cartItems = newCartItems
        notifyDataSetChanged() // Refresh the adapter
    }

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.cart_item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.cart_item_price)
        val itemQuantity: EditText = itemView.findViewById(R.id.cart_item_quantity)
        val addButton: Button = itemView.findViewById(R.id.cart_item_add_button)
        val subtractButton: Button = itemView.findViewById(R.id.cart_item_subtract_button)
        val deleteButton: Button = itemView.findViewById(R.id.cart_item_delete_button)
    }
}
