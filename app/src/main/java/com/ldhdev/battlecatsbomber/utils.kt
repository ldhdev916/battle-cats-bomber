package com.ldhdev.battlecatsbomber

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context

fun Context.getTopPackageName(): String? {

    var packageName: String? = null
    val manager = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

    val endTime = System.currentTimeMillis()
    val beginTime = endTime - 1000 * 60 * 60 // 1 hour ago
    val myUsageEvents = manager.queryEvents(beginTime, endTime)

    while (myUsageEvents.hasNextEvent()) {
        val event = UsageEvents.Event()
        myUsageEvents.getNextEvent(event)

        when (event.eventType) {
            UsageEvents.Event.ACTIVITY_RESUMED -> {
                packageName = event.packageName
            }

            UsageEvents.Event.ACTIVITY_PAUSED -> {
                packageName = null
            }
        }
    }

    return packageName
}