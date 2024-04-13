package com.example.kvaltry3

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [User::class, Item::class, Flight::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract val usersDAO: UsersDAO
    abstract val itemsDAO: ItemsDAO
    abstract val flightsDAO: FlightsDAO
}

@Dao
interface UsersDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM user")
    fun getAllUsers(): List<User>
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

@Dao
interface FlightsDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flight: Flight)

    @Delete
    suspend fun delete(flight: Flight)

    @Query("SELECT * FROM flight")
    fun getAllFlights(): List<Flight>
}