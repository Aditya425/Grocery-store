package com.example.grocerystore.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductDownload(
    var Timestamp: String = "",
    @SerializedName("Product name")
    var Product_name: String = "",
    @SerializedName("Product image(give the url of image)")
    var Product_image: String = "",
    @SerializedName("Product description")
    var Product_description: String = "",
    @SerializedName("Product price")
    var Product_price: String = "",
    @SerializedName("Product tags (separate them with commas)")
    var Product_tags: String = "",
    @SerializedName("Kg or pack (answer in true or false)")
    var kgOrPack: String = "",
    @SerializedName("Product categories")
    var Product_categories: String = ""
): Serializable {
}