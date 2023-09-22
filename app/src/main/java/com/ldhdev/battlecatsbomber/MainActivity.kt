package com.ldhdev.battlecatsbomber

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.ldhdev.battlecatsbomber.ui.theme.BattleCatsBomberTheme
import com.ldhdev.shared.SET_ALARM_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

class MainActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Settings.canDrawOverlays(this)) {
            startActivity(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION))
        }

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        manager.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_ID,
                "냥코 봄버",
                NotificationManager.IMPORTANCE_HIGH
            )
        )

        if (!manager.areNotificationsEnabled()) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 0)
        }

        setContent {
            BattleCatsBomberTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold {
                        Column(
                            modifier = Modifier
                                .padding(it)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            AlarmSetButton()

                            Spacer(modifier = Modifier.height(30.dp))

                            AppBlockSwitch()

                            Spacer(modifier = Modifier.height(30.dp))

                            PermissionRequestButton()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AlarmSetButton() {

    val context = LocalContext.current

    val dataClient = remember(context) { Wearable.getDataClient(context) }
    val scope = rememberCoroutineScope { Dispatchers.IO }

    var at by remember { mutableStateOf(LocalTime.now().plusMinutes(1)) }

    val timePickerDialog = TimePickerDialog(
        context,
        { _, hour, minute ->
            at = LocalTime.of(hour, minute)

            scope.launch {
                val request = PutDataMapRequest.create(SET_ALARM_PATH)
                    .apply {
                        dataMap.putLong("timestamp", System.currentTimeMillis())

                        val alarmAt =
                            at.atDate(LocalDate.now()).atZone(ZoneId.systemDefault()).toInstant()
                                .toEpochMilli()

                        dataMap.putLong("at", alarmAt)
                    }
                    .asPutDataRequest()
                    .setUrgent()

                val result = dataClient.putDataItem(request).await()

                launch(Dispatchers.Main) {
                    Toast.makeText(context, "$result", Toast.LENGTH_LONG).show()
                }
            }
        },
        at.hour,
        at.minute,
        false
    )

    ElevatedButton(onClick = {
        timePickerDialog.show()
    }) {
        Text(text = "알람 설정")
    }
}

@Composable
fun AppBlockSwitch() {

    val context = LocalContext.current
    var checked by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {

        Text(text = "앱 막기")

        Spacer(modifier = Modifier.width(10.dp))

        Switch(
            checked = checked,
            onCheckedChange = {
                checked = it
                val intent = Intent(context, OverlayService::class.java)

                if (it) {
                    Log.i(null, "Starting service")
                    context.startForegroundService(intent)
                } else {
                    context.stopService(intent)
                }
            }
        )
    }
}

@Composable
fun PermissionRequestButton() {

    val context = LocalContext.current

    ElevatedButton(
        onClick = {
            context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }
    ) {
        Text(text = "권한 요청")
    }
}