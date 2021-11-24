package com.longhurst.hydrateme.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class Schedule (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "scheduleName") val scheduleName : String,
    @ColumnInfo(name = "userWeight") val userWeight : Float,
    @ColumnInfo(name = "outdoorTime") val outdoorTime : Float,
    @ColumnInfo(name = "waterAmount") val waterAmount: Float,
    @ColumnInfo(name = "recurring") val recurring: Boolean,
    @ColumnInfo(name = "drinksNeeded") val drinksNeeded: Int,
    @ColumnInfo(name = "active") val active: Boolean,
)