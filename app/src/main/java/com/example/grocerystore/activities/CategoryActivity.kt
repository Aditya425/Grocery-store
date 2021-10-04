package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.*
import com.example.grocerystore.R
import com.example.grocerystore.adapters.CategoriesAdapter
import com.example.grocerystore.models.Product
import com.example.grocerystore.models.ProductDownload
import com.example.grocerystore.networking.GetPoducts
import com.example.grocerystore.utils.Constants
import kotlinx.android.synthetic.main.activity_category.*
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class CategoryActivity : BaseActivity() {
    private val productDownload: ArrayList<ProductDownload> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)
        setSupportActionBar(toolbar_category_activity)
        supportActionBar?.title = "Product categories"
        toolbar_category_activity.setNavigationIcon(R.drawable.ic_back_24)
        toolbar_category_activity.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val getPoducts: GetPoducts = retrofit.create(GetPoducts::class.java)
        showDialog()
        getPoducts.getProducts().enqueue(object : Callback<ArrayList<ProductDownload>>{
            override fun onResponse(
                call: Call<ArrayList<ProductDownload>>,
                response: Response<ArrayList<ProductDownload>>
            ) {
                for (i in response.body()!!.indices){
                    hideDialog()
                    val productDownload = response.body()!![i]
                    this@CategoryActivity.productDownload.add(productDownload)
                }
                setUpRecyclerView()
            }

            override fun onFailure(call: Call<ArrayList<ProductDownload>>, t: Throwable) {
                hideDialog()
                showErrorSnackBox(t.message)
            }

        })
    }

    private fun setUpRecyclerView(){
        rv_category.layoutManager = LinearLayoutManager(this)
        val adapter = CategoriesAdapter(this, getCategories())
        rv_category.adapter = adapter
        rv_category.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        adapter.setOnClickListener(object : CategoriesAdapter.OnClickListener{
            override fun onClick(category: String) {
                search(category)
            }

        })
    }

    private fun search(category: String){
        val products: ArrayList<Product> = ArrayList()
        var isFound = false
        for (i in productDownload.indices){
            if (productDownload[i].Product_categories == category){
                isFound=true
                val tagsList: ArrayList<String> = ArrayList(productDownload[i].Product_tags.split(","))
                val product = Product(productDownload[i].Product_name, -1, productDownload[i].Product_description, productDownload[i].Product_price.toInt()
                , tagsList, productDownload[i].kgOrPack.toBoolean(), productDownload[i].Product_image)
                products.add(product)
            }
        }

        if (isFound){
            val intent = Intent(this, CategorySearchResultsActivity::class.java)
            intent.putExtra(Constants.PRODUCTS_LIST, products)
            startActivity(intent)
        }
    }

    private fun getCategories(): ArrayList<String>{
        val list: ArrayList<String> = ArrayList()
        list.add("Vegetable")
        list.add("Fruits")
        list.add("Personal care")
        list.add("Household items")
        list.add("Kitchen and dining needs")
        list.add("Biscuits")
        list.add("Snacks")
        list.add("Chocolates")
        list.add("Beverages")
        list.add("Breakfast")
        list.add("Dairy")
        list.add("Eggs and meat")
        return list
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}