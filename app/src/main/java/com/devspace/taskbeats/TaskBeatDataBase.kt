package com.devspace.taskbeats

import androidx.room.Database
import androidx.room.RoomDatabase

@Database([CategoryEntity::class, TaskEntity:: class], version = 3)
abstract class TaskBeatDataBase: RoomDatabase() {
    abstract fun getCategoryDao(): CategoryDAO

    abstract fun getTaskDao(): TaskDao
}