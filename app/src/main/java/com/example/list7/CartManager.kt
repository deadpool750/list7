package com.example.list7

object CartManager {
    private val cartItems = mutableListOf<Item>()

    fun addItemToCart(item: Item) {
        cartItems.add(item)
    }

    fun getCartItems(): List<Item> = cartItems

    fun clearCart() {
        cartItems.clear()
    }
}
