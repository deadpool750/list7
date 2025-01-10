package com.example.list7.firebase

data class User(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfBirth: String = ""
) {
    companion object {
        fun fromMap(data: Map<String, Any?>): User {
            return User(
                name = data["name"] as? String ?: "",
                surname = data["surname"] as? String ?: "",
                email = data["email"] as? String ?: "",
                phoneNumber = data["phoneNumber"] as? String ?: "",
                address = data["address"] as? String ?: "",
                dateOfBirth = data["dateOfBirth"] as? String ?: ""
            )
        }
    }
}
