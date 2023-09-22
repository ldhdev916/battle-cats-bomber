package com.ldhdev.battlecatsbomber

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.CombinedVibration
import android.os.IBinder
import android.os.VibrationEffect
import android.os.VibratorManager
import android.util.Log
import android.widget.Toast
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.ldhdev.shared.START_BATTLE_CATS_PATH
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.time.Duration.Companion.seconds

class AlarmReceiver : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        CoroutineScope(Dispatchers.IO).launch {

            val request = PutDataMapRequest.create(START_BATTLE_CATS_PATH)
                .apply {
                    dataMap.putLong("timestamp", System.currentTimeMillis())
                }
                .asPutDataRequest()
                .setUrgent()

            val result = Wearable.getDataClient(this@AlarmReceiver).putDataItem(request).await()

            launch(Dispatchers.Main) {
                Toast.makeText(this@AlarmReceiver, "$result", Toast.LENGTH_LONG).show()
            }
        }

        Log.i(null, "RECEIVED")

        val notification = Notification.Builder(this, NOTIFICATION_ID)
            .setContentTitle("냥코 봄버")
            .setContentText("냥코 봄버")
            .setOngoing(false)
            .build()

        startForeground(1, notification)

        val vibrator = getSystemService(VIBRATOR_MANAGER_SERVICE) as VibratorManager

        val timings = longArrayOf(100, 200, 100, 200, 100, 200)
        val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
        val vibrationEffect = VibrationEffect.createWaveform(timings, amplitudes, 0)

        val combinedVibration = CombinedVibration.createParallel(vibrationEffect)
        vibrator.vibrate(combinedVibration)

        CoroutineScope(Dispatchers.Default).launch {
            delay(7.seconds)

            vibrator.cancel()

            stopSelf()
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(STOP_FOREGROUND_REMOVE)
    }
}