package com.longhurst.hydrateme.data

import androidx.room.*

@Dao
interface ScheduleDAO {
    @Insert
    suspend fun insert(schedule: Schedule)

    @Query("SELECT * FROM Schedule")
    suspend fun getAll(): List<Schedule>

    @Query("""
        SELECT id FROM Schedule
        WHERE id = :id""")
    suspend fun checkIfExists(id: Int) : List<Int>

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun delete(schedule: Schedule)
}