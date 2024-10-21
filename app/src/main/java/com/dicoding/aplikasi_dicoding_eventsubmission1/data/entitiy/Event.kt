package com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val imageLogo: String?,
    val mediaCover: String?,
    val name: String?,
    val ownerName: String?,
    val beginTime: String?,
    val quota: Int?,
    val registrants: Int?,
    val description: String?,
    val link: String?,
    val isFavorite: Boolean = false // Tambahkan kolom untuk status favorit
)
