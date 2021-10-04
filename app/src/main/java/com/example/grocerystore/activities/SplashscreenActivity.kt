package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import com.example.grocerystore.R
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.User
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class SplashscreenActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splashscreen)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        supportActionBar?.hide()

        Handler().postDelayed({
            if (FirebaseAuth.getInstance().currentUser == null) {
                startActivity(Intent(this@SplashscreenActivity, IntroActivity::class.java))
                finish()
            }else{
                Firebase().getUserSplashscreen(this, FirebaseAuth.getInstance().currentUser!!.uid)
            }
        }, 1500)

    }

    fun getUserSuccess(user: User){
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(Constants.EMAIL, user.email)
        startActivity(intent)
        finish()
    }
}