package com.example.myapplication

import android.annotation.TargetApi
import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.nio.channels.Channel
import java.nio.file.Files

class MainActivity : AppCompatActivity() {

    @TargetApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //cutout
        /*  window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

          supportActionBar?.hide()

          //window.attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER

          window.decorView.rootView.setOnApplyWindowInsetsListener { view, windowInsets ->
              val displayCutout = view.rootWindowInsets.displayCutout
              windowInsets.consumeDisplayCutout()
          }

          toggleButton.setOnClickListener{
              expandableView.toggle()
          }
          */

        //Notification events
        val doeGroup = "group-id1"
        val smithGroup = "group-id2"
        val channelId1 = "channel-id1"
        val channelId2 = "channel-id2"

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannelGroup(NotificationChannelGroup(doeGroup, "Doe Family Group"))
        manager.createNotificationChannelGroup(NotificationChannelGroup(smithGroup, "Smith Family Group"))
        manager.createNotificationChannel(createNotificationChannel(channelId1, "John Doe", doeGroup))
        manager.createNotificationChannel(createNotificationChannel(channelId2, "John Smith", smithGroup))



        notificationButton.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithGroup(doeGroup, channelId1, "John Doe", "Hello")

            )
        }

        notificationButton2.setOnClickListener {
            manager.notify(
                System.currentTimeMillis().toInt(),
                createNotificationWithPerson(doeGroup, channelId1, "John Doe", "Hello")
            )
        }

        settingsButton.setOnClickListener {
            val intent = Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS).apply{
                putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
                putExtra(Settings.EXTRA_CHANNEL_ID, channelId1)
            }
            startActivity(intent)
        }
    }

    private fun createNotificationWithGroup(groupId: String, channelId: String, title: String, text: String): Notification? {
        return Notification.Builder(this, channelId).setGroup(groupId).setContentTitle(title).setContentText(text)
            .setSmallIcon(R.drawable.notify_panel_notification_icon_bg)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true).build()
    }

    private fun createNotificationChannel(
        channelId: String,
        channelName: String,
        groupId: String
    ): NotificationChannel {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
        channel.group = groupId

        return channel

    }

    private fun createNotificationWithPerson(
        groupId: String,
        channelId: String,
        personName: String,
        messageText: String
    ) :Notification{
        val sender = Person.Builder().setName(personName).build()
        val message = Notification.MessagingStyle.Message(messageText, System.currentTimeMillis(), sender)
        val style = Notification.MessagingStyle(sender).addMessage(message)

        val file = File(filesDir, "shared/pictures12414.png")
        val uri = FileProvider.getUriForFile(this , "${BuildConfig.APPLICATION_ID}.FileProvider", file)

        message.setData("image/", uri)
        return Notification.Builder(this, channelId).setStyle(style).setGroup(groupId)
            .setSmallIcon(R.drawable.notify_panel_notification_icon_bg)
            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
            .setAutoCancel(true).build()
    }

}
