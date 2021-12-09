package com.longhurst.hydrateme.data

import android.content.Context
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteOpenHelper


@Database(entities = [Schedule::class], version = 6)
abstract class AppDatabase : RoomDatabase() {
    abstract fun scheduleDAO(): ScheduleDAO
}

object DatabaseBuilder {
    private var instance: AppDatabase? = null
    fun getInstance(context: Context): AppDatabase {
        if(instance == null){
            synchronized(AppDatabase::class){
                instance = buildRoomDB(context)
            }
        }
        return instance!!
    }
    private fun buildRoomDB(context: Context) =
        Room.databaseBuilder(context.applicationContext,
        AppDatabase::class.java,
        "com.longhurst.hydrateme").fallbackToDestructiveMigration().build()
}

interface DatabaseHelper {
    suspend fun upsert(schedule: Schedule)
    suspend fun delete(schedule: Schedule)
    suspend fun getAll(): List<Schedule>
}

class DatabaseHelperImpl(private val appDatabase: AppDatabase) : DatabaseHelper {
    override suspend fun upsert(schedule: Schedule){
        val update = appDatabase.scheduleDAO().checkIfExists(schedule.id).isEmpty()
        if(update) appDatabase.scheduleDAO().insert(schedule)
        else appDatabase.scheduleDAO().updateSchedule(schedule)
    }

    override suspend fun getAll(): List<Schedule> = appDatabase.scheduleDAO().getAll()

    override suspend fun delete(schedule: Schedule) = appDatabase.scheduleDAO().delete(schedule)
}


