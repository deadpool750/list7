package com.example.list7

/**
 * Data class representing an item in a shopping list or cart.
 * @property itemName The name of the item.
 * @property price The price of the item, represented as a Double.
 * @property uid A unique identifier for the item.
 * @property quantity The quantity of the item available.
 */
data class Item(
    val itemName: String = "",
    val price: Double = 0.0, // Changed to Double
    var uid: String = "",
    var quantity: Int = 0 // Quantity remains as Int
) {
    companion object {
        /**
         * Creates an Item object from a map of key-value pairs.
         * @param data A map containing item details.
         * @return An Item instance with mapped values.
         */
        fun fromMap(data: Map<String, Any?>): Item {
            return Item(
                itemName = data["itemName"] as? String ?: "",
                price = (data["price"] as? Number)?.toDouble() ?: 0.0, // Ensure Double type
                uid = data["uid"] as? String ?: "",
                quantity = (data["quantity"] as? Number)?.toInt() ?: 0
            )
        }
    }

    /**
     * Converts the Item instance into a map of key-value pairs.
     * @return A map representing the item details.
     */
    fun toMap(): Map<String, Any> {
        return mapOf(
            "itemName" to itemName,
            "price" to price, // Price as Double
            "uid" to uid,
            "quantity" to quantity
        )
    }
}
