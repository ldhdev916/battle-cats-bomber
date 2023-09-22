package com.ldhdev.battlecatsbomber

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.graphics.PixelFormat
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class OverlayService : Service() {

    companion object {
        private val excludePackageNames =
            setOf(
//                "com.ldhdev.battlecatsbomber",
                "com.sec.android.app.launcher",
                "jp.co.ponos.battlecatskr",
                null
            )
    }

    private var lastView: View? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()

        Log.i(null, "Created")

        val notification = Notification.Builder(this, NOTIFICATION_ID)
            .setContentTitle("냥코 봄버")
            .setContentText("냥코 봄버")
            .setOngoing(true)
            .build()

        startForeground(1, notification)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )

        val inflater = LayoutInflater.from(this)

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        scope.launch {
            while (true) {

                val topPackageName = getTopPackageName()

                lastView = if (topPackageName in excludePackageNames) {
                    lastView?.let(windowManager::removeView)

                    null
                } else {
                    if(lastView == null) {
                        val view = inflater.inflate(R.layout.overlay, null)

                        launch(Dispatchers.Main) {
                            windowManager.addView(view, params)
                        }

                        view
                    } else {
                        lastView
                    }
                }

                delay(0.1.seconds)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        stopForeground(STOP_FOREGROUND_REMOVE)

        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        lastView?.let {
            windowManager.removeView(it)
        }

        scope.cancel()
    }
}