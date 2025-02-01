package com.example.list7

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView

/**
 * Adapter for displaying a list of items in a RecyclerView.
 *
 * @param context The context in which the adapter is used.
 * @param itemList The list of items to display.
 * @param onAddToCart Callback function invoked when an item is added to the cart.
 */
class ItemAdapter(
    private val context: Context,
    private val itemList: List<Item>,
    private val onAddToCart: (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    /**
     * Creates a new ViewHolder instance when needed.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return A new instance of ItemViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)
        return ItemViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item within the dataset.
     */
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

    /**
     * Returns the total number of items in the list.
     *
     * @return The size of the itemList.
     */
    override fun getItemCount(): Int = itemList.size

    /**
     * ViewHolder for an item, holding references to UI elements.
     *
     * @param itemView The view representing a single item.
     */
    class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.item_price)
        val addToCartButton: Button = itemView.findViewById(R.id.add_to_cart_button) // Add to Cart button
    }
}
