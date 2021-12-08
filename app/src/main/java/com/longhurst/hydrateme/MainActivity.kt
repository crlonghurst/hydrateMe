package com.longhurst.hydrateme

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.widget.DatePicker
import android.widget.EditText
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement.Absolute.Center
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.colors
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.Color.Companion.Gray
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key.Companion.Calendar
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.longhurst.hydrateme.ui.theme.HydrateMeTheme
import java.time.LocalDateTime
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import com.longhurst.hydrateme.data.*
import kotlinx.coroutines.*
import kotlin.coroutines.coroutineContext


class MainActivity : AppCompatActivity() {

    lateinit var dbHelper: DatabaseHelperImpl
    lateinit var navController: NavController
    val schedules: MutableList<Schedule> = mutableListOf()
    val mHour = 0
    val mMinute = 0
    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dbHelper = DatabaseHelperImpl(DatabaseBuilder.getInstance(applicationContext))
        GlobalScope.launch {
//            dbHelper.upsert(Schedule(1, "Test", 5F, 5F, 5F, false, 5, true))
            schedules.addAll(dbHelper.getAll()) }
        setContent {
            HydrateMeTheme {
                navController = rememberNavController()
                NavHost(navController = navController as NavHostController, startDestination = "main"){
                    composable("main"){ mainSetup() }
                    composable("history") { history() }
                    composable("schedule") { schedule() }
                }
            }
        }
    }


    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @Composable
    fun schedule(){
        Column(){
            var weight: String by rememberSaveable{ mutableStateOf("0") }
            var hours: String by rememberSaveable{ mutableStateOf("0") }
            Row(Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)){
                Text(text = "Current Weight: ")
                Box(Modifier.border(5.dp, Black, RectangleShape)){
                    TextField(value = weight, onValueChange = { weight = it; totalDrinks(weight.toFloat(), hours.toFloat()) })
                }
            }
            Row(Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)){
                Text(text = "How long will you be outdoors?")
                Box(Modifier.border(5.dp, Black, RectangleShape)){
                    TextField(value = hours, onValueChange = { hours = it; totalDrinks(weight.toFloat(), hours.toFloat());})
                }
            }
//            Row(
//                Modifier
//                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
//                    .align(CenterHorizontally)){
//                Button({ newSchedule() }) {
//                    Text(text = "add more times")
//                }
//            }
            Row(
                Modifier
                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    .align(CenterHorizontally)){
                Button(onClick = { navController.navigate("main") }){
                    Text(text = "Back")
                }
            }
        }
    }
    @Composable
    fun newSchedule(){

    }
    @ExperimentalFoundationApi
    @Composable
    fun history(){
        Surface(color = MaterialTheme.colors.background) {
            Column(){
                val currentSchedules = schedules.filter { !it.recurring }
                Row(){
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        items(currentSchedules.size,
                            itemContent = {
                                HistoryListItem(listItem = currentSchedules[it])
                            })
                    }
                }
                Spacer(Modifier.border(width = 100.dp, color = Black, shape = RectangleShape))
                Button(onClick = { navController.navigate("main") },
                    Modifier.padding(160.dp,5.dp)){Text("Back") }
            }
        }
    }
    @ExperimentalFoundationApi
    @Composable
    fun mainSetup() {

            Surface(color = MaterialTheme.colors.background) {
                Column(Modifier.padding(0.dp, 0.dp, 0.dp, 0.dp)) {
                    Row(
                        Modifier
                            .padding(0.dp, 0.dp, 0.dp, 0.dp)
                            .align(CenterHorizontally)
                    ) {
                        Text(
                            "Intake",
                            style = MaterialTheme.typography.body1.copy(
                                color = Black,
                                fontSize = 30.sp,
                                fontStyle = FontStyle.Normal,
                                fontFamily = Serif,
                                textAlign = TextAlign.Center,
                            )
                        )
                    }
                    val currentSchedules = schedules.filter { it.active }
                    Row() {
                        LazyColumn(
                            contentPadding = PaddingValues(
                                horizontal = 16.dp,
                                vertical = 8.dp
                            )
                        ) {
                            items(currentSchedules.size,
                                itemContent = {
                                    ScheduleListItem(listItem = currentSchedules[it])
                                })
                            item {
                                Row(Modifier.padding(10.dp, 10.dp)) {
                                    Button(onClick = { navController.navigate("history") }) {
                                        Text(
                                            text = "View History"
                                        )
                                    }
                                    Button(
                                        onClick = { navController.navigate("schedule") },
                                        Modifier.padding(50.dp,0.dp, 0.dp, 0.dp)
                                    ) { Text(text ="Add Schedule") }
                                }
                            }
                        }
                    }
                }
            }
    }

    @Composable
    fun HistoryListItem(listItem: Schedule){
        Card(
            modifier = Modifier.padding(horizontal = 0.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 5.dp,
            shape = AbsoluteCutCornerShape(corner = CornerSize(5.dp))
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ){
                Text(text = listItem.scheduleName, style = typography.h6)
                Text(text = "Drinks you took ${listItem.drinksNeeded}", style = typography.caption)
            }
        }
    }

    @Composable
    fun ScheduleListItem(listItem: Schedule) {
        Card(
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 5.dp,
            shape = AbsoluteCutCornerShape(corner = CornerSize(5.dp))
        ) {
            Row{
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterVertically)
                ){
                    Text(text = listItem.scheduleName, style = typography.h6)
                    Text(text = "Drinks needed ${listItem.drinksNeeded}", style = typography.caption)

                }
                Column (
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterVertically)
                        ){
                    Button(onClick = { /*TODO*/ }) {
                        Text(text = "Drink!")
                    }
                }
            }
        }
    }
}