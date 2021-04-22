package com.daniel.geofenceapp.broadcastreceiver

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.daniel.geofenceapp.R
import com.daniel.geofenceapp.util.Constants.NOTIFICATION_CHANNEL_ID
import com.daniel.geofenceapp.util.Constants.NOTIFICATION_CHANNEL_NAME
import com.daniel.geofenceapp.util.Constants.NOTIFICATION_ID
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofenceStatusCodes
import com.google.android.gms.location.GeofencingEvent

class GeofenceBroadcastReceiver: BroadcastReceiver() {


    override fun onReceive(context: Context, intent: Intent) {
        val geofencingEvent = GeofencingEvent.fromIntent(intent)
        if(geofencingEvent.hasError()){
            val errorMessage = GeofenceStatusCodes
                    .getStatusCodeString(geofencingEvent.errorCode)
            Log.e("BroadCastReceiver", errorMessage)
            return
        }

        when(geofencingEvent.geofenceTransition){
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                displayNotification(context, "Geofence Enter")
                Log.d("BroadCastReceiver", "Geofence Enter")
            }
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                displayNotification(context, "Geofence Exit")
                Log.d("BroadCastReceiver", "Geofence Exit")
            }
            Geofence.GEOFENCE_TRANSITION_DWELL -> {
                displayNotification(context, "Geofence Dwell")
                Log.d("BroadCastReceiver", "Geofence Dwell")
            }
            else -> {
                Log.d("BroadCastReceiver", "invalid type")
            }
        }
    }

    private fun displayNotification(context: Context, geofenceTransition: String){
        val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        createNotificationChannel(notificationManager)

        val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Geofence")
                .setContentText(geofenceTransition)
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel(notificationManager: NotificationManager){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
}