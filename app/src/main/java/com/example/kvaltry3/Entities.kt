package com.example.kvaltry3

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class Flight(
    var startCity: String,
    var startCityCode: String,
    var endCity: String,
    var endCityCode: String,
    var startDate: String,
    var endDate: String,
    var price: Int,
    @PrimaryKey
    var searchToken: String
)
@Serializable
class Meta

@Serializable
data class JsonResponse(
    val meta: Meta,
    val data: List<Flight>
)

@Entity
data class User(
    var email: String,
    var password: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}
@Entity
data class UserMarkedFlight(
    val userId: Int,
    val searchToken: String
){
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}