package com.example.grocerystore.networking

import com.example.grocerystore.models.ProductDownload
import retrofit2.Call
import retrofit2.http.GET

interface GetPoducts {
    @GET("kkdad7ohirdgx")
    fun getProducts(): Call<ArrayList<ProductDownload>>
}