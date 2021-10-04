package com.example.grocerystore.models

import java.io.Serializable

data class ProductDocument(
        var prodInCart: ArrayList<Product> = ArrayList()
): Serializable {

}