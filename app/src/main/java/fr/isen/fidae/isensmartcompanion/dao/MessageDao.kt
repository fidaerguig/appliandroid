package fr.isen.fidae.isensmartcompanion.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import fr.isen.fidae.isensmartcompanion.models.Message

@Dao
interface MessageDao {
    @Query("SELECT * FROM message_table")
    suspend fun getAll(): List<Message>

    @Insert
    suspend fun insert(vararg message: Message)

    @Delete
    suspend fun delete(vararg message: Message)

    @Query("DELETE FROM message_table")
    suspend fun deleteAll()
}