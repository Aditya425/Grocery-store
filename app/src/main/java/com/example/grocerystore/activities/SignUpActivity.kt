package com.example.grocerystore.activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.util.lruCache
import com.example.grocerystore.R
import com.example.grocerystore.firestore.Firebase
import com.example.grocerystore.models.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_sign_up.*
import java.util.concurrent.TimeUnit

class SignUpActivity : BaseActivity(){
    private var isAuthenticateDisplayed: Boolean = true
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private var mOtpCode = ""
    private val LOCATION_CODE = 1

    private lateinit var location: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        btn_sign_up_sign_up_activity.setOnClickListener {
            authenticateMobile()
        }

        tv_address.keyListener = null

        tv_address.setOnClickListener {
            location()
            getAddress()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getAddress(){
        location = FusedLocationProviderClient(this)
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        location.requestLocationUpdates(locationRequest, object : LocationCallback(){
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                val latitude = result.lastLocation.latitude
                val longitude = result.lastLocation.longitude

                val geocoder = Geocoder(this@SignUpActivity)
                val addresses: List<Address> = geocoder.getFromLocation(latitude, longitude, 1)

                tv_address.setText(addresses[0].getAddressLine(0).toString())

                location.removeLocationUpdates(this)
            }
        }, Looper.getMainLooper())

    }

    private fun location(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_CODE)
        }else{
            getAddress()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
            getAddress()
        }else{
            location()
        }
    }

    private fun authenticateMobile(){
        if (isAuthenticateDisplayed) {
            if (validate()) {
                showDialog()
                val phone = "+91" + et_mobile_sign_up_activity.text.toString()
                PhoneAuthProvider.getInstance().verifyPhoneNumber(phone, 60L, TimeUnit.SECONDS, this, object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                    }

                    override fun onVerificationFailed(p0: FirebaseException) {
                        val dialog = AlertDialog.Builder(this@SignUpActivity)
                        dialog.setMessage(p0.toString())
                        dialog.show()
                    }

                    @SuppressLint("SetTextI18n")
                    override fun onCodeSent(code: String, p1: PhoneAuthProvider.ForceResendingToken) {
                        super.onCodeSent(code, p1)
                        et_password_layout_sign_up_activity.visibility = View.VISIBLE
                        btn_sign_up_sign_up_activity.text = "SIGN IN"
                        et_otp_code.visibility = View.VISIBLE
                        isAuthenticateDisplayed = false
                        mOtpCode = code
                        hideDialog()
                    }
                })
            }
        }else{
            if (validate() && et_password_sign_up_activity.text.toString().isNotEmpty() && et_otp_code.text.toString().isNotEmpty()) {
                showDialog()
                val userCode = et_otp_code.text.toString()
                val credential = PhoneAuthProvider.getCredential(mOtpCode, userCode)
                signUpUser(credential)
            }else{
                showErrorSnackBox("Please enter all information")
            }
        }
    }

    private fun signUpUser(credential: PhoneAuthCredential){
        mAuth.signInWithCredential(credential)
                .addOnSuccessListener {
                    hideDialog()
                    showDialog()
                    createUser()
                }
                .addOnFailureListener {
                    hideDialog()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(it.toString())
                    dialog.show()
                }
    }

    private fun validate(): Boolean{
        return et_name_sign_up_activity.text.toString().isNotEmpty() &&
                et_email_sign_up_activity.text.toString().isNotEmpty() &&
                et_mobile_sign_up_activity.text.toString().isNotEmpty()&&
                et_mobile_sign_up_activity.text?.length!! == 10 &&
                tv_address.text.toString().isNotEmpty()
    }

    private fun createUser(){
        mAuth.createUserWithEmailAndPassword(et_email_sign_up_activity.text.toString(), et_password_sign_up_activity.text.toString())
                .addOnSuccessListener {
                    getToken()
                }
                .addOnFailureListener {
                    hideDialog()
                    val dialog = AlertDialog.Builder(this)
                    dialog.setMessage(it.toString())
                    dialog.show()
                }
    }

    private fun getToken(){
        var token = ""
        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            token = it
            Log.i("token", it)
            val user = User(et_email_sign_up_activity.text.toString(), et_name_sign_up_activity.text.toString(),
                et_mobile_sign_up_activity.text.toString(), "",
                FirebaseAuth.getInstance().currentUser?.uid!!, tv_address.text!!.toString(), token)
            Firebase().uploadUserSignIn(user, this)
        }.addOnFailureListener {
            val dialog = AlertDialog.Builder(this)
            dialog.setMessage(it.toString())
            dialog.show()
        }
    }

    fun uploadUserSuccess(){
        hideDialog()
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        val intent = Intent(this, IntroActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_ANIMATION)
        startActivity(intent)
        finish()
    }
}