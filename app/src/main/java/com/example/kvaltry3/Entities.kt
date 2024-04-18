package com.example.kvaltry3

import androidx.room.Entity
import androidx.room.PrimaryKey


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
class Meta

data class JsonResponse(
    var meta: Meta,
    var data: List<Flight>
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