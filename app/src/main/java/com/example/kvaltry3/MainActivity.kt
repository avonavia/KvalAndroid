package com.example.kvaltry3

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Room
import com.example.kvaltry3.ui.theme.KvalTry3Theme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

                    LoginScreen(db = db)
                }
            }
        }
    }
}

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
fun MainScreen(db: ItemsDAO, user: User) {

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
fun ShowList(db: ItemsDAO) {
    val scope = CoroutineScope(Dispatchers.IO)

    var list by remember { mutableStateOf(db.getAllItems()) }
        list = db.getAllItems()

    LazyColumn {
        items(list) { item ->
            Box(modifier = Modifier
                .padding(10.dp)
                .fillMaxWidth()
                .border(2.dp, Color.Black, RectangleShape)){
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)) {
                    Text(item.id.toString())
                    Text(item.name)
                    Button(onClick = {
                        scope.launch {
                            db.delete(item)
                            list = db.getAllItems()
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
fun AddItemDialog(db: ItemsDAO, onDismiss: () -> Unit) {
    val scope = CoroutineScope(Dispatchers.IO)
    var name by remember { mutableStateOf("") }
    var id by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add new item") },
        text = {
            Column(modifier = Modifier.padding(8.dp)) {
                TextField(
                    value = id,
                    onValueChange = { id = it },
                    label = { Text("ID") }
                )
                TextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") }
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newId = id.toIntOrNull() ?: 0
                scope.launch {
                    db.insert(Item(newId, name))
                }
                name = ""
                id = ""
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
            MainScreen(db.itemsDAO, User(email, password))
        }
    }
}

