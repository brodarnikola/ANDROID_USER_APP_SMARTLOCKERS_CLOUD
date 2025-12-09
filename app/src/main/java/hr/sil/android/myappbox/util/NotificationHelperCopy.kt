package hr.sil.android.myappbox.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat
import hr.sil.android.myappbox.App
import hr.sil.android.myappbox.R
import hr.sil.android.myappbox.view.ui.activities.SplashActivity


class NotificationHelperCopy(val mContext: Context) {

    var mNotificationManager: NotificationManager? = null
    val NOTIFICATION_CHANNEL_ID = "10001"
    fun createNotification(title: String?, message: String? ) {
        /**Creates an explicit intent for an Activity in your app */

        val resultIntent = Intent(mContext, SplashActivity::class.java)
        //val packageName = this.mContext.packageName
        //val componentName = ComponentName(packageName, packageName + ".aliasMainActivity")
        //resultIntent.component = componentName
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        //val resultIntent = Intent(mContext, klazz)
        //resultIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        val resultPendingIntent = PendingIntent.getActivity(mContext,
                0 /* Request code */, resultIntent,
                PendingIntent.FLAG_IMMUTABLE)
        val mBuilder = NotificationCompat.Builder(mContext, NOTIFICATION_CHANNEL_ID)

        val largeIcon = BitmapFactory.decodeResource(
            App.Companion.ref.resources,
            // it needs to have the same name in all flavours project,
            // so that push notification will work
            // tried to implement with flavors, but it does not want to work
            R.drawable.notification_icon_large
        )

        mBuilder.setSmallIcon(R.drawable.notification_icon_small)
        mBuilder.setContentTitle(title)
                .setContentText(message)
                .setLargeIcon(largeIcon)
                .setAutoCancel(true)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(resultPendingIntent)

        mNotificationManager = mContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            mBuilder.setChannelId(NOTIFICATION_CHANNEL_ID)
            mNotificationManager?.createNotificationChannel(notificationChannel)
        }
        assert(mNotificationManager != null)
        mNotificationManager?.notify(0 /* Request Code */, mBuilder.build())
    }

    fun clearNotification() {
        if(mNotificationManager!=null){
            mNotificationManager?.cancelAll()
        }
    }

}