package com.example.list7

data class Item(
    val itemName: String = "",
    val price: Int = 0,
    val uid: String = ""
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): Item {
            return Item(
                itemName = data["itemName"] as? String ?: "",
                price = (data["price"] as? Long)?.toInt() ?: 0,
                uid = data["uid"] as? String ?: ""
            )
        }
    }

    fun toMap(): Map<String, Any> {
        return mapOf(
            "itemName" to itemName,
            "price" to price,
            "uid" to uid
        )
    }
}
