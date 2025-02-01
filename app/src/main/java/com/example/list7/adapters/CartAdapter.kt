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

/**
 * Adapter for displaying a list of cart items in a RecyclerView.
 *
 * @param context The context in which the adapter is used.
 * @param cartItems The list of items in the cart.
 * @param onDeleteClick Callback function invoked when an item is deleted.
 * @param onQuantityChange Callback function invoked when an item's quantity changes.
 */
class CartAdapter(
    private val context: Context,
    private var cartItems: MutableList<Item>,
    private val onDeleteClick: (Item) -> Unit,
    private val onQuantityChange: (Item, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    /**
     * Creates a new ViewHolder instance when needed.
     *
     * @param parent The parent ViewGroup.
     * @param viewType The type of the view.
     * @return A new instance of CartViewHolder.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false)
        return CartViewHolder(view)
    }

    /**
     * Binds data to the ViewHolder at the specified position.
     *
     * @param holder The ViewHolder to bind data to.
     * @param position The position of the item within the dataset.
     */
    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartItems[position]

        holder.itemName.text = item.itemName
        holder.itemPrice.text = "Price: $${item.price}"
        holder.itemQuantity.setText(item.quantity.toString())

        // Increment quantity
        holder.addButton.setOnClickListener {
            val currentQuantity = holder.itemQuantity.text.toString().toIntOrNull() ?: 1
            val newQuantity = currentQuantity + 1
            holder.itemQuantity.setText(newQuantity.toString())
            onQuantityChange(item, newQuantity)
        }

        // Decrement quantity
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

        // Delete item
        holder.deleteButton.setOnClickListener {
            onDeleteClick(item)
        }
    }

    /**
     * Returns the total number of items in the cart.
     *
     * @return The size of the cartItems list.
     */
    override fun getItemCount(): Int = cartItems.size

    /**
     * Updates the cart items and refreshes the RecyclerView.
     *
     * @param newCartItems The new list of cart items.
     */
    fun updateCartItems(newCartItems: MutableList<Item>) {
        cartItems = newCartItems
        notifyDataSetChanged()
    }

    /**
     * ViewHolder for a cart item, holding references to UI elements.
     *
     * @param itemView The view representing a single cart item.
     */
    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val itemName: TextView = itemView.findViewById(R.id.cart_item_name)
        val itemPrice: TextView = itemView.findViewById(R.id.cart_item_price)
        val itemQuantity: EditText = itemView.findViewById(R.id.cart_item_quantity)
        val addButton: Button = itemView.findViewById(R.id.cart_item_add_button)
        val subtractButton: Button = itemView.findViewById(R.id.cart_item_subtract_button)
        val deleteButton: Button = itemView.findViewById(R.id.cart_item_delete_button)
    }
}
