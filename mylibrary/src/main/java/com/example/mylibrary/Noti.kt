package com.example.myapplication


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log


class Noti(val _context: Context) {
    val CHANNEL_ID = "1000212121"
    var notificationManager: NotificationManager? = null


    fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "channel_name"
            val descriptionText = "channel_description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            notificationManager =
                    _context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager!!.createNotificationChannel(channel)
        } else {
            Log.e("noti", "버전확인!!!")
        }
    }

    fun build(
            notificationId: Int,
            title: String ="R.string.accessibility_service_label",
            text: String = "R.string.accessibility_service_description_noti"
    ): Notification {

        val notificationIntent = Intent(_context, MediaProjectionActivity::class.java)
        notificationIntent.putExtra("action", "stop")

//        val pendingIntent = PendingIntent.getActivity(
//                _context,
//                0, notificationIntent, 0
//        )
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(_context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        }else {
            PendingIntent.getBroadcast(_context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        var builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification.Builder(_context!!, CHANNEL_ID)

        } else {
            Notification.Builder(_context!!)
        }
              //  .setSmallIcon(R.drawable.ic_stat_name)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(intent)
               // .addAction(R.drawable.ic_launcher_background, "정지", pendingIntent)
                .build()
        return builder
    }

}