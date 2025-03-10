package fr.isen.fidae.isensmartcompanion.database

import androidx.room.Database
import androidx.room.RoomDatabase
import fr.isen.fidae.isensmartcompanion.dao.MessageDao
import fr.isen.fidae.isensmartcompanion.models.Message

@Database(entities = [Message::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}