package com.example.kvaltry3

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.Update

@Database(entities = [User::class, Flight::class, UserMarkedFlight::class], version = 1)
abstract class UserDatabase : RoomDatabase() {

    abstract val usersDAO: UsersDAO
    abstract val flightsDAO: FlightsDAO
    abstract val userMarkedFlightsDAO: UserMarkedFlightsDAO
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
interface FlightsDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(flight: Flight)

    @Delete
    suspend fun delete(flight: Flight)

    @Query("SELECT * FROM flight")
    fun getAllFlights(): List<Flight>
}

@Dao
interface UserMarkedFlightsDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(userMarkedFlight: UserMarkedFlight)

    @Query("DELETE FROM usermarkedflight WHERE searchToken = :token AND userId = :userId")
    fun removeMarkedFlight(userId: Int, token: String)

    @Query("SELECT searchToken FROM usermarkedflight WHERE userId = :id")
    fun getMarkedFlights(id: Int): List<String>

    @Query("SELECT COUNT(*) FROM usermarkedflight WHERE userId = :userId AND searchToken = :token")
    fun checkIfMarked(userId: Int, token: String): Int
}