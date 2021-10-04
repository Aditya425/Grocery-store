package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystore.R
import com.example.grocerystore.adapters.CategorySearchResultsActivityAdapter
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_category_search_results.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class CategorySearchResultsActivity : BaseActivity() {
    var products: ArrayList<Product> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_search_results)

        setSupportActionBar(toolbar_category_search_results)

        toolbar_category_search_results.setNavigationIcon(R.drawable.ic_back_24)
        supportActionBar!!.title = "Found products"
        toolbar_category_search_results.setNavigationOnClickListener {
            onBackPressed()
        }

        if (intent.hasExtra(Constants.PRODUCTS_LIST)){
            products = intent.getSerializableExtra(Constants.PRODUCTS_LIST) as ArrayList<Product>
            setUpRecyclerView()
        }

        //Constants.prodInCart
    }

    private fun setUpRecyclerView(){
        rv_category_search_results.layoutManager = LinearLayoutManager(this)
        val adapter = CategorySearchResultsActivityAdapter(this, products)
        rv_category_search_results.adapter = adapter
        rv_category_search_results.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))

        adapter.setOnClickListener(object : CategorySearchResultsActivityAdapter.OnClickListener{
            override fun onClick(product: Product) {
                if (!Constants.prodInCart.contains(product)) {
                    showDialog()
                    Constants.prodInCart.add(product)
                    serialize()
                    val map: HashMap<String, ArrayList<Product>> = HashMap()
                    map["prodInCart"] = Constants.prodInCart
                    Firebase().addToCartCategorySearchResults(FirebaseAuth.getInstance().currentUser!!.uid, map, this@CategorySearchResultsActivity)
                }
            }

        })
    }

//    private fun removeDuplicates(){
//        val tempList: ArrayList<Product> = ArrayList()
//        for (i in Constants.prodInCart.indices){
//            if (!tempList.contains(Constants.prodInCart[i])){
//                tempList.add(Constants.prodInCart[i])
//            }
//        }
//        Constants.prodInCart =
//    }

    fun addToCartSuccess(){
        hideDialog()
    }

    private fun serialize(){
        val folder = File(Environment.getExternalStorageDirectory(), "prodInCartMap")
        folder.mkdir()
        val file = File(folder, "prodInCartMap")
        file.createNewFile()
        val fos = FileOutputStream(file)
        val oos = ObjectOutputStream(fos)
        oos.writeObject(Constants.prodInCart)
        oos.flush()
        oos.close()
        fos.close()
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }
}