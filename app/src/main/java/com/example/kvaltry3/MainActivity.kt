package com.example.kvaltry3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import com.example.kvaltry3.ui.theme.KvalTry3Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KvalTry3Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val db = Room.databaseBuilder(
                        context = LocalContext.current.applicationContext,
                        klass = UserDatabase::class.java,
                        name = "user-database"
                    ).allowMainThreadQueries().build()

                    val scope = CoroutineScope(Dispatchers.IO)

                        val jsonString =
                            "{ \"meta\":{}, \"data\": [ {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Санкт-Петербург\",\"endCityCode\":\"led\",\"startDate\":\"2023-07-20T00:00:00Z\",\"endDate\":\"2023-07-25T00:00:00Z\",\"price\":2690,\"searchToken\":\"MOW2007LED2507Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Нижний Новгород\",\"endCityCode\":\"goj\",\"startDate\":\"2023-08-07T08:15:00Z\",\"endDate\":\"2023-08-13T09:10:00Z\",\"price\":3140,\"searchToken\":\"MOW0708GOJ1308Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Махачкала\",\"endCityCode\":\"mcx\",\"startDate\":\"2023-10-16T10:00:00Z\",\"endDate\":\"2023-10-20T12:00:00Z\",\"price\":4570,\"searchToken\":\"MOW1610MCX2010Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Калининград\",\"endCityCode\":\"kgd\",\"startDate\":\"2023-10-10T21:00:00Z\",\"endDate\":\"2023-10-15T13:00:00Z\",\"price\":4570,\"searchToken\":\"MOW1010KGD1310Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Казань\",\"endCityCode\":\"kzn\",\"startDate\":\"2023-07-21T15:00:00Z\",\"endDate\":\"2023-07-30T15:00:00Z\",\"price\":4760,\"searchToken\":\"MOW2106KZN3006Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Самара\",\"endCityCode\":\"kuf\",\"startDate\":\"2023-09-06T11:00:00Z\",\"endDate\":\"2023-09-11T11:00:00Z\",\"price\":4902,\"searchToken\":\"MOW0609KUF1109Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Краснодар\",\"endCityCode\":\"krr\",\"startDate\":\"2023-08-15T09:30:00Z\",\"endDate\":\"2023-08-23T10:30:00Z\",\"price\":4914,\"searchToken\":\"MOW1504KRR2304Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Екатеринбург\",\"endCityCode\":\"svx\",\"startDate\":\"2023-07-20T12:00:00Z\",\"endDate\":\"2023-07-26T12:00:00Z\",\"price\":5096,\"searchToken\":\"MOW2006SVX2606Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Волгоград\",\"endCityCode\":\"vog\",\"startDate\":\"2023-07-27T11:10:00Z\",\"endDate\":\"2023-08-10T10:20:00Z\",\"price\":5140,\"searchToken\":\"MOW2706VOG1007Y100\"}, {\"startCity\":\"Москва\",\"startCityCode\":\"mow\",\"endCity\":\"Пермь\",\"endCityCode\":\"pee\",\"startDate\":\"2023-07-09T21:00:00Z\",\"endDate\":\"2023-07-16T00:00:00Z\",\"price\":5140,\"searchToken\":\"MOW0906PEE1606Y100\"} ] }"

                        val dataToAdd = parseJson(jsonString)

                        val existingItems = db.flightsDAO.getAllFlights()

                        dataToAdd.data.forEach {
                            if (!existingItems.contains(it))
                                scope.launch {
                                    db.flightsDAO.insert(it)
                                }
                        }

                    LoginScreen(db = db)
                }
            }
        }
    }
}

fun parseJson(jsonString: String): JsonResponse {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<JsonResponse>(jsonString)
}

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
data class Item(
    @PrimaryKey
    var id: Int,
    var name: String
)

@Entity
data class User(
    var email: String,
    var password: String
){
    @PrimaryKey(autoGenerate = true) var id: Int = 0
}

@Composable
fun MainScreen(db: FlightsDAO, user: User) {

    var showAddItemDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = "User: " + user.email)

            Button(onClick = {
                showAddItemDialog = true
            }) {
                Text("Add new item")
            }


        if (showAddItemDialog) {
            AddItemDialog(db) {
                showAddItemDialog = false
            }
        }

        ShowList(db)
    }
}

@Composable
fun ShowList(db: FlightsDAO) {
    val scope = CoroutineScope(Dispatchers.IO)

    var list by remember { mutableStateOf(db.getAllFlights()) }
        list = db.getAllFlights()

    LazyColumn {
        items(list) { item ->
            Box(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .border(1.dp, Color.DarkGray, CutCornerShape(10.dp))
                .background(Color.LightGray, CutCornerShape(10.dp))
            ){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)) {

                    Text("Start City: " + item.startCity,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("Start City Code: " + item.startCityCode,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("End City: " + item.endCity,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("End City Code: " + item.endCityCode,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("Start Date: " + item.startDate,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("End Date: " + item.endDate,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("Price: " + item.price,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)
                    Text("Search Token: " + item.searchToken,
                        fontFamily = FontFamily.Default,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp)

                    Button(onClick = {
                        scope.launch {
                            db.delete(item)
                            list = db.getAllFlights()
                        }
                    }) {
                        Text("Remove")
                    }

                }
            }
        }
    }
}

@Composable
fun AddItemDialog(db: FlightsDAO, onDismiss: () -> Unit) {
    val scope = CoroutineScope(Dispatchers.IO)

    var startCity by remember { mutableStateOf("") }
    var startCityCode by remember { mutableStateOf("") }
    var endCity by remember { mutableStateOf("") }
    var endCityCode by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var price by remember { mutableIntStateOf(0) }
    var searchToken by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add new item") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                TextField(
                    value = startCity,
                    onValueChange = { startCity = it },
                    label = { Text("Start City") }
                )
                TextField(
                    value = startCityCode,
                    onValueChange = { startCityCode = it },
                    label = { Text("Start City Code") }
                )
                TextField(
                    value = endCity,
                    onValueChange = { endCity = it },
                    label = { Text("End City") }
                )
                TextField(
                    value = endCityCode,
                    onValueChange = { endCityCode = it },
                    label = { Text("End City Code") }
                )
                TextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") }
                )
                TextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date") }
                )
                TextField(
                    value = price.toString(),
                    onValueChange = {
                        val newPrice = it
                        price = newPrice.toIntOrNull() ?: 0
                    },
                    label = { Text("Price") }
                )
                TextField(
                    value = searchToken,
                    onValueChange = { searchToken = it },
                    label = { Text("Search Token") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val flightToAdd = Flight(startCity, startCityCode, endCity, endCityCode, startDate, endDate, price, searchToken)

                scope.launch {
                    db.insert(flightToAdd)
                }

                onDismiss()

            }) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LoginScreen(db: UserDatabase) {
    val scope = CoroutineScope(Dispatchers.IO)

    var showLoginScreen by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(8.dp)) {
        if (showLoginScreen) {
            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") }
            )
            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") }
            )

            Row {
                Button(onClick = {
                    if (db.usersDAO.getAllUsers().any { it.email == email && it.password == password }) {
                        showLoginScreen = false
                        message = ""
                    }
                    else
                    {
                        message = "User not found"
                    }
                }) {
                    Text(text = "Log In")
                }

                Button(onClick = {
                    if (email.isNullOrEmpty() || password.isNullOrEmpty()) {
                        message = "Email or Password is empty"
                    }
                    if (db.usersDAO.getAllUsers().any { it.email == email && it.password == password }) {
                        message = "User already exists"
                    }
                    if (!db.usersDAO.getAllUsers().any { it.email == email && it.password == password } && !email.isNullOrEmpty() && !password.isNullOrEmpty()) {
                        scope.launch {
                            db.usersDAO.insert(User(email, password))
                            message = "Success"
                        }
                    }
                }) {
                    Text(text = "Register")
                }
            }

            Text(text = message)
        }
        if (!showLoginScreen) {
            MainScreen(db.flightsDAO, User(email, password))
        }
    }
}

