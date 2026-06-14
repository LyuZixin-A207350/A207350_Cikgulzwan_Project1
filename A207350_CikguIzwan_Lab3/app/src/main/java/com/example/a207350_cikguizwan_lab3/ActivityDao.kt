package com.example.a207350_cikguizwan_lab3

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ActivityDao {

    @Insert
    suspend fun insertActivity(activity: ActivityEntity)

    @Query("SELECT * FROM activities ORDER BY id DESC")
    suspend fun getAllActivities(): List<ActivityEntity>
}