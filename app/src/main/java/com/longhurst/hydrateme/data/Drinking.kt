package com.longhurst.hydrateme.data

import android.util.Log
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.BASIC_ISO_DATE
import kotlin.math.ceil

//This file should contain all sorts of different functions for handling how much someone is supposed to drink
fun amountByWeight(weight: Float): Float{
    val amount = ceil(weight * 0.5)
    //Log.e("TESTTESTWEIGHT", "$weight $amount")
    return amount.toFloat()
}

fun amountofDrinks(waterAmount: Float):Float{
    val amount:Float = ceil(waterAmount / 8)
    //Log.e("TESTTESTDRINKS", "$waterAmount $amount")
    return amount
}

fun drinksFromExercise(hours: Float): Float{
    //val amount = hours * 4
    //Log.e("TESTTESTEXERCEISE", "$hours $amount")
    return hours * 3
}

fun createSchedule(weight: Float, hours: Float):Schedule{

    val ounces = amountByWeight(weight)
    val drinks = amountofDrinks(ounces) + drinksFromExercise(hours)
    val date = LocalDateTime.now().format(DateTimeFormatter.BASIC_ISO_DATE).toInt()
    Log.e("TESTTESTDRINKS", date.toString())

    return Schedule(date, "default", weight, hours, ounces, true, drinks.toInt(),  true, 0)




}