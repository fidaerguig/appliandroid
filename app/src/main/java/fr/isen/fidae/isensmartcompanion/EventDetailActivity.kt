package fr.isen.fidae.isensmartcompanion

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.fidae.isensmartcompanion.models.Event
import fr.isen.fidae.isensmartcompanion.services.NotificationReceiver

class EventDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val event = intent.getParcelableExtra<Event>("event")

        setContent {
            if (event != null) {
                val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
                EventDetailScreen(event, sharedPreferences)
            } else {
                Text("Événement introuvable", modifier = Modifier.padding(16.dp))
            }
        }
    }
}

@Composable
fun EventDetailScreen(event: Event, prefs: SharedPreferences) {
    var isNotified by remember { mutableStateOf(prefs.getBoolean(event.id, false)) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = event.title,
            style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 12.dp)
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow("📅 Date", event.date)
                DetailRow("📍 Lieu", event.location)
                DetailRow("📌 Catégorie", event.category)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = if (isNotified) "Notification activée" else "Notification désactivée",
                style = MaterialTheme.typography.bodyLarge
            )
            Switch(
                checked = isNotified,
                onCheckedChange = {
                    isNotified = it
                    prefs.edit().putBoolean(event.id, isNotified).apply()

                    if (isNotified) {
                        scheduleNotification(context, event)
                    }
                },
                colors = SwitchDefaults.colors(checkedThumbColor = Color.Red)
            )
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = "$label: ", fontWeight = FontWeight.Bold)
        Text(text = value)
    }
}

fun scheduleNotification(context: Context, event: Event) {
    val intent = Intent(context, NotificationReceiver::class.java).apply {
        putExtra("event_title", event.title)
    }

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        event.id.hashCode(),
        intent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    val triggerTime = SystemClock.elapsedRealtime() + 10 * 1000

    alarmManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
}