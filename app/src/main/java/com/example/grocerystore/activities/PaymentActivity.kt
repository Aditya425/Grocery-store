package com.example.grocerystore.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.example.grocerystore.R
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.OrderDocument
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_payment.*

class PaymentActivity : BaseActivity() {
    private var products: ArrayList<Product> = ArrayList()
    private val tempMap: HashMap<String, String> = HashMap()
    private var totalAmount: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar!!.hide()

        showDialog()
        Firebase().getCartPaymentActivity(FirebaseAuth.getInstance().currentUser!!.uid, this)

        btn_pay_upi.setOnClickListener {
            payByUpi()
        }
    }

    private fun payByUpi(){
        val uri: Uri = Uri.parse("upi://pay").buildUpon()
            .appendQueryParameter("pa", Constants.UPI_ID)
            .appendQueryParameter("pn", "Aditya Dixit")
            .appendQueryParameter("am", totalAmount.toString())
            .appendQueryParameter("mc", "")
            .appendQueryParameter("tr", "")
            .appendQueryParameter("cu", "INR")
            .appendQueryParameter("tn", Constants.TN)
            .build()

        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = uri
        try {
            startActivityForResult(Intent.createChooser(intent, "Pay with"), 1)
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

    fun getCartSuccess(products: ArrayList<Product>){
        this.products = products
        Firebase().getOrdersPaymentActivity(FirebaseAuth.getInstance().currentUser!!.uid, this)
    }

    fun getOrderSuccess(orderDocument: OrderDocument){
        hideDialog()
        for ((key, value) in orderDocument.prod_quatities){
            for (i in products.indices){
                if (key == products[i].prod_name) {
                    tempMap[key] = products[i].prod_price.toString() + " * " + value
                }
            }
        }

        for ((key, value) in tempMap){
            tv_product_details.append(
                "$key : $value \n\n"
            )
            val list = value.split("*")
            totalAmount += list[0].toDouble() * list[1].toDouble()
        }

        tv_total_amount.text = totalAmount.toString()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val intent = Intent(this, OrderSuccessActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, CartActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}
