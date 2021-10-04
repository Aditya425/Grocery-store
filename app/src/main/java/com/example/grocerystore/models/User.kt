package com.example.grocerystore.models

import java.io.Serializable

data class User(
        var email: String = "",
        var name: String = "",
        var mobile: String = "",
        var image: String = "",
        var id: String = "",
        var userAddress: String = "",
        var fcmToken: String = ""

): Serializable {
}