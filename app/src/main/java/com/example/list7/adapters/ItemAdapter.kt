package com.example.list7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

class ItemAdapter(
    private val context: Context,
    private val itemList: List<Item>,
    private val onAddToCart: (Item) -> Unit // Callback for adding items to cart
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.itemName.text = item.itemName
        holder.itemPrice.text = "$${item.price}"

        // Add to Cart Button click listener
        holder.addToCartButton.setOnClickListener {
            onAddToCart(item) // Trigger the callback
            Toast.makeText(context, "${item.itemName} added to cart", Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = itemList.size

    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val addToCartButton: Button = itemView.findViewById(R.id.add_to_cart_button) // Add to Cart button
    }
}
