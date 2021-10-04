package com.example.grocerystore.models

data class OrderDocument(
    val orderedBy: String = "",
    val prod_quatities: HashMap<String, String> = HashMap(),
    val products: ArrayList<Product> = ArrayList()
) {
}