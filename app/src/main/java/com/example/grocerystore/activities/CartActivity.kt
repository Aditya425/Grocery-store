package com.example.grocerystore.activities

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.grocerystore.R
import com.example.grocerystore.adapters.CartActivityAdapter
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Order
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_cart.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream

class CartActivity : BaseActivity() {
    private lateinit var prod_quantities: HashMap<String, String>
    private lateinit var adapter: CartActivityAdapter
    private var filled: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        prod_quantities = HashMap()
        setSupportActionBar(toolbar_cart_activity)
        toolbar_cart_activity.setNavigationIcon(R.drawable.ic_back_24)
        toolbar_cart_activity.setNavigationOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
            startActivity(intent)
            finish()
        }

        showDialog()
        Firebase().getCartCartActivity(FirebaseAuth.getInstance().currentUser!!.uid, this)

        btn_place_order.setOnClickListener {
            adapter.notifyDataSetChanged()
            showAlert()
        }
    }

    fun uploadCartSuccess(){
        hideDialog()
        if (Constants.cartProducts.isNotEmpty()) {
            rv_cart_activity.visibility = View.VISIBLE
            btn_place_order.visibility = View.VISIBLE
            tv_no_products.visibility = View.GONE
            setUpRecyclerView()
        }else{
            rv_cart_activity.visibility = View.GONE
            btn_place_order.visibility = View.GONE
            tv_no_products.visibility = View.VISIBLE
        }
    }

    private fun showAlert(){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Order")
        dialog.setCancelable(false)
        dialog.setMessage("Are you sure you want to order these items?")
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.setPositiveButton("YES"
        ) { _, _ -> placeOrder() }
        dialog.setNegativeButton("NO"
        ) { p0, _ -> p0!!.dismiss() }
        dialog.show()
    }

    private fun placeOrder() {
        if (filled) {
            val order = Order(Constants.cartProducts, FirebaseAuth.getInstance().currentUser!!.uid, prod_quantities)
            Firebase().uploadOrderCartActivity(order, FirebaseAuth.getInstance().currentUser!!.uid,this)
        }else{
            showErrorSnackBox("Enter quantity of items")
        }
    }

    private fun setUpRecyclerView(){
        rv_cart_activity.layoutManager = LinearLayoutManager(this)
        adapter = CartActivityAdapter(this, Constants.cartProducts)
        rv_cart_activity.adapter = adapter
        adapter.onAddClickListener(object : CartActivityAdapter.AddClickListener{
            override fun onAddClick(i: Double, name: String) {
                prod_quantities[name] = i.toString()
            }

        })

        adapter.onSubtractClickListener(object: CartActivityAdapter.SubtractClickListener{
            override fun onSubtractClick(j: Double, name: String) {
                prod_quantities[name] = j.toString()
            }
        })

        adapter.getText(object : CartActivityAdapter.TextViewListener{
            override fun isText(isFilled: Boolean) {
                filled = isFilled
                Log.i("isFilled", "${this@CartActivity.filled}")
            }

        })

        adapter.setOnLongClickListener(object : CartActivityAdapter.OnLongClickListener{
            override fun onLongClick(product: Product) {
                showDeleteAlert(product)
            }

        })
    }

    private fun showDeleteAlert(product: Product){
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Delete item")
        dialog.setIcon(android.R.drawable.ic_dialog_alert)
        dialog.setMessage("Are you sure you want to delete ${product.prod_name} from you cart?")
        dialog.setNegativeButton("NO"
        ) { p0, _ -> p0!!.dismiss() }
        dialog.setPositiveButton("YES") { _, _ ->
            showDialog()
            Constants.cartProducts.remove(product)
            val map: HashMap<String, ArrayList<Product>> = HashMap()
            map["prodInCart"] = Constants.cartProducts
            Constants.prodInCart.remove(product)
            serialize()
            Firebase().uploadCartCartActivity(
                FirebaseAuth.getInstance().currentUser!!.uid,
                map,
                this@CartActivity
            )
        }
        dialog.show()
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

    @Suppress("UNCHECKED_CAST")
    fun getCartSuccess(list: ArrayList<Product>?){
        hideDialog()
        if (list?.isNotEmpty() == true) {
            rv_cart_activity.visibility = View.VISIBLE
            btn_place_order.visibility = View.VISIBLE
            tv_no_products.visibility = View.GONE
            Constants.cartProducts = list
            setUpRecyclerView()
        }else{
            rv_cart_activity.visibility = View.GONE
            btn_place_order.visibility = View.GONE
            tv_no_products.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }

    fun uploadOrderSuccess(){
        Toast.makeText(this, "Order Placed Successfully", Toast.LENGTH_SHORT).show()
        val intent = Intent(this, PaymentActivity::class.java)
        startActivity(intent)
        finish()
    }
}