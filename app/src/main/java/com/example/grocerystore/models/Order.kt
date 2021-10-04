package com.example.grocerystore.models

data class Order(
    var products: ArrayList<Product> = ArrayList(),
    var orderedBy: String = "",
    var prod_quatities: HashMap<String, String> = HashMap()
) {
}