package android.example.com.squawker.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.example.com.squawker.MainActivity
import android.example.com.squawker.R
import android.example.com.squawker.provider.SquawkContract
import android.example.com.squawker.provider.SquawkProvider
import android.media.RingtoneManager
import android.support.v4.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.experimental.async

class SquawkMessageService: FirebaseMessagingService(){

    private val JSON_KEY_AUTHOR = SquawkContract.COLUMN_AUTHOR
    private val JSON_KEY_AUTHOR_KEY = SquawkContract.COLUMN_AUTHOR_KEY
    private val JSON_KEY_MESSAGE = SquawkContract.COLUMN_MESSAGE
    private val JSON_KEY_DATE = SquawkContract.COLUMN_DATE
    private val NOTIFICATION_MAX_CHARACTERS = 30

    override fun onMessageReceived(message: RemoteMessage?) {
        super.onMessageReceived(message)
        val mapOfData = message?.data
        if(mapOfData!=null){
            insertSquawk(mapOfData)
            sendNotification(mapOfData)
        }
    }

    private fun insertSquawk(returnedData: Map<String, String>){
        async{
            val cv = ContentValues()
            with(cv){
                put(SquawkContract.COLUMN_AUTHOR_KEY, returnedData.get(JSON_KEY_AUTHOR_KEY))
                put(SquawkContract.COLUMN_AUTHOR, returnedData.get(JSON_KEY_AUTHOR))
                put(SquawkContract.COLUMN_MESSAGE, returnedData.get(JSON_KEY_MESSAGE))
                put(SquawkContract.COLUMN_DATE, returnedData.get(JSON_KEY_DATE))
            }
            contentResolver.insert(SquawkProvider.SquawkMessages.CONTENT_URI, cv)
        }
    }

    private fun sendNotification(data: Map<String, String>) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        // Create the pending intent to launch the activity
        val pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT)

        val author = data[JSON_KEY_AUTHOR]
        var message = data[JSON_KEY_MESSAGE]

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message!=null && message.length > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026"
        }

        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, NotificationChannel.DEFAULT_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_duck)
                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
    }
}