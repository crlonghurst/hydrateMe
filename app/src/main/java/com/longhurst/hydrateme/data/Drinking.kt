package com.longhurst.hydrateme.data

//This file should contain all sorts of different functions for handling how much someone is supposed to drink
fun amountByWeight(weight: Float): Int{
    val amount = Math.ceil(weight * 0.5)
    return amount.toInt()
}

fun amountofDrinks(waterAmount: Int):Int{
    val amount:Double = Math.ceil(waterAmount.toDouble() / 8)
    return amount.toInt()
}

fun drinksFromExercise(hours: Int): Int{
    return hours * 3
}
