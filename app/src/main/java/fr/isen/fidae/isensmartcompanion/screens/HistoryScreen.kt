package fr.isen.fidae.isensmartcompanion.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import fr.isen.fidae.isensmartcompanion.R
import fr.isen.fidae.isensmartcompanion.database.AppDatabase
import fr.isen.fidae.isensmartcompanion.models.Message
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(innerPadding: PaddingValues, db: AppDatabase) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val messageDao = db.messageDao()
    var messages by remember { mutableStateOf<List<Message>>(listOf()) }

    // Load messages once when the screen is created
    LaunchedEffect(Unit) {
        coroutineScope.launch {
            try {
                messages = messageDao.getAll()
            } catch (e: Exception) {
                println("Room Error: ${e.message}")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Main Column with messages
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(id = R.string.message_history_title),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = colorResource(R.color.black)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Liste des messages
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(messages) { index, message ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .background(
                                colorResource(R.color.msg_font),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = if (message.isUser) stringResource(id = R.string.user) else stringResource(id = R.string.ai),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = colorResource(R.color.black)
                                )
                                Text(
                                    text = formatDate(message.timestamp),
                                    fontSize = 12.sp,
                                    color = colorResource(R.color.black)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = message.text,
                                color = colorResource(R.color.black),
                                fontSize = 16.sp
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            try {
                                                // Remove message and its paired message if exists
                                                val pairMessage: Message? = if (message.isUser) messages.getOrNull(index + 1) else messages.getOrNull(index - 1)
                                                messageDao.delete(message)
                                                if (pairMessage != null) {
                                                    messageDao.delete(pairMessage)
                                                }
                                                // Remove message from the local list
                                                messages = messages.filterNot { it == message }
                                            } catch (e: Exception) {
                                                println("Error delete: ${e.message}")
                                            }
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = stringResource(id = R.string.delete_message_description),
                                        tint = colorResource(R.color.black)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Delete All button at the bottom right
        Button(
            onClick = {
                coroutineScope.launch {
                    messageDao.deleteAll()
                    messages = emptyList() // Directly clear the local list without re-fetching
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.black),
                contentColor = colorResource(R.color.white)
            )
        ) {
            Text(stringResource(id = R.string.delete_all_button), color = colorResource(R.color.white))
        }
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
