package com.kira.autopost

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.kira.autopost.data.AppPreferences
import com.kira.autopost.tdlib.TdLibManager

class KiraApplication : Application() {

    lateinit var appPreferences: AppPreferences
        private set

    lateinit var tdLibManager: TdLibManager
        private set

    override fun onCreate() {
        super.onCreate()
        instance = this
        appPreferences = AppPreferences(applicationContext)
        tdLibManager = TdLibManager(applicationContext, appPreferences)

        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "KIRA Auto Post Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Foreground Service notification for Telegram auto-reply"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "kira_auto_post_channel"
        lateinit var instance: KiraApplication
            private set
    }
}
