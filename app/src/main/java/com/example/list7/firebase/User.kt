package com.example.list7.firebase

data class User(
    val id: String = "",
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: Map<String, String> = mapOf(),
){
    companion object{
        fun fromMap(map: Map<String, Any?>): User{
            return User(
                id = map["id"] as? String ?: "",
                name = map["name"] as? String ?: "",
                surname = map["surname"] as? String ?: "",
                email = map["email"] as? String ?: "",
                phoneNumber = map["phoneNumber"] as? String ?: "",
                address = map["address"] as? Map<String, String> ?: mapOf()
            )
        }
    }
}
