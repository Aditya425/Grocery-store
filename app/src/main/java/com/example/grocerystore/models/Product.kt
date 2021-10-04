package com.example.grocerystore.models

import java.io.Serializable

data class Product(
        var prod_name: String = "",
        var prod_image: Int = -1,
        var prod_desc: String = "",
        var prod_price: Int = -1,
        var prod_tags: ArrayList<String> = ArrayList(),
        var kgOrPack: Boolean = true,//true: pack, false: kg
        var prod_image_url: String = ""
): Serializable