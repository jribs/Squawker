package android.example.com.squawker.firebase

import android.util.Log
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService


class SquawkInstanceIDService: FirebaseInstanceIdService(){

    init {
        Log.d(this.javaClass.simpleName, "I started")
    }

    override fun onTokenRefresh() {
        try {
            var refreshedToken = FirebaseInstanceId.getInstance().token
            sendTokenToServer(refreshedToken)
            Log.d(this.javaClass.simpleName, "Current Instance ID is $refreshedToken")

        } catch (e: Exception){
            Log.e(this.javaClass.simpleName, "Error: ", e)
        }
    }

    private fun sendTokenToServer(tokenValue: String?){
        //Send it to the server here
    }
}