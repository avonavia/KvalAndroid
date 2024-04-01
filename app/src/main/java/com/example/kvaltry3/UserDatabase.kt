package com.example.kvaltry3

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase

@Database(entities = [User::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract val usersDAO: UsersDAO
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