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
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.kvaltry3.ui.theme.KvalTry3Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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

                    val userViewModel = UserViewModel()

                    MainNavHost(db = db, userViewModel = userViewModel)
                }
            }
        }
    }
}

class UserViewModel : ViewModel() {
    private val _user = mutableStateOf<User?>(null)
    val user: State<User?> = _user

    fun setUser(user: User) {
        _user.value = user
    }
}

@Composable
fun MainNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = "loginScreen",
    db: UserDatabase,
    userViewModel: UserViewModel
) {
    NavHost(
        modifier = modifier,
        navController = navController,
        startDestination = startDestination
    ) {
        composable("loginScreen") {
            LoginScreen(db, navController, userViewModel)
        }
        composable("mainScreen") {
            MainScreen(db, navController, userViewModel)
        }
    }
}

fun parseJson(jsonString: String): JsonResponse {
    val json = Json { ignoreUnknownKeys = true }
    return json.decodeFromString<JsonResponse>(jsonString)
}

@Composable
fun MainScreen(db: UserDatabase, navController: NavHostController, userViewModel: UserViewModel) {

    val currentUser = userViewModel.user.value

    var onlyShowMarked by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(10.dp)) {
        Text(text = "User: " + (currentUser?.email ?: ""))

        Button(onClick = { navController.navigate("loginScreen") }) {
            Text(text = "LogOut")
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = onlyShowMarked, onCheckedChange = {
                onlyShowMarked = !onlyShowMarked
            })
            Text(text = "Only show selected")
        }

        ShowList(db, currentUser, onlyShowMarked)
    }
}

@Composable
fun ShowList(db: UserDatabase, currentUser: User?, onlyMarked: Boolean) {
    val scope = CoroutineScope(Dispatchers.IO)

    val flightsDAO = db.flightsDAO
    val userMarkedFlightsDAO = db.userMarkedFlightsDAO

    var list by remember { mutableStateOf(flightsDAO.getAllFlights()) }
    val currentUserId by remember { mutableIntStateOf(currentUser!!.id) }

    var markedFlights by remember { mutableStateOf(userMarkedFlightsDAO.getMarkedFlights(currentUserId)) }

    if (onlyMarked) {
        list = flightsDAO.getAllFlights().filter { markedFlights.contains(it.searchToken) }
    } else {
        list = flightsDAO.getAllFlights()
    }

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

                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Checkbox(
                            checked = markedFlights.contains(item.searchToken),
                            onCheckedChange = {
                                scope.launch {
                                    if (userMarkedFlightsDAO.checkIfMarked(currentUserId, item.searchToken) == 0) {
                                        userMarkedFlightsDAO.insert(UserMarkedFlight(currentUserId, item.searchToken))
                                    } else {
                                        userMarkedFlightsDAO.removeMarkedFlight(currentUserId, item.searchToken)
                                    }
                                    markedFlights = userMarkedFlightsDAO.getMarkedFlights(currentUserId)
                                }

                                if (onlyMarked) {
                                    list = flightsDAO.getAllFlights().filter { markedFlights.contains(it.searchToken) }
                                } else {
                                    list = flightsDAO.getAllFlights()
                                }
                            }
                        )
                        Text("Selected")
                    }
                }
            }
        }
    }
}

@Composable
fun LoginScreen(db: UserDatabase, navController: NavHostController, userViewModel: UserViewModel) {

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
            val user = db.usersDAO.getAllUsers().firstOrNull {
               it.email == email && it.password == password
            }
            userViewModel.setUser(user!!)
            navController.navigate("mainScreen")
        }
    }
}

