package com.example.grocerystore.utils

import com.example.grocerystore.models.Product

object Constants {
    const val USER_COLLECTION: String = "Users"
    const val EMAIL: String = "email"
    val products: ArrayList<Product> = ArrayList()
    val searchItems: ArrayList<String> = ArrayList()
    const val PRODUCTS_LIST: String = "products_list"
    const val CART_COLLECTION: String = "Cart"
    var prodInCart: ArrayList<Product> = ArrayList()
    const val ORDER_COLLECTION = "Order"
    var cartProducts: ArrayList<Product> = ArrayList()

    const val UPI_ID = "adityadixit425@okhdfcbank"
    const val TN = "Ordered from grocery store app"
    const val BASE_URL = "https://sheetdb.io/api/v1/"
}