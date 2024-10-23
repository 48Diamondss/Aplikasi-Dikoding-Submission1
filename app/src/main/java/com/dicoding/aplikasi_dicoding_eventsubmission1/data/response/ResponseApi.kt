package com.dicoding.aplikasi_dicoding_eventsubmission1.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

data class ResponseApi(
    @field:SerializedName("listEvents")
    val listEvents: List<ListEventsItem> = listOf(),  // List tidak bisa null dan defaultnya adalah list kosong

    @field:SerializedName("error")
    val error: Boolean = false,  // Mengubah menjadi non-null dengan default false

    @field:SerializedName("message")
    val message: String = ""  // Mengubah menjadi non-null dengan default string kosong
)

@Parcelize
data class ListEventsItem(
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String,

    @field:SerializedName("summary")
    val summary: String,

    @field:SerializedName("mediaCover")
    val mediaCover: String,

    @field:SerializedName("registrants")
    val registrants: Int,

    @field:SerializedName("imageLogo")
    val imageLogo: String,

    @field:SerializedName("link")
    val link: String,

    @field:SerializedName("description")
    val description: String,

    @field:SerializedName("ownerName")
    val ownerName: String,

    @field:SerializedName("cityName")
    val cityName: String,

    @field:SerializedName("quota")
    val quota: Int,

    @field:SerializedName("beginTime")
    val beginTime: String,

    @field:SerializedName("endTime")
    val endTime: String,

    @field:SerializedName("category")
    val category: String
) : Parcelable
