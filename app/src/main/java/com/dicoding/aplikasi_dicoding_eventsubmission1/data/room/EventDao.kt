package com.dicoding.aplikasi_dicoding_eventsubmission1.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy


@Dao
interface EventDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertEvents(eventsTable: List<EventEntitiy>)


    @Query("SELECT * FROM EventTable WHERE isActive = 1 ORDER BY date(beginTime) DESC")
    suspend fun getUpcomingEvent(): List<EventEntitiy>

    @Query("DELETE FROM EventTable WHERE isActive = 1")
    suspend fun deleteUpcomingEvents(): Int


    @Query("SELECT * FROM EventTable WHERE isActive = 0 ORDER BY date(beginTime) DESC")
    suspend fun getFinishedEvent(): List<EventEntitiy>

    @Query("DELETE FROM eventTable WHERE isActive = 0")
    suspend fun deleteFinishedEvents(): Int


    @Query("SELECT * FROM eventTable WHERE isActive = :isActive AND name LIKE '%' || :query || '%' ORDER BY date(beginTime) ASC")
    suspend fun searchEvents(query: String, isActive: Int): List<EventEntitiy>
}
