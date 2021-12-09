package com.longhurst.hydrateme

import android.os.Bundle
import android.widget.Toast
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.AbsoluteCutCornerShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontFamily.Companion.Serif
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.longhurst.hydrateme.ui.theme.HydrateMeTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.lifecycleScope
import com.longhurst.hydrateme.data.*
import kotlinx.coroutines.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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
//            schedules.add(Schedule(20211207,"Not used", 180F, 12F, 12, false, 8))
//            schedules.add(Schedule(20211207,"Not used", 170F, 8F, 14, false, 9))
//            schedules.add(Schedule(20211201,"Not used", 220F, 19F, 24, false, 12))
//            schedules.forEach { dbHelper.upsert(it) }
            schedules.addAll(dbHelper.getAll())
        }
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
            var name: String by rememberSaveable{ mutableStateOf("Name") }
            var weight: String by rememberSaveable{ mutableStateOf("0") }
            var hours: String by rememberSaveable{ mutableStateOf("0") }
            Row(Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)){
                Text(text = "Current Weight: ")
                Box(Modifier.border(5.dp, Black, RectangleShape)){
                    TextField(value = name, onValueChange = { name = it } )
                }
            }
            Row(Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)){
                Text(text = "Current Weight: ")
                Box(Modifier.border(5.dp, Black, RectangleShape)){
                    TextField(value = weight,
                            onValueChange = { weight = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                    ))
                }
            }
            Row(Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)){
                Text(text = "How long will you be outdoors?")
                Box(Modifier.border(5.dp, Black, RectangleShape)) {
                    TextField(value = hours,
                            onValueChange = { hours = it },
                            keyboardOptions = KeyboardOptions.Default.copy(
                                    keyboardType = KeyboardType.Number
                            ))
                }
            }
            Row(
                Modifier
                    .padding(0.dp, 10.dp, 0.dp, 0.dp)
                    .align(CenterHorizontally)){
                Button(
                        onClick = {
                            if (name.isNotEmpty() && weight.toFloat() > 0 && hours.toFloat() >= 0) {
                                val schedule = createSchedule(weight.toFloat(), hours.toFloat());
                                lifecycleScope.launchWhenResumed {
                                    dbHelper.upsert(schedule)
                                    schedules.addAll(dbHelper.getAll())
                                }
                                navController.navigate("main")
                            }
                            else Toast.makeText(applicationContext, "You must have valid data", Toast.LENGTH_LONG).show()
                        }
                ) {
                    Text(text = "New Schedule")
                }
            }
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
    @ExperimentalFoundationApi
    @Composable
    fun history(){
        Surface(color = MaterialTheme.colors.background) {
            Column(){
                val yesterdaySchedules = schedules.filter { it.id == LocalDateTime.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE).toInt() }
                val weeksSchedule = schedules.filter {
                    it.id < LocalDateTime.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                            &&
                    it.id > LocalDateTime.now().minusDays(8).format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
                }
                Row() {
                    Text(text = "Yesterday's Schedules")
                }
                Row() {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        items(yesterdaySchedules.size,
                            itemContent = {
                                HistoryListItem(listItem = yesterdaySchedules[it])
                            })
                    }
                }
                Row() {
                    Text(text = "Last Week's Schedules")
                }
                Row(){
                    LazyColumn(
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        )
                    ) {
                        items(weeksSchedule.size,
                            itemContent = {
                                HistoryListItem(listItem = weeksSchedule[it])
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
                    val currentSchedules = schedules.filter { it.id == LocalDateTime.now().format(
                        DateTimeFormatter.BASIC_ISO_DATE).toInt() && it.active }
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
            modifier = Modifier
                .padding(horizontal = 0.dp, vertical = 8.dp)
                .fillMaxWidth(),
            elevation = 5.dp,
            shape = AbsoluteCutCornerShape(corner = CornerSize(5.dp))
        ){
            Column(
                modifier = Modifier
                    .padding(16.dp)
            ){
                Text(text = listItem.scheduleName, style = typography.h6)
                Text(text = "Drinks you took ${listItem.drinksTaken} drinks out of ${listItem.drinksNeeded}", style = typography.caption)
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
                    Text(text = "Drink every ${ 720 / listItem.drinksNeeded} minutes", style = typography.caption)
                }
                Column (
                    modifier = Modifier
                        .padding(16.dp)
                        .align(CenterVertically)
                        ){
                    if(listItem.drinksTaken <= listItem.drinksNeeded) {
                        Button(onClick = {
                            listItem.drinksTaken += 1
                            if (listItem.drinksTaken >= listItem.drinksNeeded) listItem.active =
                                false
                            lifecycleScope.launchWhenResumed { dbHelper.upsert(listItem) }
                        }) {
                            Text(text = "Drink!")
                        }
                    }
                    else{
                        Text(text = "Congrats on hydrating!!")
                    }
                }
                Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .align(CenterVertically)
                ){

                    Text(text = "Drinks Taken ${listItem.drinksTaken}", style = typography.caption)

                }
            }
        }
    }
}