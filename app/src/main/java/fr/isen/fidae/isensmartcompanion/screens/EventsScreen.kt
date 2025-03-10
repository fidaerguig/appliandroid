package fr.isen.fidae.isensmartcompanion.screens

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import fr.isen.fidae.isensmartcompanion.EventDetailActivity
import fr.isen.fidae.isensmartcompanion.R
import fr.isen.fidae.isensmartcompanion.api.RetrofitInstance
import fr.isen.fidae.isensmartcompanion.models.Event
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@Composable
fun EventsScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current
    val events = remember { mutableStateOf<List<Event>>(listOf()) }

    LaunchedEffect(Unit) {
        val response = RetrofitInstance.api.getEvents()
        response.enqueue(object : Callback<List<Event>> {
            override fun onResponse(p0: Call<List<Event>>, p1: Response<List<Event>>) {
                events.value = p1.body() ?: listOf()
            }

            override fun onFailure(p0: Call<List<Event>>, p1: Throwable) {
                Log.e("Request", p1.message ?: "Request failed")
            }
        })
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = stringResource(R.string.event_screen_title),
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.Bold,
            ),
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.CenterHorizontally)
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(events.value) { event ->
                EventItem(event) {
                    val intent = Intent(context, EventDetailActivity::class.java).apply {
                        putExtra("event", event) //Check si ça marche bien ça parce que chelou /!\
                    }
                    context.startActivity(intent)
                }
            }
        }
    }
}

@Composable
fun EventItem(event: Event, onClick: () -> Unit) {
    Card( //pour ajouter ptit ombre stylé
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Column(modifier = Modifier.padding(15.dp)) { //10 de base, a tester quand meme
            Text(text = event.title, style = MaterialTheme.typography.headlineSmall)
            Text(text = stringResource(id = R.string.event_date) + ": ${event.date}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
