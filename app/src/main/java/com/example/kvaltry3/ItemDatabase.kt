package com.example.kvaltry3

import androidx.annotation.WorkerThread
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase : RoomDatabase() {

    abstract val itemsDAO: ItemsDAO
}

@Dao
interface ItemsDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)

    @Delete
    suspend fun delete(item: Item)

    @Query("SELECT * FROM item")
    fun getAllItems(): List<Item>
}