package com.example.list7

data class Item(
    val itemName: String = "",
    val price: Double = 0.0, // Changed to Double
    var uid: String = "",
    var quantity: Int = 0 // Quantity remains as Int
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): Item {
            return Item(
                itemName = data["itemName"] as? String ?: "",
                price = (data["price"] as? Number)?.toDouble() ?: 0.0, // Ensure Double type
                uid = data["uid"] as? String ?: "",
                quantity = (data["quantity"] as? Number)?.toInt() ?: 0
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "itemName" to itemName,
            "price" to price, // Price as Double
            "uid" to uid,
            "quantity" to quantity
        )
    }
}
