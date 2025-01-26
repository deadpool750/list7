package com.example.list7

object CartManager {
    private val cartItems = mutableListOf<Item>() // Changed to MutableList

    /**
     * Get the list of items currently in the cart.
     */
    fun getCartItems(): MutableList<Item> = cartItems // Return MutableList

    /**
     * Add an item to the cart.
     */
    fun addItemToCart(item: Item) {
        val existingItem = cartItems.find { it.uid == item.uid }
        if (existingItem != null) {
            // Item already in cart, increment its quantity
            existingItem.quantity += 1
        } else {
            // Add new item with quantity set to 1
            cartItems.add(item.copy(quantity = 1))
        }
    }


    /**
     * Remove a specific item from the cart.
     */
    fun removeItem(item: Item) {
        cartItems.remove(item)
    }

    /**
     * Clear all items from the cart.
     */
    fun clearCart() {
        cartItems.clear()
    }

    /**
     * Update the quantity of a specific item in the cart.
     */
    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        val item = cartItems.find { it.uid == itemId }
        if (item != null) {
            item.quantity = newQuantity
        }
    }
}
