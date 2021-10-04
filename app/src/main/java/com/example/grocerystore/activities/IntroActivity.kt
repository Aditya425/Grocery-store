package com.example.grocerystore.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.example.grocerystore.R
import kotlinx.android.synthetic.main.activity_intro.*

class IntroActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        supportActionBar?.hide()
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        btn_sign_in.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
        btn_sign_up.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        backButton()
    }
}