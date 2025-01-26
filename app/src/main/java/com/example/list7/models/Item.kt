package com.example.list7

data class Item(
    val itemName: String = "",
    val price: Int = 0,
    var uid: String = "",
    var quantity: Int = 0 // Added quantity field
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): Item {
            return Item(
                itemName = data["itemName"] as? String ?: "",
                price = (data["price"] as? Long)?.toInt() ?: 0,
                uid = data["uid"] as? String ?: "",
                quantity = (data["quantity"] as? Long)?.toInt() ?: 0 // Map quantity field
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "itemName" to itemName,
            "price" to price,
            "uid" to uid,
            "quantity" to quantity // Include quantity in map
        )
    }
}
