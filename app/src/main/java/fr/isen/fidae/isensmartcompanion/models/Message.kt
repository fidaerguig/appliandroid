package fr.isen.fidae.isensmartcompanion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "message_table")
data class Message(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "is_user") val isUser: Boolean,
    @ColumnInfo(name = "timestamp") val timestamp: Long = System.currentTimeMillis()
)

