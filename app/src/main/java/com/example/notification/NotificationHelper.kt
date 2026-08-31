package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.model.OutageItem
import com.example.model.OutageType

class NotificationHelper(private val context: Context) {

    private val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    init {
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val waterChannel = NotificationChannel(
                CHANNEL_WATER,
                "İZSU Su Kesintileri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "İzmir genelindeki arıza ve planlı su kesintisi bildirimleri"
                enableVibration(true)
                enableLights(true)
            }

            val electricityChannel = NotificationChannel(
                CHANNEL_ELECTRICITY,
                "Gediz Elektrik Kesintileri",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "İzmir genelindeki arıza ve şebeke elektrik kesintisi bildirimleri"
                enableVibration(true)
                enableLights(true)
            }

            notificationManager.createNotificationChannel(waterChannel)
            notificationManager.createNotificationChannel(electricityChannel)
        }
    }

    fun showOutageNotification(outage: OutageItem) {
        val channelId = if (outage.type == OutageType.WATER) CHANNEL_WATER else CHANNEL_ELECTRICITY
        val emoji = if (outage.type == OutageType.WATER) "💧" else "⚡"
        val provider = if (outage.type == OutageType.WATER) "İZSU" else "Gediz Elektrik"

        val title = "$emoji ${outage.type.title} - $provider"
        val affectedStr = if (outage.neighborhoods.isNotEmpty()) {
            "${outage.district} / ${outage.neighborhoods.joinToString(", ")}"
        } else {
            outage.district
        }

        val formattedStart = formatTime(outage.startTime)
        val formattedEnd = formatTime(outage.estimatedEndTime)

        val bodyText = "$affectedStr\n${outage.reason}\nBaşlangıç: $formattedStart | Tahmini Bitiş: $formattedEnd"

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("outage_id", outage.id)
            putExtra("district", outage.district)
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            outage.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle(title)
            .setContentText("$affectedStr: Tahmini Bitiş $formattedEnd")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(bodyText)
                    .setSummaryText(provider)
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(outage.id.hashCode(), notification)
    }

    fun sendTestNotification(type: OutageType, district: String, neighborhood: String) {
        val channelId = if (type == OutageType.WATER) CHANNEL_WATER else CHANNEL_ELECTRICITY
        val emoji = if (type == OutageType.WATER) "💧" else "⚡"
        val provider = if (type == OutageType.WATER) "İZSU" else "Gediz Elektrik"
        val title = "$emoji ${type.title} ($provider)"
        val message = "$district / $neighborhood\nŞebeke iyileştirme ve arıza giderme çalışması başladı.\nTahmini Bitiş: 17:30"

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            System.currentTimeMillis().toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText("$district / $neighborhood - Kesinti Bildirimi")
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify((System.currentTimeMillis() % 100000).toInt(), notification)
    }

    private fun formatTime(millis: Long): String {
        val date = java.util.Date(millis)
        val format = java.text.SimpleDateFormat("HH:mm", java.util.Locale("tr"))
        return format.format(date)
    }

    companion object {
        const val CHANNEL_WATER = "izmir_water_outages_channel"
        const val CHANNEL_ELECTRICITY = "izmir_electricity_outages_channel"
    }
}
