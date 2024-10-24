package com.dicoding.aplikasi_dicoding_eventsubmission1.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.EventEntitiy
import com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy.FavoriteEntity


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


    // Favorite events
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteEvent(favorite: FavoriteEntity)

    @Query("DELETE FROM favoriteTable WHERE id = :eventId")
    suspend fun deleteFavoriteEvent(eventId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favoriteTable WHERE id = :eventId)")
    suspend fun isEventFavorite(eventId: String): Boolean


    // Get all favorite event IDs
    @Query("SELECT * FROM favoriteTable")
    suspend fun getFavoriteEventIds(): List<FavoriteEntity>

    // Get event details for favorite IDs
    @Query("SELECT * FROM EventTable WHERE id IN (:favoriteIds)")
    suspend fun getEventsByIds(favoriteIds: List<String>): List<EventEntitiy>

    // GET Closest Active Event
    @Query("SELECT * FROM eventTable WHERE isActive = 1 AND date(beginTime) >= :currentTime ORDER BY date(beginTime) ASC LIMIT 1")
    suspend fun getClosestActiveEvent(currentTime: Long): EventEntitiy?
}
