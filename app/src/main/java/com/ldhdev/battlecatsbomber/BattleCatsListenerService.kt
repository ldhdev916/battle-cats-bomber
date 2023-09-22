package com.ldhdev.battlecatsbomber

import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import com.ldhdev.shared.START_ACTIVITY_PATH
import com.ldhdev.shared.START_BATTLE_CATS_PATH

class BattleCatsListenerService : WearableListenerService() {

    override fun onDataChanged(buffer: DataEventBuffer) {

        buffer.forEach {
            when (it.dataItem.uri.path) {
                START_BATTLE_CATS_PATH -> {

                    val request = PutDataMapRequest.create(START_ACTIVITY_PATH)
                        .apply {
                            dataMap.putLong("timestamp", System.currentTimeMillis())
                        }
                        .asPutDataRequest()
                        .setUrgent()

                    Wearable.getDataClient(this).putDataItem(request)

                    val intent =
                        packageManager.getLaunchIntentForPackage("jp.co.ponos.battlecatskr")

                    startActivity(intent)
                }
            }
        }
    }
}