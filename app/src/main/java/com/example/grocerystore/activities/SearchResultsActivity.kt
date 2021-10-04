package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystore.R
import com.example.grocerystore.adapters.SearchResultsActivityAdapter
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_search_results.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class SearchResultsActivity : BaseActivity() {
    private lateinit var products: ArrayList<Product>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_results)

        products = ArrayList()

        setSupportActionBar(toolbar_search_results_activity)
        toolbar_search_results_activity.setNavigationIcon(R.drawable.ic_back_24)
        supportActionBar?.title = "Found items"
        toolbar_search_results_activity.setNavigationOnClickListener {
            val intent = Intent(this, SearchActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }
        Constants.searchItems.clear()
        if (intent.hasExtra(Constants.PRODUCTS_LIST)){
            products = intent.getSerializableExtra(Constants.PRODUCTS_LIST) as ArrayList<Product>
            Log.i("products", products.toString())
        }

        setUpRecyclerView()
    }

    private fun setUpRecyclerView(){
        rv_search_results_activity.layoutManager = LinearLayoutManager(this)
        val adapter = SearchResultsActivityAdapter(this, products)
        rv_search_results_activity.adapter = adapter
        adapter.onLongClickListener(object : SearchResultsActivityAdapter.OnLongClickListener{
            override fun onLongClick(product: Product) {
                if (!Constants.prodInCart.contains(product)) {
                    showDialog()
                    Constants.prodInCart.add(product)
                    serialize()
                    val map: HashMap<String, ArrayList<Product>> = HashMap()
                    map["prodInCart"] = Constants.prodInCart
                    Firebase().addToCartSearchActivityResults(
                        FirebaseAuth.getInstance().currentUser!!.uid,
                        map,
                        this@SearchResultsActivity
                    )
                }
            }

        })
    }

    fun uploadCartSuccess(){
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
        val intent = Intent(this, SearchActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}