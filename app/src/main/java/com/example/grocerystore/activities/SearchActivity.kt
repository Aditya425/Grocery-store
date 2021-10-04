
package com.example.grocerystore.activities

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystore.R
import com.example.grocerystore.adapters.FrequentItemsAdapter
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_search.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class SearchActivity : BaseActivity() {
    private lateinit var searchedItems: ArrayList<Product>
    private var isFound: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        setSupportActionBar(toolbar_search_activity)
        toolbar_search_activity.setNavigationIcon(R.drawable.ic_back_24)
        toolbar_search_activity.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            actv_search_activity.focusable = View.FOCUSABLE
        }

        setUpRecyclerView()

        val searchItems: ArrayList<String> = searchItems()

        Constants.searchItems.addAll(searchItems)
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, searchItems)
        actv_search_activity.setAdapter(adapter)

        actv_search_activity.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                val grocery = actv_search_activity.text.toString().toLowerCase(Locale.ROOT)
                searchedItems = ArrayList()
                searchNamesOnly(grocery)
                if (Constants.searchItems.contains(grocery) || isFound){
                    for (i in Constants.products.indices){
                        if (Constants.products[i].prod_name.toLowerCase(Locale.ROOT) == grocery){
                            searchedItems.add(Constants.products[i])
                        }else if (Constants.products[i].prod_tags.contains(grocery)){
                            searchedItems.add(Constants.products[i])
                        }
                    }

                    if (searchedItems.size > 0){
                        Log.i("searchItems", "$searchedItems")
                        val intent = Intent(this@SearchActivity, SearchResultsActivity::class.java)
                        intent.putExtra(Constants.PRODUCTS_LIST, searchedItems)
                        startActivity(intent)
                        finish()
                    }
                }else{
                    Toast.makeText(this, "Nothing found!", Toast.LENGTH_LONG).show()
                }
            }
            true
        }
    }

    private fun searchNamesOnly(name: String){
        for (i in Constants.products.indices){
            if (name == Constants.products[i].prod_name.toLowerCase(Locale.ROOT) || Constants.products[i].prod_tags.contains(name)){
                isFound = true
            }
        }
    }

    private fun searchItems(): ArrayList<String>{
        val searchItems: ArrayList<String> = ArrayList()
        searchItems.add("chips")
        searchItems.add("cake")
        searchItems.add("noodles")
        searchItems.add("maggi")
        searchItems.add("brittania")
        searchItems.add("parle g")
        searchItems.add("marie")
        searchItems.add("good day")
        searchItems.add("biscuits")
        searchItems.add("lays")
        searchItems.add("kurkure")
        searchItems.add("detergent")
        searchItems.add("parle g")
        searchItems.add("choco pie")
        searchItems.add("lotte choco pie")
        searchItems.add("dal")
        searchItems.add("marie gold")
        searchItems.add("marie lite")
        searchItems.add("fruit")
        searchItems.add("apple")
        return searchItems
    }

    private fun setUpRecyclerView(){
        val items: ArrayList<Product> = getItems()
        rv_search_activity.layoutManager = LinearLayoutManager(this)
        val adapter = FrequentItemsAdapter(this, items)
        rv_search_activity.adapter = adapter

        adapter.setOnLongClickListener(object : FrequentItemsAdapter.OnLongClickListener{
            override fun onLongClick(product: Product) {
                showDialog()
                Constants.prodInCart.add(product)
                val prodInCartMap: HashMap<String, ArrayList<Product>> = HashMap()
                prodInCartMap["prodInCart"] = deleteDuplicates(Constants.prodInCart)
                serializeProdInCartMap(Constants.prodInCart)
                Firebase().addToCartSearchActivity(FirebaseAuth.getInstance().currentUser!!.uid, prodInCartMap, this@SearchActivity)
            }

        })
    }

    private fun serializeProdInCartMap(list: ArrayList<Product>){
        try {
            val folder = File(Environment.getExternalStorageDirectory(), "prodInCartMap")
            folder.mkdir()
            val file = File(folder, "prodInCartMap")
            file.createNewFile()
            val fos = FileOutputStream(file)
            val oos = ObjectOutputStream(fos)
            oos.writeObject(list)
            oos.close()
            fos.flush()
            fos.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun deleteDuplicates(products: ArrayList<Product>): ArrayList<Product>{
        val list: ArrayList<Product> = ArrayList()
        for (i in products.indices){
            if (!list.contains(products[i])){
                list.add(products[i])
            }
        }
        return list
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    fun addToCartSuccess(){
        hideDialog()
    }

    private fun getItems(): ArrayList<Product>{
        val items: ArrayList<Product> = ArrayList()
        val brittania_cake_value_pack_tags: ArrayList<String> = ArrayList()
        brittania_cake_value_pack_tags.add("brittania")
        brittania_cake_value_pack_tags.add("cake")
        val choclate_milkshake_tags: ArrayList<String> = ArrayList()
        choclate_milkshake_tags.add("milkshake")
        choclate_milkshake_tags.add("cavin's")
        choclate_milkshake_tags.add("cavins")
        val choco_pie_tags: ArrayList<String> = ArrayList()
        choco_pie_tags.add("lotte")
        choco_pie_tags.add("lotte choco pie")
        choco_pie_tags.add("choco pie")
        val maggi_noodles_tags: ArrayList<String> = ArrayList()
        maggi_noodles_tags.add("maggi")
        maggi_noodles_tags.add("noodles")
        maggi_noodles_tags.add("nestle")
        val brittania_cake_value_pack = Product("brittania cake value pack", R.drawable.brittania_cake_value_pack, "Soft and delicious cake slices with the goodness of milk fruit and eggs. A chocolaty delight that you won’t forget in a hurry.", 25, brittania_cake_value_pack_tags, true)
        val choclate_milkshake = Product("Choclate milkshake", R.drawable.choclate_milkshake, "CavinKares milkshakes bring a nutrient-packed flavourful option for parents who are challenged with this every day", 45, choclate_milkshake_tags, true)
        val choco_pie = Product("Lotte choco pie", R.drawable.choco_pie, "Lotte choco pie is a two-layered cake with marshmallow filling, dipped in chocolate. Here the overall taste comprises of cake, choco dip & marshmallow layer", 10, choco_pie_tags, true)
        val maggi_noodles = Product("Maggi Noodles", R.drawable.maggi_noodles, "Explore a range of MAGGI® Noodles like Masala Noodles, Atta Noodles, Chicken noodles, Oats Masala and cook delicious meals for family. 100% Safe", 20, maggi_noodles_tags, true)
        items.add(brittania_cake_value_pack)
        items.add(choclate_milkshake)
        items.add(maggi_noodles)
        items.add(choco_pie)
        for (i in items.indices){
            if (!Constants.products.contains(items[i])){
                Constants.products.add(items[i])
            }
        }
        return items
    }
}