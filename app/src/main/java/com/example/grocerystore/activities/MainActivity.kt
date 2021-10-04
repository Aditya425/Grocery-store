package com.example.grocerystore.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.grocerystore.R
import com.example.grocerystore.adapters.RecyclerViewMainActivityAdapterOne
import com.example.grocerystore.adapters.RecyclerViewMainActivityAdapterTwo
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Product
import com.example.grocerystore.models.ProductDownload
import com.example.grocerystore.models.User
import com.example.grocerystore.networking.GetPoducts
import com.example.grocerystore.utils.Constants
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.profile_view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*

class MainActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {
    private var userEmail: String? = ""
    private lateinit var cartMenuItem: MenuItem
    private lateinit var currentUser: User
    private lateinit var prodInCartMap: HashMap<String, ArrayList<Product>>
    private lateinit var folder: File
    private lateinit var file: File

    @SuppressWarnings("deprecation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main_activity)
        getProducts()
        if (intent.hasExtra(Constants.EMAIL)){
            userEmail = intent.getStringExtra(Constants.EMAIL)
        }
        getPermissions()
        prodInCartMap = HashMap()

        deserializeProdInCart()

        products()
        Firebase().getUserMainActivity(this, FirebaseAuth.getInstance().currentUser!!.uid)
        toolbar_main_activity.setNavigationIcon(R.drawable.ic_hamburger_24)
        toolbar_main_activity.setNavigationOnClickListener {
            openDrawer()
        }

        nav_view.setNavigationItemSelectedListener(this)
        et_search_bar_main_activity.isFocusable = false
        et_search_bar_main_activity.setOnClickListener{
            val intent = Intent(this, SearchActivity::class.java)
            startActivity(intent)
            finish()
        }

        setUpRecyclerViewOne()
        setUpRecyclerViewTwo()
    }

    private fun getProducts(){
        val retrofit = Retrofit.Builder().baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val getPoducts: GetPoducts = retrofit.create(GetPoducts::class.java)
        getPoducts.getProducts().enqueue(object : Callback<ArrayList<ProductDownload>>{
            override fun onResponse(
                call: Call<ArrayList<ProductDownload>>,
                response: Response<ArrayList<ProductDownload>>
            ) {
                if (response.isSuccessful){
                    val productDownloads: ArrayList<ProductDownload> = response.body()!!
                    for (i in productDownloads.indices){
                        val tags: ArrayList<String> = ArrayList(productDownloads[i].Product_tags.split(","))
                        val product = Product(productDownloads[i].Product_name, -1, productDownloads[i].Product_description, productDownloads[i].Product_price.toInt()
                        , tags, productDownloads[i].kgOrPack.toBoolean(), productDownloads[i].Product_image)
                        if (!Constants.products.contains(product)){
                            Constants.products.add(product)
                        }
                        Log.i("productsMainActivity", Constants.products[Constants.products.size-1].toString())
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<ProductDownload>>, t: Throwable) {
                showErrorSnackBox(t.message)
            }

        })
    }

    private fun getPermissions(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE), STORAGE_REQUEST_CODE)
        }else{
            folder = File(Environment.getExternalStorageDirectory(), "prodInCartMap")
            folder.mkdir()
            file = File(folder, "prodInCartMap")
            file.createNewFile()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            folder = File(Environment.getExternalStorageDirectory(), "prodInCartMap")
            folder.mkdir()
            file = File(folder, "prodInCartMap")
            file.createNewFile()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun deserializeProdInCart(){
        try {
            val fis = FileInputStream(file)
            val ois = ObjectInputStream(fis)
            Constants.prodInCart = ois.readObject() as ArrayList<Product>
            ois.close()
            fis.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    private fun setUpRecyclerViewTwo(){
        val items: ArrayList<Product> = setUpProduct()
        rv_main_activity_two.layoutManager = LinearLayoutManager(this)
        val adapter = RecyclerViewMainActivityAdapterTwo(this, items)
        rv_main_activity_two.adapter = adapter
        adapter.setOnLongClickListener(object : RecyclerViewMainActivityAdapterTwo.OnLongClickListener{
            override fun onLongClick(product: Product) {
                Constants.prodInCart.add(product)
                showDialog()
                prodInCartMap["prodInCart"] = deleteDuplicates(Constants.prodInCart)
                serializeProdInCartMap(Constants.prodInCart)
                Firebase().addToCartMainActivity(prodInCartMap, this@MainActivity, currentUser)
            }
        })
    }

    private fun serializeProdInCartMap(list: ArrayList<Product>){
        try {
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

    fun addToCartSuccessful(){
        hideDialog()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_cart, menu)
        cartMenuItem = menu!!.getItem(0)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_cart){
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setUpProduct(): ArrayList<Product>{
        val items: ArrayList<Product> = ArrayList()
        val bujia_sev_tags: ArrayList<String> = ArrayList()
        bujia_sev_tags.add("khara")
        bujia_sev_tags.add("sev")
        bujia_sev_tags.add("haldiram's")
        bujia_sev_tags.add("haldirams")
        val corn_flakes_tags: ArrayList<String> = ArrayList()
        corn_flakes_tags.add("kellogs")
        corn_flakes_tags.add("breakfast")
        val kissan_jam_tags: ArrayList<String> = ArrayList()
        kissan_jam_tags.add("kissan")
        kissan_jam_tags.add("jam")
        kissan_jam_tags.add("mixed fruit jam")
        val tide_tags: ArrayList<String> = ArrayList()
        tide_tags.add("detergent")
        val toor_dal_tags: ArrayList<String> = ArrayList()
        toor_dal_tags.add("dal")
        val bujia_sev = Product("Bujia Sev", R.drawable.bujia_sev, "Haldiram's Bhujia Sev is an authentic rendition of the classic, textured namkeen which is deliciously crispy besan sticks, flavoured with a blast of red chilli", 40, bujia_sev_tags, true)
        val corn_flakes = Product("Corn Flakes", R.drawable.corn_flakes, "Kellogg's Corn Flakes is a nourishing and tasty ready-to-eat breakfast cereal which is High in Iron, Vitamin C and key essential B group Vitamins such as B1, B2", 190, corn_flakes_tags, true)
        val kissan_jam = Product("Kissan Jam", R.drawable.kissan_jam, "Kissan Mixed Fruit Jam is a delicious blend of 8 different fruits Pineapple, Orange, Apple, Grape, Mango, Pear, Papaya, and Banana. With Kissan's expertise", 75, kissan_jam_tags, true)
        val tide = Product("Tide", R.drawable.tide, "Tide is an American brand of laundry detergent manufactured and marketed by Procter & Gamble. Introduced in 1946,", 150, tide_tags, true)
        val toor_dal = Product("Toor Dal", R.drawable.toor_dal, "A staple in the Indian diet, toor dal is used to prepare sambar, dal tadka, the maharashtrian varan and more", 50, toor_dal_tags, false)
        items.add(bujia_sev)
        items.add(corn_flakes)
        items.add(kissan_jam)
        items.add(toor_dal)
        items.add(tide)
        for (i in items.indices){
            if (!Constants.products.contains(items[i])){
                Constants.products.add(items[i])
            }
        }
        return items
    }

    private fun products(){
        val items: ArrayList<Product> = ArrayList()
        val lays_tags: ArrayList<String> = ArrayList()
        lays_tags.add("chips")
        val lays = Product("lays", R.drawable.chips, "Wherever celebrations and good times happen, LAY'S® potato chips will be there just as they have been for more than 75 year", 10, lays_tags, true)
        items.add(lays)
        val parle_g_tags: ArrayList<String> = ArrayList()
        parle_g_tags.add("biscuits")
        parle_g_tags.add("parle g")
        parle_g_tags.add("parle")
        val parle_g = Product("parle g", R.drawable.parle_g, "Filled with the goodness of milk and wheat, Parle-G has been a source of all round nourishment for the nation since 1939", 10, parle_g_tags, true)
        items.add(parle_g)
        val marie_tags: ArrayList<String> = ArrayList()
        marie_tags.add("biscuits")
        marie_tags.add("marie")
        marie_tags.add("marie gold")
        marie_tags.add("marie lite")
        val marie = Product("marie", R.drawable.marie, "The best biscuits for you to snack on", 20, marie_tags)
        items.add(marie)
        val kurkure_tags: ArrayList<String> = ArrayList()
        kurkure_tags.add("chips")
        val kurkure = Product("kurkure", R.drawable.kurkure, "Kurkure is a crunchy new-age namkeen snack brand which symbolizes light hearted fun. Made with trusted kitchen ingredients.", 10, kurkure_tags,true)
        items.add(kurkure)
        val good_day_tags: ArrayList<String> = ArrayList()
        good_day_tags.add("biscuits")
        good_day_tags.add("brittania")
        val good_day = Product("good day", R.drawable.good_day, "Britannia Good Day Cashew Biscuit. With the abundance of nuts on the surface and a great new taste", 20, good_day_tags, true)
        items.add(good_day)
        for (i in items.indices){
            if (!Constants.products.contains(items[i])){
                Constants.products.add(items[i])
            }
        }
    }

    private fun setUpRecyclerViewOne(){
        val data: ArrayList<Int> = ArrayList()
        data.add(R.drawable.beans)
        data.add(R.drawable.brinjal)
        data.add(R.drawable.carrots)
        data.add(R.drawable.chips)
        data.add(R.drawable.milk_carton)
        rv_main_activity_one.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val adapter = RecyclerViewMainActivityAdapterOne(this, data)
        rv_main_activity_one.adapter = adapter
    }

    private fun openDrawer(){
        if (!drawer_layout.isDrawerOpen(nav_view)){
            drawer_layout.openDrawer(nav_view)
        }
    }

    fun getUserSuccess(user: User){
        currentUser = user
        supportActionBar?.title = "Welcome ${user.name}"
        Glide.with(this)
                .load(user.image)
                .into(iv_profile_nav_bar)
        tv_profile_nav_bar.text = user.name
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.your_profile){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra(Constants.EMAIL, userEmail)
            startActivity(intent)
            finish()
            return true
        }else if (item.itemId == R.id.sign_out){
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, IntroActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            return true
        }
        else if (item.itemId == R.id.categories){
            val intent = Intent(this, CategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
        return true
    }

    companion object{
        const val STORAGE_REQUEST_CODE = 1
    }
}