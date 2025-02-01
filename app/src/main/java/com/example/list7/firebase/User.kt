package com.example.list7.firebase

/**
 * Data class representing a user with personal details.
 * @property name The first name of the user.
 * @property surname The last name of the user.
 * @property email The email address of the user.
 * @property phoneNumber The phone number of the user.
 * @property address The residential address of the user.
 * @property dateOfBirth The date of birth of the user.
 */
data class User(
    val name: String = "",
    val surname: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val dateOfBirth: String = ""
) {
    companion object {
        /**
         * Creates a User object from a map of key-value pairs.
         * @param data A map containing user details.
         * @return A User instance with mapped values.
         */
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
