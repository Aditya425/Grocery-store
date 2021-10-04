package com.example.grocerystore.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.grocerystore.R
import com.example.grocerystore.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_sign_in.*
import kotlinx.android.synthetic.main.activity_sign_up.*

class SignInActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        btn_sign_in_signin_activity.setOnClickListener {
            if (validate()) {
                signInUser()
            }
        }

    }

    private fun validate(): Boolean{
        return et_email_sign_in_activity.text.toString().isNotEmpty() &&
                et_password_sign_in_activity.text.toString().isNotEmpty()
    }

    private fun signInUser(){
        showDialog()
        FirebaseAuth.getInstance().signInWithEmailAndPassword(et_email_sign_in_activity.text.toString(), et_password_sign_in_activity.text.toString())
                .addOnSuccessListener {
                    hideDialog()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra(Constants.EMAIL, et_email_sign_in_activity.text.toString())
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener {
                    hideDialog()
                    showErrorSnackBox(it.message)
                }
    }

    override fun onBackPressed() {
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}