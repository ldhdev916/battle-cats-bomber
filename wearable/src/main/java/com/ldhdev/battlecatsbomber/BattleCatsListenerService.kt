package com.ldhdev.battlecatsbomber

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.util.Log
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.WearableListenerService
import com.ldhdev.battlecatsbomber.presentation.MainActivity
import com.ldhdev.shared.SET_ALARM_PATH
import com.ldhdev.shared.START_ACTIVITY_PATH

class BattleCatsListenerService : WearableListenerService() {

    override fun onDataChanged(buffer: DataEventBuffer) {
        buffer.forEach {
            when (it.dataItem.uri.path) {
                SET_ALARM_PATH -> {
                    val alarmManger = getSystemService(ALARM_SERVICE) as AlarmManager

                    if (alarmManger.canScheduleExactAlarms()) {

                        Log.i(null, "Scheduling alarm")

                        val at = DataMapItem.fromDataItem(it.dataItem).dataMap.getLong("at")

                        alarmManger.setExactAndAllowWhileIdle(
                            AlarmManager.RTC_WAKEUP,
                            at,
                            PendingIntent.getForegroundService(
                                this,
                                it.dataItem.hashCode(),
                                Intent(this, AlarmReceiver::class.java),
                                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                    }
                }

                START_ACTIVITY_PATH -> {
                    startActivity(
                        Intent(
                            this,
                            MainActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                }
            }
        }
    }
}