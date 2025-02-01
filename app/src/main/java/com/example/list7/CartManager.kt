package com.example.list7

/**
 * CartManager is responsible for managing the items in the user's cart, including adding,
 * removing, updating quantities, and clearing the cart. It maintains the list of items in memory.
 */
object CartManager {
    private val cartItems = mutableListOf<Item>() // Changed to MutableList

    /**
     * Returns the list of items currently in the cart.
     *
     * @return A MutableList containing the items in the cart.
     */
    fun getCartItems(): MutableList<Item> = cartItems // Return MutableList

    /**
     * Adds an item to the cart. If the item is already in the cart, its quantity is incremented by 1.
     *
     * @param item The item to be added to the cart.
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
     * Removes a specific item from the cart.
     *
     * @param item The item to be removed from the cart.
     */
    fun removeItem(item: Item) {
        cartItems.remove(item)
    }

    /**
     * Clears all items from the cart.
     */
    fun clearCart() {
        cartItems.clear()
    }

    /**
     * Updates the quantity of a specific item in the cart.
     *
     * @param itemId The unique ID of the item whose quantity is to be updated.
     * @param newQuantity The new quantity to set for the item.
     */
    fun updateItemQuantity(itemId: String, newQuantity: Int) {
        val item = cartItems.find { it.uid == itemId }
        if (item != null) {
            item.quantity = newQuantity
        }
    }
}
