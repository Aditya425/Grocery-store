package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.example.grocerystore.R
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.Order
import com.example.grocerystore.models.Product
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import kotlinx.android.synthetic.main.activity_order_sucess.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class OrderSuccessActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_sucess)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

        iv_tick_mark.animate()!!.rotationBy(720f).setDuration(2000).startDelay = 500

        btn_order_more.isEnabled = false
        btn_order_more.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
            finish()
        }

        val map: HashMap<String, ArrayList<Product>> = HashMap()
        val list: ArrayList<Product> = ArrayList()
        map["prodInCart"] = list

        Constants.prodInCart.clear()
        serialize()

        showDialog()
        Firebase().deleteCartItemsOrderSuccess(FirebaseAuth.getInstance().currentUser!!.uid, this, map)
    }

    private fun serialize(){
        val folder = File(Environment.getExternalStorageDirectory().absolutePath+"/prodInCartMap")
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

    fun deleteSuccess(){
        hideDialog()
        btn_order_more.isEnabled = true
//        val order = Order()
//        Firebase().deleteOrderOrderSuccess(FirebaseAuth.getInstance().currentUser!!.uid, order, this)
    }

    fun deleteOrderSuccess(){
        hideDialog()

    }
}