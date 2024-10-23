package com.dicoding.aplikasi_dicoding_eventsubmission1.data.entitiy

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "favoriteTable")
@Parcelize
data class FavoriteEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    var id: String,
) : Parcelable