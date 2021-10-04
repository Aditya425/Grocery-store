package com.example.grocerystore.fcm

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService

class MyFirebaseInstanceIDService: FirebaseMessagingService() {
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
        Log.i("onNewToken", p0)
    }
}