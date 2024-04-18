package com.example.kvaltry3

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertTextEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Rule

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.kvaltry3", appContext.packageName)
    }

    @RunWith(AndroidJUnit4::class)
    class MainActivityTest {

        @get:Rule
        val composeTestRule = createComposeRule()

        @Test
        fun testLoginError() {
            composeTestRule.setContent {

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

            composeTestRule.onNodeWithTag("EMAIL").performTextInput("login")
            composeTestRule.onNodeWithTag("PASSWORD").performTextInput("password")

            composeTestRule.onNodeWithTag("LOGIN").performClick()

            composeTestRule.onNodeWithTag("MESSAGE").assertTextEquals("User not found")
        }
    }
}